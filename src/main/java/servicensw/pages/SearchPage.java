package servicensw.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;

import utils.reportmanager.Reporter;

public class SearchPage {

	// Page locators
	private By searchResultLabel = By.xpath("//div[@class='pane-content']/h1");
	private By nextLink = By.xpath("//a[contains(text(),'next')]");

	
	// Functions 

	private Reporter reporter;
	private WebDriver driver;
	private WebDriverWait wait;

	public SearchPage(WebDriver driver, Reporter reporter) {
		this.driver = driver;
		this.reporter = reporter;
		wait = new WebDriverWait(driver, 30);
	}

	// Function to verify Search page is loaded correctly
	public void VerifySearchPageLoad() {

		wait.until(ExpectedConditions.presenceOfElementLocated(searchResultLabel));

		if (driver.getTitle().equals("Search Results | Service NSW"))
			reporter.log(Status.PASS, "'Search Results' Page shows up Successfully");
		else
			reporter.log("'Search Results' Page does NOT show up. Please investigate", driver);
	}	

	// Function to search desired text and find the page from the search result list and click on it
	public void clickDesiredSearchResultLink(String searchText) throws InterruptedException {
		
		WebElement totalPagesPane = driver.findElement(By.xpath("//ul[@class='pager']"));
		List<WebElement> numberOfPages = totalPagesPane.findElements(By.tagName("a"));
		System.out.println(numberOfPages.size());

		WebElement resultLinkPane;
		List<WebElement> resultLinks;
		boolean exitFlag = false;
		int i = 0;
		
		do
		{
			// Code to check the desired link and click on it
			 resultLinkPane = driver.findElement(By.xpath("//div[@class='view-content']"));
			 resultLinks = resultLinkPane.findElements(By.tagName("a"));
				for(int j=0;j<resultLinks.size();j++)
				{
					System.out.println(resultLinks.get(j).getText());
					if(searchText.equals(resultLinks.get(j).getText()))
					{
						resultLinks.get(j).click();
						reporter.log(Status.PASS, "Page with searach text '"+ searchText + "' is found and clicked Successfully");
						exitFlag = true;
						break;
					}
				}
			
			if(exitFlag)
				break;
			
			// Iterate through all result pages till desired search result page is found
			driver.findElement(nextLink).click();
			Thread.sleep(2000);
			i++;
		}
		while(i<(numberOfPages.size()-1));
		
	}
	



}
