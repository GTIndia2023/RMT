package RMT.Pages;

import RMT.Constants.AppConstants;
import RMT.Utils.ElementUtil;
import RMT.Utils.TimeUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
 * Encapsulates the allocation approval task grid and approval actions used in the workflow flow.
 */
public class AllocationWorkflowPage {
    private final WebDriver driver;
    private final ElementUtil eleutil;

    /**
     * Creates the workflow page helper for task approval operations.
     */
    public AllocationWorkflowPage(WebDriver driver) {
        this.driver = driver;
        this.eleutil = new ElementUtil(driver);
    }

    private void logAction(String message) {
        System.out.println("[AllocationWorkflow] " + message);
    }

    private final By allocationTaskTitle = By.xpath("//*[contains(normalize-space(),'Allocation') and contains(normalize-space(),'Pending')]");
    private final By tasksMenu = By.xpath("//*[contains(@aria-label,'Task') or contains(normalize-space(),'Tasks')]");
    private final By allocationApprovalsMenu = By.xpath("//*[contains(normalize-space(),'Allocation Approval') or contains(normalize-space(),'Allocation Approvals')]");
    private final By approveButton = By.xpath("//button[normalize-space()='Approve']");
    private final By rejectButton = By.xpath("//button[normalize-space()='Reject']");
    private final By remarksTextArea = By.xpath("//textarea[contains(@name,'remark') or contains(@placeholder,'Reason')]");
    private final By submitButton = By.xpath("//button[normalize-space()='Submit' or normalize-space()='Save']");
    private final By statusToast = By.xpath("//div[contains(@class,'MuiAlert-message')]");
    private final By[] acceptButtons = {
            By.xpath("//button[normalize-space()='Accept']"),
            By.xpath("//button[contains(normalize-space(),'Accept')]"),
            By.xpath("//*[self::button or self::span][contains(normalize-space(),'Accept')]/ancestor::button[1]")
    };

    private final By[] taskEntries = {
            By.xpath("//div[contains(@class,'ag-row')]//span[contains(normalize-space(),'Allocation')]"),
            By.xpath("//table//tr//td[contains(normalize-space(),'Allocation')]"),
            By.xpath("//*[contains(@class,'task') and contains(normalize-space(),'Allocation')]")
    };
    private final By[] taskGridRows = {
            By.xpath("//div[contains(@class,'ag-center-cols-container')]//div[contains(@class,'ag-row') and @role='row']"),
            By.xpath("//div[contains(@class,'ag-center-cols-container')]//div[contains(@class,'ag-row')]"),
            By.xpath("//table//tr[.//a]")
    };
    private final By[] requestedOnCellsInRow = {
            By.xpath(".//div[@role='gridcell' and @col-id='created_at']"),
            By.xpath(".//div[@role='gridcell' and (@col-id='requested_on' or @col-id='requestedOn')]"),
            By.xpath(".//*[@role='gridcell' and @aria-colindex='7']")
    };
    private final By[] requestedOnHeaders = {
            By.xpath("//div[@role='columnheader' and @col-id='created_at']"),
            By.xpath("//div[@role='columnheader' and (@col-id='requested_on' or @col-id='requestedOn')]"),
            By.xpath("//div[@role='columnheader' and @aria-colindex='7']")
    };
    private final By[] taskIdLinkInRow = {
            By.xpath(".//a[@href and normalize-space()!='']"),
            By.xpath(".//a[normalize-space()!='']")
    };
    private final By[] taskAllocationCheckboxes = {
            By.xpath("//div[contains(@class,'ag-center-cols-container')]//input[contains(@class,'ag-checkbox-input') and @type='checkbox' and contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'row selection')]"),
            By.xpath("//div[contains(@class,'ag-pinned-left-cols-container')]//input[contains(@class,'ag-checkbox-input') and @type='checkbox']"),
            By.xpath("//input[contains(@class,'ag-checkbox-input') and @type='checkbox' and contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'row selection')]"),
            By.xpath("//input[contains(@class,'ag-checkbox-input') and @type='checkbox' and not(@disabled)]")
    };
    private final By[] taskAllocationCheckboxWrappers = {
            By.xpath("//div[contains(@class,'ag-selection-checkbox')]//div[contains(@class,'ag-checkbox-input-wrapper')]"),
            By.xpath("//div[contains(@class,'ag-pinned-left-cols-container')]//div[contains(@class,'ag-checkbox-input-wrapper')]"),
            By.xpath("//span[contains(@class,'ag-checkbox-input-wrapper')]")
    };
    private final By[] allocationStatusChipLabels = {
            By.xpath("//span[contains(@class,'MuiChip-label') and normalize-space()='Allocation Complete']"),
            By.xpath("//span[contains(@class,'MuiChip-label') and contains(normalize-space(),'Allocation')]"),
            By.xpath("//*[contains(@class,'MuiChip-root')]//span[contains(normalize-space(),'Allocation')]")
    };
    private final By[] taskAllocationSectionMarkers = {
            acceptButtons[0],
            acceptButtons[1],
            taskAllocationCheckboxWrappers[0],
            taskAllocationCheckboxes[0],
            allocationStatusChipLabels[1]
    };

