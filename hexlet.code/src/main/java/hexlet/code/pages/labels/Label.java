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
    private String slug;

    public Label(WebElement row, String id, String name, String slug) {
        this.row = row;
        this.id = id;
        this.name = name;
        this.slug = slug;
    }

    public Label(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    public void clickLabel() {
        row.findElement(By.xpath(".//td[3]//a | .//td[3]")).click();
    }

    public void clickCheckbox() {
        row.findElement(By.xpath(".//span[contains(@class, 'MuiCheckbox-root')]")).click();
    }
}
