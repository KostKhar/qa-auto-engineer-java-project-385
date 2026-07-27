package hexlet.code.components;

import hexlet.code.actions.ElementAction;
import hexlet.code.pages.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Header {
    private final ElementAction elementAction;
    private final WebDriver driver;
    private final By profileButton = By.xpath("//*[@aria-label='Profile']");
    private final By logoutButton = By.xpath("//*[@data-testid='PowerSettingsNewIcon']");

    public Header(WebDriver driver) {
        this.driver = driver;
        this.elementAction = new ElementAction(driver);
    }

    public LoginPage clickLogoutButton() {
        elementAction.find(profileButton).click();
        elementAction.find(logoutButton).click();
        return new LoginPage(driver);
    }
}
