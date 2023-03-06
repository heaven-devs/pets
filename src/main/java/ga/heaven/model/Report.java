package ga.heaven.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Report { // Таблица: Отчет (Report) (о питомце)
    @Id
    private long id; // уникальный id
    private String petReport; // отчет текстовый: рацион, самочувствие, поведение питомца

    private LocalDateTime date; // дата сдачи отчета

    // ------------------ фото -----------------
    // Описание файла с фото питомца
    private String filePath;
    private long fileSize;
    private String mediaType;
    private byte[] photo; // фото
    // ------------------ фото -----------------

    @ManyToOne
    @JoinColumn(name = "id_pet")
    private Pet pet; // id питомца (из таблицы Pet) (one-to-one)

    // -----------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return id == report.id && fileSize == report.fileSize && Objects.equals(petReport, report.petReport) && Objects.equals(date, report.date) && Objects.equals(filePath, report.filePath) && Objects.equals(mediaType, report.mediaType) && Objects.equals(pet, report.pet);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, petReport, date, filePath, fileSize, mediaType, pet);
        result = 31 * result + Arrays.hashCode(photo);
        return result;
    }
}
