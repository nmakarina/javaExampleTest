package ui.pages;

import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import ui.Browser;
import ui.services.Folder;
import ui.services.WaitConditions;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public abstract class ParentPage implements Loadable {
    protected Browser browser;
    protected By main;
    protected SoftAssert softAssert;

    public ParentPage(Browser browser) {
        this.browser = browser;
        softAssert = new SoftAssert();
    }

    private boolean findFilesWithEnds(String directory, String end) {
        browser.pause(1);
        File dir = new File(directory);
        int tm = 0;
        File[] files = dir.listFiles((dir1, name) -> name.toUpperCase().endsWith(end.toUpperCase()));
        boolean flag = files.length > 0;
        while (!flag && tm < 60) {
            browser.pause(2);
            tm += 2;
            files = dir.listFiles((dir1, name) -> name.toUpperCase().endsWith(end.toUpperCase()));
            flag = files.length > 0;
        }
        /**for (File f : files)
         {
         saveTextLog(f.getAbsolutePath(),"Найден файл "+f.getName());
         }**/
        browser.pause(1);
        return flag;
    }

    @Step("Проверка - файл с расширением '{1}' успешно загружен")
    public void verifyFileWithEndDownload(String path, String end) {
        Assert.assertTrue(this.findFilesWithEnds(path, end), "Файл с расширением '" + end + "' не загружен\n");
    }

    //ждем загрузки файла
    private boolean findDownloadFile(String name) {
        int tm = 0;
        boolean flag = Folder.findFile(name);
        while (!flag && tm < 299) {
            browser.pause(2);
            tm += 2;
            flag = Folder.findFile(name);
        }
        browser.pause(1);
        return flag;
    }


    @Step("Получение списка файлов в каталоге")
    public List<File> getFilesIndirectory(String directory, String end) {
        File dir = new File(directory);
        File[] files = dir.listFiles((dir1, name) -> name.toUpperCase().endsWith(end.toUpperCase()));
        List<File> list = Arrays.asList(files);

        return list;
    }

    @Override
    public boolean isLoad() {
        return browser.isElementPresent(main) && browser.isElementVisible(main);
    }

    public void waitForUnexpectedAlert(int time) {
        int i = 0;
        while (i++ < time) {
            try {
                Alert alert = browser.switchToAlert();
                String text = browser.getAlertText(alert);
                saveTextLog(text, "Обнаружено сообщение об ошибке: " + text);
                browser.alertAccept(alert);
                break;
            } catch (NoAlertPresentException e) {
                browser.pause(1);
                continue;
            }
        }
    }

    @Override
    public void load() {
        browser.waitForLoad();
        browser.waitFor(main, WaitConditions.visible, 30);
    }

    public String getCurrentURL() {
        return browser.currentUrl();
    }

    @Attachment(value = "{1}", type = "text/plain")
    protected String saveTextLog(String message, String value) {
        return message;
    }

    @Attachment(value = "Page screenshot", type = "image/png")
    public byte[] saveScreenshotPNG(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    @Step("Проверка - каталог '{0}' успешно создан")
    public void createDir(String filePath) {
        boolean flag = Folder.createFolder(filePath);
        browser.pause(1);
        Assert.assertTrue(flag, "Каталог '" + filePath + "' не создан\n");
    }

    @Step("Проверка - загруженные файлы успешно перемещены в каталог {0}")
    public void renameAllFileTo(String newDir, String oldPath) {
        //browser.pause(1);
        boolean flag = Folder.renameAll(oldPath, newDir);
        browser.pause(1);
        Assert.assertTrue(flag, "Файлы не перемещены в каталог " + newDir);
    }

    @Step("Проверка - сравнение количества записей поиска({0}) и количества записей из XLS файла({1})")
    public void verifyRecordsCount(String countUI, String countXLS) {
        Assert.assertEquals(countUI, countXLS, "Количество записей не совпадает");
    }

    @Step("Результаты прогона теста")
    public void assertAll() {
        softAssert.assertAll();
    }

    @Step("Zoom = {0}")
    public void zoomPge(String zoom) {
        browser.openNewTab();
        browser.waitForNewTabAndSwitchToIt(10);
        browser.getDriver().get("chrome://settings/");
        JavascriptExecutor js = (JavascriptExecutor) browser.getDriver();
        js.executeScript("chrome.settingsPrivate.setDefaultZoom(" + zoom + ");");
        browser.pause(1);
        browser.closeTab();
        browser.switchToMainTab();
        load();
    }
}
