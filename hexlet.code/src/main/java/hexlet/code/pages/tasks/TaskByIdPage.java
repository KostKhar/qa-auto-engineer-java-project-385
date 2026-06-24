package hexlet.code.pages.tasks;

import hexlet.code.components.SideBar;
import hexlet.code.pages.BasePage;
import org.openqa.selenium.*;

import java.util.List;

public class TaskByIdPage extends BasePage {
    private final By titleField = By.xpath("//*[@name='title']");
    private final By contentField = By.xpath("//*[@name='content']");
    private final By assigneeCombobox = By.xpath(
            "(//*[@name='assignee_id']/ancestor::div[contains(@class,'MuiFormControl-root')]//div[@role='combobox'])[1]"
    );
    private final By statusCombobox = By.xpath(
            "(//*[@name='status_id']/ancestor::div[contains(@class,'MuiFormControl-root')]//div[@role='combobox'])[1]"
    );
    private final By labelCombobox = By.xpath(
            "//*[contains(@class,'RaSelectArrayInput')]//div[@role='combobox']"
    );
    private final By saveButton = By.xpath("//*[@aria-label='Save']");
    private final By deleteButton = By.xpath("//*[@aria-label='Delete']");
    private final By confirmDeleteButton = By.xpath("//*[@role='dialog']//button[contains(text(), 'Confirm')]");
    private final By successCreatePopup = By.xpath("//*[contains(text(), 'Element created')]");
    private final By successUpdatedPopup = By.xpath("//*[contains(text(), 'Element updated')]");
    private final By successDeletePopup = By.xpath("//*[contains(text(), 'Element deleted')]");
    private final By validationError = By.xpath(
            "//*[contains(@class, 'MuiFormHelperText-root') and contains(@class, 'Mui-error')]"
    );

    public TaskByIdPage(WebDriver driver) {
        super(driver);
    }

    public void fillTaskForm(Task task) {
        if (task.getTitle() != null) {
            waitForElementClearAndSendKeys(titleField, task.getTitle());
        }
        if (task.getContent() != null) {
            waitForElementClearAndSendKeys(contentField, task.getContent());
        }
        if (task.getAssigneeEmail() != null) {
            selectComboboxOption(assigneeCombobox, task.getAssigneeEmail());
        }
        if (task.getStatusName() != null) {
            selectComboboxOption(statusCombobox, task.getStatusName());
        }
        if (task.getLabels() != null && !task.getLabels().isEmpty()) {
            selectLabels(task.getLabels());
        }
    }

    public TasksListPage createTaskAndReturnToBoard(Task task) {
        fillTaskForm(task);
        waitForElementClickable(saveButton).click();
        waitForElementVisible(successCreatePopup);
        return new SideBar(driver).getTaskListPage();
    }

    public void updateTask(Task task) {
        fillTaskForm(task);
        waitForElementClickable(saveButton).click();
        try {
            waitForElementVisible(successUpdatedPopup);
        } catch (TimeoutException ignored) {
            waitForPageLoaded();
        }
    }

    public TasksListPage updateTaskAndReturnToBoard(Task task) {
        updateTask(task);
        return new SideBar(driver).getTaskListPage();
    }

    public void submitFormWithoutWaitingForSuccess() {
        waitForElementClickable(saveButton).click();
    }

    public void deleteTask() {
        waitForElementClickable(deleteButton).click();
        try {
            waitForElementClickable(confirmDeleteButton).click();
            waitForElementVisible(successDeletePopup);
        } catch (TimeoutException e) {
            waitForCondition(driver -> {
                String url = driver.getCurrentUrl();
                return url.endsWith("#/tasks") || url.endsWith("#/tasks/");
            });
        }
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
                || hasVisibleGlobalValidationError();
    }

    public boolean isTitleValidationErrorVisible() {
        return hasBrowserValidationMessage(titleField) || hasVisibleValidationError(titleField);
    }

    public boolean isAssigneeValidationErrorVisible() {
        return hasVisibleValidationError(assigneeCombobox);
    }

    public boolean isStatusValidationErrorVisible() {
        return hasVisibleValidationError(statusCombobox);
    }

    private void selectComboboxOption(By combobox, String optionText) {
        WebElement field = waitForElementClickable(combobox);
        if (field.getText().trim().equals(optionText)) {
            return;
        }
        field.click();
        By option = By.xpath(
                "//*[@role='listbox']//*[@role='option'][normalize-space(.)="
                        + xpathLiteral(optionText) + "]"
        );
        waitForElementClickable(option).click();
    }

    private void selectLabels(List<String> labels) {
        for (String label : labels) {
            WebElement field = waitForElementClickable(labelCombobox);
            field.click();
            By option = By.xpath(
                    "//*[@role='listbox']//*[@role='option'][contains(normalize-space(.),"
                            + xpathLiteral(label.trim()) + ")]"
            );
            waitForElementClickable(option).click();
        }
    }

    private boolean hasBrowserValidationMessage(By field) {
        WebElement element = waitForElementVisible(field);
        Object message = ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].validationMessage;", element
        );
        return message != null && !message.toString().isBlank();
    }

    private boolean hasVisibleValidationError(By field) {
        try {
            WebElement element = waitForElementVisible(field);
            List<WebElement> errors = element.findElements(By.xpath(
                    "./ancestor::div[contains(@class, 'MuiFormControl-root')]"
                            + "//*[contains(@class, 'MuiFormHelperText-root') and contains(@class, 'Mui-error')]"
            ));
            return errors.stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean hasVisibleGlobalValidationError() {
        try {
            waitForElementVisible(validationError);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
