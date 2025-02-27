package RMT.Pages;

import RMT.Constants.AppConstants;
import RMT.Utils.ElementUtil;
import RMT.Utils.JavascriptUtil;
import RMT.Utils.TimeUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class RequisitionPage {
    private WebDriver driver;
    ElementUtil eleutil;

    //1. Skill master page constructor
    public RequisitionPage(WebDriver driver){
        this.driver=driver;
        eleutil=new ElementUtil(driver);
    }

    //2. Project Page locators
    private By DesignationInput=By.xpath("(//input[@type='text'])[1]");
    private By TaskDescription = By.xpath("//textarea[@name=\"description\"]");
    private By CompetencyDropown=By.xpath("(//input[@type='text'])[4]");
    private By DirectorDesgn=By.xpath("(//li[text()='A3 (D) | Director'])");
    private By Competency=By.xpath("(//li[text()='Due Diligence'])");
    private By saveBtn=By.xpath("(//button[@type='submit'])");
    private By successMsg=By.xpath("(//div[@class='MuiAlert-message css-1xsto0d'])");
    private By skilldropn=By.xpath("(//input[@type='text'])[5]");
    private By skillSelect=By.xpath("(//li[text()='Agile Methodology (SK000040)'])");
    private By systemSuggestions=By.xpath("//button[text()='Search']");
    private By card=By.xpath("//div[text()='RMSED Offering']");

    //3.Page actions

    /**
     * This method is used for capturing the title of the Job Details page and returning it
     * @return
     * @throws InterruptedException
     */
    public String getRequisitionPageTitle() throws InterruptedException {
        String title=eleutil.waitForTitleToBe(AppConstants.SKILL_MASTER_PAGE_TITLE, TimeUtil.MEDIUM_TIME_OUT);
        System.out.println("Requisiton  Page  title is "+ title);
        return title;
    }

    /**
     * This method is used for capturing the Requisition page URL and returning it
     * @return
     */
    public String getRequisitionPageUrl(){
        String url=eleutil.waitForURLToBe(AppConstants.REQUISITON_PAGE_URL,TimeUtil.DEFAULT_TIME_OUT);
        System.out.println("Requisition  page Url is " + url);
        return url;
    }
    public boolean CreateRequisition(){
        Actions act = new Actions(driver);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(DesignationInput);
        eleutil.sendKeysWithWait(DesignationInput,"A3 (D) | Director",2);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(DirectorDesgn);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        eleutil.doActionsSendKeys(TaskDescription,"Require director for the job");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(CompetencyDropown);
        eleutil.sendKeysWithWait(CompetencyDropown,"Due Diligence",3);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(Competency);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(skilldropn);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(skillSelect);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.clickWhenReady(saveBtn,TimeUtil.DEFAULT_TIME_OUT);
        String successMessage=eleutil.waitForElementVisible(successMsg,TimeUtil.MEDIUM_TIME_OUT).getText();
        System.out.println("Requisiton Status " + successMessage);
        if (successMessage.equalsIgnoreCase(AppConstants.REQUISITON_CREATED_MESSAGE)){
            return true;
        }else{
            return false;
        }

    }

    public boolean searchAlgo(){
        JavascriptUtil jsUtil= new JavascriptUtil(driver);
        Actions act = new Actions(driver);
        eleutil.doActionsClick(systemSuggestions);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //javascriptUtil.scrollIntoView(eleutil.getElement(card));
        WebElement cardlayout = driver.findElement(By.xpath("// div[text()='RMSED Offering']"));
        act.moveToElement(cardlayout);
        jsUtil.scrollPageDown();
        String suggestionCard= String.valueOf(eleutil.getElement(card));
        if (suggestionCard.equalsIgnoreCase(AppConstants.SYSTEM_SUGGESTED_CARD)){
            return true;
        }else {
            return false;
        }
    }

}

