package RMT.Pages;

import RMT.App;
import RMT.Constants.AppConstants;
import RMT.Exceptions.ElementException;
import RMT.Utils.ElementUtil;
import RMT.Utils.TimeUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class ProjectListingsPage {
    private WebDriver driver;
    ElementUtil eleutil;

    //1.ProjectListings page constructor
    public  ProjectListingsPage(WebDriver driver){
        this.driver=driver;
        eleutil=new ElementUtil(driver);
    }

    //2. ProjectListing page By locators
    private By skillsBtn=By.id("basic-menu-15");
    private By skillMasterBtn=By.xpath("//a[@href='/skillmaster']");
    private By Search = By.xpath("//input[@placeholder='Search']");
    private By JobCode=By.xpath("//span[@aria-label='Deals Structuring-Inteligent Hub-2425-01-P-1']");
    private By ThreeDots = By.xpath("(//span[@class='MuiTouchRipple-root css-w0pj6f'])[18]");
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
    private By clickonPencilIcon= By.xpath("(//button[@type='button'])[15]");
    private By addingDescription=By.xpath("(//textarea[@class='MuiInputBase-input MuiOutlinedInput-input MuiInputBase-inputMultiline css-u36398'])[1]");
    private By clickonDescriptionSaveBtn= By.xpath(" //*[local-name()='svg' and @data-testid='SaveIcon']");
    private By navigateReportsPage=By.xpath("//li[text()='Reports']");
    private By budgetDetails=By.xpath("//button[text()='Budget Details']");
    private By startDate=By.xpath("(//input[@placeholder='DD-MM-YYYY'])");

    //3.Page actions

    /**
     *This method is used to get the projectLiting page title to verify that
     * user is on the project listings page
     * @return title as String
     * @throws  ElementException
     */
    public String getProjectListingsPageTitle() {
        String title=eleutil.waitForTitleToBe(AppConstants.PROJECT_LISTINGS_PAGE_TITILE, TimeUtil.MEDIUM_TIME_OUT);
        System.out.println("Project Listings Page  title is "+ title);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
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
        String url=eleutil.waitForURLToBe(AppConstants.PROJECT_LISTING_PAGE_URL,TimeUtil.DEFAULT_TIME_OUT);
        System.out.println("Project listing page Url is " + url);
        return url;
    }

    /**
     * This method is used to navigate to skill master page
     * @return skillMaster Page object
     */
    public SkillMasterPage navigateToskillMasterPage(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(skillsBtn);
        eleutil.handleParentSubMenu(skillsBtn,skillMasterBtn);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new ElementException("Element not visible on the page yet ");
        }
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

}
