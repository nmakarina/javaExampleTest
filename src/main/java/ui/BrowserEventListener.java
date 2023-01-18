package ui;

import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

public class BrowserEventListener extends AbstractWebDriverEventListener {

    /**@Override
    public void onException(Throwable throwable, WebDriver driver) {
        if (!throwable.getMessage().split("\n")[0].equals("actions")) {
            makeScreenShot("Exception", driver);
        }
    }

    public void makeScreenShot(String description, WebDriver driver) {
        new ScreenShot(driver, new Folder().createInProjectFolder().path()).make(description);
    }

    public void makeScreenShot(String description, WebDriver driver, String pathToFolder) {
        new ScreenShot(driver, new Folder().createIn(pathToFolder).path()).make(description);
    }**/
}
