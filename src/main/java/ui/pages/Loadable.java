package ui.pages;

import org.openqa.selenium.By;

public interface Loadable {
    //значок лоадера при загрузке страницы
    By loading = By.xpath("//div[@class='k-loading-image' or @class='loading-image']");

    boolean isLoad();

    void load();

}
