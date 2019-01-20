package servicensw.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;

import utils.reportmanager.Reporter;

public class FindAServiceNSWLocationPage {

	// Page locators
	private By pageLabel = By.xpath("//div[@class='container']/h1");
	private By searchTextbox = By.id("locatorTextSearch");
	private By searchButton = By.xpath("//button[contains(@class,'search button')]");
	
	// Functions 

	private Reporter reporter;
	private WebDriver driver;
	private WebDriverWait wait;

	public FindAServiceNSWLocationPage(WebDriver driver, Reporter reporter) {
		this.driver = driver;
		this.reporter = reporter;
		wait = new WebDriverWait(driver, 30);
	}


	// Function to verify Find A Service NSW Location page is loaded correctly
	public void VerifyFindAServiceNSWLocationPageLoad() {

		wait.until(ExpectedConditions.presenceOfElementLocated(pageLabel));

		if (driver.getTitle().equals("Find a Service NSW location | Service NSW"))
			reporter.log(Status.PASS, "'Find a Service NSW location' Page shows up Successfully");
		else
			reporter.log("'Find a Service NSW location' Page does NOT show up. Please investigate", driver);
	}	


	public void setSearchLocationText(String searchText) {
		wait.until(ExpectedConditions.presenceOfElementLocated(searchTextbox));
		driver.findElement(searchTextbox).clear();
		driver.findElement(searchTextbox).sendKeys(searchText);
		reporter.log(Status.INFO, searchText + " - is entered onto location textbox");
	}

	public void clickSearchButton() {
		wait.until(ExpectedConditions.presenceOfElementLocated(searchButton));
		//driver.findElement(searchButton).click();
		driver.findElement(searchTextbox).sendKeys(Keys.ENTER);
		
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}
	
	// Function to verify desired service centre is present
	public void verifyServiceCentrePresence(String serviceCentre) {
		
		WebElement resultListPane = driver.findElement(By.xpath("//div[@class='locator__results-list']"));
		List<WebElement> serviceCentreList = resultListPane.findElements(By.tagName("a"));
		boolean found = false;
		System.out.println(serviceCentreList.size());
		
		for(int i=0; i<serviceCentreList.size(); i++)
		{
			System.out.println(serviceCentreList.get(i).getText());
			if(serviceCentre.equals(serviceCentreList.get(i).getText()))
			{
				reporter.log(Status.PASS, serviceCentre + " - is found Successfully for the given location");
				found = true;
				break;
			}
		}
		
		if(!found)
			reporter.log(serviceCentre + " - is NOT found for the given location", driver);
		
	}
	

}
