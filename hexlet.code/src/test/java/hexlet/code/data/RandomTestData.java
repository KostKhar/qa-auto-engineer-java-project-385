package hexlet.code.data;

import com.github.javafaker.Faker;
import hexlet.code.pages.labels.Label;
import hexlet.code.pages.statuses.Status;
import hexlet.code.pages.tasks.Task;
import hexlet.code.pages.users.User;

import java.util.Collections;
import java.util.UUID;

public class RandomTestData {
    private static final Faker FAKER = new Faker();

    public static User getUser() {
        String email = "test_auto_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12) + "@example.com";
        return new User(email, FAKER.name().firstName(), FAKER.name().lastName());
    }

    public static Status getStatus() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String name = "Status " + suffix;
        String slug = "status-" + suffix;
        return new Status(name, slug);
    }

    public static Label getLabel() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String name = "Label " + suffix;
        String slug = "label-" + suffix;
        return new Label(name, slug);
    }

    public static Task getTask() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String title = "Task " + suffix;
        String content = "Description " + suffix;
        return new Task(title, content, "john@google.com", "Draft", Collections.emptyList());
    }
}
