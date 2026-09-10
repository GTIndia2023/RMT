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
    private By emaiInputField=By.xpath("//input[@type='email' or @name='loginfmt' or @id='i0116']");
    private By passwordInputField=By.xpath("//input[@type='password' or @name='passwd' or @id='i0118']");
    private By nextBtn= By.xpath("//input[@type='submit' or @id='idSIButton9' or @value='Next' or @value='Sign in']");
    private By signInBtn= By.xpath("//input[@type='submit' or @id='idSIButton9' or @value='Sign in' or @value='Next']");
    private By forgotPassword=By.xpath("//a[@id='idA_PWD_ForgotPassword']");
    private By submitBtn= By.xpath("//input[@type='submit' or @id='idSIButton9' or @value='Yes' or @value='Next' or @value='Sign in']");
    private By clickOnUseOtherAccount = By.xpath("//*[contains(normalize-space(.),'Use another account') or contains(normalize-space(.),'Add another account')]");

    private void logAction(String message) {
        System.out.println("[LoginPage] " + message);
    }


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
        logAction("Starting Microsoft login flow.");
        if (eleutil.isApplicationLandingVisible()) {
            logAction("Application landing is already visible. Reusing the active session.");
            return new ProjectListingsPage(driver);
        }

        eleutil.openMicrosoftEmailStepIfAccountPickerIsVisible(
                emaiInputField,
                clickOnUseOtherAccount,
                passwordInputField,
                TimeUtil.LONG_TIME_OUT
        );
        By emailLocator = eleutil.waitForAnyVisibleLocator(new By[]{emaiInputField}, TimeUtil.LONG_TIME_OUT);
        if (emailLocator == null) {
            throw new IllegalStateException("Microsoft email input did not become visible after account selection.");
        }
        eleutil.enterTextReliable(emailLocator, un, TimeUtil.LONG_TIME_OUT);
        eleutil.clickStable(nextBtn, TimeUtil.MEDIUM_TIME_OUT);

        By passwordLocator = eleutil.waitForMicrosoftPasswordInputAfterEmail(passwordInputField, TimeUtil.LONG_TIME_OUT);
        eleutil.enterTextReliable(passwordLocator, pswd, TimeUtil.LONG_TIME_OUT);
        eleutil.clickStable(signInBtn, TimeUtil.MEDIUM_TIME_OUT);
        eleutil.handleMicrosoftPostPasswordSubmitPrompt(
                submitBtn,
                eleutil.shouldSkipMicrosoftPostPasswordSubmitPromptForCurrentEnvironment(),
                TimeUtil.MEDIUM_TIME_OUT
        );
        eleutil.ensureApplicationLandingAfterLogin();
        return new ProjectListingsPage(driver);

    }

}
