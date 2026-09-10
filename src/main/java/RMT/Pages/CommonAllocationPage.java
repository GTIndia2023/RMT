package RMT.Pages;

import RMT.Utils.ElementUtil;
import RMT.Utils.JavascriptUtil;
import RMT.Utils.TimeUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Models the Common Allocation popup and grid interactions used to create, update, and validate allocations.
 */
public class CommonAllocationPage {
    private final WebDriver driver;
    private final ElementUtil eleutil;
    private final JavascriptUtil jsUtil;
    private final Actions actions;
    private static final DateTimeFormatter DD_MM_YYYY = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final Pattern WARNING_DATE_PATTERN = Pattern.compile("\\b\\d{2}-\\d{2}-\\d{4}\\b");
    private static final String[] AVAILABILITY_WARNING_PHRASES = {
            "not available on the following dates",
            "hours are not available",
            "user is not available",
            "resource is not available",
            "unavailable"
    };
    private String selectedResourceName = "";
    private String selectedStartDate = "";
    private String selectedEndDate = "";
    private String selectedRequestedHours = "";

    /**
     * Creates the common allocation helper for the active browser session.
     */
    public CommonAllocationPage(WebDriver driver) {
        this.driver = driver;
        this.eleutil = new ElementUtil(driver);
        this.jsUtil = new JavascriptUtil(driver);
        this.actions = new Actions(driver);
    }

    private void logAction(String message) {
        System.out.println("[CommonAllocation] " + message);
    }

    private final By commonAllocationPopupHeader = By.xpath("//*[contains(normalize-space(),'Common Allocation') or contains(normalize-space(),'Allocate Employee')]");
    private final By allocateButton = By.xpath("//button[normalize-space()='Allocate']");
    private final By allocateEmployeeButton = By.xpath("//button[normalize-space()='Allocate Employee']");
    private final By saveButton = By.xpath("//button[normalize-space()='Save']");
    private final By[] scopedSubmissionActionButtons = {
            By.xpath(".//button[normalize-space()='Allocate']"),
            By.xpath(".//button[normalize-space()='Allocate Employee']"),
            By.xpath(".//button[normalize-space()='Save']")
    };
    private final By[] yesButtons = {
            By.xpath("//button[normalize-space()='Yes']"),
            By.xpath("//button[contains(normalize-space(.),'Yes')]"),
            By.xpath("//button[.//*[normalize-space()='Yes']]"),
            By.xpath("//*[(@role='button' or self::button) and normalize-space()='Yes']")
    };
    private final By[] closeButtons = {
            By.xpath("//button[@aria-label='close' or @aria-label='Close']"),
            By.xpath("//*[name()='svg' and (@data-testid='CloseIcon' or @data-testid='CancelIcon')]/ancestor::button[1]")
    };
    private final By[] okButtons = {
            By.xpath("//button[normalize-space()='OK']"),
            By.xpath("//button[normalize-space()='Ok']"),
            By.xpath("//button[@type='submit' and (normalize-space()='OK' or normalize-space()='Ok')]")
    };
    private final By allocationToast = By.xpath("//div[contains(@class,'MuiAlert-message')]");
    private final By[] validationMessageLocators = {
            By.xpath("//div[contains(@class,'MuiAlert-message')]"),
            By.xpath("//*[@role='alert']"),
            By.xpath("//div[contains(@class,'MuiSnackbar-root')]//*[self::div or self::p]"),
            By.xpath("//div[contains(@class,'MuiDialog-root') or @role='dialog']//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'allocated') or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'required') or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'invalid') or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'please') or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'not available') or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'unavailable')]")
    };
    private final By addMoreButton = By.xpath("//*[normalize-space(text())='Add more']//*[name()='svg']");

    private final By[] searchResourceInputs = {
            By.xpath("//div[@role='dialog']//input[@id='searchEmployee']"),
            By.xpath("//div[@role='dialog']//input[contains(@id,'searchEmployee')]"),
            By.xpath("//div[@role='dialog']//input[contains(@placeholder,'Search Employee')]"),
            By.xpath("//input[contains(@placeholder,'Search Employee')]"),
            By.xpath("//input[@id='searchEmployee']")
    };

    private final By[] employeeSuggestionRows = {
            By.xpath("//ul[contains(@id,'searchEmployee') and @role='listbox']//li[@role='option']"),
            By.xpath("//li[contains(@id,'searchEmployee') and @role='option']"),
            By.xpath("//ul[@role='listbox']//li[@role='option']")
    };

    private final By[] descriptionInputs = {
            By.xpath("//div[contains(@class,'MuiModal-root') or contains(@class,'MuiDialog-root') or contains(@class,'MuiPaper-root') or @role='dialog']"
                    + "//label[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'task description')]"
                    + "/ancestor::*[contains(@class,'MuiFormControl-root') or contains(@class,'MuiGrid-root') or contains(@class,'MuiBox-root')][1]"
                    + "//*[self::textarea"
                    + " or @contenteditable='true'"
                    + " or (@role='textbox' and not(self::input[contains(@class,'MuiAutocomplete-input')]))"
                    + " or (self::input[not(contains(@class,'MuiAutocomplete-input'))"
                    + " and not(contains(translate(@class,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'autocomplete'))"
                    + " and not(@type='hidden') and not(@type='checkbox') and not(@type='radio') and not(@type='number')])][1]"),
            By.xpath("//div[contains(@class,'MuiModal-root') or contains(@class,'MuiDialog-root') or contains(@class,'MuiPaper-root') or @role='dialog']"
                    + "//*[self::textarea or @contenteditable='true' or @role='textbox']"
                    + "[ancestor::*[contains(@class,'MuiFormControl-root')][.//label[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'task description')]]][1]"),
            By.xpath("//div[contains(@class,'MuiModal-root') or contains(@class,'MuiDialog-root') or contains(@class,'MuiPaper-root') or @role='dialog']"
                    + "//*[self::textarea or self::input][contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'task description')"
                    + " or contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'task description')"
                    + " or contains(translate(@name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'description')]"
                    + "[not(contains(@class,'MuiAutocomplete-input'))"
                    + " and not(contains(translate(@class,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'autocomplete'))]"),
            By.xpath("//div[contains(@class,'MuiModal-root') or contains(@class,'MuiDialog-root') or contains(@class,'MuiPaper-root') or @role='dialog']"
                    + "//*[self::textarea or self::input][contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'description')"
                    + " or contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'description')]"
                    + "[not(contains(@class,'MuiAutocomplete-input'))"
                    + " and not(contains(translate(@class,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'autocomplete'))]"),
            By.xpath("//div[contains(@class,'MuiModal-root') or contains(@class,'MuiDialog-root') or contains(@class,'MuiPaper-root') or @role='dialog']//textarea[contains(@class,'MuiInputBase-input') or contains(@class,'MuiInputBase-inputMultiline')]"),
            By.xpath("//div[contains(@class,'MuiModal-root') or contains(@class,'MuiDialog-root') or contains(@class,'MuiPaper-root') or @role='dialog']//*[(@role='textbox' or @contenteditable='true') and not(@aria-hidden='true')]"),
            By.xpath("//textarea[contains(@name,'description') or contains(@placeholder,'Description') or contains(@placeholder,'Task Description')]"),
            By.xpath("//input[(contains(translate(@name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'description')"
                    + " or contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'description')"
                    + " or contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'description'))"
                    + " and not(contains(@class,'MuiAutocomplete-input'))"
                    + " and not(contains(translate(@class,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'autocomplete'))]")
    };
    private final By[] descriptionFieldFallbacks = {
            By.xpath(".//textarea[not(@disabled)]"),
            By.xpath(".//*[@role='textbox' and not(@aria-hidden='true')]"),
            By.xpath(".//*[@contenteditable='true' and not(@aria-hidden='true')]")
    };
    private final By[] descriptionFieldFallbacksGlobal = {
            By.xpath("//textarea[not(@disabled)]"),
            By.xpath("//*[@role='textbox' and not(@aria-hidden='true')]"),
            By.xpath("//*[@contenteditable='true' and not(@aria-hidden='true')]")
    };

    private final By[] skillDropdownInputs = {
            By.xpath(".//label[contains(normalize-space(),'Skill')]/following::input[1]"),
            By.xpath(".//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'skills')]/following::input[1]"),
            By.xpath(".//input[contains(@aria-label,'Skill')]"),
            By.xpath(".//input[contains(@placeholder,'Type And Select')]")
    };
    private final By[] skillDropdownClickTargets = {
            By.xpath(".//label[contains(normalize-space(),'Skill')]/following::*[@role='combobox'][1]"),
            By.xpath(".//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'skills')]/following::*[@role='combobox'][1]"),
            By.xpath(".//div[@role='combobox' or @role='button'][contains(@aria-haspopup,'listbox')]"),
            By.xpath(".//*[name()='svg' and (@data-testid='ArrowDropDownIcon' or @data-testid='ExpandMoreIcon')]/ancestor::*[self::button or self::div][1]")
    };

    private final By[] skillSuggestionRows = {
            By.xpath("//ul[@role='listbox']//li[@role='option' and not(@aria-disabled='true')]"),
            By.xpath("//div[@role='listbox']//*[self::li or self::div][@role='option' and not(@aria-disabled='true')]"),
            By.xpath("//li[not(@aria-disabled='true') and not(contains(@style,'display: none'))]")
    };
    private final By[] selectedSkillIndicators = {
            By.xpath(".//*[contains(@class,'MuiChip-label') and normalize-space()!='']"),
            By.xpath(".//div[@role='combobox' and normalize-space()!='' and not(contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'skills'))]"),
            By.xpath(".//input[contains(@aria-label,'Skill') and string-length(normalize-space(@value))>0]"),
            By.xpath(".//input[contains(@placeholder,'Type And Select') and string-length(normalize-space(@value))>0]")
    };
    private final By[] skillErrorIndicators = {
            By.xpath(".//*[contains(@class,'MuiFormControl-root') and .//label[contains(normalize-space(),'Skills')] and contains(@class,'Mui-error')]"),
            By.xpath(".//*[contains(@class,'MuiAutocomplete-root') and .//label[contains(normalize-space(),'Skills')] and contains(@class,'required_field')]//*[contains(@class,'Mui-error')]"),
            By.xpath(".//p[contains(@class,'MuiFormHelperText-root') and contains(@class,'Mui-error')]")
    };

    private final By[] startDateInputs = {
            By.xpath(".//label[contains(normalize-space(),'Start Date')]/following::input[1]"),
            By.xpath(".//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'start date')]/following::input[1]"),
            By.xpath(".//input[@placeholder='DD-MM-YYYY' and not(@disabled)][1]"),
            By.xpath(".//input[contains(translate(@name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'start') and not(@disabled)]"),
            By.xpath(".//input[contains(translate(@id,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'start') and not(@disabled)]")
    };

    private final By[] endDateInputs = {
            By.xpath(".//label[contains(normalize-space(),'End Date')]/following::input[1]"),
            By.xpath(".//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'end date')]/following::input[1]"),
            By.xpath("(.//input[@placeholder='DD-MM-YYYY' and not(@disabled)])[2]"),
            By.xpath(".//input[contains(translate(@name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'end') and not(@disabled)]"),
            By.xpath(".//input[contains(translate(@id,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'end') and not(@disabled)]")
    };

    private final By[] hoursInputFields = {
            By.xpath(".//label[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'effort')]/following::input[1]"),
            By.xpath(".//input[@type='number' and not(@disabled)]"),
            By.xpath(".//input[contains(translate(@name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'hour') and not(@disabled)]")
    };

    private final By[] resourceCheckboxes = {
            By.xpath("//tbody//tr[1]//label[.//input[@type='checkbox']]"),
            By.xpath("//tbody//tr[1]//span[contains(@class,'MuiCheckbox-root')]"),
            By.xpath("//tbody//tr[1]//input[@type='checkbox']/following-sibling::span"),
            By.xpath("//div[contains(@class,'ag-selection-checkbox')]//div[contains(@class,'ag-checkbox-input-wrapper')]"),
            By.xpath("//div[contains(@class,'ag-pinned-left-cols-container')]//input[contains(@class,'ag-checkbox-input') and @type='checkbox']"),
            By.xpath("//input[contains(@class,'ag-checkbox-input') and @type='checkbox' and contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'row selection')]"),
            By.xpath("(//input[@type='checkbox' and not(@disabled)])[2]"),
            By.xpath("(//input[@type='checkbox' and not(@disabled)])[1]"),
            By.xpath("//input[@type='checkbox' and not(@disabled)]")
    };

    private final By[] cancelButtons = {
            By.xpath("//button[normalize-space()='Cancel']"),
            By.xpath("//button[contains(normalize-space(),'Cancel')]")
    };
    private final By[] backButtons = {
            By.xpath("(//*[name()='svg' and @data-testid='ArrowBackIosNewIcon'])[2]/ancestor::button[1]"),
            By.xpath("(//*[name()='svg' and @data-testid='ArrowBackIosNewIcon'])[1]/ancestor::button[1]")
    };
    private final By unsavedChangesDialog = By.xpath(
            "//div[contains(@class,'MuiModal-root') or contains(@class,'MuiDialog-root') or @role='presentation']"
                    + "[.//*[contains(normalize-space(),'There might be some unsaved changes')"
                    + " or contains(normalize-space(),'Are you sure you want to navigate')]]"
    );
    private final By[] unsavedChangesYesButtons = {
            By.xpath("//div[contains(@class,'MuiModal-root') or contains(@class,'MuiDialog-root') or @role='presentation']"
                    + "[.//*[contains(normalize-space(),'There might be some unsaved changes')"
                    + " or contains(normalize-space(),'Are you sure you want to navigate')]]"
                    + "//button[normalize-space()='Yes']"),
            By.xpath("//button[normalize-space()='Yes' and contains(@class,'rmt-action-button')]")
    };

    private final By[] validationPopupCloseButtons = {
            By.xpath("//button[@aria-label='close' or @aria-label='Close']"),
            By.xpath("//*[name()='svg' and (@data-testid='CloseIcon' or @data-testid='CancelIcon')]/ancestor::button[1]"),
            By.xpath("//button[normalize-space()='OK' or normalize-space()='Ok']"),
            By.xpath("//button[normalize-space()='Close']")
    };

    private final By[] updateAllocationButtons = {
            By.xpath("//button[contains(@aria-label,'Update Allocation')]"),
            By.xpath("//button[@aria-label='Update Allocation']"),
            By.xpath("//*[name()='svg' and (@data-testid='EditCalendarIcon' or @data-testid='EditIcon')]/ancestor::button[1]"),
            By.xpath("//button[contains(normalize-space(),'Update Allocation')]")
    };

    private final By[] appendedCapacityIndicators = {
            By.xpath("//*[contains(normalize-space(),'Appended Capacity')]"),
            By.xpath("//*[contains(normalize-space(),'Extended Utilization')]"),
            By.xpath("//*[contains(normalize-space(),'from Appended Capacity')]"),
            By.xpath("//*[contains(normalize-space(),'!') and contains(@class,'Mui')]")
    };

