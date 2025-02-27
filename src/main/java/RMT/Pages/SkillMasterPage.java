package RMT.Pages;

import RMT.Constants.AppConstants;
import RMT.Utils.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private By competencyContains=By.xpath("(// input[@ref='eInput'])[4]");
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
    private By competencyDrodpown=By.xpath("(// span[@ref='eMenu'])[1]");
    private By editCompetencyBtn=By.xpath("(//button[@type='button'])[9]");
    private By bacnBtn=By.xpath("(//button[@type='button'])[7]");


    //3.Page actions

    /**
     * This method is used for capturing the title of the skillMaster page and returning it
     * @return
     * @throws InterruptedException
     */
    public String getSkillMasterPageTitle(){
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LocalDateTime startOperation = LocalDateTime.now();
        System.out.println("Start Time for skill filter operation: " + startOperation.format(formatter));
        eleutil.clickWhenReady(skillNameFilter, TimeUtil.DEFAULT_TIME_OUT);//Click on Ag grid filter
        eleutil.clickWhenReady(filterIcon,TimeUtil.DEFAULT_TIME_OUT);// Click on Filter icon
        eleutil.doSendKeys(this.containsInput,skillname,TimeUtil.DEFAULT_TIME_OUT); // Entering the skill name from the filter/File
        LocalDateTime endOperation = LocalDateTime.now();
        Duration duration = Duration.between(startOperation, endOperation);
        System.out.println("Time taken for skill filter operation: " + duration.toMinutes() + " min " + duration.toSecondsPart() + " sec");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            LocalDateTime editStart = LocalDateTime.now();
            System.out.println("Start Time for skill edit operation: " + editStart.format(formatter));
            eleutil.clickWhenReady(editSkill,TimeUtil.DEFAULT_TIME_OUT);//Clicking on edit skill icon
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            jsUtil.zoomFirefoxChromeEdgeSafari("50");
            jsUtil.clickElementByJS(driver.findElement(By.xpath("//a[@href='#']")));
            eleutil.handleCompetencyMenue(CompetencyInput,competency);//Clicking on competency field and selecting the value from Excel
            LocalDateTime editEnd = LocalDateTime.now();
            Duration editDuration = Duration.between(editStart, editEnd);
            System.out.println("Time taken for skill edit operation: " + editDuration.toMinutes() + " min " + editDuration.toSecondsPart() + " sec");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            // Capture time for designation mapping
            LocalDateTime designationStart = LocalDateTime.now();
            System.out.println("Start Time for designation mapping: " + designationStart.format(formatter));
            jsUtil.zoomFirefoxChromeEdgeSafari("50");
            if (A1_P_GR00001.equals("Yes")){
                eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_PARTNER);
            }else {
                LocalDateTime designationEnd = LocalDateTime.now();
                Duration designationDuration = Duration.between(designationStart, designationEnd);
                System.out.println("Time taken for designation mapping: " + designationDuration.toMinutes() + " min " + designationDuration.toSecondsPart() + " sec");
                System.out.println("A1_P_GR00001 column value is No");
            }
            if (A2_ED_GR00002.equals("Yes")){
                eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_Executive_Director);
            }else {
                System.out.println("A2_ED_GR00002 column value is No");
            }
            if (A3_D_GR00003.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_Director);
            }else {
                System.out.println("A3_D_GR00003 Column value is No ");
            }
            if (B1_AD_GR00004.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_Associate_Director);
            }else {
                System.out.println("B1_AD_GR00004 Column value is No ");
            }
            if (B2_M_GR00005.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_MANAGER);
            }else {
                System.out.println("B2_M_GR00005 Column value is No ");
            }
            if (C1_AM_GR00006.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_Assistant_MANAGER);
            }else {
                System.out.println("C1_AM_GR00006 Column value is No ");
            }
            if (C2_SA_GR00007.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_ASSOCIATE);
            }else {
                System.out.println("C2_SA_GR00007 Column value is No ");
            }
            if (D1_GT_GR00008.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_Graduate_Trainee);
            }else {
                System.out.println("D1_GT_GR00008 Column value is No ");
            }
            if (D2_T_GR00009.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_Trainee);
            }else {
                System.out.println("D2_T_GR00009 Column value is No ");
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            String text=eleutil.doGetText(popUpText);
            jsUtil.zoomFirefoxChromeEdgeSafari("50");
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
            // Capture start time for skill addition
            LocalDateTime startSkillAddition = LocalDateTime.now();
            System.out.println("Start Time for skill addition: " + startSkillAddition.format(formatter));
            eleutil.doClick(addNewSkillBtn,TimeUtil.DEFAULT_TIME_OUT);// Click on AdNewSkill button
            eleutil.doSendKeys(skillName1,skillname,TimeUtil.DEFAULT_TIME_OUT);// Inputing the skill name
            LocalDateTime endSkillAddition = LocalDateTime.now();
            Duration durationSkillAddition = Duration.between(startSkillAddition, endSkillAddition);
            System.out.println("Time taken for skill addition: " + durationSkillAddition.toMinutes() + " min " + durationSkillAddition.toSecondsPart() + " sec");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException f) {
                throw new RuntimeException(f);
            }
            // Capture start time for entering skill details
            LocalDateTime startDetails = LocalDateTime.now();
            System.out.println("Start Time for entering skill details: " + startDetails.format(formatter));
            try {
                eleutil.handleDropdownMenue(skillCategory,Category);
            } catch (RuntimeException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            eleutil.doSendKeys(this.skillDescription,description,TimeUtil.DEFAULT_TIME_OUT);
            eleutil.doSendKeys(this.staringInput,starting,TimeUtil.DEFAULT_TIME_OUT);
            eleutil.doSendKeys(this.buildingInput,building,TimeUtil.DEFAULT_TIME_OUT);
            eleutil.doSendKeys(this.skilledInput,skilled,TimeUtil.DEFAULT_TIME_OUT);
            eleutil.doSendKeys(this.excelledInput,excelled,TimeUtil.DEFAULT_TIME_OUT);
            LocalDateTime endDetails = LocalDateTime.now();
            Duration durationDetails = Duration.between(startDetails, endDetails);
            System.out.println("Time taken for entering skill details: " + durationDetails.toMinutes() + " min " + durationDetails.toSecondsPart() + " sec");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            //Capture start time for competency selection
            LocalDateTime startCompetency = LocalDateTime.now();
            System.out.println("Start Time for competency selection: " + startCompetency.format(formatter));
            jsUtil.clickElementByJS(driver.findElement(By.xpath("//a[@href='#']")));
            eleutil.handleCompetencyMenue(CompetencyInput,competency);//Clicking on copetency field and selecting the value from Excel
            LocalDateTime endCompetency = LocalDateTime.now();
            Duration durationCompetency = Duration.between(startCompetency, endCompetency);
            System.out.println("Time taken for competency selection: " + durationCompetency.toMinutes() + " min " + durationCompetency.toSecondsPart() + " sec");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            // Capture start time for designation selection
            LocalDateTime startDesignation = LocalDateTime.now();
            System.out.println("Start Time for designation selection: " + startDesignation.format(formatter));
            jsUtil.zoomFirefoxChromeEdgeSafari("60");
            if (A1_P_GR00001.equals("Yes")){
                eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_PARTNER);
            }else {
                System.out.println("A1_P_GR00001 column value is No");
            }
            if (A2_ED_GR00002.equals("Yes")){
                eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_Executive_Director);
            }else {
                System.out.println("A2_ED_GR00002 column value is No");
            }
            if (A3_D_GR00003.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_Director);
            }else {
                System.out.println("A3_D_GR00003 Column value is No ");
            }
            if (B1_AD_GR00004.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_Associate_Director);
            }else {
                System.out.println("B1_AD_GR00004 Column value is No ");
            }
            if (B2_M_GR00005.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_MANAGER);
            }else {
                System.out.println("B2_M_GR00005 Column value is No ");
            }
            if (C1_AM_GR00006.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_Assistant_MANAGER);
            }else {
                System.out.println("C1_AM_GR00006 Column value is No ");
            }
            if (C2_SA_GR00007.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_ASSOCIATE);
            }else {
                System.out.println("C2_SA_GR00007 Column value is No ");
            }
            if (D1_GT_GR00008.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_Graduate_Trainee);
            }else {
                System.out.println("D1_GT_GR00008 Column value is No ");
            }
            if (D2_T_GR00009.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_Trainee);
            }else {
                System.out.println("D2_T_GR00009 Column value is No ");
            }
            LocalDateTime endDesignation = LocalDateTime.now();
            Duration durationDesignation = Duration.between(startDesignation, endDesignation);
            System.out.println("Time taken for designation selection: " + durationDesignation.toMinutes() + " min " + durationDesignation.toSecondsPart() + " sec");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            String text=eleutil.doGetText(popUpText);
            jsUtil.zoomFirefoxChromeEdgeSafari("60");
            System.out.println(text);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            eleutil.doActionsClick(popUpText);// This will click on Add Mapping Text.
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            jsUtil.clickElementByJS(driver.findElement(By.xpath("(//button[@type='submit'])[2]")));
            jsUtil.clickElementByJS(driver.findElement(By.xpath("(//button[@type='submit'])[1]")));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            eleutil.clickWhenReady(yesBtn,TimeUtil.DEFAULT_TIME_OUT);
            String successMessage=eleutil.waitForElementVisible(successMsg,TimeUtil.MEDIUM_TIME_OUT).getText();
            System.out.println("Skill added and tagged successfully " + successMessage);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            if (successMessage.equalsIgnoreCase(AppConstants.SKILL_MASTER_SKILL_ADDITON_SUCCESS_MESSAGE)){
                return true;
            }else
                return false;
        }

    }

    public boolean handleFailedSkill(String fileName ,String skillID,String skillname , String description , String AnyRemarks,String starting , String building , String skilled, String excelled, String A1_P_GR00001,String A2_ED_GR00002,String A3_D_GR00003, String B1_AD_GR00004,String B2_M_GR00005,String C1_AM_GR00006,String C2_SA_GR00007,String D1_GT_GR00008,String D2_T_GR00009,String BU,String Expertise,String Specialisation,String competency ,String Classification,String Category) {

            Actions act = new Actions(driver);
            JavascriptUtil jsUtil = new JavascriptUtil(driver);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            eleutil.clickWhenReady(skillNameFilter, TimeUtil.DEFAULT_TIME_OUT);
            eleutil.clickWhenReady(filterIcon, TimeUtil.DEFAULT_TIME_OUT);
            eleutil.doSendKeys(this.containsInput, skillname, TimeUtil.DEFAULT_TIME_OUT);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        // Retry mechanism with while loop
        boolean competencyFound = false;
        int attempts = 0;

        while (!competencyFound && attempts < 2) {  // Try checking twice before adding a skill
            try {
                eleutil.clickWhenReady(editSkill, TimeUtil.DEFAULT_TIME_OUT);
                try {
                    Thread.sleep(2000);  // Consider replacing with WebDriverWait for better efficiency
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                eleutil.clickWhenReady(competencyDrodpown, TimeUtil.MEDIUM_TIME_OUT);
                eleutil.clickWhenReady(filterIcon, TimeUtil.DEFAULT_TIME_OUT);
                eleutil.doSendKeys(this.competencyContains, competency, TimeUtil.DEFAULT_TIME_OUT);

                try {
                    WebElement editBtn = eleutil.waitForElementVisible(editCompetencyBtn, 5);
                    if (editBtn != null) {
                        System.out.println("This skill is tagged to a competency");
                        eleutil.clickWhenReady(bacnBtn, 5);  // Click back button
                        return true;  // Exit method since skill is already tagged
                    }
                } catch (NoSuchElementException | TimeoutException e) {
                    System.out.println("Edit competency button not found: " + e.getMessage());
                }

            } catch (TimeoutException e) {
                System.out.println("Skill competency not found, retrying...");
            }

            attempts++; // Increment attempt count
        }

// If competency is still not found after retries, proceed to add a new skill
        System.out.println("Competency not found after retries. Proceeding to add skill...");

        // If the competency was not found, proceed to add the skill
        eleutil.doClick(addNewSkillBtn,TimeUtil.DEFAULT_TIME_OUT);// Click on AdNewSkill button
        eleutil.doSendKeys(skillName1,skillname,TimeUtil.DEFAULT_TIME_OUT);// Inputing the skill name
        try {
            Thread.sleep(2000);
        } catch (InterruptedException f) {
            throw new RuntimeException(f);
        }
        try {
            eleutil.handleDropdownMenue(skillCategory,Category);
        } catch (RuntimeException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        eleutil.doSendKeys(this.skillDescription,description,TimeUtil.DEFAULT_TIME_OUT);
        eleutil.doSendKeys(this.staringInput,starting,TimeUtil.DEFAULT_TIME_OUT);
        eleutil.doSendKeys(this.buildingInput,building,TimeUtil.DEFAULT_TIME_OUT);
        eleutil.doSendKeys(this.skilledInput,skilled,TimeUtil.DEFAULT_TIME_OUT);
        eleutil.doSendKeys(this.excelledInput,excelled,TimeUtil.DEFAULT_TIME_OUT);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        jsUtil.clickElementByJS(driver.findElement(By.xpath("//a[@href='#']")));
        eleutil.handleCompetencyMenue(CompetencyInput,competency);//Clicking on copetency field and selecting the value from Excel
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        jsUtil.zoomFirefoxChromeEdgeSafari("50");
        if (A1_P_GR00001.equals("Yes")){
            eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_PARTNER);
        }else {
            System.out.println("A1_P_GR00001 column value is No");
        }
        if (A2_ED_GR00002.equals("Yes")){
            eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_Executive_Director);
        }else {
            System.out.println("A2_ED_GR00002 column value is No");
        }
        if (A3_D_GR00003.equals("Yes")) {
            eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_Director);
        }else {
            System.out.println("A3_D_GR00003 Column value is No ");
        }
        if (B1_AD_GR00004.equals("Yes")) {
            eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_Associate_Director);
        }else {
            System.out.println("B1_AD_GR00004 Column value is No ");
        }
        if (B2_M_GR00005.equals("Yes")) {
            eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_MANAGER);
        }else {
            System.out.println("B2_M_GR00005 Column value is No ");
        }
        if (C1_AM_GR00006.equals("Yes")) {
            eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_Assistant_MANAGER);
        }else {
            System.out.println("C1_AM_GR00006 Column value is No ");
        }
        if (C2_SA_GR00007.equals("Yes")) {
            eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_ASSOCIATE);
        }else {
            System.out.println("C2_SA_GR00007 Column value is No ");
        }
        if (D1_GT_GR00008.equals("Yes")) {
            eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_Graduate_Trainee);
        }else {
            System.out.println("D1_GT_GR00008 Column value is No ");
        }
        if (D2_T_GR00009.equals("Yes")) {
            eleutil.handleDesignationMenue1(DesignationInput,AppConstants.DESIGNATION_MASTER_GRADE_Trainee);
        }else {
            System.out.println("D2_T_GR00009 Column value is No ");
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        String text=eleutil.doGetText(popUpText);
        jsUtil.zoomFirefoxChromeEdgeSafari("50");
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
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        if (successMessage.equalsIgnoreCase(AppConstants.SKILL_MASTER_SKILL_ADDITON_SUCCESS_MESSAGE)){
            return true;
        }else
            return false;
        }

    }
