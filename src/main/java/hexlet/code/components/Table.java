package hexlet.code.components;

import hexlet.code.actions.ElementAction;
import hexlet.code.actions.Waiter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

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
    private static final By SNACKBAR = By.xpath("//*[contains(@class,'MuiSnackbar-root')]");

    private static final By TABLE_BODY = By.xpath(".//tbody");
    private static final By TABLE_HEADER = By.xpath(".//thead");
    private static final By CELL = By.xpath(".//td");

    private final WebDriver driver;
    private final ElementAction elementAction;
    private final Waiter waiter;
    private final WebElement tableContainer;
    private final RowMapper<T> rowMapper;

    private Table(WebDriver driver, WebElement tableContainer, RowMapper<T> rowMapper) {
        this.driver = driver;
        this.elementAction = new ElementAction(driver);
        this.waiter = new Waiter(driver);
        this.tableContainer = tableContainer;
        this.rowMapper = rowMapper;
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
            return waiter.waitForVisible(TABLE_CONTAINER);
        }
        return waiter.waitForVisible(LIST_ROOT);
    }

    public static WebElement waitForListTableContainer(WebDriver driver) {
        return new Waiter(driver).waitForVisible(TABLE_CONTAINER);
    }

    public void waitForReady() {
        waiter.waitForVisible(CREATE_BUTTON);
    }

    public boolean hasColumnHeaders(String... expectedHeaders) {
        List<String> headers = getHeaders();
        return Arrays.stream(expectedHeaders)
                .allMatch(expected -> headers.stream()
                        .anyMatch(header -> header.equalsIgnoreCase(expected)));
    }

    public boolean isCreateButtonVisible() {
        return elementAction.find(CREATE_BUTTON).isDisplayed();
    }

    public boolean isTableLoaded() {
        waiter.waitForVisible(TABLE_CONTAINER);
        return !getRows().isEmpty();
    }

    public boolean isTableContainerVisible() {
        waiter.waitForVisible(TABLE_CONTAINER);
        return true;
    }

    public boolean isVisible() {
        waiter.waitForVisible(CREATE_BUTTON);
        return !driver.findElements(TABLE_CONTAINER).isEmpty()
                || elementAction.find(LIST_ROOT).isDisplayed();
    }

    public int getRowCount() {
        return getRows().size();
    }

    public void selectAllRows() {
        elementAction.find(SELECT_ALL_CHECKBOX).click();
    }

    public void deleteSelectedRows() {
        elementAction.find(DELETE_BUTTON).click();
        waiter.waitForVisible(SUCCESS_DELETE_POPUP);
    }

    public void clickCreateButton() {
        waiter.waitForInvisibleIfPresent(SNACKBAR);
        elementAction.find(CREATE_BUTTON).click();
    }

    public List<WebElement> getRows() {
        List<WebElement> tbodies = tableContainer.findElements(TABLE_BODY);
        if (tbodies.isEmpty()) {
            return List.of();
        }
        WebElement tbody = tbodies.getFirst();
        waiter.waitForVisible(tbody);
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
        waiter.waitForVisible(headerRow);
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
