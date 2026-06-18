package hexlet.code.pages.statuses;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class Status {
    private final WebElement row;
    private final String id;
    private final String name;
    private String createdAt;

    public Status(WebElement row, String id, String name) {
        this.row = row;
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void clickCheckbox() {
        row.findElement(By.xpath(".//input[@data-testid='CheckBoxOutlineBlankIcon']")).click();
    }
}
