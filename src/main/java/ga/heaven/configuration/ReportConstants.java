package ga.heaven.configuration;

public class ReportConstants {
    public static final String CARRIAGE_RETURN = "\r\n";

    public static final String ANSWER_DONT_HAVE_PETS = "У вас нет питомцев";
    public static final String ANSWER_ONE_PET = "У вас один питомец, отправьте отчет";
    public static final String ANSWER_ENTER_PET_ID = "Введите id питомца:";
    public static final String ANSWER_NON_EXISTENT_PET = "Некорректные данные, введите id питомца из списка выше.";
    public static final String ANSWER_SEND_REPORT_FOR_PET_WITH_ID = "Отправьте отчет о питомце с id = ";

    public static final String ANSWER_REPORT_ACCEPTED = "<b>Отчет принят, спасибо</b>";
    public static final String ANSWER_REPORT_NOT_ACCEPTED_DESCRIPTION_REQUIRED =
            "<b><span style='color: red'>Отчет не принят</span></b>" + CARRIAGE_RETURN +
            "Пришлите следующую информацию:" + CARRIAGE_RETURN +
            "1. Рацион животного." + CARRIAGE_RETURN +
            "2. Общее самочувствие и привыкание к новому месту." + CARRIAGE_RETURN +
            "3. Изменение в поведении: отказ от старых привычек, приобретение новых.";
    public static final String ANSWER_REPORT_NOT_ACCEPTED_PHOTO_REQIRED =
            "<b>Отчет не принят</b>" + CARRIAGE_RETURN + "Пришлите фотографию питомца";

}
