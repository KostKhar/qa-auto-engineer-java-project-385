package hexlet.code.components;

import hexlet.code.actions.ElementAction;
import hexlet.code.actions.Waiter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static hexlet.code.configure.ConfigurationManager.config;

public final class Table<T> {
    public static final By CREATE_BUTTON = By.xpath("//*[@aria-label='Create']");
    public static final By DELETE_BUTTON = By.xpath("//*[@aria-label='Delete']");
    public static final By SELECT_ALL_CHECKBOX = By.xpath(
            "//*[contains(@class, 'RaList-main')]//thead//span[contains(@class, 'MuiCheckbox-root')]"
    );
    public static final By SUCCESS_DELETE_POPUP = By.xpath("//*[contains(text(), 'deleted')]");
    public static final By TABLE_CONTAINER = By.className("RaList-main");
    public static final By LIST_ROOT = By.xpath(
            "//*[@aria-label='Create']/ancestor::div[contains(@class, 'RaList')][1]"
    );

    private static final By TABLE_BODY = By.xpath(".//tbody");
    private static final By TABLE_HEADER = By.xpath(".//thead");
    private static final By CELL = By.xpath(".//td");

    private final WebDriver driver;
    private final ElementAction elementAction;
    private final Waiter waiter;
    private final WebElement tableContainer;
    private final RowMapper<T> rowMapper;
    private final WebDriverWait wait;

    private Table(WebDriver driver, WebElement tableContainer, RowMapper<T> rowMapper) {
        this.driver = driver;
        this.elementAction = new ElementAction(driver);
        this.waiter = new Waiter(driver);
        this.tableContainer = tableContainer;
        this.rowMapper = rowMapper;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(config().timeout()));
    }

    public static <T> Table<T> create(WebDriver driver, RowMapper<T> rowMapper) {
        return new Table<>(driver, waitForTableContainer(driver), rowMapper);
    }

    public static <T> Table<T> createForList(WebDriver driver, RowMapper<T> rowMapper) {
        return new Table<>(driver, waitForListTableContainer(driver), rowMapper);
    }

    public static WebElement waitForTableContainer(WebDriver driver) {
        Waiter waiter = new Waiter(driver);
        List<WebElement> containers = driver.findElements(TABLE_CONTAINER);
        if (!containers.isEmpty()) {
            return waiter.getWait().until(ExpectedConditions.visibilityOfElementLocated(TABLE_CONTAINER));
        }
        return waiter.getWait().until(ExpectedConditions.visibilityOfElementLocated(LIST_ROOT));
    }

    public static WebElement waitForListTableContainer(WebDriver driver) {
        Waiter waiter = new Waiter(driver);
        return waiter.getWait().until(ExpectedConditions.visibilityOfElementLocated(TABLE_CONTAINER));
    }

    public void waitForReady() {
        elementAction.find(CREATE_BUTTON).waitUntilVisible();
    }

    public boolean hasColumnHeaders(String... expectedHeaders) {
        List<String> headers = getHeaders();
        return Arrays.stream(expectedHeaders)
                .allMatch(expected -> headers.stream()
                        .anyMatch(header -> header.equalsIgnoreCase(expected)));
    }

    public boolean isCreateButtonVisible() {
        return elementAction.find(CREATE_BUTTON).waitUntilVisible().isDisplayed();
    }

    public boolean isTableLoaded() {
        elementAction.find(TABLE_CONTAINER).waitUntilVisible();
        return !getRows().isEmpty();
    }

    public boolean isTableContainerVisible() {
        elementAction.find(TABLE_CONTAINER).waitUntilVisible();
        return true;
    }

    public boolean isVisible() {
        elementAction.find(CREATE_BUTTON).waitUntilVisible();
        return !driver.findElements(TABLE_CONTAINER).isEmpty()
                || elementAction.find(LIST_ROOT).waitUntilVisible().isDisplayed();
    }

    public int getRowCount() {
        return getRows().size();
    }

    public void selectAllRows() {
        elementAction.find(SELECT_ALL_CHECKBOX).waitUntilClickable().click();
    }

    public void deleteSelectedRows() {
        elementAction.find(DELETE_BUTTON).waitUntilClickable().click();
        elementAction.find(SUCCESS_DELETE_POPUP).waitUntilVisible();
    }

    public void clickCreateButton() {
        waiter.waitForSnackbarToDisappear();
        elementAction.find(CREATE_BUTTON).waitUntilClickable().click();
    }

    public List<WebElement> getRows() {
        List<WebElement> tbodies = tableContainer.findElements(TABLE_BODY);
        if (tbodies.isEmpty()) {
            return List.of();
        }
        WebElement tbody = tbodies.getFirst();
        wait.until(ExpectedConditions.visibilityOf(tbody));
        return tableContainer.findElements(By.xpath(".//tbody//tr"));
    }

    public WebElement getRow(int index) {
        List<WebElement> rows = getRows();
        if (index < 0 || index >= rows.size()) {
            throw new IndexOutOfBoundsException("Row index " + index + " out of bounds. Table has " + rows.size() + " rows.");
        }
        return rows.get(index);
    }

    public WebElement getCell(int rowIndex, int colIndex) {
        WebElement row = getRow(rowIndex);
        List<WebElement> cells = row.findElements(CELL);
        if (colIndex < 0 || colIndex >= cells.size()) {
            throw new IndexOutOfBoundsException("Column index " + colIndex + " out of bounds. Row has " + cells.size() + " cells.");
        }
        return cells.get(colIndex);
    }

    public String getCellText(int rowIndex, int colIndex) {
        return getCell(rowIndex, colIndex).getText().trim();
    }

    public List<String> getHeaders() {
        WebElement headerRow = tableContainer.findElement(TABLE_HEADER);
        wait.until(ExpectedConditions.visibilityOf(headerRow));
        List<WebElement> headers = headerRow.findElements(By.xpath(".//th"));
        return headers.stream()
                .map(WebElement::getText)
                .toList();
    }

    public WebElement findRowByColumnValue(int colIndex, String expectedValue) {
        List<WebElement> rows = getRows();

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(CELL);
            if (colIndex < cells.size()) {
                String cellText = cells.get(colIndex).getText().trim();
                if (cellText.equals(expectedValue)) {
                    return row;
                }
            }
        }

        return null;
    }

    public T getRowAsObject(int index) {
        WebElement row = getRow(index);
        return rowMapper.map(row);
    }

    public boolean containsValueInColumn(int colIndex, String value) {
        return findRowByColumnValue(colIndex, value) != null;
    }

    public T findRowObjectByColumnValue(int colIndex, String expectedValue) {
        WebElement row = findRowByColumnValue(colIndex, expectedValue);
        if (row == null) {
            return null;
        }
        return rowMapper.map(row);
    }

    @FunctionalInterface
    public interface RowMapper<T> {
        T map(WebElement row);
    }
}
