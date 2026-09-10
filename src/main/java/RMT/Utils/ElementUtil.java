package RMT.Utils;

import RMT.Exceptions.ElementException;
import io.qameta.allure.Step;
//import org.checkerframework.checker.units.qual.C;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ElementUtil {
    private WebDriver driver;

    public ElementUtil(WebDriver driver) {
        this.driver = driver;
    }

    private void logAction(String message) {
        System.out.println("[ElementUtil] " + message);
    }

    private String describeLocator(By locator) {
        return locator == null ? "<null locator>" : locator.toString();
    }

    private boolean isSensitiveLocator(By locator) {
        if (locator == null) {
            return false;
        }
        String normalized = locator.toString().toLowerCase(Locale.ENGLISH);
        return normalized.contains("password")
                || normalized.contains("passwd")
                || normalized.contains("secret")
                || normalized.contains("token");
    }

    private String formatValueForLog(By locator, String value) {
        if (value == null) {
            return "<null>";
        }
        String normalized = value.replaceAll("\\s+", " ").trim();
        if (normalized.isEmpty()) {
            return "<empty>";
        }
        if (isSensitiveLocator(locator)) {
            return "<masked len=" + normalized.length() + ">";
        }
        if (normalized.length() > 80) {
            return normalized.substring(0, 80) + "... (len=" + normalized.length() + ")";
        }
        return normalized;
    }

    private String joinCharSequenceValue(CharSequence... value) {
        if (value == null || value.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (CharSequence seq : value) {
            if (seq != null) {
                builder.append(seq);
            }
        }
        return builder.toString();
    }

    private void nullCheck(String value) {
        if (value == null) {
            throw new ElementException("VALUE IS NULL" + value);
        }
    }

    /**
     *
     * @param locator
     * @param value
     */
    public void doSendKeys(By locator, String value) {
        nullCheck(value);
        logAction("Typing into " + describeLocator(locator) + " value=" + formatValueForLog(locator, value));
        getElement(locator).clear();
        getElement(locator).sendKeys(value);
    }
    @Step("Entering the value using  locator: {0} with value : {1} and waiting for element with timeout : {2}sec ")
    public void doSendKeys(By locator, String value, int timeOut) {
        nullCheck(value);
        logAction("Typing into " + describeLocator(locator) + " value=" + formatValueForLog(locator, value)
                + " timeout=" + timeOut + "s");
        waitForElementVisible(locator, timeOut).clear();
        waitForElementVisible(locator, timeOut).sendKeys(value);
    }

    public void doSendKeys(By locator, CharSequence... value) {
        logAction("Typing into " + describeLocator(locator) + " value=" + formatValueForLog(locator, joinCharSequenceValue(value)));
        getElement(locator).clear();
        getElement(locator).sendKeys(value);
    }

    public WebElement getElement(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element;
        } catch (NoSuchElementException e) {
            System.out.println("Element is not present on the page..." + locator);
            e.printStackTrace();
            return null;
        }
    }

    public void doClick(By locator) {
        logAction("Clicking " + describeLocator(locator));
        getElement(locator).click();
    }
    @Step("Clicking on the element using the locator: {0}")
    public void doClick(By locator, int timeOut) {
        logAction("Clicking " + describeLocator(locator) + " timeout=" + timeOut + "s");
        waitForElementVisible(locator, timeOut).click();
    }

    public String doGetText(By locator) {
        return getElement(locator).getText();
    }

    public String doGetAttribute(By locator, String attrName) {
        return getElement(locator).getAttribute(attrName);
    }

    public boolean doIsDisplayed(By locator) throws InterruptedException {
        Thread.sleep(8000);
        try {
            boolean flag = getElement(locator).isDisplayed();
            System.out.println("element is displayed: " + locator);
            return flag;
        } catch (NoSuchElementException e) {
            System.out.println("element with locator : " + locator + " is not displayed");
            return false;
        }

    }

    public boolean isElementDisplayed(By locator) {
        int elementCount = getElements(locator).size();
        if (elementCount == 1) {
            System.out.println("single element is displayed: " + locator);
            return true;
        } else {
            System.out.println("multiple or zero elements are displayed: " + locator);
            return false;
        }
    }

    public boolean isElementDisplayed(By locator, int expectedElementCount) {
        int elementCount = getElements(locator).size();
        if (elementCount == expectedElementCount) {
            System.out.println("element is displayed: " + locator + " with the occurrence of " + elementCount);
            return true;
        } else {
            System.out.println(
                    "multiple or zero elements are displayed: " + locator + " with the occurrence of " + elementCount);
            return false;
        }
    }
    public boolean isElementVisible(By locator, int timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public List<WebElement> getElements(By locator) {
        return driver.findElements(locator);
    }

    public int getElementsCount(By locator) {
        return getElements(locator).size();
    }

    public List<String> getElementsTextList(By locator) {
        List<WebElement> eleList = getElements(locator);
        List<String> eleTextList = new ArrayList<String>();// pc=0, size=0

        for (WebElement e : eleList) {
            String text = e.getText();
            if (text.length() != 0) {
                eleTextList.add(text);
            }
        }

        return eleTextList;
    }

    public List<String> getElementAttributeList(By locator, String attrName) {
        List<WebElement> imagesList = getElements(locator);
        List<String> attrList = new ArrayList<String>();
        for (WebElement e : imagesList) {
            String attrVal = e.getAttribute(attrName);
            if (attrVal != null && attrVal.length() != 0) {
                attrList.add(attrVal);
                // System.out.println(attrVal);
            }
        }
        return attrList;
    }

    // ********************** Select drop down utils **************//

    public void doSelectByIndex(By locator, int index) {
        Select select = new Select(getElement(locator));
        select.selectByIndex(index);
    }

    public void doSelectByVisbleText(By locator, String visibleText) {
        Select select = new Select(getElement(locator));
        select.selectByVisibleText(visibleText);
    }

    public void doSelectByValue(By locator, String value) {
        Select select = new Select(getElement(locator));
        select.selectByValue(value);
    }

    public int getDropDownOptionsCount(By locator) {
        Select select = new Select(driver.findElement(locator));
        return select.getOptions().size();

    }

    public List<String> getDropDownOptionsTextList(By locator) {
        Select select = new Select(driver.findElement(locator));

        List<WebElement> optionsList = select.getOptions();
        List<String> optionsTextList = new ArrayList<String>();

        for (WebElement e : optionsList) {
            String text = e.getText();
            optionsTextList.add(text);
        }

        return optionsTextList;
    }

    public void selectValueFromDropDown(By locator, String optionText) {
        Select select = new Select(getElement(locator));
        List<WebElement> optionsList = select.getOptions();

        for (WebElement e : optionsList) {
            String text = e.getText();
            System.out.println(text);
            if (text.equals(optionText.trim())) {
                e.click();
                break;
            }
        }

    }

    public void selectValueFromDropDownWithoutSelectClass(By locator, String optionText) {
        List<WebElement> optionsList = getElements(locator);
        for (WebElement e : optionsList) {
            String text = e.getText();
            System.out.println(text);
            if (text.equals(optionText)) {
                e.click();
                break;
            }
        }

    }

    public void doSearch(By searchField, String searchKey, By suggestions, String value) throws InterruptedException {
        doSendKeys(searchField, searchKey);
        Thread.sleep(3000);
        List<WebElement> suggList = getElements(suggestions);
        System.out.println(suggList.size());
        for (WebElement e : suggList) {
            String text = e.getText();
            System.out.println(text);
            if (text.contains(value)) {
                e.click();
                break;
            }
        }
    }

    // *****************Actions utils********************//

    /**
     * This method is used where on hovering on parentLocator the childLocators get visible
     * and user click on childLocator to seletc the same
     * @param parentLocator
     * @param childLocator
     */
    public void handleParentSubMenu(By parentLocator, By childLocator){
        Actions act = new Actions(driver);
        logAction("Hovering " + describeLocator(parentLocator) + " then clicking " + describeLocator(childLocator));
        act.moveToElement(getElement(parentLocator)).perform();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        doClick(childLocator);
    }
    /**
     * This method clicks on a parent menu item (e.g., a dropdown or expandable menu),
     * waits for the corresponding child menu item to become visible and clickable,
     * and then clicks on the child item. If the child item is not found within the wait time,
     * it logs an appropriate message.
     *
     * @param parentLocator By locator for the parent menu element
     * @param childLocator  By locator for the child submenu element
     */
    public void handleParentSubMenuWithClick(By parentLocator, By childLocator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            logAction("Clicking parent menu " + describeLocator(parentLocator));
            // Click on the parent menu item
            WebElement parentElement = wait.until(ExpectedConditions.elementToBeClickable(parentLocator));
            parentElement.click();
            System.out.println("Clicked on parent menu item.");

            logAction("Clicking child menu " + describeLocator(childLocator));
            // Click on the child menu item
            WebElement childElement = wait.until(ExpectedConditions.elementToBeClickable(childLocator));
            childElement.click();
            System.out.println("Clicked on child menu item.");

        } catch (TimeoutException e) {
            System.out.println("Child Locator not found.");
        } catch (Exception e) {
            System.out.println("Unexpected error while handling submenu: " + e.getMessage());
        }
    }

    /**
     * This method is used for handling the dropdown menue for RMS Skill Master category dropdown as " Technical"
     * @param parentLocator
     * @throws InterruptedException
     */
    public void handleDropdownMenue(By parentLocator, String Category) throws InterruptedException {
        Actions act = new Actions(driver);
        logAction("Opening dropdown " + describeLocator(parentLocator) + " and selecting category=" + Category);
        doClick(parentLocator);// Clcking on dropdown
        Thread.sleep(2000);
        act.sendKeys(Category).perform();
        Thread.sleep(1000);
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
    }
    /**
     * This method is used for handling the dropdown menue for RMS Skill Master competency dropdown as "Competency from excel sheet"
     * @param parentLocator
     * @throws InterruptedException
     */
    public void handleCompetencyMenue(By parentLocator, String competency ) {
        Actions act = new Actions(driver);
        logAction("Opening competency dropdown " + describeLocator(parentLocator) + " and selecting competency=" + competency);
        //By competencyChoice = By.xpath("(//li[text()='Business Process Solution'])");
        doClick(parentLocator);// Clicking on competency dropdown
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //doActionsClick(competencyChoice);
        act.sendKeys(competency).perform();
        act.sendKeys(Keys.ARROW_DOWN).perform();
        act.sendKeys(Keys.ENTER).perform();
    }

    /**
     * This method is used for reading the data from "Designation excel" where based on gradeFilter entered by the user
     * the designation "Xpaths" are fetched which are then we are using in designation dropdown to select the respective
     * designation values
     * @param parentLocator
     * @param gradeFilter
     */
    public void handleDesignationMenue1(By parentLocator, String gradeFilter) {
        JavascriptUtil jsUtil= new JavascriptUtil(driver);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List <String> elementList=DesignationUtil.getFilteredData(gradeFilter);// This will be giving us the list of xpaths base on grade
        int totalXPaths = 0; // Counter to track total selected XPaths
        for( String xpath : elementList){
            // Capture the start time for each designation selection
            LocalDateTime startSelection = LocalDateTime.now();
            System.out.println("Start Time for selecting designation: " + startSelection.format(formatter));
        Actions act = new Actions(driver);
        doActionsClick(parentLocator);// This operation will click on designation dropdown
        act.sendKeys("").perform();
        if (gradeFilter.equals(gradeFilter)){
            totalXPaths++; // Increment counter
            System.out.println(" Xpath to be selected are " + xpath);
        }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Explicit wait for the element to be visible before interacting with it
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            jsUtil.scrollIntoView(button);
            // Try normal click
            try {
                button.click();
            } catch (Exception e) {
                // Use JavaScript click if normal click fails
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", button);
            }
            // Capture the end time for this designation selection
            LocalDateTime endSelection = LocalDateTime.now();
            Duration durationSelection = Duration.between(startSelection, endSelection);
            System.out.println("Time taken for selecting designation: " + durationSelection.toMinutes() + " min " + durationSelection.toSecondsPart() + " sec");
        }
        // Print total number of selected XPaths
        System.out.println("Total XPaths selected during this method call: " + totalXPaths);
    }
    public void doDragAndDrop(By sourcelocator, By targetLocator) {
        Actions act = new Actions(driver);
        act.dragAndDrop(getElement(sourcelocator), getElement(targetLocator)).perform();
    }

    public void doActionsSendKeys(By locator, String value) {
        Actions act = new Actions(driver);
        act.sendKeys(getElement(locator), value).perform();
    }

    public void doActionsClick(By locator) {
        Actions act = new Actions(driver);
        logAction("Actions-click on " + describeLocator(locator));
        act.click(getElement(locator)).perform();
    }

    /**
     * This method is used to enter the value in the text field with a pause.
     *
     * @param locator
     * @param value
     * @param pauseTime
     */
    public void doActionsSendKeysWithPause(By locator, String value, long pauseTime) {
        Actions act = new Actions(driver);
        logAction("Actions-typing into " + describeLocator(locator) + " value=" + formatValueForLog(locator, value)
                + " pause=" + pauseTime + "ms");
        char ch[] = value.toCharArray();
        for (char c : ch) {
            act.sendKeys(getElement(locator), String.valueOf(c)).pause(pauseTime).perform();
        }
    }

    /**
     * This method is used to enter the value in the text field with a pause of 500
     * ms (by default).
     *
     * @param locator
     * @param value
     */
    public void doActionsSendKeysWithPause(By locator, String value) {
        Actions act = new Actions(driver);
        logAction("Actions-typing into " + describeLocator(locator) + " value=" + formatValueForLog(locator, value)
                + " pause=500ms");
        char ch[] = value.toCharArray();
        for (char c : ch) {
            act.sendKeys(getElement(locator), String.valueOf(c)).pause(500).perform();
        }
    }

    /**
     * This method is used to enter the random number from 1-8 to enter in effortHrs fields
     * @param locator
     * @param value
     */
    public void doActionsSendNumberWithPause(By locator, String value) {
        Actions act = new Actions(driver);
        Random random = new Random();
        WebElement element = getElement(locator);
        logAction("Actions-typing numeric value into " + describeLocator(locator));
        // Clear the field using CTRL + A + BACKSPACE to ensure full wipe
        act.click(element)
                .keyDown(Keys.CONTROL)
                .sendKeys("a")
                .keyUp(Keys.CONTROL)
                .sendKeys(Keys.BACK_SPACE)
                .perform();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Generate a random digit between 1 and 8
        int randomDigit = 9 + random.nextInt(5);
        logAction("Generated numeric value " + randomDigit + " for " + describeLocator(locator));

        // Send the random digit as a string with pause
        act.sendKeys(getElement(locator), String.valueOf(randomDigit))
                .pause(Duration.ofMillis(500))
                .perform();
    }

    public void level4MenuSubMenuHandlingUsingClick(By level1, String level2, String level3, String level4)
            throws InterruptedException {

        doClick(level1);
        Thread.sleep(1000);

        Actions act = new Actions(driver);
        act.moveToElement(getElement(By.linkText(level2))).perform();
        Thread.sleep(1000);
        act.moveToElement(getElement(By.linkText(level3))).perform();
        Thread.sleep(1000);
        doClick(By.linkText(level4));
    }

    public void level4MenuSubMenuHandlingUsingClick(By level1, By level2, By level3, By level4)
            throws InterruptedException {

        doClick(level1);
        Thread.sleep(1000);

        Actions act = new Actions(driver);
        act.moveToElement(getElement(level2)).perform();
        Thread.sleep(1000);
        act.moveToElement(getElement(level3)).perform();
        Thread.sleep(1000);
        doClick(level4);

    }

    public void level4MenuSubMenuHandlingUsingMouseHover(By level1, By level2, By level3, By level4)
            throws InterruptedException {

        Actions act = new Actions(driver);

        act.moveToElement(getElement(level1)).perform();
        Thread.sleep(1000);

        act.moveToElement(getElement(level2)).perform();
        Thread.sleep(1000);
        act.moveToElement(getElement(level3)).perform();
        Thread.sleep(1000);
        doClick(level4);

    }

    // *******************Wait Utils***************//

    /**
     * An expectation for checking that an element is present on the DOM of a page.
     * This does not necessarily mean that the element is visible.
     *
     * @param locator
     * @param timeOut
     * @return
     */
    public List<WebElement> waitForElementsPresence(By locator, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));

    }

    /**
     * This method will wait for all the elements to be present on the page for the given timeOut.
     * It will poll the elements every intervalTime seconds to check if the elements are present.
     * If the elements are present, it will return the list of elements.
     * If the elements are not present after the given timeOut, it will throw an exception
     * @param locator : By locator of the elements
     * @param timeOut : in seconds
     * @return List<WebElement>
     */
    public List<WebElement> waitForVisiblityOfElementsLocated(By locator, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        }catch (Exception e) {
            return Arrays.asList(new WebElement[0]);// return empty array list if element is not found
        }

    }
    /**
     * An expectation for checking that an element is present on the DOM of a page
     * and visible. Visibility means that the element is not only displayed but also
     * has a height and width that is greater than 0.
     *
     * @param locator
     * @param timeOut
     * @return
     * Normal WebDriver wait
     */
    public WebElement waitForElementVisible(By locator, int timeOut) {
        logAction("Waiting up to " + timeOut + "s for visible element " + describeLocator(locator));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        WebElement element = wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(locator)));
        logAction("Visible element found " + describeLocator(locator));
        return element;

    }
    /**
     * This method will wait for the element to be visible on the page for the given timeOut.
     * It will poll the element every intervalTime seconds to check if the element is visible.
     * If the element is visible, it will return the element.
     * If the element is not visible after the given timeOut, it will throw an exception
     * @param locator : By locator of the element
     * @param timeOut : in seconds
     * @param intervalTime : in seconds
     * @return WebElement
     * //WebDriver wait with fluent wait
     */
    public  WebElement waitForElementVisible(By locator, int timeOut, int intervalTime) {

        logAction("Fluent-waiting up to " + timeOut + "s for visible element " + describeLocator(locator)
                + " polling every " + intervalTime + "s");
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(timeOut))
                .pollingEvery(Duration.ofSeconds(intervalTime))
                .ignoring(NoSuchElementException.class)
                .withMessage("===element is not found===");


        WebElement element = wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(locator)));
        logAction("Visible element found " + describeLocator(locator));
        return element;

    }

    public WebElement waitForFreshVisibleElement(By locator, int timeOut) {
        logAction("Waiting up to " + timeOut + "s for fresh visible element " + describeLocator(locator));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        WebElement element = wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(locator)));
        logAction("Fresh visible element found " + describeLocator(locator));
        return element;
    }

    /**
     * An expectation for checking an element is visible and enabled such that you
     * can click it.
     *
     * @param locator
     * @param timeOut
     */
    public void clickWhenReady(By locator, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }
    public void sendKeysWithWait(By locator, String value ,int timeout ){
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(timeout));
        wait.until(ExpectedConditions.elementToBeClickable(locator)).sendKeys(value);
    }

    public String waitForTitleContains(String titleFraction, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));

        try {
            if (wait.until(ExpectedConditions.titleContains(titleFraction))) {
                return driver.getTitle();
            }
        } catch (TimeoutException e) {
            System.out.println("title not found");
        }
        return driver.getTitle();
    }
    @Step("Waiting for the Title and capturing it")
    public String waitForTitleToBe(String titleVal, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));

        try {
            if (wait.until(ExpectedConditions.titleIs(titleVal))) {
                return driver.getTitle();
            }
        } catch (TimeoutException e) {
            System.out.println("title not found");
        }
        return driver.getTitle();
    }

    public String waitForURLContains(String urlFraction, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));

        try {
            if (wait.until(ExpectedConditions.urlContains(urlFraction))) {
                return driver.getCurrentUrl();
            }
        } catch (TimeoutException e) {
            System.out.println("URL not found");
        }
        return driver.getCurrentUrl();
    }

    public String waitForURLToBe(String urlValue, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));

        try {
            if (wait.until(ExpectedConditions.urlToBe(urlValue))) {
                return driver.getCurrentUrl();
            }
        } catch (TimeoutException e) {
            System.out.println("URL not found");
        }
        return driver.getCurrentUrl();
    }

    /**
     * This method will wait for the URL to match the given URL Value
     * and returns the URL if the condition is met within the given timeout
     * @param timeOut
     * @return
     * this is normal webdriver wait
     */
    public Alert waitForJSAlert(int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        return wait.until(ExpectedConditions.alertIsPresent());
    }
    /**
     * This method will wait for the JS Alert to be present on the page
     * and returns the Alert object if the condition is met within the given timeout
     * @param timeOut
     * @return
     * this is with fluent wait
     */

    public Alert waitForJSAlert(int timeOut, int intervalTime) {

        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(timeOut))
                .pollingEvery(Duration.ofSeconds(intervalTime))
                .ignoring(NoAlertPresentException.class)
                .withMessage("===alert is not found===");
        return wait.until(ExpectedConditions.alertIsPresent());
    }


    public String getAlertText(int timeOut) {
        Alert alert = waitForJSAlert(timeOut);
        String text = alert.getText();
        alert.accept();
        return text;
    }

    public void acceptAlert(int timeOut) {
        waitForJSAlert(timeOut).accept();
    }

    public void dismissAlert(int timeOut) {
        waitForJSAlert(timeOut).dismiss();
    }

    public void alertSendKeys(int timeOut, String value) {
        Alert alert = waitForJSAlert(timeOut);
        alert.sendKeys(value);
        alert.accept();
    }


    //wait for iframes/frame:
    /**
     * An expectation for checking whether the given frame is available to switch
     * to. If the frame is available it switches the given driver to the specified
     * frame.
     *
     * @param frameLocator
     * @param timeOut
     */
    public void waitForFrameByLocator(By frameLocator, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameLocator));
    }


    public void waitForFrameByLocator(By frameLocator, int timeOut, int intervalTime) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(timeOut))
                .pollingEvery(Duration.ofSeconds(intervalTime))
                .ignoring(NoSuchFrameException.class)
                .withMessage("===frame is not found===");

        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameLocator));

    }


    public void waitForFrameByIndex(int frameIndex, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameIndex));

    }

    public void waitForFrameByIndex(String frameIDOrName, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameIDOrName));

    }

    public void waitForFrameByIndex(WebElement frameElement, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement));

    }


    public boolean waitForWindowsToBe(int totalWindows, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        return wait.until(ExpectedConditions.numberOfWindowsToBe(totalWindows));
    }

    /**
     * This is to check if page is loaded or not in the given timeout
     * @param timeOut
     */
    public void isPageLoaded(int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        String flag = wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'")).toString();//"true"

        if(Boolean.parseBoolean(flag)) {
            System.out.println("page is completely loaded");
        }
        else {
            throw new RuntimeException("page is not loaded");
        }
    }

    // ✅ Helper method to calculate next working day
    public LocalDate getNextWorkingDayExcludingWeekends(LocalDate currentDate) {
        if (currentDate == null) {
            throw new IllegalArgumentException("currentDate cannot be null");
        }
        LocalDate nextDay = currentDate.plusDays(1); // Start from tomorrow
        while (nextDay.getDayOfWeek() == DayOfWeek.SATURDAY || nextDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
            nextDay = nextDay.plusDays(1);
        }
        return nextDay;

    }

    public void clickStable(By locator, int timeoutSec) {
        logAction("Stable-click on " + describeLocator(locator) + " timeout=" + timeoutSec + "s");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));
        WebElement el;
        try {
            el = wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(locator)));
        } catch (TimeoutException timeoutException) {
            logAction("Clickable wait timed out for " + describeLocator(locator) + ", falling back to visible element.");
            el = wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(locator)));
        }
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", el);
        clickElementWithFallback(el, locator);
    }

    private void clickElementWithFallback(WebElement el, By locator) {
        try {
            el.click();
        } catch (ElementClickInterceptedException e) {
            logAction("Native click intercepted for " + describeLocator(locator) + ", trying Actions click first");
            try {
                new Actions(driver).moveToElement(el).pause(Duration.ofMillis(150)).click().perform();
            } catch (Exception actionsException) {
                logAction("Actions click failed for " + describeLocator(locator) + ", falling back to JS click");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
            }
        } catch (StaleElementReferenceException staleElementReferenceException) {
            logAction("Element went stale while clicking " + describeLocator(locator) + ", retrying with fresh locator.");
            WebElement freshElement = waitForFreshVisibleElement(locator, 5);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", freshElement);
            try {
                freshElement.click();
            } catch (Exception freshClickException) {
                try {
                    new Actions(driver).moveToElement(freshElement).pause(Duration.ofMillis(150)).click().perform();
                } catch (Exception ignored) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", freshElement);
                }
            }
        }
    }

    /**
     * Reliably enters text in a field:
     * - waits visible + enabled
     * - clears (with Ctrl+A + Delete)
     * - types
     * - verifies the value is present (if it's a text/password input, checks value attribute)
     * - retries up to 2 times
     */
    public void enterTextReliable(By locator, String text, int timeoutSec) {
        logAction("Reliable text entry into " + describeLocator(locator) + " value=" + formatValueForLog(locator, text)
                + " timeout=" + timeoutSec + "s");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));
        int attempts = 0;

        while (attempts < 3) {
            logAction("Text entry attempt " + (attempts + 1) + " for " + describeLocator(locator));
            WebElement input = wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(locator)));
            if (!input.isEnabled()) {
                throw new IllegalStateException("Input not enabled for locator: " + locator);
            }

            focusElement(input, locator);
            clearInputValue(input, locator);

            // type
            try {
                input.sendKeys(text);
            } catch (Exception typingException) {
                logAction("sendKeys failed for " + describeLocator(locator) + ", using JavaScript value injection.");
                setInputValueViaJavaScript(input, text);
            }

            // verify input value (for <input type='password'> the value is present but masked in UI)
            String value = safeReadInputValue(input);
            if (value != null && !value.isEmpty()) {
                logAction("Text entry succeeded for " + describeLocator(locator));
                return; // success
            }

            setInputValueViaJavaScript(input, text);
            value = safeReadInputValue(wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(locator))));
            if (value != null && !value.isEmpty()) {
                logAction("Text entry succeeded for " + describeLocator(locator) + " after JavaScript fallback.");
                return; // success
            }

            attempts++;
        }

        throw new IllegalStateException("Failed to enter text into: " + locator +
                " after retries. The element may be losing focus or being re-rendered.");
    }

    private void focusElement(WebElement element, By locator) {
        try {
            new Actions(driver).moveToElement(element).pause(Duration.ofMillis(100)).click().perform();
            return;
        } catch (Exception actionException) {
            logAction("Actions focus failed for " + describeLocator(locator) + ", using JavaScript focus.");
        }
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].focus();", element);
        } catch (Exception ignored) {
        }
    }

    private void clearInputValue(WebElement input, By locator) {
        try {
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            input.sendKeys(Keys.DELETE);
        } catch (Exception clearException) {
            logAction("Keyboard clear failed for " + describeLocator(locator) + ", using JavaScript clear.");
            setInputValueViaJavaScript(input, "");
        }
    }

    private String safeReadInputValue(WebElement input) {
        if (input == null) {
            return "";
        }
        try {
            String value = input.getAttribute("value");
            if (value == null || value.trim().isEmpty()) {
                value = input.getText();
            }
            return value == null ? "" : value.trim();
        } catch (StaleElementReferenceException staleElementReferenceException) {
            return "";
        }
    }

    private void setInputValueViaJavaScript(WebElement input, String value) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1];" +
                            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                    input,
                    value
            );
        } catch (Exception ignored) {
        }
    }

    /**
     * Optional: waits for overlay/spinner to disappear
     */
    public void waitForOverlayToDisappear(By overlayLocator, int timeoutSec) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(overlayLocator));
    }

    public void logoutAndLoginWithMicrosoftAccount(By[] accountMenuCandidates,
                                                   By[] logoutCandidates,
                                                   By[] accountCandidates,
                                                   By[] useAnotherAccountCandidates,
                                                   By[] emailCandidates,
                                                   By nextBtnLocator,
                                                   By[] passwordCandidates,
                                                   By signInBtnLocator,
                                                   By[] staySignedInCandidates,
                                                   String email,
                                                   String password) {
        logAction("Starting Microsoft logout/login flow.");
        if (!isAnyVisible(emailCandidates) && !isAnyVisible(passwordCandidates)) {
            dismissTransientMenus();
            boolean accountMenuClicked = clickFirstVisible(accountMenuCandidates, 10);
            By logoutLocator = waitForAnyVisibleLocator(logoutCandidates, 8);
            if (logoutLocator == null) {
                logAction("Logout option was not visible after the first account-menu click. Retrying with a fresh header state.");
                dismissTransientMenus();
                accountMenuClicked = clickFirstVisible(accountMenuCandidates, 10) || accountMenuClicked;
                logoutLocator = waitForAnyVisibleLocator(logoutCandidates, 8);
            }

            if (!accountMenuClicked || logoutLocator == null) {
                throw new IllegalStateException("Logout controls not visible/clickable. Cannot proceed with Microsoft login.");
            }

            logAction("Clicking logout option " + describeLocator(logoutLocator));
            clickStable(logoutLocator, 10);
        }

        if (!isAnyVisible(emailCandidates) && !isAnyVisible(passwordCandidates)) {
            logAction("Current account tile is visible, selecting it before switching account.");
            clickFirstVisible(accountCandidates, 12);
        }

        List<By> postAccountStates = new ArrayList<>();
        if (useAnotherAccountCandidates != null) {
            postAccountStates.addAll(Arrays.asList(useAnotherAccountCandidates));
        }
        if (emailCandidates != null) {
            postAccountStates.addAll(Arrays.asList(emailCandidates));
        }
        if (passwordCandidates != null) {
            postAccountStates.addAll(Arrays.asList(passwordCandidates));
        }

        By stateLocator = waitForAnyVisibleLocator(postAccountStates.toArray(new By[0]), 18);
        if (stateLocator == null) {
            logAction("Account picker did not expose the expected state, retrying current account tile.");
            clickFirstVisible(accountCandidates, 10);
            stateLocator = waitForAnyVisibleLocator(postAccountStates.toArray(new By[0]), 18);
        }
        if (stateLocator != null && useAnotherAccountCandidates != null && Arrays.asList(useAnotherAccountCandidates).contains(stateLocator)) {
            logAction("Clicking 'Use another account' tile " + describeLocator(stateLocator));
            clickFirstVisible(useAnotherAccountCandidates, 12);
        }

        By emailLocator = waitForAnyVisibleLocator(emailCandidates, 20);
        if (emailLocator == null && clickFirstVisible(useAnotherAccountCandidates, 10)) {
            logAction("'Use another account' clicked while waiting for email field.");
            emailLocator = waitForAnyVisibleLocator(emailCandidates, 15);
        }
        if (emailLocator == null && clickFirstVisible(accountCandidates, 10)) {
            logAction("Account tile clicked while waiting for email field.");
            emailLocator = waitForAnyVisibleLocator(emailCandidates, 15);
        }

        if (emailLocator == null) {
            throw new IllegalStateException("Email field was not visible after account selection. " +
                    "Current Microsoft picker state is not ready for sign-in.");
        }

        try {
            logAction("Entering Microsoft email " + formatValueForLog(emailLocator, email));
            enterTextReliable(emailLocator, email, 20);
            logAction("Clicking Next after email entry.");
            clickStable(nextBtnLocator, 15);
        } catch (TimeoutException te) {
            throw new IllegalStateException("Email field or Next button not available during sign-in.", te);
        } catch (IllegalStateException ise) {
            throw new IllegalStateException("Failed to enter email due to re-render/focus issues.", ise);
        }

        By passwordLocator = waitForAnyVisibleLocator(passwordCandidates, 20);
        if (passwordLocator == null) {
            throw new IllegalStateException("Password field was not visible after entering the email.");
        }

        try {
            logAction("Entering Microsoft password " + formatValueForLog(passwordLocator, password));
            WebElement pwdEl = waitForElementVisible(passwordLocator, 20);
            if (!pwdEl.isDisplayed() || !pwdEl.isEnabled()) {
                throw new IllegalStateException("Password field is present but not visible/enabled.");
            }
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", pwdEl);
            enterTextReliable(passwordLocator, password, 20);

            String pwdValue = driver.findElement(passwordLocator).getAttribute("value");
            if (pwdValue == null || pwdValue.isEmpty()) {
                throw new IllegalStateException("Password value did not stick after entry - UI may be re-rendering.");
            }
        } catch (TimeoutException te) {
            throw new NoSuchElementException("Password field is not visible within timeout - login page may not have loaded, or an overlay is blocking.", te);
        } catch (StaleElementReferenceException sere) {
            WebElement pwdEl = waitForElementVisible(passwordLocator, 10);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", pwdEl);
            enterTextReliable(passwordLocator, password, 10);
        } catch (IllegalStateException ise) {
            throw new IllegalStateException("Failed to enter password: " + ise.getMessage(), ise);
        }

        try {
            logAction("Clicking Sign In.");
            clickStable(signInBtnLocator, 20);
        } catch (TimeoutException te) {
            throw new IllegalStateException("Sign-in button not clickable. An overlay or validation error might be blocking.", te);
        }

        if (staySignedInCandidates != null && staySignedInCandidates.length > 0) {
            logAction("Checking for stay-signed-in prompt.");
            By staySignedInLocator = waitForAnyVisibleLocator(staySignedInCandidates, 10);
            if (staySignedInLocator != null) {
                logAction("Clicking stay-signed-in option " + describeLocator(staySignedInLocator));
                clickStable(staySignedInLocator, 12);
            }
        }
    }

    private void dismissTransientMenus() {
        try {
            new Actions(driver).sendKeys(Keys.ESCAPE).pause(Duration.ofMillis(150)).perform();
            logAction("Dismissed transient menu with ESC.");
        } catch (Exception ignored) {
        }
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "if (document && document.body) { document.body.dispatchEvent(new MouseEvent('click', {bubbles:true})); }"
            );
            logAction("Issued a body click to close any open popovers.");
        } catch (Exception ignored) {
        }
    }

    public boolean clickFirstVisibleAccount(List<By> locators, int timeoutSeconds) {
        if (locators == null || locators.isEmpty()) {
            return false;
        }
        logAction("Clicking first visible account candidate from " + locators.size() + " options.");
        return clickFirstVisible(locators.toArray(new By[0]), timeoutSeconds);
    }

    public boolean isAnyVisible(By[] locators) {
        if (locators == null) {
            return false;
        }
        for (By locator : locators) {
            if (locator == null) {
                continue;
            }
            try {
                if (isElementVisible(locator, 2)) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    public boolean waitForAnyVisible(By[] locators, int timeoutSec) {
        return waitForAnyVisibleLocator(locators, timeoutSec) != null;
    }

    public By waitForAnyVisibleLocator(By[] locators, int timeoutSec) {
        if (locators == null || locators.length == 0) {
            return null;
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));
        try {
            return wait.until(d -> {
                for (By locator : locators) {
                    if (locator == null) {
                        continue;
                    }
                    try {
                        List<WebElement> elements = d.findElements(locator);
                        for (WebElement element : elements) {
                            try {
                                if (element.isDisplayed()) {
                                    return locator;
                                }
                            } catch (StaleElementReferenceException stale) {
                                logAction("Ignoring stale element while checking visibility for " + describeLocator(locator));
                            }
                        }
                    } catch (StaleElementReferenceException stale) {
                        logAction("Ignoring stale locator while checking visibility for " + describeLocator(locator));
                    }
                }
                return null;
            });
        } catch (TimeoutException e) {
            return null;
        }
    }

    public void waitForAppLanding() {
        logAction("Waiting for application landing markers after Microsoft sign-in.");
        By[] landingMarkers = {
                By.xpath("//input[@placeholder='Search']"),
                By.xpath("//*[name()='svg' and @data-testid='AddTaskIcon']"),
                By.cssSelector(".css-odsz1v")
        };
        By landingMarker = waitForAnyVisibleLocator(landingMarkers, 20);
        if (landingMarker == null) {
            throw new IllegalStateException("Application did not load after alternate account sign-in.");
        }
        logAction("Application landing detected via " + describeLocator(landingMarker));
    }

    /**
     * Checks whether the OptiWise landing shell is already visible.
     * This is useful when Microsoft redirects directly into an active app session.
     */
    public boolean isApplicationLandingVisible() {
        By[] landingMarkers = {
                By.xpath("//input[@placeholder='Search']"),
                By.xpath("//*[name()='svg' and @data-testid='AddTaskIcon']"),
                By.cssSelector(".css-odsz1v")
        };
        return waitForAnyVisible(landingMarkers, 2);
    }

    /**
     * Opens the email-entry step when Microsoft shows a saved-account picker first.
     * The method handles fresh browsers, saved sessions, and already-visible password states.
     */
    public void openMicrosoftEmailStepIfAccountPickerIsVisible(By emailInputField,
                                                               By useOtherAccount,
                                                               By passwordInputField,
                                                               int timeoutSeconds) {
        if (isElementVisible(emailInputField, 5)) {
            logAction("Microsoft email field is already visible.");
            return;
        }

        logAction("Resolving Microsoft account-picker state before entering credentials.");
        By otherTile = By.id("otherTile");
        By otherTileText = By.id("otherTileText");
        By pickAccountHeading = By.xpath("//*[normalize-space()='Pick an account']");
        By[] preLoginStates = {
                emailInputField,
                useOtherAccount,
                otherTile,
                otherTileText,
                pickAccountHeading,
                passwordInputField,
                By.xpath("//input[@placeholder='Search']"),
                By.xpath("//*[name()='svg' and @data-testid='AddTaskIcon']"),
                By.cssSelector(".css-odsz1v")
        };

        By stateLocator = waitForAnyVisibleLocator(preLoginStates, timeoutSeconds);
        if (stateLocator == null) {
            throw new IllegalStateException("Unable to determine the Microsoft sign-in state before entering credentials.");
        }

        if (isApplicationLandingVisible()) {
            logAction("Application landing became visible while resolving account picker.");
            return;
        }

        if (matchesLocator(stateLocator, passwordInputField)) {
            logAction("Microsoft password field is already visible.");
            return;
        }

        if (matchesLocator(stateLocator, emailInputField)) {
            logAction("Microsoft email field became visible.");
            return;
        }

        if (matchesLocator(stateLocator, useOtherAccount)
                || matchesLocator(stateLocator, otherTile)
                || matchesLocator(stateLocator, otherTileText)
                || matchesLocator(stateLocator, pickAccountHeading)) {
            logAction("Microsoft account picker detected. Clicking 'Use another account'.");
            List<By> useAnotherAccountCandidates = Arrays.asList(
                    By.xpath("//*[contains(normalize-space(.),'Use another account') or contains(normalize-space(.),'Add another account')]/ancestor::*[self::button or self::li or self::div[@role='button'] or self::div[@role='option'] or self::div[@id='otherTile']][1]"),
                    otherTile,
                    otherTileText,
                    useOtherAccount
            );
            boolean clicked = clickFirstVisibleAccount(useAnotherAccountCandidates, timeoutSeconds);
            if (!clicked) {
                throw new IllegalStateException("'Use another account' option was visible but could not be clicked.");
            }
        }
    }

    /**
     * Handles the Microsoft post-password submit/KMSI prompt only for environments that expose it.
     */
    public void handleMicrosoftPostPasswordSubmitPrompt(By submitButton,
                                                        boolean skipPrompt,
                                                        int timeoutSeconds) {
        if (skipPrompt) {
            logAction("Skipping Microsoft post-password submit prompt for this environment.");
            return;
        }

        if (isElementVisible(submitButton, timeoutSeconds)) {
            logAction("Microsoft post-password submit prompt visible. Clicking submit button.");
            clickStable(submitButton, timeoutSeconds);
            return;
        }

        logAction("Microsoft post-password submit prompt was not visible. Continuing to application landing.");
    }

    /**
     * Waits for the Microsoft password input after the email step and recovers from transient
     * Edge network error pages such as ERR_CONNECTION_RESET, which can appear in CI or flaky VPN sessions.
     */
    public By waitForMicrosoftPasswordInputAfterEmail(By passwordInputField, int timeoutSeconds) {
        int attempts = 3;
        for (int attempt = 1; attempt <= attempts; attempt++) {
            int waitSeconds = attempt == 1 ? timeoutSeconds : Math.max(8, timeoutSeconds / 2);
            By passwordLocator = waitForAnyVisibleLocator(new By[]{passwordInputField}, waitSeconds);
            if (passwordLocator != null) {
                return passwordLocator;
            }

            if (!isEdgeNetworkErrorPageVisible()) {
                break;
            }

            logAction("Edge network error page detected while waiting for Microsoft password input. "
                    + "Refreshing auth page. Attempt " + attempt + " of " + attempts + ".");
            refreshCurrentPageFromBrowserError();
        }

        throw new IllegalStateException("Microsoft password input did not become visible after entering the email. "
                + "Current title='" + driver.getTitle() + "', currentUrl='" + driver.getCurrentUrl() + "'.");
    }

    /**
     * Detects Edge/Chromium network error pages so login can fail clearly or retry safely.
     */
    public boolean isEdgeNetworkErrorPageVisible() {
        By[] networkErrorMarkers = {
                By.id("main-frame-error"),
                By.cssSelector(".error-code"),
                By.xpath("//*[contains(normalize-space(.),'ERR_CONNECTION_RESET')]"),
                By.xpath("//*[contains(normalize-space(.),\"can't reach this page\")]"),
                By.xpath("//*[contains(normalize-space(.),'The connection was reset')]")
        };
        return waitForAnyVisible(networkErrorMarkers, 1);
    }

    private void refreshCurrentPageFromBrowserError() {
        By[] refreshCandidates = {
                By.id("reload-button"),
                By.xpath("//button[normalize-space()='Refresh']")
        };
        if (!clickFirstVisible(refreshCandidates, 4)) {
            driver.navigate().refresh();
        }
    }

    /**
     * PROD does not expose the extra submit/KMSI prompt in the current RMT login path.
     */
    public boolean shouldSkipMicrosoftPostPasswordSubmitPromptForCurrentEnvironment() {
        String envName = System.getProperty("env", "uat");
        if (envName == null || envName.trim().isEmpty()) {
            envName = "uat";
        }
        return "prod".equalsIgnoreCase(envName.trim().toLowerCase(Locale.ENGLISH));
    }

    /**
     * Completes the post-login Microsoft prompt when it appears and waits until the OptiWise landing markers
     * are visible. This keeps local and CI runs resilient when Microsoft inserts a KMSI step between
     * credential submission and the application shell.
     */
    public void ensureApplicationLandingAfterLogin() {
        logAction("Ensuring OptiWise application landing after login.");
        By[] landingMarkers = {
                By.xpath("//input[@placeholder='Search']"),
                By.xpath("//*[name()='svg' and @data-testid='AddTaskIcon']"),
                By.cssSelector(".css-odsz1v"),
                By.xpath("//button[contains(@aria-label,'account')]")
        };
        if (waitForAnyVisible(landingMarkers, 4)) {
            logAction("Application markers are already visible.");
            return;
        }

        resolveStaySignedInPromptIfPresent();

        By landingMarker = waitForAnyVisibleLocator(landingMarkers, 25);
        if (landingMarker != null) {
            logAction("Application landing confirmed via " + describeLocator(landingMarker));
            return;
        }

        throwIfBlockingMicrosoftChallengeIsVisible();

        throw new IllegalStateException("Application landing was not reached after login. Current title='"
                + driver.getTitle() + "', currentUrl='" + driver.getCurrentUrl() + "'.");
    }

    /**
     * Detects interactive Microsoft challenges that cannot be completed in unattended automation, such as
     * Authenticator push approval. Surfacing the blocker explicitly makes CI/CD failures easier to diagnose.
     */
    private void throwIfBlockingMicrosoftChallengeIsVisible() {
        By[] authenticatorChallengeMarkers = {
                By.id("idDiv_SAOTCAS_Description"),
                By.xpath("//*[contains(normalize-space(.),'Open your Authenticator app')]"),
                By.xpath("//*[contains(normalize-space(.),\"I can't use my Microsoft Authenticator app right now\")]"),
                By.id("idRichContext_DisplaySign")
        };

        By blockingMarker = waitForAnyVisibleLocator(authenticatorChallengeMarkers, 3);
        if (blockingMarker == null) {
            return;
        }

        throw new IllegalStateException("Microsoft Authenticator approval is blocking the login flow. "
                + "Use a CI-safe account or disable interactive MFA for unattended PROD execution. "
                + "Current title='" + driver.getTitle() + "', currentUrl='" + driver.getCurrentUrl() + "'.");
    }

    /**
     * Handles Microsoft "Stay signed in?" prompts using the safest visible continuation button.
     */
    private void resolveStaySignedInPromptIfPresent() {
        By[] kmsiMarkers = {
                By.id("KmsiDescription"),
                By.id("idSIButton9"),
                By.id("idBtn_Back"),
                By.xpath("//*[contains(normalize-space(.),'Stay signed in')]"),
                By.xpath("//input[@name='DontShowAgain']/ancestor::label[1]")
        };
        By kmsiMarker = waitForAnyVisibleLocator(kmsiMarkers, 5);
        if (kmsiMarker == null) {
            return;
        }

        logAction("Microsoft KMSI prompt detected via " + describeLocator(kmsiMarker));
        By[] declineCandidates = {
                By.id("idBtn_Back"),
                By.xpath("//input[@value='No']"),
                By.xpath("//button[normalize-space()='No']")
        };
        By[] acceptCandidates = {
                By.id("idSIButton9"),
                By.xpath("//input[@value='Yes']"),
                By.xpath("//button[normalize-space()='Yes']")
        };

        if (clickFirstVisible(declineCandidates, 5)) {
            logAction("Dismissed KMSI prompt using the 'No' action.");
            return;
        }
        if (clickFirstVisible(acceptCandidates, 5)) {
            logAction("Continued through KMSI prompt using the 'Yes' action.");
        }
    }

    private boolean clickFirstVisible(By[] locators, int timeoutSec) {
        if (locators == null) {
            return false;
        }
        for (By locator : locators) {
            if (locator == null) {
                continue;
            }
            try {
                if (!isElementVisible(locator, timeoutSec)) {
                    continue;
                }
                clickStable(locator, timeoutSec);
                return true;
            } catch (Exception ignored) {
                try {
                    WebElement element = waitForElementVisible(locator, timeoutSec);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                    return true;
                } catch (Exception ignoredToo) {
                    // Try the next locator candidate.
                }
            }
        }
        return false;
    }

    private boolean matchesLocator(By actualLocator, By expectedLocator) {
        if (actualLocator == null || expectedLocator == null) {
            return false;
        }
        return actualLocator.toString().equals(expectedLocator.toString());
    }
}
