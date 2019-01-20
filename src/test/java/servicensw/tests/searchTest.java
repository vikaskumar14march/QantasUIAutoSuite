package servicensw.tests;


import java.util.Properties;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import servicensw.pages.ApplyForANumberPlatePage;
import servicensw.pages.FindAServiceNSWLocationPage;
import servicensw.pages.HomePage;
import servicensw.pages.SearchPage;
import utils.ConstUtil;
import utils.TestBase;

public class searchTest extends TestBase {
	
	Properties props = new Properties();
	String mName = null;
	int rowNumber;
	String baseURL ;

	// Search functionality and locating Service centre
	@Test(priority = 1,dataProvider="inputs")
	private void searchLocateTest(String location, String serviceCentre) throws InterruptedException {

		baseURL = ConstUtil.PRODURL;
		String searchText = "Apply for a number plate";
		
		HomePage Homepage = new HomePage(driver, reporter);
		SearchPage SearchPage = new SearchPage(driver, reporter);
		ApplyForANumberPlatePage ApplyForANumberPlatePage = new ApplyForANumberPlatePage(driver, reporter);
		FindAServiceNSWLocationPage FindAServiceNSWLocationPage = new FindAServiceNSWLocationPage(driver, reporter);
		
		// Launch Service NSW portal
		Homepage.launchPage(baseURL);
		Homepage.VerifyHomePageLoad();
		
		// Set search text and click Search button
		Homepage.setSearchText(searchText);
		Homepage.clickSearchButton();
		
		// Verify search result page
		SearchPage.VerifySearchPageLoad();
		
		// click on desired result link on search result page 
		SearchPage.clickDesiredSearchResultLink(searchText);
		
		// Verify Apply For A Number Plate Page
		ApplyForANumberPlatePage.VerifyApplyForANumberPlatePageLoad();
		
		// Click on Locate Us button on homepage
		Homepage.clickLocateUsButton();
		
		// Verify 'Find a Service NSW Location' page opens
		FindAServiceNSWLocationPage.VerifyFindAServiceNSWLocationPageLoad();
		
		// Search with location and verify desired service centre shows up in list
		FindAServiceNSWLocationPage.setSearchLocationText(location);
		FindAServiceNSWLocationPage.clickSearchButton();
		waitforSec(2000);
		FindAServiceNSWLocationPage.verifyServiceCentrePresence(serviceCentre);
		
		
	}

	
	@DataProvider(name="inputs")
	public Object[][] getData() {
		return new Object[][] {
			{"Sydney 2000", "Marrickville Service Centre"},
			{"Sydney Domestic Airport 2020", "Rockdale Service Centre"}
		};
	}

	
}


