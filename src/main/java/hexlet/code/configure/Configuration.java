package hexlet.code.configure;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:config-${profile}.properties"
})
public interface Configuration extends Config {

    @Key("APP_BASE_URL")
    @DefaultValue("http://localhost:5173/")
    String baseUrl();

    @Key("headless")
    @DefaultValue("false")
    boolean headless();

    @Key("timeout")
    @DefaultValue("10")
    int timeout();

    @Key("browser")
    @DefaultValue("chromium")
    String browser();

    @Key("window.width")
    @DefaultValue("1920")
    int windowWidth();

    @Key("window.height")
    @DefaultValue("1080")
    int windowHeight();
}