    private static final Pattern DATE_TOKEN_PATTERN = Pattern.compile("(\\d{2}[-/]\\d{2}[-/]\\d{4})");
    private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
    };

    /**
     * Checks whether the allocation approval section is visible to the logged-in user.
     */
    public boolean isAllocationTaskSectionVisible() {
        return driver.findElements(allocationTaskTitle).stream().anyMatch(WebElement::isDisplayed)
                || firstVisible(taskEntries) != null;
    }

    /**
     * Opens the allocation approval view from the header navigation when it is available.
     */
    public void navigateToAllocationApprovalsFromHeader() {
        if (driver.findElements(tasksMenu).stream().anyMatch(WebElement::isDisplayed)) {
            eleutil.clickStable(tasksMenu, TimeUtil.DEFAULT_TIME_OUT);
        }
        if (driver.findElements(allocationApprovalsMenu).stream().anyMatch(WebElement::isDisplayed)) {
            eleutil.clickStable(allocationApprovalsMenu, TimeUtil.DEFAULT_TIME_OUT);
        }
    }

    /**
     * Opens the first visible allocation task entry from the workflow listing.
     */
    public void openFirstAllocationTask() {
        By locator = firstVisible(taskEntries);
        if (locator == null) {
            throw new NoSuchElementException("No allocation task entry is visible.");
        }
        eleutil.clickStable(locator, TimeUtil.DEFAULT_TIME_OUT);
    }

    /**
     * Approves the opened allocation task and returns the resulting toast message.
     */
    public String approveTaskAndCaptureMessage() {
        eleutil.clickStable(approveButton, TimeUtil.DEFAULT_TIME_OUT);
        return readStatusToast();
    }

    /**
     * Rejects the opened allocation task with the supplied reason and returns the resulting toast message.
     */
    public String rejectTaskWithReason(String reason) {
        eleutil.clickStable(rejectButton, TimeUtil.DEFAULT_TIME_OUT);
        if (driver.findElements(remarksTextArea).stream().anyMatch(WebElement::isDisplayed)) {
            eleutil.enterTextReliable(remarksTextArea, reason, TimeUtil.DEFAULT_TIME_OUT);
        }
        if (driver.findElements(submitButton).stream().anyMatch(WebElement::isDisplayed)) {
            eleutil.clickStable(submitButton, TimeUtil.DEFAULT_TIME_OUT);
        }
        return readStatusToast();
    }

    /**
     * Opens the latest visible allocation task by requested-on date without filtering by job token.
     */
    public String openLatestTaskFromGridByRequestedOnDate() {
        return openLatestTaskFromGridByRequestedOnDate("");
    }

    /**
     * Opens the latest allocation task row for today, preferring rows whose task link matches the supplied token.
     *
     * @param requiredTaskToken token derived from the job code that helps identify the correct task row
     * @return the requested-on date text from the row that was opened
     */
    public String openLatestTaskFromGridByRequestedOnDate(String requiredTaskToken) {
        LocalDate today = LocalDate.now();
        String todayText = today.format(DATE_FORMATS[0]);
        String normalizedRequiredToken = normalizeToken(requiredTaskToken);
        List<String> visibleRowsSummary = new ArrayList<>();
        long deadline = System.nanoTime()
                + Duration.ofSeconds(AppConstants.ALLOCATION_TASK_VISIBILITY_WAIT_SECONDS).toNanos();
        int attempt = 0;

        while (System.nanoTime() < deadline) {
            attempt++;
            sortRequestedOnDescendingIfPossible();
            waitForAnyVisible(taskGridRows, 12);

            List<WebElement> rows = visibleElements(taskGridRows);
            if (rows.isEmpty()) {
                if (!waitForTodayTaskToAppear(today, normalizedRequiredToken, 6)) {
                    refreshTaskGrid();
                }
                continue;
            }

            WebElement todayMatchingLink = null;
            String todayMatchingDateText = "";

            WebElement todayAnyLink = null;
            String todayAnyDateText = "";
            visibleRowsSummary.clear();

            for (WebElement row : rows) {
                if (!row.isDisplayed()) {
                    continue;
                }

                WebElement taskLink = firstVisibleChild(row, taskIdLinkInRow);
                if (taskLink == null) {
                    continue;
                }

                String requestedOnRaw = readRequestedOnDateFromRow(row);
                String requestedOnNormalized = normalizeDateToken(requestedOnRaw);
                String linkLabel = safeTrim(taskLink.getText());

                String linkText = linkLabel.toLowerCase(Locale.ENGLISH);
                String normalizedLinkText = normalizeToken(linkText);
                boolean tokenMatched = normalizedRequiredToken.isEmpty()
                        || normalizedLinkText.contains(normalizedRequiredToken);
                visibleRowsSummary.add(requestedOnNormalized + " | tokenMatched=" + tokenMatched + " | " + linkLabel);
                logAction("Task row candidate requestedOn=" + requestedOnNormalized
                        + " | tokenMatched=" + tokenMatched
                        + " | link=" + linkLabel);

                LocalDate parsedDate = parseDate(requestedOnNormalized);
                if (parsedDate == null || !parsedDate.isEqual(today)) {
                    continue;
                }

                if (todayAnyLink == null) {
                    todayAnyLink = taskLink;
                    todayAnyDateText = requestedOnNormalized;
                }
                if (tokenMatched) {
                    todayMatchingLink = taskLink;
                    todayMatchingDateText = requestedOnNormalized;
                    break;
                }
            }

            WebElement linkToClick = todayMatchingLink != null ? todayMatchingLink : todayAnyLink;
            String selectedDate = todayMatchingLink != null ? todayMatchingDateText : todayAnyDateText;

            if (linkToClick != null) {
                if (todayMatchingLink != null) {
                    logAction("Selecting task row with today's requested-on date and matching token: " + selectedDate);
                } else {
                    logAction("No task row matched the requested token, but today's requested-on date (" + todayText
                            + ") is available. Selecting today's task row.");
                }
                clickWithFallback(linkToClick);
                waitForAnyVisible(taskAllocationSectionMarkers, 20);
                return selectedDate;
            }

            logAction("Today's task row was not visible on attempt " + attempt
                    + ". Visible rows: " + String.join(" || ", visibleRowsSummary));

            long remainingSeconds = Duration.ofNanos(deadline - System.nanoTime()).toSeconds();
            if (remainingSeconds <= 0) {
                break;
            }

            int waitSeconds = (int) Math.min(AppConstants.ALLOCATION_TASK_VISIBILITY_POLL_SECONDS, remainingSeconds);
            if (!waitForTodayTaskToAppear(today, normalizedRequiredToken, waitSeconds)) {
                refreshTaskGrid();
            }
        }

        throw new NoSuchElementException("No task row with today's requested-on date (" + todayText
                + ") was visible for token '" + requiredTaskToken + "'. Visible rows: "
                + String.join(" || ", visibleRowsSummary));
    }

    /**
     * Selects the first available checkbox in the allocation approval grid for the opened task.
     */
    public void selectFirstTaskAllocationCheckbox() {
        waitForAnyVisible(taskAllocationSectionMarkers, 10);

        if (clickFirstUncheckedWrapper()) {
            return;
        }

        if (clickFirstUncheckedInput()) {
            return;
        }

        throw new NoSuchElementException("No selectable task allocation checkbox found in AG grid.");
    }

    /**
     * Clicks the Accept action for the currently selected allocation row and throws if no action is available.
     */
    public void clickAcceptForSelectedTaskAllocation() {
        if (!clickAcceptForSelectedTaskAllocationIfVisible()) {
            throw new NoSuchElementException("Accept button is not visible in task allocation section.");
        }
    }

    /**
     * Clicks the Accept action for the selected allocation row when the button is visible.
     *
     * @return true when the Accept action was clicked, otherwise false
     */
    public boolean clickAcceptForSelectedTaskAllocationIfVisible() {
        waitForAnyVisible(acceptButtons, 10);
        By acceptLocator = firstVisible(acceptButtons);
        if (acceptLocator == null) {
            logAction("Accept button is still not visible after waiting.");
            return false;
        }
        eleutil.waitForElementVisible(acceptLocator, TimeUtil.DEFAULT_TIME_OUT);
        eleutil.clickStable(acceptLocator, TimeUtil.DEFAULT_TIME_OUT);
        return true;
    }

    /**
     * Waits for the task status chip to appear after accepting the allocation and returns the displayed value.
     */
    public String waitForTaskAllocationStatusAfterAccept(int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            wait.until(d -> readTaskAllocationStatusChip().length() > 0);
        } catch (Exception ignored) {
        }
        return readTaskAllocationStatusChip();
    }

    /**
     * Reads the current allocation status chip text from the opened workflow task.
     */
    public String readTaskAllocationStatusChip() {
        for (By locator : allocationStatusChipLabels) {
            for (WebElement element : driver.findElements(locator)) {
                if (!element.isDisplayed()) {
                    continue;
                }
                String text = safeTrim(element.getText());
                if (!text.isEmpty()) {
                    return text;
                }
            }
        }
        return "";
    }

    /**
     * Validates whether the supplied workflow status represents a completed allocation state.
     */
    public boolean isAllocationCompleteStatus(String statusText) {
        String normalized = safeTrim(statusText).toLowerCase(Locale.ENGLISH);
        return normalized.contains(AppConstants.ALLOCATION_APPROVED_MESSAGE.toLowerCase(Locale.ENGLISH))
                || normalized.contains("allocation complete");
    }

    private String readStatusToast() {
        if (driver.findElements(statusToast).stream().anyMatch(WebElement::isDisplayed)) {
            return eleutil.doGetText(statusToast).trim();
        }
        return "";
    }

    private String readRequestedOnDateFromRow(WebElement row) {
        WebElement dateCell = firstVisibleChild(row, requestedOnCellsInRow);
        if (dateCell == null) {
            return "";
        }

        String dateText = safeTrim(dateCell.getText());
        if (!dateText.isEmpty()) {
            return dateText;
        }

        return safeTrim(dateCell.getAttribute("title"));
    }

    private String normalizeDateToken(String value) {
        String raw = safeTrim(value);
        if (raw.isEmpty()) {
            return raw;
        }
        Matcher matcher = DATE_TOKEN_PATTERN.matcher(raw);
        if (matcher.find()) {
            return matcher.group(1).replace("/", "-");
        }
        return raw.replace("/", "-");
    }

    private LocalDate parseDate(String value) {
        String candidate = safeTrim(value);
        if (candidate.isEmpty()) {
            return null;
        }
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(candidate, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }

    private void clickWithFallback(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
            element.click();
        } catch (Exception clickException) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private boolean isCheckboxSelected(WebElement checkbox) {
        String checked = safeAttr(checkbox, "checked");
        String ariaChecked = safeAttr(checkbox, "aria-checked");
        return "true".equalsIgnoreCase(checked)
                || "checked".equalsIgnoreCase(checked)
                || "true".equalsIgnoreCase(ariaChecked)
                || checkbox.isSelected();
    }

    private boolean clickFirstUncheckedWrapper() {
        List<WebElement> wrappers = visibleElements(taskAllocationCheckboxWrappers);
        for (WebElement wrapper : wrappers) {
            if (!wrapper.isDisplayed()) {
                continue;
            }
            if (!wrapper.findElements(By.xpath("./ancestor::*[contains(@class,'ag-header')]")).isEmpty()) {
                continue;
            }

            String classes = safeAttr(wrapper, "class").toLowerCase(Locale.ENGLISH);
            if (classes.contains("ag-checked")) {
                return true;
            }

            clickWithFallback(wrapper);
            String updatedClasses = safeAttr(wrapper, "class").toLowerCase(Locale.ENGLISH);
            if (updatedClasses.contains("ag-checked")) {
                return true;
            }
            return true;
        }
        return false;
    }

    private boolean clickFirstUncheckedInput() {
        List<WebElement> checkboxes = allElements(taskAllocationCheckboxes);
        for (WebElement checkbox : checkboxes) {
            String ariaLabel = safeAttr(checkbox, "aria-label").toLowerCase(Locale.ENGLISH);
            if (ariaLabel.contains("all rows") || ariaLabel.contains("select all")) {
                continue;
            }

            if (isCheckboxSelected(checkbox)) {
                return true;
            }

            try {
                if (checkbox.isDisplayed()) {
                    clickWithFallback(checkbox);
                } else {
                    WebElement wrapper = firstVisibleChild(checkbox, new By[]{
                            By.xpath("./ancestor::div[contains(@class,'ag-checkbox-input-wrapper')][1]"),
                            By.xpath("./ancestor::span[contains(@class,'ag-checkbox-input-wrapper')][1]"),
                            By.xpath("./ancestor::label[1]")
                    });
                    if (wrapper != null) {
                        clickWithFallback(wrapper);
                    } else {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
                    }
                }
            } catch (Exception e) {
                continue;
            }

            return true;
        }
        return false;
    }

    private void waitForAnyVisible(By[] locators, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            wait.until(d -> {
                for (By locator : locators) {
                    if (d.findElements(locator).stream().anyMatch(WebElement::isDisplayed)) {
                        return true;
                    }
                }
                return false;
            });
        } catch (Exception ignored) {
        }
    }

    private boolean waitForTodayTaskToAppear(LocalDate today, String normalizedRequiredToken, int timeoutSeconds) {
        if (timeoutSeconds <= 0) {
            return false;
        }

        logAction("Waiting up to " + timeoutSeconds + " seconds for today's task row to appear.");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            return Boolean.TRUE.equals(wait.until(d -> hasVisibleTodayTask(today, normalizedRequiredToken)));
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean hasVisibleTodayTask(LocalDate today, String normalizedRequiredToken) {
        List<WebElement> rows = visibleElements(taskGridRows);
        for (WebElement row : rows) {
            if (!row.isDisplayed()) {
                continue;
            }

            WebElement taskLink = firstVisibleChild(row, taskIdLinkInRow);
            if (taskLink == null) {
                continue;
            }

            LocalDate parsedDate = parseDate(normalizeDateToken(readRequestedOnDateFromRow(row)));
            if (parsedDate == null || !parsedDate.isEqual(today)) {
                continue;
            }

            if (normalizedRequiredToken.isEmpty()) {
                return true;
            }

            String normalizedLinkText = normalizeToken(taskLink.getText());
            if (normalizedLinkText.contains(normalizedRequiredToken)) {
                return true;
            }
        }
        return false;
    }

    private void refreshTaskGrid() {
        logAction("Refreshing My Approval page to look for the current task.");
        driver.navigate().refresh();
        waitForAnyVisible(taskGridRows, 15);
    }

    private List<WebElement> visibleElements(By[] locators) {
        List<WebElement> visible = new ArrayList<>();
        for (By locator : locators) {
            for (WebElement element : driver.findElements(locator)) {
                if (element.isDisplayed()) {
                    visible.add(element);
                }
            }
            if (!visible.isEmpty()) {
                return visible;
            }
        }
        return visible;
    }

    private List<WebElement> allElements(By[] locators) {
        List<WebElement> elements = new ArrayList<>();
        for (By locator : locators) {
            elements.addAll(driver.findElements(locator));
        }
        return elements;
    }

    private WebElement firstVisibleChild(WebElement root, By[] relativeLocators) {
        for (By locator : relativeLocators) {
            List<WebElement> elements = root.findElements(locator);
            for (WebElement element : elements) {
                if (element.isDisplayed()) {
                    return element;
                }
            }
        }
        return null;
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeToken(String value) {
        return safeTrim(value).toLowerCase(Locale.ENGLISH).replaceAll("[^a-z0-9]", "");
    }

    private String safeAttr(WebElement element, String name) {
        try {
            String value = element.getAttribute(name);
            return value == null ? "" : value;
        } catch (Exception e) {
            return "";
        }
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

    private WebElement firstVisibleElement(By[] locators) {
        for (By locator : locators) {
            for (WebElement element : driver.findElements(locator)) {
                if (element.isDisplayed()) {
                    return element;
                }
            }
        }
        return null;
    }

    private void sortRequestedOnDescendingIfPossible() {
        WebElement header = firstVisibleElement(requestedOnHeaders);
        if (header == null) {
            return;
        }

        for (int attempt = 1; attempt <= 3; attempt++) {
            header = firstVisibleElement(requestedOnHeaders);
            if (header == null) {
                return;
            }

            String sortState = safeAttr(header, "aria-sort").toLowerCase(Locale.ENGLISH);
            if ("descending".equals(sortState)) {
                logAction("Requested On column is already sorted descending.");
                return;
            }

            logAction("Clicking Requested On header to sort descending. attempt=" + attempt + " currentSort=" + sortState);
            clickWithFallback(header);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            try {
                wait.until(d -> {
                    WebElement refreshedHeader = firstVisibleElement(requestedOnHeaders);
                    if (refreshedHeader == null) {
                        return false;
                    }
                    String refreshedState = safeAttr(refreshedHeader, "aria-sort").toLowerCase(Locale.ENGLISH);
                    return !"".equals(refreshedState) && !refreshedState.equals(sortState);
                });
            } catch (Exception ignored) {
            }
        }
    }
}
