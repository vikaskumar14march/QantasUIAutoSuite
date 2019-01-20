package servicensw.pages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;

import utils.reportmanager.Reporter;

public class HomePage {

	// Page locators
	private By searchTextbox = By.id("edit-contains");
	private By searchButton = By.id("edit-submit-site-search");
	private By locateUsButton = By.xpath("//a[contains(text(),'Locate us')]");


	// Functions 

	private Reporter reporter;
	private WebDriver driver;
	private WebDriverWait wait;

	public HomePage(WebDriver driver, Reporter reporter) {
		this.driver = driver;
		this.reporter = reporter;
		wait = new WebDriverWait(driver, 30);
	}


	public void setSearchText(String searchText) {
		wait.until(ExpectedConditions.presenceOfElementLocated(searchTextbox));
		driver.findElement(searchTextbox).sendKeys(searchText);
	}

	public void clickSearchButton() {
		wait.until(ExpectedConditions.presenceOfElementLocated(searchButton));
		driver.findElement(searchButton).click();
	}

	public void clickLocateUsButton() {
		wait.until(ExpectedConditions.presenceOfElementLocated(locateUsButton));
		driver.findElement(locateUsButton).click();
	}


	// Function to verify Home page is loaded correctly
	public void VerifyHomePageLoad() {

		wait.until(ExpectedConditions.presenceOfElementLocated(searchButton));

		if (driver.getTitle().equals("Home | Service NSW"))
			reporter.log(Status.PASS, "'Home' Page shows up Successfully");
		else
			reporter.log("'Home' Page does NOT show up. Please investigate", driver);
	}	

	// Function to launch desired portal
	public void launchPage(String url) {
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.get(url);

	}


}