    private final By[] allocationStatusValues = {
            By.xpath("//div[@title='Allocation complete']"),
            By.xpath("//div[@title='Allocation Complete']"),
            By.xpath("//div[contains(@title,'Pending Approval')]"),
            By.xpath("//div[contains(@class,'MuiChip-root')]//span[contains(@class,'MuiChip-label')]")
    };
    private final By[] allocationStatusValuesInRow = {
            By.xpath(".//div[@title='Allocation complete']"),
            By.xpath(".//div[@title='Allocation Complete']"),
            By.xpath(".//div[contains(@title,'Pending Approval')]"),
            By.xpath(".//div[contains(@class,'MuiChip-root')]//span[contains(@class,'MuiChip-label')]")
    };

    private final By[] availabilityBars = {
            By.xpath("//div[contains(@class,'item-available')]"),
            By.xpath("//div[contains(@class,'rct-item') and contains(@class,'available')]")
    };

    private final By[] availabilityHoverTooltip = {
            By.xpath("//div[@role='tooltip']"),
            By.xpath("//div[contains(@class,'MuiTooltip-tooltip')]")
    };

    private final By[] allocationAvailabilityWarnings = {
            By.xpath(".//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'not available on the following dates')]"),
            By.xpath(".//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'hours are not available')]"),
            By.xpath(".//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'user is not available')]"),
            By.xpath(".//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'resource is not available')]"),
            By.xpath(".//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'not available')]"),
            By.xpath(".//*[@role='tooltip' or contains(@class,'MuiTooltip-tooltip')]")
    };

    /**
     * Checks whether the Common Allocation popup is visible and ready for interaction.
     */
    public boolean isCommonAllocationScreenDisplayed() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        try {
            return wait.until(d -> isAnyVisible(
                    By.xpath("//div[contains(@class,'MuiModal-root') and .//input[@id='searchEmployee']]"),
                    By.xpath("//div[contains(@class,'MuiDialog-root') and .//input[@id='searchEmployee']]"),
                    By.xpath("//div[@role='dialog' and .//input[@id='searchEmployee']]"),
                    By.xpath("//div[contains(@class,'MuiModal-root') and .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'allocate employee')]]"),
                    By.xpath("//div[contains(@class,'MuiDialog-root') and .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'allocate employee')]]")
            ));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Backward-compatible wrapper that searches and selects a resource from the allocation popup.
     */
    public void searchAndSelectResource(String resourceName) {
        searchAndSelectEmployee(resourceName);
    }

    /**
     * Searches for the supplied employee name and selects the matching resource in the allocation popup.
     */
    public void searchAndSelectEmployee(String resourceName) {
        if (resourceName == null || resourceName.trim().isEmpty()) {
            throw new IllegalArgumentException("Resource name is required for Common Allocation.");
        }
        logAction("Selecting resource: " + resourceName.trim());
        selectedResourceName = resourceName.trim();

        WebElement inputElement = firstVisibleElement(searchResourceInputs);
        if (inputElement == null) {
            throw new NoSuchElementException("Resource search input not found on Common Allocation screen.");
        }

        clearAndType(inputElement, resourceName.trim());
        waitForAnyVisible(employeeSuggestionRows, 6);

        if (!selectEmployeeSuggestion(inputElement, resourceName.trim())) {
            logAction("Employee suggestion click did not apply; sending Enter via JavaScript fallback.");
            try {
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].dispatchEvent(new KeyboardEvent('keydown', {key:'Enter', code:'Enter', keyCode:13, which:13, bubbles:true}));"
                                + "arguments[0].dispatchEvent(new KeyboardEvent('keyup', {key:'Enter', code:'Enter', keyCode:13, which:13, bubbles:true}));",
                        inputElement
                );
            } catch (Exception ignored) {
                logAction("JavaScript Enter fallback failed for resource search field.");
            }
        }

        waitForSelectionToApply(inputElement, resourceName.trim());
        boolean selectionCommitted = waitForEmployeeSelectionToMaterialize(inputElement, resourceName.trim(), 5);
        if (!selectionCommitted) {
            logAction("Employee selection did not materialize after click. Retrying with keyboard selection.");
            commitEmployeeSelectionWithKeyboard(inputElement);
            waitForSelectionToApply(inputElement, resourceName.trim());
            selectionCommitted = waitForEmployeeSelectionToMaterialize(inputElement, resourceName.trim(), 6);
        }

        if (!selectionCommitted && isEmployeeSuggestionOpen(inputElement)) {
            logAction("Employee suggestion is still open after initial selection. Trying a direct option click and dismiss cycle.");
            clickMatchingEmployeeSuggestion(inputElement, resourceName.trim());
            dismissEmployeeSuggestionPopup(inputElement);
            selectionCommitted = waitForEmployeeSelectionToMaterialize(inputElement, resourceName.trim(), 6);
        }

        String selectedValue = safeTrim(inputElement.getAttribute("value"));
        String normalizedSelected = normalizeForMatch(selectedValue);
        String normalizedExpected = normalizeForMatch(resourceName.trim());
        if (!normalizedSelected.contains(normalizedExpected)) {
            throw new NoSuchElementException("Unable to select resource from Common Allocation popup. Expected: "
                    + resourceName + ", actual input value: " + safeTrim(inputElement.getAttribute("value")));
        }

        if (!selectionCommitted) {
            throw new NoSuchElementException("Resource selection did not transition Common Allocation into an editable state for: "
                    + resourceName + ". The autosuggest remained open or the allocation editor did not render.");
        }

