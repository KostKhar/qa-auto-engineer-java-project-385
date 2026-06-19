package hexlet.code.data;

import com.github.javafaker.Faker;
import hexlet.code.pages.users.User;

import java.util.UUID;

public class RandomTestData {
    private static final Faker FAKER = new Faker();

    public static User getUser() {
        String email = "test_auto_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12) + "@example.com";
        return new User(email, FAKER.name().firstName(), FAKER.name().lastName());
    }
}
