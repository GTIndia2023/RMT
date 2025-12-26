package com.qa.rmt.MyTest;

import RMT.Constants.AppConstants;
import RMT.Errors.AppError;
import RMT.Utils.ExcelUtil;
import com.qa.rmt.base.BaseTest;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import jdk.jfr.Description;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;


public class SkillMasterTest extends BaseTest {
    @BeforeClass
    public void accSetup(){
        projectPage=loginPage.doLogin(prop.getProperty("username"),prop.getProperty("password"));
        skillmasterPage=projectPage.navigateToskillMasterPage();
    }
    @Step("This is used to check the title of the SkillMaster page")
    @Test(priority = 1)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void SkillPageTitleTest(){
        String actuaTitle= skillmasterPage.getSkillMasterPageTitle();
        Assert.assertEquals(actuaTitle, AppConstants.SKILL_MASTER_PAGE_TITLE, AppError.TITLE_NOT_FOUND);
    }
    @Step("This test is used to test the URL of the skillMaster page ")
    @Test(priority = 2)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.MINOR)
    public void skillMasterPageUrlTest(){
        String actualUrl=skillmasterPage.getSkillMasterPageUrl();
        Assert.assertEquals(actualUrl, AppConstants.SKILL_MASTER_PAGE_URL, AppError.URL_NOT_FOUND);

    }
    @Step("This method is used to populate the data fetched from skill Master Excel")
    @DataProvider (name = "getSkillMasterData")
    public Object[][] getSkillMasterData() {
        return ExcelUtil.getTestData(AppConstants.SKILL_MASTER_DATA_SHEET_NAME);
    }
    @Step( "This method is used to upload the skills from skill master excel file")
    @Test(dataProvider = "getSkillMasterData",priority = 3)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void skillMasterTest(String fileName ,String skillID,String skillname , String description , String AnyRemarks,String starting , String building , String skilled, String excelled, String A1_P_GR00001,String A2_ED_GR00002,String A3_D_GR00003, String B1_AD_GR00004,String B2_M_GR00005,String C1_AM_GR00006,String C2_SA_GR00007,String D1_GT_GR00008,String D2_T_GR00009,String BU,String Expertise,String Specialisation,String competency ,String Classification,String Category ){
        if (skillname.equals("null")){
            System.out.println("Skill name not found ");
        }else {
        Assert.assertTrue(skillmasterPage.handleSkill(fileName,skillID,skillname,description,AnyRemarks,starting,building,skilled,excelled,A1_P_GR00001,A2_ED_GR00002,A3_D_GR00003,B1_AD_GR00004,B2_M_GR00005,C1_AM_GR00006,C2_SA_GR00007,D1_GT_GR00008,D2_T_GR00009,BU,Expertise,Specialisation,competency,Classification,Category), AppError.ROWS_TEXT_NOT_FOUND);
    }}
    @Step( "This method is used to upload the Failed skills from skill master excel file")
    @Test(dataProvider = "getSkillMasterData",priority = 4)
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void failedSkillTest(String fileName ,String skillID,String skillname , String description , String AnyRemarks,String starting , String building , String skilled, String excelled, String A1_P_GR00001,String A2_ED_GR00002,String A3_D_GR00003, String B1_AD_GR00004,String B2_M_GR00005,String C1_AM_GR00006,String C2_SA_GR00007,String D1_GT_GR00008,String D2_T_GR00009,String BU,String Expertise,String Specialisation,String competency ,String Classification,String Category ){
        if (skillname.equals("null")){
            System.out.println("Skill name not found ");
        }else {
            Assert.assertTrue(skillmasterPage.handleFailedSkill(fileName,skillID,skillname,description,AnyRemarks,starting,building,skilled,excelled,A1_P_GR00001,A2_ED_GR00002,A3_D_GR00003,B1_AD_GR00004,B2_M_GR00005,C1_AM_GR00006,C2_SA_GR00007,D1_GT_GR00008,D2_T_GR00009,BU,Expertise,Specialisation,competency,Classification,Category), AppError.ROWS_TEXT_NOT_FOUND);
        }}
    @Test(priority = 5)
    @Description("This test is checking that user is able to perform skill search  ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.NORMAL)
    public void skillSearchBySkillNameTest(){
        Assert.assertTrue(skillmasterPage.skillSearch(),AppConstants.SKILL_SEARCH_BY_SKILL_NAME_NOT_FOUND);
    }
    @Test(priority = 6)
    @Description("This test is checking that user is able to perform skill search  ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.NORMAL)
    public void skillSearchByEmplNameTest(){
        Assert.assertTrue(skillmasterPage.searchByEmployeeName(),AppConstants.SKILL_SEARCH_BY_EMPLOYEE_NAME_NOT_FOUND);
    }
    @Test(priority = 7)
    @Description("This test is checking that user is able to add a skill ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    public void addingSkillTest(){
        Assert.assertTrue(skillmasterPage.addSkill(),AppConstants.SKILL_SEARCH_BY_EMPLOYEE_NAME_NOT_FOUND);
    }

}
