package RMT.Pages;

import RMT.Constants.AppConstants;
import RMT.Exceptions.ElementException;
import RMT.Utils.ElementUtil;
import RMT.Utils.JavascriptUtil;
import RMT.Utils.TimeUtil;
import dev.failsafe.Timeout;
import org.apache.poi.ss.formula.functions.T;
import org.openqa.selenium.*;

import java.sql.Time;

public class SkillMasterPage {
    private WebDriver driver;
    ElementUtil eleutil;

    //1. Skill master page constructor
    public SkillMasterPage(WebDriver driver){
        this.driver=driver;
        eleutil=new ElementUtil(driver);
    }

    //2.Page locators
    private By addNewSkillBtn= By.xpath("//button[@type='submit']");
    private By skillName1=By.xpath("//textarea[@name='skillName']");
    private By skillNameFilter = By.xpath("(//span[@ref='eMenu'])[2]");
    private By filterIcon= By.xpath("(//span[@role='tab'])[2]");
    private By equalsSelection = By.xpath("(//div[@class='ag-picker-field-display'])[1]");
    private By containsInput = By.xpath("(//input[@ref='eInput'])[7]");
    private By skillExists= By.xpath("(//div[@title='Selenium'])");
    private By editSkill = By.xpath("(//button[@aria-label='Edit Skill'])");
    private By skillCategory=By.xpath("//input[@type='text']");
    private By technical=By.id(":r1t:-option-0");
    private By soft=By.id(":r1t:-option-1");
    private By skillDescription=By.xpath("//textarea[@name='skillDescription']");
    private By staringInput=By.xpath("//textarea[@name='basic']");
    private By buildingInput=By.xpath("//textarea[@name='intermediate']");
    private By skilledInput=By.xpath("//textarea[@name='advanced']");
    private By excelledInput=By.xpath("//textarea[@name='expert']");
    private By addTagbtn=By.xpath("//span[@class='add-skill-link']");
    private By CompetencyInput=By.xpath("(//input[@type='text'])[2]");
    private By DesignationInput=By.xpath("(//input[@type='text'])[3]");
    private By saveBtn=By.xpath("//button[@type='submit']");
    private By yesBtn=By.xpath("//button[text()='Yes']");
    private By successMsg= By.xpath("//div[@class='MuiAlert-message css-1xsto0d']");
    private By popUpText = By.xpath("//div[contains(@class, 'skill-mapping-modal')]");


    //3.Page actions

    /**
     * This method is used for capturing the title of the skillMaster page and returning it
     * @return
     * @throws InterruptedException
     */
    public String getSkillMasterPageTitle() throws InterruptedException {
        String title=eleutil.waitForTitleToBe(AppConstants.SKILL_MASTER_PAGE_TITLE, TimeUtil.MEDIUM_TIME_OUT);
        System.out.println("Skill master Page  title is "+ title);
        return title;
    }

    /**
     * This method is used for capturing the skillMaster page URL and returning it
     * @return
     */
    public String getSkillMasterPageUrl(){
        String url=eleutil.waitForURLToBe(AppConstants.SKILL_MASTER_PAGE_URL,TimeUtil.DEFAULT_TIME_OUT);
        System.out.println("Skill master page Url is " + url);
        return url;
    }

