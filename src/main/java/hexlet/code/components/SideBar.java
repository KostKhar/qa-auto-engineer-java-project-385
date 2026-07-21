package hexlet.code.components;

import hexlet.code.actions.ElementAction;
import hexlet.code.pages.DashboardPage;
import hexlet.code.pages.labels.LabelsListPage;
import hexlet.code.pages.statuses.StatusesListPage;
import hexlet.code.pages.tasks.TasksListPage;
import hexlet.code.pages.users.UsersListPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SideBar {
    private final ElementAction elementAction;
    private final WebDriver driver;

    private final By dashboardButton = By.xpath("//*[@href='#/']");
    private final By tasksButton = By.xpath("//*[@href='#/tasks']");
    private final By usersButton = By.xpath("//*[@href='#/users']");
    private final By labelsButton = By.xpath("//*[@href='#/labels']");
    private final By taskStatusesButton = By.xpath("//*[@href='#/task_statuses']");

    public SideBar(WebDriver driver) {
        this.driver = driver;
        this.elementAction = new ElementAction(driver);
    }

    public DashboardPage getDashboardPage() {
        clickLink(dashboardButton);
        return new DashboardPage(driver);
    }

    public TasksListPage getTaskListPage() {
        clickLink(tasksButton);
        return new TasksListPage(driver);
    }

    public UsersListPage getUsersListPage() {
        clickLink(usersButton);
        return new UsersListPage(driver);
    }

    public LabelsListPage getLabelsListPage() {
        clickLink(labelsButton);
        return new LabelsListPage(driver);
    }

    public StatusesListPage getStatusesListPage() {
        clickLink(taskStatusesButton);
        return new StatusesListPage(driver);
    }

    private void clickLink(By locator) {
        elementAction.find(locator).waitUntilClickable().click();
    }
}
