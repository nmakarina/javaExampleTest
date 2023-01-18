package ui.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import ui.Browser;
import ui.services.WaitConditions;

import java.util.List;

//какая-нибудь страница, отличная от главной страницы
public class AnyPage extends ParentPage {

    private final By title = By.xpath("//li[.='Название страницы']");

    private final By buttonFind = By.xpath("//button[@id='find']");
    private final By countrecordsLine = By.xpath("//span[@class='pager-info']");
    private final By exportExcelButtonWithButton = By.xpath("//button[@title='Экспорт в Excel']");
    private final By nextButtonXls = By.xpath("//button[@id='executeExport']");
    private final By dateMenuOpen = By.xpath("//ul[@id='_rpDateMenu']/li");
    
    public AnyPage(Browser browser) {
        super(browser);
        this.browser = browser;
        main = title;
    }

    @Step("Нажатие кнопки Найти")
    public AnyPage clickFind() {
        browser.waitFor(buttonFind, WaitConditions.clickable, 10);
        browser.click(buttonFind);
        load();
        browser.pause(1);
        return this;
    }

    @Step("Выбор даты загрузки - {0}")
    public AnyPage selectPeriod(String text) {
        browser.waitFor(dateMenuOpen, WaitConditions.clickable, 20);
        browser.click(dateMenuOpen);
        By locator = By.xpath("//span[.='" + text + "']");
        browser.waitFor(locator, WaitConditions.clickable, 20);
        browser.click(locator);
        browser.pause(1);
        //load();
        return this;
    }

    @Step("Выгрузка результатов в Excel - в формате {0}")
    public AnyPage exportExcelWithType(String type) {
        browser.waitFor(countrecordsLine, WaitConditions.clickable, 20);
        browser.pause(1);
        browser.click(exportExcelButtonWithButton);
        By locator = By.xpath("//span[contains(text(),'" + type + ")')]");
        browser.waitFor(locator, WaitConditions.clickable, 20);
        browser.click(locator);
        browser.pause(1);
        browser.waitFor(nextButtonXls, WaitConditions.clickable, 20);
        browser.click(nextButtonXls);
        browser.pause(1);
        return this;
    }

    @Step("Проверка - страница Документооборот загружена")
    public void verifyLoad(String url){
        load();
        saveTextLog(browser.currentUrl(), "Url страницы:");
        Assert.assertTrue(this.isLoad(), "Страница Документооборот не загружена");
    }

    @Step("Получение количества записей со страницы ")
    public String getCountRecords() {
        try {
            //load();
            browser.pause(2);
            By countrecordsLine = By.xpath("//*[contains(@class,'k-pager-info k-label')]");
            browser.waitFor(countrecordsLine, WaitConditions.exist, 5);
            List<WebElement> els = browser.findAll(countrecordsLine);
            String text = "";
            for (WebElement el:els) {
                if (el.isDisplayed()) {
                    text = el.getText();
                    break;
                }
            }
            String[] parts1 = text.split("из ");
            String[] parts2 = parts1[1].split(" запи");
            saveTextLog(parts2[0], "Общее количество записей на странице");
            return parts2[0];
        } catch (Exception e) {
            return "0";
        }
    }

}
