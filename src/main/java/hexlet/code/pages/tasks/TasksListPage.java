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
    private static final int DRAG_WAIT_SECONDS = 15;
    private static final int DRAG_FALLBACK_WAIT_SECONDS = 3;
    private static final int DRAG_MIN_OFFSET = 10;
    private static final long DRAG_PAUSE_MS = 300L;
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
        return getDriver().findElements(columnTitle).stream()
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
        return getDriver().findElements(taskTitle).size();
    }

    public TaskByIdPage clickCreateTask() {
        waitForSnackbarToDisappear();
        clickElement(createButton);
        return new TaskByIdPage(getDriver());
    }

    public TaskByIdForm openTaskShowByTitle(String title) {
        requireTaskCardElement(title).findElement(showButton).click();
        return new TaskByIdForm(getDriver());
    }

    public TaskByIdPage openTaskEditByTitle(String title) {
        WebElement card = requireTaskCardElement(title);
        scrollIntoView(card);
        waitForSnackbarToDisappear();
        clickElement(card.findElement(By.xpath(".//*[@data-testid='CreateIcon']")));
        return new TaskByIdPage(getDriver());
    }

    public TasksListPage updateTaskByTitle(String title, Task updatedTask) {
        TasksListPage tasksListPage = openTaskEditByTitle(title).updateTaskAndReturnToBoard(updatedTask);
        if (updatedTask.getStatusName() != null) {
            waitForCondition(driver -> tasksListPage.isTaskInColumn(title, updatedTask.getStatusName()));
        }
        return tasksListPage;
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

    public void filterByLabel(String labelName) {
        if (labelName == null || labelName.isEmpty()) {
            return;
        }
            waitForElementClickable(labelFilter).click();
            By option = By.xpath(
                    "//*[@role='listbox']//*[@role='option'][normalize-space(.)=" + xpathLiteral(labelName) + "]"
            );
            waitForElementClickable(option).click();
        waitForPageLoaded();
    }


    public boolean waitUntilTaskHidden(String title) {
        return waitForCondition(driver -> !isTaskExists(title));
    }

    public void moveTaskToColumnByDrag(String title, String targetColumnName) {
        waitForCondition(driver -> isTaskExists(title));

        WebElement sourceCard = requireTaskCardElement(title);
        WebElement targetColumn = findColumnContent(targetColumnName);

        performDragToColumn(sourceCard, targetColumn);
        if (!waitForConditionOptional(
                driver -> isTaskInColumn(title, targetColumnName), Duration.ofSeconds(DRAG_FALLBACK_WAIT_SECONDS))) {
            dragAndDrop(sourceCard, targetColumn);
        }
        waitForCondition(driver -> isTaskInColumn(title, targetColumnName), Duration.ofSeconds(DRAG_WAIT_SECONDS));
    }

    private void performDragToColumn(WebElement sourceCard, WebElement targetColumn) {
        WebElement dragSource = findDragHandle(sourceCard);
        scrollIntoView(dragSource);
        scrollIntoView(targetColumn);

        int targetX = Math.max(DRAG_MIN_OFFSET, targetColumn.getSize().getWidth() / 2);
        int targetY = Math.max(DRAG_MIN_OFFSET, targetColumn.getSize().getHeight() / 2);

        new Actions(getDriver())
                .moveToElement(dragSource)
                .clickAndHold()
                .moveByOffset(DRAG_MIN_OFFSET, 0)
                .pause(Duration.ofMillis(DRAG_PAUSE_MS))
                .moveToElement(targetColumn, targetX, targetY)
                .pause(Duration.ofMillis(DRAG_PAUSE_MS))
                .release()
                .perform();
    }

    private WebElement findDragHandle(WebElement card) {
        List<WebElement> handles = card.findElements(By.xpath(
                ".//*[@data-testid='DragIndicatorIcon']"
                        + " | .//*[@data-testid='DragHandleIcon']"
                        + " | .//*[contains(@class,'drag')]"
        ));
        if (!handles.isEmpty()) {
            return handles.getFirst();
        }
        return card;
    }

    public TasksListPage moveTaskToStatusByEdit(String title, String newStatusName) {
        TasksListPage tasksListPage = openTaskEditByTitle(title)
                .updateTaskAndReturnToBoard(new Task(title, null, null, newStatusName));
        waitForCondition(driver -> tasksListPage.isTaskInColumn(title, newStatusName));
        return tasksListPage;
    }

    public void deleteTaskByTitle(String title) {
        openTaskEditByTitle(title).deleteTask();
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
            return getDriver().findElement(By.xpath(
                    "//*[contains(@class,'MuiTypography-h5') and normalize-space(text())="
                            + xpathLiteral(title)
                            + "]/ancestor::div[contains(@class,'MuiCard-root')]"
            ));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private WebElement findColumnContent(String columnName) {
        return getDriver().findElement(By.xpath(
                "//*[contains(@class,'MuiTypography-subtitle1') and normalize-space(text())="
                        + xpathLiteral(columnName) + "]/following-sibling::div[1]"
        ));
    }

    private void selectFilterOption(By filterCombobox, String optionText) {
        selectComboboxOption(filterCombobox, optionText);
        waitForPageLoaded();
    }
}
