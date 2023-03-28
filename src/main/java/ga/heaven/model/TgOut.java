package ga.heaven.model;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import ga.heaven.service.CustomerService;
import ga.heaven.service.MsgService;
import io.github.jamsesso.jsonlogic.JsonLogic;
import io.github.jamsesso.jsonlogic.JsonLogicException;
import lombok.*;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static ga.heaven.configuration.Constants.SHELTERS_MENU_LEVEL;
import static ga.heaven.configuration.Constants.SHELTER_EPT;

@Getter
@Setter
@ToString
@EqualsAndHashCode
//@Builder(toBuilder = true)
public class TgOut {
    private static final Logger LOGGER = LoggerFactory.getLogger(TgOut.class);
    private static final String TEXT_FIELD_NAME = "text";
    
    private static final String ENTITIES_SECTION_NAME = "entities";
    private static final String REPLY_SECTION_NAME = "reply_markup";
    private ObjectNode msgJSON;
    private InlineKeyboardMarkup inlineMarkup;
    
    private TgIn in;
    private List<String> textStatus;
    private String textBody;
    private String textMenuCaption;
    
    private MsgService svcMsg;
    private CustomerService svcCustomer;
    
    public TgOut() {
        this.textStatus = new ArrayList<>();
        this.textBody = Strings.EMPTY;
        this.textMenuCaption = Strings.EMPTY;
        try {
            this.msgJSON = (ObjectNode) new ObjectMapper().readTree(BotUtils.toJson(new Message()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        
        LOGGER.debug(Objects.isNull(this.msgJSON) ? "" : this.msgJSON.toString());
    }
    
    private void injectServices(MsgService svcMsg, CustomerService svcCustomer) {
        this.svcMsg = svcMsg;
        this.svcCustomer = svcCustomer;
    }
    
    public void updateTextField() {
        this.msgJSON.put(TEXT_FIELD_NAME,
                "<b>" +
                        (this.textStatus.isEmpty() ? "" : "ℹ ") +
                        String.join("\nℹ️ ", this.textStatus) +
                        "</b>\n\n" +
                        this.textBody +
                        "\n\n" +
                        (Strings.isNotEmpty(this.textMenuCaption) ? "\uD83D\uDD3D " + this.textMenuCaption + ":" : ""));
    }
    
    public String text() {
        return this.msgJSON.path("text").asText(null);
    }
    
    public TgOut clearStatus() {
        this.textStatus.clear();
        updateTextField();
        return this;
    }
    
    public TgOut addStatusLine(String line) {
        textStatus.add(line);
        updateTextField();
        return this;
    }
    
    public TgOut textBody(String text) {
        this.textBody = text;
        updateTextField();
        return this;
    }
    
    public TgOut textMenuCaption(String caption) {
        this.textMenuCaption = caption;
        updateTextField();
        return this;
    }
    
    public TgOut inlineMarkup(InlineKeyboardMarkup inlineMarkup) {
        
        Optional.ofNullable(inlineMarkup).ifPresent(r -> {
            this.inlineMarkup = r;
            try {
                this.msgJSON.set(REPLY_SECTION_NAME,
                        new ObjectMapper().readTree(BotUtils.toJson(r))
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        
        return this;
    }
    
    public InlineKeyboardMarkup inlineMarkup() {
        this.inlineMarkup = BotUtils.fromJson(this.msgJSON.path("reply_markup").toPrettyString(), InlineKeyboardMarkup.class);
        return this.inlineMarkup;
    }
    
    public TgOut applyKeyboardMarkup() {
        Optional.ofNullable(this.inlineMarkup).ifPresent(r -> {
            try {
                this.msgJSON.set(REPLY_SECTION_NAME,
                        new ObjectMapper().readTree(BotUtils.toJson(r))
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        return this;
    }
    
    public TgOut tgIn(TgIn in) {
        this.in = in;
        this.chatId(this.in.chatId());
        this.injectServices(in.getSvcMsg(), in.getSvcCustomer());
        this.updateTextField();
        return this;
    }
    
    public Long chatId() {
        long result = this.msgJSON.path("chat").path("id").asLong(0);
        return result == 0 ? null : result;
    }
    
    public TgOut chatId(Long chatId) {
        msgJSON.putObject("chat").put("id", chatId);
        return this;
    }
    
    public Integer messageId() {
        int result = this.msgJSON.path("message_id").asInt();
        return result == 0 ? null : result;
    }
    
    public TgOut messageId(Integer messageId) {
        Optional.ofNullable(messageId).ifPresent(m -> this.msgJSON.put("message_id", m));
        return this;
    }
    
    public void save() {
        svcCustomer.updateCustomer(this.in.getCustomer());
        //this.in.getSvcApp().removeInputInstance(in);
        //this.in = null;
        //return this;
    }
    
    public TgOut setSelectedShelter(Long shelterId) {
       // in.getCustomer().getCustomerContext().setShelterId(in.currentShelter(shelterId).getId());
        in.getCustomer().getCustomerContext().setShelterId(shelterId);
        return this;
    }
    
    public Shelter getCurrentShelter() {
        return in.currentShelter(in.getCustomer().getCustomerContext().getShelterId());
    }
    
    public TgOut send() {
        this.messageId(in.getModalMessageId());
        //Message msgObj = BotUtils.fromJson(msgJSON.toPrettyString(), Message.class);
        if (!svcMsg.editMsg(this.chatId(), this.messageId(), this.text(), this.inlineMarkup())) {
            Message newMsg = svcMsg.sendMsg(this.chatId(), this.text(), this.inlineMarkup());
            this.messageId(newMsg.messageId());
        }
        
        in.getCustomer().getCustomerContext().setLastOutMsg(this.msgJSON.toPrettyString());
        return this;
    }
    
   /* public TgOut generateMarkup() {
    
    }*/
    
    
    public TgOut generateMarkup(Long id) {
        in.getCustomer().getCustomerContext().setCurLevel(id);
        this.textMenuCaption(in.navigationItemById(id).getText());
        Optional.ofNullable(this.getCurrentShelter())
                .map(Shelter::getName)
                .ifPresent(s -> this.addStatusLine("Selected shelter: " + s));
    
        if (SHELTERS_MENU_LEVEL.equals(id)) {
            this.inlineMarkup = new InlineKeyboardMarkup();
            in.getShelterList()
                    .forEach(shelter -> this.inlineMarkup
                            .addRow(new InlineKeyboardButton(shelter.getName())
                                    .callbackData("/" + SHELTER_EPT + "/" + shelter.getId())));
            this.applyKeyboardMarkup();
            
            return this;
        }
        this.inlineMarkup = new InlineKeyboardMarkup();
        in.getNavigationList().
                forEach(button -> {
            JsonLogic jsonLogic = new JsonLogic();
            Boolean enabled;
            try {
                String rulesJson = button.getRules();
                rulesJson = rulesJson==null ? "true" : rulesJson;
                Map<String, String> data = new HashMap<>();
                data.put("shelterId", in.getCustomer().getCustomerContext().getShelterId().toString());
                data.put("dialogContext", in.getCustomer().getCustomerContext().getDialogContext().toString());
                enabled = (Boolean) jsonLogic.apply(rulesJson,data) && id.equals(button.getLevelView());
            } catch (JsonLogicException e) {
                throw new RuntimeException(e);
            }
            if (enabled) {
                this.inlineMarkup
                        .addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getEndpoint()));
            }
        });
        this.applyKeyboardMarkup();
    
        return this;
    }
    
}
