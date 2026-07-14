package hexlet.code.configure;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "classpath:config.properties",
        "classpath:allure.properties"
})
public interface Configuration extends Config {

    @Key("headless")
    boolean headless();

    @Key("timeout")
    int timeout();

    @Key("browser")
    String browser();

    @Key("window.width")
    @DefaultValue("1920")
    int windowWidth();

    @Key("window.height")
    @DefaultValue("1080")
    int windowHeight();
}
