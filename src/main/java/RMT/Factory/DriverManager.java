package RMT.Factory;

import RMT.Constants.AppConstants;
import RMT.Errors.AppError;
import RMT.Exceptions.BrowserException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.io.FileHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class DriverManager {

    WebDriver driver;
    Properties prop;
    public static ThreadLocal<WebDriver>tlDriver=new ThreadLocal<WebDriver>();


    public WebDriver initDriver(Properties prop ){
        //cross browser logic

        String browserName= prop.getProperty("browser");
        System.out.println("Browser name is "+ browserName);
        switch (browserName.trim().toLowerCase()){
            case "chrome":
                tlDriver.set(new ChromeDriver());
                break;
            case "firefox":
                tlDriver.set(new FirefoxDriver());
                break;
            case "edge":
                tlDriver.set(new EdgeDriver());
                break;
            default:
                System.out.println("Please pass the right browser"+ browserName);
                throw new BrowserException(AppError.BROWSER_NOT_FOUND );
        }
        getDriver().manage().deleteAllCookies();
        getDriver().manage().window().maximize();
        getDriver().get(prop.getProperty("url"));
        return getDriver();
    }

    /**
     * get the local thread copy of the driver
     * @return
     */
    public static WebDriver getDriver(){
        return tlDriver.get();
    }


    public Properties initProp(){
        prop = new Properties();
        FileInputStream ip= null;
        try {
            ip = new FileInputStream(AppConstants.CONGIF_FILE_PATH);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            prop.load(ip);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return prop;
    }
}
