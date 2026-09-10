package RMT.Pages;

import RMT.Utils.ElementUtil;
import RMT.Utils.TimeUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Encapsulates the appended capacity configuration screen interactions used by admin-side tests.
 */
public class AppendedCapacityConfigPage {
    private final WebDriver driver;
    private final ElementUtil eleutil;

    /**
     * Creates the appended capacity configuration helper for the active browser session.
     */
    public AppendedCapacityConfigPage(WebDriver driver) {
        this.driver = driver;
        this.eleutil = new ElementUtil(driver);
    }

    private final By configHeader = By.xpath("//*[contains(normalize-space(),'Configuration') or contains(normalize-space(),'Appended Capacity')]");
    private final By saveButton = By.xpath("//button[normalize-space()='Save' or normalize-space()='Submit']");
    private final By successToast = By.xpath("//div[contains(@class,'MuiAlert-message')]");

    private final By[] competencySearchInputs = {
            By.xpath("//input[contains(@placeholder,'Competency')]"),
            By.xpath("//input[contains(@placeholder,'Search')]"),
            By.xpath("//input[@type='text' and contains(@aria-label,'Competency')]")
    };

    private final By[] appendedHoursInputs = {
            By.xpath("//input[contains(@name,'appended') and @type='number']"),
            By.xpath("//input[contains(@id,'appended') and @type='number']"),
            By.xpath("(//input[@type='number' and not(@disabled)])[1]")
    };

    private final By[] supercoachBypassToggles = {
            By.xpath("//*[contains(normalize-space(),'Supercoach approval enabled')]/ancestor::*[self::label or self::div]//*[contains(@class,'switch') or @role='checkbox']"),
            By.xpath("//input[@type='checkbox' and contains(@name,'supercoach')]")
    };

    /**
     * Checks whether the appended capacity configuration screen is currently visible.
     */
    public boolean isConfigurationScreenDisplayed() {
        return driver.findElements(configHeader).stream().anyMatch(WebElement::isDisplayed);
    }

    /**
     * Searches for the requested competency on the appended capacity configuration screen.
     */
    public void searchCompetency(String competencyName) {
        if (competencyName == null || competencyName.trim().isEmpty()) {
            return;
        }
        By input = firstVisible(competencySearchInputs);
        if (input == null) {
            throw new NoSuchElementException("Competency search input is not visible.");
        }

        eleutil.enterTextReliable(input, competencyName.trim(), TimeUtil.DEFAULT_TIME_OUT);
    }

    /**
     * Updates the appended capacity hours field with the supplied numeric value.
     */
    public void setAppendedCapacityHours(String hours) {
        By input = firstVisible(appendedHoursInputs);
        if (input == null) {
            throw new NoSuchElementException("Appended Capacity hours field is not visible.");
        }
        eleutil.enterTextReliable(input, hours, TimeUtil.DEFAULT_TIME_OUT);
    }

    /**
     * Aligns the Supercoach bypass toggle with the requested state.
     */
    public void setSupercoachBypass(boolean enableBypass) {
        By toggle = firstVisible(supercoachBypassToggles);
        if (toggle == null) {
            return;
        }

        WebElement element = eleutil.waitForElementVisible(toggle, TimeUtil.DEFAULT_TIME_OUT);
        boolean current = readToggleState(element);
        if (current != enableBypass) {
            element.click();
        }
    }

    /**
     * Saves the appended capacity changes and returns the visible success or validation message.
     */
    public String saveAndCaptureMessage() {
        eleutil.clickStable(saveButton, TimeUtil.DEFAULT_TIME_OUT);
        if (driver.findElements(successToast).stream().anyMatch(WebElement::isDisplayed)) {
            return eleutil.doGetText(successToast).trim();
        }
        return "";
    }

    private By firstVisible(By[] locators) {
        for (By locator : locators) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                if (element.isDisplayed()) {
                    return locator;
                }
            }
        }
        return null;
    }

    private boolean readToggleState(WebElement element) {
        String ariaChecked = element.getAttribute("aria-checked");
        if (ariaChecked != null) {
            return Boolean.parseBoolean(ariaChecked);
        }
        String checked = element.getAttribute("checked");
        return checked != null && (checked.equalsIgnoreCase("true") || checked.equalsIgnoreCase("checked"));
    }
}
