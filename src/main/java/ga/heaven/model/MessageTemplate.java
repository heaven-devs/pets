package ga.heaven.model;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.*;
import org.apache.logging.log4j.util.Strings;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class MessageTemplate {
    private InlineKeyboardMarkup keyboard;
    private String textStatus;
    private String textBody;
    private String textMenuCaption;
    
    public String getText() {
        return "<b>" +
                textStatus +
                "</b>\n\n" +
                textBody +
                "\n" +
                textMenuCaption + ":";
    }
    
    public MessageTemplate() {
        this.keyboard = new InlineKeyboardMarkup();
        this.textBody = Strings.EMPTY;
        this.textStatus = Strings.EMPTY;
        this.textMenuCaption = Strings.EMPTY;
    }
    
}
