package com.qa.rmt.base;

import RMT.Factory.DriverManager;
import RMT.Pages.LoginPage;
import RMT.Pages.ProjectListingsPage;
import RMT.Pages.SkillMasterPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.util.Properties;

public class BaseTest {

    DriverManager df;
    WebDriver driver;
    protected Properties prop;
    protected LoginPage loginPage;// This is excess modfier where we can use the child methods in another classes which have inherited it
    protected ProjectListingsPage projectPage;
    protected SkillMasterPage skillmasterPage;
    @Parameters({"browser"})
    @BeforeTest
    public void setup(@Optional("chrome") String browserName) {
        df = new DriverManager();
        prop=df.initProp();
        if(browserName!=null){
            prop.setProperty("browser", browserName);
        }
        driver=df.initDriver(prop);
        loginPage = new LoginPage(driver);
    }

    @AfterTest
    public void tearDown() {
        driver.quit();
    }
}
