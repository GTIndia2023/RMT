package RMT.Factory;

import RMT.Errors.AppError;
import RMT.Exceptions.BrowserException;
import com.titusfortner.logging.SeleniumLogger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DriverManager {

    WebDriver driver;
    Properties prop;
    OptionsManager optionsManager;
    public static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<WebDriver>();

    public WebDriver initDriver(Properties prop) {
        String browserName = prop.getProperty("browser");
        System.out.println("Browser name is " + browserName);
        if (browserName == null || browserName.trim().isEmpty()) {
            throw new BrowserException("Browser is not defined in config file");
        }

        optionsManager = new OptionsManager(prop);
        switch (browserName.trim().toLowerCase()) {
            case "chrome":
                SeleniumLogger.enable();
                try {
                    System.out.println("Setting up chrome driver....");
                    tlDriver.set(new ChromeDriver(optionsManager.getChromeOptions()));
                    System.out.println("ChromeDriver initialized successfully " + tlDriver.get());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ChromeDriver failed to initialize " + e);
                }
                break;

            case "firefox":
                tlDriver.set(new FirefoxDriver(optionsManager.getFirefoxOptions()));
                break;

            case "edge":
                System.out.println("Loaded config: " + prop);
                SeleniumLogger.enable();
                try {
                    System.out.println("Setting up edge driver.....");
                    tlDriver.set(new EdgeDriver(optionsManager.getEdgeOptions()));
                    System.out.println("EdgeDriver initialized successfully " + tlDriver.get());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("EdgeDriver failed to initialize " + e);
                }
                break;

            default:
                System.out.println("Please pass the right browser " + browserName);
                throw new BrowserException(AppError.BROWSER_NOT_FOUND);
        }

        WebDriver wd = getDriver();
        if (wd == null) {
            throw new RuntimeException("Driver initialization failed! WebDriver is null.");
        }

        getDriver().manage().deleteAllCookies();
        applyDeterministicWindowSize(prop);
        openApplicationUrlWithRetry(prop.getProperty("url"));
        return getDriver();
    }

    /**
     * Applies a deterministic desktop viewport so headless and local runs render the same layout.
     */
    private void applyDeterministicWindowSize(Properties prop) {
        Dimension targetSize = new Dimension(1920, 1080);
        try {
            getDriver().manage().window().maximize();
        } catch (Exception ignored) {
        }

        try {
            Dimension currentSize = getDriver().manage().window().getSize();
            boolean headlessMode = Boolean.parseBoolean(prop.getProperty("headless", "false"));
            if (headlessMode || currentSize.getWidth() < 1400 || currentSize.getHeight() < 900) {
                getDriver().manage().window().setSize(targetSize);
            }
        } catch (Exception e) {
            getDriver().manage().window().setSize(targetSize);
        }
    }

    /**
     * Opens the application URL with lightweight retries so transient network resets do not fail the suite immediately.
     */
    private void openApplicationUrlWithRetry(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("Application URL is not configured.");
        }

        WebDriverException lastFailure = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                getDriver().get(url.trim());
                return;
            } catch (WebDriverException e) {
                lastFailure = e;
                System.out.println("[DriverManager] Navigation attempt " + attempt + " failed for URL: " + url
                        + " | " + e.getMessage());
                if (attempt == 3) {
                    break;
                }
                try {
                    Thread.sleep(1500L * attempt);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while retrying application navigation.", interruptedException);
                }
            }
        }

        throw lastFailure == null
                ? new WebDriverException("Unable to open application URL: " + url)
                : lastFailure;
    }

    /**
     * get the local thread copy of the driver
     * @return driver copy of the ThreadLocal driver
     */
    public static WebDriver getDriver() {
        return tlDriver.get();
    }

    public Properties initProp() {
        prop = new Properties();

        String envName = System.getProperty("env");
        System.out.println("Running the test suite on env ---> " + envName);

        if (envName == null || envName.trim().isEmpty()) {
            envName = "uat";
            System.out.println("No env provided. Defaulting to UAT.");
        }

        String fileName = "Config/config." + envName.toLowerCase() + ".properties";

        try (InputStream ip = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (ip == null) {
                throw new RuntimeException("Config file not found in classpath: " + fileName);
            }
            prop.load(ip);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file", e);
        }

        applyRuntimeOverrides(
                "browser",
                "headless",
                "incognito",
                "remote",
                "url",
                "username",
                "password",
                "db.url",
                "db.username",
                "db.password",
                "allocation.validation.query",
                "allocation.switch.username",
                "allocation.switch.password"
        );

        return prop;
    }

    /**
     * Applies runtime property overrides so CI pipelines can inject secrets and environment-specific values safely.
     */
    private void applyRuntimeOverrides(String... keys) {
        for (String key : keys) {
            overrideFromSystemOrEnv(key);
        }
    }

    private void overrideFromSystemOrEnv(String key) {
        String fromSystem = System.getProperty(key);
        if (!isBlank(fromSystem)) {
            prop.setProperty(key, fromSystem.trim());
            System.out.println("Overriding property from system: " + key + "=" + maskIfSensitive(key, fromSystem));
            return;
        }

        for (String envKey : getEnvCandidates(key)) {
            String fromEnv = System.getenv(envKey);
            if (!isBlank(fromEnv)) {
                prop.setProperty(key, fromEnv.trim());
                System.out.println("Overriding property from env: " + key + " <= " + envKey
                        + " (" + maskIfSensitive(key, fromEnv) + ")");
                return;
            }
        }
    }

    private String[] getEnvCandidates(String key) {
        String normalized = key.toUpperCase().replace('.', '_').replace('-', '_');
        switch (key) {
            case "browser":
                return new String[]{"RMT_BROWSER", "BROWSER"};
            case "headless":
                return new String[]{"RMT_HEADLESS", "HEADLESS"};
            case "incognito":
                return new String[]{"RMT_INCOGNITO", "INCOGNITO"};
            case "remote":
                return new String[]{"RMT_REMOTE", "REMOTE"};
            case "url":
                return new String[]{"RMT_URL", "APP_URL", "URL"};
            case "username":
                return new String[]{"RMT_USERNAME", "APP_USERNAME"};
            case "password":
                return new String[]{"RMT_PASSWORD", "APP_PASSWORD", "PASSWORD"};
            case "db.url":
                return new String[]{"DB_URL", "RMT_DB_URL"};
            case "db.username":
                return new String[]{"DB_USERNAME", "RMT_DB_USERNAME"};
            case "db.password":
                return new String[]{"DB_PASSWORD", "RMT_DB_PASSWORD"};
            case "allocation.validation.query":
                return new String[]{"ALLOCATION_VALIDATION_QUERY", "RMT_ALLOCATION_VALIDATION_QUERY"};
            case "allocation.switch.username":
                return new String[]{"RMT_ALLOCATION_SWITCH_USERNAME", "ALLOCATION_SWITCH_USERNAME"};
            case "allocation.switch.password":
                return new String[]{"RMT_ALLOCATION_SWITCH_PASSWORD", "ALLOCATION_SWITCH_PASSWORD"};
            default:
                return new String[]{"RMT_" + normalized, normalized};
        }
    }

    private String maskIfSensitive(String key, String value) {
        if (isBlank(value)) {
            return "";
        }
        String normalizedKey = key == null ? "" : key.toLowerCase();
        if (normalizedKey.contains("password")) {
            return "******";
        }
        return value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
