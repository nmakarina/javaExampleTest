package ui;

import io.qameta.allure.Attachment;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.net.UrlChecker;
import org.openqa.selenium.support.ThreadGuard;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import ui.pages.Loadable;
import ui.services.Message;
import ui.services.WaitConditions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class Browser {
    private static final By IFRAMES = By.xpath("//iframe");
    private final EventFiringWebDriver driver;
    private final BrowserEventListener listener;
    private Actions actions;
    private int commonTimeOutInSeconds = 3;
    private String mainHandle;

    public Browser(WebDriver driver) {
        this(driver, 0);
    }

    public Browser(WebDriver driver, int commonTimeOutInSeconds) {
        this.driver = new EventFiringWebDriver(/**threadSafeDriver(**/driver);
        listener = new BrowserEventListener();
        this.driver.register(listener);
        setCommonTimeOut(commonTimeOutInSeconds);
        mainHandle = currentHandle();
        actions = new Actions(driver);
    }

    public void setCommonTimeOut(int timeOutInSeconds) {
        commonTimeOutInSeconds = timeOutInSeconds < 0 ? 0 : timeOutInSeconds;
        driver.manage().timeouts().implicitlyWait(commonTimeOutInSeconds, TimeUnit.SECONDS);
    }

    public List<String> browserLogs() {
        return browserLogEntries().stream().map(LogEntry::toString).collect(Collectors.toList());
    }

    public List<LogEntry> browserLogEntries() {
        return driver.manage().logs().get("browser").getAll();
    }

    public List<LogEntry> consoleErrors() {
        return browserLogEntries().stream().filter(n -> n.getLevel().equals(Level.SEVERE)).collect(Collectors.toList());
    }

    public Map<String, ?> browserCapabilities() {
        Map<String, ?> mapOfCapabilities = new HashMap<>();
        try {
            HasCapabilities capabilities = (HasCapabilities) driver.getWrappedDriver();
            mapOfCapabilities = capabilities.getCapabilities().asMap();
        } catch (Exception e) {
            System.out.println("Error while requesting the capabilities!");
            printErrorMessageFirstLine(e);
        }
        return mapOfCapabilities;
    }

    public void scrollTo(WebElement element) {
        ((Locatable) element).getCoordinates().inViewPort();
    }

    public void scroll(int x, int y) {
        useScript("window.scrollBy(" + x + "," + y + ");");
    }

    public void goToPage(String pageUrl) {
        pageUrl = formUrl(pageUrl);
        driver.navigate().to(pageUrl);
    }

    private String formUrl(String pageUrl) {
        if (pageUrl.startsWith("http") || (pageUrl.startsWith("file"))) {
            return pageUrl;
        }
        if (isUrlAvailable("http://" + pageUrl)) {
            return "http://" + pageUrl;
        }
        if (isUrlAvailable("https://" + pageUrl)) {
            return "https://" + pageUrl;
        }
        return pageUrl;
    }

    public void refreshPage() {
        driver.navigate().refresh();
    }

    public void backToPreviousPage() {
        driver.navigate().back();
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    public String pageTitle() {
        return driver.getTitle();
    }

    public Set<String> windowHandles() {
        return driver.getWindowHandles();
    }

    public WebElement find(By locator) {
        return driver.findElement(locator);
    }

    public WebElement findVisibleElement(By locator) {
        List<WebElement> els = findAll(locator);
        for (WebElement el : els) {
            if (el.isDisplayed()) {
                return el;
            }
        }
        return null;
    }

    public WebElement findUnderElement(WebElement element, By locator) {
        return element.findElement(locator);
    }

    public int iFramesCount() {
        return findAll(IFRAMES).size();
    }

    public boolean isIFramesOnPage() {
        return iFramesCount() > 0;
    }

    public List<WebElement> findAll(By locator) {
        return driver.findElements(locator);
    }

    public List<WebElement> findAllUnderElement(WebElement element, By locator) {
        return element.findElements(locator);
    }

    public boolean isElementPresent(By locator) {
        int timeOut = commonTimeOutInSeconds;
        //setCommonTimeOut(0);
        try {
            return findAll(locator).size() > 0;
        } finally {
            setCommonTimeOut(timeOut);
        }
    }

    public boolean isElementPresentUnderElement(WebElement element, By locator) {
        int timeOut = commonTimeOutInSeconds;
        setCommonTimeOut(0);
        try {
            return findAllUnderElement(element, locator).size() > 0;
        } finally {
            setCommonTimeOut(timeOut);
        }
    }

    public boolean isElementVisible(By locator) {
        return find(locator).isDisplayed();
    }

    public String elementAttribute(By locator, String attribute) {
        return find(locator).getAttribute(attribute);
    }

    public String cssValue(By locator, String value) {
        return find(locator).getCssValue(value);
    }

    public void maximize() {
        driver.manage().window().maximize();
    }

    public void switchToFrame() {
        waitFor(IFRAMES, WaitConditions.exist, 20);
        driver.switchTo().frame(0);
    }

    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    public boolean waitFor(By locator, WaitConditions conditions, int time) {
        WebDriverWait wait = new WebDriverWait(driver, time);
        try {
            wait.until(conditions.getType().apply(locator));
            return true;
        } catch (TimeoutException ex) {
            //makeScreenShot("WaitingFail");
            System.out.println("Waiting time has expired " + locator.toString());
            return false;
        }
    }

    public boolean typeTextToInput(By locator, CharSequence text) {
        return typeTextToInput(find(locator), text);
    }

    public boolean typeTextToInput(WebElement webElement, CharSequence text) {
        try {
            webElement.clear();
            webElement.sendKeys(text);
            return true;
        } catch (Exception e) {
            //makeScreenShot("InputFail");
            printErrorMessageFirstLine(e);
            return false;
        }
    }

    public boolean pressButtons(CharSequence sequence) {
        try {
            actions.sendKeys(sequence).perform();
            return true;
        } catch (Exception e) {
            //makeScreenShot("ErrorWhilePressButton");
            printErrorMessageFirstLine(e);
            return false;
        }
    }

    public boolean click(By locator) {
        return click(find(locator));
    }

    public boolean click(WebElement element) {
        try {
            element.click();
            return true;
        } catch (Exception e) {
            //makeScreenShot("CantClickElement");
            printErrorMessageFirstLine(e);
            return false;
        }
    }

    public boolean doubleClick(WebElement element) {
        return useAction(element, "double");
    }

    public boolean contextClick(WebElement element) {
        return useAction(element, "context");
    }

    public boolean clickWithOffset(By locator){
        try {
            int w = find(locator).getSize().getWidth();
            int h = find(locator).getSize().getHeight();
            Actions act = new Actions(getDriver());
            act.moveToElement(find(locator), w / 2, h / 4).click().build().perform();
            return true;
        } catch(Exception e){
            printErrorMessageFirstLine(e);
            return false;
        }
    }

    public boolean clickTooltip() {
        By customTooltip = By.xpath("//ui-tooltip");
        if (isElementPresent(customTooltip)) {
            click(customTooltip);
            return true;
        }
        return false;
    }

    private boolean useAction(WebElement element, String type) {
        actions = type.equals("double") ? actions.doubleClick(element) : actions.contextClick(element);
        try {
            actions.perform();
            return true;
        } catch (WebDriverException e) {
            //makeScreenShot("ActionError");
            printErrorMessageFirstLine(e);
            return false;
        }
    }

    public boolean hoverElement(WebElement element) {
        try {
            actions.moveToElement(element).perform();
            return true;
        } catch (WebDriverException e) {
            printErrorMessageFirstLine(e);
            return false;
        }
    }

    public boolean drugNDrop(WebElement fromElement, WebElement toElement) {
        try {
            actions.dragAndDrop(fromElement,toElement).perform();
            return true;
        } catch (WebDriverException e) {
            printErrorMessageFirstLine(e);
            return false;
        }
    }

    public boolean drugNDrop(By fromElement, By toElement) {
        try {
            actions.dragAndDrop(find(fromElement),find(toElement)).perform();
            return true;
        } catch (WebDriverException e) {
            printErrorMessageFirstLine(e);
            return false;
        }
    }

    public boolean hoverElement(By locator) {
        return hoverElement(find(locator));
    }

    public Optional<Object> useScript(String script) throws WebDriverException {
        return Optional.ofNullable(driver.executeScript(script));
    }

    public void pause(int timeInSeconds) {
        new Actions(driver).pause(timeInSeconds * 1000).perform();
//        int timeOut = timeInSeconds < 1 ? 1 : timeInSeconds;
//        try {
//            Sleeper.SYSTEM_SLEEPER.sleep(Duration.ofSeconds(timeOut));
//        } catch (InterruptedException ignored) {
//
//        }
    }

    public boolean openNewTab() {
        useScript("window.open()");
        return windowHandles().size() > 1;
    }

    public boolean waitForNewTabAndSwitchToIt(int timeInSeconds) {
        long time = timeInSeconds <= 0 ? 1000 : timeInSeconds * 1000;
        long startTime = System.currentTimeMillis();
        mainHandle = currentHandle();
        while (System.currentTimeMillis() - startTime < time) {
            if (windowHandles().size() > 1) {
                for(String winHandle : driver.getWindowHandles()){
                    if (!winHandle.equals(mainHandle)) {
                        driver.switchTo().window(winHandle);
                    }
                }
                //Optional<String> newHandle = windowHandles().stream().filter(n -> !n.equals(mainHandle)).findFirst();
                //driver.switchTo().window(newHandle.get());
                break;
            }
        }
        return !currentHandle().equals(mainHandle);
    }

    public Set<String> getAllHandles() {
        Set<String> hds=driver.getWindowHandles();
        return hds;
    }

    public boolean waitForNewTab3AndSwitchToIt(int timeInSeconds, Set<String> hds) {
         long time = timeInSeconds <= 0 ? 1000 : timeInSeconds * 1000;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < time) {
            if (windowHandles().size() > 1) {
                for(String winHandle : driver.getWindowHandles()){
                    if (!hds.contains(winHandle)) {
                        driver.switchTo().window(winHandle);
                    }
                }
                break;
            }
        }
        return !hds.contains(currentHandle());
    }

    public void switchToMainTab() {
        driver.switchTo().window(mainHandle);
    }

    public void setCurrentTabAsMain() {
        mainHandle = currentHandle();
    }

    private String currentHandle() {
        return driver.getWindowHandle();
    }

    public boolean openNewTab(String url) {
        if (!openNewTab() || !waitForNewTabAndSwitchToIt(1)) {
            return false;
        }
        goToPage(url);
        return true;
    }

    public void closeTab() {
        if (windowHandles().size() == 1) {
            quit();
        } else {
            driver.close();
        }
    }

    public void quit() {
        if (driver != null) {
            driver.quit();
        }
    }

    public boolean isUrlAvailable(String url) {
        return isUrlAvailable(url, 1);
    }

    public boolean isUrlAvailable(String url, int timeOutInSeconds) {
        timeOutInSeconds = timeOutInSeconds < 1 ? 1 : timeOutInSeconds;
        try {
            new UrlChecker().waitUntilAvailable(timeOutInSeconds, TimeUnit.SECONDS, new URL(url));
            return true;
        } catch (UrlChecker.TimeoutException e) {
            return false;
        } catch (MalformedURLException e) {
            printErrorMessageFirstLine(e);
            return false;
        }
    }

    private static void printErrorMessageFirstLine(Exception e) {
        System.err.println(new Message().exceptionFirstLine(e));
    }

    //public void makeScreenShot(String description) {
    //    listener.makeScreenShot(description, driver);
    //}

    public boolean isJQueryOnCurrentPage() {
        try {
            return useScript("return window.jQuery.active").isPresent();
        } catch (WebDriverException e) {
            return false;
        }
    }

    public void waitForJQueryEnds() {
        int i = 0;
        while (i++ < 60) {
            boolean complete = true;
            try {
                Alert alert = switchToAlert();
                String text = getAlertText(alert);
                alertAccept(alert);
                Assert.assertTrue(false, "Обнаружено сообщение об ошибке: " + text);
                break;
            } catch (org.openqa.selenium.NoAlertPresentException e) {
            }
            try {
                complete = (Boolean) driver.executeScript("var result = true; try { result = (typeof jQuery != 'undefined') ? jQuery.active == 0 : true } catch (e) {}; return result;");
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return;
            }
            if (complete) break;
            pause(1);
        }
    }

    public boolean waitForLoad() {
        waitForJQueryEnds();
        return waitFor(Loadable.loading, WaitConditions.invisible, 180);
    }

    private WebDriver threadSafeDriver(WebDriver webDriver) {
        return ThreadGuard.protect(webDriver);
    }

    public String text(By by) {
        return find(by).getText();
    }

    public String value(By by) {
        return find(by).getAttribute("value");
    }

    public void scrollTop(By scrl) {
        WebElement scroll = driver.findElement(scrl);
        Actions actions = new Actions(driver);
        actions.moveToElement(scroll);
        actions.click();
        actions.sendKeys(Keys.PAGE_UP);
        actions.build().perform();
    }

    public void scrollDown(By scrl, By element) {
        boolean flag = driver.findElement(element).isDisplayed();
        int iflag = 0;
        while (!flag && iflag < 500) {
            WebElement scroll = driver.findElement(scrl);
            Actions actions = new Actions(driver);
            actions.moveToElement(scroll);
            actions.click();
            actions.sendKeys(Keys.DOWN);
            actions.build().perform();
            flag = driver.findElement(element).isDisplayed();
            iflag++;
        }
        if (iflag > 0) {
            pause(1);
        }
    }


    public void scrollDownByKeyDown(By element, int times) {
        actions.sendKeys(Keys.DOWN);
        int iflag = 0;
        while (iflag < times) {
            Actions actions = new Actions(driver);
            //actions.click();
            actions.sendKeys(Keys.DOWN);
            actions.build().perform();
            iflag++;
        }
        if (iflag > 0) {
            pause(1);
        }
    }

    //долгий клик - 1 сек между нажатием и отпусканием клавиши
    public void actionLongClick(By element) {
        Actions actions = new Actions(driver);
        WebElement el = driver.findElement(element);
        actions.moveToElement(el);
        actions.clickAndHold();
        pause(1);
        actions.release();
        actions.build().perform();

    }

    //долгий клик - 1 сек между нажатием и отпусканием клавиши
    public void actionLongClickElement(WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element);
        actions.clickAndHold();
        pause(1);
        actions.release();
        actions.build().perform();

    }


    public void delAllText(By element) {
        Actions actions = new Actions(driver);
        WebElement el = driver.findElement(element);
        actions.click(el);
        actions.keyDown(Keys.CONTROL);
        //"a"
        actions.sendKeys(String.valueOf('\u0061'));
        actions.keyUp(Keys.CONTROL);
        actions.sendKeys(Keys.BACK_SPACE);
        actions.build().perform();

    }

    public EventFiringWebDriver getDriver() {
        return driver;
    }

    public Alert switchToAlert() {
        return driver.switchTo().alert();
    }

    public String getAlertText(Alert alert) {
        return alert.getText();
    }

    public void alertAccept(Alert alert) {
        alert.accept();
    }

    public void alertDismis(Alert alert) {
        alert.dismiss();
    }

    public void acceptAllAlerts() {
        try {
            switchToAlert().accept();
        } catch (Exception ignore) {
            // no alerts presents
        }
    }

    public void enterToAlert(String alertText) {
        int i = 0;
        while (i++ < 4) {
            boolean complete = false;
            try {
                Alert alert = switchToAlert();
                String text = getAlertText(alert);
                alert.sendKeys(alertText);
                complete = true;
                alertAccept(alert);
                Assert.assertTrue(true, "Обнаружено окно: " + text);
                break;
            } catch (org.openqa.selenium.NoAlertPresentException e) {
            }

            if (complete) break;
            pause(1);
        }
    }

    public void selectSomeElements(List<WebElement> elements){
        Actions actions = new Actions(driver);
        actions.keyDown(Keys.LEFT_CONTROL);
        elements.forEach(actions::click);
        actions.keyUp(Keys.LEFT_CONTROL).build().perform();
    }

    public void moveByOffset(int x, int y){
        new Actions(driver).moveByOffset(x,y)/**moveToElement(element)**/.perform();
    }

    @Attachment(value = "Page screenshot", type = "image/png")
    public byte[] saveScreenshotPNG() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
