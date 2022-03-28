import Mobile.helper.StoreHelper;
import Mobile.model.SelectorInfo;
import com.thoughtworks.gauge.Step;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static java.time.Duration.ofMillis;

public class StepImpl extends HookImpl {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Step({"<key> li elementi bul ve tıkla"})
    public void clickByKey(String key) {
        findElementByKey(key).click();
        logger.info(key + " elementine tıklandı");
    }

    public MobileElement findElementByKey(String key) {
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);

        MobileElement mobileElement = null;
        try {
            mobileElement = selectorInfo.getIndex() > 0 ? findElements(selectorInfo.getBy())
                    .get(selectorInfo.getIndex()) : findElement(selectorInfo.getBy());
        } catch (Exception e) {
            Assertions.fail("key = %s by = %s Element not found ", key, selectorInfo.getBy().toString());
            e.printStackTrace();
        }
        return mobileElement;
    }

    public MobileElement findElement(By by) {
        MobileElement mobileElement;
        try {
            mobileElement = findElements(by).get(0);
        } catch (Exception e) {
            throw e;
        }
        return mobileElement;
    }

    public List<MobileElement> findElements(By by) {
        List<MobileElement> webElementList;
        try {
            webElementList = appiumFluentWait.until(new ExpectedCondition<List<MobileElement>>() {
                @Nullable
                @Override
                public List<MobileElement> apply(@Nullable WebDriver driver) {
                    List<MobileElement> elements = driver.findElements(by);
                    return elements.size() > 0 ? elements : null;
                }
            });
            if (webElementList == null) {
                throw new NullPointerException(String.format("by = %s Web element list not found", by.toString()));
            }
        } catch (Exception e) {
            throw e;
        }
        return webElementList;
    }


    @Step({"<key> li elementi bul ve <text> değerini yaz"})
    public void sendKeysByKeyNotClear(String key, String text) {
        findElementByKey(key).sendKeys(text);
        logger.info(key + " elementine " + text + " değeri yazıldı.");

    }


    @Step({"<seconds> saniye bekle"})
    public void waitBySecond(int seconds) {
        try {
            logger.info(seconds + " saniye bekleniyor.");
            Thread.sleep(seconds * 1000L);
            logger.info(seconds + " saniye beklendi.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Step({"Elementin yüklenmesini bekle <key>"})
    public MobileElement getElementWithKeyIfExists(String key) {
        MobileElement element;
        try {
            element = findElementByKey(key);
            logger.info(key + " key'li element bulundu.");
        } catch (Exception ex) {
            Assert.fail("Element: '" + key + "' doesn't exist.");
            return null;
        }
        return element;
    }

    @Step({"<key> listesinden random bir elemente tıkla"})
    public void clickRandomElementWithKey(String key) {

        List<MobileElement> elementList = findElementsByKey(key);
        Random rand = new Random();
        MobileElement element = elementList.get(rand.nextInt(elementList.size()));
        element.click();
        logger.info(key + " listesinden random bir elemente tıklandı.");
    }

    public List<MobileElement> findElementsByKey(String key) {
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);
        List<MobileElement> mobileElements = null;
        try {
            mobileElements = findElements(selectorInfo.getBy());
        } catch (Exception e) {
            Assertions.fail("key = %s by = %s Elements not found ", key, selectorInfo.getBy().toString());
            e.printStackTrace();
        }
        return mobileElements;
    }


    @Step({"Textinde <variable> değişkenindeki value geçen elementin var olduğunu kontrol et"})
    public void checkElementWithAllPageTextContainsWithSavedValue(String myVariable) {
        String savedValue = StoreHelper.INSTANCE.getValue(myVariable);
        logger.info("Hafızadaki value = " + savedValue);
        MobileElement element = null;
        try {
            element = fastFindElementExplicitWaitWithText(savedValue);
        } catch (Exception e) {
        }

        if (element != null) {
            logger.info(savedValue + " geçen element bulundu.");
        } else {
            Assert.fail(savedValue + " geçen elementin sayfada bulunması gerekiyordu.");
        }
    }

    public MobileElement fastFindElementExplicitWaitWithText(String text) {

        WebDriverWait wait = new WebDriverWait(appiumDriver, 0, 500);
        MobileElement element = (MobileElement) wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@text, '" + text + "')]")));

        return element;
    }

    @Step({"<key> elementinin <attribute> değerini <variable> değişkeni olarak kaydet"})
    public void saveValueWithAttributeElement(String key, String attribute, String myVariable) {

        MobileElement element = findElementByKey(key);
        StoreHelper.INSTANCE.saveValue(myVariable, element.getAttribute(attribute));
        String savedValue = StoreHelper.INSTANCE.getValue(myVariable);
        logger.info("Hafızaya kaydedilen " + attribute + " value = " + savedValue);
    }

    @Step({"<key> elementini bulana kadar aşağı kaydır ve tıkla"})
    public void swipeDownFindAndClick(String key) {

        int i;
        for (i = 0; i < 25; i++) {

            MobileElement element;

            try {

                element = fastFindElementExplicitWait(key);

                if (element != null) {
                    ShortSwipeUpAccordingToPhoneSize();
                    element.click();
                    logger.info(key + " elementine tıklandı");
                    break;
                }

            } catch (Exception e) {
                System.out.println(key + " elementi bulunamadı, aşağı kaydırılıyor..." + i);
                swipeUpAccordingToPhoneSize();
            }
        }

        if (i >= 25) {
            Assert.fail(key + "Elementi sayfada bulunamadı.");
        }
    }

    public MobileElement fastFindElementExplicitWait(String key) {

        SelectorInfo selectorInfo = selector.getSelectorInfo(key);
        WebDriverWait wait = new WebDriverWait(appiumDriver, 0, 500);

        MobileElement element = (MobileElement) wait.until(ExpectedConditions.visibilityOfElementLocated(selectorInfo.getBy()));

        return element;
    }

    public void ShortSwipeUpAccordingToPhoneSize() {
        if (appiumDriver instanceof AndroidDriver) {
            Dimension d = appiumDriver.manage().window().getSize();
            int height = d.height;
            int width = d.width;
            System.out.println(width + "  " + height);

            int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
            int swipeStartHeight = (height * 30) / 100;
            int swipeEndHeight = (height * 35) / 100;
            System.out.println("Start width: " + swipeStartWidth + " - Start height: " + swipeStartHeight + " - End height: " + swipeEndHeight);
            new TouchAction(appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeEndHeight))
                    .waitAction(WaitOptions.waitOptions(ofMillis(2000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeStartHeight))
                    .release()
                    .perform();
        } else {
            Dimension d = appiumDriver.manage().window().getSize();
            int height = d.height;
            int width = d.width;

            int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
            int swipeStartHeight = (height * 33) / 100;
            int swipeEndHeight = (height * 30) / 100;
            System.out.println("Start width: " + swipeStartWidth + " - Start height: " + swipeStartHeight + " - End height: " + swipeEndHeight);
            new TouchAction(appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeStartHeight))
                    .waitAction(WaitOptions.waitOptions(ofMillis(1000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeEndHeight))
                    .release()
                    .perform();
        }
    }

    public void swipeUpAccordingToPhoneSize() {
        if (appiumDriver instanceof AndroidDriver) {
            Dimension d = appiumDriver.manage().window().getSize();
            int height = d.height;
            int width = d.width;

            int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
            int swipeStartHeight = (height * 30) / 100;
            int swipeEndHeight = (height * 70) / 100;
            new TouchAction(appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeEndHeight))
                    .waitAction(WaitOptions.waitOptions(ofMillis(2000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeStartHeight))
                    .release()
                    .perform();
        } else {
            Dimension d = appiumDriver.manage().window().getSize();
            int height = d.height;
            int width = d.width;

            int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
            int swipeStartHeight = (height * 70) / 100;
            int swipeEndHeight = (height * 30) / 100;
            new TouchAction(appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeStartHeight))
                    .waitAction(WaitOptions.waitOptions(ofMillis(1000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeEndHeight))
                    .release()
                    .perform();
        }
    }

    @Step({"<key> li elementi bul ve varsa tıkla"})
    public void existTapByKey(String key) {
        waitBySecond(5);
        MobileElement element = null;
        try {

            element = findElementWithKeyWithoutAssert(key);

        } catch (Exception e) {

        }
        if (element != null) {
            element.click();
            logger.info(key + " elementine tıklandı");
        } else {
            logger.info(key + " elementi olmadığı icin tıklanmadı.");
        }
    }

    public MobileElement findElementWithKeyWithoutAssert(String key) {

        MobileElement element = null;
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);

        try {
            WebDriverWait wait = new WebDriverWait(appiumDriver, 0, 500);
            element = (MobileElement) wait.until(ExpectedConditions.visibilityOfElementLocated(selectorInfo.getBy()));
        } catch (Exception e) {
        }
        return element;
    }

    @Step({"<key> elementinin hizasından <yon> yönüne kaydır"})
    public void swipeWithElementHeight(String key, String direction) {

        WebElement element = findElementByKey(key);

        Dimension d = appiumDriver.manage().window().getSize();
        int width = d.width;

        if (direction.equals("SAĞ")) {

            int swipeStartWidth = (width * 80) / 100;
            int swipeEndWidth = (width * 30) / 100;
            int swipeStartHeight = element.getLocation().y;
            int swipeEndHeight = element.getLocation().y;

            new TouchAction(appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeStartHeight))
                    .waitAction(WaitOptions.waitOptions(ofMillis(1000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeEndHeight))
                    .release()
                    .perform();

        } else if (direction.equals("SOL")) {

            int swipeStartWidth = (width * 30) / 100;
            int swipeEndWidth = (width * 80) / 100;
            int swipeStartHeight = element.getLocation().y;
            int swipeEndHeight = element.getLocation().y;

            new TouchAction(appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeStartHeight))
                    .waitAction(WaitOptions.waitOptions(ofMillis(1000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeEndHeight))
                    .release()
                    .perform();

        }
    }
}