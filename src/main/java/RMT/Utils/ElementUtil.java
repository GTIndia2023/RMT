package RMT.Utils;

import RMT.Exceptions.ElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ElementUtil {
    private WebDriver driver;

    public ElementUtil(WebDriver driver) {
        this.driver = driver;
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
        getElement(locator).clear();
        getElement(locator).sendKeys(value);
    }

    public void doSendKeys(By locator, String value, int timeOut) {
        nullCheck(value);
        waitForElementVisible(locator, timeOut).clear();
        waitForElementVisible(locator, timeOut).sendKeys(value);
    }

    public void doSendKeys(By locator, CharSequence... value) {
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
        getElement(locator).click();
    }

    public void doClick(By locator, int timeOut) {
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

    public void handleParentSubMenu(By parentLocator, By childLocator) throws InterruptedException {
        Actions act = new Actions(driver);
        act.moveToElement(getElement(parentLocator)).perform();
        Thread.sleep(2000);
        doClick(childLocator);
    }

    /**
     * This method is used for handling the dropdown menue for RMS Skill Master category dropdown as " Technical"
     * @param parentLocator
     * @throws InterruptedException
     */
    public void handleDropdownMenue(By parentLocator) throws InterruptedException {
        Actions act = new Actions(driver);
        doClick(parentLocator);// Clcking on dropdown
        Thread.sleep(2000);
        act.sendKeys("Technical").perform();
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
        doClick(parentLocator);// Clicking on competency dropdown
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
        List <String> elementList=DesignationUtil.getFilteredData(gradeFilter);// This will be giving us the list of xpaths base on grade
        for( String xpath : elementList){
        Actions act = new Actions(driver);
        doActionsClick(parentLocator);// This operation will click on designation dropdown
        act.sendKeys("").perform();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (gradeFilter.equals(gradeFilter)){
            System.out.println(" Xpath to be selected are " + xpath);
        }
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            WebElement button = driver.findElement(By.xpath(xpath));
            jsUtil.scrollIntoView(button);
            button.click();
        }
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
        char ch[] = value.toCharArray();
        for (char c : ch) {
            act.sendKeys(getElement(locator), String.valueOf(c)).pause(500).perform();
        }
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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

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

        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(timeOut))
                .pollingEvery(Duration.ofSeconds(intervalTime))
                .ignoring(NoSuchElementException.class)
                .withMessage("===element is not found===");


        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

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
}
