package com.qa.rmt.base;

import RMT.Factory.DriverManager;
import RMT.Pages.*;
import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;
import com.qa.rmt.utils.AllureAttachmentHelper;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class BaseTest {

    protected DriverManager df;
    protected WebDriver driver;
    protected Properties prop;

    protected LoginPage loginPage;
    protected ProjectListingsPage projectPage;
    protected SkillMasterPage skillmasterPage;
    protected RequisitionPage requisitionPage;
    protected ReportsPage reportsPage;
    protected BudgetPage budgetPage;
    protected CommonAllocationPage commonAllocationPage;
    protected AppendedCapacityConfigPage appendedCapacityConfigPage;
    protected AllocationWorkflowPage allocationWorkflowPage;
    protected SoftAssert softAssert;

    private static final Object CONSOLE_LOCK = new Object();
    private static final DateTimeFormatter LOG_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yy");
    private static final DateTimeFormatter LOG_TIME_FORMAT = DateTimeFormatter.ofPattern("HH-mm-ss");
    private static boolean consoleMirroringInitialized = false;
    private static String activeLogFileKey = "";
    private static PrintStream originalOut;
    private static PrintStream originalErr;
    private static PrintStream fileOut;
    private static PrintStream fileErr;

    @Parameters({"browser"})
    @BeforeClass(alwaysRun = true)
    public void setup(ITestContext testContext, @Optional("") String browserName) {
        initializeConsoleMirroring(testContext);

        df = new DriverManager();

        // Initialize properties per class
        prop = df.initProp();

        if (browserName != null && !browserName.trim().isEmpty()) {
            prop.setProperty("browser", browserName.trim());
        }

        if (!shouldBootstrapDriverForCurrentClass()) {
            softAssert = new SoftAssert();
            addAllureEnvironmentLabels();
            String skipMessage = "Driver bootstrap skipped for class: " + getClass().getSimpleName();
            System.out.println(skipMessage);
            throw new SkipException(skipMessage);
        }

        driver = df.initDriver(prop);

        loginPage = new LoginPage(driver);
        softAssert = new SoftAssert();

        addAllureEnvironmentLabels();
    }

    private void addAllureEnvironmentLabels() {
        Allure.label("browser", prop.getProperty("browser"));
        Allure.label("env", System.getProperty("env", "uat"));
        Allure.label("os", System.getProperty("os.name"));
    }

    @AfterMethod(alwaysRun = true)
    public void attachFailureArtifacts(ITestResult result) {
        if (result == null || result.getStatus() != ITestResult.FAILURE) {
            return;
        }
        AllureAttachmentHelper.attachFailureArtifacts(result, driver);
    }

    private void initializeConsoleMirroring(ITestContext testContext) {
        synchronized (CONSOLE_LOCK) {
            String logFileKey = buildLogFileKey(testContext);
            if (consoleMirroringInitialized && logFileKey.equals(activeLogFileKey)) {
                return;
            }

            try {
                Path logDir = Paths.get("test_Logs");
                Files.createDirectories(logDir);
                Path logFile = logDir.resolve(logFileKey);

                if (consoleMirroringInitialized) {
                    try {
                        if (fileOut != null) {
                            fileOut.flush();
                            fileOut.close();
                        }
                    } catch (Exception ignored) {
                    }
                    try {
                        if (fileErr != null && fileErr != fileOut) {
                            fileErr.flush();
                            fileErr.close();
                        }
                    } catch (Exception ignored) {
                    }
                }

                originalOut = System.out;
                originalErr = System.err;

                fileOut = new PrintStream(new FileOutputStream(logFile.toFile(), true), true, StandardCharsets.UTF_8);
                fileErr = new PrintStream(new FileOutputStream(logFile.toFile(), true), true, StandardCharsets.UTF_8);

                System.setOut(new PrintStream(new TeeOutputStream(originalOut, fileOut), true, StandardCharsets.UTF_8));
                System.setErr(new PrintStream(new TeeOutputStream(originalErr, fileErr), true, StandardCharsets.UTF_8));

                System.out.println("[BaseTest] Console mirroring enabled. Log file: " + logFile.toAbsolutePath());
                activeLogFileKey = logFileKey;

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    synchronized (CONSOLE_LOCK) {
                        try {
                            if (fileOut != null) {
                                fileOut.flush();
                                fileOut.close();
                            }
                        } catch (Exception ignored) {
                        }
                        try {
                            if (fileErr != null && fileErr != fileOut) {
                                fileErr.flush();
                                fileErr.close();
                            }
                        } catch (Exception ignored) {
                        }
                        try {
                            if (originalOut != null) {
                                System.setOut(originalOut);
                            }
                            if (originalErr != null) {
                                System.setErr(originalErr);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }, "console-mirroring-shutdown"));

                consoleMirroringInitialized = true;
            } catch (IOException e) {
                throw new IllegalStateException("Failed to initialize console log mirroring.", e);
            }
        }
    }

    private String buildLogFileKey(ITestContext testContext) {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(LOG_DATE_FORMAT);
        String timePart = now.format(LOG_TIME_FORMAT);
        String xmlTestName = resolveXmlTestName(testContext);
        String safeTestName = sanitizeForFileName(xmlTestName);
        return "Test_Run_" + datePart + "_" + safeTestName + "_" + timePart + ".log";
    }

    private String resolveXmlTestName(ITestContext testContext) {
        if (testContext != null && testContext.getCurrentXmlTest() != null) {
            String xmlTestName = testContext.getCurrentXmlTest().getName();
            if (xmlTestName != null && !xmlTestName.trim().isEmpty()) {
                return xmlTestName.trim();
            }
        }
        return getClass().getSimpleName();
    }

    private String sanitizeForFileName(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "UnnamedTest";
        }
        String sanitized = value.trim().replaceAll("[\\\\/:*?\"<>|]+", "_");
        sanitized = sanitized.replaceAll("\\s+", " ").trim();
        return sanitized.isEmpty() ? "UnnamedTest" : sanitized;
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private boolean shouldBootstrapDriverForCurrentClass() {
        String className = getClass().getSimpleName();
        if ("AllocationModulePOMTest".equals(className)) {
            return Boolean.parseBoolean(prop.getProperty("allocation.ui.run.enabled", "true"));
        }
        if ("AppendedCapacityConfigTest".equals(className)) {
            return Boolean.parseBoolean(prop.getProperty("appended.config.run.enabled", "false"));
        }
        if ("AllocationWorkflowPOMTest".equals(className)) {
            return Boolean.parseBoolean(prop.getProperty("allocation.workflow.run.enabled", "false"));
        }
        return true;
    }

    private static final class TeeOutputStream extends OutputStream {
        private final OutputStream primary;
        private final OutputStream secondary;

        private TeeOutputStream(OutputStream primary, OutputStream secondary) {
            this.primary = primary;
            this.secondary = secondary;
        }

        @Override
        public void write(int b) throws IOException {
            primary.write(b);
            if (secondary != null) {
                secondary.write(b);
            }
        }

        @Override
        public void write(byte[] b) throws IOException {
            primary.write(b);
            if (secondary != null) {
                secondary.write(b);
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            primary.write(b, off, len);
            if (secondary != null) {
                secondary.write(b, off, len);
            }
        }

        @Override
        public void flush() throws IOException {
            primary.flush();
            if (secondary != null) {
                secondary.flush();
            }
        }

        @Override
        public void close() throws IOException {
            flush();
        }
    }
}
