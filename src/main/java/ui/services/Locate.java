package ui.services;

import org.openqa.selenium.By;

public final class Locate {

    private Locate() {

    }

    public static By parentByText(String text) {
        return By.xpath(String.join("", "//*[text()='", text, "']/.."));
    }

    public static By withText(String text) {
        return By.xpath(String.join("", "//*[text()='", text, "']"));
    }

    public static By withTitle(String title) {
        return withTagAndTitle("*", title);
    }

    public static By withTagAndTitle(String tag, String title) {
        return By.xpath(String.join("", "//", tag, "[@title='", title, "']"));
    }

    public static By divWithTitle(String title) {
        return withTagAndTitle("div", title);
    }

    public static By withTagAndText(String tag, String text) {
        return By.xpath(String.join("", "//", tag, "[text()='", text, "']"));
    }

    public static By imageWithAlt(String alt) {
        return By.xpath(String.join("", "//img[@alt='", alt, "']"));
    }

    public static By containingText(String text) {
        return By.xpath(String.join("", "//*[contains(text(),'", text, "')]"));
    }

    public static By withValue(String value) {
        return By.cssSelector(String.join("", "*[value='", value, "']"));
    }

    public static By inputWithValue(String value) {
        return By.cssSelector(String.join("", "input[value='", value, "']"));
    }
}
