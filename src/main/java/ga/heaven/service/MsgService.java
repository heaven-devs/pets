package ga.heaven.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class MsgService {
    private static final String MSG_NOT_FOUND = "Bad Request: message to edit not found";
    private static final String MSG_NOT_CHANGED = "Bad Request: message is not modified: specified new message content and reply markup are exactly the same as a current content and reply markup of the message";
    private static final Logger LOGGER = LoggerFactory.getLogger(MsgService.class);
    
    private final TelegramBot tgBot;
    
    private final CustomerService customerService;
    
    
    public MsgService(TelegramBot tgBot, CustomerService customerService) {
        this.tgBot = tgBot;
        this.customerService = customerService;
    }
    
    
    public void getMe() {
        GetMe g = new GetMe();
        LOGGER.error(tgBot.execute(g).user().toString());
        Message m = new Message();
    }
    
    
    public void reqContactMsg(Long chatId, String inputMessage) {
        SendMessage sendMessage = new SendMessage(chatId, inputMessage);
        KeyboardButton keyboardButton = new KeyboardButton("Send contact");
        keyboardButton.requestContact(true);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboardButton).resizeKeyboard(true).selective(true).inputFieldPlaceholder("").oneTimeKeyboard(true);
        sendMessage.replyMarkup(keyboardMarkup);
        tgBot.execute(sendMessage);
    }
    
    
    
    public Boolean editMsg(Long chatId, Integer msgId, InlineKeyboardMarkup keyboard) {
        Boolean result = false;
        if (keyboard != null) {
            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(chatId, msgId);
            editMessageReplyMarkup.replyMarkup(keyboard);
            BaseResponse baseResponse = tgBot.execute(editMessageReplyMarkup);
            if (!baseResponse.isOk()) {
                LOGGER.error(baseResponse.description());
                result = false;
            } else {
                result = true;
            }
            
        }
        return result;
    }
    
    public Boolean editMsg(Long chatId, Integer msgId, String msgText, InlineKeyboardMarkup keyboard) {
        EditMessageText editMessage ;
        if (Objects.isNull(chatId) || Objects.isNull(msgId)) {
            return false;
        }
        if ((msgText == null) && (keyboard == null)) {
            return false;
        } else if ((msgText != null) && (keyboard == null)) {
            editMessage = new EditMessageText(chatId, msgId, msgText);
        } else if ((msgText != null) && (keyboard != null)) {
            editMessage = new EditMessageText(chatId, msgId, msgText).replyMarkup(keyboard);
            
        } else {
            return editMsg(chatId, msgId, keyboard);
        }
        BaseResponse baseResponse = tgBot.execute(editMessage.parseMode(ParseMode.HTML));
        if (!baseResponse.isOk()) {
            LOGGER.error(baseResponse.description());
            if (MSG_NOT_CHANGED.equals(baseResponse.description())) {
                return true;
                //todo: sendMsg();
            }
            return false;
        }
        return true;
    }
    
    public void interactiveMsg(Long chatId, InlineKeyboardMarkup newKeyboard, String newText) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode msgJSON;
        Customer customer;
        CustomerContext context;
        Message msgObj;
        customer = customerService.findCustomerByChatId(chatId);
        context = customer.getCustomerContext();
        try {
            msgJSON = (ObjectNode) mapper.readTree(context.getLastOutMsg());
            if (newKeyboard != null) {
                msgJSON.set("reply_markup", mapper.readTree(BotUtils.toJson(newKeyboard)));
            }
            if (newText != null) {
                msgJSON.put("text", newText);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        msgObj = BotUtils.fromJson(msgJSON.toPrettyString(), Message.class);
        //msgObj.replyMarkup().inlineKeyboard()
        //editMsg(chatId, msgObj.messageId(), msgObj.text(), msgObj.replyMarkup());
        context.setLastOutMsg(msgJSON.toPrettyString());
        //customerService.updateCustomer(customer);
    }
    
    public void deleteMsg(Long chatId, Integer msgId) {
        DeleteMessage deleteMessage = new DeleteMessage(chatId, msgId);
        BaseResponse deleteResponse = tgBot.execute(deleteMessage);
        if (!deleteResponse.isOk()) {
            LOGGER.error(deleteResponse.description());
        }
    }
    
    public BaseResponse sendCallbackQueryResponse(String id) {
        return tgBot.execute(new AnswerCallbackQuery(id));
    }
    
    public void sendMsg(Long chatId, String inputMessage) {
        sendMsg(chatId, inputMessage, null);
    }
    
    public Message sendMsg(Long chatId, String inputMessage, Keyboard keyboard) {
        SendMessage outputMessage = new SendMessage(chatId, inputMessage)
                .parseMode(ParseMode.HTML);
        if (keyboard != null) {
            outputMessage.replyMarkup(keyboard);
        }
        SendResponse sendResponse = tgBot.execute(outputMessage);
        if (!sendResponse.isOk()) {
            LOGGER.error(sendResponse.description());
            return null;
        }
            return sendResponse.message();
            /*Customer customer = customerService.findCustomerByChatId(chatId);
            if (customer != null) {
                customer.getCustomerContext().setLastOutMsg(BotUtils.toJson(sendResponse.message()));
                customerService.updateCustomer(customer);
            }*/
            
        
    }
}
