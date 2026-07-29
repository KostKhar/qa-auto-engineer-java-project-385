package hexlet.code.tests.cleanup;

import hexlet.code.tests.BaseTest;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.openqa.selenium.WebDriver;

public class CleanupExtension implements BeforeEachCallback, AfterEachCallback {
    private static final Namespace NS = Namespace.create(CleanupExtension.class);
    private static final String KEY = "registry";
    private static final ThreadLocal<CleanupRegistry> CURRENT = new ThreadLocal<>();

    public static CleanupRegistry cleanup() {
        CleanupRegistry registry = CURRENT.get();
        if (registry == null) {
            throw new IllegalStateException("CleanupRegistry is not initialized. "
                    + "Ensure the test class extends BaseTest.");
        }
        return registry;
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        CleanupRegistry registry = new CleanupRegistry();
        context.getStore(NS).put(KEY, registry);
        CURRENT.set(registry);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        try {
            CleanupRegistry registry = context.getStore(NS).remove(KEY, CleanupRegistry.class);
            WebDriver driver = resolveDriver(context);
            if (registry != null && driver != null) {
                registry.cleanup(driver);
            }
        } finally {
            CURRENT.remove();
        }
    }

    private static WebDriver resolveDriver(ExtensionContext context) {
        Object testInstance = context.getRequiredTestInstance();
        if (testInstance instanceof BaseTest baseTest) {
            return baseTest.getDriver();
        }
        return null;
    }
}
