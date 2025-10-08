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

public class ReportsPage {
    private WebDriver driver;
    ElementUtil eleutil;

    //1. Skill master page constructor
    public ReportsPage(WebDriver driver){
        this.driver=driver;
        eleutil=new ElementUtil(driver);
    }

    //2.Page locators
    private By card1Data= By.xpath("(//p[@class='MuiTypography-root MuiTypography-body1 css-pac55i'])[1]");
    private By card2Data= By.xpath("(//p[@class='MuiTypography-root MuiTypography-body1 css-pac55i'])[2]");
    private By card3Data= By.xpath("(//p[@class='MuiTypography-root MuiTypography-body1 css-pac55i'])[3]");
    private By card4Data= By.xpath("(//p[@class='MuiTypography-root MuiTypography-body1 css-pac55i'])[4]");
    private By card5Data =By.xpath(" (//*[local-name()='svg' and contains(@class, 'speedometer')]//*[local-name()='text' and contains(@class, 'current-value')])[1]");
    private By card6Data=By.xpath(" //*[local-name()='svg' and contains(@class, 'recharts-surface')]//*[local-name()='text' and text()='7.2%' and @text-anchor='middle']");
    private By card7Data =By.xpath(" (//*[local-name()='svg' and contains(@class, 'speedometer')]//*[local-name()='text' and contains(@class, 'current-value')])[2]");
    private By card1Title=By.xpath("//p[text()='Total Project']");
    private By card2Title=By.xpath("//p[text()='Allocation']");
    private By card3Title=By.xpath("//p[text()='Chargeable Allocation']");
    private By card4Title=By.xpath("//p[text()='NC Allocation']");
    private By card5Title=By.xpath("//p[text()='Allocation vs Capacity (hours)']");
    private By card6Title=By.xpath("//p[text()='Allocation Hours']");
    private By card7Title=By.xpath("//p[text()='Actuals vs Capacity (hours)']");
    private By card8Title=By.xpath("//p[text()='Actual Hours']");
    private By startDate=By.xpath("(//input[@inputmode='text'])[1]");
    private By EndDate =By.xpath("(//input[@inputmode='text'])[2]");
    private By clickCapacityBtn=By.xpath("//button[text()='Capacity Utilization Chart']");
    private By clickSchduleBtn=By.xpath("//button[text()='Scheduled Vs Variance Chart']");
    private By capacityCard1Titile =By.xpath("//p[text()='Total Capacity']");
    private By capacityCard2Titile=By.xpath("//p[text()='Total Allocation']");
    private By capacityCard3Titile=By.xpath("//p[text()='Total Availability']");
    private By capacityCard4Titile=By.xpath("//p[text()='Total Actual']");
    private By capacityCard1Data=By.xpath("(//p[@class='MuiTypography-root MuiTypography-body1 css-pac55i'])[1]");
    private By capacityeCard2Data=By.xpath("(//p[@class='MuiTypography-root MuiTypography-body1 css-pac55i'])[2]");
    private By capacityCard3Data=By.xpath("(//p[@class='MuiTypography-root MuiTypography-body1 css-pac55i'])[3]");
    private By capacityCard4Data=By.xpath("(//p[@class='MuiTypography-root MuiTypography-body1 css-pac55i'])[4]");
    private By capacityChartData=By.xpath("//*[name()='text' and normalize-space()='Deals']");
    private By exportBtn=By.xpath("//div[@class='download-icon']");
    private By dealsBar=By.xpath("(//*[local-name()='svg' and contains(@class,'recharts-surface')] //*[local-name()='path' and @class='recharts-rectangle' and @name='Deals'])[2]");
    private By exportBtn2=By.xpath("(//div[@class='download-icon'])[2]");
    private By clickBuFilter=By.xpath("(//span[@ref='eMenu'])[22]");
    private By inputBu=By.xpath("(//input[@placeholder='Filter...'])[1]");
    private By clickOnTitle=By.xpath("//span[text()='CAPACITY UTILIZATION']");
    private By clickCloseIcon=By.xpath("//*[local-name()='svg' and contains(@class,'Close-Icon') and @data-testid='CloseIcon']\n");
    private By chartsData=By.xpath("(//*[local-name()='svg' and contains(@class,'recharts-surface')]//*[local-name()='path' and @fill='#00A7B5'])[2]");



    //3.Page actions

