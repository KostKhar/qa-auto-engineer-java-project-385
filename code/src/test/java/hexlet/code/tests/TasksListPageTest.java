package hexlet.code.tests;

import hexlet.code.components.SideBar;
import hexlet.code.data.RandomTestData;
import hexlet.code.pages.DashboardPage;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.tasks.Task;
import hexlet.code.pages.tasks.TaskByIdPage;
import hexlet.code.pages.tasks.TasksListPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TasksListPageTest extends BasePageTest {
    private final List<String> titlesToCleanup = new ArrayList<>();
    private TasksListPage tasksListPage;

    @MethodSource
    static Stream<Arguments> getDataForMoveTask() {
        return Stream.of(
                Arguments.of("Draft", "To Review"),
                Arguments.of("Draft", "To Be Fixed"),
                Arguments.of("Draft", "To Publish"),
                Arguments.of("Draft", "Published"),
                Arguments.of("To Review", "Draft"),
                Arguments.of("To Review", "To Be Fixed"),
                Arguments.of("To Review", "To Publish"),
                Arguments.of("To Review", "Published"),
                Arguments.of("To Be Fixed", "Draft"),
                Arguments.of("To Be Fixed", "To Review"),
                Arguments.of("To Be Fixed", "To Publish"),
                Arguments.of("To Be Fixed", "Published"),
                Arguments.of("To Publish", "Draft"),
                Arguments.of("To Publish", "To Review"),
                Arguments.of("To Publish", "To Be Fixed"),
                Arguments.of("To Publish", "Published"),
                Arguments.of("Published", "Draft"),
                Arguments.of("Published", "To Review"),
                Arguments.of("Published", "To Be Fixed"),
                Arguments.of("Published", "To Publish")
        );
    }

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
        } catch (Exception e) {
            e.printStackTrace();
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
        assertAll(
                () -> assertTrue(tasksListPage.getVisibleTaskCount() > 0),
                () -> assertTrue(tasksListPage.isBoardVisible()),
                () -> assertTrue(tasksListPage.isCreateButtonVisible()),
                () -> assertTrue(tasksListPage.isExportButtonVisible()),
                () -> assertTrue(tasksListPage.hasColumns("Draft", "To Review", "To Be Fixed", "To Publish", "Published"))
        );
    }

    @Test
    @DisplayName("Задачи загружены на доске")
    void checkTasksAreLoaded() {
        assertAll(
                () -> assertTrue(tasksListPage.isBoardLoaded()),
                () -> assertTrue(tasksListPage.getVisibleTaskCount() > 0),
                () -> assertTrue(tasksListPage.isTaskExists("Task 1"))
        );
    }

    @DisplayName("Фильтрация задач по статусу")
    @ParameterizedTest
    @ValueSource(strings = {"Draft", "To Review", "To Be Fixed", "To Publish", "Published"})
    void checkFilterByStatus(String status) {
        Task testTask = RandomTestData.getTask();
        testTask.setStatusName(status);
        String title = testTask.getTitle();
        trackForCleanup(testTask.getTitle());

        tasksListPage = tasksListPage.clickCreateTask().createTaskAndReturnToBoard(testTask);

        tasksListPage.filterByStatus(status);

        TaskByIdPage taskByIdPage = tasksListPage.openTaskEditByTitle(title);

        assertEquals(status, taskByIdPage.getStatusValue(), "filter by status not work");
    }

    @Test
    @DisplayName("Фильтрация задач по исполнителю")
    void checkFilterByAssignee() {
        Task testTask = RandomTestData.getTask();
        String email = testTask.getAssigneeEmail();
        String title = testTask.getTitle();
        trackForCleanup(testTask.getTitle());

        tasksListPage = tasksListPage.clickCreateTask().createTaskAndReturnToBoard(testTask);
        tasksListPage.filterByAssignee(email);

        TaskByIdPage taskByIdPage = tasksListPage.openTaskEditByTitle(title);

        assertEquals(email, taskByIdPage.getAssigneeValue(), "filter by status not work");
    }

    @DisplayName("Фильтрация задач по метке")
    @ParameterizedTest
    @ValueSource(strings = {"critical", "task", "enhancement", "bug"})
    void checkFilterByLabel(String label) {
        Task testTask = RandomTestData.getTask();
        testTask.setLabels(List.of(label));
        String title = testTask.getTitle();
        trackForCleanup(testTask.getTitle());

        tasksListPage = tasksListPage.clickCreateTask().createTaskAndReturnToBoard(testTask);
        tasksListPage.filterByLabel(label);

        TaskByIdPage taskByIdPage = tasksListPage.openTaskEditByTitle(title);

        assertEquals(List.of(label), taskByIdPage.getLabels(), "label by status not work");
    }

    @Test
    @DisplayName("Фильтрация по статусу скрывает задачи с другим статусом")
    void checkFilterByStatusHidesOtherTasks() {
        Task matchingTask = RandomTestData.getTask("Published");
        Task otherTask = RandomTestData.getTask("Draft");
        trackForCleanup(matchingTask.getTitle());
        trackForCleanup(otherTask.getTitle());

        tasksListPage = tasksListPage.clickCreateTask().createTaskAndReturnToBoard(matchingTask);
        tasksListPage = tasksListPage.clickCreateTask().createTaskAndReturnToBoard(otherTask);

        tasksListPage.filterByStatus("Published");

        assertAll(
                () -> assertTrue(tasksListPage.isTaskExists(matchingTask.getTitle()),
                        "matching task should be visible after status filter"),
                () -> assertTrue(tasksListPage.waitUntilTaskHidden(otherTask.getTitle()),
                        "task with different status should be hidden")
        );
    }

    @Test
    @DisplayName("Фильтрация по исполнителю скрывает задачи других исполнителей")
    void checkFilterByAssigneeHidesOtherTasks() {
        Task matchingTask = RandomTestData.getTask();
        matchingTask.setAssigneeEmail("alice@hotmail.com");
        Task otherTask = RandomTestData.getTask();
        otherTask.setAssigneeEmail("john@google.com");
        trackForCleanup(matchingTask.getTitle());
        trackForCleanup(otherTask.getTitle());

        tasksListPage = tasksListPage.clickCreateTask().createTaskAndReturnToBoard(matchingTask);
        tasksListPage = tasksListPage.clickCreateTask().createTaskAndReturnToBoard(otherTask);

        tasksListPage.filterByAssignee("alice@hotmail.com");

        assertAll(
                () -> assertTrue(tasksListPage.isTaskExists(matchingTask.getTitle()),
                        "matching task should be visible after assignee filter"),
                () -> assertTrue(tasksListPage.waitUntilTaskHidden(otherTask.getTitle()),
                        "task with different assignee should be hidden")
        );
    }

    @Test
    @DisplayName("Фильтрация по метке скрывает задачи без этой метки")
    void checkFilterByLabelHidesOtherTasks() {
        Task matchingTask = RandomTestData.getTask();
        matchingTask.setLabels(List.of("critical"));
        Task otherTask = RandomTestData.getTask();
        otherTask.setLabels(List.of("bug"));
        trackForCleanup(matchingTask.getTitle());
        trackForCleanup(otherTask.getTitle());

        tasksListPage = tasksListPage.clickCreateTask().createTaskAndReturnToBoard(matchingTask);
        tasksListPage = tasksListPage.clickCreateTask().createTaskAndReturnToBoard(otherTask);

        tasksListPage.filterByLabel("critical");

        assertAll(
                () -> assertTrue(tasksListPage.isTaskExists(matchingTask.getTitle()),
                        "matching task should be visible after label filter"),
                () -> assertTrue(tasksListPage.waitUntilTaskHidden(otherTask.getTitle()),
                        "task without matching label should be hidden")
        );
    }

    @DisplayName("Фильтрация задач по нескольким меткам")
    @Test
    void checkFilterByLabels() {
        List<String> labels = List.of("critical", "task", "enhancement", "bug");
        Task testTask = RandomTestData.getTask();
        testTask.setLabels(labels);
        String title = testTask.getTitle();
        trackForCleanup(testTask.getTitle());

        tasksListPage = tasksListPage.clickCreateTask().createTaskAndReturnToBoard(testTask);
        tasksListPage.filterByLabel(labels);

        TaskByIdPage taskByIdPage = tasksListPage.openTaskEditByTitle(title);

        assertEquals(labels, taskByIdPage.getLabels(), "filter by labels not work");
    }

    @DisplayName("Перемещение задачи между колонками перетаскиванием")
    @ParameterizedTest
    @MethodSource("getDataForMoveTask")
    void checkMoveTaskBetweenColumnsByDrag(String statusOld, String statusNew) {
        Task testTask = RandomTestData.getTask(statusOld);
        String title = testTask.getTitle();
        trackForCleanup(testTask.getTitle());

        tasksListPage = tasksListPage.clickCreateTask().createTaskAndReturnToBoard(testTask);
        assertTrue(tasksListPage.isTaskInColumn(title, statusOld));

        tasksListPage.moveTaskToColumnByDrag(title, statusNew);
        assertTrue(tasksListPage.isTaskInColumn(title, statusNew));

        TaskByIdPage taskByIdPage = tasksListPage.openTaskEditByTitle(title);

        assertEquals(statusNew, taskByIdPage.getStatusValue(), "status is not '" + statusNew + "'");
    }
}
