package meta;

import io.qameta.allure.Attachment;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.*;
import ui.BaseTest;


/**
 * Слушатель UI тестов, обеспечивает запись в allure-отчет по событиям в тестах
 */
public class UITestListener extends BaseTest implements ITestListener {

    @Attachment(value = "Page screenshot", type = "image/png")
    public byte[] saveScreenshotPNG (WebDriver driver){
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    @Attachment(value = "{1}", type = "text/plain")
    public String saveTextLog (String message, String value){
        return message;
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {
        saveTextLog("test started", "Старт теста");
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        saveTextLog("test ended success", "Тест завершен успешно");
        Object testClass = iTestResult.getInstance();
        WebDriver driver = ((BaseTest)testClass).getDriver();
        saveScreenshotPNG(driver);
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        saveTextLog("test failure", "Ошибка");
        Object testClass = iTestResult.getInstance();
        WebDriver driver = ((BaseTest)testClass).getDriver();
        saveScreenshotPNG(driver);
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        saveTextLog("test skipped", "Тест пропущен");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        saveTextLog("test failured but witin success percentage", "test failured but witin success percentage");
        Object testClass = iTestResult.getInstance();
        WebDriver driver = ((BaseTest)testClass).getDriver();
        saveScreenshotPNG(driver);
    }

    @Override
    public void onStart(ITestContext iTestContext) {
        saveTextLog("test start", "Старт теста");
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        saveTextLog("test finish", "Окончание теста");
    }
}
