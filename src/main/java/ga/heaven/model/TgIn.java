package ga.heaven.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import ga.heaven.service.AppLogicService;
import ga.heaven.service.CustomerService;
import ga.heaven.service.MsgService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ga.heaven.model.TgIn.Endpoint.Type.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TgIn {
    @Getter
    @Setter
    public static class Endpoint {
        public enum Type {
            STATIC,
            DYNAMIC
        }
        
        private String name;
        private String value;
        private Type type;
        
        public Long getValueAsLong() {
            return Long.parseLong(this.value);
        }
    }
    
    private static final String DYNAMIC_ENDPOINT_REGEXP = "^/(.*)/(.*)[0-9]*";
    private static final String STATIC_ENDPOINT_REGEXP = "^/([^/]*)$";
    private static final Logger LOGGER = LoggerFactory.getLogger(TgIn.class);
    private static final String TEXT_FIELD_NAME = "text";
    private static final String ENTITIES_SECTION_NAME = "entities";
    private static final String REPLY_SECTION_NAME = "reply_markup";
    
    private ObjectNode msgJSON;
    
    private Customer customer;
    
    private Long callbackQueryId;
    
    private Integer modalMessageId;
    
    private List<Navigation> navigationList;
    
    private List<Shelter> shelterList;
    
    
    private MsgService svcMsg;
    private CustomerService svcCustomer;
    private AppLogicService svcApp;
    
    
    public TgIn injectServices(MsgService svcMsg, CustomerService svcCustomer, AppLogicService svcApp) {
        this.svcMsg = svcMsg;
        this.svcCustomer = svcCustomer;
        this.svcApp = svcApp;
        return this;
    }
    
    public Integer renewMessageById(Integer past, Integer next) {
        if (Objects.nonNull(past) && Objects.nonNull(next)) {
            if (!past.equals(next)) {
                svcMsg.deleteMsg(this.chatId(), past);
            }
        } else if (Objects.nonNull(past))  {
            return past;
        }
        return next;
    }
    
    
    public TgIn initMsgInstanceEnvironment() {
        Optional.ofNullable(svcCustomer.findCustomerByChatId(this.chatId()))
                .ifPresentOrElse(
                        c -> this.customer = c
                        , () -> this.customer = svcCustomer.createCustomer(this.chatId())
                );
        
        this.modalMessageId = renewMessageById(this.lastInQueryMessageId(), this.lastOutQueryMessageId());
        
        Optional.ofNullable(this.getCallbackQueryId())
                .ifPresentOrElse(lId -> {
                    svcMsg.sendCallbackQueryResponse(lId.toString());
                    this.modalMessageId = renewMessageById(this.modalMessageId, this.messageId());
                    this.getCustomer().getCustomerContext().setLastInMsg(this.getMsgJSON().toPrettyString());
                }, () -> {
                    svcMsg.deleteMsg(this.chatId(), this.messageId());
                });
        
        //svcApp.addInputInstance(this);
        
        return this;
    }
    
    public Endpoint endpoint() {
        Endpoint result = new Endpoint();
        final Matcher matcher = Pattern.compile(DYNAMIC_ENDPOINT_REGEXP).matcher(this.text());
        if (matcher.matches()) {
            result.setType(DYNAMIC);
            result.setName(matcher.group(1));
            result.setValue(matcher.group(2));
        } else if (Pattern.compile(STATIC_ENDPOINT_REGEXP).matcher(this.text()).matches()) {
            result.setType(STATIC);
            result.setName(this.text());
            //result.setValue(null);
        }
        return result;
    }
    
    public TgIn() {
        try {
            this.msgJSON = (ObjectNode) new ObjectMapper().readTree(BotUtils.toJson(new Message()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //LOGGER.debug(Objects.isNull(this.msgJSON) ? "" : this.msgJSON.toString());
    }
    
    public TgIn newInstance() {
        return new TgIn()
                .setShelterList(new ArrayList<>(this.getShelterList()))
                .setNavigationList(new ArrayList<>(this.getNavigationList()))
                .injectServices(this.svcMsg, this.svcCustomer,this.svcApp);
    }
    
    public Long chatId() {
        long result = this.msgJSON.path("chat").path("id").asLong(0);
        return result == 0 ? null : result;
    }
    
    public String text() {
        return this.msgJSON.path("text").asText(null);
    }
    
    public PhotoSize[] photo() {
        return BotUtils.fromJson(this.msgJSON.path("photo").asText(null), PhotoSize[].class);
    }
    
    public Message message() {
        return BotUtils.fromJson(this.msgJSON.toPrettyString(), Message.class);
    }
    
    public InlineKeyboardMarkup inlineMarkup() {
        return BotUtils.fromJson(this.msgJSON.path("reply_markup").asText(null), InlineKeyboardMarkup.class);
    }
    
    public Integer messageId() {
        int result = this.msgJSON.path("message_id").asInt();
        return result == 0 ? null : result;
    }
    
    public TgIn messageId(Integer messageId) {
        Optional.ofNullable(messageId).ifPresent(m -> this.msgJSON.put("message_id", m));
        return this;
    }
    
    
    public Integer lastInQueryMessageId() {
        return Optional.ofNullable(this.customer.getCustomerContext().getLastInMsg())
                .map(m -> BotUtils.fromJson(m, Message.class).messageId())
                .orElse(null);
    }
    
    public Integer lastOutQueryMessageId() {
        return Optional.ofNullable(this.customer.getCustomerContext().getLastOutMsg())
                .map(m -> BotUtils.fromJson(m, Message.class).messageId())
                .orElse(null);
    }
    
    public TgIn setNavigationList(List<Navigation> navigationList) {
        this.navigationList = navigationList;
        //LOGGER.debug(String.valueOf(navigationList));
        return this;
    }
    
    public TgIn setShelterList(List<Shelter> shelterList) {
        this.shelterList = shelterList;
        //LOGGER.debug(String.valueOf(shelterList));
        return this;
    }
    
    public TgIn setCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }
    
    public Navigation navigationItemById(Long navigationLevel) {
        return this.navigationList.stream().filter(n -> navigationLevel.equals(n.getId()))
                //.map(Navigation::getText)
                .findFirst()
                .orElse(null);
    }
    
    
    
    
    
    public Shelter currentShelter(Long shelterId) {
        if (Objects.nonNull(shelterId)) {
                return this.shelterList.stream().filter(s -> shelterId == s.getId())
                        //.map(Shelter::getName)
                        .findFirst()
                        .orElse(null);
            }
        return null;
    }
    
    public TgIn update(Update updateObj) {
        
        
        this.msgJSON = Optional.ofNullable(updateObj)
                .flatMap(u -> Optional.ofNullable(u.callbackQuery())
                        .flatMap(c -> Optional.ofNullable(c.message())
                                .map(BotUtils::toJson)
                                .map(s ->
                                {
                                    ObjectNode n;
                                    try {
                                        n = (ObjectNode) new ObjectMapper().readTree(s);
                                        if (Objects.nonNull(updateObj.callbackQuery().data())) {
                                            n.put(TEXT_FIELD_NAME, updateObj.callbackQuery().data());
                                            n.remove(ENTITIES_SECTION_NAME);
                                            //n.remove(REPLY_SECTION_NAME);
                                            this.callbackQueryId = Long.valueOf(updateObj.callbackQuery().id());
                                        }
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                    return n;
                                }))
                        .or(
                                () -> Optional.ofNullable(u.message())
                                        .map(message -> {
                                            try {
                                                return (ObjectNode) new ObjectMapper().readTree(BotUtils.toJson(message));
                                            } catch (JsonProcessingException e) {
                                                throw new RuntimeException(e);
                                            }
                                        })
                        ))
                .orElse(null);
        
        //LOGGER.debug(Objects.isNull(this.msgJSON) ? "" : this.msgJSON.toString());
        return this;
        
    }
    
}
