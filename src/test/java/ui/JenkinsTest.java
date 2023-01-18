package ui;

import io.qameta.allure.Feature;
import meta.UITestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Listeners({UITestListener.class})
@Feature("Проверка размера экрана")
public class JenkinsTest extends BaseTest {

    @Test(description = "Проверка размера экрана при прогоне тестов на Jenkins")
    public void testSize() {
        browser.getDriver().manage().window().maximize();//setSize(new Dimension(1920, 1080));
        browser.goToPage("https://google.com");
        browser.pause(3);
        //browser.makeScreenShot("1");
        System.out.println(browser.getDriver().manage().window().getSize());
        //Проверяем, что размер экрана близок к 1920х1080 - при maximize() он может немного отличаться
        assertTrue(browser.getDriver().manage().window().getSize().getWidth()>=1910); //1920
        assertTrue(browser.getDriver().manage().window().getSize().getHeight()>=1050); //1080
    }

}