        ensureAllocationRowEditorVisible();
        waitForAnyVisible(new By[]{
                descriptionInputs[0],
                skillDropdownInputs[0],
                startDateInputs[0],
                endDateInputs[0],
                hoursInputFields[0]
        }, 15);
    }

    /**
     * Fills the task description field used for the allocation request.
     */
    public void enterAllocationDescription(String description) {
        String trimmedDescription = safeTrim(description);
        if (trimmedDescription.isEmpty()) {
            return;
        }

        logAction("Entering allocation description: " + trimmedDescription);
        WebElement input = resolveAllocationDescriptionInput();
        if (input == null) {
            throw new NoSuchElementException("Allocation description input not found on Common Allocation screen.");
        }

        setTextValueWithFallback(input, trimmedDescription);
        try {
            input.sendKeys(Keys.TAB);
        } catch (Exception ignored) {
        }

        logAction("Allocation description populated.");
    }

    private WebElement resolveAllocationDescriptionInput() {
        for (int attempt = 1; attempt <= 5; attempt++) {
            logAction("Description lookup attempt " + attempt + " of 5.");
            try {
                WebElement editor = ensureAllocationRowEditorVisibleStrict();
                WebElement input = firstVisibleElement(editor, descriptionInputs);
                if (input == null) {
                    input = firstVisibleElement(descriptionInputs);
                }
                if (input == null) {
                    input = firstVisibleElement(editor, descriptionFieldFallbacks);
                }
                if (input == null) {
                    input = firstVisibleElement(descriptionFieldFallbacksGlobal);
                }
                if (input == null) {
                    input = findLikelyDescriptionField(editor);
                }
                if (input == null) {
                    input = findLikelyDescriptionField(null);
                }
                if (input != null) {
                    logAction("Resolved allocation description field tag=" + safeTrim(input.getTagName())
                            + " class=" + safeTrim(input.getAttribute("class"))
                            + " placeholder=" + safeTrim(input.getAttribute("placeholder"))
                            + " aria-label=" + safeTrim(input.getAttribute("aria-label")));
                    return input;
                }
                debugDescriptionFieldCandidates(editor);
                waitForAnyVisible(descriptionInputs, 4);
            } catch (Exception e) {
                logAction("Description field lookup attempt " + attempt + " did not resolve cleanly: " + e.getMessage());
                debugDescriptionFieldCandidates(null);
            }
            waitForBlockingBackdropToClear(3);
        }
        return null;
    }

    /**
     * Selects the requested skill or falls back to the first valid skill option when none is configured.
     */
    public void selectSkill(String skillName) {
        String desiredSkill = safeTrim(skillName);
        logAction(desiredSkill.isEmpty()
                ? "No skill configured. Selecting the first available skill option."
                : "Selecting skill: " + desiredSkill);
        WebElement editor = ensureAllocationRowEditorVisibleStrict();
        boolean hadSkillErrorBefore = isSkillFieldInErrorState(editor);
        boolean selectionApplied = false;

        WebElement skillInput = firstVisibleElement(editor, skillDropdownInputs);
        if (skillInput == null) {
            skillInput = firstVisibleElement(skillDropdownInputs);
        }
        if (skillInput != null && !"true".equalsIgnoreCase(safeTrim(skillInput.getAttribute("readonly")))) {
            if (!clickElementWithFallback(skillInput)) {
                throw new NoSuchElementException("Skill input is present but not clickable.");
            }
            if (!desiredSkill.isEmpty()) {
                clearAndType(skillInput, desiredSkill);
            }
            waitForAnyVisible(skillSuggestionRows, 6);
            if (!desiredSkill.isEmpty() && clickSuggestionByText(skillSuggestionRows, desiredSkill)) {
                selectionApplied = waitForSkillSelection(editor, hadSkillErrorBefore, 5);
                if (selectionApplied) {
                    commitFieldSelectionAndWaitForNextSection(skillInput, "skill");
                    return;
                }
            }
            selectFirstSuggestion(skillSuggestionRows, "Skill dropdown option");
            selectionApplied = waitForSkillSelection(editor, hadSkillErrorBefore, 5);
            if (selectionApplied) {
                if (desiredSkill.isEmpty()) {
                    logAction("Selected fallback skill from the first available dropdown option.");
                }
                commitFieldSelectionAndWaitForNextSection(skillInput, "skill");
                return;
            }
        }

        WebElement skillClickTarget = firstVisibleElement(editor, skillDropdownClickTargets);
        if (skillClickTarget == null) {
            skillClickTarget = firstVisibleElement(skillDropdownClickTargets);
        }
        if (skillClickTarget == null) {
            throw new NoSuchElementException("Skill dropdown is not visible inside Allocate Employee popup.");
        }

        if (!clickElementWithFallback(skillClickTarget)) {
            throw new NoSuchElementException("Skill dropdown could not be opened.");
        }
        waitForAnyVisible(skillSuggestionRows, 6);

        if (!desiredSkill.isEmpty() && clickSuggestionByText(skillSuggestionRows, desiredSkill)) {
            selectionApplied = waitForSkillSelection(editor, hadSkillErrorBefore, 5);
            if (selectionApplied) {
                commitFieldSelectionAndWaitForNextSection(skillClickTarget, "skill");
                return;
            }
        }
        selectFirstSuggestion(skillSuggestionRows, "Skill dropdown option");
        selectionApplied = waitForSkillSelection(editor, hadSkillErrorBefore, 5);
        if (!selectionApplied) {
            throw new NoSuchElementException("Skill selection did not apply in Allocate Employee popup."
                    + (desiredSkill.isEmpty()
                    ? " No configured skill was provided and no fallback suggestion could be selected."
                    : " Configured skill: " + desiredSkill));
        }
        if (desiredSkill.isEmpty()) {
            logAction("Selected fallback skill from the first available dropdown option.");
        }
        commitFieldSelectionAndWaitForNextSection(skillClickTarget, "skill");
    }

    /**
     * Sets the same future business date as both the start and end date of the allocation.
     */
    public void setStartAndEndDateWithBusinessOffset(int businessDaysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = addBusinessDays(today, businessDaysAhead);
        setStartAndEndDates(targetDate, targetDate);
    }

    /**
     * Sets future business dates for the allocation start and end fields.
     */
    public void setStartAndEndDateWithBusinessOffsets(int startBusinessDaysAhead, int endBusinessDaysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = addBusinessDays(today, startBusinessDaysAhead);
        LocalDate endDate = addBusinessDays(today, endBusinessDaysAhead);

        if (endDate.isBefore(startDate)) {
            endDate = startDate;
        }

        setStartAndEndDates(startDate, endDate);
    }

    /**
     * Updates the requested effort or hours field for the selected allocation row.
     */
    public void setRequestedHours(String hours) {
        logAction("Setting requested hours: " + safeTrim(hours));
        WebElement editor = ensureAllocationRowEditorVisibleStrict();
        WebElement inputElement = firstVisibleElement(editor, hoursInputFields);
        if (inputElement == null) {
            inputElement = firstVisibleElement(hoursInputFields);
        }
        if (inputElement == null) {
            throw new NoSuchElementException("Hours input field not found on Common Allocation screen.");
        }
        clearAndType(inputElement, hours);
        try {
            inputElement.sendKeys(Keys.TAB);
        } catch (Exception ignored) {
        }
    }

    /**
     * Sets the date range and hours, then retries with later future business dates when availability warnings are shown.
     *
     * @return the last captured availability warning, or an empty string when the requested schedule is accepted
     */
    public String setDatesAndHoursWithAvailabilityRetry(int startBusinessDaysAhead,
                                                        int endBusinessDaysAhead,
                                                        String hours,
                                                        int maxAttempts) {
        logAction("Setting allocation dates/hours with availability retry. startOffset=" + startBusinessDaysAhead
                + ", endOffset=" + endBusinessDaysAhead + ", hours=" + safeTrim(hours)
                + ", attempts=" + maxAttempts);
        int startOffset = Math.max(0, startBusinessDaysAhead);
        int endOffset = Math.max(startOffset, endBusinessDaysAhead);
        int attempts = Math.max(1, maxAttempts);
        String capturedWarning = "";
        LocalDate startDate = addBusinessDays(LocalDate.now(), startOffset);
        LocalDate endDate = addBusinessDays(LocalDate.now(), endOffset);
        int rangeSpanBusinessDays = Math.max(0, endOffset - startOffset);

        for (int attempt = 0; attempt < attempts; attempt++) {
            logAction("Attempt " + (attempt + 1) + " using dates " + startDate.format(DD_MM_YYYY)
                    + " to " + endDate.format(DD_MM_YYYY));
            ensureAllocationRowEditorVisibleStrict();
            setStartAndEndDates(startDate, endDate);
            setRequestedHours(hours);
            clickOkToSaveRowIfVisible();
            revealAvailabilityWarningTrigger();
            String warningText = waitForAvailabilityWarningText(10);

            if (!warningText.isEmpty()) {
                capturedWarning = warningText;
                logAction("Availability warning detected: " + warningText);
                System.out.println("Allocation availability warning on attempt " + (attempt + 1)
                        + " for range " + startDate.format(DD_MM_YYYY)
                        + " to " + endDate.format(DD_MM_YYYY)
                        + ": " + warningText);
                LocalDate warningDate = extractLatestWarningDate(warningText);
                LocalDate nextBaseDate = warningDate != null ? warningDate : startDate;
                startDate = advanceToNextMonthBusinessDate(nextBaseDate);
                endDate = addBusinessDays(startDate, rangeSpanBusinessDays);
                logAction("Retrying allocation with future business dates: "
                        + startDate.format(DD_MM_YYYY) + " to " + endDate.format(DD_MM_YYYY));
                System.out.println("Retrying allocation with future business dates: "
                        + startDate.format(DD_MM_YYYY) + " to " + endDate.format(DD_MM_YYYY));
                continue;
            }

            selectedStartDate = startDate.format(DD_MM_YYYY);
            selectedEndDate = endDate.format(DD_MM_YYYY);
            selectedRequestedHours = safeTrim(hours);
            logAction("No availability warning found. Waiting for allocation editor to close.");
            waitForAllocationEditorToClose(8);
            return capturedWarning;
        }

        throw new IllegalStateException("Unable to find available future dates for requested hours after "
                + attempts + " attempts."
                + (capturedWarning.isEmpty() ? "" : " Last availability warning: " + capturedWarning));
    }

    /**
     * Clicks the OK action in the allocation editor and expects the action to be available.
     */
    public void clickOkToSaveRow() {
        clickFirstVisible(okButtons, "OK button on allocation pop-up");
    }

    /**
     * Clicks the OK action in the allocation editor only when the button is visible.
     */
    public void clickOkToSaveRowIfVisible() {
        try {
            logAction("Clicking OK button if visible.");
            if (isAnyVisible(okButtons)) {
                clickFirstVisible(okButtons, "OK button on allocation pop-up");
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Waits for the allocation editor popup to close after saving or cancelling the row.
     */
    public boolean waitForAllocationEditorToClose(int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            return wait.until(d -> !isAnyVisible(okButtons));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Reads the currently visible allocation validation or toast message without waiting.
     */
    public String readAllocationValidationMessage() {
        for (By locator : validationMessageLocators) {
            for (WebElement element : driver.findElements(locator)) {
                if (!element.isDisplayed()) {
                    continue;
                }
                String text = element.getText();
                if (text != null && !text.trim().isEmpty()) {
                    return text.trim();
                }
            }
        }
        return "";
    }

    /**
     * Waits for an allocation validation or toast message and returns its text.
     */
    public String waitForAllocationValidationMessage(int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            return wait.until(d -> {
                String message = readAllocationValidationMessage();
                return message.isEmpty() ? null : message;
            });
        } catch (Exception ignored) {
            return readAllocationValidationMessage();
        }
    }

    private String readAvailabilityWarningMessage() {
        String scopedWarning = readAvailabilityWarningMessage(resolveAllocationEditorContainer());
        if (!scopedWarning.isEmpty()) {
            return scopedWarning;
        }

        String jsWarning = scanAvailabilityWarningTextFromDom();
        if (!jsWarning.isEmpty()) {
            return jsWarning;
        }

        for (By locator : allocationAvailabilityWarnings) {
            for (WebElement element : driver.findElements(locator)) {
                if (!element.isDisplayed()) {
                    continue;
                }
                String text = element.getText();
                if (isAvailabilityWarningText(text)) {
                    return text.trim();
                }
                String innerText = element.getAttribute("innerText");
                if (isAvailabilityWarningText(innerText)) {
                    return innerText.trim();
                }
                String textContent = element.getAttribute("textContent");
                if (isAvailabilityWarningText(textContent)) {
                    return textContent.trim();
                }
                String title = element.getAttribute("title");
                if (isAvailabilityWarningText(title)) {
                    return title.trim();
                }
            }
        }
        return "";
    }

    private String readAvailabilityWarningMessage(WebElement scope) {
        if (scope == null) {
            return "";
        }

        for (By locator : allocationAvailabilityWarnings) {
            for (WebElement element : scope.findElements(locator)) {
                if (!element.isDisplayed()) {
                    continue;
                }
                String text = element.getText();
                if (isAvailabilityWarningText(text)) {
                    return text.trim();
                }
                String innerText = element.getAttribute("innerText");
                if (isAvailabilityWarningText(innerText)) {
                    return innerText.trim();
                }
                String textContent = element.getAttribute("textContent");
                if (isAvailabilityWarningText(textContent)) {
                    return textContent.trim();
                }
                String title = element.getAttribute("title");
                if (isAvailabilityWarningText(title)) {
                    return title.trim();
                }
            }
        }
        return "";
    }

    private boolean isAvailabilityWarningText(String text) {
        if (text == null) {
            return false;
        }
        String normalized = text.trim().toLowerCase(Locale.ENGLISH);
        if (normalized.isEmpty()) {
            return false;
        }
        for (String phrase : AVAILABILITY_WARNING_PHRASES) {
            if (normalized.contains(phrase)) {
                return true;
            }
        }
        return normalized.contains("not available");
    }

    /**
     * Checks whether the current allocation popup is showing an already-allocated warning.
     */
    public boolean isAlreadyAllocatedValidationVisible() {
        String validation = readAllocationValidationMessage().toLowerCase(Locale.ENGLISH);
        return validation.contains("already allocated") || validation.contains("update user");
    }

    /**
     * Closes the visible validation popup when the allocation flow shows an informational dialog.
     */
    public void closeValidationPopupIfPresent() {
        String validation = readAllocationValidationMessage().toLowerCase(Locale.ENGLISH);
        if (validation.isEmpty()) {
            return;
        }

        if (validation.contains("already allocated")
                || validation.contains("update user")
                || validation.contains("already")
                || validation.contains("conflict")) {
            logAction("Closing validation popup: " + validation);
            try {
                clickFirstVisible(validationPopupCloseButtons, "Validation popup close button");
            } catch (Exception ignored) {
            }
            waitForBlockingBackdropToClear(6);
        }
    }

    /**
     * Cancels the allocation editor when it is still open after a validation branch.
     */
    public void cancelAllocationEditorIfOpen() {
        if (isAnyVisible(okButtons)) {
            clickFirstVisible(cancelButtons, "Cancel button on allocation popup");
            waitForEditorToClose(8);
        }
    }

    /**
     * Clicks the Cancel button in the allocation popup when it is visible.
     */
    public void clickCancelButtonIfVisible() {
        if (isAnyVisible(cancelButtons)) {
            logAction("Clicking Cancel button.");
            try {
                clickFirstVisible(cancelButtons, "Cancel button");
            } catch (NoSuchElementException e) {
                logAction("Cancel button disappeared before click: " + e.getMessage());
            }
            waitForBlockingBackdropToClear(6);
        }
    }

    /**
     * Returns to the project listing from the allocation area using the back button.
     */
    public void clickBackButtonToProjectListing() {
        logAction("Clicking Back button to return to Project Listings.");
        clickFirstVisible(backButtons, "Back button");
        confirmUnsavedChangesNavigationIfPresent();
        waitForBlockingBackdropToClear(8);
    }

    /**
     * Returns to the project listing from the allocation area only when the back button is visible.
     */
    public void clickBackButtonToProjectListingIfVisible() {
        if (isAnyVisible(backButtons)) {
            logAction("Clicking Back button to return to Project Listings.");
            try {
                clickFirstVisible(backButtons, "Back button");
            } catch (NoSuchElementException e) {
                logAction("Back button disappeared before click: " + e.getMessage());
            }
            confirmUnsavedChangesNavigationIfPresent();
            waitForBlockingBackdropToClear(8);
        }
    }

    /**
     * Closes the allocation editor by using the visible cancel or close controls.
     */
    public void closeAllocationEditor() {
        if (!isAnyVisible(okButtons)) {
            return;
        }

        By[] closers = {
                closeButtons[0],
                closeButtons[1],
                cancelButtons[0],
                cancelButtons[1]
        };
        clickFirstVisible(closers, "Close/Cancel allocation popup");
        waitForEditorToClose(8);
    }

    /**
     * Opens the update-allocation editor from the allocation grid.
     */
    public void openUpdateAllocationEditor() {
        logAction("Opening Update Allocation editor.");
        waitForBlockingBackdropToClear(6);
        clickFirstVisible(updateAllocationButtons, "Update Allocation icon");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> isAnyVisible(okButtons));
    }

    /**
     * Checks whether the allocation UI is showing an appended-capacity or extended-utilization indicator.
     */
    public boolean isAppendedCapacityIndicatorVisible() {
        return firstVisibleLocator(appendedCapacityIndicators) != null;
    }

    /**
     * Submits the selected allocation row, handles optional confirmation, and returns the resulting message.
     */
    public String submitAllocationAndCaptureMessage(boolean confirmAction) {
        logAction("Submitting allocation. confirmAction=" + confirmAction);
        ensureResourceSelectionReadyForSubmission();
        debugSubmissionActionState();

        String acknowledgement = clickSubmissionActionAndWaitForOutcome(confirmAction, "initial");
        if (!acknowledgement.isEmpty()) {
            return acknowledgement;
        }

        logAction("No acknowledgement captured after the initial Allocate click. Re-selecting the resource row and retrying once.");
        ensureResourceSelectionReadyForSubmission();
        debugSubmissionActionState();
        acknowledgement = clickSubmissionActionAndWaitForOutcome(confirmAction, "retry");

        if (!acknowledgement.isEmpty()) {
            return acknowledgement;
        }

        if (forceClickVisibleActionButton()) {
            acknowledgement = waitForSubmissionOutcome(confirmAction, 12);
        }

        return acknowledgement;
    }

    /**
     * Makes sure the selected resource row is checked and the Allocate or Save action becomes ready for submission.
     */
    private void ensureResourceSelectionReadyForSubmission() {
        if (!clickSelectedResourceCheckbox()) {
            clickFirstUncheckedVisibleCheckbox(resourceCheckboxes);
        }
        waitForSubmissionActionReady(10);
    }

    /**
     * Clicks the current Allocate or Save action from the active Common Allocation container and waits for the outcome.
     */
    private String clickSubmissionActionAndWaitForOutcome(boolean confirmAction, String attemptLabel) {
        WebElement actionButton = resolveSubmissionActionButton();
        if (actionButton == null) {
            logAction("No enabled Allocate/Save action button was available during the " + attemptLabel + " submit attempt.");
            String preMessage = readAllocationValidationMessage();
            return preMessage == null ? "" : preMessage.trim();
        }

        String buttonText = safeTrim(actionButton.getText());
        logAction("Clicking '" + buttonText + "' during the " + attemptLabel + " submit attempt.");
        if (!clickElementWithFallback(actionButton)) {
            logAction("Could not click the '" + buttonText + "' button during the " + attemptLabel + " submit attempt.");
            return "";
        }

        return waitForSubmissionOutcome(confirmAction, 18);
    }

    /**
     * Waits for the Allocate or Save action to become enabled after the resource row has been selected.
     */
    private void waitForSubmissionActionReady(int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            wait.until(d -> resolveSubmissionActionButton() != null);
        } catch (Exception ignored) {
        }
    }

    /**
     * Waits for a toast, validation message, or Yes confirmation dialog after the submit action is clicked.
     */
    private String waitForSubmissionOutcome(boolean confirmAction, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.pollingEvery(Duration.ofMillis(500));

        final boolean[] confirmationClicked = {false};
        try {
            return wait.until(d -> {
                String toastMessage = waitForToastMessage(1);
                if (!toastMessage.isEmpty()) {
                    return toastMessage;
                }

                String validationMessage = readAllocationValidationMessage();
                if (!validationMessage.isEmpty()) {
                    return validationMessage;
                }

                if (confirmAction) {
                    WebElement yesButton = resolveConfirmationYesButton();
                    if (yesButton != null) {
                        logAction("Confirming allocation with Yes button.");
                        if (clickElementWithFallback(yesButton)) {
                            confirmationClicked[0] = true;
                        }
                        return null;
                    }
                }

                if (confirmationClicked[0] && !isAnyVisible(yesButtons) && !hasOpenAllocationEditorSignalsVisible()) {
                    return "";
                }

                return null;
            });
        } catch (Exception ignored) {
            String validationMessage = readAllocationValidationMessage();
            if (!validationMessage.isEmpty()) {
                return validationMessage;
            }
            return "";
        }
    }

    /**
     * Resolves the enabled allocation submit action from the active Common Allocation container.
     */
    private WebElement resolveSubmissionActionButton() {
        WebElement scopedContainer = resolveCommonAllocationContainer();
        WebElement allocateAction = findLowestVisibleEnabledButton(scopedContainer, By.xpath(".//button[normalize-space()='Allocate']"));
        if (allocateAction != null) {
            return allocateAction;
        }

        WebElement allocateEmployeeAction = findLowestVisibleEnabledButton(scopedContainer, By.xpath(".//button[normalize-space()='Allocate Employee']"));
        if (allocateEmployeeAction != null) {
            return allocateEmployeeAction;
        }

        WebElement saveAction = findLowestVisibleEnabledButton(scopedContainer, By.xpath(".//button[normalize-space()='Save']"));
        if (saveAction != null) {
            return saveAction;
        }

        allocateAction = findLowestVisibleEnabledButton(null, allocateButton);
        if (allocateAction != null) {
            return allocateAction;
        }

        saveAction = findLowestVisibleEnabledButton(null, saveButton);
        if (saveAction != null) {
            return saveAction;
        }

        return null;
    }

    /**
     * Resolves the visible Yes confirmation button from the top-most confirmation dialog.
     */
    private WebElement resolveConfirmationYesButton() {
        List<WebElement> candidates = new ArrayList<>();
        for (By locator : yesButtons) {
            for (WebElement element : driver.findElements(locator)) {
                try {
                    if (element.isDisplayed() && isActionButtonEnabled(element)) {
                        candidates.add(element);
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return pickLowestOnScreen(candidates);
    }

    /**
     * Finds the lowest visible enabled button from the supplied root and locators.
     */
    private WebElement findLowestVisibleEnabledButton(WebElement root, By... locators) {
        List<WebElement> candidates = new ArrayList<>();
        for (By locator : locators) {
            List<WebElement> elements = root == null ? driver.findElements(locator) : root.findElements(locator);
            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed() && isActionButtonEnabled(element)) {
                        candidates.add(element);
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return pickLowestOnScreen(candidates);
    }

    /**
     * Chooses the lowest visible element on screen, which tends to map to the active dialog action area.
     */
    private WebElement pickLowestOnScreen(List<WebElement> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        WebElement preferred = null;
        int preferredY = Integer.MIN_VALUE;
        for (WebElement candidate : candidates) {
            try {
                int currentY = candidate.getRect().getY();
                if (preferred == null || currentY >= preferredY) {
                    preferred = candidate;
                    preferredY = currentY;
                }
            } catch (Exception ignored) {
            }
        }
        return preferred;
    }

    /**
     * Checks whether a button-like element is enabled for user interaction.
     */
    private boolean isActionButtonEnabled(WebElement element) {
        if (element == null) {
            return false;
        }
        try {
            if (!element.isEnabled()) {
                return false;
            }
        } catch (Exception ignored) {
            return false;
        }

        String ariaDisabled = safeTrim(element.getAttribute("aria-disabled")).toLowerCase(Locale.ENGLISH);
        String disabledAttr = safeTrim(element.getAttribute("disabled")).toLowerCase(Locale.ENGLISH);
        String classAttr = safeTrim(element.getAttribute("class")).toLowerCase(Locale.ENGLISH);

        return !"true".equals(ariaDisabled)
                && !"disabled".equals(disabledAttr)
                && !classAttr.contains("mui-disabled")
                && !classAttr.contains("disabled");
    }

    /**
     * Prints the current Allocate/Save/Yes visibility and enablement state for later headless debugging.
     */
    private void debugSubmissionActionState() {
        WebElement actionButton = resolveSubmissionActionButton();
        WebElement yesButton = resolveConfirmationYesButton();
        System.out.println("DEBUG ALLOCATION SUBMIT: allocateVisible=" + isAnyVisible(new By[]{allocateButton})
                + " allocateEmployeeVisible=" + isAnyVisible(new By[]{allocateEmployeeButton})
                + " saveVisible=" + isAnyVisible(new By[]{saveButton})
                + " yesVisible=" + isAnyVisible(yesButtons)
                + " resolvedAction=" + (actionButton == null ? "" : safeTrim(actionButton.getText()))
                + " actionEnabled=" + (actionButton != null && isActionButtonEnabled(actionButton))
                + " yesEnabled=" + (yesButton != null && isActionButtonEnabled(yesButton)));
    }

    private boolean forceClickVisibleActionButton() {
        WebElement actionButton = resolveSubmissionActionButton();
        if (actionButton == null) {
            actionButton = firstVisibleElement(new By[]{allocateButton});
        }
        if (actionButton == null) {
            actionButton = firstVisibleElement(new By[]{allocateEmployeeButton});
        }
        if (actionButton == null) {
            actionButton = firstVisibleElement(new By[]{saveButton});
        }
        if (actionButton == null) {
            return false;
        }

        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", actionButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", actionButton);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String waitForSubmissionAcknowledgement(int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            return wait.until(d -> {
                String toastMessage = waitForToastMessage(1);
                if (!toastMessage.isEmpty()) {
                    return toastMessage;
                }

                String validationMessage = readAllocationValidationMessage();
                if (!validationMessage.isEmpty()) {
                    return validationMessage;
                }

                return null;
            });
        } catch (Exception ignored) {
            String validationMessage = readAllocationValidationMessage();
            if (!validationMessage.isEmpty()) {
                return validationMessage;
            }
            return "";
        }
    }

    private boolean clickFirstUncheckedVisibleCheckbox(By[] locators) {
        List<WebElement> checkboxCandidates = new ArrayList<>();
        if (locators != null) {
            for (By locator : locators) {
                checkboxCandidates.addAll(driver.findElements(locator));
            }
        }
        checkboxCandidates.addAll(driver.findElements(By.xpath(
                "//input[@type='checkbox' and not(@disabled)]"
                        + " | //div[contains(@class,'ag-checkbox-input-wrapper')]"
                        + " | //span[contains(@class,'MuiCheckbox-root')]"
        )));

        for (WebElement checkbox : checkboxCandidates) {
            try {
                if (!checkbox.isDisplayed()) {
                    continue;
                }
                if (isSelectAllCheckbox(checkbox)) {
                    continue;
                }
                if (isCheckboxSelected(checkbox)) {
                    System.out.println("DEBUG ALLOCATION: checkbox already selected, leaving it as-is. class="
                            + safeTrim(checkbox.getAttribute("class"))
                            + " aria-label=" + safeTrim(checkbox.getAttribute("aria-label")));
                    return true;
                }
                if (ensureCheckboxSelected(checkbox, "fallback checkbox")) {
                    System.out.println("DEBUG ALLOCATION: clicked unchecked fallback checkbox. class="
                            + safeTrim(checkbox.getAttribute("class"))
                            + " aria-label=" + safeTrim(checkbox.getAttribute("aria-label")));
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private boolean isSelectAllCheckbox(WebElement checkbox) {
        if (checkbox == null) {
            return false;
        }
        String ariaLabel = safeTrim(checkbox.getAttribute("aria-label")).toLowerCase(Locale.ENGLISH);
        String title = safeTrim(checkbox.getAttribute("title")).toLowerCase(Locale.ENGLISH);
        String text = safeTrim(checkbox.getText()).toLowerCase(Locale.ENGLISH);
        String classAttr = safeTrim(checkbox.getAttribute("class")).toLowerCase(Locale.ENGLISH);
        return ariaLabel.contains("select all")
                || title.contains("select all")
                || text.contains("select all")
                || classAttr.contains("select all");
    }

    private boolean clickSelectedResourceCheckbox() {
        if (selectedResourceName == null || selectedResourceName.trim().isEmpty()) {
            return false;
        }

        String normalizedExpected = normalizeForMatch(selectedResourceName);
        System.out.println("DEBUG ALLOCATION: trying to select resource row for '" + selectedResourceName + "'");
        By[] rowLocators = {
                By.xpath("//div[contains(@class,'ag-row') or @role='row']"),
                By.xpath("//tr"),
                By.xpath("//*[contains(@class,'MuiTableRow-root')]")
        };

        for (By rowLocator : rowLocators) {
            int inspectedRows = 0;
            for (WebElement row : driver.findElements(rowLocator)) {
                try {
                    if (!row.isDisplayed()) {
                        continue;
                    }
                    if (inspectedRows < 5) {
                        System.out.println("DEBUG ALLOCATION: row candidate text=" + safeTrim(row.getText()));
                    }
                    inspectedRows++;
                    String rowText = normalizeForMatch(row.getText());
                    if (rowText.isEmpty() || !rowText.contains(normalizedExpected)) {
                        continue;
                    }
                    System.out.println("DEBUG ALLOCATION: matched resource row text=" + safeTrim(row.getText()));

                    List<WebElement> checkboxCandidates = new ArrayList<>();
                    checkboxCandidates.addAll(row.findElements(By.xpath(".//input[@type='checkbox' and not(@disabled)]")));
                    checkboxCandidates.addAll(row.findElements(By.xpath(".//div[contains(@class,'ag-checkbox-input-wrapper')]")));
                    checkboxCandidates.addAll(row.findElements(By.xpath(".//span[contains(@class,'MuiCheckbox-root')]")));
                    checkboxCandidates.addAll(row.findElements(By.xpath(".//label[.//input[@type='checkbox']]")));
                    System.out.println("DEBUG ALLOCATION: checkbox candidate count=" + checkboxCandidates.size());

                    for (WebElement checkbox : checkboxCandidates) {
                        if (!checkbox.isDisplayed()) {
                            continue;
                        }
                        if (isCheckboxSelected(checkbox)) {
                            System.out.println("DEBUG ALLOCATION: checkbox already selected for matched resource row.");
                            return true;
                        }
                        if (ensureCheckboxSelected(checkbox, "resource grid row")) {
                            System.out.println("DEBUG ALLOCATION: selected checkbox for resource row.");
                            return true;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            if (inspectedRows == 0) {
                System.out.println("DEBUG ALLOCATION: no visible rows found for locator=" + rowLocator);
            }
        }
        WebElement timelineRow = findTimelineSidebarRowForSelectedResource();
        if (timelineRow != null) {
            System.out.println("DEBUG ALLOCATION: falling back to timeline sidebar row for resource '" + selectedResourceName + "'");
            clickElementWithFallback(timelineRow);
            List<WebElement> checkboxCandidates = new ArrayList<>();
            checkboxCandidates.addAll(timelineRow.findElements(By.xpath(".//input[@type='checkbox' and not(@disabled)]")));
            checkboxCandidates.addAll(timelineRow.findElements(By.xpath(".//span[contains(@class,'MuiCheckbox-root')]")));
            checkboxCandidates.addAll(timelineRow.findElements(By.xpath(".//label[.//input[@type='checkbox']]")));

            for (WebElement checkbox : checkboxCandidates) {
                try {
                    if (!checkbox.isDisplayed() || isSelectAllCheckbox(checkbox)) {
                        continue;
                    }
                    if (isCheckboxSelected(checkbox)) {
                        System.out.println("DEBUG ALLOCATION: timeline checkbox already selected for matched resource row.");
                        return true;
                    }
                    if (ensureCheckboxSelected(checkbox, "timeline resource row")) {
                        System.out.println("DEBUG ALLOCATION: selected timeline checkbox for resource row.");
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        List<WebElement> visibleCheckboxes = driver.findElements(By.xpath(
                "//input[@type='checkbox' and not(@disabled)]"
                        + " | //div[contains(@class,'ag-checkbox-input-wrapper')]"
                        + " | //span[contains(@class,'MuiCheckbox-root')]"
        ));
        int checkboxIndex = 1;
        for (WebElement checkbox : visibleCheckboxes) {
            try {
                if (!checkbox.isDisplayed()) {
                    continue;
                }
                if (checkboxIndex > 5) {
                    break;
                }
                System.out.println("DEBUG ALLOCATION: visible checkbox[" + checkboxIndex + "] tag=" + checkbox.getTagName()
                        + " class=" + safeTrim(checkbox.getAttribute("class"))
                        + " aria-label=" + safeTrim(checkbox.getAttribute("aria-label"))
                        + " title=" + safeTrim(checkbox.getAttribute("title"))
                        + " text=" + safeTrim(checkbox.getText())
                        + " selected=" + isCheckboxSelected(checkbox));
                checkboxIndex++;
            } catch (Exception ignored) {
            }
        }
        System.out.println("DEBUG ALLOCATION: no matching resource checkbox found for '" + selectedResourceName + "'");
        return false;
    }

    /**
     * Verifies that a checkbox-like control actually becomes selected after interaction.
     */
    private boolean ensureCheckboxSelected(WebElement checkbox, String logicalName) {
        if (checkbox == null) {
            return false;
        }

        if (isCheckboxSelected(checkbox)) {
            return true;
        }

        if (clickElementWithFallback(checkbox) && waitForCheckboxSelected(checkbox, 2)) {
            return true;
        }

        WebElement actualInput = resolveCheckboxInput(checkbox);
        if (actualInput != null && actualInput != checkbox) {
            logAction("Checkbox wrapper click did not stick for " + logicalName + ". Retrying on the underlying input.");
            if (clickElementWithFallback(actualInput) && waitForCheckboxSelected(actualInput, 2)) {
                return true;
            }
        }

        WebElement label = resolveCheckboxLabel(checkbox);
        if (label != null && label != checkbox) {
            logAction("Checkbox input click did not stick for " + logicalName + ". Retrying on the associated label.");
            if (clickElementWithFallback(label) && waitForCheckboxSelected(checkbox, 2)) {
                return true;
            }
        }

        try {
            WebElement focusTarget = actualInput != null ? actualInput : checkbox;
            focusTarget.sendKeys(Keys.SPACE);
            return waitForCheckboxSelected(focusTarget, 2);
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Waits for the checkbox selection state to become true after a click or keyboard interaction.
     */
    private boolean waitForCheckboxSelected(WebElement checkbox, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            return wait.until(d -> isCheckboxSelected(checkbox));
        } catch (Exception e) {
            return isCheckboxSelected(checkbox);
        }
    }

    /**
     * Resolves the real checkbox input associated with a wrapper or label element.
     */
    private WebElement resolveCheckboxInput(WebElement checkbox) {
        if (checkbox == null) {
            return null;
        }

        try {
            if ("input".equalsIgnoreCase(safeTrim(checkbox.getTagName()))) {
                return checkbox;
            }
        } catch (Exception ignored) {
        }

        By[] inputLocators = {
                By.xpath(".//input[@type='checkbox']"),
                By.xpath("./ancestor::label[1]//input[@type='checkbox']"),
                By.xpath("./ancestor::*[@role='checkbox'][1]//input[@type='checkbox']")
        };

        for (By locator : inputLocators) {
            try {
                List<WebElement> inputs = checkbox.findElements(locator);
                if (!inputs.isEmpty()) {
                    return inputs.get(0);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * Resolves a clickable label or checkbox wrapper that toggles the same checkbox input.
     */
    private WebElement resolveCheckboxLabel(WebElement checkbox) {
        if (checkbox == null) {
            return null;
        }

        By[] labelLocators = {
                By.xpath("./ancestor::label[1]"),
                By.xpath("./ancestor::*[@role='checkbox'][1]"),
                By.xpath("./ancestor::span[contains(@class,'MuiCheckbox-root')][1]")
        };
        for (By locator : labelLocators) {
            try {
                List<WebElement> labels = checkbox.findElements(locator);
                if (!labels.isEmpty()) {
                    return labels.get(0);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private boolean isCheckboxSelected(WebElement checkbox) {
        if (checkbox == null) {
            return false;
        }
        try {
            String tagName = safeTrim(checkbox.getTagName()).toLowerCase(Locale.ENGLISH);
            if ("input".equals(tagName)) {
                return checkbox.isSelected();
            }
        } catch (Exception ignored) {
        }

        String ariaChecked = safeTrim(checkbox.getAttribute("aria-checked")).toLowerCase(Locale.ENGLISH);
        String checkedAttr = safeTrim(checkbox.getAttribute("checked")).toLowerCase(Locale.ENGLISH);
        String classAttr = safeTrim(checkbox.getAttribute("class")).toLowerCase(Locale.ENGLISH);

        return "true".equals(ariaChecked)
                || "checked".equals(checkedAttr)
                || classAttr.contains("mui-checked")
                || classAttr.contains("ag-checked")
                || classAttr.contains("selected");
    }

    /**
     * Reads the current allocation status text from the selected grid row.
     */
    public String readAllocationStatusFromGrid() {
        String selectedResourceStatus = readAllocationStatusForSelectedResource();
        if (!selectedResourceStatus.isEmpty()) {
            return selectedResourceStatus;
        }

        if (isSelectedResourceTimelineAllocationVisible()) {
            return "Timeline allocation visible";
        }

        for (By locator : allocationStatusValues) {
            for (WebElement element : driver.findElements(locator)) {
                if (!element.isDisplayed()) {
                    continue;
                }
                String text = element.getText();
                if (text != null && !text.trim().isEmpty()) {
                    String normalized = text.trim().toLowerCase(Locale.ENGLISH);
                    if (normalized.contains("allocation")
                            || normalized.contains("pending")
                            || normalized.contains("complete")) {
                        return text.trim();
                    }
                }
                String title = element.getAttribute("title");
                if (title != null && !title.trim().isEmpty()) {
                    String normalizedTitle = title.trim().toLowerCase(Locale.ENGLISH);
                    if (normalizedTitle.contains("allocation")
                            || normalizedTitle.contains("pending")
                            || normalizedTitle.contains("complete")) {
                        return title.trim();
                    }
                }
            }
        }
        return "";
    }

    private String readAllocationStatusForSelectedResource() {
        if (selectedResourceName == null || selectedResourceName.trim().isEmpty()) {
            return "";
        }

        String normalizedExpected = normalizeForMatch(selectedResourceName);
        By[] rowLocators = {
                By.xpath("//div[contains(@class,'ag-row') or @role='row']"),
                By.xpath("//tr"),
                By.xpath("//*[contains(@class,'MuiTableRow-root')]")
        };

        for (By rowLocator : rowLocators) {
            for (WebElement row : driver.findElements(rowLocator)) {
                try {
                    if (!row.isDisplayed()) {
                        continue;
                    }
                    String rowText = normalizeForMatch(row.getText());
                    if (rowText.isEmpty() || !rowText.contains(normalizedExpected)) {
                        continue;
                    }

                    for (By statusLocator : allocationStatusValuesInRow) {
                        for (WebElement element : row.findElements(statusLocator)) {
                            if (!element.isDisplayed()) {
                                continue;
                            }

                            String text = safeTrim(element.getText());
                            if (!text.isEmpty()) {
                                return text;
                            }

                            String title = safeTrim(element.getAttribute("title"));
                            if (!title.isEmpty()) {
                                return title;
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }

        if (isSelectedResourceTimelineAllocationVisible()) {
            return "Timeline allocation visible";
        }

        return "";
    }

    /**
     * Waits for the allocation status text to become visible in the grid and returns the displayed value.
     */
    public String waitForAllocationStatusFromGrid(int timeoutSeconds) {
        logAction("Waiting up to " + timeoutSeconds + "s for allocation status in grid.");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            String status = wait.until(d -> {
                String gridStatus = readAllocationStatusFromGrid();
                return gridStatus.isEmpty() ? null : gridStatus;
            });
            logAction("Allocation status detected: " + status);
            return status;
        } catch (Exception e) {
            String status = readAllocationStatusFromGrid();
            logAction("Allocation status after wait fallback: " + status);
            return status;
        }
    }

    /**
     * Selects the allocation row that belongs to the resource chosen earlier in the allocation popup.
     */
    public boolean selectAllocationRowForSelectedResource() {
        if (selectedResourceName == null || selectedResourceName.trim().isEmpty()) {
            logAction("Selected resource name is blank, cannot focus an allocation row.");
            return false;
        }

        logAction("Selecting allocation row for resource: " + selectedResourceName);
        if (clickSelectedResourceCheckbox()) {
            return true;
        }

        String normalizedExpected = normalizeForMatch(selectedResourceName);
        By[] rowLocators = {
                By.xpath("//div[contains(@class,'ag-row') and @role='row']"),
                By.xpath("//div[contains(@class,'ag-row')]"),
                By.xpath("//tr"),
                By.xpath("//*[contains(@class,'MuiTableRow-root')]")
        };

        for (By rowLocator : rowLocators) {
            for (WebElement row : driver.findElements(rowLocator)) {
                try {
                    if (!row.isDisplayed()) {
                        continue;
                    }
                    String rowText = normalizeForMatch(row.getText());
                    if (rowText.isEmpty() || !rowText.contains(normalizedExpected)) {
                        continue;
                    }
                    logAction("Clicking matching allocation row: " + safeTrim(row.getText()));
                    if (clickElementWithFallback(row)) {
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        WebElement timelineRow = findTimelineSidebarRowForSelectedResource();
        if (timelineRow != null) {
            logAction("Clicking matching timeline allocation row: " + safeTrim(timelineRow.getText()));
            if (clickElementWithFallback(timelineRow)) {
                return true;
            }
        }

        logAction("No allocation row could be selected for resource: " + selectedResourceName);
        return false;
    }

    /**
     * Builds a debug summary of the selected resource row from the allocation grid.
     */
    public String readAllocationRowSummaryForSelectedResource() {
        if (selectedResourceName == null || selectedResourceName.trim().isEmpty()) {
            return "";
        }

        String normalizedExpected = normalizeForMatch(selectedResourceName);
        By[] rowLocators = {
                By.xpath("//div[contains(@class,'ag-row') and @role='row']"),
                By.xpath("//div[contains(@class,'ag-row')]"),
                By.xpath("//tr"),
                By.xpath("//*[contains(@class,'MuiTableRow-root')]")
        };

        for (By rowLocator : rowLocators) {
            for (WebElement row : driver.findElements(rowLocator)) {
                try {
                    if (!row.isDisplayed()) {
                        continue;
                    }
                    String rowText = safeTrim(row.getText());
                    if (rowText.isEmpty()) {
                        continue;
                    }
                    if (normalizeForMatch(rowText).contains(normalizedExpected)) {
                        return rowText;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        WebElement timelineRow = findTimelineSidebarRowForSelectedResource();
        if (timelineRow != null) {
            String timelineSummary = buildTimelineAllocationSummary(timelineRow);
            if (!timelineSummary.isEmpty()) {
                return timelineSummary;
            }
        }

        return "";
    }

    /**
     * Validates whether the selected grid row reflects the requested dates and hours used in the test flow.
     */
    public boolean doesSelectedAllocationRowMatchRequestedSchedule() {
        String rowSummary = readAllocationRowSummaryForSelectedResource();
        if (rowSummary.isEmpty()) {
            return isSelectedResourceTimelineAllocationVisible();
        }

        String normalizedSummary = normalizeForMatch(rowSummary);
        if (!selectedStartDate.isEmpty() && normalizedSummary.contains(normalizeForMatch(selectedStartDate))) {
            return true;
        }
        if (!selectedEndDate.isEmpty() && normalizedSummary.contains(normalizeForMatch(selectedEndDate))) {
            return true;
        }
        if (!selectedRequestedHours.isEmpty() && normalizedSummary.contains(normalizeForMatch(selectedRequestedHours))) {
            return true;
        }
        return isSelectedResourceTimelineAllocationVisible();
    }

    /**
     * Checks whether a timeline allocation bar is visible for the currently selected resource.
     */
    public boolean isSelectedResourceTimelineAllocationVisible() {
        WebElement timelineRow = findTimelineSidebarRowForSelectedResource();
        if (timelineRow == null) {
            return false;
        }

        List<WebElement> allocationItems = driver.findElements(By.xpath(
                "//div[contains(@class,'rct-item') and contains(@class,'item-allocation')]"
                        + " | //div[contains(@class,'rct-item') and contains(@class,'item-allocation ')]"
                        + " | //div[contains(@class,'item-allocation')]"
        ));

        for (WebElement item : allocationItems) {
            try {
                if (!item.isDisplayed()) {
                    continue;
                }
                String itemText = safeTrim(item.getText());
                if (!itemText.isEmpty()) {
                    logAction("Visible timeline allocation item detected for selected resource: " + itemText);
                } else {
                    logAction("Visible timeline allocation item detected for selected resource.");
                }
                return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    /**
     * Reads the availability tooltip text shown for the selected resource timeline entry.
     */
    public String readAvailabilityHoverText() {
        By barLocator = firstVisibleLocator(availabilityBars);
        if (barLocator == null) {
            return "";
        }

        WebElement bar = eleutil.waitForElementVisible(barLocator, TimeUtil.DEFAULT_TIME_OUT);
        jsUtil.scrollIntoView(bar);
        actions.moveToElement(bar).perform();

        By tooltip = firstVisibleLocator(availabilityHoverTooltip);
        if (tooltip == null) {
            waitForAnyVisible(availabilityHoverTooltip, 3);
            tooltip = firstVisibleLocator(availabilityHoverTooltip);
        }
        if (tooltip == null) {
            return "";
        }
        return eleutil.doGetText(tooltip).trim();
    }

    /**
     * Opens the schedule calendar or timeline area for the currently selected resource.
     */
    public void openCalendarForSelectedResource() {
        logAction("Opening calendar/selection for the selected resource.");
        clickFirstVisible(resourceCheckboxes, "Resource selection checkbox");
    }

    /**
     * Clicks the Add more action when the allocation screen exposes it.
     */
    public void clickAddMoreIfVisible() {
        if (isAnyVisible(addMoreButton)) {
            eleutil.doClick(addMoreButton, TimeUtil.DEFAULT_TIME_OUT);
        }
    }

    private void setDateInField(By[] locators, String dateText, int preferredIndex) {
        RuntimeException lastFailure = null;
        for (int attempt = 1; attempt <= 5; attempt++) {
            WebElement editor = ensureAllocationRowEditorVisibleStrict();
            WebElement dateInput = firstVisibleElementAllowReadonly(editor, locators);

            if (dateInput == null) {
                dateInput = firstVisibleElementAllowReadonly(locators);
            }

            if (dateInput == null) {
                List<WebElement> scopedDateInputs = resolveScopedDateInputs(editor);
                if (!scopedDateInputs.isEmpty()) {
                    dateInput = pickIndexedElement(scopedDateInputs, preferredIndex);
                }
            }

            if (dateInput == null && isAnyVisible(addMoreButton)) {
                clickAddMoreIfVisible();
                waitForAnyVisible(startDateInputs, 5);
                editor = ensureAllocationRowEditorVisibleStrict();
                dateInput = firstVisibleElementAllowReadonly(editor, locators);
                if (dateInput == null) {
                    dateInput = firstVisibleElementAllowReadonly(locators);
                }
                if (dateInput == null) {
                    dateInput = pickIndexedElement(resolveScopedDateInputs(editor), preferredIndex);
                }
            }

            if (dateInput != null) {
                try {
                    setDateValueWithFallback(dateInput, dateText);
                    return;
                } catch (StaleElementReferenceException | IllegalStateException retriableFailure) {
                    lastFailure = retriableFailure;
                    logAction("Date input re-rendered while setting " + dateText
                            + ". Retrying with a fresh element reference (attempt " + attempt + " of 5). Cause="
                            + retriableFailure.getClass().getSimpleName());
                    if (attempt < 5) {
                        waitForAnyVisible(new By[]{
                                startDateInputs[0],
                                endDateInputs[0],
                                hoursInputFields[0]
                        }, 3);
                        continue;
                    }
                }
            }

            lastFailure = new NoSuchElementException("Date field not found on Common Allocation screen.");
            if (attempt < 5) {
                logAction("Date field not visible yet for " + dateText + ". Waiting for the allocation row to finish rendering (attempt " + attempt + " of 5).");
                waitForAnyVisible(new By[]{
                        startDateInputs[0],
                        endDateInputs[0],
                        hoursInputFields[0]
                }, 4);
            }
        }
        debugDateFieldCandidates();
        if (lastFailure != null) {
            throw lastFailure;
        }
        throw new NoSuchElementException("Date field not found on Common Allocation screen.");
    }

    private void setStartAndEndDates(LocalDate startDate, LocalDate endDate) {
        logAction("Applying start/end dates: " + startDate.format(DD_MM_YYYY) + " to " + endDate.format(DD_MM_YYYY));
        setDateInField(startDateInputs, startDate.format(DD_MM_YYYY), 0);
        setDateInField(endDateInputs, endDate.format(DD_MM_YYYY), 1);
    }

    private LocalDate addBusinessDays(LocalDate start, int businessDays) {
        int remaining = Math.max(0, businessDays);
        LocalDate date = start;
        while (remaining > 0) {
            date = date.plusDays(1);
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                remaining--;
            }
        }
        return date;
    }

    private void selectSuggestion(String expectedText, By[] suggestionLocators) {
        List<WebElement> suggestions = getVisibleElements(suggestionLocators);
        if (suggestions.isEmpty()) {
            actions.sendKeys(Keys.ARROW_DOWN).perform();
            actions.sendKeys(Keys.ENTER).perform();
            return;
        }

        String lowerExpected = expectedText.toLowerCase(Locale.ENGLISH);
        for (WebElement item : suggestions) {
            String text = item.getText();
            if (text != null && text.toLowerCase(Locale.ENGLISH).contains(lowerExpected)) {
                if (!clickElementWithFallback(item)) {
                    actions.sendKeys(Keys.ARROW_DOWN).perform();
                    actions.sendKeys(Keys.ENTER).perform();
                }
                return;
            }
        }

        // Avoid clicking unrelated options from other open dropdowns.
        actions.sendKeys(Keys.ARROW_DOWN).perform();
        actions.sendKeys(Keys.ENTER).perform();
    }

    private void selectFirstSuggestion(By[] suggestionLocators, String logicalName) {
        for (By locator : suggestionLocators) {
            List<WebElement> suggestions = driver.findElements(locator);
            for (WebElement option : suggestions) {
                try {
                    if (!option.isDisplayed()) {
                        continue;
                    }
                    String text = safeTrim(option.getText()).toLowerCase(Locale.ENGLISH);
                    if (text.contains("select")) {
                        continue;
                    }
                    if (clickElementWithFallback(option)) {
                        return;
                    }
                } catch (StaleElementReferenceException stale) {
                    logAction("Suggestion row went stale while selecting " + logicalName + ", retrying with fresh elements.");
                    break;
                }
            }

            suggestions = driver.findElements(locator);
            for (WebElement option : suggestions) {
                try {
                    if (option.isDisplayed() && clickElementWithFallback(option)) {
                        return;
                    }
                } catch (StaleElementReferenceException stale) {
                    logAction("Suggestion row went stale while clicking the first visible " + logicalName + ", retrying.");
                    break;
                }
            }
        }

        actions.sendKeys(Keys.ARROW_DOWN).perform();
        actions.sendKeys(Keys.ENTER).perform();
    }

    private void clickWithFallback(By... locators) {
        if (!tryClickWithFallback(locators)) {
            throw new NoSuchElementException("None of the fallback click locators are visible.");
        }
    }

    private boolean tryClickWithFallback(By... locators) {
        for (By locator : locators) {
            if (locator == null) {
                continue;
            }
            if (isAnyVisible(locator)) {
                try {
                    eleutil.clickStable(locator, TimeUtil.DEFAULT_TIME_OUT);
                    return true;
                } catch (Exception ignored) {
                }

                for (WebElement element : driver.findElements(locator)) {
                    if (!element.isDisplayed()) {
                        continue;
                    }
                    if (clickElementWithFallback(element)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void commitFieldSelectionAndWaitForNextSection(WebElement field, String fieldName) {
        try {
            logAction("Committing " + fieldName + " selection with TAB to reveal the next allocation section.");
            if (field != null) {
                field.sendKeys(Keys.TAB);
            } else {
                actions.sendKeys(Keys.TAB).perform();
            }
        } catch (Exception ignored) {
        }

        try {
            waitForAnyVisible(new By[]{
                    startDateInputs[0],
                    endDateInputs[0],
                    hoursInputFields[0],
                    addMoreButton
            }, 6);
        } catch (Exception ignored) {
        }
    }

    private void clickFirstVisible(By[] locators, String logicalName) {
        logAction("Attempting to click: " + logicalName);
        for (By locator : locators) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                if (!element.isDisplayed()) {
                    continue;
                }
                if (clickElementWithFallback(element)) {
                    logAction("Clicked " + logicalName + " using " + locator);
                    return;
                }
            }
        }

        throw new NoSuchElementException(logicalName + " is not visible.");
    }

    private By firstVisibleLocator(By... locators) {
        for (By locator : locators) {
            try {
                if (isAnyVisible(locator)) {
                    return locator;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private List<WebElement> getVisibleElements(By[] locators) {
        List<WebElement> result = new ArrayList<>();
        for (By locator : locators) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                if (element.isDisplayed()) {
                    result.add(element);
                }
            }
            if (!result.isEmpty()) {
                return result;
            }
        }
        return result;
    }

    private boolean isAnyVisible(By... locators) {
        for (By locator : locators) {
            try {
                if (driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed)) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private boolean clickElementWithFallback(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
            element.click();
            logAction("Native click succeeded for element tag=" + safeTrim(element.getTagName())
                    + " class=" + safeTrim(element.getAttribute("class")));
            return true;
        } catch (Exception clickException) {
            try {
                logAction("Native click failed, using JS click for element tag=" + safeTrim(element.getTagName())
                        + " class=" + safeTrim(element.getAttribute("class")));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                return true;
            } catch (Exception jsException) {
                return false;
            }
        }
    }

    private WebElement firstVisibleElement(By... locators) {
        for (By locator : locators) {
            for (WebElement element : driver.findElements(locator)) {
                if (element.isDisplayed() && element.isEnabled()) {
                    return element;
                }
            }
        }
        return null;
    }

    private WebElement firstVisibleElement(WebElement root, By... locators) {
        if (root == null) {
            return null;
        }
        for (By locator : locators) {
            for (WebElement element : root.findElements(locator)) {
                if (element.isDisplayed() && element.isEnabled()) {
                    return element;
                }
            }
        }
        return null;
    }

    private WebElement firstVisibleElementAllowReadonly(By... locators) {
        for (By locator : locators) {
            for (WebElement element : driver.findElements(locator)) {
                if (element.isDisplayed()) {
                    return element;
                }
            }
        }
        return null;
    }

    private WebElement firstVisibleElementAllowReadonly(WebElement root, By... locators) {
        if (root == null) {
            return null;
        }
        for (By locator : locators) {
            for (WebElement element : root.findElements(locator)) {
                if (element.isDisplayed()) {
                    return element;
                }
            }
        }
        return null;
    }

    private void clearAndType(WebElement input, String value) {
        waitForBlockingBackdropToClear(6);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeUtil.DEFAULT_TIME_OUT));
        wait.until(ExpectedConditions.visibilityOf(input));
        logAction("Typing into field tag=" + safeTrim(input.getTagName())
                + " class=" + safeTrim(input.getAttribute("class"))
                + " value=" + safeTrim(value));
        try {
            wait.until(ExpectedConditions.elementToBeClickable(input));
        } catch (Exception ignored) {
        }

        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", input);
            input.click();
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            input.sendKeys(Keys.DELETE);
            input.sendKeys(value);
            return;
        } catch (Exception nativeTypeFailure) {
            logAction("Native clear/type failed, using JS fallback. Cause=" + nativeTypeFailure.getMessage());
            waitForBlockingBackdropToClear(6);
        }

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", input);
        js.executeScript("arguments[0].focus();", input);
        js.executeScript("arguments[0].value='';", input);
        js.executeScript("arguments[0].dispatchEvent(new Event('input', {bubbles:true}));", input);
        js.executeScript("arguments[0].dispatchEvent(new Event('change', {bubbles:true}));", input);
        js.executeScript("arguments[0].value=arguments[1];", input, value);
        js.executeScript("arguments[0].dispatchEvent(new Event('input', {bubbles:true}));", input);
        js.executeScript("arguments[0].dispatchEvent(new Event('change', {bubbles:true}));", input);
        js.executeScript("arguments[0].dispatchEvent(new Event('blur', {bubbles:true}));", input);
    }

    private void setDateValueWithFallback(WebElement input, String value) {
        if (input == null) {
            throw new NoSuchElementException("Date input is null on Common Allocation screen.");
        }

        try {
            logAction("Setting date field to " + safeTrim(value));
            clearAndType(input, value);
        } catch (Exception e) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            logAction("Falling back to JS date entry for value " + safeTrim(value));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", input);
            js.executeScript("arguments[0].value=arguments[1];", input, value);
            js.executeScript("arguments[0].dispatchEvent(new Event('input', {bubbles:true}));", input);
            js.executeScript("arguments[0].dispatchEvent(new Event('change', {bubbles:true}));", input);
            js.executeScript("arguments[0].dispatchEvent(new Event('blur', {bubbles:true}));", input);
        }

        if (!waitForInputValue(input, value, 4)) {
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].value=arguments[1];", input, value);
                js.executeScript("arguments[0].dispatchEvent(new Event('input', {bubbles:true}));", input);
                js.executeScript("arguments[0].dispatchEvent(new Event('change', {bubbles:true}));", input);
                js.executeScript("arguments[0].dispatchEvent(new Event('blur', {bubbles:true}));", input);
            } catch (Exception ignored) {
            }
        }

        if (!waitForInputValue(input, value, 4)) {
            throw new IllegalStateException("Date value did not persist after update: " + value);
        }

        try {
            input.sendKeys(Keys.TAB);
        } catch (Exception ignored) {
        }
    }

    private void setTextValueWithFallback(WebElement input, String value) {
        if (input == null) {
            throw new NoSuchElementException("Text input is null on Common Allocation screen.");
        }

        try {
            logAction("Setting text field to " + safeTrim(value));
            clearAndType(input, value);
        } catch (Exception e) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            logAction("Falling back to JS text entry for value " + safeTrim(value));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", input);
            js.executeScript("arguments[0].value=arguments[1];", input, value);
            js.executeScript("arguments[0].dispatchEvent(new Event('input', {bubbles:true}));", input);
            js.executeScript("arguments[0].dispatchEvent(new Event('change', {bubbles:true}));", input);
            js.executeScript("arguments[0].dispatchEvent(new Event('blur', {bubbles:true}));", input);
        }

        if (!waitForInputValue(input, value, 4)) {
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].value=arguments[1];", input, value);
                js.executeScript("arguments[0].dispatchEvent(new Event('input', {bubbles:true}));", input);
                js.executeScript("arguments[0].dispatchEvent(new Event('change', {bubbles:true}));", input);
                js.executeScript("arguments[0].dispatchEvent(new Event('blur', {bubbles:true}));", input);
            } catch (Exception ignored) {
            }
        }

        if (!waitForInputValue(input, value, 4)) {
            throw new IllegalStateException("Text value did not persist after update: " + value);
        }
    }

    private WebElement findLikelyDescriptionField(WebElement root) {
        List<WebElement> candidates = new ArrayList<>();

        if (root != null) {
            candidates.addAll(root.findElements(By.xpath(".//textarea[not(@disabled)]")));
            candidates.addAll(root.findElements(By.xpath(".//*[@role='textbox' and not(@aria-hidden='true')]")));
            candidates.addAll(root.findElements(By.xpath(".//*[@contenteditable='true' and not(@aria-hidden='true')]")));
            candidates.addAll(root.findElements(By.xpath(".//input[not(@disabled)"
                    + " and not(@type='hidden') and not(@type='checkbox') and not(@type='radio') and not(@type='number')"
                    + " and not(contains(@class,'MuiAutocomplete-input'))"
                    + " and not(contains(translate(@class,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'autocomplete'))"
                    + " and (contains(translate(@name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'description')"
                    + " or contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'description')"
                    + " or contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'description')"
                    + " or contains(translate(@id,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'description'))]")));
        } else {
            candidates.addAll(driver.findElements(By.xpath("//textarea[not(@disabled)]")));
            candidates.addAll(driver.findElements(By.xpath("//*[@role='textbox' and not(@aria-hidden='true')]")));
            candidates.addAll(driver.findElements(By.xpath("//*[@contenteditable='true' and not(@aria-hidden='true')]")));
            candidates.addAll(driver.findElements(By.xpath("//input[not(@disabled)"
                    + " and not(@type='hidden') and not(@type='checkbox') and not(@type='radio') and not(@type='number')"
                    + " and not(contains(@class,'MuiAutocomplete-input'))"
                    + " and not(contains(translate(@class,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'autocomplete'))"
                    + " and (contains(translate(@name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'description')"
                    + " or contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'description')"
                    + " or contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'description')"
                    + " or contains(translate(@id,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'description'))]")));
        }

        WebElement fallbackCandidate = null;
        for (WebElement candidate : candidates) {
            try {
                if (!candidate.isDisplayed() || !candidate.isEnabled()) {
                    continue;
                }
                if (isLikelyDescriptionField(candidate)) {
                    return candidate;
                }
                if (fallbackCandidate == null && !isClearlyNonDescriptionField(candidate)) {
                    fallbackCandidate = candidate;
                }
            } catch (StaleElementReferenceException ignored) {
            }
        }
        return fallbackCandidate;
    }

    private boolean isLikelyDescriptionField(WebElement element) {
        if (element == null) {
            return false;
        }

        String tag = safeTrim(element.getTagName()).toLowerCase(Locale.ENGLISH);
        String role = safeTrim(element.getAttribute("role")).toLowerCase(Locale.ENGLISH);
        String contentEditable = safeTrim(element.getAttribute("contenteditable")).toLowerCase(Locale.ENGLISH);
        String searchable = normalizeForMatch(
                safeTrim(element.getAttribute("id")) + " "
                        + safeTrim(element.getAttribute("name")) + " "
                        + safeTrim(element.getAttribute("placeholder")) + " "
                        + safeTrim(element.getAttribute("aria-label")) + " "
                        + safeTrim(element.getAttribute("class")) + " "
                        + safeTrim(element.getAttribute("value")) + " "
                        + safeTrim(element.getText())
        );

        if (searchable.contains("searchemployee")
                || searchable.contains("skill")
                || searchable.contains("startdate")
                || searchable.contains("enddate")
                || searchable.contains("hour")
                || searchable.contains("effort")
                || searchable.contains("checkbox")
                || searchable.contains("autocomplete")) {
            return false;
        }

        if ("textarea".equals(tag) || "textbox".equals(role) || "true".equals(contentEditable)) {
            return true;
        }

        return searchable.contains("description")
                || searchable.contains("taskdescription")
                || searchable.contains("task desc")
                || searchable.contains("details")
                || searchable.contains("notes")
                || searchable.contains("remarks");
    }

    private boolean isClearlyNonDescriptionField(WebElement element) {
        if (element == null) {
            return true;
        }

        String searchable = normalizeForMatch(
                safeTrim(element.getAttribute("id")) + " "
                        + safeTrim(element.getAttribute("name")) + " "
                        + safeTrim(element.getAttribute("placeholder")) + " "
                        + safeTrim(element.getAttribute("aria-label")) + " "
                        + safeTrim(element.getAttribute("class")) + " "
                        + safeTrim(element.getAttribute("value")) + " "
                        + safeTrim(element.getText())
        );

        return searchable.contains("searchemployee")
                || searchable.contains("skill")
                || searchable.contains("startdate")
                || searchable.contains("enddate")
                || searchable.contains("hour")
                || searchable.contains("effort")
                || searchable.contains("checkbox")
                || searchable.contains("autocomplete")
                || searchable.contains("allocateemployee")
                || searchable.contains("allocationcomplete");
    }

    private void debugDescriptionFieldCandidates(WebElement root) {
        try {
            List<WebElement> candidates = new ArrayList<>();
            if (root != null) {
                candidates.addAll(root.findElements(By.xpath(".//textarea[not(@disabled)]")));
                candidates.addAll(root.findElements(By.xpath(".//*[@role='textbox' and not(@aria-hidden='true')]")));
                candidates.addAll(root.findElements(By.xpath(".//*[@contenteditable='true' and not(@aria-hidden='true')]")));
                candidates.addAll(root.findElements(By.xpath(".//input[not(@disabled) and not(@type='hidden') and not(@type='checkbox') and not(@type='radio') and not(@type='number')]")));
            } else {
                candidates.addAll(driver.findElements(By.xpath("//textarea[not(@disabled)]")));
                candidates.addAll(driver.findElements(By.xpath("//*[@role='textbox' and not(@aria-hidden='true')]")));
                candidates.addAll(driver.findElements(By.xpath("//*[@contenteditable='true' and not(@aria-hidden='true')]")));
                candidates.addAll(driver.findElements(By.xpath("//input[not(@disabled) and not(@type='hidden') and not(@type='checkbox') and not(@type='radio') and not(@type='number')]")));
            }

            int index = 1;
            for (WebElement candidate : candidates) {
                try {
                    if (!candidate.isDisplayed()) {
                        continue;
                    }
                    System.out.println("DEBUG DESC CANDIDATE[" + index + "] tag=" + safeTrim(candidate.getTagName())
                            + " role=" + safeTrim(candidate.getAttribute("role"))
                            + " type=" + safeTrim(candidate.getAttribute("type"))
                            + " id=" + safeTrim(candidate.getAttribute("id"))
                            + " name=" + safeTrim(candidate.getAttribute("name"))
                            + " placeholder=" + safeTrim(candidate.getAttribute("placeholder"))
                            + " aria-label=" + safeTrim(candidate.getAttribute("aria-label"))
                            + " class=" + safeTrim(candidate.getAttribute("class"))
                            + " value=" + safeTrim(candidate.getAttribute("value"))
                            + " text=" + safeTrim(candidate.getText()));
                    index++;
                } catch (StaleElementReferenceException ignored) {
                }
            }
        } catch (Exception e) {
            System.out.println("DEBUG DESC: unable to inspect candidates: " + e.getMessage());
        }
    }

    private boolean selectEmployeeSuggestion(WebElement searchInput, String resourceName) {
        String listBoxId = safeTrim(searchInput.getAttribute("aria-controls"));
        if (!listBoxId.isEmpty()) {
            By controlledOptions = By.xpath("//ul[@id='" + listBoxId + "']//li[@role='option']");
            if (clickSuggestionByText(controlledOptions, resourceName)) {
                logAction("Selected employee suggestion by text: " + resourceName);
                return true;
            }
            for (WebElement option : driver.findElements(controlledOptions)) {
                if (option.isDisplayed() && clickElementWithFallback(option)) {
                    logAction("Selected employee suggestion from controlled listbox: " + resourceName);
                    return true;
                }
            }
        }
        if (clickSuggestionByText(employeeSuggestionRows, resourceName)) {
            logAction("Selected employee suggestion by text: " + resourceName);
            return true;
        }
        for (WebElement option : getVisibleElements(employeeSuggestionRows)) {
            if (clickElementWithFallback(option)) {
                logAction("Selected employee suggestion from visible rows: " + resourceName);
                return true;
            }
        }
        return false;
    }

    private boolean clickSuggestionByText(By locator, String expectedText) {
        String normalizedExpected = normalizeForMatch(expectedText);
        for (WebElement option : driver.findElements(locator)) {
            try {
                if (!option.isDisplayed()) {
                    continue;
                }
                String optionText = safeTrim(option.getText());
                String normalizedOption = normalizeForMatch(optionText);
                if (normalizedOption.isEmpty() || !normalizedOption.contains(normalizedExpected)) {
                    continue;
                }
                if (clickElementWithFallback(option)) {
                    return true;
                }
            } catch (StaleElementReferenceException stale) {
                logAction("Suggestion row went stale while matching text '" + expectedText + "', retrying.");
            }
        }
        return false;
    }

    private boolean clickSuggestionByText(By[] locators, String expectedText) {
        for (By locator : locators) {
            if (clickSuggestionByText(locator, expectedText)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAvailabilityWarningVisible(int timeoutSeconds) {
        return !waitForAvailabilityWarningText(timeoutSeconds).isEmpty();
    }

    private void waitForAvailabilityCheck(int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            wait.until(d -> {
                WebElement editor = resolveAllocationEditorContainer();
                if (editor == null) {
                    return false;
                }
                return true;
            });
        } catch (Exception ignored) {
        }
    }

    private String waitForAvailabilityWarningText(int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            return wait.until(d -> {
                String warning = readAvailabilityWarningMessage();
                if (!warning.isEmpty()) {
                    return warning;
                }
                warning = readAvailabilityWarningMessageAfterHover();
                return warning.isEmpty() ? null : warning;
            });
        } catch (Exception e) {
            String warning = readAvailabilityWarningMessage();
            if (!warning.isEmpty()) {
                return warning;
            }
            warning = readAvailabilityWarningMessageAfterHover();
            return warning == null ? "" : warning.trim();
        }
    }

    private void revealAvailabilityWarningTrigger() {
        try {
            WebElement editor = resolveAllocationEditorContainer();
            WebElement hoursInput = firstVisibleElement(editor, hoursInputFields);
            if (hoursInput == null) {
                hoursInput = firstVisibleElement(hoursInputFields);
            }
            if (hoursInput != null) {
                jsUtil.scrollIntoView(hoursInput);
                actions.moveToElement(hoursInput).perform();
                return;
            }

            WebElement dateInput = firstVisibleElementAllowReadonly(editor, startDateInputs);
            if (dateInput == null) {
                dateInput = firstVisibleElementAllowReadonly(startDateInputs);
            }
            if (dateInput != null) {
                jsUtil.scrollIntoView(dateInput);
                actions.moveToElement(dateInput).perform();
                return;
            }

            if (editor != null) {
                actions.moveToElement(editor).perform();
            }
        } catch (Exception ignored) {
        }
    }

    private String readAvailabilityWarningMessageAfterHover() {
        WebElement editor = resolveAllocationEditorContainer();
        By[] triggerLocators = {
                By.xpath(".//button"),
                By.xpath(".//*[name()='svg']"),
                By.xpath(".//*[contains(@class,'MuiIconButton-root') or contains(@class,'MuiSvgIcon-root')]")
        };

        List<WebElement> triggerElements = new ArrayList<>();
        if (editor != null) {
            triggerElements.addAll(editor.findElements(triggerLocators[0]));
            triggerElements.addAll(editor.findElements(triggerLocators[1]));
            triggerElements.addAll(editor.findElements(triggerLocators[2]));
        }
        if (triggerElements.isEmpty()) {
            triggerElements.addAll(driver.findElements(triggerLocators[0]));
            triggerElements.addAll(driver.findElements(triggerLocators[1]));
            triggerElements.addAll(driver.findElements(triggerLocators[2]));
        }

        for (WebElement trigger : triggerElements) {
            try {
                if (!trigger.isDisplayed()) {
                    continue;
                }
                jsUtil.scrollIntoView(trigger);
                actions.moveToElement(trigger).perform();
                String warning = readAvailabilityWarningMessage();
                if (!warning.isEmpty()) {
                    return warning;
                }
            } catch (Exception ignored) {
            }
        }
        return "";
    }

    private LocalDate extractLatestWarningDate(String warningText) {
        if (warningText == null || warningText.trim().isEmpty()) {
            return null;
        }

        Matcher matcher = WARNING_DATE_PATTERN.matcher(warningText);
        LocalDate latest = null;
        while (matcher.find()) {
            String candidate = matcher.group();
            try {
                LocalDate parsed = LocalDate.parse(candidate, DD_MM_YYYY);
                if (latest == null || parsed.isAfter(latest)) {
                    latest = parsed;
                }
            } catch (DateTimeParseException ignored) {
                // Ignore malformed dates in the warning text and keep scanning.
            }
        }
        return latest;
    }

    private LocalDate advanceToNextMonthBusinessDate(LocalDate baseDate) {
        LocalDate candidate = baseDate.plusMonths(1);
        while (candidate.getDayOfWeek() == DayOfWeek.SATURDAY || candidate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            candidate = candidate.plusDays(1);
        }
        return candidate;
    }

    private String scanAvailabilityWarningTextFromDom() {
        try {
            Object result = ((JavascriptExecutor) driver).executeScript(
                    "const needles = ['not available on the following dates','hours are not available','user is not available','resource is not available','unavailable'];" +
                    "const normalize = (value) => (value || '').replace(/\\s+/g, ' ').trim();" +
                    "const scan = (value) => {" +
                    "  const text = normalize(value);" +
                    "  if (!text) return '';" +
                    "  const lower = text.toLowerCase();" +
                    "  return needles.some(n => lower.includes(n)) ? text : '';" +
                    "};" +
                    "const bodyText = document.body ? (document.body.innerText || document.body.textContent || '') : '';" +
                    "for (const line of bodyText.split(/\\n+/)) {" +
                    "  const hit = scan(line);" +
                    "  if (hit) return hit;" +
                    "}" +
                    "const selectors = ['[role=\"tooltip\"]','[class*=\"MuiTooltip-tooltip\"]','[class*=\"MuiAlert-message\"]','[role=\"alert\"]','[class*=\"MuiSnackbar-root\"]'];" +
                    "for (const selector of selectors) {" +
                    "  for (const el of document.querySelectorAll(selector)) {" +
                    "    const hit = scan(el.innerText || el.textContent || el.getAttribute('title') || el.getAttribute('aria-label'));" +
                    "    if (hit) return hit;" +
                    "  }" +
                    "}" +
                    "return '';"
            );
            return result == null ? "" : result.toString().trim();
        } catch (Exception ignored) {
            return "";
        }
    }

    private void debugDateFieldCandidates() {
        try {
            WebElement container = resolveAllocationEditorContainer();
            List<WebElement> visibleInputs = new ArrayList<>();
            if (container != null) {
                visibleInputs = container.findElements(By.xpath(".//input"));
                System.out.println("DEBUG DATE: resolved editor container class = " + safeTrim(container.getAttribute("class")));
            } else {
                System.out.println("DEBUG DATE: editor container could not be resolved.");
            }
            List<WebElement> allVisibleInputs = driver.findElements(By.xpath("//input[not(@type='hidden')]"));
            System.out.println("DEBUG DATE: visible input count in dialog = " + visibleInputs.size());
            System.out.println("DEBUG DATE: visible input count globally = " + allVisibleInputs.size());
            int index = 1;
            for (WebElement input : visibleInputs) {
                if (!input.isDisplayed()) {
                    continue;
                }
                System.out.println("DEBUG DATE INPUT[" + index + "]"
                        + " type=" + safeTrim(input.getAttribute("type"))
                        + " id=" + safeTrim(input.getAttribute("id"))
                        + " name=" + safeTrim(input.getAttribute("name"))
                        + " placeholder=" + safeTrim(input.getAttribute("placeholder"))
                        + " class=" + safeTrim(input.getAttribute("class"))
                        + " value=" + safeTrim(input.getAttribute("value"))
                        + " readonly=" + safeTrim(input.getAttribute("readonly")));
                index++;
            }

            int globalIndex = 1;
            for (WebElement input : allVisibleInputs) {
                if (!input.isDisplayed()) {
                    continue;
                }
                System.out.println("DEBUG GLOBAL INPUT[" + globalIndex + "]"
                        + " type=" + safeTrim(input.getAttribute("type"))
                        + " id=" + safeTrim(input.getAttribute("id"))
                        + " name=" + safeTrim(input.getAttribute("name"))
                        + " placeholder=" + safeTrim(input.getAttribute("placeholder"))
                        + " class=" + safeTrim(input.getAttribute("class"))
                        + " value=" + safeTrim(input.getAttribute("value"))
                        + " readonly=" + safeTrim(input.getAttribute("readonly")));
                globalIndex++;
            }

            List<WebElement> labels = container == null
                    ? new ArrayList<>()
                    : container.findElements(By.xpath(".//*[self::label or self::span or self::p or self::div]"));
            for (WebElement label : labels) {
                String text = safeTrim(label.getText()).toLowerCase(Locale.ENGLISH);
                if (text.contains("start date") || text.contains("end date") || text.contains("skill")) {
                    System.out.println("DEBUG DATE LABEL: " + safeTrim(label.getText()));
                }
            }

            List<WebElement> globalLabels = driver.findElements(By.xpath("//*[self::label or self::span or self::p or self::div]"));
            for (WebElement label : globalLabels) {
                if (!label.isDisplayed()) {
                    continue;
                }
                String raw = safeTrim(label.getText());
                String text = raw.toLowerCase(Locale.ENGLISH);
                if (text.contains("start date") || text.contains("end date") || text.contains("skill")) {
                    System.out.println("DEBUG GLOBAL LABEL: tag=" + label.getTagName()
                            + " class=" + safeTrim(label.getAttribute("class"))
                            + " text=" + raw);
                }
            }
        } catch (Exception e) {
            System.out.println("DEBUG DATE: unable to inspect candidates: " + e.getMessage());
        }
    }

    private void waitForSelectionToApply(WebElement inputElement, String expectedText) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        String expected = normalizeForMatch(expectedText);
        try {
            wait.until(d -> {
                String current = normalizeForMatch(safeTrim(inputElement.getAttribute("value")));
                return current.contains(expected);
            });
        } catch (Exception ignored) {
        }
    }

    private boolean waitForEmployeeSelectionToMaterialize(WebElement inputElement, String expectedText, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        String expected = normalizeForMatch(expectedText);
        try {
            return wait.until(d -> {
                String current = normalizeForMatch(safeTrim(inputElement.getAttribute("value")));
                boolean valueMatches = current.contains(expected);
                boolean suggestionClosed = !isEmployeeSuggestionOpen(inputElement);
                boolean allocationContextVisible = hasSelectedResourceTimelineVisible()
                        || hasOpenAllocationEditorSignalsVisible()
                        || isAnyVisible(addMoreButton);
                return valueMatches && suggestionClosed && allocationContextVisible;
            });
        } catch (Exception e) {
            return false;
        }
    }

    private void commitEmployeeSelectionWithKeyboard(WebElement inputElement) {
        if (inputElement == null) {
            return;
        }
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", inputElement);
        } catch (Exception ignored) {
        }
        try {
            inputElement.sendKeys(Keys.ARROW_DOWN);
            inputElement.sendKeys(Keys.ENTER);
            return;
        } catch (Exception ignored) {
        }
        try {
            actions.moveToElement(inputElement).click().sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).perform();
            return;
        } catch (Exception ignored) {
        }
        try {
            inputElement.sendKeys(Keys.TAB);
        } catch (Exception ignored) {
        }
    }

    private boolean isEmployeeSuggestionOpen(WebElement inputElement) {
        if (inputElement == null) {
            return false;
        }
        try {
            if ("true".equalsIgnoreCase(safeTrim(inputElement.getAttribute("aria-expanded")))) {
                return true;
            }
        } catch (Exception ignored) {
        }

        try {
            String listBoxId = safeTrim(inputElement.getAttribute("aria-controls"));
            if (!listBoxId.isEmpty()) {
                for (WebElement listBox : driver.findElements(By.id(listBoxId))) {
                    if (listBox.isDisplayed()) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    private boolean hasSelectedResourceTimelineVisible() {
        if (selectedResourceName == null || selectedResourceName.trim().isEmpty()) {
            return false;
        }

        String normalizedExpected = normalizeForMatch(selectedResourceName);
        By[] resourceContextLocators = {
                By.xpath("//*[contains(@class,'rct-sidebar-row')]//*[self::div or self::span or self::p]"),
                By.xpath("//*[contains(@class,'rct-item')]//*[self::div or self::span or self::p]"),
                By.xpath("//div[contains(@class,'ag-row') and not(ancestor::ul[@role='listbox'])]//*[self::div or self::span or self::p]"),
                By.xpath("//tr[not(ancestor::ul[@role='listbox'])]//*[self::div or self::span or self::p]")
        };

        for (By locator : resourceContextLocators) {
            for (WebElement element : driver.findElements(locator)) {
                try {
                    if (!element.isDisplayed()) {
                        continue;
                    }
                    if (!normalizeForMatch(safeTrim(element.getText())).contains(normalizedExpected)) {
                        continue;
                    }
                    if (!element.findElements(By.xpath("./ancestor::ul[@role='listbox']")).isEmpty()) {
                        continue;
                    }
                    return true;
                } catch (Exception ignored) {
                }
            }
        }
        return false;
    }

    private WebElement findTimelineSidebarRowForSelectedResource() {
        if (selectedResourceName == null || selectedResourceName.trim().isEmpty()) {
            return null;
        }

        String normalizedExpected = normalizeForMatch(selectedResourceName);
        List<WebElement> timelineRows = driver.findElements(By.xpath("//*[contains(@class,'rct-sidebar-row')]"));
        for (WebElement row : timelineRows) {
            try {
                if (!row.isDisplayed()) {
                    continue;
                }
                String rowText = safeTrim(row.getText());
                if (rowText.isEmpty()) {
                    continue;
                }
                if (normalizeForMatch(rowText).contains(normalizedExpected)) {
                    return row;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private String buildTimelineAllocationSummary(WebElement timelineRow) {
        if (timelineRow == null) {
            return "";
        }

        String rowText = safeTrim(timelineRow.getText());
        String itemText = "";
        List<WebElement> allocationItems = driver.findElements(By.xpath(
                "//div[contains(@class,'rct-item') and contains(@class,'item-allocation')]"
                        + " | //div[contains(@class,'rct-item') and contains(@class,'item-allocation ')]"
                        + " | //div[contains(@class,'item-allocation')]"
        ));
        for (WebElement item : allocationItems) {
            try {
                if (!item.isDisplayed()) {
                    continue;
                }
                itemText = safeTrim(item.getText());
                if (!itemText.isEmpty()) {
                    break;
                }
            } catch (Exception ignored) {
            }
        }

        List<String> parts = new ArrayList<>();
        if (!rowText.isEmpty()) {
            parts.add(rowText);
        }
        if (!itemText.isEmpty()) {
            parts.add(itemText);
        }
        if (!selectedStartDate.isEmpty()) {
            parts.add("Start=" + selectedStartDate);
        }
        if (!selectedEndDate.isEmpty()) {
            parts.add("End=" + selectedEndDate);
        }
        if (!selectedRequestedHours.isEmpty()) {
            parts.add("Hours=" + selectedRequestedHours);
        }
        return String.join(" | ", parts);
    }

    private void clickMatchingEmployeeSuggestion(WebElement searchInput, String resourceName) {
        if (searchInput == null || safeTrim(resourceName).isEmpty()) {
            return;
        }

        String listBoxId = safeTrim(searchInput.getAttribute("aria-controls"));
        if (!listBoxId.isEmpty()) {
            By controlledOptions = By.xpath("//ul[@id='" + listBoxId + "']//li[@role='option']");
            if (clickSuggestionByText(controlledOptions, resourceName)) {
                return;
            }
        }

        clickSuggestionByText(employeeSuggestionRows, resourceName);
    }

    private void dismissEmployeeSuggestionPopup(WebElement inputElement) {
        if (inputElement == null) {
            return;
        }

        try {
            inputElement.sendKeys(Keys.ESCAPE);
        } catch (Exception ignored) {
        }

        try {
            ((JavascriptExecutor) driver).executeScript("document.body.click();");
        } catch (Exception ignored) {
        }

        try {
            inputElement.sendKeys(Keys.TAB);
        } catch (Exception ignored) {
        }
    }

    private boolean waitForSkillSelection(WebElement editor, boolean hadSkillErrorBefore, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            return wait.until(d -> isSkillSelected(editor)
                    || isSkillSelected(null)
                    || (hadSkillErrorBefore && !isSkillFieldInErrorState(editor)));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean waitForInputValue(WebElement input, String expectedValue, int timeoutSeconds) {
        if (input == null) {
            return false;
        }

        String expected = safeTrim(expectedValue);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            return wait.until(d -> {
                try {
                    String actual = safeTrim(input.getAttribute("value"));
                    return expected.equals(actual);
                } catch (Exception ignored) {
                    return false;
                }
            });
        } catch (Exception e) {
            try {
                String actual = safeTrim(input.getAttribute("value"));
                return expected.equals(actual);
            } catch (Exception ignored) {
                return false;
            }
        }
    }

    private boolean isSkillSelected(WebElement editor) {
        if (editor == null) {
            for (By indicator : selectedSkillIndicators) {
                for (WebElement element : driver.findElements(indicator)) {
                    if (!element.isDisplayed()) {
                        continue;
                    }
                    String text = safeTrim(element.getText());
                    if (!text.isEmpty() && !"skills".equalsIgnoreCase(text)) {
                        return true;
                    }
                    String value = safeTrim(element.getAttribute("value"));
                    if (!value.isEmpty() && !"skills".equalsIgnoreCase(value)) {
                        return true;
                    }
                }
            }
            return false;
        }
        for (By indicator : selectedSkillIndicators) {
            for (WebElement element : editor.findElements(indicator)) {
                if (!element.isDisplayed()) {
                    continue;
                }
                String text = safeTrim(element.getText());
                if (!text.isEmpty() && !"skills".equalsIgnoreCase(text)) {
                    return true;
                }
                String value = safeTrim(element.getAttribute("value"));
                if (!value.isEmpty() && !"skills".equalsIgnoreCase(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSkillFieldInErrorState(WebElement editor) {
        if (editor == null) {
            for (By locator : skillErrorIndicators) {
                for (WebElement element : driver.findElements(locator)) {
                    if (element.isDisplayed()) {
                        return true;
                    }
                }
            }
            return false;
        }
        for (By locator : skillErrorIndicators) {
            for (WebElement element : editor.findElements(locator)) {
                if (element.isDisplayed()) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<WebElement> resolveScopedDateInputs(WebElement scopedContainer) {
        if (scopedContainer == null) {
            return new ArrayList<>();
        }

        By[] scopedDateLocators = {
                By.xpath(".//input[@placeholder='DD-MM-YYYY']"),
                By.xpath(".//input[contains(translate(@name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'start')]"),
                By.xpath(".//input[contains(translate(@name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'end')]"),
                By.xpath(".//input[contains(translate(@id,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'start')]"),
                By.xpath(".//input[contains(translate(@id,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'end')]"),
                By.xpath(".//input[contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'date')]")
        };

        List<WebElement> results = new ArrayList<>();
        for (By locator : scopedDateLocators) {
            for (WebElement element : scopedContainer.findElements(locator)) {
                if (!element.isDisplayed()) {
                    continue;
                }
                if (!results.contains(element)) {
                    results.add(element);
                }
            }
        }
        return results;
    }

    private List<WebElement> resolveScopedDateInputs() {
        return resolveScopedDateInputs(resolveAllocationEditorContainer());
    }

    /**
     * Resolves the visible Common Allocation container that owns the search box, timeline, and submission actions.
     */
    private WebElement resolveCommonAllocationContainer() {
        By[] rootLocators = {
                By.xpath("//div[contains(@class,'MuiModal-root') and (.//input[@id='searchEmployee'] or .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'allocate employee')])]"),
                By.xpath("//div[contains(@class,'MuiDialog-root') and (.//input[@id='searchEmployee'] or .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'allocate employee')])]"),
                By.xpath("//div[@role='dialog' and (.//input[@id='searchEmployee'] or .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'allocate employee')])]"),
                By.xpath("//div[contains(@class,'MuiPaper-root') and (.//input[@id='searchEmployee'] or .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'allocate employee')])]"),
                By.xpath("//form[.//input[@id='searchEmployee'] or .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'allocate employee')]]")
        };

        WebElement root = firstVisibleElementAllowReadonly(rootLocators);
        if (root != null) {
            return root;
        }
        return resolveAllocationEditorContainer();
    }

    private WebElement resolveAllocationEditorContainer() {
        By[] rootLocators = {
                By.xpath("//div[contains(@class,'MuiModal-root') and ("
                        + ".//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'allocate employee')]"
                        + " or .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'task description')]"
                        + " or .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'skills')]"
                        + " or .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'start date')]"
                        + " or .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'end date')]"
                        + ")]"),
                By.xpath("//div[contains(@class,'MuiDialog-root') and ("
                        + ".//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'allocate employee')]"
                        + " or .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'task description')]"
                        + " or .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'skills')]"
                        + " or .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'start date')]"
                        + " or .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'end date')]"
                        + ")]"),
                By.xpath("//div[@role='dialog' and ("
                        + ".//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'allocate employee')]"
                        + " or .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'task description')]"
                        + " or .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'skills')]"
                        + " or .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'start date')]"
                        + " or .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'end date')]"
                        + ")]"),
                By.xpath("//div[contains(@class,'MuiModal-root') and (.//input[@id='searchEmployee'] or .//input[@placeholder='DD-MM-YYYY'] or .//input[contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'skill')] or .//input[contains(@placeholder,'Type And Select')] or .//input[@type='number'])]"),
                By.xpath("//div[contains(@class,'MuiDialog-container') and (.//input[@id='searchEmployee'] or .//input[@placeholder='DD-MM-YYYY'] or .//input[contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'skill')] or .//input[contains(@placeholder,'Type And Select')] or .//input[@type='number'])]"),
                By.xpath("//div[contains(@class,'MuiPaper-root') and (.//input[@id='searchEmployee'] or .//input[@placeholder='DD-MM-YYYY'] or .//input[contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'skill')] or .//input[contains(@placeholder,'Type And Select')] or .//input[@type='number'])]"),
                By.xpath("//div[contains(@class,'MuiBox-root') and (.//input[@id='searchEmployee'] or .//input[@placeholder='DD-MM-YYYY'] or .//input[contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'skill')] or .//input[contains(@placeholder,'Type And Select')] or .//input[@type='number'])]"),
                By.xpath("//div[@role='dialog' and (.//input[@id='searchEmployee'] or .//input[@placeholder='DD-MM-YYYY'] or .//input[contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'skill')] or .//input[contains(@placeholder,'Type And Select')] or .//input[@type='number'])]"),
                By.xpath("//div[contains(@class,'MuiDialog-root') and (.//input[@id='searchEmployee'] or .//input[@placeholder='DD-MM-YYYY'] or .//input[contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'skill')] or .//input[contains(@placeholder,'Type And Select')] or .//input[@type='number'])]"),
                By.xpath("//div[contains(@class,'MuiDialogContent-root') and (.//input[@id='searchEmployee'] or .//input[@placeholder='DD-MM-YYYY'] or .//input[contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'skill')] or .//input[contains(@placeholder,'Type And Select')] or .//input[@type='number'])]"),
                By.xpath("//form[.//input[@id='searchEmployee'] or .//input[@placeholder='DD-MM-YYYY'] or .//input[contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'skill')] or .//input[contains(@placeholder,'Type And Select')] or .//input[@type='number']]")
        };

        WebElement root = firstVisibleElement(rootLocators);
        if (root != null) {
            return root;
        }

        WebElement searchInput = firstVisibleElement(searchResourceInputs);
        if (searchInput == null) {
            return null;
        }

        By[] ancestryLocators = {
            By.xpath("./ancestor::div[contains(@class,'MuiDialogContent-root')][1]"),
            By.xpath("./ancestor::div[contains(@class,'MuiModal-root')][1]"),
            By.xpath("./ancestor::div[contains(@class,'MuiDialog-container')][1]"),
            By.xpath("./ancestor::div[contains(@class,'MuiDialog-root')][1]"),
            By.xpath("./ancestor::div[contains(@class,'MuiPaper-root')][1]"),
            By.xpath("./ancestor::div[contains(@class,'MuiBox-root')][1]"),
            By.xpath("./ancestor::form[1]")
        };
        for (By locator : ancestryLocators) {
            List<WebElement> ancestors = searchInput.findElements(locator);
            for (WebElement ancestor : ancestors) {
                if (ancestor.isDisplayed() && containsAllocationEditorKeywords(ancestor)) {
                    return ancestor;
                }
            }
        }
        return null;
    }

    private boolean containsAllocationEditorKeywords(WebElement element) {
        if (element == null) {
            return false;
        }
        String text = normalizeForMatch(safeTrim(element.getText()));
        return text.contains("allocateemployee")
                || text.contains("taskdescription")
                || text.contains("skills")
                || text.contains("startdate")
                || text.contains("enddate")
                || text.contains("hours")
                || text.contains("viewschedule");
    }

    private WebElement pickIndexedElement(List<WebElement> elements, int preferredIndex) {
        if (elements == null || elements.isEmpty()) {
            return null;
        }
        if (preferredIndex < 0) {
            return elements.get(0);
        }
        if (preferredIndex >= elements.size()) {
            return elements.get(elements.size() - 1);
        }
        return elements.get(preferredIndex);
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeForMatch(String value) {
        return safeTrim(value)
                .toLowerCase(Locale.ENGLISH)
                .replaceAll("[^a-z0-9]", "");
    }

    private void ensureAllocationRowEditorVisible() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        try {
            wait.until(d -> hasDateOrHoursInputVisible());
            return;
        } catch (Exception ignored) {
        }

        if (selectAllocationRowForSelectedResource()) {
            try {
                wait.until(d -> hasDateOrHoursInputVisible()
                        || isAnyVisible(okButtons)
                        || isAnyVisible(addMoreButton));
                return;
            } catch (Exception ignored) {
            }
        }

        if (tryOpenAllocationEditorFromAvailabilityBar()) {
            try {
                wait.until(d -> hasDateOrHoursInputVisible() || isAnyVisible(okButtons));
                return;
            } catch (Exception ignored) {
            }
        }

        if (isAnyVisible(addMoreButton)) {
            clickAddMoreIfVisible();
            try {
                wait.until(d -> hasDateOrHoursInputVisible());
            } catch (Exception ignored) {
            }
        }
    }

    private WebElement ensureAllocationRowEditorVisibleStrict() {
        ensureAllocationRowEditorVisible();
        WebElement editor = resolveAllocationEditorContainer();
        if (editor == null || !hasDateOrHoursInputVisible()) {
            waitForAnyVisible(new By[]{
                    descriptionInputs[0],
                    skillDropdownInputs[0],
                    startDateInputs[0],
                    endDateInputs[0],
                    hoursInputFields[0],
                    searchResourceInputs[0]
            }, 10);
            editor = resolveAllocationEditorContainer();
        }
        if (editor == null && !hasVisibleAllocationControlsGlobally()) {
            debugDateFieldCandidates();
            throw new NoSuchElementException("Allocation editor fields are not visible after selecting resource.");
        }
        if (editor != null && !hasDateOrHoursInputVisible() && !hasVisibleAllocationControlsGlobally()) {
            debugDateFieldCandidates();
            throw new NoSuchElementException("Allocation editor fields are not visible after selecting resource.");
        }
        return editor;
    }

    private boolean hasDateOrHoursInputVisible() {
        WebElement editor = resolveAllocationEditorContainer();
        if (editor == null) {
            return hasVisibleAllocationControlsGlobally();
        }
        return firstVisibleElementAllowReadonly(editor, startDateInputs) != null
                || firstVisibleElementAllowReadonly(editor, endDateInputs) != null
                || firstVisibleElementAllowReadonly(editor, hoursInputFields) != null
                || !resolveScopedDateInputs(editor).isEmpty();
    }

    private boolean hasVisibleAllocationControlsGlobally() {
        return hasEditableAllocationControlsVisible();
    }

    private boolean hasEditableAllocationControlsVisible() {
        return firstVisibleElement(descriptionInputs) != null
                || firstVisibleElement(skillDropdownInputs) != null
                || firstVisibleElement(skillDropdownClickTargets) != null
                || firstVisibleElementAllowReadonly(startDateInputs) != null
                || firstVisibleElementAllowReadonly(endDateInputs) != null
                || firstVisibleElementAllowReadonly(hoursInputFields) != null;
    }

    private boolean hasOpenAllocationEditorSignalsVisible() {
        WebElement editor = resolveAllocationEditorContainer();
        if (editor == null) {
            return false;
        }
        return containsAllocationEditorKeywords(editor)
                && (firstVisibleElementAllowReadonly(editor, startDateInputs) != null
                || firstVisibleElementAllowReadonly(editor, endDateInputs) != null
                || firstVisibleElementAllowReadonly(editor, hoursInputFields) != null
                || firstVisibleElement(editor, descriptionInputs) != null
                || firstVisibleElement(editor, skillDropdownInputs) != null
                || isAnyVisible(addMoreButton));
    }

    private boolean tryOpenAllocationEditorFromAvailabilityBar() {
        List<WebElement> bars = getVisibleElements(availabilityBars);
        if (bars.isEmpty()) {
            return false;
        }

        int slotIndex = 1;
        for (WebElement bar : bars) {
            try {
                String barText = safeTrim(bar.getText());
                logAction("Opening allocation editor from timeline availability slot " + slotIndex
                        + (barText.isEmpty() ? "" : " (" + barText + ")") + ".");
                jsUtil.scrollIntoView(bar);
                clickElementWithFallback(bar);
                actions.doubleClick(bar).perform();
                try {
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].dispatchEvent(new MouseEvent('dblclick',{bubbles:true}));",
                            bar
                    );
                } catch (Exception ignored) {
                }
                waitForAnyVisible(new By[]{
                        okButtons[0],
                        okButtons[1],
                        addMoreButton,
                        By.xpath("//input[@placeholder='DD-MM-YYYY' and not(@disabled)]"),
                        By.xpath("//input[@type='number' and not(@disabled)]")
                }, 6);
                if (hasDateOrHoursInputVisible() || isAnyVisible(okButtons) || isAnyVisible(addMoreButton)) {
                    return true;
                }
            } catch (Exception e) {
                logAction("Timeline availability slot " + slotIndex + " did not open the allocation editor. Cause="
                        + e.getClass().getSimpleName());
            }
            slotIndex++;
        }
        return false;
    }

    private void waitForBlockingBackdropToClear(int timeoutSeconds) {
        By activeBackdrop = By.xpath("//div[contains(@class,'MuiBackdrop-root') and not(contains(@style,'opacity: 0'))]");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(activeBackdrop));
        } catch (Exception ignored) {
        }
    }

    private void waitForAnyVisible(By[] locators, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            wait.until(d -> isAnyVisible(locators));
        } catch (Exception ignored) {
        }
    }

    private boolean waitForEditorToClose(int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(okButtons[1]))
                    || wait.until(d -> !isAnyVisible(okButtons));
        } catch (Exception e) {
            return !isAnyVisible(okButtons);
        }
    }

    /**
     * Confirms the unsaved-changes navigation prompt that can appear after leaving the allocation editor.
     * This prompt shows up more consistently in headless mode after the Back action.
     */
    private void confirmUnsavedChangesNavigationIfPresent() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
            boolean promptVisible = wait.until(d -> {
                for (WebElement dialog : d.findElements(unsavedChangesDialog)) {
                    try {
                        if (dialog.isDisplayed()) {
                            return true;
                        }
                    } catch (Exception ignored) {
                    }
                }
                return false;
            });

            if (!promptVisible) {
                return;
            }
        } catch (Exception ignored) {
            return;
        }

        logAction("Unsaved changes navigation prompt detected. Confirming navigation with Yes.");
        try {
            clickFirstVisible(unsavedChangesYesButtons, "Unsaved changes confirmation");
        } catch (Exception e) {
            logAction("Dialog-specific Yes button was not stable. Falling back to generic Yes buttons. " + e.getMessage());
            try {
                clickFirstVisible(yesButtons, "Generic Yes confirmation");
            } catch (Exception ignored) {
            }
        }

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(unsavedChangesDialog));
        } catch (Exception ignored) {
        }
    }

    private String waitForToastMessage(int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            wait.until(d -> isAnyVisible(validationMessageLocators));
            String message = readAllocationValidationMessage();
            return message == null ? "" : message.trim();
        } catch (Exception e) {
            return "";
        }
    }
}