    /**
     * This method is used for capturing the title of the Reports page and returning it
     *
     * @return
     * @throws InterruptedException
     */
    public String getReportsPageTitle() {
        String title = eleutil.waitForTitleToBe(AppConstants.REPORTS_PAGE_TITLE, TimeUtil.MEDIUM_TIME_OUT);
        System.out.println("Skill master Page  title is " + title);
        return title;
    }

    /**
     * This method is used for capturing the Reports page URL and returning it
     *
     * @return
     */
    public String getReportsPageUrl() {
        String url = eleutil.waitForURLToBe(AppConstants.REQUISITON_PAGE_URL, TimeUtil.DEFAULT_TIME_OUT);
        System.out.println("Skill master page Url is " + url);
        return url;
    }
    
    
    /**
     * This method retrieves the title of Card 1 by fetching its text content from the web element.
     *
     * @return the title of Card 1 as a string
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
     * This method retrieves the title of Card 2 by fetching its text content from the specified web element.
     *
     * @return the title of Card 2 as a string
     */
    public String checkcard2Title(){
        String title2=eleutil.doGetText(card2Title);
        System.out.println("Card 1 Title is " + title2);
        return title2;
    }

    /**
     * This method retrieves the title of Card 3 by fetching its text content from the web element.
     *
     * @return the title of Card 3 as a string
     */
    public String checkcard3Title(){
        String title3=eleutil.doGetText(card3Title);
        System.out.println("Card 1 Title is " + title3);
        return title3;
    }
    /**
     * This method retrieves the title of Card 4 by fetching its text content from the web element.
     *
     * @return the title of Card 4 as a string
     */
    public String checkcard4Title(){
        String title4=eleutil.doGetText(card4Title);
        System.out.println("Card 1 Title is " + title4);
        return title4;
    }
    /**
     * This method retrieves the title of Card 5 by fetching its text content from the web element.
     *
     * @return the title of Card 5 as a string
     */
    public String checkcard5Title(){
        String title5=eleutil.doGetText(card5Title);
        System.out.println("Card 1 Title is " + title5);
        return title5;
    }
    /**
     * This method retrieves the title of Card 6 by fetching its text content from the web element.
     *
     * @return the title of Card 6 as a string
     */
    public String checkcard6Title(){
        String title6=eleutil.doGetText(card6Title);
        System.out.println("Card 1 Titile is " + title6);
        return title6;
    }
    /**
     * This method retrieves the title of Card 7 by extracting its text content from the associated web element.
     *
     * @return the title of Card 7 as a string
     */
    public String checkcard7Title(){
        String title7=eleutil.doGetText(card7Title);
        System.out.println("Card 1 Titile is " + title7);
        return title7;
    }
    /**
     * This method retrieves the title of Card 8 by fetching its text content from the web element.
     *
     * @return the title of Card 8 as a string
     */
    public String checkcard8Title(){
        String title8=eleutil.doGetText(card8Title);
        System.out.println("Card 1 Titile is " + title8);
        return title8;
    }

