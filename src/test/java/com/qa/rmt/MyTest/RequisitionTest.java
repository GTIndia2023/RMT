package com.qa.rmt.MyTest;

import RMT.Constants.AppConstants;
import RMT.Errors.AppError;
import com.qa.rmt.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RequisitionTest extends BaseTest {
    @BeforeClass
    public void accSetup(){
        projectPage=loginPage.doLogin(prop.getProperty("username"),prop.getProperty("password"));
        requisitionPage=projectPage.navigateToRequisitonPage();
    }
    @Test(priority = 1)
    public void requisitionPageTitleTest() throws InterruptedException {
        String actuaTitle= requisitionPage.getRequisitionPageTitle();
        Assert.assertEquals(actuaTitle, AppConstants.PROJECT_LISTINGS_PAGE_TITILE, AppError.TITLE_NOT_FOUND);
    }
    @Test(priority = 3)
    public void requisitionPageUrlTest(){
//        String actualUrl= requisitionPage.getRequisitionPageUrl();
//        Assert.assertTrue(actualUrl.trim().contains(AppConstants.REQUISITON_PAGE_URL.trim()),AppError.URL_NOT_FOUND);
        String actualUrl = requisitionPage.getRequisitionPageUrl();
        String expectedUrl = AppConstants.REQUISITON_PAGE_URL;

        System.out.println("Actual URL: " + actualUrl);
        System.out.println("Expected Substring: " + expectedUrl);

        Assert.assertTrue(actualUrl.contains(expectedUrl), "URL NOT FOUND: Expected substring not found in actual URL.");

    }
    @Test(priority = 2)
    public void checkRequisiton(){
        Assert.assertTrue(requisitionPage.CreateRequisition(),AppConstants.CREATE_REQUISITION_FAILED);
    }
    @Test(priority = 4)
    public void checkSystemSuggestion(){
        Assert.assertTrue(requisitionPage.searchAlgo(),AppConstants.SYSTEM_SUGGESTION_CARD_NOT_VISIBLE);
    }
}
