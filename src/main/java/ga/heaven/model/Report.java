package ga.heaven.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@Entity
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPetReport() {
        return petReport;
    }

    public void setPetReport(String petReport) {
        this.petReport = petReport;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

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
