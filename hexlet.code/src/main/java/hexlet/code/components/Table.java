package hexlet.code.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static hexlet.code.config.ConfigurationManager.config;

public class Table<T> {
    private static final By TABLE_BODY = By.xpath(".//tbody");
    private static final By TABLE_HEADER = By.xpath(".//thead");

    private final WebElement tableContainer;
    private final RowMapper<T> rowMapper;
    private final WebDriverWait wait;
    private final WebElement headerRow;
    private final WebElement tbody;

    public Table(WebDriver driver, WebElement tableContainer, RowMapper<T> rowMapper) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(config().timeout()));
        this.tableContainer = tableContainer;
        this.rowMapper = rowMapper;
        this.headerRow = tableContainer.findElement(TABLE_HEADER);
        this.tbody = tableContainer.findElement(TABLE_BODY);
    }

    public List<WebElement> getRows() {
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
        List<WebElement> cells = row.findElements(By.xpath(".//td"));
        if (colIndex < 0 || colIndex >= cells.size()) {
            throw new IndexOutOfBoundsException("Column index " + colIndex + " out of bounds. Row has " + cells.size() + " cells.");
        }
        return cells.get(colIndex);
    }

    public String getCellText(int rowIndex, int colIndex) {
        return getCell(rowIndex, colIndex).getText().trim();
    }

    public List<List<String>> getTableData() {
        List<List<String>> data = new ArrayList<>();
        List<WebElement> rows = getRows();

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.xpath(".//td"));
            List<String> rowData = cells.stream()
                    .map(cell -> cell.getText().trim())
                    .toList();
            data.add(rowData);
        }

        return data;
    }

    public List<String> getHeaders() {
        wait.until(ExpectedConditions.visibilityOf(headerRow));
        List<WebElement> headers = headerRow.findElements(By.xpath(".//th"));
        return headers.stream()
                .map(WebElement::getText)
                .toList();
    }

    public WebElement findRowByColumnValue(int colIndex, String expectedValue) {
        List<WebElement> rows = getRows();

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.xpath(".//td"));
            if (colIndex < cells.size()) {
                String cellText = cells.get(colIndex).getText().trim();
                if (cellText.equals(expectedValue)) {
                    return row;
                }
            }
        }

        return null;
    }

    public WebElement findRowByColumnValueIgnoreCase(int colIndex, String expectedValue) {
        List<WebElement> rows = getRows();

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.xpath(".//td"));
            if (colIndex < cells.size()) {
                String cellText = cells.get(colIndex).getText().trim();
                if (cellText.equalsIgnoreCase(expectedValue)) {
                    return row;
                }
            }
        }

        return null;
    }

    public List<WebElement> findRowsByColumnContains(int colIndex, String partialValue) {
        List<WebElement> matchedRows = new ArrayList<>();
        List<WebElement> rows = getRows();

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.xpath(".//td"));
            if (colIndex < cells.size()) {
                String cellText = cells.get(colIndex).getText().trim();
                if (cellText.contains(partialValue)) {
                    matchedRows.add(row);
                }
            }
        }

        return matchedRows;
    }

    public List<T> getRowsAsObjects() {
        List<T> objects = new ArrayList<>();
        List<WebElement> rows = getRows();

        for (WebElement row : rows) {
            objects.add(rowMapper.map(row));
        }

        return objects;
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
        return row != null ? rowMapper.map(row) : null;
    }

    @FunctionalInterface
    public interface RowMapper<T> {
        T map(WebElement row);
    }
}
