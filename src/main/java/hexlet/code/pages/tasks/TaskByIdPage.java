package hexlet.code.pages.tasks;

import hexlet.code.actions.ElementAction;
import hexlet.code.components.SideBar;
import hexlet.code.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TaskByIdPage extends BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskByIdPage.class);

    private final By titleField = By.xpath("//*[@name='title']");
    private final By contentField = By.xpath("//*[@name='content']");
    private final By assigneeCombobox = By.xpath("(//*[@name='assignee_id']/ancestor::div[contains(@class,'MuiFormControl-root')]//div[@role='combobox'])[1]");
    private final By statusCombobox = By.xpath("(//*[@name='status_id']/ancestor::div[contains(@class,'MuiFormControl-root')]//div[@role='combobox'])[1]");
    private final By labelCombobox = By.xpath(".//*/div[@data-testid='selectArray']");
    private final By saveButton = By.xpath("//*[@aria-label='Save']");
    private final By deleteButton = By.xpath("//*[@aria-label='Delete']");
    private final By deleteConfirmDialog = By.xpath("//*[@role='dialog']");
    private final By confirmDeleteButton = By.xpath(
            "//*[@role='dialog']//button[contains(@class,'MuiButton-contained')]"
    );
    private final By successCreatePopup = By.xpath("//*[contains(text(), 'created')]");
    private final By successUpdatedPopup = By.xpath("//*[contains(text(), 'updated')]");
    private final By successDeletePopup = By.xpath("//*[contains(text(), 'deleted')]");
    private final By validationError = By.xpath(
            "//*[contains(@class, 'MuiFormHelperText-root') and contains(@class, 'Mui-error')]"
    );

    public TaskByIdPage(WebDriver driver) {
        super(driver);
    }

    public void fillTaskForm(Task task) {
        fillTitleIfPresent(task);
        fillContentIfPresent(task);
        fillAssigneeIfPresent(task);
        fillStatusIfPresent(task);
        fillLabelsIfPresent(task);
    }

    private void fillTitleIfPresent(Task task) {
        if (task.getTitle() != null) {
            elementAction().find(titleField).waitUntilVisible().clearAndSendKeys(task.getTitle());
        }
    }

    private void fillContentIfPresent(Task task) {
        if (task.getContent() != null) {
            elementAction().find(contentField).waitUntilVisible().clearAndSendKeys(task.getContent());
        }
    }

    private void fillAssigneeIfPresent(Task task) {
        if (task.getAssigneeEmail() != null) {
            elementAction().selectComboboxOption(assigneeCombobox, task.getAssigneeEmail());
        }
    }

    private void fillStatusIfPresent(Task task) {
        if (task.getStatusName() != null) {
            elementAction().selectComboboxOption(statusCombobox, task.getStatusName());
        }
    }

    private void fillLabelsIfPresent(Task task) {
        if (task.getLabels() != null && !task.getLabels().isEmpty()) {
            selectLabels(task.getLabels());
        }
    }

    public TasksListPage createTaskAndReturnToBoard(Task task) {
        fillTaskForm(task);
        elementAction().find(saveButton).waitUntilClickable().click();
        elementAction().find(successCreatePopup).waitUntilVisible();
        elementAction().find(successCreatePopup).waitUntilInvisible();
        TasksListPage tasksListPage = new SideBar(getDriver()).getTaskListPage();
        waiter().waitForCondition(driver -> tasksListPage.isTaskExists(task.getTitle()));
        return tasksListPage;
    }

    public void updateTask(Task task) {
        fillTaskForm(task);
        elementAction().find(saveButton).waitUntilClickable().click();
        try {
            elementAction().find(successUpdatedPopup).waitUntilVisible();
            elementAction().find(successUpdatedPopup).waitUntilInvisible();
        } catch (TimeoutException e) {
            LOGGER.warn("Timed out waiting for task update popup", e);
            waiter().waitForPageLoaded();
        }
    }

    public TasksListPage updateTaskAndReturnToBoard(Task task) {
        updateTask(task);
        TasksListPage tasksListPage = new SideBar(getDriver()).getTaskListPage();
        if (task.getTitle() != null && task.getStatusName() != null) {
            waiter().waitForCondition(driver -> tasksListPage.isTaskInColumn(task.getTitle(), task.getStatusName()));
        }
        return tasksListPage;
    }

    public void submitFormWithoutWaitingForSuccess() {
        elementAction().find(saveButton).waitUntilClickable().click();
    }

    public void deleteTask() {
        waiter().waitForSnackbarToDisappear();
        elementAction().find(deleteButton).waitUntilClickable().click();
        confirmDeleteIfRequired();
        elementAction().find(successDeletePopup).waitUntilVisible();
    }

    private void confirmDeleteIfRequired() {
        if (getDriver().findElements(deleteConfirmDialog).isEmpty()) {
            return;
        }
        elementAction().find(deleteConfirmDialog).waitUntilVisible();
        elementAction().find(confirmDeleteButton).waitUntilClickable().click();
    }

    public String getTitleValue() {
        return elementAction().find(titleField).waitUntilVisible().getAttribute("value");
    }

    public String getContentValue() {
        return elementAction().find(contentField).waitUntilVisible().getAttribute("value");
    }

    public String getAssigneeValue() {
        return elementAction().find(assigneeCombobox).waitUntilVisible().getText().trim();
    }

    public String getStatusValue() {
        return elementAction().find(statusCombobox).waitUntilVisible().getText().trim();
    }

    public boolean isTitleFieldVisible() {
        return elementAction().find(titleField).waitUntilVisible().isDisplayed();
    }

    public boolean isContentFieldVisible() {
        return elementAction().find(contentField).waitUntilVisible().isDisplayed();
    }

    public boolean isAssigneeFieldVisible() {
        return elementAction().find(assigneeCombobox).waitUntilVisible().isDisplayed();
    }

    public boolean isStatusFieldVisible() {
        return elementAction().find(statusCombobox).waitUntilVisible().isDisplayed();
    }

    public boolean isSaveButtonVisible() {
        return elementAction().find(saveButton).waitUntilVisible().isDisplayed();
    }

    public boolean hasValidationError() {
        return isTitleValidationErrorVisible()
                || isAssigneeValidationErrorVisible()
                || isStatusValidationErrorVisible()
                || elementAction().hasVisibleGlobalValidationError(validationError);
    }

    public boolean isTitleValidationErrorVisible() {
        return elementAction().hasBrowserValidationMessage(titleField)
                || elementAction().hasVisibleValidationError(titleField, validationError);
    }

    public boolean isAssigneeValidationErrorVisible() {
        return elementAction().hasVisibleValidationError(assigneeCombobox, validationError);
    }

    public boolean isStatusValidationErrorVisible() {
        return elementAction().hasVisibleValidationError(statusCombobox, validationError);
    }

    public List<String> getLabels() {
        WebElement labelsField = elementAction().find(labelCombobox).waitUntilVisible().getElement()
                .findElement(By.xpath("./ancestor::div[contains(@class,'MuiFormControl-root')]"));
        return labelsField.findElements(By.xpath(".//*[contains(@class,'MuiChip-label')]")).stream()
                .map(element -> element.getText().trim())
                .filter(text -> !text.isBlank())
                .toList();
    }

    private void selectLabels(List<String> labels) {
        By listbox = By.xpath("//*[@role='listbox']");
        elementAction().find(labelCombobox).waitUntilClickable().click();
        elementAction().find(listbox).waitUntilVisible();
        for (String label : labels) {
            By option = By.xpath("//*[@role='listbox']//*[@role='option'][contains(normalize-space(.),"
                    + ElementAction.xpathLiteral(label.trim()) + ")]");
            elementAction().find(option).waitUntilClickable().click();
        }
        closeOpenListbox(listbox);
    }

    private void closeOpenListbox(By listbox) {
        new Actions(getDriver()).sendKeys(Keys.ESCAPE).perform();
        try {
            elementAction().find(listbox).waitUntilInvisible();
        } catch (TimeoutException e) {
            List<WebElement> backdrops = getDriver().findElements(
                    By.xpath("//*[contains(@class,'MuiBackdrop-root')]"));
            if (!backdrops.isEmpty()) {
                backdrops.getFirst().click();
            }
            elementAction().find(listbox).waitUntilInvisible();
        }
    }
}
