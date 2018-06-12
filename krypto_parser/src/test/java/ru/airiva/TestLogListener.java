package ru.airiva;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/**
 * Created by Ivan
 */
public class TestLogListener extends TestListenerAdapter {

    private static final String STRING = "+++++++++++";

    @Override
    public void onTestStart(ITestResult result) {
        log("");
        log(STRING);
        log(String.format("Test '%s.%s' started", result.getTestClass().getRealClass().getSimpleName(), result.getMethod().getMethodName()));
        log(STRING);
        log("");
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        log("");
        log(STRING);
        log(String.format("Test '%s.%s' result SUCCESS", tr.getTestClass().getRealClass().getSimpleName(), tr.getMethod().getMethodName()));
        log(STRING);
        log("");
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        log("");
        log(STRING);
        log(String.format("Test '%s.%s' result FAILURE", tr.getTestClass().getRealClass().getSimpleName(), tr.getMethod().getMethodName()));
        log(STRING);
        log("");
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        log("");
        log(STRING);
        log(String.format("Test '%s.%s' result SKIPPED", tr.getTestClass().getRealClass().getSimpleName(), tr.getMethod().getMethodName()));
        log(STRING);
        log("");
    }

    private void log(String s) {
        System.out.println(s);
    }

}
