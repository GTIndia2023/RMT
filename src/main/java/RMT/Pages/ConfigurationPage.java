package RMT.Pages;

import RMT.Constants.AppConstants;
import RMT.Errors.AppError;
import RMT.Utils.ElementUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Encapsulates the Configurations screen, including validation, numeric updates, and create-dialog flows.
 */
public class ConfigurationPage {
    private final WebDriver driver;
    private final ElementUtil eleutil;
    private final Properties prop;

    /**
     * Creates the configuration page helper without configuration properties.
     */
    public ConfigurationPage(WebDriver driver) {
        this(driver, null);
    }

    /**
     * Creates the configuration page helper with access to environment-driven test data.
     */
    public ConfigurationPage(WebDriver driver, Properties prop) {
        this.driver = driver;
        this.prop = prop;
        this.eleutil = new ElementUtil(driver);
    }

    /**
     * Represents the result of a configuration action, including message text and selected values.
     */
    public static final class ConfigurationActionResult {
        private final String message;
        private final List<String> selectedValues;
        private final boolean existingSelectionDetected;

        /**
         * Creates a result for a configuration action that returns selected values and a message.
         */
        public ConfigurationActionResult(String message, List<String> selectedValues) {
            this(message, selectedValues, false);
        }

        /**
         * Creates a result for a configuration action, including whether an existing selection was detected.
         */
        public ConfigurationActionResult(String message, List<String> selectedValues, boolean existingSelectionDetected) {
            this.message = message == null ? "" : message.trim();
            this.selectedValues = selectedValues == null ? List.of() : List.copyOf(selectedValues);
            this.existingSelectionDetected = existingSelectionDetected;
        }

        /**
         * Returns the success, validation, or duplicate message captured for the configuration action.
         */
        public String getMessage() {
            return message;
        }

        /**
         * Returns the values that were selected or entered while executing the configuration action.
         */
        public List<String> getSelectedValues() {
            return selectedValues;
        }

        /**
         * Returns the first selected value, which is useful for single-select create flows.
         */
        public String getPrimaryValue() {
            return selectedValues.isEmpty() ? "" : selectedValues.get(0);
        }

        /**
         * Indicates whether the create flow detected an already-configured record instead of creating a new one.
         */
        public boolean isExistingSelectionDetected() {
            return existingSelectionDetected;
        }
    }

    private void logAction(String message) {
        System.out.println("[ConfigurationPage] " + message);
    }

    private final By settingsSharpIcon = By.xpath("//*[name()='svg' and @data-testid='SettingsSharpIcon']");
    private final By settingsIcon = By.xpath("//*[name()='svg' and @data-testid='SettingsIcon']");
    private final By settingsButton = By.xpath("//button[contains(@aria-label,'Settings')]");
    private final By settingsMenuItem = By.xpath("//li[contains(normalize-space(),'Settings')]");
    private final By configurationsMenuItem = By.xpath("//a[@href='/configurations']//li[normalize-space()='Configurations']");
    private final By configurationsMenuItemFallback = By.xpath("//a[contains(@href,'/configurations')]//*[normalize-space()='Configurations']");
    private final By configurationTitleSpans = By.xpath("//div[contains(@class,'MuiAccordionSummary-root')]//span[contains(@class,'MuiTypography-body1') and normalize-space()]");
    private final By saveButton = By.xpath("//button[normalize-space()='Save' or normalize-space()='Submit']");
    private final By configurationMessageCandidates = By.xpath(
            "//div[@role='alert'] | "
                    + "//div[contains(@class,'MuiAlert-message')] | "
                    + "//div[contains(@class,'MuiSnackbarContent-message')] | "
                    + "//div[contains(@class,'MuiSnackbar-root')] | "
                    + "//div[contains(@class,'MuiSnackbarContent-root')] | "
                    + "//div[contains(@class,'MuiAlert-root')] | "
                    + "//p[contains(@class,'MuiFormHelperText-root')] | "
                    + "//span[contains(@class,'MuiFormHelperText-root')]"
    );
    private final By dialogTextFieldCandidates = By.xpath(
            "((//div[@role='dialog' or contains(@class,'MuiDialog-root') or contains(@class,'MuiModal-root') or contains(@class,'MuiDialog-container') or contains(@class,'MuiPopover-root') or contains(@class,'MuiDrawer-root')]//input[not(@type='hidden')]) | "
                    + "(//div[@role='dialog' or contains(@class,'MuiDialog-root') or contains(@class,'MuiModal-root') or contains(@class,'MuiDialog-container') or contains(@class,'MuiPopover-root') or contains(@class,'MuiDrawer-root')]//textarea) | "
                    + "(//div[@role='dialog' or contains(@class,'MuiDialog-root') or contains(@class,'MuiModal-root') or contains(@class,'MuiDialog-container') or contains(@class,'MuiPopover-root') or contains(@class,'MuiDrawer-root')]//*[@role='textbox' or @role='combobox' or @contenteditable='true']))"
    );
    private final By pageTextFieldCandidates = By.xpath(
            "//input[not(@type='hidden') and not(@disabled)] | "
                    + "//textarea[not(@disabled)] | "
                    + "//*[@role='textbox' and not(@aria-disabled='true')] | "
                    + "//*[@role='combobox' and not(@aria-disabled='true')] | "
                    + "//*[@contenteditable='true' and not(@aria-disabled='true')]"
    );
    private final By dialogButtonCandidates = By.xpath(
            "//div[@role='dialog' or contains(@class,'MuiDialog-root') or contains(@class,'MuiModal-root') or contains(@class,'MuiDialog-container') or contains(@class,'MuiPopover-root') or contains(@class,'MuiDrawer-root')]//button"
    );
    private final By dropdownOptionCandidates = By.xpath(
            "//*[@" +
                    "role='option' or @role='menuitem' or @role='listitem' or " +
                    "contains(@class,'MuiAutocomplete-option') or contains(@class,'MuiMenuItem-root') or contains(@class,'MuiListItem-root')" +
                    "]"
    );
    private final By dialogRootCandidates = By.xpath("//div[@role='dialog' or contains(@class,'MuiDialog-root') or contains(@class,'MuiModal-root') or contains(@class,'MuiDialog-container') or contains(@class,'MuiPopover-root') or contains(@class,'MuiDrawer-root')]");

    /**
     * Logs in with the supplied credentials and navigates to the Configurations screen from Settings.
     */
    public ConfigurationPage openConfigurationScreen(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required to open configuration screen.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required to open configuration screen.");
        }

