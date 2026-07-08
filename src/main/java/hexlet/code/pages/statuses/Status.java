package hexlet.code.pages.statuses;

import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@Getter
@Setter
public class Status {
    private WebElement row;
    private String id;
    private String name;
    private String slug;

    public Status(WebElement row, String id, String name, String slug) {
        this.row = row;
        this.id = id;
        this.slug = slug;
        this.name = name;
    }

    public Status(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    public void clickStatus() {
        row.findElement(By.xpath(".//td[3]//a | .//td[3]")).click();
    }

    public void clickCheckbox() {
        row.findElement(By.xpath(".//span[contains(@class, 'MuiCheckbox-root')]")).click();
    }
}
