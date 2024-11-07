package com.qa.rmt.MyTest;

import RMT.Constants.AppConstants;
import RMT.Errors.AppError;
import RMT.Exceptions.ElementException;
import com.qa.rmt.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {
    @Test(priority = 1)
    public void loginPageTitleTest(){
        String actuaTitle=loginPage.getLoginPageTitle();
        Assert.assertEquals(actuaTitle, AppConstants.LOGIN_PAGE_TITLE, AppError.TITLE_NOT_FOUND);
    }
    @Test(priority = 2)
    public void LoginTest(){
        projectPage =loginPage.doLogin(prop.getProperty("username"),prop.getProperty("password"));
        Assert.assertEquals(projectPage.getProjectListingsPageTitle(),AppConstants.PROJECT_LISTINGS_PAGE_TITILE,AppError.TITLE_NOT_FOUND);
    }


}
