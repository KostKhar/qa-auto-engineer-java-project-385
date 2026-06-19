package hexlet.code.pages.tasks;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TaskByIdForm {

    private final WebDriver driver;
    private final By title = By.cssSelector(".MuiTypography-root.MuiTypography-h5.css-elr2b6");
    private final By description = By.cssSelector(".MuiTypography-root.MuiTypography-body2.css-bxmwoh");
    private final By editButton = By.xpath("//*[@data-testid='CreateIcon']");
    private final By showButton = By.xpath("//*[@data-testid='RemoveRedEyeIcon']");

    public TaskByIdForm(WebDriver driver) {
        this.driver = driver;
    }
}
