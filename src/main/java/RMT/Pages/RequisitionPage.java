package RMT.Pages;

import RMT.Constants.AppConstants;
import RMT.Utils.ElementUtil;
import RMT.Utils.JavascriptUtil;
import RMT.Utils.TimeUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static java.lang.Thread.sleep;

public class RequisitionPage {
    // Strategy B: start by picking “today + 7 days”
    private static LocalDate nextDateToPick = LocalDate.now().plusDays(18);
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
    private By AMDesgn=By.xpath("(//li[text()='C1 (AM) | Assistant Manager'])");
    private By DirectorDesgn=By.xpath("(//li[text()='A3 (D) | Director'])");
    private By Competency=By.xpath("(//li[text()='Due Diligence'])");
    private By saveBtn=By.xpath("(//button[@type='submit'])");
    private By successMsg=By.xpath("(//div[@class='MuiAlert-message css-1xsto0d'])");
    private By successMsgForSystemSuggestion=By.xpath("(//div[@class='MuiAlert-message css-1xsto0d'])");
    private By skilldropn=By.xpath("(//input[@type='text'])[5]");
    private By skillSelect=By.xpath("(//li[text()='Agile Methodology (SK000040)'])");
    private By skillSelectForSystemSuggestion=By.xpath("(//li[text()='Financial Modelling (SK000064)'])");
    private By systemSuggestions=By.xpath("//button[text()='Search']");
    private By card=By.xpath("// div[text()='Ritika Sharma']");
    private By cardForDirector=By.xpath("// div[text()='RMSED Offering']");
    private By cardView=By.xpath("//*[name()='svg' and @aria-label='Cards View']");
    private By startDate=By.xpath("(//input[@placeholder='DD-MM-YYYY'])[1]");
    private By weekDay=By.xpath("(//button[@tabindex='0'])[15]");
    private By backBtn=By.xpath("//*[name()='svg' and @data-testid='ArrowBackIosNewIcon']");
    private By confirmBtn=By.xpath("//button[text()='Yes']");
    private By deleteRequisiton = By.xpath("(//*[name()='svg' and @data-testid='DeleteOutlinedIcon'])[1]");
    private By yesBtn=By.xpath("//button[text()='Yes']");
    private By deleteSuccessMessage=By.xpath("//div[@class='MuiAlert-message css-1xsto0d']");
    private By editIcon=By.xpath("(//button[@aria-label='View-update requisition'])[1]");
    private By editReqBtn=By.xpath("(//button[@aria-label='Edit requisition'])[1]");
    private By optionalSkill=By.xpath("//li[text()='Selenium (SK000039)']");
    private By industryDrpn=By.xpath("(// input[@type='text'])[7]");
    private By selectIndustry=By.xpath("(// li[text()='Food Processing'])");
    private By subIndustryDrpn=By.xpath("(// input[@type='text'])[10]");
    private By subIndustry=By.xpath("(// li[text()='Food Processing-GCC'])");
    private By updateSuccessMesg=By.xpath("//div[@class='MuiAlert-message css-1xsto0d']");
    private By subIndustrySlider=By.xpath("(//div[@class='MuiTypography-root MuiTypography-body1 css-1nnquuk'])[7]");
    private By exportBtn=By.xpath("//div[@class='download-icon']");
    private By locationDrpn=By.xpath("(//input[@type='text'])[8]");
    private By locationSelect=By.xpath("(//li[text()='DEL'])");
    private By detailView=By.xpath("(//button[text()='Detail View'])");
    private By delegateDropwn=By.xpath("((//input[@placeholder='Type And Select']))");
    private By addtionalElDropdn=By.xpath("((//input[@type='text']))[2]");
    private By addtionalDelegateDropdwn=By.xpath("((//input[@type='text']))[3]");
    private By updateDetailsBtn=By.xpath("(//button[text()='Update Details'])");
    private By clickOnAdd=By.xpath("//span[text()='Add']");
    private By clickCalenderBtn=By.xpath("(//button[@type='button'])[10]");
    private By createReqBtn=By.xpath("//button[text()='Create Requisition']");
    private By requitionGrid=By.xpath("//button[text()='Requisitions']");
    private By clickOnCheckbox=By.xpath("(//input[@type='checkbox'])[2]");
    private By clickOnViewCalender=By.xpath("(//button[text()='View Calendar'])");
    private By clickOnCommanAllocationCheckbox=By.xpath("(//input[@type='checkbox'])[3]");
    private By clickOnAllocateBtn=By.xpath("//button[text()='Allocate']");
    private By arrowRightIcon=By.xpath(" .//*[name()='svg' and @data-testid='ArrowRightIcon']");



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

