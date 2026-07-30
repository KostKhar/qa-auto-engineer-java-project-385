package hexlet.code.tests;

import hexlet.code.data.RandomTestData;
import hexlet.code.pages.DashboardPage;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.tasks.Task;
import hexlet.code.pages.tasks.TaskByIdPage;
import hexlet.code.pages.tasks.TasksListPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static hexlet.code.tests.cleanup.CleanupExtension.cleanup;
import static org.junit.jupiter.api.Assertions.*;

class TasksListPageTest extends BaseTest {
    private TasksListPage tasksListPage;

    static class MoveTaskArgumentsProvider implements ArgumentsProvider {
        private static final List<String> STATUSES = List.of(
                "Draft", "To Review", "To Be Fixed", "To Publish", "Published"
        );

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return STATUSES.stream()
                    .flatMap(oldStatus -> STATUSES.stream()
                            .filter(newStatus -> !newStatus.equals(oldStatus))
                            .map(newStatus -> Arguments.of(oldStatus, newStatus)));
        }
    }

    @BeforeEach
    void login() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.signInByLoginAndPassword("admin", "password");
        tasksListPage = dashboardPage.getSideBar().getTaskListPage();
        assertNotNull(tasksListPage, "Tasks list page is null");
    }

    @Test
    @DisplayName("Отображение Kanban-доски задач")
    void checkTasksKanbanPage() {
        tasksListPage.assertTasksPageHealthy();
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
        tasksListPage.assertTasksPageHealthy();
        assertAll(
                () -> assertTrue(tasksListPage.isBoardLoaded()),
                () -> assertTrue(tasksListPage.getVisibleTaskCount() > 0),
                () -> assertTrue(tasksListPage.isTaskExists("Task 1"))
        );
    }

    @Test
    @DisplayName("Создание задачи открывает форму и сохраняет карточку")
    void checkCreateTaskOpensFormAndSavesCard() {
        Task testTask = RandomTestData.getTask();
        cleanup().trackTask(testTask.getTitle());

        TaskByIdPage form = tasksListPage.clickCreateTask();
        assertAll(
                () -> assertTrue(form.isTitleFieldVisible()),
                () -> assertTrue(form.isAssigneeFieldVisible()),
                () -> assertTrue(form.isStatusFieldVisible()),
                () -> assertTrue(form.isSaveButtonVisible())
        );

        tasksListPage = form.createTaskAndReturnToBoard(testTask);
        tasksListPage.assertTasksPageHealthy();
        assertAll(
                () -> assertTrue(tasksListPage.isTaskExists(testTask.getTitle())),
                () -> assertTrue(tasksListPage.isTaskInColumn(testTask.getTitle(), testTask.getStatusName()))
        );
    }

    @DisplayName("Фильтрация задач по статусу")
    @ParameterizedTest
    @ValueSource(strings = {"Draft", "To Review", "To Be Fixed", "To Publish", "Published"})
    void checkFilterByStatus(String status) {
        Task testTask = RandomTestData.getTask();
        testTask.setStatusName(status);
        String title = testTask.getTitle();
        cleanup().trackTask(testTask.getTitle());

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
        cleanup().trackTask(testTask.getTitle());

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
        cleanup().trackTask(testTask.getTitle());

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
        cleanup().trackTask(matchingTask.getTitle());
        cleanup().trackTask(otherTask.getTitle());

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
        cleanup().trackTask(matchingTask.getTitle());
        cleanup().trackTask(otherTask.getTitle());

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
        cleanup().trackTask(matchingTask.getTitle());
        cleanup().trackTask(otherTask.getTitle());

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

    @DisplayName("Перемещение задачи между колонками перетаскиванием")
    @ParameterizedTest
    @ArgumentsSource(MoveTaskArgumentsProvider.class)
    void checkMoveTaskBetweenColumnsByDrag(String statusOld, String statusNew) {
        Task testTask = RandomTestData.getTask(statusOld);
        String title = testTask.getTitle();
        cleanup().trackTask(testTask.getTitle());

        tasksListPage = tasksListPage.clickCreateTask().createTaskAndReturnToBoard(testTask);
        assertTrue(tasksListPage.isTaskInColumn(title, statusOld));

        tasksListPage.moveTaskToColumnByDrag(title, statusNew);
        assertTrue(tasksListPage.isTaskInColumn(title, statusNew));

        TaskByIdPage taskByIdPage = tasksListPage.openTaskEditByTitle(title);

        assertEquals(statusNew, taskByIdPage.getStatusValue(), "status is not '" + statusNew + "'");
    }
}
