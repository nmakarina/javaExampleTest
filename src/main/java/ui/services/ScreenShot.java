package ui.services;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.FileHandler;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class ScreenShot {
    private final WebDriver webDriver;
    private String filePath = "";

    public ScreenShot(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public ScreenShot(WebDriver webDriver, String filePath) {
        this.webDriver = webDriver;
        this.filePath = filePath;
    }

    public void make(String problem) {
        File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        String fileName = String.join("", filePath, problem, "_", getTime(), ".png");
        try {
            new FileHandler().copy(scrFile, new File(fileName));
        } catch (IOException e) {
            System.out.println(String.join("","Error making screenshot:",fileName," Error-",e.getMessage()));
        }
    }

    private String getTime() {
        LocalTime time = LocalTime.now();
        return time.format(DateTimeFormatter.ofPattern("HH-mm"));
    }
}
