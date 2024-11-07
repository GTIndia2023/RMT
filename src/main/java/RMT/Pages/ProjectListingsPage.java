package RMT.Pages;

import RMT.Constants.AppConstants;
import RMT.Exceptions.ElementException;
import RMT.Utils.ElementUtil;
import RMT.Utils.TimeUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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
        try {
            eleutil.handleParentSubMenu(skillsBtn,skillMasterBtn);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new ElementException("Element not visible on the page yet ");
        }
        return new SkillMasterPage(driver);

    }


}
