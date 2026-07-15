package hexlet.code.pages.tasks;

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
            waitForElementClearAndSendKeys(titleField, task.getTitle());
        }
    }

    private void fillContentIfPresent(Task task) {
        if (task.getContent() != null) {
            waitForElementClearAndSendKeys(contentField, task.getContent());
        }
    }

    private void fillAssigneeIfPresent(Task task) {
        if (task.getAssigneeEmail() != null) {
            selectComboboxOption(assigneeCombobox, task.getAssigneeEmail());
        }
    }

    private void fillStatusIfPresent(Task task) {
        if (task.getStatusName() != null) {
            selectComboboxOption(statusCombobox, task.getStatusName());
        }
    }

    private void fillLabelsIfPresent(Task task) {
        if (task.getLabels() != null && !task.getLabels().isEmpty()) {
            selectLabels(task.getLabels());
        }
    }

    public TasksListPage createTaskAndReturnToBoard(Task task) {
        fillTaskForm(task);
        clickElement(saveButton);
        waitForElementVisible(successCreatePopup);
        waitForElementInvisible(successCreatePopup);
        TasksListPage tasksListPage = new SideBar(getDriver()).getTaskListPage();
        waitForCondition(driver -> tasksListPage.isTaskExists(task.getTitle()));
        return tasksListPage;
    }

    public void updateTask(Task task) {
        fillTaskForm(task);
        clickElement(saveButton);
        try {
            waitForElementVisible(successUpdatedPopup);
            waitForElementInvisible(successUpdatedPopup);
        } catch (TimeoutException e) {
            LOGGER.warn("Timed out waiting for task update popup", e);
            waitForPageLoaded();
        }
    }

    public TasksListPage updateTaskAndReturnToBoard(Task task) {
        updateTask(task);
        TasksListPage tasksListPage = new SideBar(getDriver()).getTaskListPage();
        if (task.getTitle() != null && task.getStatusName() != null) {
            waitForCondition(driver -> tasksListPage.isTaskInColumn(task.getTitle(), task.getStatusName()));
        }
        return tasksListPage;
    }

    public void submitFormWithoutWaitingForSuccess() {
        waitForElementClickable(saveButton).click();
    }

    public void deleteTask() {
        waitForSnackbarToDisappear();
        clickElement(deleteButton);
        confirmDeleteIfRequired();
        waitForElementVisible(successDeletePopup);
    }

    private void confirmDeleteIfRequired() {
        if (getDriver().findElements(deleteConfirmDialog).isEmpty()) {
            return;
        }
        waitForElementVisible(deleteConfirmDialog);
        clickElement(confirmDeleteButton);
    }

    public String getTitleValue() {
        return waitForElementVisible(titleField).getAttribute("value");
    }

    public String getContentValue() {
        return waitForElementVisible(contentField).getAttribute("value");
    }

    public String getAssigneeValue() {
        return waitForElementVisible(assigneeCombobox).getText().trim();
    }

    public String getStatusValue() {
        return waitForElementVisible(statusCombobox).getText().trim();
    }

    public boolean isTitleFieldVisible() {
        return waitForElementVisible(titleField).isDisplayed();
    }

    public boolean isContentFieldVisible() {
        return waitForElementVisible(contentField).isDisplayed();
    }

    public boolean isAssigneeFieldVisible() {
        return waitForElementVisible(assigneeCombobox).isDisplayed();
    }

    public boolean isStatusFieldVisible() {
        return waitForElementVisible(statusCombobox).isDisplayed();
    }

    public boolean isSaveButtonVisible() {
        return waitForElementVisible(saveButton).isDisplayed();
    }

    public boolean hasValidationError() {
        return isTitleValidationErrorVisible()
                || isAssigneeValidationErrorVisible()
                || isStatusValidationErrorVisible()
                || hasVisibleGlobalValidationError(validationError);
    }

    public boolean isTitleValidationErrorVisible() {
        return hasBrowserValidationMessage(titleField) || hasVisibleValidationError(titleField, validationError);
    }

    public boolean isAssigneeValidationErrorVisible() {
        return hasVisibleValidationError(assigneeCombobox, validationError);
    }

    public boolean isStatusValidationErrorVisible() {
        return hasVisibleValidationError(statusCombobox, validationError);
    }

    public List<String> getLabels() {
        WebElement labelsField = waitForElementVisible(labelCombobox)
                .findElement(By.xpath("./ancestor::div[contains(@class,'MuiFormControl-root')]"));
        return labelsField.findElements(By.xpath(".//*[contains(@class,'MuiChip-label')]")).stream()
                .map(element -> element.getText().trim())
                .filter(text -> !text.isBlank())
                .toList();
    }

    private void selectLabels(List<String> labels) {
        By listbox = By.xpath("//*[@role='listbox']");
        waitForElementClickable(labelCombobox).click();
        waitForElementVisible(listbox);
        for (String label : labels) {
            By option = By.xpath("//*[@role='listbox']//*[@role='option'][contains(normalize-space(.),"
                    + xpathLiteral(label.trim()) + ")]");
            waitForElementClickable(option).click();
        }
        closeOpenListbox(listbox);
    }

    private void closeOpenListbox(By listbox) {
        new Actions(getDriver()).sendKeys(Keys.ESCAPE).perform();
        try {
            waitForElementInvisible(listbox);
        } catch (TimeoutException e) {
            List<WebElement> backdrops = getDriver().findElements(
                    By.xpath("//*[contains(@class,'MuiBackdrop-root')]"));
            if (!backdrops.isEmpty()) {
                backdrops.getFirst().click();
            }
            waitForElementInvisible(listbox);
        }
    }
}