    /**
     * Checks the data of Card 1 by retrieving its text content from the web element,
     * determining if the text is a valid number greater than 0. The method trims
     * the retrieved text and evaluates it based on the following logic:
     *
     * - If the text is empty or equal to "0", it returns false.
     * - If the text is a valid number, it checks if the number is greater than 0
     *   and returns true if it is; otherwise, returns false.
     * - If the text is not a valid number but is neither empty nor "0", it considers
     *   the data valid and returns true.
     *
     * @return true if the data of Card 1 is valid (non-empty, "0", or a positive number);
     *         false otherwise
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
     * This method checks the data associated with Card 2 and determines its validity.
     * The method retrieves the text content of the Card 2 element, trims the text,
     * and evaluates it based on the following criteria:
     *
     * - Returns false if the text is empty or precisely "0".
     * - Attempts to parse the text as a double:
     *   - If the parsed value is greater than 0, returns true.
     *   - If the parsed value is less than or equal to 0, returns false.
     * - If the text cannot be parsed as a number but is neither empty nor "0", returns true.
     *
     * @return true if the Card 2 data is valid or non-zero, false otherwise
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
     * Validates the data retrieved from the card3Data web element.
     * It performs the following checks:
     * 1. If the text is empty or exactly "0", the method returns false.
     * 2. If the text can be parsed as a valid number greater than 0, the method returns true.
     * 3. If the text is not empty/"0" but cannot be parsed as a number, the method still considers the data valid
     *    and returns true.
     *
     * @return true if the card3Data element contains a valid non-zero or non-empty value,
     *         false if the value is empty, "0", or represents a number less than or equal to 0.
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
     * This method checks the data for Card 4 by retrieving its text content,
     * validating its value, and determining whether it meets specific conditions.
     *
     * The data is considered valid if:
     * - The text is non-empty.
     * - The text is not equal to "0".
     * - The text can be parsed as a numeric value greater than 0.
     * If the text cannot be parsed as a number but is non-empty and not "0",
     * it is also considered valid.
     *
     * @return true if the data for Card 4 meets the specified criteria, false otherwise
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
     * This method verifies the data of Card 5 by retrieving its text,
     * checking for validity, and determining whether its value is greater than 0.
     * If the retrieved text is empty or equal to "0", it returns false.
     * If the text can be parsed as a number and is greater than 0, it returns true.
     * If the text is non-empty but cannot be parsed as a number, it also returns true.
     *
     * @return true if the data in Card 5 is valid per the conditions, otherwise false
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
     * Checks the data displayed for Card 6 and determines its validity.
     * The method retrieves the text content of Card 6, trims the retrieved value,
     * and applies the following checks in sequence:
     * 1. If the text is empty or exactly "0", the method returns false.
     * 2. If the text is a valid numeric value and greater than 0, the method returns true.
     * 3. If the text cannot be parsed as a number but is neither empty nor "0", the method returns true.
     *
     * @return true if the data for Card 6 is valid (non-empty, non-zero, or a valid positive number);
     *         false otherwise.
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
     * This method checks the data content of Card 7 to determine its validity.
     * It retrieves the text content of Card 7 and performs a series of checks:
     * 1. If the text is empty or the value is "0", it returns false.
     * 2. If the content can be parsed as a number and is greater than 0, it returns true.
     * 3. If the content cannot be parsed as a number but is not empty or "0",
     *    it assumes the content is valid and returns true.
     *
     * @return {@code true} if the content of Card 7 is valid based on the defined conditions;
     *         {@code false} otherwise
     */
    public boolean checkCard7Data() {
        // Retrieve text from the card1Data element
        String text = eleutil.doGetText(card7Data).trim();
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
     * This method checks if the start date is displayed on the page.
     * It uses the element utility method to verify the visibility of the start date element.
     * If the element is not displayed or an exception occurs, it returns true indicating
     * the start date is unavailable. Otherwise, it returns false.
     *
     * @return true if the start date is not displayed, false if it is displayed
     */
    public boolean checkStartDate(){
        try {
            // Attempt to locate the Start Date element
            WebElement startDateElem = eleutil.getElement(startDate);
            if (startDateElem == null) {
                System.out.println("Start Date locator did not match any element.");
                return false;
            }
            // Now safely check visibility
            boolean isPresent = startDateElem.isDisplayed();
            System.out.println("Start Date is displayed: " + isPresent);
            return isPresent;
        }
        catch (NoSuchElementException e) {
            System.out.println("Start Date element not present or not visible.");
            return false;
        }
    }

    /**
     * This method checks whether the 'End Date' element is displayed on the page.
     * If the element is not displayed, it returns true. Otherwise, it returns false.
     *
     * @return true if the 'End Date' element is not displayed, false otherwise
     */
    public boolean checkEndDate(){
        try {
            // Attempt to locate the End Date element
            WebElement endDateElem = eleutil.getElement(EndDate);
            if (endDateElem == null) {
                System.out.println("End Date locator did not match any element.");
                return false;
            }
            // Now safely check visibility
            boolean isPresent = endDateElem.isDisplayed();
            System.out.println("End Date is displayed: " + isPresent);
            return isPresent;
        }
        catch (NoSuchElementException e) {
            System.out.println("End Date element not present or not visible.");
            return false;
        }
    }

    /**
     * Checks and retrieves the title of the capacity card 1.
     * The method performs a click action on the capacity button, waits for a specified time,
     * and then fetches the text of the capacity card 1 title.
     *
     * @return The title of the capacity card 1 as a String.
     */
    public String checkCapacityCard1Title(){
        eleutil.doActionsClick(clickCapacityBtn);
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String capacityCardtitle=eleutil.doGetText(capacityCard1Titile);
        System.out.println("Card 1 Title is " + capacityCardtitle);
        return capacityCardtitle;
    }

    /**
     * Retrieves and returns the title of the capacity card 2 element.
     * Uses the utility method to fetch the text of the specified UI element.
     * Also prints the title value to the console for reference.
     *
     * @return The text value representing the title of capacity card 2.
     */
    public String checkCapacityCard2Title(){
        String capacityCard2title=eleutil.doGetText(capacityCard2Titile);
        System.out.println("Card 2 Title is " + capacityCard2title);
        return capacityCard2title;
    }
    /**
     * Retrieves the title of the third capacity card by extracting text from the specified element.
     * Prints the title to the console.
     *
     * @return the title of the third capacity card as a String
     */
    public String checkCapacityCard3Title(){
        String capacityCard3title=eleutil.doGetText(capacityCard3Titile);
        System.out.println("Card 3 Title is " + capacityCard3title);
        return capacityCard3title;
    }

    /**
     * Retrieves the title of the fourth capacity card and prints it to the console.
     *
     * @return The title of the fourth capacity card as a String.
     */
    public String checkCapacityCard4Title(){
        String capacityCard4title=eleutil.doGetText(capacityCard4Titile);
        System.out.println("Card 4 Title is " + capacityCard4title);
        return capacityCard4title;
    }

    /**
     * Checks the capacity data of card 1 by retrieving text from the specified element,
     * verifying its validity, and determining if it represents a positive number.
     *
     * The method performs the following steps:
     * 1. Extracts the text content of the card1Data element and trims any surrounding whitespace.
     * 2. Returns false if the text is empty or exactly equal to "0".
     * 3. Attempts to parse the text as a numeric value. If the value is successfully parsed
     *    and greater than 0, returns true. Otherwise, returns false if parsed but not greater than 0.
     * 4. If a parsing error occurs, but the text is not empty or "0", considers the text valid and returns true.
     *
     * @return true if the card1Data element's value is non-empty, not "0", or represents a positive number;
     *         false otherwise.
     */
    public boolean checkCapacityCard1Data(){
        // Retrieve text from the card1Data element
        String text = eleutil.doGetText(capacityCard1Data).trim();
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
     * Checks the capacity of Card 2 data by retrieving and validating the text from the associated element.
     * The method performs the following actions:
     * - Retrieves the text from the capacityCard2Data element and trims any leading or trailing spaces.
     * - Determines if the retrieved text is empty or equals "0", returning false in such cases.
     * - Attempts to parse the text as a numeric value and checks if it is greater than 0, returning true if valid.
     * - If the text cannot be parsed as a numeric value but is not empty or "0", it is considered valid and returns true.
     *
     * @return true if the card data is valid (non-empty, not "0", and either a valid number greater than 0
     *         or an invalid number but still considered non-zero);
     *         false if the card data is empty, exactly "0", or a valid number less than or equal to 0.
     */
    public boolean checkCapacityCard2Data(){
        // Retrieve text from the card1Data element
        String text = eleutil.doGetText(capacityeCard2Data).trim();
        System.out.println("Card 2 Data text is: " + text);

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
                System.out.println("Card 2 Data is not greater than 0: " + value);
                return false;
            }
        } catch (NumberFormatException e) {
            // If it isn’t a valid number but not empty/“0”, we still consider it “true”
            return true;
        }
    }

