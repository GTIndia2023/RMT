package com.qa.rmt.MyTest;

import RMT.Constants.AppConstants;
import RMT.Errors.AppError;
import RMT.Exceptions.ElementException;
import com.qa.rmt.base.BaseTest;
import io.qameta.allure.*;
import jdk.jfr.Description;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Locale;

@Epic("EP001: Design an inhouse app for resource management")
@Story("US:001= Create a login page for RMT Application")
public class LoginTest extends BaseTest {
    @Test(priority = 1)
    @Description("This test is checking the page Title exists or not ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.NORMAL)
    public void loginPageTitleTest(){
        String actuaTitle=loginPage.getLoginPageTitle();
        String normalizedTitle = actuaTitle == null ? "" : actuaTitle.trim().toLowerCase(Locale.ENGLISH);
        Assert.assertTrue(
                normalizedTitle.equals(AppConstants.LOGIN_PAGE_TITLE.toLowerCase(Locale.ENGLISH))
                        || normalizedTitle.equals("optiwise"),
                AppError.TITLE_NOT_FOUND + " Actual title: " + actuaTitle
        );
    }
    @Test(priority = 2)
    @Description("This test is checking the user is able to login or not ")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.BLOCKER)
    @Link("https://rms-uat.wcgt.in/")
    public void LoginTest(){
        projectPage =loginPage.doLogin(prop.getProperty("username"),prop.getProperty("password"));
        String actualTitle = projectPage.getProjectListingsPageTitle();
        String actualUrl = projectPage.getProjectListingPageUrl();
        Assert.assertFalse(actualTitle == null || actualTitle.trim().isEmpty(),
                AppError.TITLE_NOT_FOUND + " Actual title: " + actualTitle);
        Assert.assertTrue(actualUrl.startsWith(prop.getProperty("url").trim()),
                AppError.URL_NOT_FOUND + " Actual url: " + actualUrl);
    }


}
