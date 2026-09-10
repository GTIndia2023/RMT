package RMT.Pages;

import RMT.App;
import RMT.Constants.AppConstants;
import RMT.Exceptions.ElementException;
import RMT.Utils.ElementUtil;
import RMT.Utils.TimeUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ProjectListingsPage {
    private WebDriver driver;
    ElementUtil eleutil;

    //1.ProjectListings page constructor
    public  ProjectListingsPage(WebDriver driver){
        this.driver=driver;
        eleutil=new ElementUtil(driver);
        ensureApplicationLandingState();
    }

    private void logAction(String message) {
        System.out.println("[ProjectListings] " + message);
    }

    /**
     * Ensures the browser has fully transitioned from Microsoft authentication back into the OptiWise shell.
     * The helper is intentionally invoked from the page constructor and public entry points so headed and
     * headless runs stay consistent.
     */
    public void ensureApplicationLandingState() {
        eleutil.ensureApplicationLandingAfterLogin();
    }

    //2. ProjectListing page By locators
    private By skillsBtn=By.id("basic-menu-15");
    private By skillMasterBtn=By.xpath("//a[@href='/skillmaster']");
    private By Search = By.xpath("//input[@placeholder='Search']");
    private By JobCode=By.xpath("//span[@aria-label='Deals Structuring-Inteligent Hub-2425-01-P-1']");
    private By ThreeDots = By.xpath("(//span[@class='MuiTouchRipple-root css-w0pj6f'])[18]");
    private By threeDotsAllocationAction = By.xpath("(//*[name()='svg' and @data-testid='MoreVertIcon'])[2]/ancestor::button[1]");
    private By allocateEmployeeMenuOption = By.xpath("//ul[@role='menu']//li[@role='menuitem' and normalize-space(.)='Allocate Employee']");
    private By CreateRequisiton=By.xpath("(//li[@tabindex='-1'])[10]");
    private By calenderView=By.xpath("(//li[@tabindex='-1'])[7]");
    private By downArrow=By.xpath("//*[name()='svg' and @data-testid='KeyboardArrowRightIcon']");
    private By effortHrs=By.xpath("(//input[@type='number'])[5]");
    private By OkBtn=By.xpath("(//button[@type='submit'])");
    private By CheckboxBtn=By.xpath("(//input[@type='checkbox'])[2]");
    private By updateAllocationGridCheckboxBtn= By.xpath("(//input[@type='checkbox'])[15]");
    private By allocateBtn=By.xpath("(//button[text()='Allocate'])");
    private By yesBtn=By.xpath("(//button[text()='Yes'])");
    private By allocationStatus=By.xpath("//div[@title='Allocation complete']");
    private By detailView=By.xpath("//button[text()='Detail View']");
    private By delegateDropdown=By.xpath("//input[@placeholder='Type And Select']");
    private By updateDetailsBtn=By.xpath("//button[text()='Update Details']");
    private By saveBtn=By.xpath("(//button[@type='button'])[22]");
    private By updateSuccessFullMesg=By.xpath("//div[text()='Project details updated successfully!']");
    private By AddMoreIcon=By.xpath("//*[normalize-space(text())='Add more']//*[name()='svg' and @data-testid='AddCircleOutlinedIcon']");
    private By releaseResourceBtn=By.xpath("(//button[@type='button'])[16]");
    private By releaseResourceSuccessMsg=By.xpath("//div[@class='MuiAlert-message css-1xsto0d']");
    private By updateBtn=By.xpath("//button[@aria-label='Update Allocation']");
    private By allocationsMenuOption = By.xpath("//ul[@role='menu']//li[@role='menuitem' and contains(normalize-space(.),'Allocation')]");
    private By clickonPencilIcon= By.xpath("(//button[@type='button'])[15]");
    private By addingDescription=By.xpath("(//textarea[@class='MuiInputBase-input MuiOutlinedInput-input MuiInputBase-inputMultiline css-u36398'])[1]");
    private By clickonDescriptionSaveBtn= By.xpath(" //*[local-name()='svg' and @data-testid='SaveIcon']");
    private By navigateReportsPage=By.xpath("//li[text()='Reports']");
    private By budgetDetails=By.xpath("//button[text()='Budget Details']");
    private By startDate=By.xpath("(//input[@placeholder='DD-MM-YYYY'])");
    private By settingsIcon=By.xpath("//*[name()='svg' and @data-testid='SettingsIcon']");
    private By configMenu=By.xpath("//li[contains(normalize-space(),'Configuration') or contains(normalize-space(),'Settings')]");
    private By appendedCapacityMenu=By.xpath("//li[contains(normalize-space(),'Appended Capacity') or contains(normalize-space(),'Extended Utilization')]");
    private By projectsNavTab=By.xpath("//a[normalize-space()='Projects' or contains(@href,'project')]");
    private By accountMenuIcon=By.cssSelector(".css-odsz1v");
    private By logoutMenuItem=By.cssSelector("#account-menu .MuiButtonBase-root");
    private By taskIdIcon=By.cssSelector("svg[data-testid='AddTaskIcon']");
    private final By[] releaseResourceActionButtons = {
            By.xpath("//*[name()='svg' and @data-testid='PersonRemoveSharpIcon']/ancestor::button[1]"),
            By.xpath("//*[name()='svg' and @data-testid='PersonRemoveSharpIcon']/ancestor::*[self::button or self::div or self::span or self::li][1]"),
            By.xpath("//*[name()='svg' and @data-testid='PersonRemoveSharpIcon']/ancestor::*[contains(@class,'MuiButtonBase-root') or contains(@class,'MuiIconButton-root') or contains(@class,'MuiButton-root')][1]"),
            By.xpath("//button[.//*[name()='svg' and @data-testid='PersonRemoveSharpIcon']]"),
            By.xpath("//button[contains(@aria-label,'Release') or contains(@aria-label,'Remove')]"),
            By.xpath("//*[name()='svg' and @data-testid='PersonRemoveSharpIcon']")
    };
    private final By[] releaseConfirmButtons = {
            By.xpath("//button[normalize-space()='Yes']"),
            By.xpath("//button[normalize-space()='OK']"),
            By.xpath("//button[normalize-space()='Ok']"),
            By.xpath("//button[normalize-space()='Confirm']"),
            By.xpath("//button[contains(normalize-space(),'Release')]")
    };
    private final By[] toastMessageLocators = {
            By.xpath("//div[contains(@class,'MuiAlert-message')]"),
            By.xpath("//*[@role='alert']"),
            By.xpath("//div[contains(@class,'MuiSnackbar-root')]//*[self::div or self::p]")
    };
    private final By[] allocateEmployeeMenuOptions = {
            By.xpath("//ul[@role='menu']//li[@role='menuitem' and normalize-space()='Allocate Employee']"),
            By.xpath("//ul[@role='menu']//li[@role='menuitem']//span[normalize-space()='Allocate Employee']/ancestor::li[1]"),
            By.xpath("//*[(@role='menuitem' or self::li or self::button) and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'allocate employee')]"),
            By.xpath("//*[(@role='menuitem' or self::li or self::button) and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'allocate resource')]")
    };
    private final By[] allocationsMenuOptions = {
            By.xpath("//ul[@role='menu']//li[@role='menuitem' and normalize-space()='Allocations']"),
            By.xpath("//ul[@role='menu']//li[@role='menuitem']//span[normalize-space()='Allocations']/ancestor::li[1]"),
            By.xpath("//*[(@role='menuitem' or self::li or self::button) and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'allocations')]"),
            By.xpath("//*[(@role='menuitem' or self::li or self::button) and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'allocation')]"),
            allocationsMenuOption
    };
    private final By[] commonAllocationMarkers = {
            By.xpath("//div[contains(@class,'MuiModal-root') and .//input[@id='searchEmployee']]"),
            By.xpath("//div[contains(@class,'MuiDialog-root') and .//input[@id='searchEmployee']]"),
            By.xpath("//div[@role='dialog' and .//input[@id='searchEmployee']]"),
            By.xpath("//div[contains(@class,'MuiModal-root') and .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'allocate employee')]]"),
            By.xpath("//div[contains(@class,'MuiDialog-root') and .//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'allocate employee')]]")
    };
    private final By[] blockingUiLocators = {
            By.cssSelector("div.loader"),
            By.cssSelector(".loader"),
            By.xpath("//div[contains(@class,'MuiBackdrop-root') and not(contains(@style,'opacity: 0'))]"),
            By.xpath("//div[contains(@class,'MuiCircularProgress-root')]/ancestor::div[contains(@class,'MuiBackdrop-root')]")
    };

    //3.Page actions

    /**
     *This method is used to get the projectLiting page title to verify that
     * user is on the project listings page
     * @return title as String
     * @throws  ElementException
     */
    public String getProjectListingsPageTitle() {
        ensureApplicationLandingState();
        String title=driver.getTitle();
        System.out.println("Project Listings Page  title is "+ title);
        if (title == null || title.trim().isEmpty()) {
            throw new ElementException(" Element for project listings title is not present on the page ");
        }
        return title;
    }

    /**
     * This method is used to check the project lostings page URL
     * which can be used to check that on login user is not getting directed to some another page
     * @return Url as String
     */
    public String getProjectListingPageUrl(){
        ensureApplicationLandingState();
        String url=driver.getCurrentUrl();
        System.out.println("Project listing page Url is " + url);
        return url;
    }

    /**
     * This method is used to navigate to skill master page
     * @return skillMaster Page object
     */
    public SkillMasterPage navigateToskillMasterPage(){
        ensureApplicationLandingState();
        eleutil.clickStable(skillsBtn,10);
        eleutil.handleParentSubMenu(skillsBtn,skillMasterBtn);
        eleutil.waitForURLContains("/skillmaster", TimeUtil.DEFAULT_TIME_OUT);
        return new SkillMasterPage(driver);

    }

    /**
     * This method is used to navigate to Job Requisition page
     * @return
     */
    public RequisitionPage navigateToRequisitonPage (){
        eleutil.doActionsSendKeys(Search,AppConstants.Search_JOB_BY_JOB_CODE);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(ThreeDots);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(CreateRequisiton);
        return new RequisitionPage(driver);


    }

    /**
     * This method is used to navigate to the Reports page.
     * The method waits for a short duration to ensure visibility of the required elements
     * before performing the navigation action.
     *
     * @return ReportsPage object representing the Reports page.
     * @throws RuntimeException if the thread sleep is interrupted.
     * @throws ElementException if the element is not visible on the page.
     */
    public ReportsPage navigateToReportsPage (){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(navigateReportsPage);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new ElementException("Element not visible on the page yet ");
        }
        return new ReportsPage(driver);


    }

    /**
     * This method is used to navigate to the Project Budget page from the Project Listings page.
     * It performs a series of actions such as searching for a job, interacting with UI elements,
     * and clicking the necessary options to open the Project Budget page.
     *
     * @return an instance of BudgetPage, which represents the Project Budget page.
     */
    public BudgetPage navigateToProjectBudgetPage (){
        eleutil.doActionsSendKeys(Search,AppConstants.Search_JOB_BY_JOB_CODE);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(ThreeDots);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(calenderView);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(budgetDetails);
        return new BudgetPage(driver);


    }

    /**
     * This method updates the allocation for a project from the project listing page by interacting
     * with various UI elements such as dropdowns, buttons, and input fields. It uses Actions class
     * for performing user interactions like double click and keyboard inputs. The allocation status
     * is retrieved after performing these actions.
     *
     * @return the allocation status as a String after performing the update
     */
    public String updateAllocationFromProjectListingPage(){
        Actions act=new Actions(driver);
        eleutil.doActionsSendKeys(Search,AppConstants.UPDATE_ALLOCATION_BY_JOB_CODE);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(downArrow);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        act.doubleClick(driver.findElement(By.xpath("(//div[@class='rct-item item-allocation '])[1]"))).perform();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //eleutil.doActionsClick(updateAllocation);
        act.doubleClick(driver.findElement(By.xpath("(//div[@class='rct-item item-available'])[3]"))).perform();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(AddMoreIcon);
        eleutil.doActionsSendNumberWithPause(effortHrs,"");
        eleutil.doActionsClick(OkBtn);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(CheckboxBtn);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(allocateBtn);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(yesBtn);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String status=eleutil.doGetText(allocationStatus);
        System.out.println("Allocation Status is  "+ status);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

    /**
     * This method is used to release a resource by clicking on the appropriate buttons
     * and retrieving the success message displayed after the operation.
     *
     * @return A string containing the success message indicating the resource
     *         was released successfully.
     */
    public String checkReleaseResource(){
        eleutil.doActionsClick(releaseResourceBtn);
        eleutil.doActionsClick(yesBtn);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String successMsg=eleutil.doGetText(releaseResourceSuccessMsg);
        System.out.println("Release Resource Successfully  " + successMsg);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return successMsg;

    }

    /**
     * This method performs the allocation update process from the grid page. It interacts
     * with various elements on the page such as buttons and input fields to handle allocation
     * and release operations. It also retrieves and returns the current allocation status
     * after the process is completed.
     *
     * @return the allocation status as a String
     */
    public String updateAllocationFromGridPage(){
        Actions act=new Actions(driver);
        eleutil.doActionsClick(updateBtn);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        act.doubleClick(driver.findElement(By.xpath("(//div[@class='rct-item item-allocation'])[2]"))).perform();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(AddMoreIcon);
        eleutil.doActionsSendNumberWithPause(effortHrs,"");
        eleutil.doActionsClick(OkBtn);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(updateAllocationGridCheckboxBtn);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(allocateBtn);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(yesBtn);
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String status=eleutil.doGetText(allocationStatus);
        System.out.println("Allocation Status is  "+ status);
        eleutil.doActionsClick(releaseResourceBtn);
        eleutil.doActionsClick(yesBtn);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String successMsg=eleutil.doGetText(releaseResourceSuccessMsg);
        System.out.println("Release Resource Successfully  " + successMsg);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

    /**
     * This method is used to add or update a description in a project listing workflow.
     * It involves interacting with various UI elements such as buttons, input fields,
     * and performing actions like clicking, sending keys, and verifying the success of the operation.
     *
     * @return The success message as a String after the description is added or updated successfully.
     */
    public String checkadddingDescription(){
        Actions act= new Actions(driver);
        eleutil.doActionsClick(detailView);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(clickonPencilIcon);
        act
                .click(driver.findElement(By.xpath("(//textarea[@class='MuiInputBase-input MuiOutlinedInput-input MuiInputBase-inputMultiline css-u36398'])[1]")))  // Focus on the input field
                .keyDown(Keys.CONTROL)
                .sendKeys("a")                        // Select all text
                .keyUp(Keys.CONTROL)
                .sendKeys(Keys.DELETE)               // Delete selected text
                .perform();
        eleutil.doActionsClick(clickonDescriptionSaveBtn);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doClick(updateDetailsBtn,10);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doClick(saveBtn);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsSendKeysWithPause(addingDescription,"This is to test the description addition",10);
        eleutil.doActionsClick(clickonDescriptionSaveBtn);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doClick(updateDetailsBtn,10);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doClick(saveBtn);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String successMesg=eleutil.doGetText(updateSuccessFullMesg);
        System.out.println("Description Added Successfully   " + successMesg);
        eleutil.doActionsClick(clickonPencilIcon);
        return successMesg;
    }

    /**
     * Navigates to Common Allocation screen from Project Listings using Job Code context.
     * This method is intended for UC008-style allocation entry points.
     *
     * @param jobCode project/job code to search
     * @return CommonAllocationPage object
     */
    public CommonAllocationPage navigateToCommonAllocationByJobCode(String jobCode){
        if (jobCode == null || jobCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Job code is required to navigate to Common Allocation screen.");
        }

        logAction("Navigating to Common Allocation for job code: " + jobCode.trim());
        ensureProjectsContext();
        searchByJobCode(jobCode.trim());
        openMoreActionsMenuForSearchedJob(jobCode.trim(), allocateEmployeeMenuOptions);
        clickFirstVisible(allocateEmployeeMenuOptions, "Allocate Employee option");

        if (!waitForCommonAllocationPopup(10)) {
            openMoreActionsMenuForSearchedJob(jobCode.trim(), allocateEmployeeMenuOptions);
            clickFirstVisible(allocateEmployeeMenuOptions, "Allocate Employee option");
            waitForCommonAllocationPopup(12);
        }
        return new CommonAllocationPage(driver);
    }

    public CommonAllocationPage navigateToCommonAllocationByJobCodeStrict(String jobCode){
        if (jobCode == null || jobCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Job code is required to navigate to Common Allocation screen.");
        }

        logAction("Navigating to Common Allocation (strict) for job code: " + jobCode.trim());
        ensureProjectsContext();
        searchByJobCode(jobCode.trim());

        if (!clickMenuActionAndWait(threeDotsAllocationAction, false, allocateEmployeeMenuOptions)) {
            openMoreActionsMenuForSearchedJob(jobCode.trim(), allocateEmployeeMenuOptions);
        }

        eleutil.waitForElementVisible(allocateEmployeeMenuOption, TimeUtil.DEFAULT_TIME_OUT);
        eleutil.clickStable(allocateEmployeeMenuOption, TimeUtil.DEFAULT_TIME_OUT);

        if (!waitForCommonAllocationPopup(12)) {
            openMoreActionsMenuForSearchedJob(jobCode.trim(), allocateEmployeeMenuOptions);
            clickFirstVisible(allocateEmployeeMenuOptions, "Allocate Employee option");
            if (!waitForCommonAllocationPopup(12)) {
                throw new NoSuchElementException("Common Allocation popup was not displayed after clicking Allocate Employee.");
            }
        }
        return new CommonAllocationPage(driver);
    }

    public void openAllocationsForJobCode(String jobCode) {
        if (jobCode == null || jobCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Job code is required to open Allocations.");
        }

        logAction("Opening Allocations for job code: " + jobCode.trim());
        waitForBlockingUiToClear(12);
        ensureProjectsContext();
        searchByJobCode(jobCode.trim());
        waitForBlockingUiToClear(8);
        openMoreActionsMenuForSearchedJob(jobCode.trim(), allocationsMenuOptions);
        try {
            waitForAnyVisible(allocationsMenuOptions, 8);
            clickFirstVisible(allocationsMenuOptions, "Allocations menu option");
        } catch (NoSuchElementException firstAttemptFailure) {
            logAction("Allocations menu option was not visible on the first attempt. Waiting for the current menu to settle and retrying.");
            waitForBlockingUiToClear(8);
            try {
                waitForAnyVisible(allocationsMenuOptions, 12);
                clickFirstVisible(allocationsMenuOptions, "Allocations menu option");
            } catch (NoSuchElementException retryFailure) {
                logAction("Allocations menu option still not visible after waiting. Reopening the row actions menu once more.");
                waitForBlockingUiToClear(6);
                openMoreActionsMenuForSearchedJob(jobCode.trim(), allocationsMenuOptions);
                waitForAnyVisible(allocationsMenuOptions, 12);
                clickFirstVisible(allocationsMenuOptions, "Allocations menu option");
            }
        }

        waitForAllocationRowsToLoad(30);
    }

    public String releaseFirstAllocatedResourceAndCaptureMessage() {
        logAction("Releasing first allocated resource from the Allocation view.");
        waitForAllocationRowsToLoad(30);
        boolean releaseIconVisible = hasEnabledReleaseResourceAction();
        if (!releaseIconVisible) {
            waitForAnyVisible(releaseResourceActionButtons, 12);
            releaseIconVisible = hasEnabledReleaseResourceAction();
        }
        if (!releaseIconVisible) {
            openFirstAllocatedRowForRelease();
            waitForAnyVisible(releaseResourceActionButtons, 10);
            releaseIconVisible = hasEnabledReleaseResourceAction();
        }
        if (!releaseIconVisible) {
            debugPrintAllocationPageState();
            throw new NoSuchElementException("Release resource button is not enabled after opening allocations.");
        }
        clickFirstEnabledVisible(releaseResourceActionButtons, "Release resource button");

        if (isAnyVisible(releaseConfirmButtons)) {
            try {
                clickFirstVisible(releaseConfirmButtons, "Release confirmation button");
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                logAction("Release confirmation button was not stable after release click. Continuing to toast capture. " + e.getMessage());
            }
        }

        return waitForToastMessage(12);
    }

    private boolean hasEnabledReleaseResourceAction() {
        return firstEnabledVisibleElement(releaseResourceActionButtons) != null;
    }

    public boolean isUpdateAllocationButtonEnabled() {
        WebElement updateButton = firstVisibleElement(new By[]{
                updateBtn,
                By.xpath("//button[@aria-label='Update Allocation']")
        });
        if (updateButton == null) {
            return false;
        }

        String ariaDisabled = safeAttr(updateButton, "aria-disabled");
        String classAttr = safeAttr(updateButton, "class").toLowerCase();
        return updateButton.isEnabled()
                && !"true".equalsIgnoreCase(ariaDisabled)
                && !classAttr.contains("disabled");
    }

    public void clickUpdateAllocationButtonIfEnabled() {
        if (!isUpdateAllocationButtonEnabled()) {
            logAction("Update Allocation button is disabled or hidden.");
            return;
        }
        logAction("Clicking Update Allocation button.");
        clickFirstVisible(new By[]{updateBtn, By.xpath("//button[@aria-label='Update Allocation']")}, "Update Allocation button");
    }

    public void clickTaskIdIcon() {
        logAction("Clicking TaskID icon.");
        By[] taskIconTargets = {
                By.xpath("//*[name()='svg' and @data-testid='AddTaskIcon']/ancestor::button[1]"),
                By.xpath("//*[name()='svg' and @data-testid='AddTaskIcon']"),
                taskIdIcon
        };
        waitForAnyVisible(taskIconTargets, 12);
        clickFirstVisible(taskIconTargets, "TaskID icon");
    }

    /**
     * Logs out from the current application session using the account menu.
     * This is used in allocation E2E flow to end the test exactly as business users do.
     */
    public void logoutFromApplication() {
        logAction("Logging out from application.");
        By[] accountMenus = {
                accountMenuIcon,
                By.xpath("//button[contains(@aria-label,'account')]"),
                By.xpath("//*[contains(@class,'avatar')]")
        };
        clickFirstVisible(accountMenus, "Account menu");

        By[] logoutOptions = {
                By.xpath("//li[normalize-space()='Logout']"),
                By.xpath("//li[normalize-space()='Log Out']"),
                By.xpath("//li[normalize-space()='Sign out']"),
                By.xpath("//button[normalize-space()='Logout']"),
                By.xpath("//button[normalize-space()='Log Out']"),
                By.xpath("//span[normalize-space()='Logout']/ancestor::*[self::li or self::button][1]"),
                logoutMenuItem
        };
        waitForAnyVisible(logoutOptions, 8);
        clickFirstVisible(logoutOptions, "Logout option");
        waitForAnyVisible(new By[]{
                By.xpath("//input[@type='email']"),
                By.xpath("//div[normalize-space()='Use another account']")
        }, 12);
    }

    /**
     * Navigates to Appended Capacity configuration screen.
     * This method is aligned with FRD use case: Admin Configures Appended Capacity for Extended Utilization Allocation.
     *
     * @return AppendedCapacityConfigPage object
     */
    public AppendedCapacityConfigPage navigateToAppendedCapacityConfiguration(){
        if (eleutil.isElementVisible(settingsIcon, 5)) {
            eleutil.clickStable(settingsIcon, 10);
        } else {
            By[] fallbackSettings = {
                    By.xpath("//button[contains(@aria-label,'Settings')]"),
                    By.xpath("//div[contains(@class,'settings')]"),
                    By.xpath("//li[contains(normalize-space(),'Settings')]")
            };
            clickFirstVisible(fallbackSettings, "Settings icon/menu");
        }

        if (eleutil.isElementVisible(configMenu, 4)) {
            eleutil.clickStable(configMenu, 8);
        }

        if (eleutil.isElementVisible(appendedCapacityMenu, 5)) {
            eleutil.clickStable(appendedCapacityMenu, 8);
        } else {
            By[] fallbackAppendedMenu = {
                    By.xpath("//*[contains(normalize-space(),'Appended Capacity')]"),
                    By.xpath("//*[contains(normalize-space(),'Extended Utilization')]")
            };
            clickFirstVisible(fallbackAppendedMenu, "Appended Capacity menu");
        }
        waitForAnyVisible(new By[]{appendedCapacityMenu, By.xpath("//*[contains(normalize-space(),'Appended Capacity')]")}, 8);
        return new AppendedCapacityConfigPage(driver);
    }

    private void clickFirstVisible(By[] locators, String logicalName){
        logAction("Attempting to click: " + logicalName);
        for (By locator : locators) {
            try {
                for (WebElement element : driver.findElements(locator)) {
                    if (!element.isDisplayed()) {
                        continue;
                    }
                    if (clickElementWithFallback(element)) {
                        logAction("Clicked " + logicalName + " using " + locator);
                        return;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        throw new NoSuchElementException("Unable to find visible element for: " + logicalName);
    }

    private void clickFirstEnabledVisible(By[] locators, String logicalName) {
        logAction("Attempting to click enabled element: " + logicalName);
        for (By locator : locators) {
            try {
                for (WebElement element : driver.findElements(locator)) {
                    if (!isElementEnabledForAction(element)) {
                        continue;
                    }
                    if (clickElementWithFallback(element)) {
                        logAction("Clicked enabled " + logicalName + " using " + locator);
                        return;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        throw new NoSuchElementException("Unable to find enabled visible element for: " + logicalName);
    }

    private boolean isAnyVisible(By[] locators) {
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

    private WebElement firstEnabledVisibleElement(By[] locators) {
        for (By locator : locators) {
            try {
                for (WebElement element : driver.findElements(locator)) {
                    if (isElementEnabledForAction(element)) {
                        return element;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private boolean isElementEnabledForAction(WebElement element) {
        if (element == null) {
            return false;
        }
        try {
            if (!element.isDisplayed()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        String disabledAttr = safeAttr(element, "disabled");
        String ariaDisabled = safeAttr(element, "aria-disabled");
        String classes = safeAttr(element, "class").toLowerCase();
        return element.isEnabled()
                && !"true".equalsIgnoreCase(disabledAttr)
                && !"true".equalsIgnoreCase(ariaDisabled)
                && !classes.contains("mui-disabled")
                && !classes.contains("disabled");
    }

    private void ensureProjectsContext() {
        By[] projectsTabs = {
                projectsNavTab,
                By.xpath("//button[normalize-space()='Projects']"),
                By.xpath("//span[normalize-space()='Projects']"),
                By.xpath("//li[normalize-space()='Projects']")
        };

        if (isAnyVisible(projectsTabs)) {
            logAction("Ensuring Projects context is selected.");
            clickFirstVisible(projectsTabs, "Projects tab");
            waitForAnyVisible(new By[]{Search}, 8);
        }
    }

    private void searchByJobCode(String jobCode) {
        logAction("Searching job code: " + jobCode);
        waitForBlockingUiToClear(20);

        WebElement searchInput = eleutil.waitForElementVisible(Search, 12);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(searchInput));
            logAction("Typing job code into search field using native WebDriver.");
            searchInput.click();
            searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            searchInput.sendKeys(Keys.DELETE);
            searchInput.sendKeys(jobCode);
            searchInput.sendKeys(Keys.ENTER);
            logAction("Submitted job code using ENTER key.");
        } catch (Exception clickOrTypeException) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            try {
                logAction("Native job search typing failed, using JavaScript fallback.");
                js.executeScript("arguments[0].scrollIntoView({block:'center'});", searchInput);
                js.executeScript("arguments[0].value=arguments[1];", searchInput, jobCode);
                js.executeScript("arguments[0].dispatchEvent(new Event('input', {bubbles:true}));", searchInput);
                js.executeScript("arguments[0].dispatchEvent(new Event('change', {bubbles:true}));", searchInput);
                js.executeScript(
                        "arguments[0].dispatchEvent(new KeyboardEvent('keydown', {key:'Enter', code:'Enter', keyCode:13, which:13, bubbles:true}));",
                        searchInput
                );
                js.executeScript(
                        "arguments[0].dispatchEvent(new KeyboardEvent('keyup', {key:'Enter', code:'Enter', keyCode:13, which:13, bubbles:true}));",
                        searchInput
                );
                logAction("Submitted job code using JavaScript fallback.");
            } catch (Exception ignored) {
                try {
                    logAction("JavaScript fallback failed, using direct sendKeys fallback.");
                    searchInput.sendKeys(jobCode);
                    searchInput.sendKeys(Keys.ENTER);
                } catch (Exception fallbackIgnored) {
                    // Keep the JS-set value and let the result wait handle the rest.
                }
            }
        }

        waitForSearchResults(jobCode, 12);
        logAction("Search results loaded for job code: " + jobCode);
    }

    private void openMoreActionsMenuForSearchedJob(String jobCode) {
        openMoreActionsMenuForSearchedJob(jobCode, allocateEmployeeMenuOptions);
    }

    private void openMoreActionsMenuForSearchedJob(String jobCode, By[] expectedMenuOptions) {
        logAction("Opening more-actions menu for searched job code: " + jobCode);
        waitForBlockingUiToClear(8);
        List<By> strategies = new ArrayList<>();
        String jobCodeLiteral = toXPathLiteral(jobCode);

        hoverSearchResultRow(jobCodeLiteral);
        waitForBlockingUiToClear(3);

        WebElement rowScopedMoreActions = findRowScopedMoreActionsButton(jobCode);
        if (rowScopedMoreActions != null) {
            logAction("Trying row-scoped more-actions button for job code: " + jobCode);
            if (clickElementWithFallback(rowScopedMoreActions) && waitForMenuVisibility(expectedMenuOptions, 3)) {
                logAction("More-actions menu opened from row-scoped button for job code: " + jobCode);
                return;
            }
        }

        strategies.add(By.xpath("//*[contains(normalize-space(), " + jobCodeLiteral + ")]/ancestor::*[self::div or self::li][1]//*[name()='svg' and contains(@data-testid,'More')]/ancestor::button[1]"));
        strategies.add(By.xpath("//*[contains(normalize-space(), " + jobCodeLiteral + ")]/ancestor::*[self::div or self::li or self::tr or self::td][1]//button[.//*[name()='svg' and not(contains(@data-testid,'KeyboardArrow'))]]"));
        strategies.add(By.xpath("//*[contains(normalize-space(),'Open-Active') or contains(normalize-space(),'Allocation Complete') or contains(normalize-space(),'Pending Approval')]/ancestor::*[self::div or self::li or self::tr or self::td][1]//*[name()='svg' and contains(@data-testid,'More')]/ancestor::button[1]"));
        strategies.add(By.xpath("//*[contains(normalize-space(),'Open-Active') or contains(normalize-space(),'Allocation Complete') or contains(normalize-space(),'Pending Approval')]/ancestor::*[self::div or self::li or self::tr or self::td][1]//button[.//*[name()='svg' and not(contains(@data-testid,'KeyboardArrow'))]]"));
        strategies.add(threeDotsAllocationAction);
        strategies.add(By.xpath("(//*[name()='svg' and @data-testid='MoreVertIcon'])[1]/ancestor::button[1]"));
        strategies.add(By.xpath("(//*[name()='svg' and @data-testid='MoreVertIcon'])[2]/ancestor::button[1]"));
        strategies.add(By.xpath("(//*[name()='svg' and contains(@data-testid,'More')]/ancestor::button[1])[1]"));
        strategies.add(By.xpath("(//*[name()='svg' and contains(@data-testid,'More')]/ancestor::button[1])[2]"));
        strategies.add(ThreeDots);

        for (int i = 0; i < strategies.size(); i++) {
            boolean usePanelBoundsFilter = i >= 2;
            logAction("Trying more-actions strategy " + (i + 1) + ": " + strategies.get(i));
            waitForBlockingUiToClear(3);
            if (clickMenuActionAndWait(strategies.get(i), usePanelBoundsFilter, expectedMenuOptions)) {
                logAction("More-actions menu opened for job code: " + jobCode);
                return;
            }
            hoverSearchResultRow(jobCodeLiteral);
        }

        debugPrintRowActionCandidates();
        throw new NoSuchElementException("Unable to open row action menu for searched job code: " + jobCode);
    }

    private boolean clickMenuActionAndWait(By locator, boolean usePanelBoundsFilter) {
        return clickMenuActionAndWait(locator, usePanelBoundsFilter, allocateEmployeeMenuOptions);
    }

    private boolean clickMenuActionAndWait(By locator, boolean usePanelBoundsFilter, By[] expectedMenuOptions) {
        List<WebElement> elements = driver.findElements(locator);
        for (WebElement element : elements) {
            try {
                if (!element.isDisplayed()) {
                    continue;
                }
                if (usePanelBoundsFilter && !isLikelyLeftPanelRowIcon(element)) {
                    continue;
                }

                logAction("Clicking menu action candidate " + locator);
                if (!clickElementWithFallback(element)) {
                    continue;
                }

                if (waitForMenuVisibility(expectedMenuOptions, 2)) {
                    logAction("Menu visible after clicking candidate " + locator);
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private void hoverSearchResultRow(String jobCodeLiteral) {
        List<By> rowMarkers = new ArrayList<>();
        rowMarkers.add(By.xpath("//*[contains(normalize-space(), " + jobCodeLiteral + ")]"));
        rowMarkers.add(By.xpath("//*[contains(normalize-space(),'Open-Active')]"));
        rowMarkers.add(By.xpath("//*[contains(normalize-space(),'Allocation Complete')]"));
        rowMarkers.add(By.xpath("//*[contains(normalize-space(),'Pending Approval')]"));

        for (By marker : rowMarkers) {
            for (WebElement markerEl : driver.findElements(marker)) {
                try {
                    if (!markerEl.isDisplayed()) {
                        continue;
                    }
                    WebElement row = markerEl;
                    try {
                        row = markerEl.findElement(By.xpath("./ancestor::*[self::div or self::li or self::tr or self::td][1]"));
                    } catch (Exception ignored) {
                    }
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", row);
                    new Actions(driver).moveToElement(row).perform();
                    return;
                } catch (Exception ignored) {
                }
            }
        }
    }

    private WebElement findRowScopedMoreActionsButton(String jobCode) {
        String normalizedJobCode = normalizeToken(jobCode);
        if (normalizedJobCode.isEmpty()) {
            return null;
        }

        By[] rowLocators = {
                By.xpath("//div[contains(@class,'ag-row') and @role='row']"),
                By.xpath("//div[contains(@class,'ag-row')]"),
                By.xpath("//tr")
        };
        By[] rowActionLocators = {
                By.xpath(".//*[name()='svg' and contains(@data-testid,'More')]/ancestor::button[1]"),
                By.xpath(".//button[.//*[name()='svg' and contains(@data-testid,'More')]]"),
                By.xpath(".//button[.//*[name()='svg' and not(contains(@data-testid,'KeyboardArrow'))]]")
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
                    if (!normalizeToken(rowText).contains(normalizedJobCode)) {
                        continue;
                    }

                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", row);
                    new Actions(driver).moveToElement(row).perform();

                    for (By actionLocator : rowActionLocators) {
                        for (WebElement action : row.findElements(actionLocator)) {
                            try {
                                if (action.isDisplayed() && isElementEnabledForAction(action)) {
                                    return action;
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    private boolean waitForAllocateMenuVisibility(int timeoutSeconds) {
        return waitForMenuVisibility(allocateEmployeeMenuOptions, timeoutSeconds);
    }

    private boolean waitForMenuVisibility(By[] menuLocators, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            return wait.until(d -> isAnyVisible(menuLocators));
        } catch (Exception e) {
            return false;
        }
    }

    private void waitForBlockingUiToClear(int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            wait.until(d -> !isAnyVisible(blockingUiLocators));
            return;
        } catch (Exception ignored) {
        }

        if (isAnyVisible(blockingUiLocators)) {
            logAction("Blocking UI is still visible after " + timeoutSeconds + "s. Forcing dismissal of stale overlays.");
            forceDismissBlockingUi();
            try {
                wait.until(d -> !isAnyVisible(blockingUiLocators));
            } catch (Exception ignored) {
            }
        }
    }

    private void forceDismissBlockingUi() {
        try {
            List<WebElement> visibleBlockers = new ArrayList<>();
            for (By locator : blockingUiLocators) {
                for (WebElement element : driver.findElements(locator)) {
                    try {
                        if (element.isDisplayed()) {
                            visibleBlockers.add(element);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }

            if (visibleBlockers.isEmpty()) {
                return;
            }

            logAction("Forcing dismissal of " + visibleBlockers.size() + " blocking overlay element(s).");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            for (WebElement blocker : visibleBlockers) {
                try {
                    js.executeScript(
                            "arguments[0].style.pointerEvents='none';" +
                                    "arguments[0].style.opacity='0';" +
                                    "arguments[0].style.visibility='hidden';" +
                                    "arguments[0].style.display='none';",
                            blocker
                    );
                } catch (Exception ignored) {
                }
            }

            try {
                new Actions(driver).sendKeys(Keys.ESCAPE).perform();
            } catch (Exception ignored) {
            }
        } catch (Exception e) {
            logAction("Unable to force-dismiss blocking UI: " + e.getMessage());
        }
    }

    private boolean waitForCommonAllocationPopup(int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            return wait.until(d -> isAnyVisible(commonAllocationMarkers));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean openFirstAllocatedRowForRelease() {
        Actions rowActions = new Actions(driver);
        By[] allocationRowTargets = {
                By.xpath("(//div[contains(@class,'rct-item') and contains(@class,'item-allocation')])[1]"),
                By.xpath("(//div[contains(@class,'rct-item') and contains(@class,'item-allocation ')])[1]"),
                By.xpath("(//div[contains(@class,'item-allocation')])[1]"),
                By.xpath("(//*[contains(@class,'MuiChip-label') and contains(normalize-space(),'Allocation Complete')]/ancestor::*[self::div or self::tr][1])"),
                By.xpath("(//*[contains(@class,'MuiChip-label') and contains(normalize-space(),'Pending Approval')]/ancestor::*[self::div or self::tr][1])"),
                By.xpath("(//div[contains(@class,'ag-row')]//*[contains(normalize-space(),'Allocation')])[1]")
        };

        for (By locator : allocationRowTargets) {
            try {
                for (WebElement row : driver.findElements(locator)) {
                    if (!row.isDisplayed()) {
                        continue;
                    }
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", row);
                    try {
                        rowActions.doubleClick(row).perform();
                    } catch (Exception ignored) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].dblclick();", row);
                    }
                    waitForAnyVisible(new By[]{
                            releaseResourceActionButtons[0],
                            releaseResourceActionButtons[1],
                            releaseResourceActionButtons[2],
                            releaseResourceActionButtons[3],
                            releaseResourceActionButtons[4]
                    }, 8);
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private void waitForAllocationRowsToLoad(int timeoutSeconds) {
        logAction("Waiting up to " + timeoutSeconds + "s for allocation rows to load.");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        By[] allocationLoadMarkers = {
                releaseResourceActionButtons[0],
                releaseResourceActionButtons[1],
                releaseResourceActionButtons[2],
                releaseResourceActionButtons[3],
                releaseResourceActionButtons[4],
                By.xpath("//div[contains(@class,'rct-item') and contains(@class,'item-allocation')]"),
                By.xpath("//div[contains(@class,'ag-row')]//*[contains(normalize-space(),'Allocation Complete') or contains(normalize-space(),'Pending Approval')]"),
                By.xpath("//*[contains(@class,'MuiChip-label') and contains(normalize-space(),'Allocation')]")
        };
        try {
            wait.until(d -> isAnyVisible(allocationLoadMarkers));
        } catch (Exception ignored) {
        }
    }

    private void debugPrintAllocationPageState() {
        try {
            logAction("Debugging allocation page state before throwing an error.");
            List<WebElement> visibleButtons = driver.findElements(By.xpath("//button")).stream()
                    .filter(WebElement::isDisplayed)
                    .toList();
            System.out.println("DEBUG RELEASE: visible button count = " + visibleButtons.size());
            int index = 1;
            for (WebElement button : visibleButtons) {
                String text = safeAttr(button, "innerText");
                if (text == null || text.trim().isEmpty()) {
                    text = button.getText();
                }
                System.out.println("DEBUG RELEASE BUTTON[" + index + "] class=" + safeAttr(button, "class")
                        + " aria-label=" + safeAttr(button, "aria-label")
                        + " title=" + safeAttr(button, "title")
                        + " text=" + safeTrim(text));
                index++;
            }

            List<WebElement> visibleSvgs = driver.findElements(By.xpath("//*[name()='svg' and @data-testid='PersonRemoveSharpIcon']")).stream()
                    .filter(WebElement::isDisplayed)
                    .toList();
            System.out.println("DEBUG RELEASE: visible PersonRemoveSharpIcon count = " + visibleSvgs.size());

            List<WebElement> visibleChips = driver.findElements(By.xpath("//*[contains(@class,'MuiChip-label') and contains(normalize-space(.),'Allocation')]")).stream()
                    .filter(WebElement::isDisplayed)
                    .toList();
            System.out.println("DEBUG RELEASE: visible allocation chip count = " + visibleChips.size());
            for (WebElement chip : visibleChips) {
                System.out.println("DEBUG RELEASE CHIP text=" + safeTrim(chip.getText())
                        + " class=" + safeAttr(chip, "class"));
            }
        } catch (Exception e) {
            System.out.println("DEBUG RELEASE: unable to inspect allocation page state: " + e.getMessage());
        }
    }

    private void waitForAnyVisible(By[] locators, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            wait.until(d -> isAnyVisible(locators));
        } catch (Exception ignored) {
        }
    }

    private void waitForSearchResults(String jobCode, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        String jobCodeLiteral = toXPathLiteral(jobCode);
        By exactJobResultMarker = By.xpath("//*[contains(normalize-space(), " + jobCodeLiteral + ")]");

        try {
            wait.until(d -> {
                for (WebElement element : d.findElements(exactJobResultMarker)) {
                    try {
                        if (element.isDisplayed()) {
                            return true;
                        }
                    } catch (Exception ignored) {
                    }
                }
                return false;
            });
        } catch (Exception ignored) {
            logAction("Exact job code marker was not visible within " + timeoutSeconds
                    + "s. Continuing after the search box settled for job code: " + jobCode);
            try {
                wait.until(d -> {
                    WebElement searchInput = firstVisibleElement(new By[]{Search});
                    String currentValue = searchInput == null ? "" : safeAttr(searchInput, "value");
                    return jobCode.equalsIgnoreCase(currentValue) && !isAnyVisible(blockingUiLocators);
                });
            } catch (Exception ignoredToo) {
            }
        }
    }

    private boolean clickElementWithFallback(WebElement element) {
        try {
            String elementTag = safeTrim(element.getTagName());
            String elementClass = safeTrim(element.getAttribute("class"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
            element.click();
            logAction("Native click succeeded for element tag=" + elementTag
                    + " class=" + elementClass);
            return true;
        } catch (Exception clickException) {
            try {
                String elementTag = safeTrim(element.getTagName());
                String elementClass = safeTrim(element.getAttribute("class"));
                logAction("Native click failed, using JS click for element tag=" + elementTag
                        + " class=" + elementClass);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                return true;
            } catch (Exception jsException) {
                return false;
            }
        }
    }

    private WebElement firstVisibleElement(By[] locators) {
        for (By locator : locators) {
            try {
                for (WebElement element : driver.findElements(locator)) {
                    if (element.isDisplayed()) {
                        return element;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private boolean isLikelyLeftPanelRowIcon(WebElement element) {
        Rectangle rect = element.getRect();
        return rect != null && rect.getY() > 120 && rect.getX() < 700;
    }

    private String toXPathLiteral(String value) {
        if (value.contains("'") && value.contains("\"")) {
            String[] parts = value.split("'");
            StringBuilder builder = new StringBuilder("concat(");
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) {
                    builder.append(", \"'\", ");
                }
                builder.append("'").append(parts[i]).append("'");
            }
            builder.append(")");
            return builder.toString();
        }
        if (value.contains("'")) {
            return "\"" + value + "\"";
        }
        return "'" + value + "'";
    }

    private String normalizeToken(String value) {
        return safeTrim(value).toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    private void debugPrintRowActionCandidates() {
        try {
            List<WebElement> statusMarkers = new ArrayList<>();
            statusMarkers.addAll(driver.findElements(By.xpath("//*[contains(normalize-space(),'Open-Active')]")));
            statusMarkers.addAll(driver.findElements(By.xpath("//*[contains(normalize-space(),'Allocation Complete')]")));
            statusMarkers.addAll(driver.findElements(By.xpath("//*[contains(normalize-space(),'Pending Approval')]")));
            if (statusMarkers.isEmpty()) {
                statusMarkers.addAll(driver.findElements(By.xpath("//*[contains(normalize-space(),'"+ AppConstants.UPDATE_ALLOCATION_BY_JOB_CODE +"')]")));
            }
            if (statusMarkers.isEmpty()) {
                System.out.println("DEBUG: No row status marker found for row introspection.");
                return;
            }

            WebElement row = statusMarkers.get(0).findElement(By.xpath("./ancestor::*[self::div or self::li or self::tr or self::td][1]"));
            List<WebElement> controls = row.findElements(By.xpath(".//*[self::button or self::span or self::svg or self::i]"));

            System.out.println("DEBUG: Row action candidates count = " + controls.size());
            int i = 1;
            for (WebElement control : controls) {
                String tag = control.getTagName();
                String cls = safeAttr(control, "class");
                String aria = safeAttr(control, "aria-label");
                String testId = safeAttr(control, "data-testid");
                String title = safeAttr(control, "title");
                String text = control.getText();
                if (text == null) {
                    text = "";
                }
                text = text.trim();
                System.out.println("DEBUG CANDIDATE " + i + " tag=" + tag
                        + " class=" + cls
                        + " aria-label=" + aria
                        + " data-testid=" + testId
                        + " title=" + title
                        + " text=" + text);
                i++;
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Unable to inspect row action candidates: " + e.getMessage());
        }
    }

    private String safeAttr(WebElement element, String name) {
        try {
            String value = element.getAttribute(name);
            return value == null ? "" : value;
        } catch (Exception e) {
            return "";
        }
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String waitForToastMessage(int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            wait.until(d -> isAnyVisible(toastMessageLocators));
        } catch (Exception ignored) {
        }

        for (By locator : toastMessageLocators) {
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

}
