package jp.vmi.selenium.selenese.inject;

import java.io.File;

import org.openqa.selenium.WebDriver;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;

import jp.vmi.selenium.selenese.ErrorTestCase;
import jp.vmi.selenium.selenese.ErrorTestSuite;
import jp.vmi.selenium.selenese.InvalidSeleneseException;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.TestSuite;

/**
 * Apply aspect.
 */
public class Binder {
    private static Injector injector;

    static {
        injector = Guice.createInjector(
            new AbstractModule() {
                @Override
                protected void configure() {
                    bindInterceptor(
                        Matchers.any(),
                        Matchers.annotatedWith(DoCommand.class),
                        new CommandLogInterceptor()
                    );
                    bindInterceptor(
                        Matchers.any(),
                        Matchers.annotatedWith(ExecuteTestCase.class),
                        new ExecuteTestCaseInterceptor()
                    );
                    bindInterceptor(
                        Matchers.any(),
                        Matchers.annotatedWith(ExecuteTestSuite.class),
                        new ExecuteTestSuiteInterceptor()
                    );
                }
            }
            );
    }

    /**
     * Constructs TestCase applied aspect.
     *
     * @param file selenese script file.
     * @param name test-case name.
     * @param driver target WebDriver instance.
     * @param baseURL effective base URL.
     * @return TestCase instance.
     */
    public static TestCase newTestCase(File file, String name, WebDriver driver, String baseURL) {
        TestCase testCase = injector.getInstance(TestCase.class);
        return testCase.initialize(file, name, driver, baseURL);
    }

    /**
     * Constructs TestSuite applied aspect.
     *
     * @param file Selenese script file.
     * @param name test-case name.
     * @return TestSuite instance.
     */
    public static TestSuite newTestSuite(File file, String name) {
        TestSuite testSuite = injector.getInstance(TestSuite.class);
        return testSuite.initialize(file, name);
    }

    /**
     * Constructs ErrorTestCase applied aspect.
     *
     * @param name test-case name.
     * @param e InvalidSeleneseException instance.
     * @return ErrorTestCase instance.
     */
    public static ErrorTestCase newErrorTestCase(String name, InvalidSeleneseException e) {
        ErrorTestCase errorTestCase = injector.getInstance(ErrorTestCase.class);
        return errorTestCase.initialize(name, e);
    }

    /**
     * Constructs ErrorTestSuite applied aspect.
     *
     * @param name test-suite name.
     * @param e InvalidSeleneseException instance.
     * @return ErrorSuiteCase instance.
     */
    public static ErrorTestSuite newErrorTestSuite(String name, InvalidSeleneseException e) {
        ErrorTestSuite errorTestSuite = injector.getInstance(ErrorTestSuite.class);
        return errorTestSuite.initialize(name, e);
    }
}
