package com.qa.rmt.MyTest;

import RMT.Constants.AppConstants;
import RMT.Errors.AppError;
import com.qa.rmt.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ProjectListingTest extends BaseTest {
    @BeforeClass
    public void accSetup(){
        projectPage=loginPage.doLogin(prop.getProperty("username"),prop.getProperty("password"));
    }
    @Test(priority = 1)
    public void projectListingTitleTest() throws InterruptedException {
        String actuaTitle= projectPage.getProjectListingsPageTitle();
        Assert.assertEquals(actuaTitle, AppConstants.PROJECT_LISTINGS_PAGE_TITILE, AppError.TITLE_NOT_FOUND);
    }
    @Test(priority = 2)
    public void projectListingUrlTest(){
        String actualUrl=projectPage.getProjectListingPageUrl();
       Assert.assertEquals(actualUrl,AppConstants.PROJECT_LISTING_PAGE_URL,AppError.URL_NOT_FOUND);

    }
    @Test(priority = 3)
    public void navigateToSkillMasterTest() throws InterruptedException {
        skillmasterPage=projectPage.navigateToskillMasterPage();
        Assert.assertEquals(skillmasterPage.getSkillMasterPageTitle(),AppConstants.SKILL_MASTER_PAGE_TITLE,AppError.TITLE_NOT_FOUND);
    }
}
