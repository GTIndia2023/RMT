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
    private By downArrow=By.xpath("//*[name()='svg' and @data-testid='KeyboardArrowRightIcon']");
    private By effortHrs=By.xpath("(//input[@type='number'])[2]");
    private By OkBtn=By.xpath("(//button[@type='submit'])");
    private By CheckboxBtn=By.xpath("(//input[@type='checkbox'])[2]");
    private By allocateBtn=By.xpath("(//button[text()='Allocate'])");
    private By yesBtn=By.xpath("(//button[text()='Yes'])");
    private By allocationStatus=By.xpath("//div[@title='Allocation complete']");
    private By detailView=By.xpath("//button[text()='Detail View']");
    private By delegateDropdown=By.xpath("//input[@placeholder='Type And Select']");
    private By updateDetailsBtn=By.xpath("//button[text()='Update Details']");
    private By saveBtn=By.xpath("(//button[@type='button'])[23]");
    private By updateSuccessFullMesg=By.xpath("//div[text()='Project details updated successfully!']");
    private By AddMoreIcon=By.xpath("//*[normalize-space(text())='Add more']//*[name()='svg' and @data-testid='AddCircleOutlinedIcon']");

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
     * This method is used to perform update of an existing allocation
     */
    public String updateAllocation(){
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
        act.doubleClick(driver.findElement(By.xpath("(//div[@class='rct-item-content'])[2]"))).perform();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //eleutil.doActionsClick(updateAllocation);
        act.doubleClick(driver.findElement(By.xpath("(//div[@class='rct-item-content'])[2]"))).perform();
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
        return status;
    }

    /**
     * This method is used to check the addition and updation of delegate functionality
     * @return
     */
    public String checkAllocateDelegate(){
        Actions act= new Actions(driver);
        eleutil.doActionsClick(detailView);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsSendKeysWithPause(delegateDropdown,"RMSED.Offeringleader@IN.GT.COM",10);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        eleutil.doClick(updateDetailsBtn,10);
        eleutil.doClick(saveBtn);
        String successMesg=eleutil.doGetText(updateSuccessFullMesg);
        System.out.println("Delegate Addition/Update Successfully  " + successMesg);
        return successMesg;
    }


}