    /**
     * This method is used to verify the functionality of creating requisition
     * @return boolean value as TRUE if success message is visible else FALSE
     */
    public boolean CreateRequisition(){
        Actions act = new Actions(driver);
        JavascriptUtil jsUtil= new JavascriptUtil(driver);
        jsUtil.zoomFirefoxChromeEdgeSafari("67");
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(DesignationInput);
        eleutil.sendKeysWithWait(DesignationInput,"C1 (AM) | Assistant Manager",2);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(AMDesgn);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        eleutil.doActionsSendKeys(TaskDescription,"Require Assistant Manager for the job");
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        jsUtil.scrollIntoView(eleutil.getElement(startDate));
        jsUtil.clickElementByJS(eleutil.getElement(clickCalenderBtn));
        // Wait for calendar grid to appear
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'MuiDayCalendar-weekContainer')]")));

// Get all weekday buttons (Mon-Fri) that are not disabled
        List<WebElement> weekdayButtons = driver.findElements(By.xpath(
                "//button[contains(@class,'MuiPickersDay-root') and not(contains(@class,'Mui-disabled')) and " +
                        "@aria-colindex <= 5]"
        ));

// Optional: Filter for tomorrow onward
        LocalDate today = LocalDate.now();
        long todayTimestamp = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        WebElement firstValidButton = null;

        for (WebElement button : weekdayButtons) {
            String timestampStr = button.getAttribute("data-timestamp");
            if (timestampStr != null) {
                long ts = Long.parseLong(timestampStr);
                if (ts > todayTimestamp) {
                    firstValidButton = button;
                    break;
                }
            }
        }

        if (firstValidButton != null) {
            firstValidButton.click();
            System.out.println("✅ Selected weekday date from calendar.");
        } else {
            System.out.println("❌ No valid weekday date available for selection.");
        }
        eleutil.doActionsClick(CompetencyDropown);
        eleutil.sendKeysWithWait(CompetencyDropown,"Due Diligence",3);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(Competency);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(skilldropn);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(skillSelect);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(locationDrpn);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(locationSelect);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.clickWhenReady(saveBtn,TimeUtil.DEFAULT_TIME_OUT);
        String successMessage=eleutil.waitForElementVisible(successMsg,TimeUtil.MEDIUM_TIME_OUT).getText();
        System.out.println("Requisition Status :  " + successMessage);
        if (successMessage.equalsIgnoreCase(AppConstants.REQUISITON_CREATED_MESSAGE)){
            return true;
        }else{
            return false;
        }

    }

    /**
     * This method is used to verify the functionality whether the system suggestion=on card is visible or not
     * @return a boolean TRUE id correct card is visible based on designation and skills selected else FALSE
     */
    public boolean searchAlgo() {
        JavascriptUtil jsUtil = new JavascriptUtil(driver);
        Actions act = new Actions(driver);
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(systemSuggestions);
        try {
            sleep(9000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(cardView);
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        jsUtil.scrollIntoView(eleutil.getElement(card));
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement suggestionCard1 = eleutil.waitForElementVisible(card, 4);
        String suggestionCard=eleutil.waitForElementVisible(card,3).getText();
        System.out.println("Suggestion card visible is : " + suggestionCard);
        if (suggestionCard.equalsIgnoreCase(AppConstants.SYSTEM_SUGGESTED_CARD)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * This method is used to verify the user is able to update the requisition created earlier
     * @return boolean TRUE if user is able to update and False if failed
     */
    public boolean updateRequisiton(){
        Actions act = new Actions(driver);
        JavascriptUtil jsutil=new JavascriptUtil(driver);
        jsutil.scrollIntoView(eleutil.getElement(backBtn));
        eleutil.doActionsClick(backBtn);
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.clickWhenReady(confirmBtn,7);
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.clickWhenReady(editIcon,10);
        try {
            sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.clickWhenReady(editReqBtn,10);
        eleutil.doActionsClick(skilldropn);
        eleutil.sendKeysWithWait(skilldropn,"Selenium",9);
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(optionalSkill);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
//        eleutil.doActionsClick(industryDrpn);
//        eleutil.sendKeysWithWait(industryDrpn,"Food Processing",5);
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        eleutil.getElement(selectIndustry);
//        act.sendKeys(Keys.ARROW_DOWN).perform();
//        act.sendKeys(Keys.ENTER).perform();
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(subIndustryDrpn);
        eleutil.sendKeysWithWait(subIndustryDrpn,"Food Processing-GCC",7);
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(subIndustry);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(subIndustrySlider);
        // Click and drag the slider (Adjust offset based on your UI)
        act.clickAndHold(eleutil.getElement(subIndustrySlider)).moveByOffset(60, 0).release().perform();
        eleutil.clickWhenReady(saveBtn,TimeUtil.DEFAULT_TIME_OUT);
        String successMessage=eleutil.waitForElementVisible(updateSuccessMesg,TimeUtil.MEDIUM_TIME_OUT).getText();
        System.out.println("Requisition Status " + successMessage);
        if (successMessage.equalsIgnoreCase(AppConstants.REQUSIITON_UPDATE_MESSAGE)){
            return true;
        }else{
            return false;
        }


    }

    /**
     * This method is used to verify the user is able to view the updated suggested card
     * @return boolean True if updated card is visible and FALSE if not
     */
    public boolean updatedSearchAlgo(){
        JavascriptUtil jsUtil = new JavascriptUtil(driver);
        Actions act = new Actions(driver);
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(systemSuggestions);
        try {
            sleep(9000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(cardView);
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        jsUtil.scrollIntoView(eleutil.getElement(card));
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement suggestionCard1 = eleutil.waitForElementVisible(card, 4);
        String suggestionCard=eleutil.waitForElementVisible(card,3).getText();
        System.out.println("Suggestion card visible is : " + suggestionCard);
        if (suggestionCard.equalsIgnoreCase(AppConstants.UPDATED_SYSTEM_SUGGESTED_CARD)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Thie method is used to verify the exported functionality is vworking ot not
     * and if exported btn is not Enabled or not working then it will fail
     * @return
     */
    public boolean exportRequisition(){
        JavascriptUtil jsutil=new JavascriptUtil(driver);
        jsutil.scrollIntoView(eleutil.getElement(backBtn));
        eleutil.doActionsClick(backBtn);
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.clickWhenReady(confirmBtn,7);
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        jsutil.scrollIntoView(eleutil.getElement(exportBtn));
        WebElement btn=eleutil.waitForElementVisible(exportBtn,10);
        System.out.println("Exported btn is visible : "+  btn);
        eleutil.doActionsClick(exportBtn);
        if (btn.isEnabled()){
            return true;
        }else{
            return false;
        }


    }

    /**
     * This method is used to validate the functionality to check whether user is able to delete the
     * created requisition or not
     * @return value as True if deletion message is visible else false
     */
    public boolean requisitonDeletion(){
        eleutil.doActionsClick(deleteRequisiton);
        eleutil.doActionsClick(yesBtn);
        String successMessage=eleutil.waitForElementVisible(deleteSuccessMessage,TimeUtil.MEDIUM_TIME_OUT).getText();
        System.out.println("Requisiton Deletion :  " + successMessage);
        if (successMessage.equalsIgnoreCase(AppConstants.REQUSITON_DELETED_MESSAGE)){
            return true;
        }else{
            return false;
        }

    }

    /**
     * This method is used to verify whether user is able to add delegate into a job or not
     * @return False ( Addition of Delegate to a job failed)
     */
    public boolean addDelegate(){
        Actions act=new Actions(driver);
        eleutil.doActionsClick(detailView);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        act
                .click(driver.findElement(By.xpath("((//input[@placeholder='Type And Select']))")))  // Focus on the input field
                .keyDown(Keys.CONTROL)
                .sendKeys("a")                        // Select all text
                .keyUp(Keys.CONTROL)
                .sendKeys(Keys.DELETE)               // Delete selected text
                .perform();
        eleutil.doActionsClick(delegateDropwn);
        eleutil.doActionsSendKeysWithPause(delegateDropwn,"Resource Requester",20);
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        eleutil.doActionsClick(updateDetailsBtn);
        eleutil.doActionsClick(confirmBtn);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String successMessage=eleutil.waitForElementVisible(updateSuccessMesg,TimeUtil.MEDIUM_TIME_OUT).getText();
        System.out.println("Adding Delegate Status " + successMessage);
        if (successMessage.equalsIgnoreCase(AppConstants.ADD_UPDATE_DELEGATE_SUCCESS_MESSAGE)){
            return true;
        }else{
            return false;
        }
    }
    public boolean updateDelegate(){
        Actions act=new Actions(driver);
        eleutil.doActionsClick(detailView);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        act
                .click(driver.findElement(By.xpath("((//input[@placeholder='Type And Select']))")))  // Focus on the input field
                .keyDown(Keys.CONTROL)
                .sendKeys("a")                        // Select all text
                .keyUp(Keys.CONTROL)
                .sendKeys(Keys.DELETE)               // Delete selected text
                .perform();
        eleutil.doActionsClick(delegateDropwn);
        eleutil.doActionsSendKeysWithPause(delegateDropwn,"RMSED.Resource4@IN.GT.COM",10);
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        eleutil.doActionsClick(updateDetailsBtn);
        eleutil.doActionsClick(confirmBtn);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String successMessage=eleutil.waitForElementVisible(updateSuccessMesg,TimeUtil.MEDIUM_TIME_OUT).getText();
        System.out.println("Updating Delegate Status " + successMessage);
        if (successMessage.equalsIgnoreCase(AppConstants.ADD_UPDATE_DELEGATE_SUCCESS_MESSAGE)){
            return true;
        }else{
            return false;
        }

    }

    /**
     * This method is used to verify whether user is able to add delegate into a job or not
     * @return False ( Addition of Delegate to a job failed)
     */
    public boolean addEngadgementLeader(){
        Actions act=new Actions(driver);
        eleutil.doActionsClick(clickOnAdd);
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(addtionalElDropdn);
        eleutil.doActionsSendKeysWithPause(addtionalElDropdn,"Resource Requester",20);
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        eleutil.doActionsClick(updateDetailsBtn);
        eleutil.doActionsClick(confirmBtn);
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String successMessage=eleutil.waitForElementVisible(updateSuccessMesg,TimeUtil.MEDIUM_TIME_OUT).getText();
        System.out.println("Additional EL  Status " + successMessage);
        if (successMessage.equalsIgnoreCase(AppConstants.ADD_UPDATE_DELEGATE_SUCCESS_MESSAGE)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Checks the system's suggestion for resource allocation by performing various UI interactions
     * such as filling form data, selecting dates and options, and handling dynamic elements
     * to verify the allocation success message.
     *
     * The method performs the following tasks:
     * - Adjusts browser zoom level for visibility.
     * - Inputs required data such as designation, task description, and competency.
     * - Handles the date picker to choose a valid weekday.
     * - Selects various dropdown options such as skills, location, and competency.
     * - Verifies that suggestion cards are visible and performs allocation.
     * - Confirms success by comparing the final success message.
     *
     * @return true if the final success message matches the expected resource allocation message indicating
     *         a successful suggestion check and allocation process; false otherwise.
     */
    public boolean checkAllocationSystemSuggestion() {
        Actions act = new Actions(driver);
        JavascriptUtil jsUtil = new JavascriptUtil(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1) Zoom out a bit so the calendar is visible
        jsUtil.zoomFirefoxChromeEdgeSafari("67");
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //eleutil.doActionsClick(createReqBtn);
        jsUtil.zoomFirefoxChromeEdgeSafari("67");
        eleutil.sendKeysWithWait(DesignationInput, "A3 (D) | Director", 2);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(DirectorDesgn);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        eleutil.doActionsSendKeys(TaskDescription, "Require Director for the job");
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 2) Scroll to “Start Date” and open the calendar widget
        jsUtil.scrollIntoView(eleutil.getElement(startDate));
        jsUtil.clickElementByJS(eleutil.getElement(clickCalenderBtn));

        // Wait for the calendar grid
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'MuiDayCalendar-weekContainer')]")
        ));

        // 3) Ensure nextDateToPick isn’t a weekend; if it is, push forward until it isn’t
        while (nextDateToPick.getDayOfWeek() == DayOfWeek.SATURDAY ||
                nextDateToPick.getDayOfWeek() == DayOfWeek.SUNDAY) {
            nextDateToPick = nextDateToPick.plusDays(1);
        }

        // Debug: print out which date we're actually picking
        System.out.println(">> [DEBUG] nextDateToPick = " + nextDateToPick);

        // 4) Convert that LocalDate to a midnight‐local timestamp
        long targetTimestamp = nextDateToPick
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        // 5) Build an XPath to locate the enabled day‐button with that timestamp
        By xpathForTarget = By.xpath(
                "//button[@data-timestamp='" + targetTimestamp + "' " +
                        "and contains(@class,'MuiPickersDay-root') " +
                        "and not(contains(@class,'Mui-disabled'))]"
        );
        List<WebElement> matches = driver.findElements(xpathForTarget);
        // 6) If not found in current view, click “Next Month” once and retry
        if (matches.isEmpty()) {
            WebElement nextArrow = eleutil.getElement(
                    By.xpath("//*[local-name()='svg' and @data-testid='ArrowRightIcon']")
            );
            jsUtil.clickElementByJS(nextArrow);

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'MuiDayCalendar-weekContainer')]")
            ));
            matches = driver.findElements(xpathForTarget);
        }
        // 7) If we found a button, click it; otherwise log failure
        if (!matches.isEmpty()) {
            WebElement targetButton = matches.get(0);
            jsUtil.scrollIntoView(targetButton);
            jsUtil.clickElementByJS(targetButton);
            System.out.println("✅ Selected date: " + nextDateToPick);
        } else {
            System.out.println("❌ No valid weekday date available for " + nextDateToPick);
        }
        // 8) Advance nextDateToPick by one calendar day so next call uses +1 from here
        nextDateToPick = nextDateToPick.plusDays(1);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // … (remaining logic: competency, skill, location, save, suggestions, etc.) …
        eleutil.doActionsClick(CompetencyDropown);
        eleutil.sendKeysWithWait(CompetencyDropown, "Due Diligence", 3);
        try {
            Thread.sleep(1000);
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
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(skillSelect);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(locationDrpn);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(locationSelect);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.clickWhenReady(saveBtn, TimeUtil.DEFAULT_TIME_OUT);
        String successMessage1 = eleutil.waitForElementVisible(successMsg, TimeUtil.MEDIUM_TIME_OUT).getText();
        System.out.println("Requisition Status: " + successMessage1);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(systemSuggestions);
        try {
            Thread.sleep(9000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(cardView);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        jsUtil.scrollIntoView(eleutil.getElement(cardForDirector));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement suggestionCard1 = eleutil.waitForElementVisible(cardForDirector, 4);
        String suggestionCard = eleutil.waitForElementVisible(cardForDirector, 3).getText();
        System.out.println("Suggestion card visible is: " + suggestionCard);
        eleutil.doActionsClick(clickOnCheckbox);
        eleutil.doActionsClick(clickOnViewCalender);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(clickOnCommanAllocationCheckbox);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(clickOnAllocateBtn);
        eleutil.doActionsClick(yesBtn);
        String finalSuccess = eleutil.waitForElementVisible(successMsg, TimeUtil.MEDIUM_TIME_OUT).getText();
        System.out.println("Requisition Status: " + finalSuccess);
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(requitionGrid);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(createReqBtn);
        return finalSuccess.equalsIgnoreCase(AppConstants.RESOURCE_ALLOCATION_MESSAGE);
    }

}