    /**
     * This method is used to capture the inputs from the Excel file by using ExcelUtil by checkng that in skill master
     * page the relevent SkillName is present or not , if present it will initialize AddSkill and if not it wil initialize
     * EditSkill and will return True or False ( And verify the successMessage through constants)
     * @param skillname
     * @param description
     * @param starting
     * @param building
     * @param skilled
     * @param excelled
     * @param competency
     * @return
     */
    public boolean handleSkill(String skillname , String description , String starting , String building , String skilled, String excelled,String competency , String A1,String A2,String A3, String B1,String B2,String C1,String C2,String D1,String D2 )  {
        JavascriptUtil jsUtil= new JavascriptUtil(driver);
        eleutil.clickWhenReady(skillNameFilter, TimeUtil.DEFAULT_TIME_OUT);//Click on Ag grid filter
        eleutil.clickWhenReady(filterIcon,TimeUtil.DEFAULT_TIME_OUT);// Click on Filter icon
        //eleutil.clickWhenReady(equalsSelection,TimeUtil.MEDIUM_TIME_OUT);
        eleutil.doSendKeys(this.containsInput,skillname,TimeUtil.DEFAULT_TIME_OUT); // Entering the skill name from the filter
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            eleutil.clickWhenReady(editSkill,TimeUtil.DEFAULT_TIME_OUT);//Clicking on edit skill icon
            eleutil.clickWhenReady(addTagbtn,TimeUtil.MEDIUM_TIME_OUT);
            eleutil.handleCompetencyMenue(CompetencyInput,competency);//Clicking on copetency field and selecting the value from Excel
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            eleutil.doClick(DesignationInput,TimeUtil.MEDIUM_TIME_OUT);// Clicking on designation dropdown field
            if (A1.equals("Yes")){
                eleutil.handleDesgnationMenue(DesignationInput,AppConstants.SKILL_MASTER_SKILL_PARTNER_DROPDOWN);
            }if (A2.equals("Yes")){
                eleutil.handleDesgnationMenue(DesignationInput,AppConstants.SKILL_MASTER_SKILL_Executive_Director_DROPDOWN);
            }if (A3.equals("Yes")) {
                eleutil.handleDesgnationMenue(DesignationInput,AppConstants.SKILL_MASTER_SKILL_Director_DROPDOWN);
            }if (B1.equals("Yes")) {
                eleutil.handleDesgnationMenue(DesignationInput,AppConstants.SKILL_MASTER_SKILL_Associate_Director_DROPDOWN);
            }if (B2.equals("Yes")) {
                eleutil.handleDesgnationMenue(DesignationInput,AppConstants.SKILL_MASTER_SKILL_Manager_DROPDOWN);
            }if (C1.equals("Yes")) {
                eleutil.handleDesgnationMenue(DesignationInput,AppConstants.SKILL_MASTER_SKILL_Assistant_MANAGER_DROPDOWN);
            }if (C2.equals("Yes")) {
                eleutil.handleDesgnationMenue(DesignationInput,AppConstants.SKILL_MASTER_SKILL_Senior_ASSOCIATE_DRODPOWN);
            }if (D1.equals("Yes")) {
                eleutil.handleDesgnationMenue(DesignationInput,AppConstants.SKILL_MASTER_SKILL_Graduate_Trainee_DROPDOWN);
            }if (D2.equals("Yes")) {
                eleutil.handleDesgnationMenue(DesignationInput,AppConstants.SKILL_MASTER_SKILL_Trainee_DROPDOWN);
            }else if (A1.equals("No") || A2.equals("No") || A3.equals("No") || B1.equals("No") || B2.equals("No") || C1.equals("No") || C2.equals("No") || D1.equals("No") || D2.equals("No")) {
                System.out.println("No designation value is found");
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            String text=eleutil.doGetText(popUpText);
            jsUtil.zoomFirefoxChromeEdgeSafari("90");
            System.out.println(text);
            eleutil.doActionsClick(popUpText);
            try {
                Thread.sleep(8000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            jsUtil.clickElementByJS(driver.findElement(By.xpath("(//button[@type='submit'])[1]")));
            jsUtil.clickElementByJS(driver.findElement(By.xpath("(//button[@type='submit'])[1]")));
            eleutil.clickWhenReady(yesBtn,TimeUtil.DEFAULT_TIME_OUT);
            String successMessage=eleutil.waitForElementVisible(successMsg,TimeUtil.MEDIUM_TIME_OUT).getText();
            System.out.println("Skill updated  " + successMessage);
            if (successMessage.equalsIgnoreCase(AppConstants.SKILL_MASTER_SKILL_UPDATION_SUCCESS_MESSAGE)){
                return true;
            }else{
                return false;
            }
        }catch (TimeoutException e){
            eleutil.doClick(addNewSkillBtn,TimeUtil.DEFAULT_TIME_OUT);// Click on AdNewSkill button
            eleutil.doSendKeys(skillName1,skillname,TimeUtil.DEFAULT_TIME_OUT);// Inputing the skill name
            try {
                Thread.sleep(2000);
            } catch (InterruptedException f) {
                throw new RuntimeException(f);
            }
            try {
                eleutil.handleDropdownMenue(skillCategory);
            } catch (RuntimeException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            eleutil.doSendKeys(this.skillDescription,description,TimeUtil.DEFAULT_TIME_OUT);
            eleutil.doSendKeys(this.staringInput,starting,TimeUtil.DEFAULT_TIME_OUT);
            eleutil.doSendKeys(this.buildingInput,building,TimeUtil.DEFAULT_TIME_OUT);
            eleutil.doSendKeys(this.skilledInput,skilled,TimeUtil.DEFAULT_TIME_OUT);
            eleutil.doSendKeys(this.excelledInput,excelled,TimeUtil.DEFAULT_TIME_OUT);
            eleutil.doClick(addTagbtn,TimeUtil.MEDIUM_TIME_OUT);
            eleutil.handleCompetencyMenue(CompetencyInput,competency);//Clicking on copetency field and selecting the value from Excel
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            eleutil.doClick(DesignationInput,TimeUtil.MEDIUM_TIME_OUT);// Clicking on designation dropdown field
            if (A1.equals("Yes")){
                eleutil.handleDesgnationMenue(DesignationInput,AppConstants.SKILL_MASTER_SKILL_PARTNER_DROPDOWN);
            }if (A2.equals("Yes")){
                eleutil.handleDesgnationMenue(DesignationInput,AppConstants.SKILL_MASTER_SKILL_Executive_Director_DROPDOWN);
            }if (A3.equals("Yes")) {
                eleutil.handleDesgnationMenue(DesignationInput,AppConstants.SKILL_MASTER_SKILL_Director_DROPDOWN);
            }if (B1.equals("Yes")) {
                eleutil.handleDesgnationMenue(DesignationInput,AppConstants.SKILL_MASTER_SKILL_Associate_Director_DROPDOWN);
            }if (B2.equals("Yes")) {
                eleutil.handleDesgnationMenue(DesignationInput,AppConstants.SKILL_MASTER_SKILL_Manager_DROPDOWN);
            }if (C1.equals("Yes")) {
                eleutil.handleDesgnationMenue(DesignationInput,AppConstants.SKILL_MASTER_SKILL_Assistant_MANAGER_DROPDOWN);
            }if (C2.equals("Yes")) {
                eleutil.handleDesgnationMenue(DesignationInput,AppConstants.SKILL_MASTER_SKILL_Senior_ASSOCIATE_DRODPOWN);
            }if (D1.equals("Yes")) {
                eleutil.handleDesgnationMenue(DesignationInput,AppConstants.SKILL_MASTER_SKILL_Graduate_Trainee_DROPDOWN);
            }if (D2.equals("Yes")) {
                eleutil.handleDesgnationMenue(DesignationInput,AppConstants.SKILL_MASTER_SKILL_Trainee_DROPDOWN);
            }else if (A1.equals("No") || A2.equals("No") || A3.equals("No") || B1.equals("No") || B2.equals("No") || C1.equals("No") || C2.equals("No") || D1.equals("No") || D2.equals("No")) {
                System.out.println("No designation value is found");
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            String text=eleutil.doGetText(popUpText);
            jsUtil.zoomFirefoxChromeEdgeSafari("90");
            System.out.println(text);
            eleutil.doActionsClick(popUpText);
            try {
                Thread.sleep(8000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            jsUtil.clickElementByJS(driver.findElement(By.xpath("(//button[@type='submit'])[2]")));
            jsUtil.clickElementByJS(driver.findElement(By.xpath("(//button[@type='submit'])[1]")));
            eleutil.clickWhenReady(yesBtn,TimeUtil.DEFAULT_TIME_OUT);
            String successMessage=eleutil.waitForElementVisible(successMsg,TimeUtil.MEDIUM_TIME_OUT).getText();
            System.out.println("Skill added and tagged successfully " + successMessage);
            if (successMessage.equalsIgnoreCase(AppConstants.SKILL_MASTER_SKILL_ADDITON_SUCCESS_MESSAGE)){
                return true;
            }else{
                return false;
            }
        }

    }
}
