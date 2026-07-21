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
    boolean headless();

    @Key("timeout")
    int timeout();

    @Key("browser")
    String browser();

    @Key("window.width")
    int windowWidth();

    @Key("window.height")
    int windowHeight();
}
