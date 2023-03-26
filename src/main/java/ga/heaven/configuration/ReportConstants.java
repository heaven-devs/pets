package ga.heaven.configuration;

public class ReportConstants {
    public static final String CR = "\r\n";

    public static final String ANSWER_DONT_HAVE_PETS = "У вас нет питомцев";
    public static final String ANSWER_NO_NEED_TO_REPORT = "Сегодня вы сдали отчеты обо всех своих питомцах";
    public static final String ANSWER_ONE_PET = "Отправьте отчет о питомце по кличке ";
    public static final String ANSWER_ENTER_PET_ID = "Введите id питомца:";
    public static final String ANSWER_NON_EXISTENT_PET = "Некорректные данные, введите id питомца из списка выше.";
    public static final String ANSWER_SEND_REPORT_FOR_PET_WITH_ID = "Отправьте отчет о питомце с id = ";
    public static final String ANSWER_WAIT_REPORT = "Ожидаю отчет";
    public static final String ANSWER_PHOTO_REPORT_ACCEPTED = "Фото отчет сдан, отправьте текстовый отчет (одним сообщением)";
    public static final String ANSWER_TEST_REPORT_ACCEPTED = "Текстовый отчет сдан, отправьте фото отчет";
    public static final String ANSWER_REPORT_ACCEPTED = "<b>Отчет принят</b>";
    public static final String ANSWER_REPORT_NOT_ACCEPTED_DESCRIPTION_REQUIRED =
            "<b>Отчет не принят</b>" + CR +
                    "Пришлите следующую информацию:" + CR +
                    "1. Рацион животного." + CR +
                    "2. Общее самочувствие и привыкание к новому месту." + CR +
                    "3. Изменение в поведении: отказ от старых привычек, приобретение новых.";
    public static final String ANSWER_REPORT_NOT_ACCEPTED_PHOTO_REQIRED =
            "<b>Отчет не принят</b>" + CR +
                    "Пришлите фотографию питомца";
    public static final String ANSWER_PHOTO_ADD_TO_REPORT = "Фото добавлено к отчету";
    public static final String ANSWER_UNRECOGNIZED_PHOTO = "Нераспознанная фотография";

}
