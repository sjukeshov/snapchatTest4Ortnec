package ui;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.apache.log4j.BasicConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SnapchatUI {
    private static final By BUTTON_LOGIN = By.id("com.snapchat.android:id/login_and_signup_page_fragment_login_button");
    private static final By TEXTBOX_USERNAME_OR_EMAIL = By.id("com.snapchat.android:id/username_or_email_field");
    private static final By TEXTBOX_PASSWORD = By.id("com.snapchat.android:id/password_field");
    private static final By BUTTON_NEXT = By.id("com.snapchat.android:id/button_text");
    private static final By buttonFriends = By.id("com.snapchat.android:id/hova_nav_feed_label");
    private static final By listLabelsOfFriends = By.id("com.snapchat.android:id/title");
    private static final By textboxMessage = By.id("com.snapchat.android:id/chat_input_text_field");
    private static final By listMessages = By.id("com.snapchat.android:id/chat_message_user_text");

    private final Logger logger = LoggerFactory.getLogger(SnapchatUI.class);
    private final AndroidDriver<MobileElement> driver;
    private final WebDriverWait wait;
    private final String username;
    private final String password;

    public SnapchatUI(AndroidDriver<MobileElement> driver, String username, String password) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 30);
        this.username = username;
        this.password = password;
        //Just to put sl4j logs to console
        BasicConfigurator.configure();
    }

    public void login() {
        logger.info("Logging in as {}", username);
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(BUTTON_LOGIN)));
        driver.findElement(BUTTON_LOGIN).click();
        driver.findElement(TEXTBOX_USERNAME_OR_EMAIL).sendKeys(username);
        driver.findElement(TEXTBOX_PASSWORD).sendKeys(password);
        driver.findElement(BUTTON_NEXT).click();
    }

    public void sendMessage(String messageToSend) {
        logger.info("Sending message {}", messageToSend);
        driver.findElement(SnapchatUI.textboxMessage).setValue(messageToSend);
        driver.pressKey(new KeyEvent(AndroidKey.ENTER));
    }

    public String getLastSentMessage() {
        List<MobileElement> messagesList = driver.findElements(SnapchatUI.listMessages);
        logger.info("Got {} messages", messagesList.size());
        return messagesList.get(messagesList.size() - 1).getText();
    }

    public void allowAllAppPermissions() {
        logger.info("Allowing app permissions");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        while (driver.findElements(MobileBy.xpath("//*[@class='android.widget.Button'][1]")).size() > 0) {
            driver.findElement(MobileBy.xpath("//*[@class='android.widget.Button'][1]")).click();
        }
        //I bring back Implicit wait to 5 seconds after permisssions so test would work a bit faster
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    public void clickOnFriendToChat(String friendsName) {
        logger.info("Starting chat with friend {}", friendsName);
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(SnapchatUI.buttonFriends)));
        driver.findElement(SnapchatUI.buttonFriends).click();
        List<MobileElement> friendsList = driver.findElements(SnapchatUI.listLabelsOfFriends);
        for (WebElement friend : friendsList) {
            if (friend.getText().equals(friendsName)) {
                friend.click();
                break;
            }
        }
    }
}
