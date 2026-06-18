package hexlet.code.pages.users;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class User {

    private final WebElement row;
    private final String id;
    private final String email;
    private final String firstname;
    private final String lastname;
    private final String createdAt;

    public User(WebElement row, String id, String email, String firstname, String lastname, String createdAt) {
        this.row = row;
        this.id = id;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.createdAt = createdAt;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getLastname() {
        return lastname;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void clickCheckbox() {
        row.findElement(By.xpath(".//input[@aria-label='Select this row']")).click();
    }
}
