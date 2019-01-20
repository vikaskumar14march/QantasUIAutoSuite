package utils.reportmanager;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class Reporter {
	private ExtentReports extent;
	private ExtentTest logger;
	private int screenshotnum = 1;
	private String testName;

	public Reporter(String testName) {
		this.testName = testName;

	}

	
	public void createReport(String testName) {

		extent = ExtentManager.getInstance();
	}

	public void initlogger(ExtentTest logger) {
		this.logger = logger;
	}

	public void log(Status status, String msg) {
		if (status == Status.PASS) {
			logger.log(Status.PASS, MarkupHelper.createLabel(msg, ExtentColor.GREEN));
			System.out.println(msg);
		} else if (status == Status.SKIP) {
			logger.log(Status.SKIP, MarkupHelper.createLabel(msg, ExtentColor.ORANGE));
			System.out.println("SKIPPED - " + msg);
		} else if (status == Status.ERROR) {
			logger.log(status, MarkupHelper.createLabel(msg, ExtentColor.RED));
			System.err.println(msg);
		} else if (status == Status.INFO) {
			logger.log(Status.INFO, MarkupHelper.createLabel(msg, ExtentColor.WHITE));
			System.out.println(msg);
		}
	}

	public void log(String msg, WebDriver driver) {
		try {
			logger.log(Status.FAIL, msg,
					MediaEntityBuilder.createScreenCaptureFromPath(this.captureScreen(driver)).build());
			System.err.println(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void logWithScreenshot(String msg, WebDriver driver) {
		try {
			logger.log(Status.INFO, msg,
					MediaEntityBuilder.createScreenCaptureFromPath(this.captureScreen(driver)).build());
			System.out.println(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String captureScreen(WebDriver driver) {
		String filePath = "screenshots/" + this.testName + "-" + screenshotnum + ".jpg";
		try {
			TakesScreenshot sshot = ((TakesScreenshot) driver);
			File dest = new File(System.getProperty("user.dir") + "/Reports/" + filePath);
			if (!dest.exists()) {
				dest.getParentFile().mkdirs();
				dest.createNewFile();
			}
			FileUtils.copyFile(sshot.getScreenshotAs(OutputType.FILE), dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		screenshotnum += 1;
		return filePath;
	}

	public void close() {
		extent.flush();
	}
}
