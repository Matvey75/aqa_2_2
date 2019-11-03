import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.time.LocalDate;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CardDeliveryTest {
    private int minDaysToCorrectDate = 3;
    private LocalDate correctDate = LocalDate.now().plusDays(minDaysToCorrectDate);
    private LocalDate unCorrectDate = LocalDate.now();
    private String locatorCity = "[data-test-id=city]";
    private String locatorCityMenu = ".menu-item__control";
    private String locatorDate = "[data-test-id=date]";
    private String locatorName = "[data-test-id=name]";
    private String locatorPhone = "[data-test-id=phone]";
    private String locatorAgreement = "[data-test-id=agreement]";
    private String locatorNotification = "[data-test-id=notification]";
    private String locatorNotificationTitle = "[class='notification__title']";
    private String locatorNotificationContent = "[class='notification__content']";

    @BeforeEach
    void openHost() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName(value = "Should successfully for correct mettings day ")
    void shouldSuccessWithCorrectValues() {
        $(locatorCity + " input").setValue("Краснодар");
        $(locatorDate + " input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $(locatorDate + " input").sendKeys(getFormatDate(correctDate));
        $(locatorName + " input").setValue("Иванов Василий");
        $(locatorPhone + " input").setValue("+79270000000");
        $(locatorAgreement).click();
        $$(By.className("button")).find(exactText("Забронировать")).click();
        SelenideElement notification = $(locatorNotification);
        notification.waitUntil(Condition.visible, 15000);
        notification.$(locatorNotificationTitle).should(Condition.exactText("Успешно!"));
        notification.$(locatorNotificationContent).should(Condition.exactText("Встреча успешно забронирована на " + getFormatDate(correctDate)));
    }
    @Test
    @DisplayName(value = "Should error for uncorrect mettings day ")
    void shouldErrorWithUncorrectDate() {
        $(locatorCity + " input").setValue("Казань");
        $(locatorDate + " input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $(locatorDate + " input").sendKeys(getFormatDate(unCorrectDate));
        $(locatorName + " input").setValue("Иванов Василий");
        $(locatorPhone + " input").setValue("+79270000000");
        $(locatorAgreement).click();
        $$(By.className("button")).find(exactText("Забронировать")).click();
        $(locatorDate + " .input__sub").shouldBe(Condition.visible);
        $(locatorDate + " .input__sub").shouldHave(Condition.exactText("Заказ на выбранную дату невозможен"));
    }
    @Test
    @DisplayName(value = "Should error for uncorrect city ")
    void shouldErrorWithUncorrectCity() {
        $(locatorCity + " input").setValue("Сочи");
        $(locatorDate + " input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $(locatorDate + " input").sendKeys(getFormatDate(correctDate));
        $(locatorName + " input").setValue("Иванов Василий");
        $(locatorPhone + " input").setValue("+79270000000");
        $(locatorAgreement).click();
        $$(By.className("button")).find(exactText("Забронировать")).click();
        $(locatorCity + " .input__sub").shouldBe(Condition.visible);
        $(locatorCity + " .input__sub").shouldHave(Condition.exactText("Доставка в выбранный город недоступна"));
    }
    @Test
    @DisplayName(value = "Should error for uncorrect name ")
    void shouldErrorWithUncorrectName() {
        $(locatorCity + " input").setValue("Москва");
        $(locatorDate + " input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $(locatorDate + " input").sendKeys(getFormatDate(correctDate));
        $(locatorName + " input").setValue("Ivanov Vasily");
        $(locatorPhone + " input").setValue("+79270000000");
        $(locatorAgreement).click();
        $$(By.className("button")).find(exactText("Забронировать")).click();
        $(locatorName + " .input__sub").shouldBe(Condition.visible);
        $(locatorName + " .input__sub").shouldHave(Condition.exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }
    @Test
    @DisplayName(value = "Should error for uncorrect phone ")
    void shouldErrorWithUncorrectPhone() {
        $(locatorCity + " input").setValue("Уфа");
        $(locatorDate + " input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $(locatorDate + " input").sendKeys(getFormatDate(correctDate));
        $(locatorName + " input").setValue("Иванов Василий");
        $(locatorPhone + " input").setValue("+9270000000");
        $(locatorAgreement).click();
        $$(By.className("button")).find(exactText("Забронировать")).click();
        $(locatorPhone + " .input__sub").shouldBe(Condition.visible);
        $(locatorPhone + " .input__sub").shouldHave(Condition.exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }
    @Test
    @DisplayName(value = "Should error for don't click Agreement ")
    void shouldErrorWithoutAgreement() {
        $(locatorCity + " input").setValue("Владивосток");
        $(locatorDate + " input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $(locatorDate + " input").sendKeys(getFormatDate(correctDate));
        $(locatorName + " input").setValue("Иванов Василий");
        $(locatorPhone + " input").setValue("+79270000000");
        $$(By.className("button")).find(exactText("Забронировать")).click();
        $(locatorAgreement).shouldHave(Condition.cssClass("input_invalid"));
    }
    @Test
    @DisplayName(value = "Should successfully for correct values and using menu with mouth ")
    void shouldSuccessWithCorrectValuesWithMouth() {
        String choiceDay = String.valueOf(LocalDate.now().getDayOfMonth()+7);
        $(locatorCity + " input").setValue("Кра");
        $$(locatorCityMenu).findBy(Condition.exactText("Краснодар")).click();
        $("button .icon_name_calendar").click();
        $(".calendar__layout").waitUntil(Condition.visible, 2000);
        $$(".calendar__day").find(attribute("textContent",choiceDay)).click();
        $(locatorName + " input").setValue("Иванов Василий");
        $(locatorPhone + " input").setValue("+79270000000");
        $(locatorAgreement).click();
        $$(By.className("button")).find(exactText("Забронировать")).click();
        SelenideElement notification = $(locatorNotification);
        notification.waitUntil(Condition.visible, 15000);
        notification.$(locatorNotificationTitle).should(Condition.exactText("Успешно!"));
    }

    // возвращает дату в текстовом формате для подстановки в поле
    public String getFormatDate(LocalDate date) {
        return String.format("%02d.%02d.%d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }
}
