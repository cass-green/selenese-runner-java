/*
@formatter:off

Patched for Selenese Runner by Motonori IWAMURO.

Original:
---
Copyright 2007-2009 Selenium committers

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package jp.vmi.selenium.selenese.subcommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;

import com.google.common.collect.Maps;
import com.thoughtworks.selenium.SeleniumException;
import com.thoughtworks.selenium.webdriven.commands.Windows;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.webdriver.NullDriver;

@SuppressWarnings("javadoc")
public class SeleneseRunnerWindows extends Windows {
  private final Map<String, String> lastFrame = Maps.newHashMap();
  private final Context context;

  public SeleneseRunnerWindows(Context context) {
    super(new NullDriver());
    this.context = context;
  }

  @Override
  public void selectWindow(WebDriver driver, String windowID) {
    if (null == windowID || "null".equals(windowID) || "".equals(windowID)) {
      driver.switchTo().window(context.getInitialWindowHandle());
    } else if ("_blank".equals(windowID)) {
      selectBlankWindow(driver);
    } else {
      if (windowID.startsWith("title=")) {
        selectWindowWithTitle(driver, windowID.substring("title=".length()));
        return;
      }

      if (windowID.startsWith("name=")) {
        windowID = windowID.substring("name=".length());
      }

      try {
        driver.switchTo().window(windowID);
      } catch (NoSuchWindowException e) {
        selectWindowWithTitle(driver, windowID);
      }
    }

    if (lastFrame.containsKey(driver.getWindowHandle())) {
      // If the frame has gone, fall back
      try {
        selectFrame(driver, lastFrame.get(driver.getWindowHandle()));
      } catch (SeleniumException e) {
        lastFrame.remove(driver.getWindowHandle());
      }
    }
  }

  @Override
  public void selectPopUp(WebDriver driver, String windowID) {
    if ("null".equals(windowID) || "".equals(windowID)) {
      Set<String> windowHandles = driver.getWindowHandles();
      windowHandles.remove(context.getInitialWindowHandle());
      if (windowHandles.size() > 0) {
        driver.switchTo().window(windowHandles.iterator().next());
      } else {
        throw new SeleniumException("Unable to find a popup window");
      }
    } else {
      selectWindow(driver, windowID);
    }
  }

  @Override
  public void selectFrame(WebDriver driver, String locator) {
    if ("relative=top".equals(locator)) {
      driver.switchTo().defaultContent();
      lastFrame.remove(driver.getWindowHandle());
      return;
    }

// TODO uncomment when support parentFrame() on next Selenium.
//    if ("relative=up".equals(locator)) {
//      driver.switchTo().parentFrame();
//      lastFrame.put(driver.getWindowHandle(), locator);
//      return;
//    }

    if (locator.startsWith("index=")) {
      try {
        int index = Integer.parseInt(locator.substring("index=".length()));
        lastFrame.put(driver.getWindowHandle(), locator);
        driver.switchTo().frame(index);
        return;
      } catch (NumberFormatException e) {
        throw new SeleniumException(e.getMessage(), e);
      } catch (NoSuchFrameException e) {
        throw new SeleniumException(e.getMessage(), e);
      }
    }

    if (locator.startsWith("id=")) {
      locator = locator.substring("id=".length());
    } else if (locator.startsWith("name=")) {
      locator = locator.substring("name=".length());
    }

    try {
      lastFrame.put(driver.getWindowHandle(), locator);
      driver.switchTo().frame(locator);
    } catch (NoSuchFrameException e) {
      throw new SeleniumException(e.getMessage(), e);
    }
  }

  private void selectWindowWithTitle(WebDriver driver, String title) {
    String current = driver.getWindowHandle();
    for (String handle : driver.getWindowHandles()) {
      driver.switchTo().window(handle);
      if (title.equals(driver.getTitle())) {
        return;
      }
    }

    driver.switchTo()
        .window(current);
    throw new SeleniumException("Unable to select window with title: " + title);
  }

  /**
   * Selects the only <code>_blank</code> window. A window open with <code>target='_blank'</code>
   * will have a <code>window.name = null</code>.
   * <p/>
   * <p>
   * This method assumes that there will only be one single <code>_blank</code> window and selects
   * the first one with no name. Therefore if for any reasons there are multiple windows with
   * <code>window.name = null</code> the first found one will be selected.
   * <p/>
   * <p>
   * If none of the windows have <code>window.name = null</code> the last selected one will be
   * re-selected and a {@link SeleniumException} will be thrown.
   * 
   * @throws NoSuchWindowException if no window with <code>window.name = null</code> is found.
   */
  @Override
  public void selectBlankWindow(WebDriver driver) {
    String current = driver.getWindowHandle();
    // Find the first window without a "name" attribute
    List<String> handles = new ArrayList<String>(driver.getWindowHandles());
    for (String handle : handles) {
      // the original window will never be a _blank window, so don't even look at it
      // this is also important to skip, because the original/root window won't have
      // a name either, so if we didn't know better we might think it's a _blank popup!
      if (handle.equals(context.getInitialWindowHandle())) {
        continue;
      }
      driver.switchTo().window(handle);
      String value = (String)
          ((JavascriptExecutor) driver).executeScript("return window.name;");
      if (value == null || "".equals(value)) {
        // We found it!
        return;
      }
    }
    // We couldn't find it
    driver.switchTo().window(current);
    throw new SeleniumException("Unable to select window _blank");
  }
}
