package utils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
//import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.reportmanager.ExtentManager;
import utils.reportmanager.ExtentTestManager;
import utils.reportmanager.Reporter;


public class TestBase {
	public WebDriver driver;
	public Reporter reporter;
	public ExcelUtil reader;
	public SoftAssert sAssert;
	public String nodeURL;
	private static ThreadLocal<ExtentTest> parentTest = new ThreadLocal<ExtentTest>();
	private static ThreadLocal<ExtentTest> test = new ThreadLocal<ExtentTest>();

	private static String reportFile = "";

	public TestBase() {
		File folder = new File(getOutputFolder());
		folder.mkdirs();
	}

	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}

	public void setDriver(WebDriver d) {
		driver = d;
	}

	@BeforeSuite
	public void beforeSuite(ITestContext itc) {
		reportFile = getOutputFolder() + itc.getSuite().getName() + ".html";
		ExtentManager.createInstance(reportFile);
		ExtentTestManager.setReporter(ExtentManager.getInstance());
	}

	@BeforeTest
	public void beforeTest(ITestContext iTC) {
		CommonUtil.loadProps();
		reporter = new Reporter(iTC.getCurrentXmlTest().getName());
		reporter.createReport(iTC.getCurrentXmlTest().getName());
	}

	@BeforeClass
	public void beforeClass(ITestContext iTC) {
		ExtentTest parent = ExtentManager.getInstance()
				.createTest(iTC.getCurrentXmlTest().getName() + "-" + getClass().getSimpleName());
		parentTest.set(parent);

			reader = new ExcelUtil(ConstUtil.DATASHEET);
		// reader = new
		// ExcelUtil("src/test/resources/"+getClass().getPackage().getName()+"testdata.xlsx");

		String browser = CommonUtil.props.getProperty("browserName");
		if (browser.equalsIgnoreCase("chrome")) {
			String chromeDriverPath = System.getProperty("user.dir") + "/chromedriver.exe";
			System.setProperty("webdriver.chrome.driver", chromeDriverPath);
			
			driver = new ChromeDriver();
		}  
		else if (browser.equalsIgnoreCase("Headless")) {
			
			// To have HtmlUnitDriver working, selenium version need to be prior to 2.53
			// driver = new HtmlUnitDriver ();
			
		}
		else if (browser.equalsIgnoreCase("BSChrome")) {
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("browser", "chrome");
			capabilities.setCapability("browser_version", "62.0");
			capabilities.setCapability("os", "Windows");
			capabilities.setCapability("os_version", "10");
			capabilities.setCapability("resolution", "1024x768");
			System.out.println("Executing on BrowserStack");

			String BSUN = "vkumar14";
			String BSAK = "TghdmkwqsKtngbg82GzQ";
			String BSURL = "https://" + BSUN + ":" + BSAK + "@hub-cloud.browserstack.com/wd/hub";

			try {
				driver = new RemoteWebDriver(new URL(BSURL), capabilities);
			} catch (MalformedURLException e) {
				System.out.println("Caught excepton while creating BrowserStack driver instance:" + e);
				e.printStackTrace();
			}
			driver.get("https://google.co.in");
			System.out.println("BrowserStack session started.");

		} else if (browser.equalsIgnoreCase("Grid")) {
			nodeURL = "http://192.168.75.128:5555/wd/hub";
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("browser", "Chrome");
			capabilities.setCapability("browser_version", "62.0");
			capabilities.setCapability("os", "Windows");
			System.out.println("Executing on Grid");

			try {
				driver = new RemoteWebDriver(new URL(nodeURL), capabilities);
			} catch (MalformedURLException e) {
				System.out.println("Caught excepton while creating Grid driver instance:" + e);
				e.printStackTrace();          
			}
			driver.get("https://google.co.in");
			
		} 
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(25, TimeUnit.SECONDS);
	}

	@BeforeMethod
	public void beforeMethod(Method method) {
		driver.manage().deleteAllCookies();
		ExtentTest child = parentTest.get().createNode(method.getName());
		test.set(child);
		reporter.initlogger(child);
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			test.get().fail(result.getThrowable());
			reporter.log("TestScript failed: " + result.getMethod().getMethodName(), driver);
		} else if (result.getStatus() == ITestResult.SKIP)
			test.get().skip(result.getThrowable());
		else
			test.get().pass("Test passed");
		System.out.println("Completed executing test:" + result.getMethod().getMethodName());
	}

	@AfterClass
	public void afterClass() {
		driver.quit();
	}

	@AfterSuite
	public void afterSuite() {
		ExtentManager.getInstance().flush();
		CommonUtil cu = new CommonUtil();
		cu.sendEmail(ConstUtil.EMAIL_TO, ConstUtil.EMAIL_CC, cu.zipFile(reportFile, true));
		System.gc();
	}

	public void waitforSec(int milliSeconds) {
		try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected String getOutputFolder() {
		return System.getProperty("user.dir") + "/reports/";
	}


}
