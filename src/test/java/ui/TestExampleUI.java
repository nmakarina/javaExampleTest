package ui;


import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import meta.UITestListener;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import ui.pages.AnyPage;
import ui.pages.LoginPage;
import ui.pages.MainPage;


@Listeners({UITestListener.class})
@Feature("Проверка выгрузки со страницы 'any page'")
public class TestExampleUI extends BaseTest {

    @Test(description = "Выгрузка в Excel - с начала года", alwaysRun = true)
    @Description(value = "Выгрузка в Excel - с начала года")
    public void testExportExcel() {
        LoginPage loginPage = new LoginPage(browser);
        MainPage page = loginPage.auth(url_home, SERT_NAME, LOGIN_NAME, PASS);
        page.verifyLoad(url_home);

        AnyPage anyPage = page.openAnyLink();
        anyPage.verifyLoad(url_home);

        anyPage.selectPeriod("С начала года").clickFind();
        //количество записей со страницы
        String resUI = anyPage.getCountRecords();

        anyPage.exportExcelWithType("xls");
        anyPage.verifyFileWithEndDownload(downloadFilepath, "xls");

    }
}

