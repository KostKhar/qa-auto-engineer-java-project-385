package hexlet.code.pages.labels;

import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@Getter
@Setter
public class Label {
    private WebElement row;
    private String id;
    private String name;
    private String createdAt;

    public Label(WebElement row, String id, String name, String createdAt) {
        this.row = row;
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public Label(String name) {
        this.name = name;
    }

    public void clickLabel() {
        row.findElement(By.xpath(".//td[3]//a | .//td[3]")).click();
    }

    public void clickCheckbox() {
        row.findElement(By.xpath(".//span[contains(@class, 'MuiCheckbox-root')]")).click();
    }
}
