package hexlet.code.configure;

import org.aeonbits.owner.ConfigFactory;

import java.util.Map;

public final class ConfigurationManager {
    private static final String PROFILE_PROPERTY = "profile";
    private static final String PROFILE_ENV = "PROFILE";
    private static final String DEFAULT_PROFILE = "local";
    private static final String CI_PROFILE = "ci";

    private static final Configuration CONFIG = createConfiguration();

    private ConfigurationManager() {
    }

    public static Configuration config() {
        return CONFIG;
    }

    public static String currentProfile() {
        return resolveProfile();
    }

    private static Configuration createConfiguration() {
        return ConfigFactory.create(Configuration.class, Map.of(PROFILE_PROPERTY, resolveProfile()));
    }

    private static String resolveProfile() {
        String profile = System.getProperty(PROFILE_PROPERTY);
        if (isBlank(profile)) {
            profile = System.getenv(PROFILE_ENV);
        }
        if (!isBlank(profile)) {
            return profile;
        }
        if (isCiEnvironment()) {
            return CI_PROFILE;
        }
        return DEFAULT_PROFILE;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static boolean isCiEnvironment() {
        return "true".equalsIgnoreCase(System.getenv("GITHUB_ACTIONS"))
                || "true".equalsIgnoreCase(System.getenv("CI"));
    }
}
