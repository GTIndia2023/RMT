package com.qa.rmt.MyTest;

import RMT.Constants.AppConstants;
import RMT.Errors.AppError;
import com.qa.rmt.base.BaseTest;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import jdk.jfr.Description;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Covers the admin-side appended capacity configuration save flow for a selected competency.
 */
public class AppendedCapacityConfigTest extends BaseTest {

    @BeforeClass(alwaysRun = true)
    /**
     * Logs in and opens the appended capacity configuration page before the test runs.
     */
    public void accSetup() {
        boolean enabled = Boolean.parseBoolean(read("appended.config.run.enabled", "false"));
        if (!enabled) {
            throw new SkipException("Set appended.config.run.enabled=true to execute Appended Capacity configuration test.");
        }

        projectPage = loginPage.doLogin(prop.getProperty("username"), prop.getProperty("password"));
        appendedCapacityConfigPage = projectPage.navigateToAppendedCapacityConfiguration();
    }

    @Test(priority = 1)
    @Description("Validates Appended Capacity configuration save flow for selected competency.")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.BLOCKER)
    /**
     * Verifies that an admin can update appended capacity settings and receive a successful save message.
     */
    public void verifyAdminCanConfigureAppendedCapacityTest() {
        Assert.assertTrue(appendedCapacityConfigPage.isConfigurationScreenDisplayed(), AppError.APPENDED_CAPACITY_CONFIGURATION_FAILED);

        String competency = read("appended.config.competency", "");
        String appendedHours = read("appended.config.hours", "2");
        boolean bypass = Boolean.parseBoolean(read("appended.config.supercoach.bypass", "false"));

        if (competency.isBlank()) {
            throw new SkipException("Set appended.config.competency in config before running this test.");
        }

        appendedCapacityConfigPage.searchCompetency(competency);
        appendedCapacityConfigPage.setAppendedCapacityHours(appendedHours);
        appendedCapacityConfigPage.setSupercoachBypass(bypass);

        String actualMessage = appendedCapacityConfigPage.saveAndCaptureMessage();
        String expectedMessage = read("appended.config.expected.message", AppConstants.APPENDED_CAPACITY_CONFIG_SUCCESS_MESSAGE);
        Assert.assertTrue(
                actualMessage.equalsIgnoreCase(expectedMessage) || actualMessage.toLowerCase().contains("success"),
                "Configuration save message mismatch. Actual: " + actualMessage
        );
    }

    private String read(String key, String defaultValue) {
        String value = prop.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }
}
