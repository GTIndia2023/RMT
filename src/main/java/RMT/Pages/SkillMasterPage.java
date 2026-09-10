package RMT.Pages;

import RMT.Constants.AppConstants;
import RMT.Exceptions.ElementException;
import RMT.Exceptions.SkillAdditionException;
import RMT.Utils.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtrRef;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkillMasterPage {
    private WebDriver driver;
    ElementUtil eleutil;
    WebDriverWait wait;

    //1. Skill master page constructor
    public SkillMasterPage(WebDriver driver) {
        this.driver = driver;
        eleutil = new ElementUtil(driver);
    }

    private void logAction(String message) {
        System.out.println("[SkillMaster] " + message);
    }

    //2.Page locators
    private By addNewSkillBtn = By.xpath("//button[@type='submit']");
    private By skillName1 = By.xpath("//textarea[@name='skillName']");
    private By skillNameFilter = By.xpath("(//span[@ref='eMenu'])[2]");
    private By filterIcon = By.xpath("(//span[@role='tab'])[2]");
    private By equalsSelection = By.xpath("(//div[@class='ag-picker-field-display'])[1]");
    private By containsInput = By.xpath("(//input[@ref='eInput'])[7]");
    private By competencyContains = By.xpath("(// input[@ref='eInput'])[4]");
    private By skillExists = By.xpath("(//div[@title='Selenium'])");
    private By editSkill = By.xpath("(//button[@aria-label='Edit Skill'])");
    private By skillCategory = By.xpath("//input[@type='text']");
    private By technical = By.id(":r1t:-option-0");
    private By soft = By.id(":r1t:-option-1");
    private By skillDescription = By.xpath("//textarea[@name='skillDescription']");
    private By staringInput = By.xpath("//textarea[@name='basic']");
    private By buildingInput = By.xpath("//textarea[@name='intermediate']");
    private By skilledInput = By.xpath("//textarea[@name='advanced']");
    private By excelledInput = By.xpath("//textarea[@name='expert']");
    private By addTagbtn = By.xpath("//span[@class='add-skill-link']");
    private By CompetencyInput = By.xpath("(//input[@type='text'])[2]");
    private By DesignationInput = By.xpath("(//input[@type='text'])[3]");
    private By saveBtn = By.xpath("//button[@type='submit']");
    private By yesBtn = By.xpath("//button[text()='Yes']");
    private By successMsg = By.xpath("//div[@class='MuiAlert-message css-1xsto0d']");
    private By popUpText = By.xpath("//div[contains(@class, 'skill-mapping-modal')]");
    private By competencyDrodpown = By.xpath("(// span[@ref='eMenu'])[1]");
    private By editCompetencyBtn = By.xpath("(//button[@type='button'])[9]");
    private By bacnBtn = By.xpath("(//button[@type='button'])[7]");
    private By skillsBtn = By.id("basic-menu-15");
    private By skillSearchBtn = By.xpath("// a[@href='/searchskill']");
    private By searchDrpn = By.xpath("//input[@placeholder='Search Skill']");
    private By searchEmp=By.xpath("// input[@placeholder='Search Employee']");
    private By searchedResult = By.xpath("//span[text()='Consultant']");
    private By toggleBtn = By.xpath("(//input[@type='checkbox'])[1]");
    private By employeeDsgn = By.xpath("(//span[text()='Associate Director'])");
    private By clickContains=By.xpath("//div[text()='Contains']");
    private By selectEquals=By.xpath("//div[@role='option' and .//span[text()='Equals']]");
    private By mySkills=By.xpath("//li[text()='My skills']");
    private By addNewSkill=By.xpath("//button[text()='Add new Skill']");
    private By skillName=By.xpath("(//input[@type='text'])[2]");
    private By proficiency=By.xpath("(//input[@type='text'])[3]");
    private By selectingStarting=By.xpath("//li[text()='Starting']");
    private By saveBtn2=By.xpath("//button[text()='Save']");
    private By skillAddedMessage=By.xpath("//div[@class='MuiAlert-message css-1xsto0d']");
    private By skillsOptions=By.xpath("//li[@aria-selected='false']");
    private By competencyInputChoice= By.xpath("(//li[text()='Forensic'])");
    private By clickOnUserName= By.xpath("//div[contains(@class,'user-container')]//button[contains(normalize-space(.),'Rmsed') or contains(normalize-space(.),'RMSED')]");
    private By clickOnLogoutBtn = By.cssSelector("#account-menu .MuiButtonBase-root");
    private By clickOnAccount = By.xpath("//div[contains(normalize-space(.),'RMSED ResourceReq2')]");
    private By clickOnSCId = By.xpath("//div[contains(normalize-space(.),'RMSED Admin')]");
    private By clickOnSignInBtn = By.xpath("//input[@value='Sign in' or @id='idSIButton9']");
    private By skillReviewBtn=By.xpath("//li[text()='Skills Review']");
    private By clickOnCheckbox= By.cssSelector("input.ag-checkbox-input[aria-label*='toggle all rows selection']");
    private By clickOnBulkApproveBtn = By.xpath("//button[text()='Bulk Approve']");
    private By enterRemarks = By.xpath("//label[text()='Remarks']");
    private By clickOnConfirmBtn =By.xpath("//button[text()='Confirm']");
    private By skillApprovedMessage = By.xpath("//div[@class='MuiAlert-message css-1xsto0d']");
    private By clickOnUseOtherAccount = By.xpath("//*[contains(normalize-space(.),'Use another account') or contains(normalize-space(.),'Add another account')]");
    private final By emailInputField = By.xpath("//input[@type='email' or @name='loginfmt' or @id='i0116']");
    private final By nextBtn = By.xpath("//input[@type='submit' or @id='idSIButton9' or @value='Next' or @value='Sign in']");
    private final By passwordInputField = By.xpath("//input[@type='password' or @name='passwd' or @id='i0118']");
    private final By rmseLeaderOption = By.xpath("//div[contains(normalize-space(.),'RMSED ResourceReq2')]");
    private By clickOnAdminAccount= By.xpath("//div[text()='RMSED Admin']");
    private By statusLocator= By.xpath("//div[contains(@class,'MuiChip-root')]//span[contains(@class,'MuiChip-label')]");
    private By filterInputFiled= By.xpath("(//input[@placeholder='Filter...'])[1]");
    private By selectProficiency=By.xpath("(//input[@type='text'])[2]");





    //3.Page actions

    /**
     * This method is used for capturing the title of the skillMaster page and returning it
     *
     * @return
     * @throws InterruptedException
     */
    public String getSkillMasterPageTitle() {
        String title = eleutil.waitForTitleToBe(AppConstants.SKILL_MASTER_PAGE_TITLE, TimeUtil.MEDIUM_TIME_OUT);
        logAction("Skill master Page  title is " + title);
        return title;
    }

    /**
     * This method is used for capturing the skillMaster page URL and returning it
     *
     * @return
     */
    public String getSkillMasterPageUrl() {
        String url = eleutil.waitForURLToBe(AppConstants.SKILL_MASTER_PAGE_URL, TimeUtil.DEFAULT_TIME_OUT);
        logAction("Skill master page Url is " + url);
        return url;
    }

    /**
     * This method is used to capture the inputs from the Excel file and designation file  by using ExcelUtil & designationUtil
     * by checking that in skill master page the relevant SkillName is present or not , if present it will initialize AddSkill and if not it wil initialize
     * EditSkill and will return True or False ( And verify the successMessage through constants)
     *
     * @param skillname
     * @param description
     * @param starting
     * @param building
     * @param skilled
     * @param excelled
     * @param competency
     * @return
     */
    public boolean handleSkill(String fileName, String skillID, String skillname, String description, String AnyRemarks, String starting, String building, String skilled, String excelled, String A1_P_GR00001, String A2_ED_GR00002, String A3_D_GR00003, String B1_AD_GR00004, String B2_M_GR00005, String C1_AM_GR00006, String C2_SA_GR00007, String D1_GT_GR00008, String D2_T_GR00009, String BU, String Expertise, String Specialisation, String competency, String Classification, String Category) {
        Actions act = new Actions(driver);
        JavascriptUtil jsUtil = new JavascriptUtil(driver);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LocalDateTime startOperation = LocalDateTime.now();
        logAction("Start Time for skill filter operation: " + startOperation.format(formatter));
        eleutil.clickWhenReady(skillNameFilter, TimeUtil.DEFAULT_TIME_OUT);//Click on Ag grid filter
        eleutil.clickWhenReady(filterIcon, TimeUtil.DEFAULT_TIME_OUT);// Click on Filter icon
        try {
            eleutil.handleParentSubMenuWithClick(clickContains,selectEquals);//Select the filter option as Equal
        } catch (ElementException e) {
            throw new ElementException("Element is not found on DOM");
        }
        eleutil.doSendKeys(this.containsInput, skillname, TimeUtil.DEFAULT_TIME_OUT); // Entering the skill name from the filter/File
        LocalDateTime endOperation = LocalDateTime.now();
        Duration duration = Duration.between(startOperation, endOperation);
        logAction("Time taken for skill filter operation: " + duration.toMinutes() + " min " + duration.toSecondsPart() + " sec");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            LocalDateTime editStart = LocalDateTime.now();
            logAction("Start Time for skill edit operation: " + editStart.format(formatter));
            eleutil.clickWhenReady(editSkill, TimeUtil.DEFAULT_TIME_OUT);//Clicking on edit skill icon
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            jsUtil.zoomFirefoxChromeEdgeSafari("50");
            jsUtil.clickElementByJS(driver.findElement(By.xpath("//a[@href='#']")));
            eleutil.handleCompetencyMenue(CompetencyInput, competency);//Clicking on competency field and selecting the value from Excel
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //eleutil.doActionsClick(competencyInputChoice);
            LocalDateTime editEnd = LocalDateTime.now();
            Duration editDuration = Duration.between(editStart, editEnd);
            logAction("Time taken for skill edit operation: " + editDuration.toMinutes() + " min " + editDuration.toSecondsPart() + " sec");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            // Capture time for designation mapping
            LocalDateTime designationStart = LocalDateTime.now();
            logAction("Start Time for designation mapping: " + designationStart.format(formatter));
            jsUtil.zoomFirefoxChromeEdgeSafari("50");
            if (A1_P_GR00001.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_PARTNER);
            } else {
                LocalDateTime designationEnd = LocalDateTime.now();
                Duration designationDuration = Duration.between(designationStart, designationEnd);
                logAction("Time taken for designation mapping: " + designationDuration.toMinutes() + " min " + designationDuration.toSecondsPart() + " sec");
                logAction("A1_P_GR00001 column value is No");
            }
            if (A2_ED_GR00002.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_Executive_Director);
            } else {
                logAction("A2_ED_GR00002 column value is No");
            }
            if (A3_D_GR00003.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_Director);
            } else {
                logAction("A3_D_GR00003 Column value is No ");
            }
            if (B1_AD_GR00004.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_Associate_Director);
            } else {
                logAction("B1_AD_GR00004 Column value is No ");
            }
            if (B2_M_GR00005.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_MANAGER);
            } else {
                logAction("B2_M_GR00005 Column value is No ");
            }
            if (C1_AM_GR00006.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_Assistant_MANAGER);
            } else {
                logAction("C1_AM_GR00006 Column value is No ");
            }
            if (C2_SA_GR00007.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_ASSOCIATE);
            } else {
                logAction("C2_SA_GR00007 Column value is No ");
            }
            if (D1_GT_GR00008.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_Graduate_Trainee);
            } else {
                logAction("D1_GT_GR00008 Column value is No ");
            }
            if (D2_T_GR00009.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_Trainee);
            } else {
                logAction("D2_T_GR00009 Column value is No ");
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            String text = eleutil.doGetText(popUpText);
            jsUtil.zoomFirefoxChromeEdgeSafari("50");
            logAction(text);
            eleutil.doActionsClick(popUpText);
            jsUtil.clickElementByJS(driver.findElement(By.xpath("(//button[text()='Save'])")));
            jsUtil.clickElementByJS(driver.findElement(By.xpath("(//button[@type='submit'])[1]")));
            eleutil.clickWhenReady(yesBtn, TimeUtil.DEFAULT_TIME_OUT);
            String successMessage = eleutil.waitForElementVisible(successMsg, TimeUtil.MEDIUM_TIME_OUT).getText();
            logAction("Skill updated  " + successMessage);
            if (successMessage.equalsIgnoreCase(AppConstants.SKILL_MASTER_SKILL_UPDATION_SUCCESS_MESSAGE)) {
                return true;
            } else {
                return false;
            }
        } catch (TimeoutException e) {
            // Capture start time for skill addition
            LocalDateTime startSkillAddition = LocalDateTime.now();
            logAction("Start Time for skill addition: " + startSkillAddition.format(formatter));
            eleutil.doClick(addNewSkillBtn, TimeUtil.DEFAULT_TIME_OUT);// Click on AdNewSkill button
            eleutil.doSendKeys(skillName1, skillname, TimeUtil.DEFAULT_TIME_OUT);// Inputing the skill name
            LocalDateTime endSkillAddition = LocalDateTime.now();
            Duration durationSkillAddition = Duration.between(startSkillAddition, endSkillAddition);
            logAction("Time taken for skill addition: " + durationSkillAddition.toMinutes() + " min " + durationSkillAddition.toSecondsPart() + " sec");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException f) {
                throw new RuntimeException(f);
            }
            // Capture start time for entering skill details
            LocalDateTime startDetails = LocalDateTime.now();
            logAction("Start Time for entering skill details: " + startDetails.format(formatter));
            try {
                eleutil.handleDropdownMenue(skillCategory, Category);
            } catch (RuntimeException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            eleutil.doSendKeys(this.skillDescription, description, TimeUtil.DEFAULT_TIME_OUT);
            eleutil.doSendKeys(this.staringInput, starting, TimeUtil.DEFAULT_TIME_OUT);
            eleutil.doSendKeys(this.buildingInput, building, TimeUtil.DEFAULT_TIME_OUT);
            eleutil.doSendKeys(this.skilledInput, skilled, TimeUtil.DEFAULT_TIME_OUT);
            eleutil.doSendKeys(this.excelledInput, excelled, TimeUtil.DEFAULT_TIME_OUT);
            LocalDateTime endDetails = LocalDateTime.now();
            Duration durationDetails = Duration.between(startDetails, endDetails);
            logAction("Time taken for entering skill details: " + durationDetails.toMinutes() + " min " + durationDetails.toSecondsPart() + " sec");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            //Capture start time for competency selection
            LocalDateTime startCompetency = LocalDateTime.now();
            logAction("Start Time for competency selection: " + startCompetency.format(formatter));
            jsUtil.clickElementByJS(driver.findElement(By.xpath("//a[@href='#']")));
            eleutil.handleCompetencyMenue(CompetencyInput, competency);//Clicking on copetency field and selecting the value from Excel
            LocalDateTime endCompetency = LocalDateTime.now();
            Duration durationCompetency = Duration.between(startCompetency, endCompetency);
            logAction("Time taken for competency selection: " + durationCompetency.toMinutes() + " min " + durationCompetency.toSecondsPart() + " sec");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            // Capture start time for designation selection
            LocalDateTime startDesignation = LocalDateTime.now();
            logAction("Start Time for designation selection: " + startDesignation.format(formatter));
            jsUtil.zoomFirefoxChromeEdgeSafari("60");
            if (A1_P_GR00001.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_PARTNER);
            } else {
                logAction("A1_P_GR00001 column value is No");
            }
            if (A2_ED_GR00002.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_Executive_Director);
            } else {
                logAction("A2_ED_GR00002 column value is No");
            }
            if (A3_D_GR00003.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_Director);
            } else {
                logAction("A3_D_GR00003 Column value is No ");
            }
            if (B1_AD_GR00004.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_Associate_Director);
            } else {
                logAction("B1_AD_GR00004 Column value is No ");
            }
            if (B2_M_GR00005.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_MANAGER);
            } else {
                logAction("B2_M_GR00005 Column value is No ");
            }
            if (C1_AM_GR00006.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_Assistant_MANAGER);
            } else {
                logAction("C1_AM_GR00006 Column value is No ");
            }
            if (C2_SA_GR00007.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_ASSOCIATE);
            } else {
                logAction("C2_SA_GR00007 Column value is No ");
            }
            if (D1_GT_GR00008.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_Graduate_Trainee);
            } else {
                logAction("D1_GT_GR00008 Column value is No ");
            }
            if (D2_T_GR00009.equals("Yes")) {
                eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_Trainee);
            } else {
                logAction("D2_T_GR00009 Column value is No ");
            }
            LocalDateTime endDesignation = LocalDateTime.now();
            Duration durationDesignation = Duration.between(startDesignation, endDesignation);
            logAction("Time taken for designation selection: " + durationDesignation.toMinutes() + " min " + durationDesignation.toSecondsPart() + " sec");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            String text = eleutil.doGetText(popUpText);
            jsUtil.zoomFirefoxChromeEdgeSafari("60");
            logAction(text);
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
            eleutil.clickWhenReady(yesBtn, TimeUtil.DEFAULT_TIME_OUT);
            String successMessage = eleutil.waitForElementVisible(successMsg, TimeUtil.MEDIUM_TIME_OUT).getText();
            logAction("Skill added and tagged successfully " + successMessage);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            if (successMessage.equalsIgnoreCase(AppConstants.SKILL_MASTER_SKILL_ADDITON_SUCCESS_MESSAGE)) {
                return true;
            } else
                return false;
        }

    }

    public boolean handleFailedSkill(String fileName, String skillID, String skillname, String description, String AnyRemarks, String starting, String building, String skilled, String excelled, String A1_P_GR00001, String A2_ED_GR00002, String A3_D_GR00003, String B1_AD_GR00004, String B2_M_GR00005, String C1_AM_GR00006, String C2_SA_GR00007, String D1_GT_GR00008, String D2_T_GR00009, String BU, String Expertise, String Specialisation, String competency, String Classification, String Category) {

        Actions act = new Actions(driver);
        JavascriptUtil jsUtil = new JavascriptUtil(driver);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        eleutil.clickWhenReady(skillNameFilter, TimeUtil.DEFAULT_TIME_OUT);
        eleutil.clickWhenReady(filterIcon, TimeUtil.DEFAULT_TIME_OUT);
        try {
            eleutil.handleParentSubMenuWithClick(clickContains,selectEquals);//Select the filter option as Equal
        } catch (ElementException e) {
            throw new ElementException("Element is not found on DOM");
        }
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
                    Thread.sleep(4000);  // Consider replacing with WebDriverWait for better efficiency
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                jsUtil.clickElementByJS(driver.findElement(By.xpath("(// span[@ref='eMenu'])[1]")));
                eleutil.clickWhenReady(filterIcon, TimeUtil.DEFAULT_TIME_OUT);
                Thread.sleep(2000);
                eleutil.doSendKeys(this.competencyContains, competency, TimeUtil.DEFAULT_TIME_OUT);

                try {
                    WebElement editBtn = eleutil.waitForElementVisible(editCompetencyBtn, 5);
                    if (editBtn != null) {
                        logAction("This skill is tagged to a competency");
                        Thread.sleep(1000);
                        jsUtil.clickElementByJS(driver.findElement(By.xpath("(// button[@type='button'])[7]")));// Click back button
                        return true;  // Exit method since skill is already tagged
                    }
                } catch (NoSuchElementException | TimeoutException e) {
                    logAction("Edit competency button not found: " + e.getMessage());
                }

            } catch (TimeoutException e) {
                logAction("Skill competency not found, retrying...");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            attempts++; // Increment attempt count
        }

