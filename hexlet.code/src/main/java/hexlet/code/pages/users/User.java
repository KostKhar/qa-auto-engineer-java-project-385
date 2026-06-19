package hexlet.code.pages.users;

import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@Getter
public class User {

    private WebElement row;
    private String id;
    private String email;
    private String firstname;
    private String lastname;
    private String createdAt;

    public User(WebElement row, String id, String email, String firstname, String lastname, String createdAt) {
        this.row = row;
        this.id = id;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.createdAt = createdAt;
    }

    public User(String email, String firstname, String lastname) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
    }


    public void clickCheckbox() {
        row.findElement(By.xpath(".//input[@aria-label='Select this row']")).click();
    }
}
