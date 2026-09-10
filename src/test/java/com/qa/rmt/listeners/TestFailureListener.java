package com.qa.rmt.listeners;

import RMT.Factory.DriverManager;
import io.qameta.allure.Allure;
import com.qa.rmt.utils.AllureAttachmentHelper;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestFailureListener implements ITestListener, IInvokedMethodListener {
    private static final String ALLURE_TEST_CASE_UUID = "ALLURE_TEST_CASE_UUID";

    @Override
    public void onTestStart(ITestResult result) {
        if (result == null || result.getTestClass() == null || result.getMethod() == null) {
            return;
        }

        String uniqueId = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
        captureCurrentAllureTestCase(result, uniqueId);

        System.out.println("[TestFailureListener] Allure history + testcase id set for: " + uniqueId);
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
        if (method == null || testResult == null || !method.isTestMethod()) {
            return;
        }

        String uniqueId = testResult.getTestClass().getName() + "." + testResult.getMethod().getMethodName();
        captureCurrentAllureTestCase(testResult, uniqueId);
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
        if (method == null || testResult == null || !method.isTestMethod()) {
            return;
        }

        if (testResult.getStatus() == ITestResult.FAILURE) {
            System.out.println("[TestFailureListener] Capturing failure artifacts from afterInvocation while test context is still active.");
            AllureAttachmentHelper.attachFailureArtifacts(testResult, DriverManager.getDriver());
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("[TestFailureListener] Test failed: "
                + result.getTestClass().getName() + "." + result.getMethod().getMethodName());
        AllureAttachmentHelper.attachFailureArtifacts(result, DriverManager.getDriver());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println("[TestFailureListener] Test failed within success percentage: "
                + result.getTestClass().getName() + "." + result.getMethod().getMethodName());
    }

    private void captureCurrentAllureTestCase(ITestResult result, String uniqueId) {
        Allure.getLifecycle().getCurrentTestCase().ifPresent(uuid -> {
            result.setAttribute(ALLURE_TEST_CASE_UUID, uuid);
            Allure.getLifecycle().updateTestCase(uuid, testResult -> {
                testResult.setHistoryId(uniqueId);
                testResult.setTestCaseId(uniqueId);
            });
            System.out.println("[TestFailureListener] Captured Allure test uuid for attachments: " + uuid);
        });
    }
}