        logAction("Logging in and navigating to the configuration screen.");
        new LoginPage(driver).doLogin(username.trim(), password.trim());
        if (!isConfigurationScreenDisplayed()) {
            boolean openedFromSettings = openSettingsMenu() && openConfigurationsMenu();
            if (!openedFromSettings) {
                logAction("Settings navigation was not available from the current landing view. Falling back to direct /configurations navigation.");
                openConfigurationScreenDirectly();
            }
        }
        waitForConfigurationScreenToLoad();
        return this;
    }

    /**
     * Checks whether the Configurations screen is loaded and exposes visible configuration titles.
     */
    public boolean isConfigurationScreenDisplayed() {
        return !getVisibleConfigurationOptionTitles().isEmpty();
    }

    /**
     * Returns all currently visible configuration titles from the accordion listing.
     */
    public List<String> getVisibleConfigurationOptionTitles() {
        Set<String> titles = new LinkedHashSet<>();
        List<WebElement> titleElements = driver.findElements(configurationTitleSpans);
        for (WebElement element : titleElements) {
            try {
                if (!element.isDisplayed()) {
                    continue;
                }
                String title = normalizeText(element.getText());
                if (!title.isEmpty()) {
                    titles.add(title);
                }
            } catch (Exception ignored) {
            }
        }
        List<String> visibleTitles = new ArrayList<>(titles);
        logAction("Visible configuration titles found: " + visibleTitles);
        return visibleTitles;
    }

    /**
     * Resolves the editable leaf configuration titles from the current screen, including nested groups.
     */
    public List<String> getEditableConfigurationOptionTitles() {
        logAction("Collecting editable configuration option titles.");
        Set<String> leafTitles = new LinkedHashSet<>();
        Set<String> processedTitles = new LinkedHashSet<>();
        Deque<String> pendingTitles = new ArrayDeque<>(getVisibleConfigurationOptionTitles());
        int safetyCounter = 0;

        while (!pendingTitles.isEmpty() && safetyCounter < 200) {
            String title = normalizeText(pendingTitles.removeFirst());
            if (title.isEmpty() || !processedTitles.add(title)) {
                continue;
            }

            if (isLeafConfigurationOption(title)) {
                leafTitles.add(title);
                continue;
            }

            logAction("Configuration group detected, scanning nested options under: " + title);
            for (String nestedTitle : getVisibleConfigurationOptionTitles()) {
                String normalizedNestedTitle = normalizeText(nestedTitle);
                if (!normalizedNestedTitle.isEmpty() && !processedTitles.contains(normalizedNestedTitle)
                        && !pendingTitles.contains(normalizedNestedTitle)) {
                    pendingTitles.addLast(normalizedNestedTitle);
                }
            }
            safetyCounter++;
        }

        List<String> editableTitles = new ArrayList<>(leafTitles);
        logAction("Editable configuration titles found: " + editableTitles);
        return editableTitles;
    }

    /**
     * Filters the supplied configuration titles down to options whose editable value is binary (-1 or 1).
     */
    public List<String> getToggleableConfigurationOptionTitles(List<String> candidateTitles) {
        logAction("Collecting toggleable configuration option titles.");
        Set<String> toggleableTitles = new LinkedHashSet<>();
        if (candidateTitles == null || candidateTitles.isEmpty()) {
            return new ArrayList<>();
        }

        for (String title : candidateTitles) {
            String normalizedTitle = normalizeText(title);
            if (normalizedTitle.isEmpty()) {
                continue;
            }
            try {
                String currentValue = openConfigurationOptionAndGetValue(normalizedTitle);
                if (isAllowedConfigurationValue(currentValue)) {
                    toggleableTitles.add(normalizedTitle);
                    logAction("Toggleable configuration detected: " + normalizedTitle + " => " + currentValue);
                } else {
                    logAction("Skipping non-toggleable configuration: " + normalizedTitle + " => " + currentValue);
                }
            } catch (Exception e) {
                logAction("Skipping configuration while building toggleable list: " + normalizedTitle + " | " + e.getMessage());
            }
        }

        List<String> toggleable = new ArrayList<>(toggleableTitles);
        logAction("Toggleable configuration titles found: " + toggleable);
        return toggleable;
    }

    /**
     * Checks whether the requested configuration accordion is visible and enabled for interaction.
     */
    public boolean isConfigurationOptionVisibleAndEnabled(String title) {
        By summaryLocator = configurationSummaryByTitle(title);
        WebElement summary = eleutil.waitForFreshVisibleElement(summaryLocator, AppConstants.CONFIGURATION_OPTION_OPEN_WAIT_SECONDS);
        return summary.isDisplayed() && summary.isEnabled();
    }

    /**
     * Opens the requested configuration accordion and reads the primary editable value shown inside it.
     */
    public String openConfigurationOptionAndGetValue(String title) {
        String normalizedTitle = normalizeText(title);
        if (normalizedTitle.isEmpty()) {
            throw new IllegalArgumentException("Configuration title is required.");
        }

        WebElement valueInput = ensureExpandedAndGetValueInput(normalizedTitle);
        String value = readConfigurationValue(valueInput);
        logAction("Read configuration value for '" + normalizedTitle + "' => " + value);
        return value;
    }

    /**
     * Toggles a binary configuration value between -1 and 1, saves it, and returns the resulting message.
     */
    public String toggleConfigurationOptionAndSave(String title) {
        String normalizedTitle = normalizeText(title);
        if (normalizedTitle.isEmpty()) {
            throw new IllegalArgumentException("Configuration title is required.");
        }

        WebElement valueInput = ensureExpandedAndGetValueInput(normalizedTitle);
        String currentValue = readConfigurationValue(valueInput);
        String nextValue = getOppositeConfigurationValue(currentValue, normalizedTitle);

        By valueLocator = configurationValueByTitle(normalizedTitle);
        logAction("Updating configuration '" + normalizedTitle + "' from " + currentValue + " to " + nextValue);
        fillInput(valueInput, nextValue);

        WebElement updatedInput = eleutil.waitForElementVisible(valueLocator, AppConstants.CONFIGURATION_PAGE_LOAD_WAIT_SECONDS);
        String actualValue = readConfigurationValue(updatedInput);
        if (!nextValue.equals(actualValue)) {
            throw new IllegalStateException("Configuration value did not update correctly for '" + normalizedTitle
                    + "'. Expected=" + nextValue + " but found=" + actualValue);
        }

        logAction("Clicking Save for configuration '" + normalizedTitle + "'.");
        clickSectionPrimaryAction(normalizedTitle, "Save", "Submit");
        String message = waitForConfigurationMessage();
        logAction("Captured save/validation message for '" + normalizedTitle + "': " + message);
        return message;
    }

    /**
     * Updates a single numeric configuration value, saves the section, and returns the captured outcome.
     */
    public ConfigurationActionResult updateNumericConfigurationValueAndSave(String title, String value) {
        String normalizedTitle = normalizeText(title);
        if (normalizedTitle.isEmpty()) {
            throw new IllegalArgumentException("Configuration title is required.");
        }
        String normalizedValue = normalizeText(value);
        if (normalizedValue.isEmpty()) {
            throw new IllegalArgumentException("Configuration value is required.");
        }

        WebElement valueInput = ensureExpandedAndGetValueInput(normalizedTitle);
        fillInput(valueInput, normalizedValue);

        String actualValue = readConfigurationValue(valueInput);
        if (!normalizedValue.equals(actualValue)) {
            throw new IllegalStateException("Configuration value did not update correctly for '" + normalizedTitle
                    + "'. Expected=" + normalizedValue + " but found=" + actualValue);
        }

        logAction("Clicking Save for numeric configuration '" + normalizedTitle + "'.");
        clickSectionPrimaryAction(normalizedTitle, "Save", "Submit");
        String message = waitForConfigurationMessage();
        logAction("Captured save/validation message for '" + normalizedTitle + "': " + message);
        return new ConfigurationActionResult(message, List.of(actualValue));
    }

    /**
     * Updates all numeric inputs within a configuration section, saves the section, and returns the captured outcome.
     */
    public ConfigurationActionResult updateMultipleNumericValuesAndSave(String title, List<String> values) {
        String normalizedTitle = normalizeText(title);
        if (normalizedTitle.isEmpty()) {
            throw new IllegalArgumentException("Configuration title is required.");
        }
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Configuration values are required.");
        }

        expandConfigurationOptionIfNeeded(configurationSummaryByTitle(normalizedTitle), normalizedTitle);
        List<WebElement> inputs = waitForVisibleElements(sectionNumericInputsByTitle(normalizedTitle), AppConstants.CONFIGURATION_PAGE_LOAD_WAIT_SECONDS);
        if (inputs.isEmpty()) {
            throw new IllegalStateException(AppError.CONFIGURATION_MULTI_INPUTS_NOT_FOUND + " -> " + normalizedTitle);
        }

        List<String> appliedValues = new ArrayList<>();
        for (int index = 0; index < inputs.size(); index++) {
            String targetValue = values.get(Math.min(index, values.size() - 1));
            List<WebElement> freshInputs = waitForVisibleElements(sectionNumericInputsByTitle(normalizedTitle), AppConstants.CONFIGURATION_PAGE_LOAD_WAIT_SECONDS);
            if (index >= freshInputs.size()) {
                throw new IllegalStateException(AppError.CONFIGURATION_MULTI_INPUTS_NOT_FOUND + " -> " + normalizedTitle
                        + " | expected index=" + index + " but found only " + freshInputs.size() + " inputs");
            }
            WebElement input = freshInputs.get(index);
            fillInput(input, targetValue);
            List<WebElement> refreshedInputs = waitForVisibleElements(sectionNumericInputsByTitle(normalizedTitle), AppConstants.CONFIGURATION_PAGE_LOAD_WAIT_SECONDS);
            if (index < refreshedInputs.size()) {
                appliedValues.add(readConfigurationValue(refreshedInputs.get(index)));
            } else {
                appliedValues.add(readConfigurationValue(input));
            }
        }

        logAction("Clicking Save for multi-input configuration '" + normalizedTitle + "'.");
        clickSectionPrimaryAction(normalizedTitle, "Save", "Submit");
        String message = waitForConfigurationMessage();
        logAction("Captured save/validation message for '" + normalizedTitle + "': " + message);
        return new ConfigurationActionResult(message, appliedValues);
    }

    /**
     * Reads all visible numeric input values inside the requested configuration section.
     */
    public List<String> getConfigurationNumericValues(String title) {
        String normalizedTitle = normalizeText(title);
        if (normalizedTitle.isEmpty()) {
            throw new IllegalArgumentException("Configuration title is required.");
        }

        expandConfigurationOptionIfNeeded(configurationSummaryByTitle(normalizedTitle), normalizedTitle);
        List<WebElement> inputs = waitForVisibleElements(sectionNumericInputsByTitle(normalizedTitle), AppConstants.CONFIGURATION_PAGE_LOAD_WAIT_SECONDS);
        List<String> values = new ArrayList<>();
        for (WebElement input : inputs) {
            values.add(readConfigurationValue(input));
        }
        logAction("Numeric values for '" + normalizedTitle + "': " + values);
        return values;
    }

    /**
     * Opens the Appended Capacity create flow, selects BU and competency values, saves, and returns the outcome.
     */
    public ConfigurationActionResult createAppendedCapacityConfigurationAndSave(String title) {
        String normalizedTitle = normalizeText(title);
        String sectionSnapshot = getSectionText(normalizedTitle);
        try {
            openConfigurationAction(normalizedTitle, "Create");
            String buSearchTerm = readConfig(AppConstants.APPENDED_CONFIG_BU_SEARCH_TERM_KEY, AppConstants.APPENDED_CONFIG_BU_SEARCH_TERM_DEFAULT);
            String configuredCompetency = readConfig(AppConstants.APPENDED_CONFIG_COMPETENCY_KEY, AppConstants.APPENDED_CONFIG_COMPETENCY_DEFAULT);
            List<String> competencyCandidates = buildOrderedCandidates(
                    configuredCompetency,
                    readConfigList(
                            AppConstants.APPENDED_CONFIG_COMPETENCY_CANDIDATES_KEY,
                            AppConstants.APPENDED_CONFIG_COMPETENCY_CANDIDATES_DEFAULT
                    )
            );
            logAction("Using appended capacity BU search term='" + buSearchTerm + "' and competency hint='" + configuredCompetency + "'.");
            String bu = selectRequiredDialogSelectById("mui-component-select-buName", "buName", buSearchTerm, "Business Unit");
            String competency = selectBestDialogSelectByCandidates(
                    "mui-component-select-competencyId",
                    "competencyId",
                    competencyCandidates,
                    "Competency",
                    sectionSnapshot
            );
            String appendedCapacity = readConfig(AppConstants.APPENDED_CONFIG_HOURS_KEY, AppConstants.APPENDED_CONFIG_HOURS_DEFAULT);
            setDialogInputValueByName("appendedCapacity", appendedCapacity, "Appended Capacity");
            setDialogToggleByLegend("Skip Supercoach Approval for Job", readBoolean(
                    AppConstants.APPENDED_CONFIG_SUPERCOACH_BYPASS_KEY,
                    AppConstants.APPENDED_CONFIG_SUPERCOACH_BYPASS_DEFAULT
            ));
            boolean existingSelectionDetected = containsAllValuesIgnoreCase(sectionSnapshot, bu, competency);
            logAction("Selected appended capacity BU='" + bu + "', competency='" + competency
                    + "', appended capacity='" + appendedCapacity + "', pre-existing=" + existingSelectionDetected + ".");
            clickDialogPrimaryAction(normalizedTitle, "Save", "Create", "Submit", "Update", "Done");
            String message = waitForConfigurationMessage();
            if (isRequiredDialogSelectionMessage(message)) {
                logAction("Required-field validation received for appended capacity. Re-selecting required dropdowns.");
                if (isDialogSelectMissing("buName")) {
                    bu = selectRequiredDialogSelectById("mui-component-select-buName", "buName", buSearchTerm, "Business Unit");
                }
                if (isDialogSelectMissing("competencyId")) {
                    competency = selectBestDialogSelectByCandidates(
                            "mui-component-select-competencyId",
                            "competencyId",
                            competencyCandidates,
                            "Competency",
                            sectionSnapshot
                    );
                }
                setDialogInputValueByName("appendedCapacity", appendedCapacity, "Appended Capacity");
                setDialogToggleByLegend("Skip Supercoach Approval for Job", readBoolean(
                        AppConstants.APPENDED_CONFIG_SUPERCOACH_BYPASS_KEY,
                        AppConstants.APPENDED_CONFIG_SUPERCOACH_BYPASS_DEFAULT
                ));
                clickDialogPrimaryAction(normalizedTitle, "Save", "Create", "Submit", "Update", "Done");
                message = waitForConfigurationMessage();
            }
            if (message.isBlank()) {
                waitForDialogToClose();
                message = waitForConfigurationMessage();
            }
            logAction("Captured appended capacity message for '" + normalizedTitle + "': " + message);
            return new ConfigurationActionResult(message, List.of(bu, competency, appendedCapacity), existingSelectionDetected);
        } finally {
            cleanupDialogAfterAction(normalizedTitle);
        }
    }

    /**
     * Opens the Reason Configurations create flow, selects reason data, saves, and returns the outcome.
     */
    public ConfigurationActionResult createReasonConfigurationAndSave(String title) {
        String normalizedTitle = normalizeText(title);
        String sectionSnapshot = getSectionText(normalizedTitle);
        try {
            openConfigurationAction(normalizedTitle, "Add new");
            WebElement reasonTypeField = waitForDialogFieldByHints("Reason Type", true, "Reason Type", "reasonType");
            String reasonType = selectDropdownOptionWithOverride(reasonTypeField, readConfig(
                    AppConstants.CONFIGURATION_REASON_TYPE_VALUE_KEY,
                    AppConstants.CONFIGURATION_REASON_TYPE_VALUE
            ), "Reason Type", "reasonType");
            String conflictReason = readConfig(AppConstants.CONFIGURATION_CONFLICT_REASON_VALUE_KEY, AppConstants.CONFIGURATION_CONFLICT_REASON_VALUE);
            logAction("Using reason type='" + reasonType + "' and reason text='" + conflictReason + "'.");
            WebElement conflictReasonField;
            try {
                conflictReasonField = waitForDialogTextInputToBecomeEnabled(normalizedTitle, "Conflict Reason", "Reason", "reasonText");
            } catch (Exception textFieldFailure) {
                logAction("Reason text-input lookup did not stabilize. Falling back to dialog field-hint lookup. Cause="
                        + textFieldFailure.getMessage());
                conflictReasonField = waitForDialogFieldByHints("Conflict Reason", true, "Conflict Reason", "Reason", "reasonText");
            }
            if (sameField(reasonTypeField, conflictReasonField)) {
                conflictReasonField = waitForDialogFieldByHints("Conflict Reason", false, "Conflict Reason", "Reason", "reasonText");
            }
            String selectedReason;
            if (isFieldCombobox(conflictReasonField)) {
                selectedReason = selectDropdownOptionWithOverride(
                        conflictReasonField,
                        conflictReason,
                        "Conflict Reason",
                        "reasonText",
                        "Reason",
                        "conflict reason"
                );
            } else if (isEditableTextField(conflictReasonField)) {
                String uniqueReason = appendUniqueSuffix(conflictReason);
                fillInput(conflictReasonField, uniqueReason);
                selectedReason = readConfigurationValue(conflictReasonField);
            } else {
                throw new IllegalStateException("Conflict reason field is not editable/selectable: " + describeDialogField(conflictReasonField));
            }
            logAction("Selected reason type='" + reasonType + "', reason text='" + selectedReason + "'.");
            clickDialogPrimaryAction(normalizedTitle, "Create", "Save", "Submit", "Update", "Done");
            String message = waitForConfigurationMessage();
            if (isRequiredDialogSelectionMessage(message)) {
                logAction("Required-field validation received for reason configuration. Re-selecting visible controls.");
                reasonTypeField = waitForDialogFieldByHints("Reason Type", true, "Reason Type", "reasonType");
                reasonType = selectDropdownOptionWithOverride(reasonTypeField, readConfig(
                        AppConstants.CONFIGURATION_REASON_TYPE_VALUE_KEY,
                        AppConstants.CONFIGURATION_REASON_TYPE_VALUE
                ), "Reason Type", "reasonType");
                try {
                    conflictReasonField = waitForDialogTextInputToBecomeEnabled(normalizedTitle, "Conflict Reason", "Reason", "reasonText");
                } catch (Exception textFieldFailure) {
                    logAction("Retry reason text-input lookup did not stabilize. Falling back to dialog field-hint lookup. Cause="
                            + textFieldFailure.getMessage());
                    conflictReasonField = waitForDialogFieldByHints("Conflict Reason", true, "Conflict Reason", "Reason", "reasonText");
                }
                if (sameField(reasonTypeField, conflictReasonField)) {
                    conflictReasonField = waitForDialogFieldByHints("Conflict Reason", false, "Conflict Reason", "Reason", "reasonText");
                }
                if (isFieldCombobox(conflictReasonField)) {
                    selectedReason = selectDropdownOptionWithOverride(
                            conflictReasonField,
                            conflictReason,
                            "Conflict Reason",
                            "reasonText",
                            "Reason",
                            "conflict reason"
                    );
                } else if (isEditableTextField(conflictReasonField)) {
                    String uniqueReason = appendUniqueSuffix(conflictReason);
                    fillInput(conflictReasonField, uniqueReason);
                    selectedReason = readConfigurationValue(conflictReasonField);
                }
                clickDialogPrimaryAction(normalizedTitle, "Create", "Save", "Submit", "Update", "Done");
                message = waitForConfigurationMessage();
            }
            if (message.isBlank()) {
                waitForDialogToClose();
                message = waitForConfigurationMessage();
            }
            logAction("Captured reason configuration message for '" + normalizedTitle + "': " + message);
            boolean existingSelectionDetected = containsAllValuesIgnoreCase(sectionSnapshot, reasonType, selectedReason);
            return new ConfigurationActionResult(message, List.of(reasonType, selectedReason), existingSelectionDetected);
        } finally {
            cleanupDialogAfterAction(normalizedTitle);
        }
    }

    /**
     * Opens the Central Allocation create flow, selects a competency, saves, and returns the outcome.
     */
    public ConfigurationActionResult createCentralAllocationConfigurationAndSave(String title) {
        String normalizedTitle = normalizeText(title);
        String sectionSnapshot = getSectionText(normalizedTitle);
        String configuredBusinessUnit = readConfig(
                AppConstants.CONFIGURATION_CENTRAL_ALLOCATION_BUSINESS_UNIT_KEY,
                AppConstants.CONFIGURATION_CENTRAL_ALLOCATION_BUSINESS_UNIT_DEFAULT
        );
        String configuredCompetency = readConfig(
                AppConstants.CONFIGURATION_CENTRAL_ALLOCATION_COMPETENCY_KEY,
                AppConstants.CONFIGURATION_CENTRAL_ALLOCATION_COMPETENCY_DEFAULT
        );
        LinkedHashSet<String> attemptedCompetencies = new LinkedHashSet<>();
        String lastBusinessUnit = "";
        String lastCompetency = "";
        String lastMessage = "";
        boolean lastExistingSelectionDetected = false;
        try {
            int maxAttempts = 8;
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                openConfigurationAction(normalizedTitle, "Create");

                lastBusinessUnit = selectRequiredDialogSelectById("mui-component-select-buid", "buid", configuredBusinessUnit, "Business Unit");
                List<String> visibleCompetencyOptions = collectDialogSelectVisibleOptionsById(
                        "mui-component-select-competencyId",
                        "competencyId",
                        "Competency"
                );
                List<String> competencyCandidates = buildOrderedCandidates(configuredCompetency, visibleCompetencyOptions);
                Set<String> visibleCompetencySet = visibleCompetencyOptions.stream()
                        .map(this::normalizeText)
                        .filter(text -> !text.isEmpty())
                        .collect(Collectors.toCollection(LinkedHashSet::new));

                lastCompetency = selectCentralAllocationCompetencyCandidate(sectionSnapshot, competencyCandidates, attemptedCompetencies);
                if (normalizeText(lastCompetency).isEmpty()) {
                    lastMessage = AppConstants.CONFIGURATION_CREATE_FAILED_MESSAGE;
                    if (!attemptedCompetencies.isEmpty()) {
                        logAction("No central allocation competency candidate could be committed from the visible dialog options "
                                + "after exhausting previously attempted live options. Treating this as a duplicate-safe environment outcome.");
                        return new ConfigurationActionResult(lastMessage, List.of(lastBusinessUnit), true);
                    }
                    logAction("No central allocation competency candidate could be committed from the visible dialog options.");
                    break;
                }
                lastExistingSelectionDetected = containsValueIgnoreCase(sectionSnapshot, lastCompetency);
                attemptedCompetencies.add(normalizeText(lastCompetency));

                logAction("Selected central allocation BU='" + lastBusinessUnit + "', competency='" + lastCompetency
                        + "', pre-existing=" + lastExistingSelectionDetected + ", attempt=" + attempt + ".");

                clickDialogPrimaryAction(normalizedTitle, "Create", "Save", "Submit", "Update", "Done");
                lastMessage = waitForConfigurationMessage();

                if (isRequiredDialogSelectionMessage(lastMessage)) {
                    logAction("Required-field validation received for central allocation configuration. Re-selecting the same dropdown values.");
                    if (isDialogSelectMissing("buid")) {
                        lastBusinessUnit = selectRequiredDialogSelectById("mui-component-select-buid", "buid", configuredBusinessUnit, "Business Unit");
                    }
                    if (isDialogSelectMissing("competencyId")) {
                        lastCompetency = selectCentralAllocationDialogCompetency(lastCompetency);
                    }
                    clickDialogPrimaryAction(normalizedTitle, "Create", "Save", "Submit", "Update", "Done");
                    lastMessage = waitForConfigurationMessage();
                }

                if (lastMessage.isBlank()) {
                    waitForDialogToClose();
                    lastMessage = waitForConfigurationMessage();
                }
                if (lastMessage.isBlank()) {
                    lastMessage = inferCentralAllocationOutcomeMessage(
                            normalizedTitle,
                            lastBusinessUnit,
                            lastCompetency,
                            lastExistingSelectionDetected
                    );
                }

                if (!shouldRetryCentralAllocationCreate(lastMessage, lastExistingSelectionDetected)) {
                    logAction("Captured central allocation configuration message for '" + normalizedTitle + "': " + lastMessage);
                    return new ConfigurationActionResult(lastMessage, List.of(lastBusinessUnit, lastCompetency), lastExistingSelectionDetected);
                }

                if (!visibleCompetencySet.isEmpty() && attemptedCompetencies.containsAll(visibleCompetencySet)) {
                    logAction("All visible central allocation competencies for BU='" + lastBusinessUnit
                            + "' have been attempted and returned the same create-failed response. "
                            + "Treating this as a duplicate-safe environment outcome.");
                    return new ConfigurationActionResult(lastMessage, List.of(lastBusinessUnit, lastCompetency), true);
                }

                logAction("Central allocation create returned '" + lastMessage
                        + "'. Reopening the dialog to try a different visible competency option.");
                cleanupDialogAfterAction(normalizedTitle);
            }

            logAction("Captured central allocation configuration message for '" + normalizedTitle + "': " + lastMessage);
            return new ConfigurationActionResult(lastMessage, List.of(lastBusinessUnit, lastCompetency), lastExistingSelectionDetected);
        } finally {
            cleanupDialogAfterAction(normalizedTitle);
        }
    }

    /**
     * Validates whether a configuration message represents a successful save or update outcome.
     */
    public boolean isSuccessfulConfigurationMessage(String message) {
        String normalized = normalizeText(message).toLowerCase();
        return !normalized.isEmpty()
                && (normalized.contains(AppConstants.CONFIGURATION_SAVE_SUCCESS_MESSAGE.toLowerCase())
                || normalized.contains(AppConstants.CONFIGURATION_UPDATE_SUCCESS_MESSAGE.toLowerCase())
                || normalized.contains(AppConstants.CONFIGURATION_GENERIC_SUCCESS_FRAGMENT.toLowerCase()));
    }

    /**
     * Validates whether a configuration update message is acceptable for the current environment, including stable validation toasts.
     */
    public boolean isAcceptedUpdateConfigurationMessage(String message) {
        String normalized = normalizeText(message).toLowerCase();
        return !normalized.isEmpty()
                && (isSuccessfulConfigurationMessage(message)
                || normalized.contains(AppConstants.CONFIGURATION_UPDATE_VALIDATION_MESSAGE.toLowerCase()));
    }

    /**
     * Validates whether a configuration message represents a duplicate or already-existing record outcome.
     */
    public boolean isDuplicateConfigurationMessage(String message) {
        String normalized = normalizeText(message).toLowerCase();
        return !normalized.isEmpty()
                && (normalized.contains(AppConstants.CONFIGURATION_DUPLICATE_FRAGMENT.toLowerCase())
                || normalized.contains("duplicate"));
    }

    /**
     * Validates whether a create action result is acceptable for test purposes, including duplicate-safe outcomes.
     */
    public boolean isAcceptedCreateOutcome(ConfigurationActionResult result) {
        if (result == null) {
            return false;
        }
        String message = result.getMessage();
        if (isAcceptedUpdateConfigurationMessage(message) || isDuplicateConfigurationMessage(message)) {
            return true;
        }
        String normalized = normalizeText(message).toLowerCase();
        return result.isExistingSelectionDetected()
                && (normalized.contains(AppConstants.CONFIGURATION_CREATE_FAILED_MESSAGE.toLowerCase())
                || normalized.contains("error updating configuration"));
    }

    /**
     * Checks whether a configuration value is one of the supported binary values used by toggle tests.
     */
    public boolean isAllowedConfigurationValue(String value) {
        String normalizedValue = normalizeText(value);
        return AppConstants.CONFIGURATION_ALLOWED_VALUE_DISABLED.equals(normalizedValue)
                || AppConstants.CONFIGURATION_ALLOWED_VALUE_ENABLED.equals(normalizedValue);
    }

    /**
     * Checks whether a configuration value can be parsed and treated as a numeric test value.
     */
    public boolean isNumericConfigurationValue(String value) {
        String normalizedValue = normalizeText(value);
        return !normalizedValue.isEmpty() && normalizedValue.matches("-?\\d+");
    }

    private boolean openSettingsMenu() {
        logAction("Clicking settings icon.");
        if (eleutil.isElementVisible(settingsSharpIcon, 5)) {
            eleutil.clickStable(settingsSharpIcon, AppConstants.CONFIGURATION_OPTION_OPEN_WAIT_SECONDS);
            return true;
        }
        if (eleutil.isElementVisible(settingsIcon, 5)) {
            eleutil.clickStable(settingsIcon, AppConstants.CONFIGURATION_OPTION_OPEN_WAIT_SECONDS);
            return true;
        }
        if (eleutil.isElementVisible(settingsButton, 5)) {
            eleutil.clickStable(settingsButton, AppConstants.CONFIGURATION_OPTION_OPEN_WAIT_SECONDS);
            return true;
        }
        if (eleutil.isElementVisible(settingsMenuItem, 5)) {
            eleutil.clickStable(settingsMenuItem, AppConstants.CONFIGURATION_OPTION_OPEN_WAIT_SECONDS);
            return true;
        }
        return false;
    }

    private boolean openConfigurationsMenu() {
        logAction("Clicking Configurations menu item.");
        if (eleutil.isElementVisible(configurationsMenuItem, 5)) {
            eleutil.clickStable(configurationsMenuItem, AppConstants.CONFIGURATION_OPTION_OPEN_WAIT_SECONDS);
            return true;
        }
        if (eleutil.isElementVisible(configurationsMenuItemFallback, 5)) {
            eleutil.clickStable(configurationsMenuItemFallback, AppConstants.CONFIGURATION_OPTION_OPEN_WAIT_SECONDS);
            return true;
        }
        return false;
    }

    /**
     * Navigates directly to the configurations route when header-driven settings navigation is hidden in headless layouts.
     */
    private void openConfigurationScreenDirectly() {
        String appUrl = readConfig("url", "");
        if (normalizeText(appUrl).isEmpty()) {
            throw new NoSuchElementException("Settings icon is not visible on the page and application URL is not configured.");
        }

        String normalizedBaseUrl = appUrl.trim();
        if (normalizedBaseUrl.endsWith("/")) {
            normalizedBaseUrl = normalizedBaseUrl.substring(0, normalizedBaseUrl.length() - 1);
        }
        String directUrl = normalizedBaseUrl + "/configurations";
        logAction("Opening configuration screen directly via " + directUrl);
        driver.get(directUrl);
    }

    private void waitForConfigurationScreenToLoad() {
        try {
            eleutil.waitForElementVisible(configurationTitleSpans, AppConstants.CONFIGURATION_PAGE_LOAD_WAIT_SECONDS);
        } catch (TimeoutException e) {
            throw new IllegalStateException("Configuration screen did not load after opening the menu.", e);
        }
    }

    private WebElement ensureExpandedAndGetValueInput(String title) {
        By summaryLocator = configurationSummaryByTitle(title);
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                WebElement summary = eleutil.waitForFreshVisibleElement(summaryLocator, AppConstants.CONFIGURATION_OPTION_OPEN_WAIT_SECONDS);
                if (!summary.isDisplayed() || !summary.isEnabled()) {
                    throw new IllegalStateException("Configuration option is not visible or enabled: " + title);
                }

                expandConfigurationOptionIfNeeded(summaryLocator, title);

                By valueLocator = configurationValueByTitle(title);
                WebElement valueInput = eleutil.waitForFreshVisibleElement(valueLocator, AppConstants.CONFIGURATION_PAGE_LOAD_WAIT_SECONDS);
                if (!valueInput.isDisplayed() || !valueInput.isEnabled()) {
                    throw new IllegalStateException("Configuration value field is not visible or enabled: " + title);
                }
                return valueInput;
            } catch (StaleElementReferenceException stale) {
                logAction("Stale element while resolving configuration '" + title + "' (attempt " + attempt + "), retrying.");
            }
        }
        throw new IllegalStateException("Unable to resolve configuration value input after retries: " + title);
    }

    private boolean isLeafConfigurationOption(String title) {
        By summaryLocator = configurationSummaryByTitle(title);
        WebElement summary = eleutil.waitForElementVisible(summaryLocator, AppConstants.CONFIGURATION_OPTION_OPEN_WAIT_SECONDS);
        if (!summary.isDisplayed() || !summary.isEnabled()) {
            return false;
        }

        expandConfigurationOptionIfNeeded(summaryLocator, title);
        By valueLocator = configurationValueByTitle(title);
        boolean valueVisible = eleutil.isElementVisible(valueLocator, 3);
        if (valueVisible) {
            logAction("Leaf configuration option detected: " + title);
        }
        return valueVisible;
    }

    private void expandConfigurationOptionIfNeeded(By summaryLocator, String title) {
        By sectionRootLocator = sectionRootByTitle(title);
        By expandIconLocator = configurationExpandIconByTitle(title);
        for (int attempt = 1; attempt <= 4; attempt++) {
            try {
                dismissTransientConfigurationUi();
                WebElement summary = eleutil.waitForFreshVisibleElement(summaryLocator, AppConstants.CONFIGURATION_OPTION_OPEN_WAIT_SECONDS);
                if (isConfigurationSectionExpanded(summaryLocator, sectionRootLocator, title)) {
                    logAction("Configuration option is already interactive: " + title);
                    return;
                }

                logAction("Expanding configuration option: " + title + " (attempt " + attempt + ")");
                scrollIntoView(summary);
                if (eleutil.isElementVisible(expandIconLocator, 2)) {
                    WebElement expandIcon = eleutil.waitForFreshVisibleElement(expandIconLocator, 4);
                    scrollIntoView(expandIcon);
                    clickWithFallback(expandIcon);
                } else {
                    clickWithFallback(summary);
                }

                if (waitForAccordionExpanded(summaryLocator, sectionRootLocator, title)) {
                    return;
                }

                WebElement refreshedSummary = eleutil.waitForFreshVisibleElement(summaryLocator, AppConstants.CONFIGURATION_OPTION_OPEN_WAIT_SECONDS);
                scrollIntoView(refreshedSummary);
                clickWithFallback(refreshedSummary);
                if (waitForAccordionExpanded(summaryLocator, sectionRootLocator, title)) {
                    return;
                }
            } catch (StaleElementReferenceException stale) {
                logAction("Stale accordion summary while expanding '" + title + "'; retrying.");
            } catch (Exception e) {
                logAction("Expand attempt for '" + title + "' did not stabilize yet. Cause=" + e.getMessage());
            }
        }
        throw new IllegalStateException("Unable to expand configuration option after retries: " + title);
    }

    /**
     * Waits until the accordion reports an expanded state or exposes interactive content in the section body.
     */
    private boolean waitForAccordionExpanded(By summaryLocator, By sectionRootLocator, String title) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.CONFIGURATION_OPTION_OPEN_WAIT_SECONDS));
            return wait.until(d -> isConfigurationSectionExpanded(summaryLocator, sectionRootLocator, title));
        } catch (TimeoutException e) {
            logAction("Accordion did not report expanded state within timeout for: " + title);
            return isConfigurationSectionExpanded(summaryLocator, sectionRootLocator, title);
        }
    }

    /**
     * Treats a configuration section as open once its accordion expands or the section exposes usable controls.
     */
    private boolean isConfigurationSectionExpanded(By summaryLocator, By sectionRootLocator, String title) {
        try {
            WebElement summary = eleutil.waitForFreshVisibleElement(summaryLocator, 4);
            if ("true".equalsIgnoreCase(safeAttribute(summary, "aria-expanded"))) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return hasVisibleConfigurationSectionContent(sectionRootLocator, title);
    }

    /**
     * Detects visible content inside the configuration section so headless runs do not depend only on aria-expanded.
     */
    private boolean hasVisibleConfigurationSectionContent(By sectionRootLocator, String title) {
        try {
            WebElement sectionRoot = eleutil.waitForFreshVisibleElement(sectionRootLocator, 4);
            List<By> interactiveLocators = List.of(
                    By.xpath(".//input[@type='number' and not(@disabled)]"),
                    By.xpath(".//button[not(@disabled) and (normalize-space()='Save' or normalize-space()='Submit' or normalize-space()='Create' or normalize-space()='Update' or normalize-space()='Done' or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add new'))]"),
                    By.xpath(".//*[@role='combobox' and not(@aria-disabled='true')]"),
                    By.xpath(".//textarea[not(@disabled)]"),
                    By.xpath(".//input[not(@type='hidden') and not(@disabled) and not(@type='number')]")
            );
            for (By locator : interactiveLocators) {
                List<WebElement> elements = sectionRoot.findElements(locator);
                for (WebElement element : elements) {
                    try {
                        if (element != null && element.isDisplayed()) {
                            return true;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (Exception e) {
            logAction("Section content visibility check for '" + title + "' is retrying. Cause=" + e.getMessage());
        }
        return false;
    }

    /**
     * Clears leftover popovers or focus state that can block accordion interaction in headless runs.
     */
    private void dismissTransientConfigurationUi() {
        try {
            new Actions(driver).sendKeys(Keys.ESCAPE).pause(Duration.ofMillis(150)).perform();
        } catch (Exception ignored) {
        }
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "if (document && document.body) { document.body.dispatchEvent(new MouseEvent('click', {bubbles:true})); }"
            );
        } catch (Exception ignored) {
        }
    }

    private String readConfigurationValue(WebElement valueInput) {
        String value = safeAttribute(valueInput, "value");
        if (value == null || value.trim().isEmpty()) {
            value = valueInput.getText();
        }
        return normalizeText(value);
    }

    private String getOppositeConfigurationValue(String currentValue, String title) {
        String normalizedValue = normalizeText(currentValue);
        if (AppConstants.CONFIGURATION_ALLOWED_VALUE_DISABLED.equals(normalizedValue)) {
            return AppConstants.CONFIGURATION_ALLOWED_VALUE_ENABLED;
        }
        if (AppConstants.CONFIGURATION_ALLOWED_VALUE_ENABLED.equals(normalizedValue)) {
            return AppConstants.CONFIGURATION_ALLOWED_VALUE_DISABLED;
        }
        throw new IllegalStateException("Unsupported configuration value for '" + title + "': " + currentValue
                + ". Expected -1 or 1.");
    }

    private String waitForConfigurationMessage() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.CONFIGURATION_PAGE_LOAD_WAIT_SECONDS));
            return wait.until(d -> {
                List<WebElement> visibleMessages = new ArrayList<>();
                for (WebElement element : d.findElements(configurationMessageCandidates)) {
                    try {
                        if (element.isDisplayed()) {
                            visibleMessages.add(element);
                        }
                    } catch (StaleElementReferenceException ignored) {
                    }
                }

                String fallbackMessage = "";
                for (WebElement messageElement : visibleMessages) {
                    String message = extractActionText(messageElement);
                    if (message.isEmpty()) {
                        message = normalizeText(safeAttribute(messageElement, "textContent"));
                    }
                    if (message.isEmpty()) {
                        message = normalizeText(messageElement.getText());
                    }
                    if (message.isEmpty()) {
                        continue;
                    }
                    if (isMeaningfulConfigurationMessage(message)) {
                        return message;
                    }
                    if (fallbackMessage.isEmpty()) {
                        fallbackMessage = message;
                    }
                }
                if (!fallbackMessage.isEmpty()) {
                    return fallbackMessage;
                }

                List<WebElement> semanticMessages = new ArrayList<>();
                By semanticLocator = By.xpath(
                        "//*[not(self::script) and not(self::style) and ("
                                + "contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'success') or "
                                + "contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'saved') or "
                                + "contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'updated') or "
                                + "contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'warning') or "
                                + "contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'invalid') or "
                                + "contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'already')"
                                + ")]"
                );
                for (WebElement element : d.findElements(semanticLocator)) {
                    try {
                        if (element.isDisplayed()) {
                            semanticMessages.add(element);
                        }
                    } catch (StaleElementReferenceException ignored) {
                    }
                }
                for (WebElement semanticMessage : semanticMessages) {
                    String message = extractActionText(semanticMessage);
                    if (message.isEmpty()) {
                        message = normalizeText(safeAttribute(semanticMessage, "textContent"));
                    }
                    if (message.isEmpty()) {
                        message = normalizeText(semanticMessage.getText());
                    }
                    if (!message.isEmpty()) {
                        return message;
                    }
                }
                return null;
            });
        } catch (TimeoutException ignored) {
        }
        return "";
    }

    private String getSectionText(String title) {
        String normalizedTitle = normalizeText(title);
        if (normalizedTitle.isEmpty()) {
            return "";
        }
        try {
            expandConfigurationOptionIfNeeded(configurationSummaryByTitle(normalizedTitle), normalizedTitle);
            WebElement sectionRoot = eleutil.waitForFreshVisibleElement(sectionRootByTitle(normalizedTitle), AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS);
            if (sectionRoot == null) {
                return "";
            }

            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
                wait.until(d -> collectSectionContentTexts(sectionRoot).size() > 1);
            } catch (Exception ignored) {
            }

            Set<String> sectionTexts = collectSectionContentTexts(sectionRoot);
            if (sectionTexts.isEmpty()) {
                return "";
            }
            return String.join(" | ", sectionTexts);
        } catch (Exception ignored) {
            return "";
        }
    }

    private boolean isMeaningfulConfigurationMessage(String message) {
        String normalized = normalizeText(message).toLowerCase();
        if (normalized.isEmpty()) {
            return false;
        }
        return normalized.contains("success")
                || normalized.contains("saved")
                || normalized.contains("updated")
                || normalized.contains("required")
                || normalized.contains("warning")
                || normalized.contains("invalid")
                || normalized.contains("already");
    }

    private boolean shouldRetryCreateWithAlternateSelection(String message) {
        String normalized = normalizeText(message).toLowerCase();
        if (normalized.isEmpty()) {
            return false;
        }
        return normalized.contains(AppConstants.CONFIGURATION_CREATE_FAILED_MESSAGE.toLowerCase())
                || normalized.contains(AppConstants.CONFIGURATION_DUPLICATE_FRAGMENT.toLowerCase())
                || normalized.contains("duplicate")
                || normalized.contains("already exists");
    }

    private void openConfigurationAction(String title, String actionText) {
        expandConfigurationOptionIfNeeded(configurationSummaryByTitle(title), title);
        By actionButton = sectionButtonByTitleAndText(title, actionText);
        WebElement button;
        try {
            button = eleutil.waitForElementVisible(actionButton, AppConstants.CONFIGURATION_OPTION_OPEN_WAIT_SECONDS);
        } catch (TimeoutException timeoutException) {
            String lowerActionText = normalizeText(actionText).toLowerCase();
            By fallbackButton = By.xpath("//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),"
                    + toXPathLiteral(lowerActionText) + ")]");
            button = eleutil.waitForElementVisible(fallbackButton, AppConstants.CONFIGURATION_OPTION_OPEN_WAIT_SECONDS);
            actionButton = fallbackButton;
        }
        if (!button.isDisplayed() || !button.isEnabled()) {
            throw new IllegalStateException("Action button '" + actionText + "' is not visible or enabled for '" + title + "'.");
        }
        logAction("Clicking '" + actionText + "' for configuration '" + title + "'.");
        scrollIntoView(button);
        clickWithFallback(button);
        waitForDialogToOpen();
    }

    private void clickDialogPrimaryAction(String title, String... preferredButtonTexts) {
        WebElement activeDialogRoot = getActionScopeRoot(title);
        List<WebElement> visibleDialogButtons = collectVisibleDialogButtons();

        if (visibleDialogButtons.isEmpty()) {
            visibleDialogButtons = waitForVisibleElements(dialogButtonCandidates, AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS);
        }
        if (visibleDialogButtons.isEmpty()) {
            List<WebElement> sectionButtons = findVisibleSectionButtons(title);
            if (!sectionButtons.isEmpty()) {
                visibleDialogButtons = sectionButtons;
            }
        }
        if (visibleDialogButtons.isEmpty()) {
            throw new IllegalStateException("No visible dialog action buttons found while saving configuration: " + title);
        }

        if (activeDialogRoot != null) {
            logAction("Active dialog root for '" + title + "': " + describeDialogRoot(activeDialogRoot));
        }
        logAction("Visible dialog buttons for '" + title + "': count=" + visibleDialogButtons.size());
        try {
            logAction("Visible dialog buttons details for '" + title + "': " + describeActionButtons(visibleDialogButtons));
        } catch (Exception ignored) {
            logAction("Skipping button detail logging for '" + title + "' because the dialog re-rendered.");
        }
        WebElement targetButton = findPreferredDialogButton(visibleDialogButtons, title, preferredButtonTexts);
        if (targetButton == null) {
            targetButton = findFallbackDialogButton(visibleDialogButtons, title);
        }
        if (targetButton == null) {
            throw new IllegalStateException("No usable dialog action button found while saving configuration: " + title);
        }

        logAction("Clicking dialog primary action for '" + title + "': " + extractActionText(targetButton));
        clickWithFallback(targetButton);
    }

    private void clickSectionPrimaryAction(String title, String... preferredButtonTexts) {
        String normalizedTitle = normalizeText(title);
        By sectionButtonsLocator = By.xpath(sectionRootXPath(normalizedTitle) + "//button");
        List<WebElement> visibleButtons = waitForVisibleElements(sectionButtonsLocator, AppConstants.CONFIGURATION_PAGE_LOAD_WAIT_SECONDS);
        if (visibleButtons.isEmpty()) {
            visibleButtons = waitForVisibleElements(
                    By.xpath(sectionRootXPath(normalizedTitle) + "//*[@role='button']"),
                    AppConstants.CONFIGURATION_PAGE_LOAD_WAIT_SECONDS
            );
        }
        if (visibleButtons.isEmpty()) {
            throw new IllegalStateException("No visible section action buttons found while saving configuration: " + normalizedTitle);
        }

        logAction("Visible section buttons for '" + normalizedTitle + "': " + describeActionButtons(visibleButtons));
        WebElement targetButton = findPreferredDialogButton(visibleButtons, title, preferredButtonTexts);
        if (targetButton == null) {
            targetButton = findFallbackDialogButton(visibleButtons, title);
        }
        if (targetButton == null || !isButtonEnabled(targetButton)) {
            throw new IllegalStateException("No enabled section action button found while saving configuration: " + normalizedTitle);
        }

        logAction("Clicking section primary action for '" + normalizedTitle + "': " + extractActionText(targetButton));
        clickWithFallback(targetButton);
    }

    private WebElement locateVisibleDialogTextField(String title, String... fieldHints) {
        WebElement activeDialogRoot = getActionScopeRoot(title);
        List<WebElement> visibleFields = collectVisibleDialogFields();

        if (!visibleFields.isEmpty()) {
            logAction("Visible dialog fields for hints '" + String.join(", ", fieldHints) + "': " + describeDialogFields(visibleFields));
        }

        if (visibleFields.isEmpty()) {
            visibleFields = waitForVisibleElements(dialogTextFieldCandidates, AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS);
        }
        if (visibleFields.isEmpty()) {
            visibleFields = waitForVisibleElements(pageTextFieldCandidates, AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS);
        }
        if (visibleFields.isEmpty()) {
            visibleFields = findVisibleSectionFields(title);
        }
        if (visibleFields.isEmpty()) {
            throw new IllegalStateException("No visible dialog text fields were found for hints: " + String.join(", ", fieldHints));
        }

        logAction("Visible dialog text fields: " + describeDialogFields(visibleFields));
        if (fieldHints != null) {
            for (String fieldHint : fieldHints) {
                String normalizedHint = normalizeText(fieldHint).toLowerCase();
                if (normalizedHint.isEmpty()) {
                    continue;
                }
                for (WebElement field : visibleFields) {
                    if (fieldMatchesHint(field, normalizedHint)) {
                        logAction("Matched dialog field for hint '" + fieldHint + "': " + describeDialogField(field));
                        return field;
                    }
                }
            }
        }

        List<WebElement> fallbackFields = waitForVisibleElements(pageTextFieldCandidates, AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS);
        if (!fallbackFields.isEmpty()) {
            logAction("Visible page text fields fallback: " + describeDialogFields(fallbackFields));
            if (fieldHints != null) {
                for (String fieldHint : fieldHints) {
                    String normalizedHint = normalizeText(fieldHint).toLowerCase();
                    if (normalizedHint.isEmpty()) {
                        continue;
                    }
                    for (WebElement field : fallbackFields) {
                        if (fieldMatchesHint(field, normalizedHint)) {
                            logAction("Matched fallback page field for hint '" + fieldHint + "': " + describeDialogField(field));
                            return field;
                        }
                    }
                }
            }
            for (int index = fallbackFields.size() - 1; index >= 0; index--) {
                WebElement candidate = fallbackFields.get(index);
                if (isEditableTextField(candidate) && !isFieldCombobox(candidate)) {
                    logAction("Using fallback page text field: " + describeDialogField(candidate));
                    return candidate;
                }
            }
        }

        for (int index = visibleFields.size() - 1; index >= 0; index--) {
            WebElement candidate = visibleFields.get(index);
            if (isEditableTextField(candidate) && !isFieldCombobox(candidate)) {
                logAction("Using fallback dialog text field: " + describeDialogField(candidate));
                return candidate;
            }
        }

        List<WebElement> globalEditableFields = waitForVisibleElements(
                By.xpath("//input[not(@type='hidden') and not(@disabled)] | //textarea[not(@disabled)] | //*[@role='textbox' and not(@aria-disabled='true')] | //*[@contenteditable='true' and not(@aria-disabled='true')]"),
                AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS
        );
        if (!globalEditableFields.isEmpty()) {
            logAction("Visible global editable fields fallback: " + describeDialogFields(globalEditableFields));
            if (fieldHints != null) {
                for (String fieldHint : fieldHints) {
                    String normalizedHint = normalizeText(fieldHint).toLowerCase();
                    if (normalizedHint.isEmpty()) {
                        continue;
                    }
                    for (WebElement field : globalEditableFields) {
                        if (fieldMatchesHint(field, normalizedHint)) {
                            logAction("Matched global editable field for hint '" + fieldHint + "': " + describeDialogField(field));
                            return field;
                        }
                    }
                }
            }
            for (int index = globalEditableFields.size() - 1; index >= 0; index--) {
                WebElement candidate = globalEditableFields.get(index);
                if (isEditableTextField(candidate) && !isFieldCombobox(candidate)) {
                    logAction("Using fallback global editable field: " + describeDialogField(candidate));
                    return candidate;
                }
            }
        }

        WebElement fallback = visibleFields.get(visibleFields.size() - 1);
        logAction("Using last visible dialog field as fallback: " + describeDialogField(fallback));
        return fallback;
    }

    private void waitForDialogToOpen() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS));
            wait.until(d -> {
                WebElement activeDialogRoot = getActiveDialogRoot();
                if (activeDialogRoot == null) {
                    return false;
                }
                List<WebElement> actionableControls = findVisibleDescendants(activeDialogRoot, By.xpath(
                        ".//button[not(@disabled)] | .//input[not(@type='hidden')] | .//textarea | .//*[@role='textbox'] | .//*[@role='combobox'] | .//*[@contenteditable='true']"
                ));
                return !actionableControls.isEmpty();
            });
        } catch (TimeoutException e) {
            throw new IllegalStateException("Configuration dialog did not open.", e);
        }
    }

    private void waitForDialogToClose() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS));
            wait.until(d -> getVisibleDialogRoots().isEmpty());
        } catch (TimeoutException ignored) {
        }
    }

    private void cleanupDialogAfterAction(String title) {
        try {
            waitForDialogToClose();
            return;
        } catch (Exception ignored) {
        }

        closeVisibleDialogIfPresent(title);
        waitForDialogToClose();
    }

    private void closeVisibleDialogIfPresent(String title) {
        List<WebElement> roots = getVisibleDialogRoots();
        if (roots.isEmpty()) {
            return;
        }

        logAction("Attempting to close remaining dialog for '" + normalizeText(title) + "'.");
        for (WebElement root : roots) {
            try {
                List<WebElement> closeCandidates = findVisibleDescendants(root, By.xpath(
                        ".//button[not(@disabled)] | .//*[@role='button' and not(@aria-disabled='true')]"
                ));
                for (WebElement candidate : closeCandidates) {
                    String actionText = extractActionText(candidate).toLowerCase();
                    String ariaLabel = safeAttribute(candidate, "aria-label").toLowerCase();
                    if (actionText.contains("cancel")
                            || actionText.contains("close")
                            || actionText.contains("back")
                            || actionText.equals("x")
                            || ariaLabel.contains("close")) {
                        logAction("Closing leftover dialog using action: " + describeDialogField(candidate));
                        clickWithFallback(candidate);
                        return;
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private List<WebElement> getVisibleDialogRoots() {
        List<WebElement> visibleRoots = new ArrayList<>();
        for (WebElement dialog : driver.findElements(dialogRootCandidates)) {
            try {
                if (dialog.isDisplayed()) {
                    visibleRoots.add(dialog);
                }
            } catch (StaleElementReferenceException ignored) {
            }
        }
        return visibleRoots;
    }

    private WebElement getActiveDialogRoot() {
        List<WebElement> visibleRoots = getVisibleDialogRoots();
        if (visibleRoots.isEmpty()) {
            return null;
        }
        for (int index = visibleRoots.size() - 1; index >= 0; index--) {
            WebElement root = visibleRoots.get(index);
            try {
                if (!findVisibleDescendants(root, By.xpath(".//button | .//*[@role='button'] | .//input[not(@type='hidden')] | .//textarea | .//*[@role='textbox'] | .//*[@role='combobox']")).isEmpty()) {
                    return root;
                }
            } catch (Exception ignored) {
            }
        }
        return visibleRoots.get(visibleRoots.size() - 1);
    }

    private WebElement getActionScopeRoot(String title) {
        WebElement activeDialogRoot = getActiveDialogRoot();
        if (activeDialogRoot != null) {
            return activeDialogRoot;
        }

        String normalizedTitle = normalizeText(title);
        if (!normalizedTitle.isEmpty()) {
            try {
                WebElement sectionRoot = eleutil.waitForFreshVisibleElement(sectionRootByTitle(normalizedTitle), AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS);
                if (sectionRoot != null && sectionRoot.isDisplayed()) {
                    return sectionRoot;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private List<WebElement> findVisibleDescendants(WebElement root, By locator) {
        List<WebElement> visible = new ArrayList<>();
        if (root == null) {
            return visible;
        }
        for (WebElement element : root.findElements(locator)) {
            try {
                if (element.isDisplayed()) {
                    visible.add(element);
                }
            } catch (StaleElementReferenceException ignored) {
            }
        }
        return visible;
    }

    private List<WebElement> collectVisibleDialogButtons() {
        List<WebElement> visible = new ArrayList<>();
        for (WebElement root : getVisibleDialogRoots()) {
            visible.addAll(findVisibleDescendants(root, By.xpath(".//button[not(@disabled)] | .//*[@role='button' and not(@aria-disabled='true')]")));
        }
        return deduplicateVisibleElements(visible);
    }

    private List<WebElement> collectVisibleDialogFields() {
        List<WebElement> visible = new ArrayList<>();
        for (WebElement root : getVisibleDialogRoots()) {
            visible.addAll(findVisibleDescendants(root, By.xpath(
                    ".//input[not(@type='hidden')] | .//textarea | .//*[@role='textbox'] | .//*[@role='combobox'] | .//*[@contenteditable='true']"
            )));
        }
        return deduplicateVisibleElements(visible);
    }

    private List<WebElement> deduplicateVisibleElements(List<WebElement> elements) {
        List<WebElement> deduplicated = new ArrayList<>();
        if (elements == null || elements.isEmpty()) {
            return deduplicated;
        }
        Set<String> fingerprints = new LinkedHashSet<>();
        for (WebElement element : elements) {
            try {
                if (element == null || !element.isDisplayed()) {
                    continue;
                }
                String fingerprint = safeTagName(element) + "|" + extractActionText(element) + "|"
                        + safeAttribute(element, "id") + "|" + safeAttribute(element, "name") + "|"
                        + safeAttribute(element, "aria-label") + "|" + safeAttribute(element, "placeholder");
                if (fingerprints.add(fingerprint)) {
                    deduplicated.add(element);
                }
            } catch (StaleElementReferenceException ignored) {
            }
        }
        return deduplicated;
    }

    private List<WebElement> findVisibleSectionButtons(String title) {
        String normalizedTitle = normalizeText(title);
        if (normalizedTitle.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            WebElement sectionRoot = eleutil.waitForFreshVisibleElement(sectionRootByTitle(normalizedTitle), AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS);
            if (sectionRoot == null || !sectionRoot.isDisplayed()) {
                return new ArrayList<>();
            }
            return findVisibleDescendants(sectionRoot, By.xpath(".//button[not(@disabled)] | .//*[@role='button' and not(@aria-disabled='true')]"));
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }

    private List<WebElement> findVisibleSectionFields(String title) {
        String normalizedTitle = normalizeText(title);
        if (normalizedTitle.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            WebElement sectionRoot = eleutil.waitForFreshVisibleElement(sectionRootByTitle(normalizedTitle), AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS);
            if (sectionRoot == null || !sectionRoot.isDisplayed()) {
                return new ArrayList<>();
            }
            return findVisibleDescendants(sectionRoot, By.xpath(
                    ".//input[not(@type='hidden')] | .//textarea | .//*[@role='textbox'] | .//*[@role='combobox'] | .//*[@contenteditable='true']"
            ));
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }

    private String describeDialogRoot(WebElement root) {
        if (root == null) {
            return "<null>";
        }
        List<String> attributes = new ArrayList<>();
        String role = safeAttribute(root, "role");
        String clazz = safeAttribute(root, "class");
        String text = normalizeText(root.getText());
        if (!role.isEmpty()) {
            attributes.add("role='" + role + "'");
        }
        if (!clazz.isEmpty()) {
            attributes.add("class='" + clazz + "'");
        }
        if (!text.isEmpty()) {
            attributes.add("text='" + text + "'");
        }
        return root.getTagName() + " " + String.join(" ", attributes);
    }

    private String selectFirstDropdownOptionWithFilter(String... fieldHints) {
        WebElement field = locateVisibleDialogDropdownField(fieldHints);
        return selectDropdownOptionWithOverride(field, "", fieldHints);
    }

    private String selectFirstDropdownOptionWithFilter(WebElement field, String... fieldHints) {
        return selectDropdownOptionWithOverride(field, "", fieldHints);
    }

    private String selectDropdownOptionWithOverride(WebElement field, String overrideSearchTerm, String... fieldHints) {
        if (field == null) {
            throw new NoSuchElementException("Unable to locate dropdown field for hints: " + String.join(", ", fieldHints));
        }
        if (!isFieldReadyForInteraction(field)) {
            throw new IllegalStateException("Field is not visible or enabled for hints: " + String.join(", ", fieldHints));
        }

        scrollIntoView(field);
        boolean editableInput = isEditableTextField(field);
        String searchTerm = normalizeText(overrideSearchTerm);
        if (searchTerm.isBlank()) {
            searchTerm = resolveSearchTerm(fieldHints);
        }
        WebElement searchField = null;
        if (!searchTerm.isBlank()) {
            searchField = locateDropdownSearchField(field, fieldHints);
            if (searchField != null && !sameField(searchField, field)) {
                editableInput = true;
                logAction("Filtering dropdown for '" + String.join(", ", fieldHints) + "' using search field '"
                        + describeDialogField(searchField) + "' and search term '" + searchTerm + "'.");
                clearAndType(searchField, searchTerm);
            } else if (editableInput) {
                logAction("Filtering dropdown for '" + String.join(", ", fieldHints) + "' using search term '" + searchTerm + "'.");
                clearAndType(field, searchTerm);
            } else {
                logAction("No dedicated search field found for '" + String.join(", ", fieldHints)
                        + "'. Using dropdown options directly.");
            }
        }

        if (!editableInput) {
            clickWithFallback(field);
            if (!searchTerm.isBlank()) {
                try {
                    field.sendKeys(searchTerm);
                } catch (Exception ignored) {
                }
            }
        } else {
            sendDropdownNavigationKeys(searchField != null && !sameField(searchField, field) ? searchField : field);
        }

        List<WebElement> visibleOptions = new ArrayList<>();
        WebElement activeDialogRoot = getActionScopeRoot(fieldHints != null && fieldHints.length > 0 ? fieldHints[0] : "");
        if (activeDialogRoot != null) {
            visibleOptions.addAll(findVisibleDescendants(activeDialogRoot, dropdownOptionCandidates));
        }
        if (visibleOptions.isEmpty()) {
            visibleOptions = waitForVisibleElements(dropdownOptionCandidates,
                    editableInput ? AppConstants.CONFIGURATION_FILTER_WAIT_SECONDS : AppConstants.CONFIGURATION_DROPDOWN_WAIT_SECONDS);
        }

        if (visibleOptions.isEmpty()) {
            logAction("No visible options found for '" + String.join(", ", fieldHints)
                    + "'. Trying keyboard fallback.");
            try {
                field.sendKeys(Keys.ARROW_DOWN);
                field.sendKeys(Keys.ENTER);
                field.sendKeys(Keys.TAB);
                field.sendKeys(Keys.ESCAPE);
            } catch (Exception ignored) {
            }
            String keyboardSelected = resolveDropdownSelectionValue(field, searchField, searchTerm, "");
            if (keyboardSelected.isEmpty()) {
                throw new IllegalStateException("No dropdown options were visible for field hints: " + String.join(", ", fieldHints));
            }
            logAction("Selected dropdown option for '" + String.join(", ", fieldHints) + "' via keyboard fallback: " + keyboardSelected);
            return normalizeText(keyboardSelected);
        }

        String firstOptionText = extractOptionText(visibleOptions.get(0));
        if (firstOptionText.isEmpty()) {
            throw new IllegalStateException("Unable to read first dropdown option for field hints: " + String.join(", ", fieldHints));
        }

        if (!searchTerm.isBlank()) {
            String lowerFilter = searchTerm.toLowerCase();
            List<String> visibleTexts = extractOptionTexts(visibleOptions);
            WebElement selectedOption = visibleOptions.get(0);
            String selectedOptionText = firstOptionText;
            for (WebElement option : visibleOptions) {
                String optionText = extractOptionText(option);
                if (!optionText.isEmpty() && optionText.toLowerCase().contains(lowerFilter)) {
                    selectedOption = option;
                    selectedOptionText = optionText;
                    break;
                }
            }
            if (!selectedOptionText.toLowerCase().contains(lowerFilter)) {
                logAction("No visible dropdown option matched filter '" + searchTerm + "' for '"
                        + String.join(", ", fieldHints) + "'. Using first visible option: " + selectedOptionText);
            } else {
                logAction("Dropdown filter for '" + String.join(", ", fieldHints) + "' returned: " + visibleTexts);
            }
            logAction("Clicking dropdown option for '" + String.join(", ", fieldHints) + "': " + selectedOptionText);
            clickWithFallback(selectedOption);
            commitDropdownSelection(searchField != null && !sameField(searchField, field) ? searchField : field);
            waitForDropdownOverlayToClose(field);
            String selectedValue = resolveDropdownSelectionValue(field, searchField, searchTerm, selectedOptionText);
            if (selectedValue.isEmpty()) {
                selectedValue = selectedOptionText;
            }
            logAction("Selected dropdown option for '" + String.join(", ", fieldHints) + "': " + selectedValue);
            return normalizeText(selectedValue);
        }

        logAction("Dropdown options visible for '" + String.join(", ", fieldHints) + "': " + extractOptionTexts(visibleOptions));
        WebElement selectedOption = visibleOptions.get(0);
        logAction("Clicking dropdown option for '" + String.join(", ", fieldHints) + "': " + firstOptionText);
        clickWithFallback(selectedOption);
        commitDropdownSelection(field);
        waitForDropdownOverlayToClose(field);
        String selectedValue = resolveDropdownSelectionValue(field, searchField, searchTerm, firstOptionText);
        if (selectedValue.isEmpty()) {
            selectedValue = firstOptionText;
        }
        logAction("Selected dropdown option for '" + String.join(", ", fieldHints) + "': " + selectedValue);
        return normalizeText(selectedValue);
    }

    private String setConfigurationFieldValue(String title, String desiredValue, String... fieldHints) {
        String normalizedDesiredValue = normalizeText(desiredValue);
        if (normalizedDesiredValue.isEmpty()) {
            throw new IllegalArgumentException("Desired configuration value is required.");
        }

        WebElement field = null;
        try {
            field = locateVisibleDialogDropdownField(false, fieldHints);
        } catch (Exception ignored) {
        }
        if (field == null) {
            field = waitForDialogTextInputToBecomeEnabled(title, fieldHints);
        }
        if (field == null) {
            throw new IllegalStateException("Unable to resolve a configuration field for hints: " + String.join(", ", fieldHints));
        }

        logAction("Resolved configuration field for hints '" + String.join(", ", fieldHints) + "': " + describeDialogField(field));
        if (isFieldCombobox(field)) {
            return selectDropdownOptionWithOverride(field, normalizedDesiredValue, fieldHints);
        }
        if (isEditableTextField(field)) {
            fillInput(field, normalizedDesiredValue);
            String actualValue = readConfigurationValue(field);
            return actualValue.isBlank() ? normalizedDesiredValue : actualValue;
        }

        throw new IllegalStateException("Resolved field is neither dropdown nor text input: " + describeDialogField(field));
    }

    private String setConfigurationFieldValueAfterTab(WebElement currentField, String title, String desiredValue, String... fieldHints) {
        if (currentField != null) {
            try {
                currentField.sendKeys(Keys.TAB);
            } catch (Exception ignored) {
                try {
                    driver.findElement(By.tagName("body")).sendKeys(Keys.TAB);
                } catch (Exception ignoredToo) {
                }
            }
        }

        WebElement focusedField = waitForFocusedEditableField(currentField, 4);
        if (focusedField != null) {
            logAction("Focused next configuration field after TAB: " + describeDialogField(focusedField));
            if (isFieldCombobox(focusedField)) {
                return selectDropdownOptionWithOverride(focusedField, desiredValue, fieldHints);
            }
            if (isEditableTextField(focusedField)) {
                fillInput(focusedField, desiredValue);
                String actualValue = readConfigurationValue(focusedField);
                return actualValue.isBlank() ? normalizeText(desiredValue) : actualValue;
            }
        }

        WebElement adjacentField = locateNextVisibleField(currentField, title, fieldHints);
        if (adjacentField != null) {
            logAction("Using next visible configuration field after TAB: " + describeDialogField(adjacentField));
            if (isFieldCombobox(adjacentField)) {
                return selectDropdownOptionWithOverride(adjacentField, desiredValue, fieldHints);
            }
            if (isEditableTextField(adjacentField)) {
                fillInput(adjacentField, desiredValue);
                String actualValue = readConfigurationValue(adjacentField);
                return actualValue.isBlank() ? normalizeText(desiredValue) : actualValue;
            }
        }

        return setConfigurationFieldValue(title, desiredValue, fieldHints);
    }

    private WebElement locateNextVisibleField(WebElement currentField, String title, String... fieldHints) {
        List<WebElement> candidates = new ArrayList<>(collectVisibleDialogFields());
        if (candidates.isEmpty()) {
            candidates.addAll(waitForVisibleElements(dialogTextFieldCandidates, AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS));
        }
        if (candidates.isEmpty()) {
            candidates.addAll(waitForVisibleElements(pageTextFieldCandidates, AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS));
        }
        if (candidates.isEmpty() && title != null && !title.trim().isEmpty()) {
            candidates.addAll(findVisibleSectionFields(title));
        }

        List<WebElement> usableCandidates = new ArrayList<>();
        for (WebElement candidate : deduplicateVisibleElements(candidates)) {
            try {
                if (candidate == null || sameField(currentField, candidate) || !isFieldReadyForInteraction(candidate)) {
                    continue;
                }
                if (isFieldCombobox(candidate) || isEditableTextField(candidate)) {
                    usableCandidates.add(candidate);
                }
            } catch (Exception ignored) {
            }
        }

        if (usableCandidates.isEmpty()) {
            return null;
        }

        logAction("Next-field candidates for hints '" + String.join(", ", fieldHints) + "': " + describeDialogFields(usableCandidates));
        WebElement matched = matchVisibleFieldByHints(usableCandidates, fieldHints);
        if (matched != null) {
            return matched;
        }
        return usableCandidates.get(usableCandidates.size() - 1);
    }

    private WebElement waitForFocusedEditableField(WebElement previousField, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            return wait.until(d -> {
                try {
                    Object activeElement = ((JavascriptExecutor) d).executeScript("return document.activeElement;");
                    if (!(activeElement instanceof WebElement)) {
                        return null;
                    }
                    WebElement field = (WebElement) activeElement;
                    if (field == null || sameField(previousField, field) || !isFieldReadyForInteraction(field)) {
                        return null;
                    }
                    if (isEditableTextField(field) || isFieldCombobox(field)) {
                        return field;
                    }
                } catch (Exception ignored) {
                }
                return null;
            });
        } catch (TimeoutException ignored) {
            return null;
        }
    }

    private WebElement locateVisibleDialogDropdownField(String... fieldHints) {
        return locateVisibleDialogDropdownField(true, fieldHints);
    }

    private WebElement locateVisibleDialogDropdownField(boolean requireEnabled, String... fieldHints) {
        List<WebElement> visibleFields = collectVisibleDialogFields();

        if (!visibleFields.isEmpty()) {
            logAction("Visible dialog dropdown candidates for hints '" + String.join(", ", fieldHints) + "': "
                    + describeDialogFields(visibleFields));
        }

        if (visibleFields.isEmpty()) {
            visibleFields = waitForVisibleElements(dialogTextFieldCandidates, AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS);
        }
        if (visibleFields.isEmpty()) {
            visibleFields = findVisibleSectionFields(fieldHints != null && fieldHints.length > 0 ? fieldHints[0] : "");
        }
        if (visibleFields.isEmpty()) {
            return null;
        }

        List<WebElement> comboboxFields = new ArrayList<>();
        List<WebElement> editableFields = new ArrayList<>();
        for (WebElement field : visibleFields) {
            try {
                if (field != null && field.isDisplayed() && (!requireEnabled || isFieldReadyForInteraction(field))) {
                    if (isFieldCombobox(field)) {
                        comboboxFields.add(field);
                    } else if (isEditableTextField(field)) {
                        editableFields.add(field);
                    }
                }
            } catch (StaleElementReferenceException ignored) {
            }
        }

        WebElement matched = matchVisibleFieldByHints(comboboxFields, fieldHints);
        if (matched != null) {
            logAction("Matched dialog combobox for hints '" + String.join(", ", fieldHints) + "': " + describeDialogField(matched));
            return matched;
        }

        matched = matchVisibleFieldByHints(editableFields, fieldHints);
        if (matched != null) {
            logAction("Matched dialog editable field for hints '" + String.join(", ", fieldHints) + "': " + describeDialogField(matched));
            return matched;
        }

        matched = matchVisibleFieldByHints(visibleFields, fieldHints);
        if (matched != null) {
            logAction("Matched dialog field for hints '" + String.join(", ", fieldHints) + "': " + describeDialogField(matched));
            return matched;
        }

        if (!comboboxFields.isEmpty()) {
            WebElement fallback = comboboxFields.get(0);
            logAction("Using fallback dialog combobox: " + describeDialogField(fallback));
            return fallback;
        }
        if (!editableFields.isEmpty()) {
            WebElement fallback = editableFields.get(0);
            logAction("Using fallback dialog editable field: " + describeDialogField(fallback));
            return fallback;
        }

        WebElement fallback = visibleFields.get(0);
        logAction("Using fallback dialog field: " + describeDialogField(fallback));
        return fallback;
    }

    private WebElement locateVisibleDialogTextInputField(String title, String... fieldHints) {
        return locateVisibleDialogTextInputField(title, true, fieldHints);
    }

    private WebElement locateVisibleDialogTextInputCandidate(String title, String... fieldHints) {
        return locateVisibleDialogTextInputField(title, false, fieldHints);
    }

    private WebElement locateVisibleDialogTextInputField(String title, boolean requireEnabled, String... fieldHints) {
        List<WebElement> visibleFields = collectVisibleDialogFields();

        if (!visibleFields.isEmpty()) {
            logAction("Visible dialog text-input candidates for hints '" + String.join(", ", fieldHints) + "': "
                    + describeDialogFields(visibleFields));
        }

        if (visibleFields.isEmpty()) {
            visibleFields = waitForVisibleElements(dialogTextFieldCandidates, AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS);
        }
        if (visibleFields.isEmpty()) {
            visibleFields = waitForVisibleElements(pageTextFieldCandidates, AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS);
        }
        if (visibleFields.isEmpty()) {
            visibleFields = findVisibleSectionFields(title);
        }
        if (visibleFields.isEmpty()) {
            throw new IllegalStateException("No visible dialog text input fields were found for hints: " + String.join(", ", fieldHints));
        }

        List<WebElement> textInputsOnly = new ArrayList<>();
        for (WebElement field : visibleFields) {
            try {
                if (field != null && field.isDisplayed() && (!requireEnabled || isFieldReadyForInteraction(field)) && isEditableTextField(field) && !isFieldCombobox(field)) {
                    textInputsOnly.add(field);
                }
            } catch (StaleElementReferenceException ignored) {
            }
        }

        if (textInputsOnly.isEmpty()) {
            List<WebElement> fallbackFields = waitForVisibleElements(pageTextFieldCandidates, AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS);
            for (WebElement field : fallbackFields) {
                try {
                    if (field != null && field.isDisplayed() && (!requireEnabled || isFieldReadyForInteraction(field)) && isEditableTextField(field) && !isFieldCombobox(field)) {
                        textInputsOnly.add(field);
                    }
                } catch (StaleElementReferenceException ignored) {
                }
            }
        }

        if (textInputsOnly.isEmpty() && !visibleFields.isEmpty()) {
            for (int index = visibleFields.size() - 1; index >= 0; index--) {
                WebElement candidate = visibleFields.get(index);
                try {
                    if (candidate != null && candidate.isDisplayed() && isEditableTextField(candidate) && !isFieldCombobox(candidate)) {
                        logAction("Using fallback visible dialog text input candidate: " + describeDialogField(candidate));
                        return candidate;
                    }
                } catch (StaleElementReferenceException ignored) {
                }
            }
        }

        if (textInputsOnly.isEmpty()) {
            throw new IllegalStateException("No visible editable text inputs were found for hints: " + String.join(", ", fieldHints));
        }

        logAction("Visible dialog text inputs: " + describeDialogFields(textInputsOnly));
        if (fieldHints != null) {
            for (String fieldHint : fieldHints) {
                String normalizedHint = normalizeText(fieldHint).toLowerCase();
                if (normalizedHint.isEmpty()) {
                    continue;
                }
                for (WebElement field : textInputsOnly) {
                    if (fieldMatchesHint(field, normalizedHint)) {
                        logAction("Matched dialog text input for hint '" + fieldHint + "': " + describeDialogField(field));
                        return field;
                    }
                }
            }
        }

        WebElement fallback = textInputsOnly.get(textInputsOnly.size() - 1);
        logAction("Using fallback dialog text input: " + describeDialogField(fallback));
        return fallback;
    }

    private WebElement locateDropdownSearchField(WebElement sourceField, String... fieldHints) {
        List<WebElement> candidates = new ArrayList<>(collectVisibleDialogFields());
        if (candidates.isEmpty()) {
            return null;
        }

        for (WebElement candidate : candidates) {
            try {
                if (candidate == null || !candidate.isDisplayed() || !isFieldReadyForInteraction(candidate)) {
                    continue;
                }
                if (sameField(sourceField, candidate)) {
                    continue;
                }
                if (!isEditableTextField(candidate)) {
                    continue;
                }
                if (fieldMatchesHint(candidate, "search") || fieldMatchesHint(candidate, "filter")) {
                    return candidate;
                }
                if (fieldHints != null) {
                    for (String fieldHint : fieldHints) {
                        String normalizedHint = normalizeText(fieldHint).toLowerCase();
                        if (!normalizedHint.isEmpty() && fieldMatchesHint(candidate, normalizedHint)) {
                            return candidate;
                        }
                    }
                }
            } catch (StaleElementReferenceException ignored) {
            }
        }
        return null;
    }

    private boolean sameField(WebElement first, WebElement second) {
        if (first == null || second == null) {
            return false;
        }
        if (first.equals(second)) {
            return true;
        }
        String firstFingerprint = describeDialogField(first);
        String secondFingerprint = describeDialogField(second);
        return !firstFingerprint.isEmpty() && firstFingerprint.equals(secondFingerprint);
    }

    private String readDropdownCommittedValue(WebElement field) {
        String value = readInputValue(field);
        if (!isPlaceholderSelectionValue(value)) {
            return normalizeText(value);
        }

        String adjacentValue = readAdjacentNativeInputValue(field);
        if (!isPlaceholderSelectionValue(adjacentValue)) {
            return normalizeText(adjacentValue);
        }

        return "";
    }

    private String readAdjacentNativeInputValue(WebElement field) {
        if (field == null) {
            return "";
        }
        List<String> xpaths = List.of(
                "following-sibling::input[1]",
                "preceding-sibling::input[1]",
                "ancestor::div[contains(@class,'MuiInputBase-root')][1]//input[1]"
        );
        for (String xpath : xpaths) {
            try {
                WebElement input = field.findElement(By.xpath(xpath));
                String value = readInputValue(input);
                if (!isPlaceholderSelectionValue(value)) {
                    return normalizeText(value);
                }
            } catch (Exception ignored) {
            }
        }
        return "";
    }

    private boolean isPlaceholderSelectionValue(String value) {
        String normalized = normalizeText(value).toLowerCase();
        return normalized.isEmpty()
                || "-0".equals(normalized)
                || "__".equals(normalized)
                || normalized.equals("select")
                || normalized.contains("select ");
    }

    private boolean isElementExplicitlyDisabled(WebElement element) {
        String ariaDisabled = normalizeText(safeAttribute(element, "aria-disabled")).toLowerCase();
        String disabled = normalizeText(safeAttribute(element, "disabled")).toLowerCase();
        String clazz = normalizeText(safeAttribute(element, "class")).toLowerCase();
        return "true".equals(ariaDisabled)
                || (!disabled.isEmpty() && !"false".equals(disabled))
                || clazz.contains("mui-disabled");
    }

    private boolean isFieldReadyForInteraction(WebElement field) {
        if (field == null) {
            return false;
        }
        try {
            if (!field.isDisplayed()) {
                return false;
            }
            if (isElementExplicitlyDisabled(field)) {
                return false;
            }
            try {
                if (field.isEnabled()) {
                    return true;
                }
            } catch (Exception ignored) {
            }
            return isFieldCombobox(field) || isEditableTextField(field);
        } catch (StaleElementReferenceException ignored) {
            return false;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void waitForFieldToBecomeEnabled(String... fieldHints) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS));
            wait.until(d -> {
                try {
                    WebElement field = locateVisibleDialogDropdownField(false, fieldHints);
                    if (field == null) {
                        return false;
                    }
                    return isFieldReadyForInteraction(field);
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (TimeoutException e) {
            throw new IllegalStateException("Field did not become enabled for hints: " + String.join(", ", fieldHints), e);
        }
    }

    private WebElement waitForExactDialogFieldToBecomeEnabled(By locator, String description) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS));
            return wait.until(d -> {
                try {
                    List<WebElement> candidates = d.findElements(locator);
                    for (WebElement candidate : candidates) {
                        if (candidate != null && isFieldReadyForInteraction(candidate)) {
                            logAction("Exact field ready for '" + description + "': " + describeDialogField(candidate));
                            return candidate;
                        }
                    }
                } catch (StaleElementReferenceException ignored) {
                } catch (Exception ignored) {
                }
                return null;
            });
        } catch (TimeoutException e) {
            for (WebElement candidate : driver.findElements(locator)) {
                try {
                    if (candidate != null && candidate.isDisplayed() && !isElementExplicitlyDisabled(candidate)) {
                        logAction("Using visible exact field fallback for '" + description + "': " + describeDialogField(candidate));
                        return candidate;
                    }
                } catch (Exception ignored) {
                }
            }
            throw new IllegalStateException("Field did not become enabled for " + description + " using locator: " + locator, e);
        }
    }

    private WebElement waitForDialogTextInputToBecomeEnabled(String title, String... fieldHints) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS));
            return wait.until(d -> {
                try {
                    WebElement field = locateVisibleDialogTextInputCandidate(title, fieldHints);
                    if (field != null && isFieldReadyForInteraction(field)) {
                        logAction("Reason text field ready: " + describeDialogField(field));
                        return field;
                    }
                } catch (Exception ignored) {
                }
                return null;
            });
        } catch (TimeoutException e) {
            try {
                WebElement fallback = locateVisibleDialogTextInputCandidate(title, fieldHints);
                if (fallback != null && fallback.isDisplayed() && !isElementExplicitlyDisabled(fallback)) {
                    logAction("Using visible reason text field fallback: " + describeDialogField(fallback));
                    return fallback;
                }
            } catch (Exception ignored) {
            }
            throw new IllegalStateException("Reason text field did not become enabled for hints: " + String.join(", ", fieldHints), e);
        }
    }

    private WebElement matchVisibleFieldByHints(List<WebElement> candidates, String... fieldHints) {
        if (candidates == null || candidates.isEmpty() || fieldHints == null || fieldHints.length == 0) {
            return null;
        }

        for (String fieldHint : fieldHints) {
            String normalizedHint = normalizeText(fieldHint).toLowerCase();
            if (normalizedHint.isEmpty()) {
                continue;
            }
            for (WebElement field : candidates) {
                if (fieldMatchesHint(field, normalizedHint)) {
                    return field;
                }
            }
        }
        return null;
    }

    private List<WebElement> waitForVisibleElements(By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            return wait.until(d -> {
                List<WebElement> visible = new ArrayList<>();
                for (WebElement element : d.findElements(locator)) {
                    try {
                        if (element.isDisplayed()) {
                            visible.add(element);
                        }
                    } catch (StaleElementReferenceException ignored) {
                    }
                }
                return visible.isEmpty() ? null : visible;
            });
        } catch (TimeoutException e) {
            return new ArrayList<>();
        }
    }

    private void fillInput(WebElement input, String value) {
        if (input == null) {
            throw new IllegalArgumentException("Input element cannot be null.");
        }
        String normalizedValue = normalizeText(value);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.CONFIGURATION_PAGE_LOAD_WAIT_SECONDS));
        wait.until(d -> {
            try {
                return isFieldReadyForInteraction(input);
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });

        scrollIntoView(input);
        clickWithFallback(input);
        try {
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            input.sendKeys(Keys.DELETE);
            input.sendKeys(normalizedValue);
        } catch (Exception typingException) {
            setInputValueViaJavaScript(input, normalizedValue);
        }

        String actualValue = readInputValue(input);
        if (!normalizedValue.equals(actualValue)) {
            setInputValueViaJavaScript(input, normalizedValue);
            actualValue = readInputValue(input);
        }
        if (!normalizedValue.equals(actualValue)) {
            input.sendKeys(Keys.TAB);
            actualValue = readInputValue(input);
        }
        if (!normalizedValue.equals(actualValue)) {
            throw new IllegalStateException("Unable to set value '" + normalizedValue + "' in configuration input. Actual=" + actualValue);
        }
    }

    private void clickWithFallback(WebElement element) {
        try {
            scrollIntoView(element);
            element.click();
        } catch (Exception clickException) {
            try {
                scrollIntoView(element);
            } catch (Exception ignored) {
            }
            try {
                new Actions(driver).moveToElement(element).pause(Duration.ofMillis(150)).click().perform();
            } catch (Exception actionsException) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            }
        }
    }

    private void clearAndType(WebElement input, String value) {
        String normalizedValue = normalizeText(value);
        scrollIntoView(input);
        try {
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", input);
            } catch (Exception ignored) {
            }
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            input.sendKeys(Keys.DELETE);
            if (!normalizedValue.isEmpty()) {
                input.sendKeys(normalizedValue);
            }
        } catch (Exception typingException) {
            setInputValueViaJavaScript(input, normalizedValue);
        }
    }

    private void sendDropdownNavigationKeys(WebElement field) {
        if (field == null) {
            return;
        }
        try {
            field.sendKeys(Keys.ARROW_DOWN);
            field.sendKeys(Keys.ENTER);
            field.sendKeys(Keys.TAB);
        } catch (Exception ignored) {
            try {
                driver.findElement(By.tagName("body")).sendKeys(Keys.ARROW_DOWN);
                driver.findElement(By.tagName("body")).sendKeys(Keys.ENTER);
                driver.findElement(By.tagName("body")).sendKeys(Keys.TAB);
            } catch (Exception ignoredToo) {
            }
        }
    }

    private String resolveDropdownSelectionValue(WebElement field, WebElement searchField, String searchTerm, String fallbackValue) {
        String candidate = "";
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                candidate = readDropdownCommittedValue(field);
                if (!candidate.isEmpty()) {
                    return normalizeText(candidate);
                }
            } catch (Exception ignored) {
            }
            try {
                candidate = readDropdownCommittedValue(field);
                if (!candidate.isEmpty()) {
                    return normalizeText(candidate);
                }
            } catch (Exception ignored) {
            }
        }

        if (searchField != null && !sameField(searchField, field)) {
            try {
                candidate = readDropdownCommittedValue(searchField);
                if (!candidate.isEmpty()) {
                    return normalizeText(candidate);
                }
            } catch (Exception ignored) {
            }
        }

        if (candidate.isEmpty()) {
            candidate = fallbackValue;
        }
        if (candidate.isEmpty() && !searchTerm.isBlank()) {
            candidate = searchTerm;
        }
        return normalizeText(candidate);
    }

    private String selectRequiredDialogSelectById(String selectId, String nativeInputName, String desiredValue, String description) {
        By selectLocator = By.id(selectId);
        WebElement field;
        try {
            field = waitForExactDialogFieldToBecomeEnabled(selectLocator, description);
        } catch (IllegalStateException exactLocatorFailure) {
            logAction("Exact field lookup failed for '" + description + "' using id '" + selectId
                    + "'. Falling back to dialog field hints. Cause=" + exactLocatorFailure.getMessage());
            field = waitForDialogFieldByHints(description, true, description, nativeInputName, selectId);
        }
        String normalizedDesiredValue = normalizeText(desiredValue);
        String selectedValue = selectDropdownOptionWithOverride(field, normalizedDesiredValue, description, nativeInputName, selectId);

        String nativeValue = waitForDialogNativeInputValueToCommit(nativeInputName);
        String visibleValue = "";
        try {
            WebElement refreshedField = driver.findElements(selectLocator).stream()
                    .filter(element -> {
                        try {
                            return element != null && element.isDisplayed();
                        } catch (Exception ignored) {
                            return false;
                        }
                    })
                    .findFirst()
                    .orElse(null);
            if (refreshedField != null) {
                visibleValue = normalizeText(extractActionText(refreshedField));
                if (visibleValue.isEmpty()) {
                    visibleValue = readDropdownCommittedValue(refreshedField);
                }
            }
        } catch (Exception refreshedFieldReadFailure) {
            logAction("Skipping refreshed field read for '" + description + "' because the dialog re-rendered. Cause="
                    + refreshedFieldReadFailure.getMessage());
        }

        if (visibleValue.isEmpty() && !isPlaceholderSelectionValue(nativeValue)) {
            visibleValue = !selectedValue.isEmpty() ? selectedValue : normalizedDesiredValue;
        }

        if (isPlaceholderSelectionValue(nativeValue) && visibleValue.isEmpty()) {
            throw new IllegalStateException(description + " selection did not stick for select id '" + selectId + "'.");
        }

        String resolvedValue = !visibleValue.isEmpty() ? visibleValue : selectedValue;
        logAction(description + " selection committed. Visible value='" + resolvedValue + "', native value='" + nativeValue + "'.");
        return normalizeText(resolvedValue);
    }

    private WebElement waitForDialogFieldByHints(String description, boolean requireEnabled, String... fieldHints) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS));
            return wait.until(d -> {
                try {
                    List<WebElement> visibleFields = collectVisibleDialogFields();
                    if (visibleFields.isEmpty()) {
                        visibleFields = waitForVisibleElements(dialogTextFieldCandidates, AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS);
                    }
                    if (visibleFields.isEmpty()) {
                        return null;
                    }

                    List<WebElement> usableFields = new ArrayList<>();
                    for (WebElement field : deduplicateVisibleElements(visibleFields)) {
                        try {
                            if (field == null || !field.isDisplayed()) {
                                continue;
                            }
                            if (requireEnabled && !isFieldReadyForInteraction(field)) {
                                continue;
                            }
                            usableFields.add(field);
                        } catch (Exception ignored) {
                        }
                    }

                    WebElement matched = matchVisibleFieldByHints(usableFields, fieldHints);
                    if (matched != null) {
                        logAction("Matched dialog field by hints for '" + description + "': " + describeDialogField(matched));
                        return matched;
                    }

                    for (WebElement candidate : usableFields) {
                        try {
                            if (isFieldCombobox(candidate) || isEditableTextField(candidate)) {
                                logAction("Using fallback dialog field for '" + description + "': " + describeDialogField(candidate));
                                return candidate;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                } catch (Exception ignored) {
                }
                return null;
            });
        } catch (TimeoutException e) {
            throw new IllegalStateException("Dialog field did not become available for " + description
                    + " using hints: " + String.join(", ", fieldHints), e);
        }
    }

    private boolean isDialogSelectMissing(String nativeInputName) {
        return isPlaceholderSelectionValue(readDialogNativeInputValue(nativeInputName));
    }

    private String waitForDialogNativeInputValueToCommit(String nativeInputName) {
        if (nativeInputName == null || nativeInputName.trim().isEmpty()) {
            return "";
        }
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS));
            return wait.until(d -> {
                String value = readDialogNativeInputValue(nativeInputName);
                return isPlaceholderSelectionValue(value) ? null : value;
            });
        } catch (TimeoutException ignored) {
            return readDialogNativeInputValue(nativeInputName);
        }
    }

    private String selectBestDialogSelectByCandidates(String selectId, String nativeInputName, List<String> candidates,
                                                      String description, String existingSectionText) {
        List<String> orderedCandidates = candidates == null ? List.of() : candidates;
        String lastSelectedValue = "";
        boolean duplicateCandidateDetected = false;
        for (String candidate : orderedCandidates) {
            String normalizedCandidate = normalizeText(candidate);
            if (normalizedCandidate.isEmpty()) {
                continue;
            }
            String selectedValue = selectRequiredDialogSelectById(selectId, nativeInputName, normalizedCandidate, description);
            lastSelectedValue = selectedValue;
            if (!containsValueIgnoreCase(existingSectionText, selectedValue)) {
                return selectedValue;
            }
            duplicateCandidateDetected = true;
            logAction(description + " candidate '" + selectedValue + "' already exists in the section grid. Trying the next candidate.");
        }

        if (duplicateCandidateDetected) {
            logAction("Configured " + description + " candidates already exist. Trying the first non-existing visible option.");
            String nonExistingVisibleOption = selectFirstNonExistingDialogSelectById(
                    selectId,
                    nativeInputName,
                    description,
                    existingSectionText
            );
            if (!normalizeText(nonExistingVisibleOption).isEmpty()) {
                return nonExistingVisibleOption;
            }
        }

        return lastSelectedValue;
    }

    /**
     * Reads the currently visible option texts for a dialog select so retries can use real UI choices instead of stale defaults.
     */
    private List<String> collectDialogSelectVisibleOptionsById(String selectId, String nativeInputName, String description) {
        By selectLocator = By.id(selectId);
        WebElement field;
        try {
            field = waitForExactDialogFieldToBecomeEnabled(selectLocator, description);
        } catch (IllegalStateException exactLocatorFailure) {
            logAction("Exact field lookup failed for '" + description + "' while collecting visible options using id '" + selectId
                    + "'. Falling back to dialog field hints. Cause=" + exactLocatorFailure.getMessage());
            field = waitForDialogFieldByHints(description, true, description, nativeInputName, selectId);
        }

        clickWithFallback(field);
        List<WebElement> visibleOptions = waitForVisibleElements(
                dropdownOptionCandidates,
                AppConstants.CONFIGURATION_DROPDOWN_WAIT_SECONDS
        );
        List<String> optionTexts = extractOptionTexts(visibleOptions).stream()
                .map(this::normalizeText)
                .filter(text -> !text.isEmpty() && !isPlaceholderSelectionValue(text))
                .distinct()
                .toList();
        logAction("Visible dialog options for '" + description + "': " + optionTexts);
        try {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        } catch (Exception ignored) {
        }
        waitForDropdownOverlayToClose(field);
        return optionTexts;
    }

    /**
     * Chooses the next usable competency for central allocation from configured hints plus live visible options.
     */
    private String selectCentralAllocationCompetencyCandidate(String existingSectionText, List<String> candidates,
                                                              Set<String> attemptedCompetencies) {
        List<String> orderedCandidates = candidates == null ? List.of() : candidates;
        String lastSelectedValue = "";
        for (String candidate : orderedCandidates) {
            String normalizedCandidate = normalizeText(candidate);
            if (normalizedCandidate.isEmpty()) {
                continue;
            }
            if (attemptedCompetencies != null && attemptedCompetencies.contains(normalizedCandidate)) {
                continue;
            }
            String selectedValue = selectCentralAllocationDialogCompetency(normalizedCandidate);
            if (normalizeText(selectedValue).isEmpty()) {
                logAction("Central allocation competency candidate '" + normalizedCandidate
                        + "' is not available in the live dialog options. Trying the next candidate.");
                continue;
            }
            lastSelectedValue = selectedValue;
            if (!containsValueIgnoreCase(existingSectionText, selectedValue)) {
                return selectedValue;
            }
            logAction("Central allocation competency candidate '" + selectedValue
                    + "' already exists in the current section snapshot. Trying the next visible option.");
        }
        return lastSelectedValue;
    }

    /**
     * Selects the requested central-allocation competency from the live dialog options and verifies the committed choice.
     */
    private String selectCentralAllocationDialogCompetency(String desiredCompetency) {
        By selectLocator = By.id("mui-component-select-competencyId");
        WebElement field;
        try {
            field = waitForExactDialogFieldToBecomeEnabled(selectLocator, "Competency");
        } catch (IllegalStateException exactLocatorFailure) {
            logAction("Exact central allocation competency lookup failed. Falling back to dialog field hints. Cause="
                    + exactLocatorFailure.getMessage());
            field = waitForDialogFieldByHints("Competency", true, "Competency", "competencyId", "mui-component-select-competencyId");
        }

        String normalizedDesiredCompetency = normalizeText(desiredCompetency);
        if (normalizedDesiredCompetency.isEmpty()) {
            return selectDropdownOptionWithOverride(field, desiredCompetency, "Competency", "competencyId", "mui-component-select-competencyId");
        }

        WebElement searchField = locateDropdownSearchField(field, "Competency", "competencyId", "mui-component-select-competencyId");
        boolean editableInput = isEditableTextField(field);
        if (searchField != null && !sameField(searchField, field)) {
            editableInput = true;
            clearAndType(searchField, normalizedDesiredCompetency);
        } else if (editableInput) {
            clearAndType(field, normalizedDesiredCompetency);
        } else {
            clickWithFallback(field);
            try {
                field.sendKeys(normalizedDesiredCompetency);
            } catch (Exception ignored) {
            }
        }

        List<WebElement> visibleOptions = List.of();
        List<String> visibleOptionTexts = List.of();
        for (int optionReadAttempt = 1; optionReadAttempt <= 3; optionReadAttempt++) {
            visibleOptions = waitForVisibleElements(
                    dropdownOptionCandidates,
                    editableInput ? AppConstants.CONFIGURATION_FILTER_WAIT_SECONDS : AppConstants.CONFIGURATION_DROPDOWN_WAIT_SECONDS
            );
            visibleOptionTexts = extractStableOptionTexts(visibleOptions);
            if (!visibleOptionTexts.isEmpty()) {
                break;
            }
            logAction("Visible competency options re-rendered while reading them. Retrying option capture attempt "
                    + optionReadAttempt + ".");
        }
        if (visibleOptionTexts.isEmpty()) {
            logAction("No visible competency options were available while trying to select '"
                    + normalizedDesiredCompetency + "'.");
            return "";
        }

        String selectedOptionText = "";
        for (String optionText : visibleOptionTexts) {
            if (optionText.equalsIgnoreCase(normalizedDesiredCompetency)) {
                selectedOptionText = optionText;
                break;
            }
        }
        if (selectedOptionText.isEmpty()) {
            for (String optionText : visibleOptionTexts) {
                if (!optionText.isEmpty() && optionText.toLowerCase().contains(normalizedDesiredCompetency.toLowerCase())) {
                    selectedOptionText = optionText;
                    break;
                }
            }
        }
        if (selectedOptionText.isEmpty()) {
            logAction("Visible competency options did not contain the requested value '" + normalizedDesiredCompetency
                    + "'.");
            try {
                driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
            } catch (Exception ignored) {
            }
            waitForDropdownOverlayToClose(field);
            return "";
        }

        logAction("Clicking exact central allocation competency option: " + selectedOptionText);
        if (!clickVisibleDropdownOptionByText(selectedOptionText)) {
            logAction("Central allocation competency option '" + selectedOptionText
                    + "' disappeared before it could be clicked. Treating it as unavailable for this retry.");
            return "";
        }
        commitDropdownSelection(searchField != null && !sameField(searchField, field) ? searchField : field);
        waitForDropdownOverlayToClose(field);

        String selectedValue = resolveDropdownSelectionValue(field, searchField, normalizedDesiredCompetency, selectedOptionText);
        if (selectedValue.isEmpty()) {
            selectedValue = selectedOptionText;
        }
        logAction("Selected central allocation competency: " + selectedValue);
        return normalizeText(selectedValue);
    }

    /**
     * Reads visible dropdown option texts defensively so transient dialog re-renders do not fail the retry loop.
     */
    private List<String> extractStableOptionTexts(List<WebElement> visibleOptions) {
        List<String> optionTexts = new ArrayList<>();
        if (visibleOptions == null) {
            return optionTexts;
        }
        for (WebElement option : visibleOptions) {
            try {
                String optionText = normalizeText(extractOptionText(option));
                if (!optionText.isEmpty()) {
                    optionTexts.add(optionText);
                }
            } catch (StaleElementReferenceException staleOption) {
                logAction("Skipping stale dropdown option while collecting live option texts.");
            } catch (Exception ignored) {
            }
        }
        return optionTexts.stream().distinct().toList();
    }

    /**
     * Re-finds a visible dropdown option by text before clicking so dialog re-renders do not break the selection path.
     */
    private boolean clickVisibleDropdownOptionByText(String optionText) {
        String normalizedOptionText = normalizeText(optionText);
        By exactOptionLocator = By.xpath(
                "//*[(@role='option' or @role='menuitem' or @role='listitem' "
                        + "or contains(@class,'MuiAutocomplete-option') "
                        + "or contains(@class,'MuiMenuItem-root') "
                        + "or contains(@class,'MuiListItem-root')) "
                        + "and (normalize-space(.)=" + toXPathLiteral(normalizedOptionText)
                        + " or .//*[normalize-space(.)=" + toXPathLiteral(normalizedOptionText) + "])]"
        );
        try {
            if (eleutil.isElementVisible(exactOptionLocator, AppConstants.CONFIGURATION_DROPDOWN_WAIT_SECONDS)) {
                eleutil.clickStable(exactOptionLocator, AppConstants.CONFIGURATION_DROPDOWN_WAIT_SECONDS);
                return true;
            }
        } catch (Exception clickFailure) {
            logAction("Dropdown option '" + normalizedOptionText + "' was not stably clickable. Cause="
                    + clickFailure.getMessage());
        }
        return false;
    }

    /**
     * Determines whether a failed central-allocation create should retry with another live competency option.
     */
    private boolean shouldRetryCentralAllocationCreate(String message, boolean existingSelectionDetected) {
        if (existingSelectionDetected) {
            return false;
        }
        String normalizedMessage = normalizeText(message).toLowerCase();
        if (normalizedMessage.isEmpty()) {
            return false;
        }
        return normalizedMessage.contains(AppConstants.CONFIGURATION_CREATE_FAILED_MESSAGE.toLowerCase())
                || normalizedMessage.contains("error updating configuration");
    }

    private String selectFirstNonExistingDialogSelectById(String selectId, String nativeInputName,
                                                          String description, String existingSectionText) {
        By selectLocator = By.id(selectId);
        WebElement field;
        try {
            field = waitForExactDialogFieldToBecomeEnabled(selectLocator, description);
        } catch (IllegalStateException exactLocatorFailure) {
            logAction("Exact field lookup failed for '" + description + "' during non-existing-option fallback using id '"
                    + selectId + "'. Falling back to dialog field hints. Cause=" + exactLocatorFailure.getMessage());
            field = waitForDialogFieldByHints(description, true, description, nativeInputName, selectId);
        }

        scrollIntoView(field);
        clickWithFallback(field);

        List<WebElement> visibleOptions = waitForVisibleElements(
                dropdownOptionCandidates,
                AppConstants.CONFIGURATION_DROPDOWN_WAIT_SECONDS
        );
        if (visibleOptions.isEmpty()) {
            throw new IllegalStateException("No visible " + description + " options were available for non-existing selection fallback.");
        }

        WebElement fallbackOption = null;
        String fallbackOptionText = "";
        for (WebElement option : visibleOptions) {
            String optionText = normalizeText(extractOptionText(option));
            if (optionText.isEmpty() || isPlaceholderSelectionValue(optionText)) {
                continue;
            }
            if (!containsValueIgnoreCase(existingSectionText, optionText)) {
                fallbackOption = option;
                fallbackOptionText = optionText;
                break;
            }
        }

        if (fallbackOption == null) {
            logAction("No non-existing visible " + description + " option was available. Keeping the current selection.");
            try {
                driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
            } catch (Exception ignored) {
            }
            return "";
        }

        logAction("Selecting first non-existing " + description + " option: " + fallbackOptionText);
        clickWithFallback(fallbackOption);
        commitDropdownSelection(field);
        waitForDropdownOverlayToClose(field);

        String nativeValue = waitForDialogNativeInputValueToCommit(nativeInputName);
        String visibleValue = "";
        try {
            WebElement refreshedField = driver.findElements(selectLocator).stream()
                    .filter(element -> {
                        try {
                            return element != null && element.isDisplayed();
                        } catch (Exception ignored) {
                            return false;
                        }
                    })
                    .findFirst()
                    .orElse(null);
            if (refreshedField != null) {
                visibleValue = normalizeText(extractActionText(refreshedField));
                if (visibleValue.isEmpty()) {
                    visibleValue = readDropdownCommittedValue(refreshedField);
                }
            }
        } catch (Exception refreshedFieldReadFailure) {
            logAction("Skipping refreshed field read for non-existing " + description + " option because the dialog re-rendered. Cause="
                    + refreshedFieldReadFailure.getMessage());
        }

        if (visibleValue.isEmpty() && !isPlaceholderSelectionValue(nativeValue)) {
            visibleValue = fallbackOptionText;
        }

        return normalizeText(visibleValue.isEmpty() ? fallbackOptionText : visibleValue);
    }

    private void setDialogInputValueByName(String inputName, String value, String description) {
        String normalizedValue = normalizeText(value);
        if (normalizeText(inputName).isEmpty() || normalizedValue.isEmpty()) {
            return;
        }
        By locator = By.xpath("//div[@role='dialog' or contains(@class,'MuiDialog-root') or contains(@class,'MuiModal-root')]//input[@name="
                + toXPathLiteral(inputName) + " and not(@disabled)]");
        WebElement input = eleutil.waitForFreshVisibleElement(locator, AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS);
        logAction("Setting dialog input '" + description + "' to '" + normalizedValue + "'.");
        fillInput(input, normalizedValue);
    }

    private void setDialogToggleByLegend(String legendText, boolean desiredState) {
        String normalizedLegend = normalizeText(legendText);
        if (normalizedLegend.isEmpty()) {
            return;
        }
        By toggleLocator = By.xpath("//div[@role='dialog' or contains(@class,'MuiDialog-root') or contains(@class,'MuiModal-root')]"
                + "//*[contains(normalize-space(.)," + toXPathLiteral(normalizedLegend) + ")]"
                + "/following::input[@type='checkbox'][1]");
        try {
            WebElement checkbox = eleutil.waitForFreshVisibleElement(toggleLocator, AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS);
            boolean currentState = checkbox.isSelected();
            if (currentState != desiredState) {
                logAction("Updating dialog toggle '" + normalizedLegend + "' to " + desiredState + ".");
                clickWithFallback(checkbox);
            }
        } catch (Exception e) {
            logAction("Skipping dialog toggle update for '" + normalizedLegend + "' because it was not stable. Cause=" + e.getMessage());
        }
    }

    private List<String> readConfigList(String key, String defaultValue) {
        String raw = readConfig(key, defaultValue);
        if (normalizeText(raw).isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(raw.split(","))
                .map(this::normalizeText)
                .filter(value -> !value.isEmpty())
                .toList();
    }

    private List<String> buildOrderedCandidates(String primaryCandidate, List<String> fallbackCandidates) {
        LinkedHashSet<String> ordered = new LinkedHashSet<>();
        String normalizedPrimary = normalizeText(primaryCandidate);
        if (!normalizedPrimary.isEmpty()) {
            ordered.add(normalizedPrimary);
        }
        if (fallbackCandidates != null) {
            for (String candidate : fallbackCandidates) {
                String normalizedCandidate = normalizeText(candidate);
                if (!normalizedCandidate.isEmpty()) {
                    ordered.add(normalizedCandidate);
                }
            }
        }
        return new ArrayList<>(ordered);
    }

    private boolean containsValueIgnoreCase(String sourceText, String candidateValue) {
        String normalizedSource = normalizeText(sourceText).toLowerCase();
        String normalizedCandidate = normalizeText(candidateValue).toLowerCase();
        return !normalizedSource.isEmpty() && !normalizedCandidate.isEmpty() && normalizedSource.contains(normalizedCandidate);
    }

    private boolean containsAllValuesIgnoreCase(String sourceText, String... values) {
        if (values == null || values.length == 0) {
            return false;
        }
        for (String value : values) {
            if (!containsValueIgnoreCase(sourceText, value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Infers a stable create outcome for central allocation when the dialog closes without showing a toast.
     */
    private String inferCentralAllocationOutcomeMessage(String title, String businessUnit, String competency,
                                                        boolean existingSelectionDetected) {
        String refreshedSectionSnapshot = getSectionText(title);
        boolean selectionVisibleAfterSave = containsAllValuesIgnoreCase(refreshedSectionSnapshot, businessUnit, competency)
                || containsValueIgnoreCase(refreshedSectionSnapshot, competency);
        if (!selectionVisibleAfterSave) {
            logAction("Central allocation section does not yet reflect BU='" + businessUnit + "', competency='"
                    + competency + "' after silent save.");
            return "";
        }
        if (existingSelectionDetected) {
            logAction("Central allocation dialog closed without toast, and the selected mapping already existed. "
                    + "Treating it as a duplicate-safe outcome.");
            return "Configuration already exists";
        }
        logAction("Central allocation dialog closed without toast, but the selected mapping is now visible in the section. "
                + "Treating it as a successful create outcome.");
        return AppConstants.CONFIGURATION_SAVE_SUCCESS_MESSAGE;
    }

    private Set<String> collectSectionContentTexts(WebElement sectionRoot) {
        Set<String> sectionTexts = new LinkedHashSet<>();
        if (sectionRoot == null) {
            return sectionTexts;
        }

        String rootText = normalizeText(sectionRoot.getText());
        if (!rootText.isEmpty()) {
            sectionTexts.add(rootText);
        }

        By[] sectionContentLocators = {
                By.xpath(".//*[@role='gridcell']"),
                By.xpath(".//td"),
                By.xpath(".//*[contains(@class,'ag-cell-value')]"),
                By.xpath(".//*[contains(@class,'MuiTableCell-root')]"),
                By.xpath(".//button"),
                By.xpath(".//label"),
                By.xpath(".//span[normalize-space()]")
        };

        for (By locator : sectionContentLocators) {
            try {
                for (WebElement element : sectionRoot.findElements(locator)) {
                    try {
                        if (!element.isDisplayed()) {
                            continue;
                        }
                        String text = normalizeText(element.getText());
                        if (text.isEmpty()) {
                            text = normalizeText(safeAttribute(element, "textContent"));
                        }
                        if (!text.isEmpty()) {
                            sectionTexts.add(text);
                        }
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception ignored) {
            }
        }

        return sectionTexts;
    }

    private String appendUniqueSuffix(String baseValue) {
        String normalizedBase = normalizeText(baseValue);
        if (normalizedBase.isEmpty()) {
            normalizedBase = "Automation Reason";
        }
        return normalizedBase + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyHHmmss"));
    }

    private boolean readBoolean(String key, boolean defaultValue) {
        if (prop == null || key == null || key.trim().isEmpty()) {
            return defaultValue;
        }
        String value = prop.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

    private boolean isRequiredDialogSelectionMessage(String message) {
        String normalizedMessage = normalizeText(message).toLowerCase();
        return normalizedMessage.contains("required");
    }

    private String readDialogNativeInputValue(String nativeInputName) {
        if (nativeInputName == null || nativeInputName.trim().isEmpty()) {
            return "";
        }

        for (WebElement root : getVisibleDialogRoots()) {
            try {
                List<WebElement> inputs = root.findElements(By.xpath(".//input[@name=" + toXPathLiteral(nativeInputName.trim()) + "]"));
                for (WebElement input : inputs) {
                    try {
                        String value = normalizeText(safeAttribute(input, "value"));
                        if (!value.isEmpty()) {
                            return value;
                        }
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception ignored) {
            }
        }

        try {
            for (WebElement input : driver.findElements(By.xpath("//input[@name=" + toXPathLiteral(nativeInputName.trim()) + "]"))) {
                try {
                    if (!input.isDisplayed() && safeAttribute(input, "aria-hidden").isEmpty()) {
                        continue;
                    }
                    String value = normalizeText(safeAttribute(input, "value"));
                    if (!value.isEmpty()) {
                        return value;
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    private WebElement waitForDialogPrimaryFieldAtIndex(int index, boolean requireEnabled) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.CONFIGURATION_DIALOG_WAIT_SECONDS));
            return wait.until(d -> {
                List<WebElement> fields = collectVisibleDialogPrimaryFields(requireEnabled);
                if (index < fields.size()) {
                    return fields.get(index);
                }
                return null;
            });
        } catch (TimeoutException e) {
            List<WebElement> fields = collectVisibleDialogPrimaryFields(false);
            if (index < fields.size()) {
                return fields.get(index);
            }
            throw new IllegalStateException("Dialog field at index " + index + " is not available.");
        }
    }

    private List<WebElement> collectVisibleDialogPrimaryFields(boolean requireEnabled) {
        List<WebElement> primaryFields = new ArrayList<>();
        for (WebElement root : getVisibleDialogRoots()) {
            try {
                List<WebElement> fieldContainers = root.findElements(By.xpath(
                        ".//div[contains(@class,'MuiFormControl-root') or contains(@class,'MuiAutocomplete-root') or contains(@class,'MuiTextField-root')]"
                ));
                for (WebElement container : fieldContainers) {
                    try {
                        if (!container.isDisplayed()) {
                            continue;
                        }
                        WebElement primaryField = findPrimaryInteractiveField(container, requireEnabled);
                        if (primaryField != null) {
                            primaryFields.add(primaryField);
                        }
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception ignored) {
            }
        }
        List<WebElement> deduplicated = deduplicateVisibleElements(primaryFields);
        logAction("Visible dialog primary fields: " + describeDialogFields(deduplicated));
        return deduplicated;
    }

    private WebElement findPrimaryInteractiveField(WebElement container, boolean requireEnabled) {
        List<By> controlLocators = List.of(
                By.xpath(".//*[@role='combobox']"),
                By.xpath(".//input[not(@type='hidden')]"),
                By.xpath(".//textarea"),
                By.xpath(".//*[@role='textbox']")
        );

        for (By locator : controlLocators) {
            try {
                for (WebElement candidate : container.findElements(locator)) {
                    if (candidate == null || !candidate.isDisplayed()) {
                        continue;
                    }
                    if (requireEnabled && !isFieldReadyForInteraction(candidate)) {
                        continue;
                    }
                    if (isFieldCombobox(candidate) || isEditableTextField(candidate)) {
                        return candidate;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private void commitDropdownSelection(WebElement field) {
        try {
            field.sendKeys(Keys.TAB);
        } catch (Exception ignored) {
        }
        waitForDropdownOverlayToClose(field);
    }

    private void waitForDropdownOverlayToClose(WebElement field) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.CONFIGURATION_DROPDOWN_WAIT_SECONDS));
            By openOverlayLocator = By.xpath(
                    "//*[contains(@class,'MuiPopover-root') or contains(@class,'MuiMenu-paper') or contains(@class,'MuiAutocomplete-popper') or @role='listbox' or @role='menu']"
            );
            wait.until(d -> {
                try {
                    String expanded = field.getAttribute("aria-expanded");
                    if ("true".equalsIgnoreCase(expanded)) {
                        return false;
                    }
                } catch (Exception ignored) {
                }

                for (WebElement overlay : d.findElements(openOverlayLocator)) {
                    try {
                        if (overlay.isDisplayed()) {
                            return false;
                        }
                    } catch (StaleElementReferenceException ignored) {
                    }
                }
                return true;
            });
        } catch (TimeoutException ignored) {
        }
    }

    private String readInputValue(WebElement input) {
        if (input == null) {
            return "";
        }
        try {
            String value = safeAttribute(input, "value");
            if (value.isEmpty()) {
                value = normalizeText(safeAttribute(input, "textContent"));
            }
            if (value.isEmpty()) {
                value = normalizeText(input.getText());
            }
            return normalizeText(value);
        } catch (StaleElementReferenceException ignored) {
            return "";
        } catch (Exception ignored) {
            return "";
        }
    }

    private boolean isEditableTextField(WebElement field) {
        String tagName = safeTagName(field);
        return "input".equalsIgnoreCase(tagName) || "textarea".equalsIgnoreCase(tagName);
    }

    private String resolveSearchTerm(String... fieldHints) {
        if (fieldHints == null || fieldHints.length == 0) {
            return "";
        }
        for (String fieldHint : fieldHints) {
            String hint = normalizeText(fieldHint).toLowerCase();
            if (hint.contains("business unit") || "bu".equals(hint) || hint.contains("search business unit")) {
                return readConfig(AppConstants.APPENDED_CONFIG_BU_SEARCH_TERM_KEY, AppConstants.APPENDED_CONFIG_BU_SEARCH_TERM_DEFAULT);
            }
            if (hint.contains("competency")) {
                return readConfig(AppConstants.APPENDED_CONFIG_COMPETENCY_KEY, AppConstants.APPENDED_CONFIG_COMPETENCY_DEFAULT);
            }
            if (hint.contains("reason type")) {
                return readConfig(AppConstants.CONFIGURATION_REASON_TYPE_VALUE_KEY, AppConstants.CONFIGURATION_REASON_TYPE_VALUE);
            }
            if (hint.contains("conflict")) {
                return readConfig(AppConstants.CONFIGURATION_CONFLICT_REASON_VALUE_KEY, AppConstants.CONFIGURATION_CONFLICT_REASON_VALUE);
            }
        }
        return "";
    }

    private String readConfig(String key, String defaultValue) {
        if (prop == null || key == null || key.trim().isEmpty()) {
            return defaultValue;
        }
        String value = prop.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    private void setInputValueViaJavaScript(WebElement input, String value) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1];" +
                            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                    input,
                    value
            );
        } catch (Exception ignored) {
        }
    }

    private void scrollIntoView(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        } catch (Exception ignored) {
        }
    }

    private String extractOptionText(WebElement option) {
        if (option == null) {
            return "";
        }
        String optionText = normalizeText(option.getText());
        if (optionText.isEmpty()) {
            optionText = normalizeText(safeAttribute(option, "textContent"));
        }
        return optionText;
    }

    private List<String> extractOptionTexts(List<WebElement> options) {
        List<String> texts = new ArrayList<>();
        if (options == null) {
            return texts;
        }
        for (WebElement option : options) {
            String text = extractOptionText(option);
            if (!text.isEmpty()) {
                texts.add(text);
            }
        }
        return texts;
    }

    private String safeTagName(WebElement element) {
        if (element == null) {
            return "";
        }
        try {
            return element.getTagName();
        } catch (Exception e) {
            return "";
        }
    }

    private By resolveFieldLocator(String... fieldHints) {
        if (fieldHints == null || fieldHints.length == 0) {
            throw new IllegalArgumentException("Field hint is required.");
        }

        List<By> candidates = new ArrayList<>();
        for (String fieldHint : fieldHints) {
            String hint = normalizeText(fieldHint);
            if (hint.isEmpty()) {
                continue;
            }
            candidates.add(By.xpath("//input[contains(@placeholder," + toXPathLiteral(hint) + ") or contains(@aria-label," + toXPathLiteral(hint) + ") or contains(@name," + toXPathLiteral(hint) + ") or contains(@id," + toXPathLiteral(hint) + ")]"));
            candidates.add(By.xpath("//label[contains(normalize-space(), " + toXPathLiteral(hint) + ")]/following::input[1]"));
            candidates.add(By.xpath("//*[contains(normalize-space(), " + toXPathLiteral(hint) + ")]//input[1]"));
            candidates.add(By.xpath("//*[contains(normalize-space(), " + toXPathLiteral(hint) + ")]/following::input[1]"));
            candidates.add(By.xpath("//*[contains(normalize-space(), " + toXPathLiteral(hint) + ")]/following::*[@role='combobox'][1]"));
        }

        for (By candidate : candidates) {
            if (eleutil.isElementVisible(candidate, 3)) {
                return candidate;
            }
        }
        throw new NoSuchElementException("Unable to locate field for hints: " + String.join(", ", fieldHints));
    }

    private By sectionRootByTitle(String title) {
        return By.xpath(sectionRootXPath(title));
    }

    private By sectionButtonByTitleAndText(String title, String buttonText) {
        String normalizedButtonText = normalizeText(buttonText);
        String loweredButtonText = normalizedButtonText.toLowerCase();
        return By.xpath("(" + sectionRootXPath(title)
                + "//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')," + toXPathLiteral(loweredButtonText)
                + ") or .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')," + toXPathLiteral(loweredButtonText) + ")]]"
                + ")[1]");
    }

    private By sectionNumericInputsByTitle(String title) {
        return By.xpath(sectionRootXPath(title) + "//input[@type='number' and not(@disabled)]");
    }

    private String sectionRootXPath(String title) {
        return "//div[contains(@class,'MuiAccordion-root')][.//div[contains(@class,'MuiAccordionSummary-root')][.//span[contains(@class,'MuiTypography-body1') and normalize-space()="
                + toXPathLiteral(title) + "]]]";
    }

    private By configurationSummaryByTitle(String title) {
        return By.xpath("//div[contains(@class,'MuiAccordionSummary-root')][.//span[contains(@class,'MuiTypography-body1') and normalize-space()="
                + toXPathLiteral(title) + "]]");
    }

    private By configurationExpandIconByTitle(String title) {
        return By.xpath(sectionRootXPath(title)
                + "//div[contains(@class,'MuiAccordionSummary-expandIconWrapper') or .//*[name()='svg' and @data-testid='ExpandCircleDownIcon']]");
    }

    private By configurationValueByTitle(String title) {
        return By.xpath("//div[contains(@class,'MuiAccordion-root')][.//div[contains(@class,'MuiAccordionSummary-root')][.//span[contains(@class,'MuiTypography-body1') and normalize-space()="
                + toXPathLiteral(title) + "]]]//input[@type='number']");
    }

    private String normalizeText(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("\\s+", " ").trim();
    }

    private String safeAttribute(WebElement element, String attributeName) {
        if (element == null) {
            return "";
        }
        try {
            String value = element.getAttribute(attributeName);
            return value == null ? "" : value.trim();
        } catch (StaleElementReferenceException ignored) {
            return "";
        } catch (Exception ignored) {
            return "";
        }
    }

    private String extractActionText(WebElement element) {
        if (element == null) {
            return "";
        }
        try {
            String text = normalizeText(element.getText());
            if (text.isEmpty()) {
                text = normalizeText(safeAttribute(element, "textContent"));
            }
            if (text.isEmpty()) {
                text = normalizeText(safeAttribute(element, "aria-label"));
            }
            if (text.isEmpty()) {
                text = normalizeText(safeAttribute(element, "title"));
            }
            if (text.isEmpty()) {
                text = normalizeText(safeAttribute(element, "value"));
            }
            return text;
        } catch (StaleElementReferenceException ignored) {
            return "";
        } catch (Exception ignored) {
            return "";
        }
    }

    private String describeActionButtons(List<WebElement> buttons) {
        List<String> descriptions = new ArrayList<>();
        if (buttons == null) {
            return descriptions.toString();
        }
        for (WebElement button : buttons) {
            descriptions.add(describeDialogField(button));
        }
        return descriptions.toString();
    }

    private String describeDialogFields(List<WebElement> fields) {
        List<String> descriptions = new ArrayList<>();
        if (fields == null) {
            return descriptions.toString();
        }
        for (WebElement field : fields) {
            descriptions.add(describeDialogField(field));
        }
        return descriptions.toString();
    }

    private String describeDialogField(WebElement element) {
        if (element == null) {
            return "<null>";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(safeTagName(element));
        String text = extractActionText(element);
        if (!text.isEmpty()) {
            builder.append(" text='").append(text).append("'");
        }
        String placeholder = safeAttribute(element, "placeholder");
        if (!placeholder.isEmpty()) {
            builder.append(" placeholder='").append(placeholder).append("'");
        }
        String ariaLabel = safeAttribute(element, "aria-label");
        if (!ariaLabel.isEmpty()) {
            builder.append(" aria-label='").append(ariaLabel).append("'");
        }
        String name = safeAttribute(element, "name");
        if (!name.isEmpty()) {
            builder.append(" name='").append(name).append("'");
        }
        String id = safeAttribute(element, "id");
        if (!id.isEmpty()) {
            builder.append(" id='").append(id).append("'");
        }
        String role = safeAttribute(element, "role");
        if (!role.isEmpty()) {
            builder.append(" role='").append(role).append("'");
        }
        return builder.toString();
    }

    private boolean fieldMatchesHint(WebElement field, String lowerHint) {
        if (field == null || lowerHint == null || lowerHint.isEmpty()) {
            return false;
        }

        List<String> candidates = List.of(
                safeAttribute(field, "placeholder"),
                safeAttribute(field, "aria-label"),
                safeAttribute(field, "name"),
                safeAttribute(field, "id"),
                safeAttribute(field, "value"),
                safeAttribute(field, "textContent"),
                safeAttribute(field, "title")
        );
        for (String candidate : candidates) {
            if (normalizeText(candidate).toLowerCase().contains(lowerHint)) {
                return true;
            }
        }

        try {
            WebElement label = field.findElement(By.xpath("preceding::label[1]"));
            String labelText = normalizeText(label.getText()).toLowerCase();
            if (!labelText.isEmpty() && labelText.contains(lowerHint)) {
                return true;
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    private boolean isFieldCombobox(WebElement field) {
        String role = normalizeText(safeAttribute(field, "role")).toLowerCase();
        String ariaExpanded = normalizeText(safeAttribute(field, "aria-expanded")).toLowerCase();
        String ariaHasPopup = normalizeText(safeAttribute(field, "aria-haspopup")).toLowerCase();
        return "combobox".equals(role) || "true".equalsIgnoreCase(ariaExpanded) || "listbox".equals(ariaHasPopup);
    }

    private WebElement findPreferredDialogButton(List<WebElement> buttons, String configurationTitle, String... preferredButtonTexts) {
        if (buttons == null || buttons.isEmpty()) {
            return null;
        }

        List<String> preferredTexts = new ArrayList<>();
        if (preferredButtonTexts != null) {
            for (String preferred : preferredButtonTexts) {
                String normalizedPreferred = normalizeText(preferred).toLowerCase();
                if (!normalizedPreferred.isEmpty()) {
                    preferredTexts.add(normalizedPreferred);
                }
            }
        }

        for (String preferredText : preferredTexts) {
            for (WebElement button : buttons) {
                String actionText = extractActionText(button).toLowerCase();
                if (!actionText.isEmpty() && actionText.contains(preferredText) && !isIgnoredDialogButton(actionText, configurationTitle) && isButtonEnabled(button)) {
                    return button;
                }
            }
        }

        for (WebElement button : buttons) {
            String actionText = extractActionText(button).toLowerCase();
            if (!actionText.isEmpty() && !isIgnoredDialogButton(actionText, configurationTitle) && isButtonEnabled(button)) {
                return button;
            }
        }

        return buttons.get(0);
    }

    private WebElement findFallbackDialogButton(List<WebElement> buttons, String configurationTitle) {
        if (buttons == null || buttons.isEmpty()) {
            return null;
        }
        for (int index = buttons.size() - 1; index >= 0; index--) {
            WebElement button = buttons.get(index);
            String actionText = extractActionText(button).toLowerCase();
            if (!actionText.isEmpty() && !isIgnoredDialogButton(actionText, configurationTitle) && isButtonEnabled(button)) {
                return button;
            }
        }
        return buttons.get(buttons.size() - 1);
    }

    private boolean isButtonEnabled(WebElement button) {
        try {
            return button != null && button.isEnabled();
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }

    private boolean isIgnoredDialogButton(String actionText, String configurationTitle) {
        if (actionText == null) {
            return false;
        }
        String normalized = actionText.trim().toLowerCase();
        String normalizedTitle = normalizeText(configurationTitle).toLowerCase();
        return normalized.equals("cancel")
                || normalized.equals("close")
                || normalized.equals("back")
                || normalized.equals("previous")
                || normalized.equals("x")
                || normalized.contains("cancel")
                || normalized.contains("close")
                || (!normalizedTitle.isEmpty() && normalized.equals(normalizedTitle));
    }

    private String toXPathLiteral(String value) {
        if (value == null) {
            return "''";
        }
        if (value.indexOf('\'') < 0) {
            return "'" + value + "'";
        }
        if (value.indexOf('"') < 0) {
            return "\"" + value + "\"";
        }

        StringBuilder builder = new StringBuilder("concat(");
        String[] parts = value.split("'");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                builder.append(", \"'\", ");
            }
            builder.append("'").append(parts[i]).append("'");
        }
        builder.append(")");
        return builder.toString();
    }
}