// If competency is still not found after retries, proceed to add a new skill
        logAction("Competency not found after retries. Proceeding to add skill...");

        // If the competency was not found, proceed to add the skill
        eleutil.doClick(addNewSkillBtn, TimeUtil.DEFAULT_TIME_OUT);// Click on AdNewSkill button
        eleutil.doSendKeys(skillName1, skillname, TimeUtil.DEFAULT_TIME_OUT);// Inputing the skill name
        try {
            Thread.sleep(2000);
        } catch (InterruptedException f) {
            throw new RuntimeException(f);
        }
        try {
            eleutil.handleDropdownMenue(skillCategory, Category);
        } catch (RuntimeException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        eleutil.doSendKeys(this.skillDescription, description, TimeUtil.DEFAULT_TIME_OUT);
        eleutil.doSendKeys(this.staringInput, starting, TimeUtil.DEFAULT_TIME_OUT);
        eleutil.doSendKeys(this.buildingInput, building, TimeUtil.DEFAULT_TIME_OUT);
        eleutil.doSendKeys(this.skilledInput, skilled, TimeUtil.DEFAULT_TIME_OUT);
        eleutil.doSendKeys(this.excelledInput, excelled, TimeUtil.DEFAULT_TIME_OUT);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        jsUtil.clickElementByJS(driver.findElement(By.xpath("//a[@href='#']")));
        eleutil.handleCompetencyMenue(CompetencyInput, competency);//Clicking on copetency field and selecting the value from Excel
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        jsUtil.zoomFirefoxChromeEdgeSafari("67");
        if (A1_P_GR00001.equals("Yes")) {
            eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_PARTNER);
        } else {
            logAction("A1_P_GR00001 column value is No");
        }
        if (A2_ED_GR00002.equals("Yes")) {
            eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_Executive_Director);
        } else {
            logAction("A2_ED_GR00002 column value is No");
        }
        if (A3_D_GR00003.equals("Yes")) {
            eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_Director);
        } else {
            logAction("A3_D_GR00003 Column value is No ");
        }
        if (B1_AD_GR00004.equals("Yes")) {
            eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_Associate_Director);
        } else {
            logAction("B1_AD_GR00004 Column value is No ");
        }
        if (B2_M_GR00005.equals("Yes")) {
            eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_MANAGER);
        } else {
            logAction("B2_M_GR00005 Column value is No ");
        }
        if (C1_AM_GR00006.equals("Yes")) {
            eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_Assistant_MANAGER);
        } else {
            logAction("C1_AM_GR00006 Column value is No ");
        }
        if (C2_SA_GR00007.equals("Yes")) {
            eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_ASSOCIATE);
        } else {
            logAction("C2_SA_GR00007 Column value is No ");
        }
        if (D1_GT_GR00008.equals("Yes")) {
            eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_Graduate_Trainee);
        } else {
            logAction("D1_GT_GR00008 Column value is No ");
        }
        if (D2_T_GR00009.equals("Yes")) {
            eleutil.handleDesignationMenue1(DesignationInput, AppConstants.DESIGNATION_MASTER_GRADE_Trainee);
        } else {
            logAction("D2_T_GR00009 Column value is No ");
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        String text = eleutil.doGetText(popUpText);
        jsUtil.zoomFirefoxChromeEdgeSafari("67");
        logAction(text);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        eleutil.doActionsClick(popUpText);// This will click on Add Mapping Text.
        jsUtil.clickElementByJS(driver.findElement(By.xpath("(//button[@type='submit'])[2]")));
        jsUtil.clickElementByJS(driver.findElement(By.xpath("(//button[@type='submit'])[1]")));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        eleutil.clickWhenReady(yesBtn, TimeUtil.DEFAULT_TIME_OUT);
        String successMessage = eleutil.waitForElementVisible(successMsg, TimeUtil.MEDIUM_TIME_OUT).getText();
        logAction("Skill added and tagged successfully " + successMessage);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        if (successMessage.equalsIgnoreCase(AppConstants.SKILL_MASTER_SKILL_ADDITON_SUCCESS_MESSAGE)) {
            return true;
        } else
            return false;
    }

    /**
     * This method is used to verify the functionality of skill search by entering the skills
     * in the skill dropdown
     *
     * @return True if entering the skill name the values are visible else false
     */
    public boolean skillSearch() {
        Actions act = new Actions(driver);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(skillsBtn);
        eleutil.handleParentSubMenu(skillsBtn, skillSearchBtn);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new ElementException("Element not visible on the page yet ");
        }
        eleutil.doActionsClick(searchDrpn);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsSendKeys(searchDrpn, "Agile Methodology (SK000040)");
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        String resultSkillName = eleutil.waitForElementVisible(searchedResult, TimeUtil.MEDIUM_TIME_OUT).getText();
        logAction("Searched result includes " + resultSkillName);
        if (resultSkillName.equalsIgnoreCase(AppConstants.SEARCHED_SKILL_RESULT)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method is used to verify the funcationlity of searching the skill based on employee name
     * entered
     *
     * @return true and employee designation with skill details else false ( throws exception as ===employee skill not found===)
     */
    public boolean searchByEmployeeName() {
        Actions act = new Actions(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(toggleBtn);
        eleutil.doActionsClick(searchEmp);
        eleutil.doActionsSendKeys(searchEmp, "RMSED.ResourceReq2@IN.GT.COM");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        String resultSkillName = eleutil.waitForElementVisible(employeeDsgn, TimeUtil.MEDIUM_TIME_OUT).getText().trim();
        logAction("Searched result includes " + resultSkillName);
        if (resultSkillName.equalsIgnoreCase(AppConstants.EMPLOYEE_SKILL_DESIGNATION)) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * Adds a new skill for the logged-in user from the "My Skills" section by
     * selecting the first available (enabled) skill from the autocomplete dropdown,
     * assigning a default proficiency, and saving the selection.
     *
     * <p>
     * Disabled or already-used skills are skipped automatically. If no valid skill
     * is available, the method exits safely without failing the test.
     *
     * <p>
     * The method navigates through the UI, performs the required selections, and
     * validates success using the confirmation message displayed after saving.
     *
     * @return {@code true} if the skill is added successfully and the success
     *         message matches {@link AppConstants#SKILL_ADDITION_SUCCESS_MESSAGE};
     *         {@code false} otherwise.
     *
     * @throws RuntimeException if the execution thread is interrupted during waits.
     */
    public String addSkill() {
        Actions act = new Actions(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // 1) Navigate into “My Skills” → “Add New Skill”
            Thread.sleep(2000);
            eleutil.doActionsClick(skillsBtn);
            eleutil.handleParentSubMenu(skillsBtn, mySkills);
            Thread.sleep(2000);
            eleutil.doActionsClick(addNewSkill);
            Thread.sleep(2000);

            // 2) Locate and open the Skill dropdown input (second text field)
            WebElement skillInput = wait.until(
                    ExpectedConditions.elementToBeClickable(skillName)
            );
            skillInput.click();
            Thread.sleep(500);

            // 3) Fetch all skill‐option WebElements from the dropdown
            //    'skillsOptions' should locate each <li> representing a dropdown item
            List<WebElement> skillElements = eleutil.getElements(skillsOptions);
            if (skillElements.isEmpty()) {
                throw new SkillAdditionException("❌ No skill options available");
            }

            // 4) Find the first truly enabled <li> whose class contains “MuiAutocomplete-option”
            WebElement chosenOption = null;
            String chosenSkill = null;
            for (WebElement option : skillElements) {
                String ariaDisabled = option.getAttribute("aria-disabled");
                String classAttr = option.getAttribute("class");
                boolean isDisabled = "true".equalsIgnoreCase(ariaDisabled);
                boolean isOptionClass = classAttr != null && classAttr.contains("MuiAutocomplete-option");

                if (isOptionClass && !isDisabled) {
                    String text = option.getText().trim();
                    if (!text.isEmpty()) {
                        chosenOption = option;
                        chosenSkill = text;
                        break;
                    }
                }
            }

            if (chosenOption == null) {
                throw new SkillAdditionException("❌ No unused (enabled) skill found");
            }

            // 5) Move to that option and click via Actions (instead of JS click)
            act.moveToElement(chosenOption)
                    .pause(Duration.ofMillis(200))
                    .click()
                    .perform();
            logAction("✅ Selected skill: " + chosenSkill);

            // 6) Handle proficiency selection (caller will implement details)
            eleutil.doActionsClick(proficiency);
            Thread.sleep(1000);
            eleutil.getElement(selectingStarting);
            act.sendKeys(Keys.ARROW_DOWN).perform();
            act.sendKeys(Keys.ENTER).perform();
            Thread.sleep(1000);

            // 7) Click the Save button
            eleutil.doActionsClick(saveBtn2);
            Thread.sleep(500);

            // 8) Wait for and verify the success message
            WebElement successElement = eleutil.waitForElementVisible(
                    skillAddedMessage, TimeUtil.MEDIUM_TIME_OUT
            );
            String successMessage = successElement.getText().trim();
            logAction("Skill added: " + successMessage);
            if (!successMessage.equalsIgnoreCase(
                    AppConstants.SKILL_ADDITION_SUCCESS_MESSAGE)) {

                throw new IllegalStateException(
                        "Skill addition failed. Expected: '"
                                + AppConstants.SKILL_ADDITION_SUCCESS_MESSAGE
                                + "', Actual: '" + successMessage + "'"
                );
            }
            return successMessage;
        }
        catch (InterruptedException ie) {
            throw new RuntimeException("Interrupted while waiting", ie);
        }
        catch (TimeoutException te) {
            throw new TimeoutException("❌ Timeout waiting for element");
        }
        catch (NoSuchElementException nsee) {
            throw new ElementException("❌ Could not locate element");
        }
    }
    /**
     * Performs a bulk skill approval as a SuperCoach user.
     *
     * <p>
     * The method logs out the current session, authenticates as a SuperCoach,
     * navigates to the Skill Review module, selects all available skill entries,
     * submits a bulk approval with remarks, and returns the resulting confirmation
     * message.
     *
     * @return the confirmation message displayed after successful bulk approval
     *
     * @throws IllegalStateException if authentication, navigation, or approval
     *         actions fail due to missing or unstable UI elements
     * @throws RuntimeException if the execution thread is interrupted
     */
    public String skillReviewBySuperCoach() {
        JavascriptUtil jsUtil = new JavascriptUtil(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        // 1) Logout current user
        try {
            eleutil.waitForElementVisible(clickOnUserName, 10);
            eleutil.clickStable(clickOnUserName, 10);

            eleutil.waitForElementVisible(clickOnLogoutBtn, 10);
            eleutil.clickStable(clickOnLogoutBtn, 10);
        } catch (TimeoutException te) {
            throw new IllegalStateException("Logout controls not visible/clickable. " +
                    "Cannot proceed to SuperCoach login.", te);
        }
        // 2) Pick account (RMSED Leader) or click via JS fallback
        try {
            // Prefer a standard click path
            eleutil.waitForElementVisible(rmseLeaderOption,20);
            //wait.until(ExpectedConditions.visibilityOfElementLocated(rmseLeaderOption));
            eleutil.clickStable(clickOnAccount, 15);
        } catch (Exception e) {
            // Fallback: click the exact account option
            WebElement leader= eleutil.waitForElementVisible(rmseLeaderOption,20);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", leader);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", leader);
        }
        // 3) Choose "Use another account"
        try {
            eleutil.clickStable(clickOnUseOtherAccount, 15);
        } catch (TimeoutException te) {
            throw new IllegalStateException("'Use another account' option not available. " +
                    "The account picker may not have opened correctly.", te);
        }
        // 4) Enter email
        try {
            eleutil.enterTextReliable(emailInputField, requiredRuntimeCredential("supercoach.username", "RMT_SUPERCOACH_USERNAME"), 15);
            eleutil.clickStable(nextBtn, 10);
        } catch (TimeoutException te) {
            throw new IllegalStateException("Email field or Next button not available during sign-in.", te);
        } catch (IllegalStateException ise) {
            throw new IllegalStateException("Failed to enter email due to re-render/focus issues.", ise);
        }
        // 5) Enter password (core stability + exception if not visible or not entered)
        try {
            // Ensure visible and enabled
            WebElement pwdEl = eleutil.waitForElementVisible(passwordInputField, 15);
            if (!pwdEl.isDisplayed() || !pwdEl.isEnabled()) {
                throw new IllegalStateException("Password field is present but not visible/enabled.");
            }
            // Scroll and enter reliably (with verification)
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", pwdEl);
            eleutil.enterTextReliable(passwordInputField, requiredRuntimeCredential("supercoach.password", "RMT_SUPERCOACH_PASSWORD"), 15);

            // Optional: extra verification step if page tends to re-render
            String pwdValue = driver.findElement(passwordInputField).getAttribute("value");
            if (pwdValue == null || pwdValue.isEmpty()) {
                throw new IllegalStateException("Password value did not stick after entry—UI may be re-rendering.");
            }
        } catch (TimeoutException te) {
            throw new NoSuchElementException("Password field is not visible within timeout—" +
                    "login page may not have loaded, or an overlay is blocking.", te);
        } catch (StaleElementReferenceException sere) {
            // re-locate once more and try again
            WebElement pwdEl = eleutil.waitForElementVisible(passwordInputField, 10);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", pwdEl);
            eleutil.enterTextReliable(passwordInputField, requiredRuntimeCredential("supercoach.password", "RMT_SUPERCOACH_PASSWORD"), 10);
        } catch (IllegalStateException ise) {
            throw new IllegalStateException("Failed to enter password: " + ise.getMessage(), ise);
        }
        // 6) Click Sign-in
        try {
            eleutil.clickStable(clickOnSignInBtn, 15);
        } catch (TimeoutException te) {
            throw new IllegalStateException("Sign-in button not clickable. " +
                    "An overlay or validation error might be blocking.", te);
        }
        // 7) Navigate to Skill Review
        try {
            // Menu open
            eleutil.clickStable(skillsBtn, 15);
            // Skill review button
            eleutil.waitForElementVisible(skillReviewBtn, 15);
            eleutil.clickStable(skillReviewBtn, 15);
            // Ensure grid/header are ready & overlays gone
            By headerRoot = By.cssSelector(".ag-header, .ag-header-viewport, .ag-pinned-left-header");
            By overlays = By.cssSelector(".ag-overlay-loading-center, .ag-overlay-panel, .MuiBackdrop-root, .cdk-overlay-backdrop");
            wait.until(ExpectedConditions.presenceOfElementLocated(headerRoot));
            try {
                wait.until(ExpectedConditions.invisibilityOfElementLocated(overlays));
            } catch (Exception ignored) {
            }
            // Try multiple header locations for the select-all checkbox
            By[] candidates = new By[]{
                    By.cssSelector(".ag-header input.ag-checkbox-input[aria-label*='toggle all rows selection']"),
                    By.cssSelector(".ag-pinned-left-header input.ag-checkbox-input[aria-label*='toggle all rows selection']"),
                    By.cssSelector("input.ag-checkbox-input[aria-label*='toggle all rows selection']")
            };
            // Find the first present checkbox
            WebElement cb = null;
            for (By loc : candidates) {
                List<WebElement> found = driver.findElements(loc);
                if (!found.isEmpty()) {
                    cb = found.get(0);
                    break;
                }
            }
            if (cb == null) {
                throw new IllegalStateException("Select-all checkbox not present in header. " +
                        "Header may not be rendered, selection column not configured, or wrong context/frame.");
            }

            // Scroll input (and wrapper) into view
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", cb);
            WebElement wrapper;
            try {
                wrapper = cb.findElement(By.xpath("./ancestor::div[contains(@class,'ag-checkbox-input-wrapper')]"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", wrapper);
            } catch (NoSuchElementException ignored) {
                wrapper = cb; // fallback to input itself
            }
            // Try native click on input, fallback to wrapper JS click
            try {
                wait.until(ExpectedConditions.elementToBeClickable(cb)).click();
            } catch (Exception e) {
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(wrapper)).click();
                } catch (Exception e2) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", wrapper);
                }
            }
            // Verify toggled; if not, try focusing and SPACE
            Boolean checked = (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checked;", cb);
            if (!Boolean.TRUE.equals(checked)) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", cb);
                cb.sendKeys(Keys.SPACE);
                checked = (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checked;", cb);
                if (!Boolean.TRUE.equals(checked)) {
                    throw new IllegalStateException("Select-all checkbox did not toggle after click/SPACE. " +
                            "It may be re-rendering, disabled, or covered by an overlay.");
                }
            }
            // Bulk approve flow
            eleutil.waitForElementVisible(clickOnBulkApproveBtn, 15);
            eleutil.clickStable(clickOnBulkApproveBtn, 15);
            eleutil.doActionsSendKeysWithPause(enterRemarks, "Approved", 10);
            eleutil.clickStable(clickOnConfirmBtn, 15);
        } catch (TimeoutException te) {
            throw new IllegalStateException("Skill review flow controls not found/clickable.", te);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 8) Verify success
        WebElement successElement = eleutil.waitForElementVisible(
                skillApprovedMessage, TimeUtil.LONG_TIME_OUT
        );
        String successMessage = successElement.getText().trim();
        logAction("Skill updated: " + successMessage);

        return successMessage;
    }

    /**
     * Logs out the current user, selects an account from the Microsoft account picker,
     * clicks "Use another account", signs in with provided credentials, clicks "Yes"
     * on stay-signed-in prompt when shown, and returns to Project Listings context.
     *
     * @param accountPickerLabel account tile label shown on account picker (e.g. RMSED.ResourceReq2)
     * @param email login email to enter after clicking "Use another account"
     * @param password login password
     * @return ProjectListingsPage object after successful sign-in
     */
    public ProjectListingsPage switchAccountUsingExistingFlow(String accountPickerLabel, String email, String password) {
        List<By> accountCandidates = buildMicrosoftAccountCandidates(accountPickerLabel == null ? "" : accountPickerLabel.trim());
        By useAnotherAccountTile = By.xpath("//*[contains(normalize-space(.),'Use another account') or contains(normalize-space(.),'Add another account')]/ancestor::*[self::button or self::li or self::div[@role='button'] or self::div[@role='option']][1]");
        By[] staySignedInCandidates = {
                By.xpath("//input[@value='Yes' or @id='idSIButton9']"),
                By.xpath("//button[normalize-space()='Yes']")
        };
        By[] accountMenuCandidates = {
                clickOnUserName,
                By.xpath("//div[contains(@class,'user-container')]//button"),
                By.xpath("//button[contains(@aria-label,'account')]"),
                By.xpath("//*[contains(@class,'user-container')]//*[self::button or self::div[@role='button']][contains(normalize-space(.),'Resourcereq') or contains(normalize-space(.),'RMSED')]")
        };
        By[] logoutCandidates = {
                By.xpath("//li[normalize-space()='Logout']"),
                By.xpath("//li[normalize-space()='Log Out']"),
                By.xpath("//li[normalize-space()='Sign out']"),
                By.xpath("//button[normalize-space()='Logout']"),
                By.xpath("//button[normalize-space()='Log Out']"),
                By.xpath("//span[normalize-space()='Logout']/ancestor::*[self::li or self::button][1]"),
                clickOnLogoutBtn
        };
        eleutil.logoutAndLoginWithMicrosoftAccount(
                accountMenuCandidates,
                logoutCandidates,
                accountCandidates.toArray(new By[0]),
                new By[]{useAnotherAccountTile, clickOnUseOtherAccount},
                new By[]{emailInputField},
                nextBtn,
                new By[]{passwordInputField},
                clickOnSignInBtn,
                staySignedInCandidates,
                email,
                password
        );
        eleutil.waitForAppLanding();
        return new ProjectListingsPage(driver);
    }

    private List<By> buildMicrosoftAccountCandidates(String normalizedAccountLabel) {
        List<By> accountCandidates = new ArrayList<>();
        if (!normalizedAccountLabel.isEmpty()) {
            String spaceVariant = normalizedAccountLabel.replace('.', ' ');
            accountCandidates.add(By.xpath("//*[contains(normalize-space(.),'" + normalizedAccountLabel + "')]/ancestor::*[self::button or self::li or self::div[@role='button'] or self::div[@role='option']][1]"));
            accountCandidates.add(By.xpath("//*[contains(normalize-space(.),'" + spaceVariant + "')]/ancestor::*[self::button or self::li or self::div[@role='button'] or self::div[@role='option']][1]"));
            accountCandidates.add(By.xpath("//*[contains(normalize-space(.),'" + normalizedAccountLabel + "')]"));
            accountCandidates.add(By.xpath("//*[contains(normalize-space(.),'" + spaceVariant + "')]"));
            if (normalizedAccountLabel.contains("@")) {
                String localPart = normalizedAccountLabel.substring(0, normalizedAccountLabel.indexOf('@')).trim();
                if (!localPart.isEmpty()) {
                    String localVariant = localPart.replace('.', ' ');
                    accountCandidates.add(By.xpath("//*[contains(normalize-space(.),'" + localVariant + "')]/ancestor::*[self::button or self::li or self::div[@role='button'] or self::div[@role='option']][1]"));
                    accountCandidates.add(By.xpath("//*[contains(normalize-space(.),'" + localVariant + "')]"));
                }
            }
        }
        accountCandidates.add(rmseLeaderOption);
        accountCandidates.add(clickOnAccount);
        accountCandidates.add(clickOnSCId);
        return accountCandidates;
    }

    /**
     * Verifies whether a skill has been approved by the SuperCoach user.
     *
     * <p>
     * The method logs out the current user, authenticates using a SuperCoach
     * (Admin) account, navigates to the Skills module, and reads the approval
     * status displayed in the AG Grid. The status is determined by inspecting
     * the status chip text rendered in the grid.
     *
     * <p>
     * The method follows a fail-fast approach for infrastructure or UI failures
     * (authentication issues, navigation failures, missing grid elements).
     * Business validation is handled via the return value.
     *
     * @return {@code true} if the skill status is {@code "Approved"};
     *         {@code false} if the status is present but not approved
     *
     * @throws IllegalStateException if login, navigation, or status retrieval
     *         fails due to missing, blocked, or unstable UI elements
     */
    public String skillStatusCheck() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            // =========================
            // 1) Logout current user
            // =========================
            eleutil.waitForElementVisible(clickOnUserName, 10);
            eleutil.clickStable(clickOnUserName, 10);
            eleutil.waitForElementVisible(clickOnLogoutBtn, 10);
            eleutil.clickStable(clickOnLogoutBtn, 10);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // =========================
            // 2) Select SuperCoach / Admin account
            // =========================
            try {
                // Prefer a standard click path
                eleutil.waitForElementVisible(clickOnSCId,20);
                //wait.until(ExpectedConditions.visibilityOfElementLocated(rmseLeaderOption));
                eleutil.clickStable(clickOnAdminAccount, 15);
            } catch (Exception e) {
                // Fallback: click the exact account option
                WebElement Admin= eleutil.waitForElementVisible(clickOnSCId,20);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", Admin);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", Admin);
            }
            // 3) Choose "Use another account"
            try {
                eleutil.clickStable(clickOnUseOtherAccount, 15);
            } catch (TimeoutException te) {
                throw new IllegalStateException("'Use another account' option not available. " +
                        "The account picker may not have opened correctly.", te);
            }
            // 4) Enter email
            try {
                eleutil.enterTextReliable(emailInputField, requiredRuntimeCredential("supercoach.username", "RMT_SUPERCOACH_USERNAME"), 15);
                eleutil.clickStable(nextBtn, 10);
            } catch (TimeoutException te) {
                throw new IllegalStateException("Email field or Next button not available during sign-in.", te);
            } catch (IllegalStateException ise) {
                throw new IllegalStateException("Failed to enter email due to re-render/focus issues.", ise);
            }
            // 5) Enter password (core stability + exception if not visible or not entered)
            try {
                // Ensure visible and enabled
                WebElement pwdEl = eleutil.waitForElementVisible(passwordInputField, 15);
                if (!pwdEl.isDisplayed() || !pwdEl.isEnabled()) {
                    throw new IllegalStateException("Password field is present but not visible/enabled.");
                }
                // Scroll and enter reliably (with verification)
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", pwdEl);
                eleutil.enterTextReliable(passwordInputField, requiredRuntimeCredential("supercoach.password", "RMT_SUPERCOACH_PASSWORD"), 15);

                // Optional: extra verification step if page tends to re-render
                String pwdValue = driver.findElement(passwordInputField).getAttribute("value");
                if (pwdValue == null || pwdValue.isEmpty()) {
                    throw new IllegalStateException("Password value did not stick after entry—UI may be re-rendering.");
                }
            } catch (TimeoutException te) {
                throw new NoSuchElementException("Password field is not visible within timeout—" +
                        "login page may not have loaded, or an overlay is blocking.", te);
            } catch (StaleElementReferenceException sere) {
                // re-locate once more and try again
                WebElement pwdEl = eleutil.waitForElementVisible(passwordInputField, 10);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", pwdEl);
                eleutil.enterTextReliable(passwordInputField, requiredRuntimeCredential("supercoach.password", "RMT_SUPERCOACH_PASSWORD"), 10);
            } catch (IllegalStateException ise) {
                throw new IllegalStateException("Failed to enter password: " + ise.getMessage(), ise);
            }
            // 6) Click Sign-in
            try {
                eleutil.clickStable(clickOnSignInBtn, 15);
            } catch (TimeoutException te) {
                throw new IllegalStateException("Sign-in button not clickable. " +
                        "An overlay or validation error might be blocking.", te);
            }
            eleutil.waitForElementVisible(skillsBtn,5,5);
            // 7) Navigate to Skills module
            eleutil.clickStable(skillsBtn, 5);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            eleutil.clickStable(mySkills,10);
            // 8) Wait for AG Grid status chip
            WebElement statusElement=eleutil.waitForElementVisible(statusLocator,5);
            String statusText = statusElement.getText().trim();
            logAction("Skill status from grid: " + statusText);
            // =========================
            // 9) Return Status String  value
            // =========================
            return statusText;

        }
        catch (TimeoutException e) {
            throw new IllegalStateException(
                    "Timeout while verifying skill approval status from SuperCoach", e
            );
        }
        catch (NoSuchElementException e) {
            throw new IllegalStateException(
                    "Required UI element missing during skill status verification", e
            );
        }
    }

    /**
     * Reads credentials used only by legacy SuperCoach helpers from a Maven property or environment variable.
     * Keeping the values outside source control prevents test-account credentials from entering Git history.
     */
    private String requiredRuntimeCredential(String systemPropertyKey, String environmentVariableKey) {
        String value = System.getProperty(systemPropertyKey);
        if (value == null || value.trim().isEmpty()) {
            value = System.getenv(environmentVariableKey);
        }
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing runtime credential. Provide -D" + systemPropertyKey
                    + " or environment variable " + environmentVariableKey + ".");
        }
        return value.trim();
    }
}
