package ga.heaven.service;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Update;
import ga.heaven.listener.TelegramBotUpdatesListenerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static ga.heaven.configuration.Constants.REPORT_SUBMIT_CMD;

@ExtendWith(MockitoExtension.class)
public class ReportSelectorServiceTest {

    @InjectMocks
    private ReportSelectorService reportSelectorService;

    @Mock
    private MsgService msgService;
    @Mock
    private ReportService reportService;

    private static final Logger logger = LoggerFactory.getLogger(ReportSelectorServiceTest.class);

    private String updateResourceFile = "text_update.json";

    @BeforeEach
    private void initialTest() {
        
        
    }
    
    @Test
    @Disabled
    public void switchCmdTestForSubmitReport() {
        String expectedCommand = REPORT_SUBMIT_CMD;

        Update update = getUpdateFromResourceFile(updateResourceFile, expectedCommand);
        reportSelectorService.switchCmd(update.message());
        
        
    }

    private Update getUpdateFromResourceFile(String resourceFile, String command) {
        String json = null;
        try {
            json = Files.readString(
                    Paths.get(TelegramBotUpdatesListenerTest.class.getResource(resourceFile).toURI()));
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
        }
        return BotUtils.fromJson(json.replace("%command%", command), Update.class);
    }

}
