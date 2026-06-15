package hexlet.code.service;

public class EnvironmentService {
    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    public static String getEnv(String key) {
        String value = System.getenv(key);   // GitLab CI/CD variable
        if (value == null || value.isBlank()) {
            value = dotenv.get(key);         // local .env
        }
        return value;
    }
}