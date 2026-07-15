package hexlet.code.configure;

import org.aeonbits.owner.ConfigCache;

public final class ConfigurationManager {
    private ConfigurationManager() {
    }

    public static Configuration config() {
        return ConfigCache.getOrCreate(Configuration.class);
    }
}
