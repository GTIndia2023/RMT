package RMT.Pages;

import RMT.Constants.AppConstants;
import RMT.Utils.ElementUtil;
import RMT.Utils.TimeUtil;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class LoginPage {
    private WebDriver driver;
    private ElementUtil eleutil;


    //1.LoginPage constructor
    public LoginPage(WebDriver driver){
        this.driver=driver;
        eleutil=new ElementUtil(driver);
    }

    //2.By locators
    private By emaiInputField=By.xpath("//input[@type='email']");
    private By passwordInputField=By.xpath("//input[@name='passwd']");
    private By nextBtn= By.xpath("//input[@type='submit']");
    private By signInBtn= By.xpath("//input[@type='submit']");
    private By forgotPassword=By.xpath("//a[@id='idA_PWD_ForgotPassword']");
    private By submitBtn= By.xpath("//input[@type='submit']");


    //3. Page actions

    /**
     * This method is used to fetch the loginPage Title
     * @return the title in string form
     */
    @Step("Getting Login Page Title")
    public String getLoginPageTitle() {
        String title = eleutil.waitForTitleToBe(AppConstants.LOGIN_PAGE_TITLE, TimeUtil.DEFAULT_TIME_OUT);
        System.out.println("Login page title is : " + title);
        return title;
    }

    /**
     * This method is used to check whether on login page forgot passwrod link exist
     * @return a boolean value as True or False
     */
    public Boolean isforgotPasswordLinkExists(){
        boolean flag=eleutil.isElementDisplayed(forgotPassword);
        System.out.println(" Forgot my Password link exists " + flag);
        return flag;
    }

    /**
     * This method takes input from config.properties file for username and password to login into the application
     * @param un
     * @param pswd
     * @return ProjectLstings page object
     */
    @Step("Login the Application wih username: {0} and password: ********")
    public ProjectListingsPage doLogin(String un , String pswd){
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doSendKeys(emaiInputField,un,TimeUtil.DEFAULT_TIME_OUT);
        eleutil.doClick(nextBtn,TimeUtil.DEFAULT_TIME_OUT);
        eleutil.doSendKeys(passwordInputField,pswd,TimeUtil.DEFAULT_TIME_OUT);
        eleutil.doClick(signInBtn,TimeUtil.DEFAULT_TIME_OUT);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
            eleutil.doClick(submitBtn,TimeUtil.MEDIUM_TIME_OUT);

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new ProjectListingsPage(driver);

    }


}
