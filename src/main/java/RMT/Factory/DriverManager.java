package RMT.Factory;

import RMT.Constants.AppConstants;
import RMT.Errors.AppError;
import RMT.Exceptions.BrowserException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class DriverManager {

    WebDriver driver;
    Properties prop;


    public WebDriver initDriver(Properties prop ){
        //cross browser logic

        String browserName= prop.getProperty("browser");
        System.out.println("Browser name is "+ browserName);
        switch (browserName.trim().toLowerCase()){
            case "chrome":
                driver=new ChromeDriver();
                break;
            case "firefox":
                driver=new FirefoxDriver();
                break;
            case "edge":
                driver=new EdgeDriver();
                break;
            default:
                System.out.println("Please pass the right browser"+ browserName);
                throw new BrowserException(AppError.BROWSER_NOT_FOUND );
        }
        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
        driver.get(prop.getProperty("url"));
        return driver;
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
