package ga.heaven.configuration;

public interface Constants {
    
    /**
     * Commands
     */
    String START_CMD = "/start";
    String SHELTER1_CMD = "/dogs";
    String SHELTER2_CMD = "/cats";
    String DATING_RULES_CMD = "/dating_rules";
    String VOLUNTEER_REQUEST_CMD = "/call_volunteer";
    String REPORT_SUBMIT_CMD = "/submit_report";
    String DOCUMENTS_CMD = "/documents"; // команда выдать список документов

    /**
     * Info fields
     */
    String COMMON_INFO_FIELD = "common_info";
    String DATING_RULES_FIELD = "dating_rules";
    String DOCUMENTS_FIELD = "documents"; // необходимые документы
    
    /**
     * Messages
     */
    String SHELTER_CHOOSE_MSG = "Please select which shelter you are interested in";
    String DATING_RULES_NOT_FOUND = "No information found regarding handling pets. Please contact the administration.";
    String DOCUMENTS_NOT_FOUND = "The list of documents is missing. Please contact the administration.";
}
