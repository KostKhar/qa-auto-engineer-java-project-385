package hexlet.code.pages.tasks;

import hexlet.code.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TaskByIdForm extends BasePage {
    private final By title = By.xpath(
            "(//*[contains(@class,'MuiCardContent-root')]//span[contains(@class,'MuiTypography-body2')])[2]"
    );
    private final By description = By.xpath(
            "(//*[contains(@class,'MuiCardContent-root')]//span[contains(@class,'MuiTypography-body2')])[3]"
    );
    private final By assigneeField = By.xpath(
            "//*[contains(@class,'MuiCardContent-root')]//a[contains(@href,'#/users')]"
    );

    public TaskByIdForm(WebDriver driver) {
        super(driver);
    }

    public String getTitleText() {
        return elementAction().find(title).getText().trim();
    }

    public String getDescriptionText() {
        return elementAction().find(description).getText().trim();
    }

    public String getAssigneeText() {
        return elementAction().find(assigneeField).getText().trim();
    }

    public boolean isTitleVisible() {
        return elementAction().find(title).isEnabled();
    }

}
