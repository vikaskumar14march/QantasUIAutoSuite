package servicensw.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;

import utils.reportmanager.Reporter;

public class ApplyForANumberPlatePage {

	// Page locators
	private By pageLabel = By.xpath("//div[@class='pane-content']/h1");
	
	
	// Functions 

	private Reporter reporter;
	private WebDriver driver;
	private WebDriverWait wait;

	public ApplyForANumberPlatePage(WebDriver driver, Reporter reporter) {
		this.driver = driver;
		this.reporter = reporter;
		wait = new WebDriverWait(driver, 30);
	}


	// Function to verify Apply for a number plate page is loaded correctly
	public void VerifyApplyForANumberPlatePageLoad() {

		wait.until(ExpectedConditions.presenceOfElementLocated(pageLabel));

		if (driver.getTitle().equals("Apply for a number plate | Service NSW"))
			reporter.log(Status.PASS, "'Apply for a number plate' Page shows up Successfully");
		else
			reporter.log("'Apply for a number plate' Page does NOT show up. Please investigate", driver);
	}	


}
