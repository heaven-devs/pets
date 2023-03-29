package ga.heaven.service;

import ga.heaven.model.TgIn;
import ga.heaven.model.TgOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static ga.heaven.configuration.Constants.*;

@Service
public class PetSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetSelectorService.class);
    private final InfoService infoService;

    public PetSelectorService(InfoService infoService) {
        this.infoService = infoService;
    }

    public void switchCmd(TgIn in) {
        String text;
        switch (in.endpoint().getName()) {
            case DATING_RULES_CMD:
                text = getActualBodyText(DATING_RULES_FIELD, DATING_RULES_NOT_FOUND);
                generateMenu(in, text, DATING_INFO_MENU_LEVEL);
                break;
            case DOCUMENTS_CMD:
                text = getActualBodyText(DOCUMENTS_FIELD, DOCUMENTS_NOT_FOUND);
                generateMenu(in, text, DATING_INFO_MENU_LEVEL);
                break;
            case TRANSPORT_CMD:
                text = getActualBodyText(TRANSPORT_FIELD, TRANSPORT_NOT_FOUND);
                generateMenu(in, text, DATING_INFO_MENU_LEVEL);
                break;
            case COMFORT_YOUNG_CMD:
                text = getActualBodyText(COMFORT_PET_FIELD, COMFORT_PET_NOT_FOUND);
                generateMenu(in, text, DATING_INFO_MENU_LEVEL);
                break;
            case COMFORT_ADULT_CMD:
                text = getActualBodyText(COMFORT_DOG_FIELD, COMFORT_DOG_NOT_FOUND);
                generateMenu(in, text, DATING_INFO_MENU_LEVEL);
                break;
            case COMFORT_HANDICAPPED_CMD:
                text = getActualBodyText(COMFORT_HANDICAPPED_FIELD, COMFORT_HANDICAPPED_NOT_FOUND);
                generateMenu(in, text, DATING_INFO_MENU_LEVEL);
                break;
            case CYNOLOGIST_ADVICE_CMD:
                text = getActualBodyText(CYNOLOGIST_ADVICE_FIELD, CYNOLOGIST_ADVICE_NOT_FOUND);
                generateMenu(in, text, DATING_INFO_MENU_LEVEL);
                break;
            case CYNOLOGISTS_LIST_CMD:
                text = getActualBodyText(CYNOLOGISTS_LIST_FIELD, CYNOLOGIST_LIST_NOT_FOUND);
                generateMenu(in, text, DATING_INFO_MENU_LEVEL);
                break;
            case REASONS_REFUSAL_CMD:
                text = getActualBodyText(REASONS_REFUSAL_FIELD, REASONS_REFUSAL_NOT_FOUND);
                generateMenu(in, text, DATING_INFO_MENU_LEVEL);
                break;
        }
    }

    private String getActualBodyText(String text, String alternativeText) {
        return infoService.findInfoByArea(text) == null ? alternativeText : text;
    }

    /**
     * Метод создает набор кнопок меню и подставляет переданный текст в сообщение бота
     *
     * @param text текст в тело сообщения
     */
    private void generateMenu(TgIn in, String text, Long level) {
        new TgOut()
                .tgIn(in)
                .textBody(text)
                .generateMarkup(level)
                .send()
                .save()
        ;
    }
}
