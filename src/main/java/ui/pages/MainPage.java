package ui.pages;

import org.openqa.selenium.By;
import ui.Browser;

import io.qameta.allure.Step;
import org.testng.Assert;
import ui.services.WaitConditions;

public final class MainPage extends ParentPage {

    //какая-нибудь ссылка на главной странице
    private static By openAnyLink = By.xpath("//a[contains(@href,'/anyUrl')]");


    public MainPage(Browser browser) {
        super(browser);
        this.browser = browser;
        //локатор, характерный для главной страницы
        main = By.xpath("//div[@class='menu']");
    }

    @Step("Проверка - главная страница загружена")
    public void verifyLoad(String url) {
        load();
        Assert.assertTrue(this.isLoad(), "Главная страница не загружена");
    }
    //Steps

    @Step("Переход на страницу {0}")
    public MainPage goToPage(String page) {
        browser.goToPage(page);
        load();
        return this;
    }

    @Step("Переход по сссылке 'any link'")
    public AnyPage openAnyLink() {
        browser.waitFor(openAnyLink, WaitConditions.clickable, 10);
        browser.click(openAnyLink);
        return new AnyPage(browser);
    }

}
