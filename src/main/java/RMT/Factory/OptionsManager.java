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

        if (Boolean.parseBoolean(prop.getProperty("headless"))) {//headless is the key of which value will be fetch from config file.
            System.out.println("====Running tests in headless======");
            co.addArguments("--headless");

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
        eo = new EdgeOptions();

        if (Boolean.parseBoolean(prop.getProperty("headless"))) {
            System.out.println("====Running tests in headless======");
            eo.addArguments("--headless");
        }
        if (Boolean.parseBoolean(prop.getProperty("incognito"))) {
            eo.addArguments("--inPrivate");
        }

        if (Boolean.parseBoolean(prop.getProperty("remote"))) {
            eo.setCapability("browserName", "edge");
            // eo.setCapability("enableVNC", true);
        }

        return eo;
    }


}
