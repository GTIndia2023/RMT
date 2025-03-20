package com.qa.rmt.MyTest;

import RMT.Constants.AppConstants;
import RMT.Errors.AppError;
import RMT.Exceptions.ElementException;
import com.qa.rmt.base.BaseTest;
import io.qameta.allure.*;
import jdk.jfr.Description;
import org.testng.Assert;
import org.testng.annotations.Test;
@Epic("EP001: Design an inhouse app for resource management")
@Story("US:001= Create a login page for RMT Application")
public class LoginTest extends BaseTest {
    @Test(priority = 1)
    @Description("This test is checking the page Title exists or not ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.NORMAL)
    public void loginPageTitleTest(){
        String actuaTitle=loginPage.getLoginPageTitle();
        Assert.assertEquals(actuaTitle, AppConstants.LOGIN_PAGE_TITLE, AppError.TITLE_NOT_FOUND);
    }
    @Test(priority = 2)
    @Description("This test is checking the user is able to login or not ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.BLOCKER)
    @Link("https://rms-uat.wcgt.in/")
    public void LoginTest(){
        projectPage =loginPage.doLogin(prop.getProperty("username"),prop.getProperty("password"));
        Assert.assertEquals(projectPage.getProjectListingsPageTitle(),AppConstants.PROJECT_LISTINGS_PAGE_TITILE,AppError.TITLE_NOT_FOUND);
    }


}
