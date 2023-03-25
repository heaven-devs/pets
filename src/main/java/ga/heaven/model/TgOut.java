package ga.heaven.model;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Message;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

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
    private List<String> textStatus;
    private String textBody;
    private String textMenuCaption;
    
    public TgOut() {
        try {
            this.msgJSON = (ObjectNode) new ObjectMapper().readTree(BotUtils.toJson(new Message()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        LOGGER.debug(Objects.isNull(this.msgJSON) ? "" : this.msgJSON.toString());
    }
    
    public void updateTextField() {
        this.msgJSON.put(TEXT_FIELD_NAME,
        "<b>" +
                String.join("\n ", this.textStatus) +
                "</b>\n\n" +
                this.textBody +
                "\n\n" +
                this.textMenuCaption + ":");
    }
    
    public void clearStatus() {
        this.textStatus.clear();
        updateTextField();
    }
    
    public void addStatusLine(String line) {
        textStatus.add(line);
        updateTextField();
    }
    
    

}
