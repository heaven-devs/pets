package ga.heaven.service;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import ga.heaven.listener.TelegramBotUpdatesListenerTest;
import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
import ga.heaven.model.Info;
import ga.heaven.model.TgIn;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static ga.heaven.configuration.Constants.*;
import static ga.heaven.model.CustomerContext.Context.FREE;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppLogicServiceTest {

    @InjectMocks
    private AppLogicService appLogicService;

    @Mock
    private InfoService infoService;
    @Mock
    private CustomerService customerService;
    @Mock
    private ShelterService shelterService;
    @Mock
    private MsgService msgService;

    private Info expectedInfo;
    private Long expectedChatId;
    private Customer expectedCustomer;
    private ArgumentCaptor<Long> argCaptor1;
    private ArgumentCaptor<InlineKeyboardMarkup> argCaptor2;
    private ArgumentCaptor<String> argCaptor3;


    private static final Logger logger = LoggerFactory.getLogger(AppLogicServiceTest.class);

    @BeforeEach
    private void getInitialTestBreeds() {
        expectedChatId = 777_777_777L;
        expectedInfo = new Info(1L, "area", COMMON_INFO_FIELD);
        expectedCustomer = new Customer();
        expectedCustomer.setId(1L);
        expectedCustomer.setChatId(expectedChatId);
        expectedCustomer.setName("Ivan");
        expectedCustomer.setCustomerContext(new CustomerContext(1L, FREE, 2L, 1L, null, null, null));

        argCaptor1 = ArgumentCaptor.forClass(Long.class);
        argCaptor2 = ArgumentCaptor.forClass(InlineKeyboardMarkup.class);
        argCaptor3 = ArgumentCaptor.forClass(String.class);
    }

    @Test
    public void handleBotCommandDatingRulesPositive() {
        when(infoService.findInfoByArea(DATING_RULES_FIELD)).thenReturn(expectedInfo);

        Update update = getUpdateFromResourceFile("text_update.json", DATING_RULES_CMD);
        appLogicService.sendDatingRules(update.message().chat().id());

        ArgumentCaptor<Long> argumentCaptor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> argumentCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(msgService).sendMsg(argumentCaptor1.capture(), argumentCaptor2.capture());
        Long actualArgument1 = argumentCaptor1.getValue();
        String actualArgument2 = argumentCaptor2.getValue();

        Assertions.assertThat(actualArgument1).isEqualTo(expectedChatId);
        Assertions.assertThat(actualArgument2).isEqualTo(expectedInfo.getInstructions());
    }

    @Test
    public void handleBotCommandDatingRulesNegative() {
        expectedInfo = null;

        when(infoService.findInfoByArea(DATING_RULES_FIELD)).thenReturn(expectedInfo);

        Update update = getUpdateFromResourceFile("text_update.json", DATING_RULES_CMD);
        appLogicService.sendDatingRules(update.message().chat().id());

        ArgumentCaptor<Long> argumentCaptor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> argumentCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(msgService).sendMsg(argumentCaptor1.capture(), argumentCaptor2.capture());
        Long actualArgument1 = argumentCaptor1.getValue();
        String actualArgument2 = argumentCaptor2.getValue();

        Assertions.assertThat(actualArgument1).isEqualTo(expectedChatId);
        Assertions.assertThat(actualArgument2).isEqualTo(DATING_RULES_NOT_FOUND);
    }

    @Test
    public void handleInitConversationForNewCustomer() {
        String expectedCommand = COMMON_INFO_FIELD;
        when(customerService.isPresent(expectedChatId)).thenReturn(false);
        when(infoService.findInfoByArea(expectedCommand)).thenReturn(expectedInfo);
        when(customerService.createCustomer(expectedChatId)).thenReturn(expectedCustomer);

        Update update = getUpdateFromResourceFile("text_update.json", expectedCommand);
        appLogicService.initConversation(new TgIn().update(update));

        ArgumentCaptor<Long> argumentCaptor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> argumentCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(msgService).sendMsg(argumentCaptor1.capture(), argumentCaptor2.capture());
        Long actualArgument1 = argumentCaptor1.getValue();
        String actualArgument2 = argumentCaptor2.getValue();
        Assertions.assertThat(actualArgument1).isEqualTo(expectedChatId);
        Assertions.assertThat(actualArgument2).isEqualTo(expectedCommand);
    }

    @Test
    public void handleInitConversationForExistingUser() {
        InlineKeyboardMarkup expectedKbMarkup = new InlineKeyboardMarkup();

        when(customerService.isPresent(expectedChatId)).thenReturn(true);

        Update update = getUpdateFromResourceFile("text_update.json", COMMON_INFO_FIELD);
        appLogicService.initConversation(new TgIn().update(update));

        ArgumentCaptor<Long> argumentCaptor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> argumentCaptor2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Keyboard> argumentCaptor3 = ArgumentCaptor.forClass(Keyboard.class);

        Mockito.verify(msgService).sendMsg(argumentCaptor1.capture(), argumentCaptor2.capture(), argumentCaptor3.capture());

        Long actualArgument1 = argumentCaptor1.getValue();
        String actualArgument2 = argumentCaptor2.getValue();
        Keyboard actualArgument3 = argumentCaptor3.getValue();

        Assertions.assertThat(actualArgument1).isEqualTo(expectedChatId);
        Assertions.assertThat(actualArgument2).isEqualTo(SHELTER_CHOOSE_MSG);
        Assertions.assertThat(actualArgument3).isEqualTo(expectedKbMarkup);
    }

    @Test
    public void sendDatingRulesPositive() {
        when(infoService.findInfoByArea(DATING_RULES_FIELD)).thenReturn(expectedInfo);
        appLogicService.sendDatingRules(expectedChatId);
        commonSendPositiveMethod();
    }

    @Test
    public void sendDocuments() {
        when(infoService.findInfoByArea(DOCUMENTS_FIELD)).thenReturn(expectedInfo);
        appLogicService.sendDocuments(expectedChatId);
        commonSendPositiveMethod();
    }

    @Test
    public void sendTransportRules() {
        when(infoService.findInfoByArea(TRANSPORT_FIELD)).thenReturn(expectedInfo);
        appLogicService.sendTransportRules(expectedChatId);
        commonSendPositiveMethod();
    }

    @Test
    public void sendComfortPet() {
        when(infoService.findInfoByArea(COMFORT_PET_FIELD)).thenReturn(expectedInfo);
        appLogicService.sendComfortPet(expectedChatId);
        commonSendPositiveMethod();
    }

    @Test
    public void sendComfortDog() {
        when(infoService.findInfoByArea(COMFORT_DOG_FIELD)).thenReturn(expectedInfo);
        appLogicService.sendComfortDog(expectedChatId);
        commonSendPositiveMethod();
    }

    @Test
    public void sendComfortHandicapped() {
        when(infoService.findInfoByArea(COMFORT_HANDICAPPED_FIELD)).thenReturn(expectedInfo);
        appLogicService.sendComfortHandicapped(expectedChatId);
        commonSendPositiveMethod();
    }

    @Test
    public void sendCynologistAdvice() {
        when(infoService.findInfoByArea(CYNOLOGIST_ADVICE_FIELD)).thenReturn(expectedInfo);
        appLogicService.sendCynologistAdvice(expectedChatId);
        commonSendPositiveMethod();
    }

    @Test
    public void sendReasonsRefusal() {
        when(infoService.findInfoByArea(REASONS_REFUSAL_FIELD)).thenReturn(expectedInfo);
        appLogicService.sendReasonsRefusal(expectedChatId);
        commonSendPositiveMethod();
    }

    @Test
    public void sendCynologistsList() {
        when(infoService.findInfoByArea(CYNOLOGISTS_LIST_FIELD)).thenReturn(expectedInfo);
        appLogicService.sendCynologistsList(expectedChatId);
        commonSendPositiveMethod();
    }

    @Test
    public void sendMultipurpose() {
        when(infoService.findInfoByArea(anyString())).thenReturn(expectedInfo);
        appLogicService.sendMultipurpose(expectedChatId, "TestArea", "notFoundString");
        commonSendPositiveMethod();
    }
    private void commonSendPositiveMethod() {
        Mockito.verify(msgService).interactiveMsg(argCaptor1.capture(), argCaptor2.capture(), argCaptor3.capture());

        Long actualArgument1 = argCaptor1.getValue();
        InlineKeyboardMarkup actualArgument2 = argCaptor2.getValue();
        String actualArgument3 = argCaptor3.getValue();

        Assertions.assertThat(actualArgument1).isEqualTo(expectedChatId);
        Assertions.assertThat(actualArgument2).isNull();
        Assertions.assertThat(actualArgument3).isEqualTo(expectedInfo.getInstructions());
    }

    @Test
    public void sendDatingRulesNegative() {
        when(infoService.findInfoByArea(DATING_RULES_FIELD)).thenReturn(null);
        appLogicService.sendDatingRules(expectedChatId);
        commonSendNegativeMethod(DATING_RULES_NOT_FOUND);
    }

    @Test
    public void sendDocumentsNegative() {
        when(infoService.findInfoByArea(DOCUMENTS_FIELD)).thenReturn(null);
        appLogicService.sendDocuments(expectedChatId);
        commonSendNegativeMethod(DOCUMENTS_NOT_FOUND);
    }

    @Test
    public void sendTransportRulesNegative() {
        when(infoService.findInfoByArea(TRANSPORT_FIELD)).thenReturn(null);
        appLogicService.sendTransportRules(expectedChatId);
        commonSendNegativeMethod(TRANSPORT_NOT_FOUND);
    }

    @Test
    public void sendComfortPetNegative() {
        when(infoService.findInfoByArea(COMFORT_PET_FIELD)).thenReturn(null);
        appLogicService.sendComfortPet(expectedChatId);
        commonSendNegativeMethod(COMFORT_PET_NOT_FOUND);
    }

    @Test
    public void sendComfortDogPetNegative() {
        when(infoService.findInfoByArea(COMFORT_DOG_FIELD)).thenReturn(null);
        appLogicService.sendComfortDog(expectedChatId);
        commonSendNegativeMethod(COMFORT_DOG_NOT_FOUND);
    }

    @Test
    public void sendComfortHandicappedNegative() {
        when(infoService.findInfoByArea(COMFORT_HANDICAPPED_FIELD)).thenReturn(null);
        appLogicService.sendComfortHandicapped(expectedChatId);
        commonSendNegativeMethod(COMFORT_HANDICAPPED_NOT_FOUND);
    }

    @Test
    public void sendCynologistAdviceNegative() {
        when(infoService.findInfoByArea(CYNOLOGIST_ADVICE_FIELD)).thenReturn(null);
        appLogicService.sendCynologistAdvice(expectedChatId);
        commonSendNegativeMethod(CYNOLOGIST_ADVICE_NOT_FOUND);
    }

    @Test
    public void sendCynologistsListNegative() {
        when(infoService.findInfoByArea(CYNOLOGISTS_LIST_FIELD)).thenReturn(null);
        appLogicService.sendCynologistsList(expectedChatId);
        commonSendNegativeMethod(CYNOLOGIST_LIST_NOT_FOUND);
    }

    @Test
    public void sendReasonsRefusalNegative() {
        when(infoService.findInfoByArea(REASONS_REFUSAL_FIELD)).thenReturn(null);
        appLogicService.sendReasonsRefusal(expectedChatId);
        commonSendNegativeMethod(REASONS_REFUSAL_NOT_FOUND);
    }

    @Test
    public void sendMultipurposeNegative() {
        String notFoundString = "NotFoundString";
        when(infoService.findInfoByArea(anyString())).thenReturn(null);
        appLogicService.sendMultipurpose(expectedChatId, "TestArea", notFoundString);
        commonSendNegativeMethod(notFoundString);
    }
    @Test
    public void commonSendNegativeMethod(String notFoundString) {
        Mockito.verify(msgService).interactiveMsg(argCaptor1.capture(), argCaptor2.capture(), argCaptor3.capture());

        Long actualArgument1 = argCaptor1.getValue();
        InlineKeyboardMarkup actualArgument2 = argCaptor2.getValue();
        String actualArgument3 = argCaptor3.getValue();

        Assertions.assertThat(actualArgument1).isEqualTo(expectedChatId);
        Assertions.assertThat(actualArgument2).isNull();
        Assertions.assertThat(actualArgument3).isEqualTo(notFoundString);
    }

    private Update getUpdateFromResourceFile(String jsonFile, String command) {
        String json = null;
        try {
            json = Files.readString(
                    Paths.get(TelegramBotUpdatesListenerTest.class.getResource(jsonFile).toURI()));
        } catch (IOException | URISyntaxException e) {
            logger.error(e.getMessage());
        }
        return BotUtils.fromJson(json.replace("%command%", command), Update.class);
    }
}
