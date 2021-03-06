// This code is based on:
//
//   com.thoughtworks.selenium.webdriven.commands.IsOrdered
//
// in Selenium WebDriver.
//
// The following copyright is copied from original.
// ---
// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package jp.vmi.selenium.selenese.subcommand;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;

/**
 * Re-implementation of IsOrdered.
 */
public class IsOrdered extends AbstractSubCommand<Boolean> {

    private static final int ARG_LOCATOR1 = 0;
    private static final int ARG_LOCATOR2 = 1;

    /**
     * Constructor.
     */
    public IsOrdered() {
        super(ArgumentType.LOCATOR, ArgumentType.LOCATOR);
    }

    @Override
    public Boolean execute(Context context, String... args) {
        WebDriver driver = context.getWrappedDriver();
        WebDriverElementFinder finder = context.getElementFinder();
        WebElement element1 = finder.findElement(driver, args[ARG_LOCATOR1]);
        WebElement element2 = finder.findElement(driver, args[ARG_LOCATOR2]);
        return context.getJSLibrary().isOrdered(driver, element1, element2);
    }
}