    /**
     * Checks the capacity data from the capacityCard3Data element.
     * This method retrieves text from the specified element, trims it, and evaluates the value.
     * - Returns false if the text is empty or exactly "0".
     * - Returns true if the text can be parsed as a number and is greater than 0.
     * - If the text is not a valid number but not empty or "0", it still considers the result as true.
     *
     * @return boolean true if the capacityCard3Data has valid content or meets the required conditions, false otherwise.
     */
    public boolean checkCapacityCard3Data(){
        // Retrieve text from the card1Data element
        String text = eleutil.doGetText(capacityCard3Data).trim();
        System.out.println("Card 3 Data text is: " + text);

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
                System.out.println("Card 3 Data is not greater than 0: " + value);
                return false;
            }
        } catch (NumberFormatException e) {
            // If it isn’t a valid number but not empty/“0”, we still consider it “true”
            return true;
        }
    }

    /**
     * Checks the capacity data for Card 4 by retrieving and analyzing its text content.
     *
     * The method performs the following checks:
     * - If the text is empty or exactly "0", it returns false.
     * - If the text can be parsed as a number and is greater than 0, it returns true.
     * - If the text is not a valid number but is not empty or "0", it returns true.
     *
     * @return true if the text represents a valid positive number or an invalid string that is neither empty nor exactly "0";
     *         false if the text is empty, "0", or a non-positive number.
     */
    public boolean checkCapacityCard4Data(){
        // Retrieve text from the card1Data element
        String text = eleutil.doGetText(capacityCard4Data).trim();
        System.out.println("Card 4 Data text is: " + text);

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
                System.out.println("Card 4 Data is not greater than 0: " + value);
                return false;
            }
        } catch (NumberFormatException e) {
            // If it isn’t a valid number but not empty/“0”, we still consider it “true”
            return true;
        }
    }

    /**
     * Checks the visibility of the capacity chart data element on the page.
     *
     * This method validates if the capacity chart data element is displayed by utilizing
     * the utility method and handles exceptions if the element is not present or visible.
     *
     * @return true if the capacity chart data element is displayed, false otherwise
     */
    public boolean checkCapacityChart(){
        try {
            boolean isPresent = eleutil.doIsDisplayed(capacityChartData);
            System.out.println("Start Date is displayed: " + isPresent);
            return isPresent;
        } catch (InterruptedException | NoSuchElementException e) {
            System.out.println("Start Date element not present or not visible.");
            return false;
        }
    }

    /**
     * Exports grid data by interacting with the export button on the user interface.
     * The method scrolls the view to the export button, waits for it to become visible,
     * and then performs a click operation. Checks the button's enabled status to
     * determine the success of the operation.
     *
     * @return true if the export button is enabled and the click operation is performed successfully; false otherwise
     */
    public boolean exportGridData(){
        JavascriptUtil jsutil=new JavascriptUtil(driver);
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
     * Exports individual business unit data by performing a series of UI actions including scrolling, clicking, and inputting data.
     * This method interacts with elements on a webpage, performs actions like scrolling into view, clicking filters, entering text, and exporting data.
     *
     * @return true if the export button is enabled and clicked successfully, false otherwise
     */
    public boolean exportIndividualBuData(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Actions act=new Actions(driver);
        JavascriptUtil jsutil=new JavascriptUtil(driver);
        jsutil.scrollIntoView(eleutil.getElement(dealsBar));
        eleutil.doActionsClick(dealsBar);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(clickBuFilter);
        eleutil.doActionsSendKeysWithPause(inputBu,"Operations",5);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        act
                .click(driver.findElement(By.xpath("(//input[@placeholder='Filter...'])[1]")))  // Focus on the input field
                .keyDown(Keys.CONTROL)
                .sendKeys("a")                        // Select all text
                .keyUp(Keys.CONTROL)
                .sendKeys(Keys.DELETE)               // Delete selected text
                .perform();
        eleutil.doActionsClick(clickOnTitle);
        try {
            // 1) Wait until the second download‐icon is present and clickable:
            By exportIcon2Locator = By.xpath("(//div[@class='download-icon'])[2]");
            WebElement exportIcon2 = wait.until(ExpectedConditions.elementToBeClickable(exportIcon2Locator));

            // 2) Scroll it into view:
            jsutil.executeScript("arguments[0].scrollIntoView(true);", exportIcon2);

            // 3) Click it:
            exportIcon2.click();

            // 4) After clicking, verify it’s still enabled:
            return exportIcon2.isEnabled();
        }
        catch (StaleElementReferenceException e) {
            try {
                // Retry once
                By exportIcon2Locator = By.xpath("(//div[@class='download-icon'])[2]");
                WebElement exportIcon2 = wait.until(ExpectedConditions.elementToBeClickable(exportIcon2Locator));
                jsutil.executeScript("arguments[0].scrollIntoView(true);", exportIcon2);
                exportIcon2.click();
                eleutil.doActionsClick(clickCloseIcon);
                Thread.sleep(3000);
                return exportIcon2.isEnabled();
            }
            catch (Exception retryException) {
                System.out.println("❌ Still could not click exportIcon2 after retry: " + retryException.getMessage());
                return false;
            }
        }
        catch (TimeoutException e) {
            System.out.println("❌ exportIcon2 never became clickable: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the schedule chart is visible by performing a click action
     * on the schedule button and verifying the appearance of the chart element.
     *
     * @return true if the schedule chart is visible and enabled, false otherwise
     */
    public boolean checkScheduleChartVisisble(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        eleutil.doActionsClick(clickSchduleBtn);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement btn=eleutil.waitForElementVisible(chartsData,5);
        if (btn.isEnabled()){
            return true;
        }else{
            return false;
        }

    }

    /**
     * Exports the schedule grid data by performing a click action on the export button.
     * The method uses utility functions to scroll into view, wait for visibility,
     * and perform actions on the export button.
     *
     * @return true if the export button is enabled and the action is performed successfully,
     *         false otherwise.
     */
    public boolean exportScheduleGridData(){
        JavascriptUtil jsutil=new JavascriptUtil(driver);
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



}