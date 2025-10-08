package RMT.Pages;

import RMT.Constants.AppConstants;
import RMT.Utils.ElementUtil;
import RMT.Utils.JavascriptUtil;
import RMT.Utils.TimeUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class BudgetPage {
    private WebDriver driver;
    ElementUtil eleutil;

    //1. Skill master page constructor
    public BudgetPage(WebDriver driver){
        this.driver=driver;
        eleutil=new ElementUtil(driver);
    }

    //2. Project Page locators
    private By card1Title= By.xpath("//p[text()='Job Fee']");
    private By card2Title= By.xpath("//p[text()='No. of Resources']");
    private By card3Title= By.xpath("//p[text()='Original Budget']");
    private By card4Title= By.xpath("//p[text()='Revised Budget']");
    private By card5Title= By.xpath("//p[text()='Allocations']");
    private By card6Title= By.xpath("(//p[text()='Actual'])[1]");
    private By card1Data=By.xpath("(//p[@class='MuiTypography-root MuiTypography-body1 css-1p6x6oc'])[1]");
    private By card2Data=By.xpath("(//p[@class='MuiTypography-root MuiTypography-body1 css-1p6x6oc'])[2]");
    private By card3Data=By.xpath("(//p[@class='MuiTypography-root MuiTypography-body1 css-1p6x6oc'])[3]");
    private By card4Data=By.xpath("(//p[@class='MuiTypography-root MuiTypography-body1 css-1p6x6oc'])[4]");
    private By card5Data=By.xpath("(//p[@class='MuiTypography-root MuiTypography-body1 css-ttt3hy'])[1]");
    private By card6Data=By.xpath("(//p[@class='MuiTypography-root MuiTypography-body1 css-ttt3hy'])[2]");
    private By card7Data=By.xpath("(//p[@class='MuiTypography-root MuiTypography-body1 css-ttt3hy'])[2]");
    private By inBudgetValue= By.xpath("//*[local-name()='svg' and contains(@class,'speedometer')]//*[local-name()='text' and @class='current-value']");
    private By budgetCost=By.xpath("//div[@col-id='budgetCost']");
    private By orignalBudgetCost=By.xpath("//div[@col-id='originalBudgetCost']");
    private By grades=By.xpath("//div[@col-id='grade']");
    private By chart3Data=By.xpath("(//*[local-name()='svg' and contains(@class,'recharts-surface')]//*[local-name()='path' and @name='Allocation' and @fill='#00A7B5'])[5]");
    private By clickTimeDropdown=By.xpath("//div[@role='combobox']");
    private By selectMonthly=By.xpath("//li[text()='Monthly']");
    private By selectQuarterly=By.xpath("//li[text()='Quarterly']");
    private By chart3DataMonthlyFilter=By.xpath("(//*[local-name()='svg' and contains(@class,'recharts-surface')]//*[local-name()='path' and @name='Allocation' and @fill='#00A7B5'])[2]");
    private By chart3DataQuarterlyFilter=By.xpath("(//*[local-name()='svg' and contains(@class,'recharts-surface')]//*[local-name()='path' and @name='Allocation' and @fill='#00A7B5'])[1]");
    private By chart4Resource1Data=By.xpath("//*[local-name()='svg' and contains(@class,'recharts-surface')]//*[local-name()='path' and @class='recharts-rectangle' and @x='110' and @y='166' and @fill='#00A7B5']");
    private By chart4FilterBtn=By.xpath("(//button[@type='button'])[20]");
    private By locationFIlter=By.xpath("(//input[@type='text'])[5]");
    private By gradeFIlter=By.xpath("(//input[@type='text'])[6]");
    private By designationFIlter=By.xpath("(//input[@type='text'])[7]");
    private By buFIlter=By.xpath("(//input[@type='text'])[8]");
    private By competencyFIlter=By.xpath("(//input[@type='text'])[9]");
    private By applyFilter=By.xpath("(//button[text()='Apply Filters'])");
    private By chart4OfferingData=By.xpath("//*[local-name()='svg' and contains(@class,'recharts-surface')]//*[local-name()='path' and @class='recharts-rectangle' and @x='110' and @y='257' and @fill='#00A7B5']");
    private By chart4LocationBasesData=By.xpath("//*[local-name()='svg' and contains(@class,'recharts-surface')]//*[local-name()='path' and @class='recharts-rectangle' and @x='110' and @y='166' and @fill='#00A7B5']");
    private By selectDel=By.xpath("//li[text()='DEL']");
    private By selectGur=By.xpath("//li[text()='GUR']");
    private By selectGrade=By.xpath("//li[text()='C2 (S)']");
    private By selectConsultantDesignation=By.xpath("//li[text()='Consultant']");
    private By selectDirectorDesignation=By.xpath("//li[text()='Director']");
    private By selectBuDeals=By.xpath("//li[text()='BU Deals']");
    private By selectCompetency=By.xpath("//li[text()='Competency']");
    private By selectConsultant=By.xpath("//li[text()='Consultant']");
    private By selectDirector=By.xpath("//li[text()='Director']");
    private By selectDelDeals=By.xpath("//li[text()='DEL Deals']");
    private By selectGurDeals=By.xpath("//li[text()='GUR Deals']");
    private By selectGradeDeals=By.xpath("//li[text()='C2 (S) Deals']");
    private By selectConsultantDesignationDeals=By.xpath("//li[text()='Consultant Deals']");
    private By selectDirectorDesignationDeals=By.xpath("//li[text()='Director Deals']");
    private By selectBuDealsDeals=By.xpath("//li[text()='BU Deals']");
    private By selectCompetencyDeals=By.xpath("//li[text()='Due Diligence']");
    private By chart4Data=By.xpath("//*[local-name()='svg' and contains(@class,'recharts-surface')]//*[local-name()='path' and @class='recharts-rectangle' and @x='110' and @y='74' and @fill='#00A7B5']");
    //3.Page actions

    /**
     * This method is used for capturing the title of the Job Budget  page and returning it
     * @return
     * @throws InterruptedException
     */
    public String getbudgetPageTitle() {
        String title=eleutil.waitForTitleToBe(AppConstants.SKILL_MASTER_PAGE_TITLE, TimeUtil.MEDIUM_TIME_OUT);
        System.out.println("Budget  Page  title is "+ title);
        return title;
    }
    /**
     * This method captures the title of card 1 on the Job Budget page.
     * It waits for 2 seconds, adjusts the browser zoom level to 67%,
     * and retrieves the text content of the card 1 title element.
     *
     * @return the text of card 1 title as a String
     */
    public String checkcard1Title(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        JavascriptUtil jsUtil = new JavascriptUtil(driver);
        jsUtil.zoomFirefoxChromeEdgeSafari("67");
        String title1=eleutil.doGetText(card1Title);
        System.out.println("Card 1 Title is " + title1);
        return title1;
    }
    /**
     * Retrieves the title text of the second card element on the BudgetPage.
     *
     * @return A string representing the title of the second card element.
     */
    public String checkcard2Title(){
        JavascriptUtil jsUtil = new JavascriptUtil(driver);
        String title1=eleutil.doGetText(card2Title);
        System.out.println("Card 1 Title is " + title1);
        return title1;
    }
    /**
     * This method retrieves and returns the text of Card 3's title on the Budget Page.
     * It uses a utility method to fetch the text from the specified element locator.
     *
     * @return the title of Card 3 as a String
     */
    public String checkcard3Title(){
        JavascriptUtil jsUtil = new JavascriptUtil(driver);
        String title1=eleutil.doGetText(card3Title);
        System.out.println("Card 1 Title is " + title1);
        return title1;
    }
    /**
     * This method retrieves the title text of card 4 on the Budget Page.
     *
     * @return The title string of card 4.
     */
    public String checkcard4Title(){
        JavascriptUtil jsUtil = new JavascriptUtil(driver);
        String title1=eleutil.doGetText(card4Title);
        System.out.println("Card 1 Title is " + title1);
        return title1;
    }
    /**
     * Retrieves the text of the fifth card's title from the Job Budget page.
     *
     * @return The text of the fifth card's title.
     */
    public String checkcard5Title(){
        JavascriptUtil jsUtil = new JavascriptUtil(driver);
        String title1=eleutil.doGetText(card5Title);
        System.out.println("Card 1 Title is " + title1);
        return title1;
    }
    /**
     * Retrieves and returns the title of card 6 from the BudgetPage.
     * This method extracts the text of the specified card title element.
     *
     * @return the title text of card 6 as a String
     */
    public String checkcard6Title(){
        JavascriptUtil jsUtil = new JavascriptUtil(driver);
        String title1=eleutil.doGetText(card6Title);
        System.out.println("Card 1 Title is " + title1);
        return title1;
    }
    /**
     * This method is responsible for verifying the data displayed in the card1Data element.
     * It retrieves the text content, trims any surrounding whitespace, and processes the value
     * to ensure it meets the following conditions:
     * - If the value is empty or exactly "0", the method returns false.
     * - If the value can be parsed as a number and is greater than 0, the method returns true.
     * - If the value is not empty, not "0", and cannot be parsed as a number, the method returns true.
     *
     * @return true if the value meets the above conditions, false otherwise
     */
    public boolean checkCard1Data() {
        // Retrieve text from the card1Data element
        String text = eleutil.doGetText(card1Data).trim();
        System.out.println("Card 1 Data text is: " + text);

        // If empty or exactly “0”, return false
        if (text.isEmpty() || "0".equals(text)) {
            return false;
        }

        // Otherwise, attempt to parse as a number and check > 0
        try {
            double value = Double.parseDouble(text);
            if (value > 0) {
                return true;
            } else {
                System.out.println("Card 1 Data is not greater than 0: " + value);
                return false;
            }
        } catch (NumberFormatException e) {
            // If it isn’t a valid number but not empty/“0”, we still consider it “true”
            return true;
        }

    }
    /**
     * This method checks the value of the card2Data element and determines its validity.
     * It retrieves the text associated with card2Data, trims any whitespace, and performs
     * validation based on the following rules:
     * 1. If the text is empty or exactly "0", it returns false.
     * 2. If the text can be parsed into a valid number and is greater than 0, it returns true.
     * 3. If the text cannot be parsed as a number but is not empty or "0", it also returns true.
     *
     * @return a boolean indicating whether the card2Data value satisfies the validation criteria.
     */
    public boolean checkCard2Data() {
        // Retrieve text from the card1Data element
        String text = eleutil.doGetText(card2Data).trim();
        System.out.println("Card 1 Data text is: " + text);

        // If empty or exactly “0”, return false
        if (text.isEmpty() || "0".equals(text)) {
            return false;
        }

        // Otherwise, attempt to parse as a number and check > 0
        try {
            double value = Double.parseDouble(text);
            if (value > 0) {
                return true;
            } else {
                System.out.println("Card 1 Data is not greater than 0: " + value);
                return false;
            }
        } catch (NumberFormatException e) {
            // If it isn’t a valid number but not empty/“0”, we still consider it “true”
            return true;
        }

    }
    /**
     * This method is used to validate the data from the element `card3Data`.
     * It retrieves the text content from the element and performs the following checks:
     * - If the text is empty or equals "0", it returns false.
     * - If the text can be parsed as a number greater than 0, it returns true.
     * - If the text cannot be parsed as a valid number but is neither empty nor "0", it also returns true.
     *
     * @return true if the data in `card3Data` meets the validation criteria, otherwise false.
     */
    public boolean checkCard3Data() {
        // Retrieve text from the card1Data element
        String text = eleutil.doGetText(card3Data).trim();
        System.out.println("Card 1 Data text is: " + text);

        // If empty or exactly “0”, return false
        if (text.isEmpty() || "0".equals(text)) {
            return false;
        }

        // Otherwise, attempt to parse as a number and check > 0
        try {
            double value = Double.parseDouble(text);
            if (value > 0) {
                return true;
            } else {
                System.out.println("Card 1 Data is not greater than 0: " + value);
                return false;
            }
        } catch (NumberFormatException e) {
            // If it isn’t a valid number but not empty/“0”, we still consider it “true”
            return true;
        }

    }

    /**
     * This method is used to validate the data of card4. It retrieves the text content
     * of the element associated with card4Data and applies multiple checks:
     * - If the text is empty or equals "0", the method returns false.
     * - If the text can be parsed into a number and is greater than 0, it returns true.
     * - If the text cannot be parsed but is neither empty nor "0", it also returns true.
     *
     * @return true if the card4Data contains a valid non-zero value, false otherwise
     */
    public boolean checkCard4Data() {
        // Retrieve text from the card1Data element
        String text = eleutil.doGetText(card4Data).trim();
        System.out.println("Card 1 Data text is: " + text);

        // If empty or exactly “0”, return false
        if (text.isEmpty() || "0".equals(text)) {
            return false;
        }

        // Otherwise, attempt to parse as a number and check > 0
        try {
            double value = Double.parseDouble(text);
            if (value > 0) {
                return true;
            } else {
                System.out.println("Card 1 Data is not greater than 0: " + value);
                return false;
            }
        } catch (NumberFormatException e) {
            // If it isn’t a valid number but not empty/“0”, we still consider it “true”
            return true;
        }

    }
    /**
     * This method checks the data of card5 and performs validation based on its text content.
     * It retrieves the text from the card5Data field, trims it, and determines if the data is valid.
     * If the text is empty or "0", it returns false.
     * Otherwise, it attempts to parse the text as a number:
     * - If parsing succeeds and the number is greater than 0, it returns true.
     * - If parsing fails due to a NumberFormatException (invalid number format), it considers the data valid and returns true.
     *
     * @return true if the card5Data text is valid and either a positive number or non-numeric string, false otherwise
     */
    public boolean checkCard5Data() {
        // Retrieve text from the card1Data element
        String text = eleutil.doGetText(card5Data).trim();
        System.out.println("Card 1 Data text is: " + text);

        // If empty or exactly “0”, return false
        if (text.isEmpty() || "0".equals(text)) {
            return false;
        }

        // Otherwise, attempt to parse as a number and check > 0
        try {
            double value = Double.parseDouble(text);
            if (value > 0) {
                return true;
            } else {
                System.out.println("Card 1 Data is not greater than 0: " + value);
                return false;
            }
        } catch (NumberFormatException e) {
            // If it isn’t a valid number but not empty/“0”, we still consider it “true”
            return true;
        }

    }
    /**
     * This method checks the numeric or textual data in the card6Data element and determines its validity.
     * It retrieves the data as a string from the card6Data element, trims the string, and performs the following verifications:
     * - If the text is empty or equals "0", the method returns false.
     * - If the text is a numeric value greater than 0, the method returns true.
     * - If the text is non-numeric but not empty/"0", the method returns true, as it assumes non-empty non-numeric values are valid.
     *
     * @return true if the card6Data element contains a non-empty, valid value (either numeric greater than 0 or non-empty non-numeric).
     *         false if the value is empty or equals "0".
     */
    public boolean checkCard6Data() {
        // Retrieve text from the card1Data element
        String text = eleutil.doGetText(card6Data).trim();
        System.out.println("Card 1 Data text is: " + text);

        // If empty or exactly “0”, return false
        if (text.isEmpty() || "0".equals(text)) {
            return false;
        }

        // Otherwise, attempt to parse as a number and check > 0
        try {
            double value = Double.parseDouble(text);
            if (value > 0) {
                return true;
            } else {
                System.out.println("Card 1 Data is not greater than 0: " + value);
                return false;
            }
        } catch (NumberFormatException e) {
            // If it isn’t a valid number but not empty/“0”, we still consider it “true”
            return true;
        }

    }
    /**
     * Checks the data in Chart 1 by retrieving text from an element, validating its contents,
     * and determining whether it represents a valid number greater than zero.
     *
     * @return true if the data in Chart 1 is a valid non-empty value or a number greater than zero; false otherwise
     */
    public boolean checkChart1Data() {
        // Retrieve text from the card1Data element
        String text = eleutil.doGetText(inBudgetValue).trim();
        System.out.println("Card 1 Data text is: " + text);

        // If empty or exactly “0”, return false
        if (text.isEmpty() || "0".equals(text)) {
            return false;
        }

        // Otherwise, attempt to parse as a number and check > 0
        try {
            double value = Double.parseDouble(text);
            if (value > 0) {
                return true;
            } else {
                System.out.println("Card 1 Data is not greater than 0: " + value);
                return false;
            }
        } catch (NumberFormatException e) {
            // If it isn’t a valid number but not empty/“0”, we still consider it “true”
            return true;
        }

    }

    /**
     * This method checks the validity of grades data by processing the list of grade texts.
     * It verifies that there are no invalid or empty grade codes based on pre-defined criteria.
     *
     * @return true if all grade texts are valid, false if any invalid or empty grades are found
     */
    public boolean checkGradesData() {
        // Fetch all grade texts
        List<String> gradeTexts = eleutil.getElementsTextList(grades);

        // If there are no grade elements at all, return false
        if (gradeTexts.isEmpty()) {
            return false;
        }

        // Define the set of invalid grade codes
        Set<String> invalidGrades = Set.of(
                "GR00001", "GR00002", "GR00003",
                "GR00004", "GR00005", "GR00006",
                "GR00007", "GR00008", "GR00009"
        );

        // Iterate through each fetched text and validate
        for (String rawText : gradeTexts) {
            String text = rawText.trim();

            // If the text is empty or is one of the disallowed codes, return false
            if (text.isEmpty() || invalidGrades.contains(text)) {
                System.out.println("Invalid grade found: \"" + text + "\"");
                return false;
            }
        }

        // All grade texts passed validation
        return true;
    }

    /**
     * Validates budget-related grade data retrieved from the grid.
     *
     * This method checks if the grid contains valid grade data. It fetches all grade
     * elements from the grid and verifies whether they meet the expected criteria.
     * The method returns false in the following cases:
     * 1. No grade elements are found.
     * 2. A grade code is empty.
     * 3. A grade code matches any of the disallowed codes in the predefined set.
     * If all grade data is valid, it returns true.
     *
     * @return true if all grade data are valid; false if any grade data is invalid or missing.
     */
    public boolean checkGridBudgetData() {
        // Fetch all grade texts
        List<String> gradeTexts = eleutil.getElementsTextList(budgetCost);

        // If there are no grade elements at all, return false
        if (gradeTexts.isEmpty()) {
            return false;
        }

        // Define the set of invalid grade codes
        Set<String> invalidGrades = Set.of(
                "GR00001", "GR00002", "GR00003",
                "GR00004", "GR00005", "GR00006",
                "GR00007", "GR00008", "GR00009"
        );

        // Iterate through each fetched text and validate
        for (String rawText : gradeTexts) {
            String text = rawText.trim();

            // If the text is empty or is one of the disallowed codes, return false
            if (text.isEmpty() || invalidGrades.contains(text)) {
                System.out.println("Invalid grade found: \"" + text + "\"");
                return false;
            }
        }

        // All grade texts passed validation
        return true;
    }
    /**
     * This method validates the textual data fetched from a grid against a predefined set of invalid values
     * and ensures no empty or disallowed values exist in the data.
     *
     * The method retrieves text values from web elements, trims them, and checks if they are empty or belong
     * to a set of invalid grade codes. If any disallowed condition is met, it logs the invalid value and
     * returns false. If all checks pass, it returns true.
     *
     * @return true if all grid data is valid and not in the disallowed list; false otherwise.
     */
    public boolean checkGridOrignalData() {
        // Fetch all grade texts
        List<String> gradeTexts = eleutil.getElementsTextList(orignalBudgetCost);

        // If there are no grade elements at all, return false
        if (gradeTexts.isEmpty()) {
            return false;
        }

        // Define the set of invalid grade codes
        Set<String> invalidGrades = Set.of(
                "GR00001", "GR00002", "GR00003",
                "GR00004", "GR00005", "GR00006",
                "GR00007", "GR00008", "GR00009"
        );

        // Iterate through each fetched text and validate
        for (String rawText : gradeTexts) {
            String text = rawText.trim();

            // If the text is empty or is one of the disallowed codes, return false
            if (text.isEmpty() || invalidGrades.contains(text)) {
                System.out.println("Invalid grade found: \"" + text + "\"");
                return false;
            }
        }

        // All grade texts passed validation
        return true;
    }

    /**
     * This method checks whether the "Allocation/Actual" data for Chart 3 is displayed on the page.
     * It attempts to locate and verify the presence of the associated element.
     * If successful, it returns true, otherwise returns false.
     *
     * @return true if the "Allocation/Actual" data for Chart 3 is displayed, false otherwise
     */
    public boolean checkChart3Data() {
        try {
            boolean isPresent = eleutil.doIsDisplayed(chart3Data);
            System.out.println("Allocation/Actual data  is displayed: " + isPresent);
            return isPresent;
        } catch (InterruptedException | NoSuchElementException e) {
            System.out.println("Allocation/Actual data element not present or not visible.");
            return false;
        }

    }
    /**
     * Checks the Chart 3 monthly-filtered data by interacting with dropdowns
     * and ensuring relevant elements are visible and clickable on the page.
     *
     * The method performs the following steps:
     * 1) Scrolls the “Time” dropdown into view and clicks it using an action.
     * 2) Awaits until the "Monthly" option becomes visible and clicks it.
     * 3) Verifies if the Chart 3 data filtered by "Monthly" is displayed.
     *
     * The method handles `InterruptedException`, `TimeoutException`,
     * and `NoSuchElementException` to ensure reliability while waiting
     * for elements on the page.
     *
     * @return true if the Chart 3 data filtered by "Monthly" is displayed, false otherwise
     */
    public boolean checkChart3MonthlyData() {
        JavascriptUtil jsutil = new JavascriptUtil(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Actions act = new Actions(driver);

        try {
            // 1) Scroll the “Time” dropdown into view
            By timeDropdownLocator = clickTimeDropdown;
            WebElement dropdown = wait.until(
                    ExpectedConditions.presenceOfElementLocated(timeDropdownLocator)
            );
            jsutil.scrollIntoView(dropdown);
            Thread.sleep(300);

            // 2) Wait until clickable, then click via Actions
            dropdown = wait.until(
                    ExpectedConditions.elementToBeClickable(timeDropdownLocator)
            );
            act.moveToElement(dropdown).click().perform();
            Thread.sleep(500);

            // 3) Wait for the “Monthly” <li> option to appear in the DOM
            By monthlyOptionLocator = selectMonthly;
            WebElement monthlyOption = wait.until(
                    ExpectedConditions.presenceOfElementLocated(monthlyOptionLocator)
            );

            // 4) Scroll the “Monthly” option into view, then click via Actions
            jsutil.scrollIntoView(monthlyOption);
            Thread.sleep(300);
            act.moveToElement(monthlyOption).click().perform();
            Thread.sleep(500);

            // 5) Verify that the Chart 3 monthly‐filtered data is displayed
            boolean isPresent = eleutil.doIsDisplayed(chart3DataMonthlyFilter);
            System.out.println("Allocation/Actual data is displayed: " + isPresent);
            return isPresent;
        }
        catch (InterruptedException ie) {
            throw new RuntimeException("Interrupted while waiting", ie);
        }
        catch (TimeoutException te) {
            System.out.println("❌ Timeout waiting for element: " + te.getMessage());
            return false;
        }
        catch (NoSuchElementException nsee) {
            System.out.println("❌ Element not present/visible: " + nsee.getMessage());
            return false;
        }

    }

    /**
     * Checks and verifies the quarterly-filtered data for Chart 3 by performing various UI interactions such as
     * scrolling into view, clicking dropdowns, and ensuring the required elements are present and displayed.
     *
     * The method involves:
     * 1) Scrolling the "Time" dropdown into view.
     * 2) Clicking on the "Time" dropdown once it becomes clickable.
     * 3) Waiting for the "Quarterly" option in the dropdown to appear.
     * 4) Clicking on the "Quarterly" option to apply the filter.
     * 5) Verifying that the Chart 3 data filtered by "Quarterly" is displayed.
     *
     * @return true if the Chart 3 quarterly-filtered data is displayed, false otherwise
     */
    public boolean checkChart3QuaterlyData() {
        JavascriptUtil jsutil = new JavascriptUtil(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Actions act = new Actions(driver);

        try {
            // 1) Scroll the “Time” dropdown into view
            By timeDropdownLocator = clickTimeDropdown;
            WebElement dropdown = wait.until(
                    ExpectedConditions.presenceOfElementLocated(timeDropdownLocator)
            );
            jsutil.scrollIntoView(dropdown);
            Thread.sleep(300);

            // 2) Wait until clickable, then click via Actions
            dropdown = wait.until(
                    ExpectedConditions.elementToBeClickable(timeDropdownLocator)
            );
            act.moveToElement(dropdown).click().perform();
            Thread.sleep(500);

            // 3) Wait for the “Monthly” <li> option to appear in the DOM
            By monthlyOptionLocator = selectQuarterly;
            WebElement monthlyOption = wait.until(
                    ExpectedConditions.presenceOfElementLocated(monthlyOptionLocator)
            );

            // 4) Scroll the “Monthly” option into view, then click via Actions
            jsutil.scrollIntoView(monthlyOption);
            Thread.sleep(300);
            act.moveToElement(monthlyOption).click().perform();
            Thread.sleep(500);

            // 5) Verify that the Chart 3 monthly‐filtered data is displayed
            boolean isPresent = eleutil.doIsDisplayed(chart3DataQuarterlyFilter);
            System.out.println("Allocation/Actual data is displayed: " + isPresent);
            return isPresent;
        }
        catch (InterruptedException ie) {
            throw new RuntimeException("Interrupted while waiting", ie);
        }
        catch (TimeoutException te) {
            System.out.println("❌ Timeout waiting for element: " + te.getMessage());
            return false;
        }
        catch (NoSuchElementException nsee) {
            System.out.println("❌ Element not present/visible: " + nsee.getMessage());
            return false;
        }

    }

    /**
     * This method checks if the Allocation/Actual data is displayed in Chart 4.
     * Utilizes the ElementUtil helper to verify the visibility of the specific element.
     * Logs and returns the display status of the data.
     * Handles exceptions for scenarios where the element is not present or not visible.
     *
     * @return true if the Allocation/Actual data is displayed; false otherwise
     */
    public boolean checkChart4Data() {
        try {
            boolean isPresent = eleutil.doIsDisplayed(chart4Data);
            System.out.println("Allocation/Actual data  is displayed: " + isPresent);
            return isPresent;
        } catch (InterruptedException | NoSuchElementException e) {
            System.out.println("Allocation/Actual data element not present or not visible.");
            return false;
        }

    }
    /**
     * This method is used to apply a location filter on Chart 4 and check whether
     * the filtered data for "Resource Wise Allocation/Actual data" is displayed.
     *
     * The method interacts with UI components like selection filters and applies
     * the required filters using WebDriver and Selenium Actions, waiting for the
     * filtered data to be rendered and checking its visibility.
     *
     * @return true if the "Resource Wise Allocation/Actual data" is displayed after
     *         applying the location filter; false if the data is not displayed or
     *         if an exception occurs.
     */
    public boolean checkChart4FilterLocation1Data() {
        Actions act = new Actions(driver);
        JavascriptUtil jsutil = new JavascriptUtil(driver);
        jsutil.clickElementByJS(eleutil.getElement(chart4FilterBtn));
        //eleutil.doActionsClick(chart4FilterBtn);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(locationFIlter);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(selectDel);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        eleutil.doClick(applyFilter);
        try {
            boolean isPresent = eleutil.doIsDisplayed(chart4LocationBasesData);
            System.out.println("Resource Wise Allocation/Actual data  is displayed: " + isPresent);
            return isPresent;
        } catch (InterruptedException | NoSuchElementException e) {
            System.out.println("Resource Wise Allocation/Actual data element not present or not visible.");
            return false;
        }

    }

    /**
     * This method is used to filter data on "Chart 4" based on a specific location and check if
     * the filtered data is displayed.
     *
     * It performs a sequence of actions, including clicking on filter buttons, selecting a
     * location, applying the filter, and verifying the presence of the filtered chart data.
     *
     * @return true if the filtered data is successfully displayed; false otherwise
     */
    public boolean checkChart4FilterLocation2Data() {
        Actions act = new Actions(driver);
        JavascriptUtil jsutil = new JavascriptUtil(driver);
        jsutil.clickElementByJS(eleutil.getElement(chart4FilterBtn));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(locationFIlter);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(selectGur);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        eleutil.doClick(applyFilter);
        try {
            boolean isPresent = eleutil.doIsDisplayed(chart4LocationBasesData);
            System.out.println("Resource Wise Allocation/Actual data  is displayed: " + isPresent);
            return isPresent;
        } catch (InterruptedException | NoSuchElementException e) {
            System.out.println("Resource Wise Allocation/Actual data element not present or not visible.");
            return false;
        }

    }

    /**
     * This method verifies the Resource Wise Allocation/Actual data displayed
     * in Chart 4 after applying a grade filter.
     * Various actions, including clicking and sending keystrokes,
     * are performed on the web elements to apply the grade filter.
     *
     * @return true if the Resource Wise Allocation/Actual data is displayed
     *         after filtering by grade; false if the data is not displayed
     *         or an exception occurs during the process.
     */
    public boolean checkChart4grade1Data() {
        Actions act = new Actions(driver);
        JavascriptUtil jsutil = new JavascriptUtil(driver);
        jsutil.clickElementByJS(eleutil.getElement(chart4FilterBtn));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        act
                .click(driver.findElement(By.xpath("(//input[@type='text'])[5]")))  // Focus on the input field
                .keyDown(Keys.CONTROL)
                .sendKeys("a")                        // Select all text
                .keyUp(Keys.CONTROL)
                .sendKeys(Keys.DELETE)               // Delete selected text
                .perform();
        eleutil.doActionsClick(gradeFIlter);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(selectGrade);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        eleutil.doClick(applyFilter);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            boolean isPresent = eleutil.doIsDisplayed(chart4Resource1Data);
            System.out.println("Resource Wise Allocation/Actual data  is displayed: " + isPresent);
            return isPresent;
        } catch (InterruptedException | NoSuchElementException e) {
            System.out.println("Resource Wise Allocation/Actual data element not present or not visible.");
            return false;
        }

    }
    /**
     * This method checks the data in "Resource Wise Allocation/Actual" chart for a specific designation filter.
     * It applies the filter and verifies if the data is displayed after filtering.
     * @return true if the filtered "Resource Wise Allocation/Actual" data is displayed, false otherwise
     */
    public boolean checkChart4Designation1Data() {
        Actions act = new Actions(driver);
        JavascriptUtil jsutil = new JavascriptUtil(driver);
        jsutil.clickElementByJS(eleutil.getElement(chart4FilterBtn));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        act
                .click(driver.findElement(By.xpath("(//input[@type='text'])[5]")))  // Focus on the input field
                .keyDown(Keys.CONTROL)
                .sendKeys("a")                        // Select all text
                .keyUp(Keys.CONTROL)
                .sendKeys(Keys.DELETE)               // Delete selected text
                .perform();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        act
                .click(driver.findElement(By.xpath("(//input[@type='text'])[6]")))  // Focus on the input field
                .keyDown(Keys.CONTROL)
                .sendKeys("a")                        // Select all text
                .keyUp(Keys.CONTROL)
                .sendKeys(Keys.DELETE)               // Delete selected text
                .perform();
        eleutil.doActionsClick(designationFIlter);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(selectConsultant);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        eleutil.doClick(applyFilter);
        try {
            boolean isPresent = eleutil.doIsDisplayed(chart4Resource1Data);
            System.out.println("Resource Wise Allocation/Actual data  is displayed: " + isPresent);
            return isPresent;
        } catch (InterruptedException | NoSuchElementException e) {
            System.out.println("Resource Wise Allocation/Actual data element not present or not visible.");
            return false;
        }

    }

    /**
     * This method is used to verify and interact with specific input fields,
     * apply a filter for Business Unit deals, and check if the Resource Wise
     * Allocation/Actual data chart is displayed.
     *
     * It performs actions such as clearing input fields, selecting options from
     * dropdowns, and clicking buttons to apply a filter. After applying the filter,
     * it checks if the chart data is displayed on the page.
     *
     * @return true if the Resource Wise Allocation/Actual data chart is displayed;
     *         false if the chart is not present or visible.
     */
    public boolean checkChart4Bu1Data() {
        Actions act = new Actions(driver);
        JavascriptUtil jsutil = new JavascriptUtil(driver);
        jsutil.clickElementByJS(eleutil.getElement(chart4FilterBtn));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        act
                .click(driver.findElement(By.xpath("(//input[@type='text'])[5]")))  // Focus on the input field
                .keyDown(Keys.CONTROL)
                .sendKeys("a")                        // Select all text
                .keyUp(Keys.CONTROL)
                .sendKeys(Keys.DELETE)               // Delete selected text
                .perform();
        act
                .click(driver.findElement(By.xpath("(//input[@type='text'])[6]")))  // Focus on the input field
                .keyDown(Keys.CONTROL)
                .sendKeys("a")                        // Select all text
                .keyUp(Keys.CONTROL)
                .sendKeys(Keys.DELETE)               // Delete selected text
                .perform();
        act
                .click(driver.findElement(By.xpath("(//input[@type='text'])[7]")))  // Focus on the input field
                .keyDown(Keys.CONTROL)
                .sendKeys("a")                        // Select all text
                .keyUp(Keys.CONTROL)
                .sendKeys(Keys.DELETE)               // Delete selected text
                .perform();
        eleutil.doActionsClick(buFIlter);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(selectBuDeals);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        eleutil.doClick(applyFilter);
        try {
            boolean isPresent = eleutil.doIsDisplayed(chart4OfferingData);
            System.out.println("Resource Wise Allocation/Actual data  is displayed: " + isPresent);
            return isPresent;
        } catch (InterruptedException | NoSuchElementException e) {
            System.out.println("Resource Wise Allocation/Actual data element not present or not visible.");
            return false;
        }

    }

    /**
     * This method performs actions to apply certain filters and validate the presence of data
     * related to resource-wise Allocation/Actual within Chart 4 for a specified competency.
     * It interacts with multiple input elements, clearing their contents, applying filter criteria,
     * and checking for data visibility on the page.
     *
     * @return true if the "Resource Wise Allocation/Actual" data is displayed after applying filters;
     *         false if the data is not displayed or an exception occurs during the process
     */
    public boolean checkChart4Competency1Data() {
        Actions act = new Actions(driver);
        JavascriptUtil jsutil = new JavascriptUtil(driver);
        jsutil.clickElementByJS(eleutil.getElement(chart4FilterBtn));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        act
                .click(driver.findElement(By.xpath("(//input[@type='text'])[5]")))  // Focus on the input field
                .keyDown(Keys.CONTROL)
                .sendKeys("a")                        // Select all text
                .keyUp(Keys.CONTROL)
                .sendKeys(Keys.DELETE)               // Delete selected text
                .perform();
        act
                .click(driver.findElement(By.xpath("(//input[@type='text'])[6]")))  // Focus on the input field
                .keyDown(Keys.CONTROL)
                .sendKeys("a")                        // Select all text
                .keyUp(Keys.CONTROL)
                .sendKeys(Keys.DELETE)               // Delete selected text
                .perform();
        act
                .click(driver.findElement(By.xpath("(//input[@type='text'])[7]")))  // Focus on the input field
                .keyDown(Keys.CONTROL)
                .sendKeys("a")                        // Select all text
                .keyUp(Keys.CONTROL)
                .sendKeys(Keys.DELETE)               // Delete selected text
                .perform();
        act
                .click(driver.findElement(By.xpath("(//input[@type='text'])[8]")))  // Focus on the input field
                .keyDown(Keys.CONTROL)
                .sendKeys("a")                        // Select all text
                .keyUp(Keys.CONTROL)
                .sendKeys(Keys.DELETE)               // Delete selected text
                .perform();
        eleutil.doActionsClick(competencyFIlter);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.getElement(selectCompetencyDeals);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
        eleutil.doClick(applyFilter);
        try {
            boolean isPresent = eleutil.doIsDisplayed(chart4OfferingData);
            System.out.println("Resource Wise Allocation/Actual data  is displayed: " + isPresent);
            return isPresent;
        } catch (InterruptedException | NoSuchElementException e) {
            System.out.println("Resource Wise Allocation/Actual data element not present or not visible.");
            return false;
        }

    }

}
