package hexlet.code.tests;

import hexlet.code.components.SideBar;
import hexlet.code.data.RandomTestData;
import hexlet.code.pages.DashboardPage;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.tasks.Task;
import hexlet.code.pages.tasks.TaskByIdForm;
import hexlet.code.pages.tasks.TaskByIdPage;
import hexlet.code.pages.tasks.TasksListPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskByIdPageTest extends BasePageTest {
    private final List<String> titlesToCleanup = new ArrayList<>();
    private TasksListPage tasksListPage;

    @BeforeEach
    void login() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.signInByLoginAndPassword("admin", "password");
        tasksListPage = dashboardPage.getSideBar().getTaskListPage();
        assertNotNull(tasksListPage);
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

    private TasksListPage createTaskOnBoard(Task task) {
        tasksListPage = tasksListPage.clickCreateTask().createTaskAndReturnToBoard(task);
        return tasksListPage;
    }

    @Test
    @DisplayName("Отображение полей формы создания задачи")
    void checkTaskCreateFormFields() {
        TaskByIdPage taskPage = tasksListPage.clickCreateTask();

        assertTrue(taskPage.isTitleFieldVisible());
        assertTrue(taskPage.isAssigneeFieldVisible());
        assertTrue(taskPage.isStatusFieldVisible());
        assertTrue(taskPage.isContentFieldVisible());
        assertTrue(taskPage.isSaveButtonVisible());
    }

    @Test
    @DisplayName("Создание новой задачи")
    void checkCreateNewTask() {
        Task testTask = RandomTestData.getTask();
        trackForCleanup(testTask.getTitle());

        createTaskOnBoard(testTask);

        assertTrue(tasksListPage.isTaskExists(testTask.getTitle()));
        assertTrue(tasksListPage.isTaskInColumn(testTask.getTitle(), testTask.getStatusName()));
    }

    @Test
    @DisplayName("Форма редактирования заполнена данными задачи")
    void checkEditFormPrefilled() {
        Task testTask = RandomTestData.getTask();
        trackForCleanup(testTask.getTitle());

        createTaskOnBoard(testTask);

        TaskByIdPage taskPage = tasksListPage.openTaskEditByTitle(testTask.getTitle());

        assertEquals(testTask.getTitle(), taskPage.getTitleValue());
        assertEquals(testTask.getContent(), taskPage.getContentValue());
        assertEquals(testTask.getAssigneeEmail(), taskPage.getAssigneeValue());
        assertEquals(testTask.getStatusName(), taskPage.getStatusValue());
    }

    @Test
    @DisplayName("Редактирование данных задачи")
    void checkUpdateTask() {
        Task testTask = RandomTestData.getTask();
        trackForCleanup(testTask.getTitle());

        createTaskOnBoard(testTask);

        Task updatedTask = new Task(
                testTask.getTitle(),
                "Updated " + testTask.getContent(),
                testTask.getAssigneeEmail(),
                "Published"
        );

        tasksListPage = tasksListPage.updateTaskByTitle(testTask.getTitle(), updatedTask);

        assertTrue(tasksListPage.isTaskExists(testTask.getTitle()));
        assertTrue(tasksListPage.isTaskInColumn(testTask.getTitle(), "Published"));
    }

    @Test
    @DisplayName("Отображение деталей задачи")
    void checkShowTaskDetails() {
        Task testTask = RandomTestData.getTask();
        trackForCleanup(testTask.getTitle());

        createTaskOnBoard(testTask);

        TaskByIdForm taskForm = tasksListPage.openTaskShowByTitle(testTask.getTitle());

        assertTrue(taskForm.isTitleVisible());
        assertEquals(testTask.getTitle(), taskForm.getTitleText());
        assertEquals(testTask.getContent(), taskForm.getDescriptionText());
        assertEquals(testTask.getAssigneeEmail(), taskForm.getAssigneeText());
    }

    @Test
    @DisplayName("Перемещение задачи между колонками через редактирование")
    void checkMoveTaskBetweenColumnsByEdit() {
        Task testTask = RandomTestData.getTask();
        trackForCleanup(testTask.getTitle());

        createTaskOnBoard(testTask);
        assertTrue(tasksListPage.isTaskInColumn(testTask.getTitle(), "Draft"));

        tasksListPage = tasksListPage.moveTaskToStatusByEdit(testTask.getTitle(), "To Publish");

        assertTrue(tasksListPage.isTaskInColumn(testTask.getTitle(), "To Publish"));
        assertFalse(tasksListPage.isTaskInColumn(testTask.getTitle(), "Draft"));
    }

    @Test
    @DisplayName("Валидация пустого заголовка при создании задачи")
    void checkEmptyTitleOnCreate() {
        TaskByIdPage taskPage = tasksListPage.clickCreateTask();
        Task invalidTask = new Task("", "Valid content", "john@google.com", "Draft");

        taskPage.fillTaskForm(invalidTask);
        taskPage.submitFormWithoutWaitingForSuccess();

        assertTrue(taskPage.hasValidationError());
    }

    @Test
    @DisplayName("Удаление задачи")
    void checkDeleteTask() {
        Task testTask = RandomTestData.getTask();
        trackForCleanup(testTask.getTitle());

        createTaskOnBoard(testTask);
        assertTrue(tasksListPage.isTaskExists(testTask.getTitle()));

        tasksListPage.deleteTaskByTitle(testTask.getTitle());
        titlesToCleanup.remove(testTask.getTitle());

        tasksListPage = new SideBar(driver).getTaskListPage();
        assertTrue(tasksListPage.isTaskNotExists(testTask.getTitle()));
    }
}
