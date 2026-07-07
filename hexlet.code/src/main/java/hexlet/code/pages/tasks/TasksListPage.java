package hexlet.code.pages.tasks;

import hexlet.code.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class TasksListPage extends BasePage {
    private final By assigneeFilter = By.xpath(
            "(//*[contains(@class,'RaFilterForm')]//div[@role='combobox'])[1]"
    );
    private final By statusFilter = By.xpath(
            "(//*[contains(@class,'RaFilterForm')]//div[@role='combobox'])[2]"
    );
    private final By labelFilter = By.xpath(
            "(//*[contains(@class,'RaFilterForm')]//div[@role='combobox'])[3]"
    );

    private final By createButton = By.xpath("//*[@data-testid='AddIcon']");
    private final By showButton = By.xpath(".//*[@data-testid='RemoveRedEyeIcon']");
    private final By exportButton = By.xpath("//*[@data-testid='GetAppIcon']");
    private final By columnTitle = By.xpath("//*[contains(@class,'MuiTypography-subtitle1')]");
    private final By taskTitle = By.xpath("//*[contains(@class,'MuiTypography-h5')]");

    public TasksListPage(WebDriver driver) {
        super(driver);
    }

    public boolean isBoardVisible() {
        waitForElementVisible(columnTitle);
        return true;
    }

    public boolean isBoardLoaded() {
        waitForElementVisible(columnTitle);
        return getVisibleTaskCount() > 0;
    }

    public boolean isCreateButtonVisible() {
        return waitForElementVisible(createButton).isDisplayed();
    }

    public boolean isExportButtonVisible() {
        return waitForElementVisible(exportButton).isDisplayed();
    }

    public List<String> getColumnNames() {
        return driver.findElements(columnTitle).stream()
                .map(element -> element.getText().trim())
                .filter(name -> !name.isBlank())
                .toList();
    }

    public boolean hasColumns(String... expectedColumns) {
        List<String> columns = getColumnNames();
        return Arrays.stream(expectedColumns)
                .allMatch(expected -> columns.stream()
                        .anyMatch(column -> column.equalsIgnoreCase(expected)));
    }

    public int getVisibleTaskCount() {
        return driver.findElements(taskTitle).size();
    }

    public TaskByIdPage clickCreateTask() {
        waitForElementClickable(createButton).click();
        return new TaskByIdPage(driver);
    }

    public TaskByIdForm openTaskShowByTitle(String title) {
        requireTaskCardElement(title).findElement(showButton).click();
        return new TaskByIdForm(driver);
    }

    public TaskByIdPage openTaskEditByTitle(String title) {
        waitForElementClickable(editButtonByTitle(title)).click();
        return new TaskByIdPage(driver);
    }

    public TasksListPage updateTaskByTitle(String title, Task updatedTask) {
        return openTaskEditByTitle(title).updateTaskAndReturnToBoard(updatedTask);
    }

    public boolean isTaskExists(String title) {
        return findTaskCardElement(title) != null;
    }

    public boolean isTaskNotExists(String title) {
        return !isTaskExists(title);
    }

    public boolean isTaskInColumn(String title, String columnName) {
        try {
            WebElement column = findColumnContent(columnName);
            return !column.findElements(By.xpath(
                    ".//*[contains(@class,'MuiTypography-h5') and normalize-space(text())="
                            + xpathLiteral(title) + "]"
            )).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public void filterByAssignee(String assigneeEmail) {
        selectFilterOption(assigneeFilter, assigneeEmail);
    }

    public void filterByStatus(String statusName) {
        selectFilterOption(statusFilter, statusName);
    }

    public void filterByLabel(List<String> labelNames) {
        if (labelNames == null || labelNames.isEmpty()) {
            return;
        }
        for (String labelName : labelNames) {
            waitForElementClickable(labelFilter).click();
            By option = By.xpath(
                    "//*[@role='listbox']//*[@role='option'][normalize-space(.)=" + xpathLiteral(labelName) + "]"
            );
            waitForElementClickable(option).click();
        }
        waitForPageLoaded();
    }

    public void filterByLabel(String labelName) {
        filterByLabel(List.of(labelName));
    }

    public boolean waitUntilTaskHidden(String title) {
        return waitForCondition(driver -> !isTaskExists(title));
    }

    public void moveTaskToColumnByDrag(String title, String targetColumnName) {
        WebElement sourceCard = requireTaskCardElement(title);
        WebElement targetColumn = findColumnContent(targetColumnName);

        new Actions(driver)
                .clickAndHold(sourceCard)
                .moveByOffset(10, 0)
                .moveToElement(targetColumn, 10, 100)
                .pause(Duration.ofMillis(400))
                .release()
                .perform();
        waitForCondition(driver -> isTaskInColumn(title, targetColumnName));
    }

    public TasksListPage moveTaskToStatusByEdit(String title, String newStatusName) {
        return openTaskEditByTitle(title)
                .updateTaskAndReturnToBoard(new Task(null, null, null, newStatusName));
    }

    public void deleteTaskByTitle(String title) {
        openTaskEditByTitle(title).deleteTask();
    }

    private By editButtonByTitle(String title) {
        return By.xpath(
                "//*[contains(@class,'MuiTypography-h5') and normalize-space(text())="
                        + xpathLiteral(title)
                        + "]/ancestor::div[contains(@class,'MuiCard-root')]//*[@data-testid='CreateIcon']"
        );
    }

    private WebElement requireTaskCardElement(String title) {
        WebElement card = findTaskCardElement(title);
        if (card == null) {
            throw new IllegalArgumentException(String.format("Task with title '%s' does not exist", title));
        }
        return card;
    }

    private WebElement findTaskCardElement(String title) {
        try {
            return driver.findElement(By.xpath(
                    "//*[contains(@class,'MuiTypography-h5') and normalize-space(text())="
                            + xpathLiteral(title)
                            + "]/ancestor::div[contains(@class,'MuiCard-root')]"
            ));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private WebElement findColumnContent(String columnName) {
        return driver.findElement(By.xpath(
                "//*[contains(@class,'MuiTypography-subtitle1') and normalize-space(text())="
                        + xpathLiteral(columnName) + "]/following-sibling::div[1]"
        ));
    }

    private void selectFilterOption(By filterCombobox, String optionText) {
        selectComboboxOption(filterCombobox, optionText);
        waitForPageLoaded();
    }
}
