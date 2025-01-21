package RMT.Pages;

import RMT.Constants.AppConstants;
import RMT.Utils.DesignationUtil;
import RMT.Utils.ElementUtil;
import RMT.Utils.JavascriptUtil;
import RMT.Utils.TimeUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.Objects;

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
     * This method is used to capture the inputs from the Excel file and designation file  by using ExcelUtil & designationUtil
     * by checking that in skill master page the relevant SkillName is present or not , if present it will initialize AddSkill and if not it wil initialize
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
    public boolean handleSkill(String fileName ,String skillID,String skillname , String description , String AnyRemarks,String starting , String building , String skilled, String excelled, String A1_P_GR00001,String A2_ED_GR00002,String A3_D_GR00003, String B1_AD_GR00004,String B2_M_GR00005,String C1_AM_GR00006,String C2_SA_GR00007,String D1_GT_GR00008,String D2_T_GR00009,String BU,String Expertise,String Specialisation,String competency ,String Classification,String Category)  {
        Actions act = new Actions(driver);
        JavascriptUtil jsUtil= new JavascriptUtil(driver);
        eleutil.clickWhenReady(skillNameFilter, TimeUtil.DEFAULT_TIME_OUT);//Click on Ag grid filter
        eleutil.clickWhenReady(filterIcon,TimeUtil.DEFAULT_TIME_OUT);// Click on Filter icon
        eleutil.doSendKeys(this.containsInput,skillname,TimeUtil.DEFAULT_TIME_OUT); // Entering the skill name from the filter
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            eleutil.clickWhenReady(editSkill,TimeUtil.DEFAULT_TIME_OUT);//Clicking on edit skill icon
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            jsUtil.zoomFirefoxChromeEdgeSafari("80");
            jsUtil.scrollIntoView(driver.findElement(By.xpath("// span[@class='add-skill-link']")));
            jsUtil.clickElementByJS(driver.findElement(By.xpath("// span[@class='add-skill-link']")));
            //eleutil.clickWhenReady(addTagbtn,TimeUtil.MEDIUM_TIME_OUT);
            eleutil.handleCompetencyMenue(CompetencyInput,competency);//Clicking on copetency field and selecting the value from Excel
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            jsUtil.zoomFirefoxChromeEdgeSafari("80");
            if (A1_P_GR00001.equals("Yes")){
                eleutil.handleDesignationMenue1(DesignationInput,"GRD00001");
            }if (A2_ED_GR00002.equals("Yes")){
                eleutil.handleDesignationMenue1(DesignationInput,"GRD00002");
            }if (A3_D_GR00003.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,"GRD00003");
            }if (B1_AD_GR00004.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,"GRD00004");
            }if (B2_M_GR00005.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,"GRD00005");
            }if (C1_AM_GR00006.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,"GRD00006");
            }if (C2_SA_GR00007.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,"GRD00007");
            }if (D1_GT_GR00008.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,"GRD00008");
            }if (D2_T_GR00009.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,"GRD00009");
            }else if (A1_P_GR00001.equals("No") || A2_ED_GR00002.equals("No") || A3_D_GR00003.equals("No") || B1_AD_GR00004.equals("No") || B2_M_GR00005.equals("No") || C1_AM_GR00006.equals("No") || C2_SA_GR00007.equals("No") || D1_GT_GR00008.equals("No") || D2_T_GR00009.equals("No")) {
                System.out.println("No designation value is found");
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            String text=eleutil.doGetText(popUpText);
            jsUtil.zoomFirefoxChromeEdgeSafari("80");
            System.out.println(text);
            eleutil.doActionsClick(popUpText);
            try {
                Thread.sleep(4000);
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
            jsUtil.zoomFirefoxChromeEdgeSafari("80");
            if (A1_P_GR00001.equals("Yes")){
                eleutil.handleDesignationMenue1(DesignationInput,"GRD00001");
            }if (A2_ED_GR00002.equals("Yes")){
                eleutil.handleDesignationMenue1(DesignationInput,"GRD00002");
            }if (A3_D_GR00003.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,"GRD00003");
            }if (B1_AD_GR00004.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,"GRD00004");
            }if (B2_M_GR00005.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,"GRD00005");
            }if (C1_AM_GR00006.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,"GRD00006");
            }if (C2_SA_GR00007.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,"GRD00007");
            }if (D1_GT_GR00008.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,"GRD00008");
            }if (D2_T_GR00009.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,"GRD00009");
            }else if (A1_P_GR00001.equals("No") || A2_ED_GR00002.equals("No") || A3_D_GR00003.equals("No") || B1_AD_GR00004.equals("No") || B2_M_GR00005.equals("No") || C1_AM_GR00006.equals("No") || C2_SA_GR00007.equals("No") || D1_GT_GR00008.equals("No") || D2_T_GR00009.equals("No")) {
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
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            eleutil.doActionsClick(popUpText);// This will click on Add Mapping Text.
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            jsUtil.clickElementByJS(driver.findElement(By.xpath("(//button[@type='submit'])[2]")));
            jsUtil.clickElementByJS(driver.findElement(By.xpath("(//button[@type='submit'])[1]")));
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
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
