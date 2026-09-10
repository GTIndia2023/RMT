package RMT.Listeners;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import RMT.Factory.DriverManager;
import java.nio.charset.StandardCharsets;
public class TestAllureListener implements ITestListener {

    private static final String ALLURE_TEST_UUID_ATTRIBUTE = "ALLURE_TEST_UUID";

    private static String getTestMethodName(ITestResult iTestResult) {
        return iTestResult.getMethod().getConstructorOrMethod().getName();
    }


    private void attachScreenshot(WebDriver driver, String attachmentName) {
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.getLifecycle().addAttachment(attachmentName, "image/png", ".png", screenshot);
    }

    private void attachTextLog(String attachmentName, String message) {
        String safeMessage = message == null ? "" : message;
        Allure.getLifecycle().addAttachment(attachmentName, "text/plain", ".txt",
                safeMessage.getBytes(StandardCharsets.UTF_8));
    }

    private void attachPageSource(String attachmentName, String html) {
        String safeHtml = html == null ? "" : html;
        Allure.getLifecycle().addAttachment(attachmentName, "text/html", ".html",
                safeHtml.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void onStart(ITestContext iTestContext) {
        System.out.println("I am in onStart method " + iTestContext.getName());
//        iTestContext.setAttribute("WebDriver", BasePage.getDriver());
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        System.out.println("I am in onFinish method " + iTestContext.getName());
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {
        System.out.println("I am in onTestStart method " + getTestMethodName(iTestResult) + " start");
        System.out.println("I am in onTestStart method " + getTestMethodName(iTestResult) + " start");
        String className = iTestResult.getTestClass().getName();
        String methodName = iTestResult.getMethod().getMethodName();

        String uniqueId = className + "." + methodName;

        Allure.getLifecycle().updateTestCase(tc -> {
            tc.setHistoryId(uniqueId);     // for trend
            tc.setTestCaseId(uniqueId);   // REQUIRED for Allure v3 history
        });

        Allure.getLifecycle().getCurrentTestCase()
                .ifPresent(uuid -> iTestResult.setAttribute(ALLURE_TEST_UUID_ATTRIBUTE, uuid));

        System.out.println("History + TestCaseId set for: " + uniqueId);
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        System.out.println("I am in onTestSuccess method " + getTestMethodName(iTestResult) + " succeed");
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        System.out.println("I am in onTestFailure method " + getTestMethodName(iTestResult) + " failed");
        Allure.getLifecycle().getCurrentTestCase()
                .ifPresent(uuid -> iTestResult.setAttribute(ALLURE_TEST_UUID_ATTRIBUTE, uuid));
        WebDriver driver = DriverManager.getDriver();
        if (driver != null) {
            try {
                attachScreenshot(driver, "Failure screenshot - " + getTestMethodName(iTestResult));
            } catch (Exception e) {
                attachTextLog("Screenshot capture issue - " + getTestMethodName(iTestResult), e.getMessage());
            }
            try {
                attachPageSource("Failure page source - " + getTestMethodName(iTestResult), driver.getPageSource());
            } catch (Exception e) {
                attachTextLog("Page source capture issue - " + getTestMethodName(iTestResult), e.getMessage());
            }
        } else {
            attachTextLog("Failure artifact issue - " + getTestMethodName(iTestResult), "No active driver available.");
        }
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        System.out.println("I am in onTestSkipped method " + getTestMethodName(iTestResult) + " skipped");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        System.out.println("Test failed but it is in defined success ratio " + getTestMethodName(iTestResult));
    }

}
