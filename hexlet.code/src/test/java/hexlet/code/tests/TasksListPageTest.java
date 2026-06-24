package hexlet.code.tests;

import hexlet.code.components.SideBar;
import hexlet.code.data.RandomTestData;
import hexlet.code.pages.DashboardPage;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.tasks.Task;
import hexlet.code.pages.tasks.TasksListPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TasksListPageTest extends BasePageTest {
    private final List<String> titlesToCleanup = new ArrayList<>();
    private TasksListPage tasksListPage;

    @BeforeEach
    void login() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.signInByLoginAndPassword("admin", "password");
        tasksListPage = dashboardPage.getSideBar().getTaskListPage();
        assertNotNull(tasksListPage, "Tasks list page is null");
    }

    @AfterEach
    void cleanupCreatedTasks() {
        try {
            TasksListPage listPage = new SideBar(driver).getTaskListPage();
            for (String title : titlesToCleanup) {
                if (listPage.isTaskExists(title)) {
                    listPage.deleteTaskByTitle(title);
                }
            }
        } catch (Exception ignored) {
        } finally {
            titlesToCleanup.clear();
        }
    }

    private void trackForCleanup(String title) {
        if (title != null && !title.isBlank()) {
            titlesToCleanup.add(title);
        }
    }

    @Test
    @DisplayName("Отображение Kanban-доски задач")
    void checkTasksKanbanPage() {
        assertTrue(tasksListPage.isBoardVisible());
        assertTrue(tasksListPage.isCreateButtonVisible());
        assertTrue(tasksListPage.isExportButtonVisible());
        assertTrue(tasksListPage.hasColumns("Draft", "To Review", "To Be Fixed", "To Publish", "Published"));
    }

    @Test
    @DisplayName("Задачи загружены на доске")
    void checkTasksAreLoaded() {
        assertTrue(tasksListPage.isBoardLoaded());
        assertTrue(tasksListPage.getVisibleTaskCount() > 0);
        assertTrue(tasksListPage.isTaskExists("Task 1"));
    }

    @Test
    @DisplayName("Фильтрация задач по статусу")
    void checkFilterByStatus() {
        tasksListPage.filterByStatus("Draft");

        assertTrue(tasksListPage.waitUntilTaskVisible("Task 5"));
        assertTrue(tasksListPage.waitUntilTaskHidden("Task 1"));

        tasksListPage.clearFilters();
        assertTrue(tasksListPage.waitUntilTaskVisible("Task 1"));
    }

    @Test
    @DisplayName("Фильтрация задач по исполнителю")
    void checkFilterByAssignee() {
        tasksListPage.filterByAssignee("john@google.com");

        assertTrue(tasksListPage.waitUntilTaskVisible("Task 1"));
        assertTrue(tasksListPage.waitUntilTaskHidden("Task 3"));

        tasksListPage.clearFilters();
        assertTrue(tasksListPage.waitUntilTaskVisible("Task 3"));
    }

    @Test
    @DisplayName("Фильтрация задач по метке")
    void checkFilterByLabel() {
        tasksListPage.filterByLabel("bug");

        assertTrue(tasksListPage.waitUntilTaskVisible("Task 2"));
        assertTrue(tasksListPage.waitUntilTaskHidden("Task 5"));

        tasksListPage.clearFilters();
        assertTrue(tasksListPage.waitUntilTaskVisible("Task 5"));
    }

    @Test
    @DisplayName("Перемещение задачи между колонками перетаскиванием")
    void checkMoveTaskBetweenColumnsByDrag() {
        Task testTask = RandomTestData.getTask();
        trackForCleanup(testTask.getTitle());

        tasksListPage = tasksListPage.clickCreateTask().createTaskAndReturnToBoard(testTask);
        assertTrue(tasksListPage.isTaskInColumn(testTask.getTitle(), "Draft"));

        tasksListPage.moveTaskToColumnByDrag(testTask.getTitle(), "To Review");
        tasksListPage = new SideBar(driver).getTaskListPage();

        assertTrue(tasksListPage.isTaskInColumn(testTask.getTitle(), "To Review"));
    }
}
