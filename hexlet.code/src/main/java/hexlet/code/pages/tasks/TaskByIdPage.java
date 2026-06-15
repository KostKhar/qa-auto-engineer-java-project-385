package hexlet.code.pages.tasks;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TaskByIdPage {

    private WebDriver driver;

    public TaskByIdPage(WebDriver driver) {
        this.driver = driver;
    }

    private By idLocator =  By.xpath("(//*[@class='MuiTypography-root MuiTypography-body2 css-68o8xu'])[1]");
    private By createdAtLocator =  By.xpath("(//*[@class='MuiTypography-root MuiTypography-body2 css-68o8xu'])[2]");

    private By assigneeField =  By.xpath("(//*[@class='MuiSelect-select MuiSelect-filled MuiInputBase-input MuiFilledInput-input MuiInputBase-inputSizeSmall css-z7ohec'])[1]");
    private By statusField =  By.xpath("(//*[@class='MuiSelect-select MuiSelect-filled MuiInputBase-input MuiFilledInput-input MuiInputBase-inputSizeSmall css-z7ohec'])[2]");

    private By titleField =  By.xpath("(//*[@name='title']");
    private By contentField =  By.xpath("(//*[@name='content']");


    private By labelField =  By.className("RaSelectArrayInput-chips");

    private By criticalLabel = By.xpath("(//*[@class='MuiButtonBase-root MuiMenuItem-root MuiMenuItem-gutters MuiMenuItem-root MuiMenuItem-gutters css-1bo1rz0'])[1]");
    private By taskLabel = By.xpath("(//*[@class='MuiButtonBase-root MuiMenuItem-root MuiMenuItem-gutters MuiMenuItem-root MuiMenuItem-gutters css-1bo1rz0'])[2]");

    private By enhancementLabel = By.xpath("(//*[@class='MuiChip-label MuiChip-labelSmall css-1pjtbja'])[1]");
    private By featureLabel = By.xpath("(//*[@class='MuiChip-label MuiChip-labelSmall css-1pjtbja'])[2]");
    private By bugLabel = By.xpath("(//*[@class='MuiChip-label MuiChip-labelSmall css-1pjtbja'])[3]");

    private By saveButton = By.xpath("(//*[@aria-label='Save'])");
    private By deleteButton = By.xpath("(//*[@aria-label='Delete'])");






}
