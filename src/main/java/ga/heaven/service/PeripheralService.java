package ga.heaven.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Update;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
public class PeripheralService {
    
    private final Logger LOGGER = LoggerFactory.getLogger(PeripheralService.class);
    
    public PeripheralService() {
    
    }
    
    public String index() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Welcome to Pets Heaven Educational Team Project</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <script>\n" +
                "        document.body.innerHTML = '<object type=\"text/html\" data=\"/swagger-ui.html\" style=\"overflow:hidden;overflow-x:hidden;overflow-y:hidden;height:100%;width:100%;position:absolute;top:0px;left:0px;right:0px;bottom:0px\"></object>';\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }
    
    
    private static Update createUpdate() throws JSONException, URISyntaxException, IOException {
        return createUpdate(null, null, null);
    }
    
    public static Update createUpdate(Long chatId, String msgText) throws URISyntaxException, IOException, JSONException {
        return createUpdate(null, chatId, msgText);
    }
    
    public static Update createUpdate(Long fromId, Long chatId, String msgText) throws URISyntaxException, IOException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = (ObjectNode) mapper.createObjectNode();
        ObjectNode messageNode = rootNode.putObject("message");
        if (fromId != null) {
            messageNode.putObject("from").put("id", fromId);
        }
        if (chatId != null) {
            messageNode.putObject("chat").put("id", chatId);
        }
        if (msgText != null) {
            messageNode.put("text", msgText);
        }
        return BotUtils.fromJson(rootNode.toPrettyString(), Update.class);
    }
}
