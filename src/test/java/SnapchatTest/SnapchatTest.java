package SnapchatTest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.restassured.RestAssured;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import ui.SnapchatUI;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class SnapchatTest {
    private AndroidDriver<MobileElement> driver;
    private SnapchatUI snapchatUI;

    @Before
    public void setUp() throws MalformedURLException {
        JsonParser parser = new JsonParser();
        InputStream stream = getClass().getResourceAsStream("/settings.json");
        JsonObject settings = parser.parse(new InputStreamReader(stream)).getAsJsonObject();

        // Created object of DesiredCapabilities class.
        DesiredCapabilities capabilities = new DesiredCapabilities();
        // Set android deviceName desired capability. Set your device name.
        capabilities.setCapability("deviceName", settings.get("deviceName").getAsString());
        // Set android VERSION desired capability. Set your mobile device's OS version.
        capabilities.setCapability(CapabilityType.VERSION, settings.get("osversion").getAsString());
        // Set android platformName desired capability. It's Android in our case here.
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appPackage", "com.snapchat.android");
        capabilities.setCapability("appActivity", "LandingPageActivity");
        capabilities.setCapability("appiumVersion", "1.10.1");
        capabilities.setCapability("automationName", "UiAutomator2");
        capabilities.setCapability(AndroidMobileCapabilityType.AUTO_GRANT_PERMISSIONS, true);
        driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);

        snapchatUI = new SnapchatUI(driver, settings.get("username").getAsString(), settings.get("password").getAsString());
    }

    @Test
    public void testSnapchat() throws JSONException {
        snapchatUI.login();
        //Snapchat asking for some permissions even when they were auto granted, so I had to add this method
        snapchatUI.allowAllAppPermissions();
        //Following method extracted to go open friend and send text message
        snapchatUI.clickOnFriendToChat("sol marl");

        String messageToSend = retrieveMessageToSend();
        snapchatUI.sendMessage(messageToSend);

        //Check if sent message shown in the messages list
        String lastMessage = snapchatUI.getLastSentMessage();
        Assert.assertTrue(lastMessage.contains(messageToSend));
    }

    private String retrieveMessageToSend() throws JSONException {
        String randomUserJson = RestAssured.get("https://randomuser.me/api/").getBody().print();
        JSONObject jsonObject = new JSONObject(randomUserJson);

        String title = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("name").get("title").toString();
        String firstName = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("name").get("first").toString();
        String lastName = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("name").get("last").toString();
        String location = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("location").getJSONObject("timezone").get("description").toString();
        String email = jsonObject.getJSONArray("results").getJSONObject(0).get("email").toString();

        return "Hello " + title + " " + firstName + " " + lastName + ". You are from " + location + " I guess. Could you say if " + email + " is your email?";
    }

    @After
    public void End() {
        driver.quit();
    }
}