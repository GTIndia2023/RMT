package com.qa.rmt.MyTest;

import RMT.Constants.AppConstants;
import RMT.Errors.AppError;
import com.qa.rmt.base.BaseTest;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import jdk.jfr.Description;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ReportsPageTest extends BaseTest {
    @BeforeClass
    public void accSetup(){
        projectPage=loginPage.doLogin(prop.getProperty("username"),prop.getProperty("password"));
        reportsPage=projectPage.navigateToReportsPage();
    }

    @Step("This is used to check the title of the Reports page")
    @Test(priority = 1)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void ReportsPageTitleTest(){
        String actuaTitle= reportsPage.getReportsPageTitle();
        Assert.assertEquals(actuaTitle, AppConstants.SKILL_MASTER_PAGE_TITLE, AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to test the URL of the Reports page ")
    @Test(priority = 2)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void ReportsPageUrlTest(){
        String actualUrl=reportsPage.getReportsPageUrl();
        Assert.assertEquals(actualUrl, AppConstants.REPORTS_PAGE_URL, AppError.URL_NOT_FOUND);

    }
    @Step("This test is used to verify the card1 Title is present or not by fetching the text and verifying it ")
    @Test(priority = 3)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void card1TitleTest(){
        String actualTitle1=reportsPage.checkcard1Title();
        Assert.assertEquals(actualTitle1,AppConstants.REPORTS_PAGE_CARD1_TITLE,AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to verify the card2 Title is present or not by fetching the text and verifying it ")
    @Test(priority = 4)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void card2TitleTest(){
        String actualTitle1=reportsPage.checkcard2Title();
        Assert.assertEquals(actualTitle1,AppConstants.REPORTS_PAGE_CARD2_TITLE,AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to verify the card3 Title is present or not by fetching the text and verifying it ")
    @Test(priority = 5)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void card3TitleTest(){
        String actualTitle1=reportsPage.checkcard3Title();
        Assert.assertEquals(actualTitle1,AppConstants.REPORTS_PAGE_CARD3_TITLE,AppError.TITLE_NOT_FOUND);
    }
    @Step("TThis test is used to verify the card4 Title is present or not by fetching the text and verifying it ")
    @Test(priority = 6)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void card4TitleTest(){
        String actualTitle1=reportsPage.checkcard4Title();
        Assert.assertEquals(actualTitle1,AppConstants.REPORTS_PAGE_CARD4_TITLE,AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to verify the card5 Title is present or not by fetching the text and verifying it ")
    @Test(priority = 7)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void card5TitleTest(){
        String actualTitle1=reportsPage.checkcard5Title();
        Assert.assertEquals(actualTitle1,AppConstants.REPORTS_PAGE_CARD5_TITLE,AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to verify the card6 Title is present or not by fetching the text and verifying it ")
    @Test(priority = 8)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void card6TitleTest(){
        String actualTitle1=reportsPage.checkcard6Title();
        Assert.assertEquals(actualTitle1,AppConstants.REPORTS_PAGE_CARD6_TITLE,AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to verify the card7 Title is present or not by fetching the text and verifying it ")
    @Test(priority = 9)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void card7TitleTest(){
        String actualTitle1=reportsPage.checkcard7Title();
        Assert.assertEquals(actualTitle1,AppConstants.REPORTS_PAGE_CARD7_TITLE,AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to verify the card8 Title is present or not by fetching the text and verifying it ")
    @Test(priority = 10)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void card8TitleTest(){
        String actualTitle1=reportsPage.checkcard8Title();
        Assert.assertEquals(actualTitle1,AppConstants.REPORTS_PAGE_CARD8_TITLE,AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to verify the card1 Data  is present or not by fetching the text and verifying it ")
    @Test(priority = 11)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void card1DataTest(){
        Assert.assertTrue(reportsPage.checkCard1Data(),AppConstants.REPORTS_PAGE_CARD1_DATA);
    }
    @Step("This test is used to verify the card2 Data  is present or not by fetching the text and verifying it ")
    @Test(priority = 12)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void card2DataTest(){
        Assert.assertTrue(reportsPage.checkCard2Data(),AppConstants.REPORTS_PAGE_CARD2_DATA);
    }
    @Step("This test is used to verify the card3 Data  is present or not by fetching the text and verifying it ")
    @Test(priority = 13)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void card3DataTest(){
        Assert.assertTrue(reportsPage.checkCard3Data(),AppConstants.REPORTS_PAGE_CARD3_DATA);
    }
    @Step("This test is used to verify the card4 Data  is present or not by fetching the text and verifying it ")
    @Test(priority = 14)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void card4DataTest(){
        Assert.assertTrue(reportsPage.checkCard4Data(),AppConstants.REPORTS_PAGE_CARD4_DATA);
    }
    @Step("This test is used to verify the card5 Data  is present or not by fetching the text and verifying it ")
    @Test(priority = 15)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void card5DataTest(){
        Assert.assertTrue(reportsPage.checkCard5Data(),AppConstants.REPORTS_PAGE_CARD5_DATA);
    }
    @Step("This test is used to verify the card6 Data  is present or not by fetching the text and verifying it ")
    @Test(priority = 16)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void card6DataTest(){
        Assert.assertTrue(reportsPage.checkCard6Data(),AppConstants.REPORTS_PAGE_CARD6_DATA);
    }
    @Step("This test is used to verify the card7 Data  is present or not by fetching the text and verifying it ")
    @Test(priority = 17)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void card7DataTest(){
        Assert.assertTrue(reportsPage.checkCard7Data(),AppConstants.REPORTS_PAGE_CARD7_DATA);
    }
    @Step("This test is used to verify the card Start Date is present verifying it ")
    @Test(priority = 18)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void cardStartDateTest(){
        Assert.assertTrue(reportsPage.checkStartDate(),AppConstants.REPORTS_PAGE_START_DATE);
    }
    @Step("This test is used to verify the card End Date is present verifying it ")
    @Test(priority = 19)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void cardEndDateTest(){
        Assert.assertTrue(reportsPage.checkEndDate(),AppConstants.REPORTS_PAGE_END_DATE);
    }
    @Step("This test is used to verify the CapacityCard1 Title  is present or not by fetching the text and verifying it ")
    @Test(priority = 20)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void Capacitycard1TitleTest(){
        String actualTitle1=reportsPage.checkCapacityCard1Title();
        Assert.assertEquals(actualTitle1,AppConstants.CAPACITY_REPORTS_PAGE_CARD1_TITLE,AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to verify the CapacityCard2 Title  is present or not by fetching the text and verifying it  ")
    @Test(priority = 21)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void Capacitycard2TitleTest(){
        String actualTitle1=reportsPage.checkCapacityCard2Title();
        Assert.assertEquals(actualTitle1,AppConstants.CAPACITY_REPORTS_PAGE_CARD2_TITLE,AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to verify the CapacityCard3 Title  is present or not by fetching the text and verifying it  ")
    @Test(priority = 22)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void Capacitycard3TitleTest(){
        String actualTitle1=reportsPage.checkCapacityCard3Title();
        Assert.assertEquals(actualTitle1,AppConstants.CAPACITY_REPORTS_PAGE_CARD3_TITLE,AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to verify the CapacityCard4 Title  is present or not by fetching the text and verifying it  ")
    @Test(priority = 23)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void Capacitycard4TitleTest(){
        String actualTitle1=reportsPage.checkCapacityCard4Title();
        Assert.assertEquals(actualTitle1,AppConstants.CAPACITY_REPORTS_PAGE_CARD4_TITLE,AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to verify the CapacityCard1 Data  is present or not by fetching the text and verifying it  ")
    @Test(priority = 24)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void capacityCard1DataTest(){
        Assert.assertTrue(reportsPage.checkCapacityCard1Data(),AppConstants.CAPACITY_REPORTS_PAGE_CARD1_DATA);
    }
    @Step("This test is used to verify the CapacityCard2 Data  is present or not by fetching the text and verifying it  ")
    @Test(priority = 25)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void capacityCard2DataTest(){
        Assert.assertTrue(reportsPage.checkCapacityCard2Data(),AppConstants.CAPACITY_REPORTS_PAGE_CARD2_DATA);
    }
    @Step("This test is used to verify the CapacityCard3 Data  is present or not by fetching the text and verifying it  ")
    @Test(priority = 26)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void capacityCard3DataTest(){
        Assert.assertTrue(reportsPage.checkCapacityCard3Data(),AppConstants.CAPACITY_REPORTS_PAGE_CARD3_DATA);
    }
    @Step("This test is used to verify the CapacityCard4 Data  is present or not by fetching the text and verifying it  ")
    @Test(priority = 24)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void capacityCard4DataTest(){
        Assert.assertTrue(reportsPage.checkCapacityCard4Data(),AppConstants.CAPACITY_REPORTS_PAGE_CARD4_DATA);
    }
    @Test(priority = 25)
    @Description("This test is checking that user is able to export the main grid data or not ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.NORMAL)
    public void checkExportTest(){
        Assert.assertTrue(reportsPage.exportGridData(),AppConstants.EXPORT_BUTTON_ON_CAPACITY_REPORTS_PAGE);
    }
    @Test(priority = 26)
    @Description("This test is checking that user is able to export the data for specific BU or not ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void checkExport2Test(){
        Assert.assertTrue(reportsPage.exportIndividualBuData(),AppConstants.EXPORT_BUTTON_ON_CAPACITY_REPORTS_PAGE);
    }
    @Test(priority = 27)
    @Description("This test is checking that user is able view the data on schedule vs variance chart ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void scheduleVsVarianceChartTest(){
        Assert.assertTrue(reportsPage.checkScheduleChartVisisble(),AppConstants.SCHEDULE_REPORTS_PAGE_DATA);
    }
    @Test(priority = 28)
    @Description("This test is checking that user is able to export the schedule vs variance grid data ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void scheduleVsVarianceCardExportTest(){
        Assert.assertTrue(reportsPage.exportScheduleGridData(),AppConstants.EXPORT_BUTTON_ON_SCHEDULE_REPORTS_PAGE);
    }
}
