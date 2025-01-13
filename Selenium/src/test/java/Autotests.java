import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class Autotests {
    WebDriver _globalDriver;

    @BeforeTest
    public void SetupWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        _globalDriver = new ChromeDriver();
        _globalDriver.get("https://advisorflow.dariussongaila.dev/");
        _globalDriver.manage().window().maximize();
        _globalDriver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

    }

    @Test
    public void registrationAndLogin() throws InterruptedException {
        //Click register
        _globalDriver.findElement(By.xpath("/html/body/div/header/div/button[2]")).click();
        //Name
        _globalDriver.findElement(By.xpath("/html/body/div/div/div/form/div[2]/input")).sendKeys("Tomas");
        //Last Name
        _globalDriver.findElement(By.xpath("/html/body/div/div/div/form/div[3]/input")).sendKeys("Tomelis");
        //Email
        _globalDriver.findElement(By.xpath("/html/body/div/div/div/form/div[4]/input")).sendKeys("tomas@pastas.com");
        //Number
        _globalDriver.findElement(By.xpath("/html/body/div/div/div/form/div[5]/input")).sendKeys("+37063076806");
        //Password
        _globalDriver.findElement(By.xpath("/html/body/div/div/div/form/div[6]/input")).sendKeys("test123");
        //Confirm Password
        _globalDriver.findElement(By.xpath("/html/body/div/div/div/form/div[7]/input")).sendKeys("test123");
        //Birth Date
        _globalDriver.findElement(By.xpath("/html/body/div/div/div/form/div[8]/input")).sendKeys("01/06/1997");
        //Register button
        _globalDriver.findElement(By.xpath("/html/body/div/div/div/form/button")).click();
        //Login email
        _globalDriver.findElement(By.xpath("/html/body/div/div/form/div[2]/input")).sendKeys("tomas@pastas.com");
        //Login password
        _globalDriver.findElement(By.xpath("/html/body/div/div/form/div[3]/input")).sendKeys("test123");
        //Login button
        _globalDriver.findElement(By.xpath("/html/body/div/div/form/button")).click();
        //Email check
        String logout = _globalDriver.findElement(By.xpath("/html/body/div/header[2]/div/button[2]")).getText();
        Assert.assertEquals(logout, "Logout");
    }

    @Test
    public void testCategoryItemClick() throws InterruptedException {
        Thread.sleep(3000);
        WebElement automobiliuKategorijas = _globalDriver.findElement(By.xpath("/html/body/div[1]/div[3]/a[2]"));
        automobiliuKategorijas.click();
        WebElement kiekioLaikiklis = _globalDriver.findElement(By.xpath("/html/body/div[1]/div[5]/div[1]/div[1]/span"));
        int count = Integer.parseInt(kiekioLaikiklis.getText());
        Assert.assertNotEquals(count, 0);
    }


}