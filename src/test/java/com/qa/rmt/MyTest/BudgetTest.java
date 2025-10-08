package com.qa.rmt.MyTest;

import RMT.Constants.AppConstants;
import RMT.Errors.AppError;
import com.qa.rmt.base.BaseTest;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BudgetTest extends BaseTest {
    @BeforeClass
    public void accSetup(){
        projectPage=loginPage.doLogin(prop.getProperty("username"),prop.getProperty("password"));
        budgetPage=projectPage.navigateToProjectBudgetPage();
    }

    @Step("This is used to check the title of the ProjectBudget page")
    @Test(priority = 1)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void budgetPageTitleTest(){
        String actuaTitle= budgetPage.getbudgetPageTitle();
        Assert.assertEquals(actuaTitle, AppConstants.SKILL_MASTER_PAGE_TITLE, AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to test the Title of the chard1 ")
    @Test(priority = 2)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void card1TitleTest(){
        String actualTitle1=budgetPage.checkcard1Title();
        Assert.assertEquals(actualTitle1,AppConstants.BUDGET_PAGE_CARD1_TITLE,AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to test the Title of the Card2 ")
    @Test(priority = 3)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void card2TitleTest(){
        String actualTitle1=budgetPage.checkcard2Title();
        Assert.assertEquals(actualTitle1,AppConstants.BUDGET_PAGE_CARD2_TITLE,AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to test the Title of the Card3 ")
    @Test(priority = 4)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void card3TitleTest(){
        String actualTitle1=budgetPage.checkcard3Title();
        Assert.assertEquals(actualTitle1,AppConstants.BUDGET_PAGE_CARD3_TITLE,AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to test the Title of the Card4 ")
    @Test(priority = 5)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void card4TitleTest(){
        String actualTitle1=budgetPage.checkcard4Title();
        Assert.assertEquals(actualTitle1,AppConstants.BUDGET_PAGE_CARD4_TITLE,AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to test the Title of the Card5 ")
    @Test(priority = 6)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void card5TitleTest(){
        String actualTitle1=budgetPage.checkcard5Title();
        Assert.assertEquals(actualTitle1,AppConstants.BUDGET_PAGE_CARD5_TITLE,AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to test the Title of the Card6 ")
    @Test(priority = 7)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void card6TitleTest(){
        String actualTitle1=budgetPage.checkcard6Title();
        Assert.assertEquals(actualTitle1,AppConstants.BUDGET_PAGE_CARD6_TITLE,AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to test the Data is visible for Card1 ")
    @Test(priority = 8)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void card1DataTest(){
        Assert.assertTrue(budgetPage.checkCard1Data(),AppConstants.BUDGET_PAGE_CARD1_DATA);
    }
    @Step("This test is used to test the Data is visible for Card2 ")
    @Test(priority = 9)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void card2DataTest(){
        Assert.assertTrue(budgetPage.checkCard2Data(),AppConstants.BUDGET_PAGE_CARD2_DATA);
    }
    @Step("This test is used to test the Data is visible for Card3 ")
    @Test(priority = 10)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void card3DataTest(){
        Assert.assertTrue(budgetPage.checkCard3Data(),AppConstants.BUDGET_PAGE_CARD3_DATA);
    }
    @Step("This test is used to test the Data is visible for Card4 ")
    @Test(priority = 11)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void card4DataTest(){
        Assert.assertTrue(budgetPage.checkCard4Data(),AppConstants.BUDGET_PAGE_CARD4_DATA);
    }
    @Step("This test is used to test the Data is visible for Card5 ")
    @Test(priority = 12)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void card5DataTest(){
        Assert.assertTrue(budgetPage.checkCard5Data(),AppConstants.BUDGET_PAGE_CARD5_DATA);
    }
    @Step("This test is used to test the Data is visible for Card6 ")
    @Test(priority = 13)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void card6DataTest(){
        Assert.assertTrue(budgetPage.checkCard6Data(),AppConstants.BUDGET_PAGE_CARD6_DATA);
    }
    @Step("This test is used to test the Data is visible for Chart1 Speedometer")
    @Test(priority = 16)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void chart1DataTest(){
        Assert.assertTrue(budgetPage.checkChart1Data(),AppConstants.BUDGET_PAGE_CHART1_DATA);
    }
    @Step("This test is used to test the Data is visible for Grid Grade Data")
    @Test(priority = 17)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void gridGradeDataTest(){
        Assert.assertTrue(budgetPage.checkGradesData(),AppConstants.BUDGET_PAGE_GRID_GRADE_DATA);
    }
    @Step("This test is used to test the Data is visible for Grid Budget Data")
    @Test(priority = 18)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void gridBudgetDataTest(){
        Assert.assertTrue(budgetPage.checkGridBudgetData(),AppConstants.BUDGET_PAGE_GRID_BUDGET_DATA);
    }
    @Step("This test is used to test the Data is visible for Grid Original Budget Data")
    @Test(priority = 19)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void gridOrignalBudgetDataTest(){
        Assert.assertTrue(budgetPage.checkGridOrignalData(),AppConstants.BUDGET_PAGE_GRID_ORIGNAL_BUDGET_DATA);
    }
    @Step("This test is used to test the Data is visible for Chart 3 ")
    @Test(priority = 20)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void chart3DataTest(){
        Assert.assertTrue(budgetPage.checkChart3Data(),AppConstants.BUDGET_PAGE_CHART3_DATA);
    }
    @Step("This test is used to test the Data is visible for Chart 3 Monthly Data")
    @Test(priority = 21)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void chart3MonthlyDataTest(){
        Assert.assertTrue(budgetPage.checkChart3MonthlyData(),AppConstants.BUDGET_PAGE_CHART3_MONTHLY_DATA);
    }
    @Step("This test is used to test the Data is visible for Chart 3 Quarterly Data  ")
    @Test(priority = 22)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void chart3QuarterlyDataTest(){
        Assert.assertTrue(budgetPage.checkChart3QuaterlyData(),AppConstants.BUDGET_PAGE_CHART3_QUARTERLY_DATA);
    }
    @Step("This test is used to test the Data is visible for Chart 4 ")
    @Test(priority = 23)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void chart4DataTest(){
        Assert.assertTrue(budgetPage.checkChart4Data(),AppConstants.BUDGET_PAGE_CHART4_DATA);
    }
    @Step("This test is used to test the Data is visible for Chart 4 After applying Location DEL  Filter ")
    @Test(priority = 24)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void chart4Location1FilterTest(){
        Assert.assertTrue(budgetPage.checkChart4FilterLocation1Data(),AppConstants.BUDGET_PAGE_CHART4_LOCATION1_DATA);
    }
    @Step("This test is used to test the Data is visible for Chart 4 After applying Location GUR Filter ")
    @Test(priority = 25)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void chart4Location2FilterTest(){
        Assert.assertTrue(budgetPage.checkChart4FilterLocation2Data(),AppConstants.BUDGET_PAGE_CHART4_LOCATION2_DATA);
    }
    @Step("This test is used to test the Data is visible for Chart 4 After applying GRADE Filter ")
    @Test(priority = 26)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void chart4GradeFilterTest(){
        Assert.assertTrue(budgetPage.checkChart4grade1Data(),AppConstants.BUDGET_PAGE_CHART4_GRADE_DATA);
    }
    @Step("This test is used to test the Data is visible for Chart 4 After applying Designation  Filter ")
    @Test(priority = 27)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void chart4DesignationFilterTest(){
        Assert.assertTrue(budgetPage.checkChart4Designation1Data(),AppConstants.BUDGET_PAGE_CHART4_DESIGNATION_DATA);
    }
    @Step("This test is used to test the Data is visible for Chart 4 After applying BU  Filter ")
    @Test(priority = 28)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void chart4BuFilterTest(){
        Assert.assertTrue(budgetPage.checkChart4Bu1Data(),AppConstants.BUDGET_PAGE_CHART4_BU_DATA);
    }
    @Step("This test is used to test the Data is visible for Chart 4 After applying Competency  Filter ")
    @Test(priority = 29)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void chart4CompetencyFilterTest(){
        Assert.assertTrue(budgetPage.checkChart4Competency1Data(),AppConstants.BUDGET_PAGE_CHART4_COMPETENCY_DATA);
    }


}
