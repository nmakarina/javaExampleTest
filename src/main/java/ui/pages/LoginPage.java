package ui.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import ui.Browser;
import ui.services.WaitConditions;

//пример обработки страницы авторизации с 2х-факторной авторизацией с использованием ЭЦП
public class LoginPage extends ParentPage {
    private final By loginInput = By.xpath("//input[@id='username']");
    private final By passInput = By.xpath("//input[@id='password']");
    private final By logInButton = By.xpath("//input[@id='submit_btn'] | //button");
    private final By signButton = By.xpath("//button[text()='Выбрать и подписать']");


    public LoginPage(Browser browser) {
        super(browser);
        this.browser = browser;
        main = By.xpath("//*[@id='logon_form' or @class='login__title']");
    }


    public LoginPage insertLogin(String login) {
        browser.typeTextToInput(browser.findAll(loginInput).get(0), login);
        return this;
    }

    public LoginPage insertPass(String pass) {
        browser.typeTextToInput(passInput, pass);
        return this;
    }

    public boolean clickLogInButton() {
        browser.click(logInButton);
        By errorCert = By.xpath("//*[contains(text(),'ошибка') or contains(text(),'Ошибка')]");
            browser.waitForLoad();
            browser.waitFor(errorCert, WaitConditions.visible, 2);
            if (browser.isElementPresent(errorCert) && browser.isElementVisible(errorCert)) {
                return false;
            }
        return true;
    }

    public LoginPage selectSert(String name) {
        By titleCert = By.xpath("//span[text()='Выберите сертификат']");
        By selectCert = By.xpath("//div[contains(text(),'Сертификат')]");
        browser.waitFor(titleCert, WaitConditions.visible, 2);
        if (browser.isElementPresent(titleCert) && browser.isElementVisible(titleCert)) {
            browser.waitFor(selectCert, WaitConditions.clickable, 10);
            browser.click(selectCert);
            browser.pause(1);
        }
        return this;
    }

    public MainPage clickSignButton() {
        if (browser.isElementPresent(signButton) && browser.isElementVisible(signButton)) {
            browser.click(signButton);
        }
        return new MainPage(browser);
    }


    //Steps

    @Step("Переход на страницу {0}")
    public LoginPage goToPage(String page){
        browser.goToPage(page);
        load();
        return this;
    }

    @Step("Подпись при помощи сертификата - {0}")
    public MainPage signWith(String name) {
        selectSert(name);
        return clickSignButton();
    }

    //заглушка пока не работает двухфазная авторизация
    /**@Step("заглушка для двухфазной авторизации")
    public MainPage signWith(String name) {
        return new MainPage(browser);
    }**/

    //универсальный метод авторизации
    public MainPage auth(String url_home, String certificate, String login, String pass) {
        //авторизация через страницу логина
        goToPage(url_home);
        verifyLoad();
        loginWith(login, pass);
        return signWith(certificate);
    }


    @Step("Авторизация пользователя с параметрами: login - {0}, password - {1}")
    public LoginPage loginWith(String login, String pass) {
        for (int i=0;i<4;i++) {
            if (insertLogin(login).insertPass(pass).clickLogInButton()) {
                break;
            }
            //browser.pause(1);
            browser.refreshPage();
            load();
        }
            return new LoginPage(browser);
    }

    @Step("Проверка - страница авторизации загружена")
    public void verifyLoad(){
        Assert.assertTrue(this.isLoad(), "Страница авторизации не загружена");
    }
}
