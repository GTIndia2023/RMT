package com.qa.rmt.MyTest;

import RMT.Constants.AppConstants;
import RMT.Errors.AppError;
import RMT.Utils.ExcelUtil;
import com.qa.rmt.base.BaseTest;
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
    @Test
    public void skillMasterPageUrlTest(){
        String actualUrl=skillmasterPage.getSkillMasterPageUrl();
        Assert.assertEquals(actualUrl, AppConstants.SKILL_MASTER_PAGE_URL, AppError.URL_NOT_FOUND);

    }
    @DataProvider (name = "getSkillMasterData")
    public Object[][] getSkillMasterData() {
        return ExcelUtil.getTestData(AppConstants.SKILL_MASTER_DATA_SHEET_NAME);
    }
    @Test(dataProvider = "getSkillMasterData")
    public void skillMasterTest(String skillName, String description , String starting , String building , String skilled, String excelled,String competency,String A1,String A2,String A3, String B1,String B2,String C1,String C2,String D1,String D2 ){
        if (skillName.equals("null")){
            System.out.println("Skill name not found ");
        }
        Assert.assertTrue(skillmasterPage.handleSkill(skillName,description,starting,building,skilled,excelled,competency,A1,A2,A3,B1,B2,C1,C2,D1,D2), AppError.ROWS_TEXT_NOT_FOUND);
    }

}
