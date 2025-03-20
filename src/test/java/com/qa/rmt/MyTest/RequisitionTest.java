package com.qa.rmt.MyTest;

import RMT.App;
import RMT.Constants.AppConstants;
import RMT.Errors.AppError;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.qa.rmt.base.BaseTest;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import jdk.jfr.Description;
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
    @Description("This test is checking the page Title exists or not ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.NORMAL)
    public void requisitionPageTitleTest() throws InterruptedException {
        String actuaTitle= requisitionPage.getRequisitionPageTitle();
        Assert.assertEquals(actuaTitle, AppConstants.PROJECT_LISTINGS_PAGE_TITILE, AppError.TITLE_NOT_FOUND);
    }
    @Test(priority = 3)
    @Description("This test is checking the page URL exists or not ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.NORMAL)
    public void requisitionPageUrlTest(){
        String actualUrl = requisitionPage.getRequisitionPageUrl().trim();
        String expectedUrl = AppConstants.REQUISITON_PAGE_URL.trim();

        System.out.println("Actual URL: [" + actualUrl + "]");
        System.out.println("Expected URL: [" + expectedUrl + "]");

        Assert.assertTrue(actualUrl.startsWith(expectedUrl),
                "❌ URL NOT FOUND: Expected URL does not match the beginning of the actual URL.");
    }
    @Test(priority = 2)
    @Description("This test is checking that user is able to create the requisition or not  ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.BLOCKER)
    public void checkRequisiton(){
        Assert.assertTrue(requisitionPage.CreateRequisition(),AppConstants.CREATE_REQUISITION_FAILED);
    }
    @Test(priority = 4)
    @Description("This test is checking that user is able to view correct system suggestion card or not   ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.BLOCKER)
    public void checkSystemSuggestion(){
        Assert.assertTrue(requisitionPage.searchAlgo(),AppConstants.SYSTEM_SUGGESTION_CARD_NOT_VISIBLE);
    }
    @Test(priority = 5)
    @Description("This test is checking that user is able to view correct system suggestion card or not   ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.BLOCKER)
    public void updateRequistion(){
        Assert.assertTrue(requisitionPage.updateRequisiton(),AppConstants.UPDATE_REQUISITION_FAILED);
    }
    @Test(priority = 6)
    @Description("This test is checking that user is able to view correct system suggestion card or not   ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.BLOCKER)
    public void updateSeachAlgo(){
        Assert.assertTrue(requisitionPage.updatedSearchAlgo(), AppConstants.SYSTEM_SUGGESTION_CARD_NOT_VISIBLE);
    }
    @Test(priority = 7)
    @Description("This test is checking that user is able to view correct system suggestion card or not   ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void checkExport(){
        Assert.assertTrue(requisitionPage.exportRequisition(),AppConstants.EXPORT_BUTTON_ON_REQUISITON_PAGE);
    }
    @Test(priority = 8)
    @Description("This test is checking that user is able to delete the requisition or not  ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void checkDeleteRequisiton(){
        Assert.assertTrue(requisitionPage.requisitonDeletion(),AppConstants.DELETE_REQUISITON_BUTTON);
    }
}
