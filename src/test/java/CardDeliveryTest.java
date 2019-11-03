import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.time.LocalDate;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.*;

class CardDeliveryTest {
    private int minDaysToCorrectDate = 3;
    private LocalDate correctDate = LocalDate.now().plusDays(minDaysToCorrectDate);
    private LocalDate unCorrectDate = LocalDate.now();
    private String locatorCity = "[data-test-id=city] input";
    private String locatorDate = "[data-test-id=date] input";
    private String locatorName = "[data-test-id=name] input";
    private String locatorPhone = "[data-test-id=phone] input";
    private String locatorAgreement = "[data-test-id=agreement]";
    private String locatorNotification = "[data-test-id=notification]";
    private String locatorNotificationTitle = "[class='notification__title']";
    private String locatorNotificationContent = "[class='notification__content']";

    @BeforeEach
    void openHost() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName(value = "Should meeting is successfully reserved")
    void shouldSuccess() {
        $(locatorCity).setValue("Краснодар");
        $(locatorDate).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $(locatorDate).sendKeys(getFormatDate(correctDate));
        $(locatorName).setValue("Иванов Василий");
        $(locatorPhone).setValue("+79270000000");
        $(locatorAgreement).click();
        $$(By.className("button")).find(exactText("Забронировать")).click();
        SelenideElement notification = $(locatorNotification);
        notification.waitUntil(Condition.visible, 15000);
        notification.$(locatorNotificationTitle).should(Condition.exactText("Успешно!"));
        notification.$(locatorNotificationContent).should(Condition.exactText("Встреча успешно забронирована на " + getFormatDate(correctDate)));
    }

    // возвращает дату в текстовом формате для подстановки в поле
    public String getFormatDate(LocalDate date) {
        return String.format("%02d.%02d.%d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }
}
