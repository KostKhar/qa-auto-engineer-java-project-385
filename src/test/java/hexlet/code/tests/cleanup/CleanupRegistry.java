package hexlet.code.tests.cleanup;

import hexlet.code.components.SideBar;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

public final class CleanupRegistry {
    private final List<Pending> pending = new ArrayList<>();

    public void trackUser(String email) {
        track(email, (driver, value) -> {
            var page = new SideBar(driver).getUsersListPage();
            if (page.isUserExists(value)) {
                page.deleteUserByEmail(value);
            }
        });
    }

    public void trackLabel(String name) {
        track(name, (driver, value) -> {
            var page = new SideBar(driver).getLabelsListPage();
            if (page.isLabelExists(value)) {
                page.deleteLabelByName(value);
            }
        });
    }

    public void trackStatus(String name) {
        track(name, (driver, value) -> {
            var page = new SideBar(driver).getStatusesListPage();
            if (page.isStatusExists(value)) {
                page.deleteStatusByName(value);
            }
        });
    }

    public void trackTask(String title) {
        track(title, (driver, value) -> {
            var page = new SideBar(driver).getTaskListPage();
            if (page.isTaskExists(value)) {
                page.deleteTaskByTitle(value);
            }
        });
    }

    public void untrack(String value) {
        if (value == null) {
            return;
        }
        pending.removeIf(item -> value.equals(item.value()));
    }

    public void clear() {
        pending.clear();
    }

    void cleanup(WebDriver driver) {
        Iterator<Pending> iterator = pending.iterator();
        while (iterator.hasNext()) {
            Pending item = iterator.next();
            try {
                item.cleaner().accept(driver, item.value());
            } catch (Exception e) {
                System.err.println("Cleanup failed for '" + item.value() + "': " + e.getMessage());
            } finally {
                iterator.remove();
            }
        }
    }

    private void track(String value, BiConsumer<WebDriver, String> cleaner) {
        if (value == null || value.isBlank()) {
            return;
        }
        pending.add(new Pending(value, cleaner));
    }

    private record Pending(String value, BiConsumer<WebDriver, String> cleaner) {
    }
}
