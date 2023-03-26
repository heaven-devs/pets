package ga.heaven.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MsgService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MsgService.class);
    
    private final TelegramBot tgBot;
    
    private final CustomerService customerService;
    
    
    public MsgService(TelegramBot tgBot, CustomerService customerService) {
        this.tgBot = tgBot;
        this.customerService = customerService;
    }
    
    public void reqContactMsg(Long chatId, String inputMessage) {
        SendMessage sendMessage = new SendMessage(chatId, inputMessage);
        KeyboardButton keyboardButton = new KeyboardButton("Send contact");
        keyboardButton.requestContact(true);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboardButton).resizeKeyboard(true).selective(true).inputFieldPlaceholder("").oneTimeKeyboard(true);
        sendMessage.replyMarkup(keyboardMarkup);
        tgBot.execute(sendMessage);
    }
    
    public void sendContact(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId, "This person want to consult with volunteer:").parseMode(ParseMode.HTML);
        
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton("profile").url("tg://user?id=" + chatId);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(keyboardButton);
        sendMessage.replyMarkup(keyboardMarkup);
        tgBot.execute(sendMessage);
    }
    
    public void editMsg(Long chatId, Integer msgId, InlineKeyboardMarkup keyboard) {
        if (keyboard != null) {
            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(chatId, msgId);
            editMessageReplyMarkup.replyMarkup(keyboard);
            BaseResponse baseResponse = tgBot.execute(editMessageReplyMarkup);
            if (!baseResponse.isOk()) {
                LOGGER.error(baseResponse.description());
            }
        }
    }
    
    public void editMsg(Long chatId, Integer msgId, String msgText, InlineKeyboardMarkup keyboard) {
        EditMessageText editMessage = null;
        if ((msgText == null) && (keyboard == null)) {
            return;
        } else if ((msgText != null) && (keyboard == null)) {
            editMessage = new EditMessageText(chatId, msgId, msgText);
        } else if ((msgText != null) && (keyboard != null)) {
            editMessage = new EditMessageText(chatId, msgId, msgText).replyMarkup(keyboard);
        } else {
            editMsg(chatId, msgId, keyboard);
            return;
        }
        BaseResponse baseResponse = tgBot.execute(editMessage.parseMode(ParseMode.HTML));
        if (!baseResponse.isOk()) {
            LOGGER.error(baseResponse.description());
        }
    }
    
    public Message msgExtractor(Update updateObj) {
        Message msgObj = new Message();
        ObjectNode msgJSON = null;
        String msgStringJSON;
        
        ObjectMapper mapper;
        
        Customer customer = new Customer();
        CustomerContext context;
        
        if (updateObj.message() != null) {
            customer = customerService.findCustomerByChatId(updateObj.message().chat().id());
            msgObj = updateObj.message();
            msgStringJSON = BotUtils.toJson(msgObj);
        }
        
        if (updateObj.callbackQuery() != null) {
            if (updateObj.callbackQuery().id() != null) {
                sendCallbackQueryResponse(updateObj.callbackQuery().id());
            }
            
            
            customer = customerService.findCustomerByChatId(updateObj.callbackQuery().message().chat().id());
            
            if (customer == null) {
                return null;
            }
            
            msgObj = updateObj.callbackQuery().message();
            msgStringJSON = BotUtils.toJson(msgObj);
            
            try {
                mapper = new ObjectMapper();
                msgJSON = (ObjectNode) mapper.readTree(msgStringJSON);
                /*if (updateObj.callbackQuery().from() != null) {
                    mapper = new ObjectMapper();
                    msgJSON.set("from", mapper.readTree(BotUtils.toJson(updateObj.callbackQuery().from())));
                }*/
                
                if (updateObj.callbackQuery().data() != null) {
                    msgJSON.put("text", updateObj.callbackQuery().data());
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            msgJSON.remove("entities");
            msgJSON.remove("reply_markup");
            
            msgObj = BotUtils.fromJson(msgJSON.toPrettyString(), Message.class);
        }
        
        if (msgJSON != null) {
            context = customer.getCustomerContext();
            context.setLastInMsg(msgJSON.toPrettyString());
            customerService.updateCustomer(customer);
        }
        
        return msgObj;
        
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
                //msgJSON.put("text", humanViewContext(chatId) + "\n \n" + newText);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        msgObj = BotUtils.fromJson(msgJSON.toPrettyString(), Message.class);
        //msgObj.replyMarkup().inlineKeyboard()
        editMsg(chatId, msgObj.messageId(), msgObj.text(), msgObj.replyMarkup());
        context.setLastOutMsg(msgJSON.toPrettyString());
        customerService.updateCustomer(customer);
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
    
    public void sendMsg(Long chatId, String inputMessage, Keyboard keyboard) {
        SendMessage outputMessage = new SendMessage(chatId, inputMessage)
                .parseMode(ParseMode.HTML);
        if (keyboard != null) {
            outputMessage.replyMarkup(keyboard);
        }
        SendResponse sendResponse = tgBot.execute(outputMessage);
        if (!sendResponse.isOk()) {
            LOGGER.error(sendResponse.description());
        } else {
            Customer customer = customerService.findCustomerByChatId(chatId);
            if (customer != null) {
                customer.getCustomerContext().setLastOutMsg(BotUtils.toJson(sendResponse.message()));
                customerService.updateCustomer(customer);
            }
            
        }
    }
}
