package RMT.Factory;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class OptionsManager {
    private Properties prop;
    private ChromeOptions co;
    private FirefoxOptions fo;
    private EdgeOptions eo;

    public OptionsManager(Properties prop) {
        this.prop = prop;
    }

    /**
     * Creates an instance of ChromeOptions based on the properties provided in the constructor.
     * The properties that are used to configure the ChromeOptions are:
     * <ul>
     *     <li>headless: if true, the test will be run in headless mode.</li>
     *     <li>incognito: if true, the test will be run in incognito mode.</li>
     *     <li>remote: if true, the test will be run in remote mode.</li>
     * </ul>
     *
     * @return the configured ChromeOptions
     */
    public ChromeOptions getChromeOptions() {
        co = new ChromeOptions();

        if (Boolean.parseBoolean(prop.getProperty("headless"))) {
            System.out.println("====Running tests in headless======");

            // 🔥 Use new headless mode (important for Chrome 109+)
            co.addArguments("--headless=new");

            // 🔥 REQUIRED for Linux CI
            co.addArguments("--no-sandbox");
            co.addArguments("--disable-dev-shm-usage");

            // 🔥 Prevent resolution/rendering issues
            co.addArguments("--disable-gpu");
            co.addArguments("--window-size=1920,1080");
        }

        if (Boolean.parseBoolean(prop.getProperty("incognito"))) {
            co.addArguments("--incognito");
        }

        if (Boolean.parseBoolean(prop.getProperty("remote"))) {
            co.setCapability("browserName", "chrome");
            co.setBrowserVersion(prop.getProperty("browserversion").trim());

            Map<String, Object> selenoidOptions = new HashMap<>();
            selenoidOptions.put("screenResolution", "1280x1024x24");
            selenoidOptions.put("enableVNC", true);
            selenoidOptions.put("name", prop.getProperty("testname"));
            co.setCapability("selenoid:options", selenoidOptions);
        }

        return co;
    }

    /**
     * Returns an instance of FirefoxOptions configured based on the properties provided in the constructor.
     * The properties that are used to configure the FirefoxOptions are:
     * <ul>
     *     <li>headless: if true, the test will be run in headless mode.</li>
     *     <li>incognito: if true, the test will be run in incognito mode.</li>
     *     <li>remote: if true, the test will be run in remote mode.</li>
     * </ul>
     *
     * @return the configured FirefoxOptions
     */
    public FirefoxOptions getFirefoxOptions() {
        fo = new FirefoxOptions();

        if (Boolean.parseBoolean(prop.getProperty("headless"))) {
            System.out.println("====Running tests in headless======");
            fo.addArguments("--headless");
        }
        if (Boolean.parseBoolean(prop.getProperty("incognito"))) {
            fo.addArguments("--incognito");
        }
        if (Boolean.parseBoolean(prop.getProperty("remote"))) {
            fo.setCapability("browserName", "firefox");
            fo.setBrowserVersion(prop.getProperty("browserversion").trim());

            Map<String, Object> selenoidOptions = new HashMap<>();
            selenoidOptions.put("screenResolution", "1280x1024x24");
            selenoidOptions.put("enableVNC", true);
            selenoidOptions.put("name", prop.getProperty("testname"));
            fo.setCapability("selenoid:options", selenoidOptions);		}

        return fo;
    }

    /**
     * Creates an instance of EdgeOptions based on the properties provided in the constructor.
     * The properties that are used to configure the EdgeOptions are:
     * <ul>
     *     <li>headless: if true, the test will be run in headless mode.</li>
     *     <li>incognito: if true, the test will be run in incognito mode.</li>
     *     <li>remote: if true, the test will be run in remote mode.</li>
     * </ul>
     *
     * @return the configured EdgeOptions
     */
    public EdgeOptions getEdgeOptions() {
        EdgeOptions eo = new EdgeOptions();

        if (Boolean.parseBoolean(prop.getProperty("headless"))) {
            System.out.println("====Running tests in headless======");

            eo.addArguments("--headless=new");      // VERY IMPORTANT
            eo.addArguments("--window-size=1920,1080");
            eo.addArguments("--start-maximized");
            eo.addArguments("--disable-gpu");
            eo.addArguments("--no-sandbox");
            eo.addArguments("--disable-dev-shm-usage");
        }

        if (Boolean.parseBoolean(prop.getProperty("incognito"))) {
            eo.addArguments("--inPrivate");
        }

        if (Boolean.parseBoolean(prop.getProperty("remote"))) {
            eo.setCapability("browserName", "edge");
        }

        return eo;
    }


}
