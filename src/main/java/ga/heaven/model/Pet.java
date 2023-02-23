package ga.heaven.model;

import javax.persistence.Entity;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@Entity
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // уникальный id
    String breed; // порода питомца (many-to-one к табл Pets_care_recommendations)
    int age; // возраст (месяцев)
    String name; // имя питомца
    @ManyToOne
    @JoinColumn(name = "id_customer")
    private Customer customer;

    // ------------------ фото -----------------
    // Описание файла с фото питомца
    private String filePath;
    private long fileSize;
    private String mediaType;
    private byte[] photo; // фото
    // ------------------ фото -----------------

    private LocalDateTime decisionDate; // дата принятия решения по усыновлению
    @OneToOne
    @JoinColumn(name="id_shelter")
    private Shelter shelter; // ссылка на приют питомца

    // ----------------------------------------------
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public LocalDateTime getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(LocalDateTime decisionDate) {
        this.decisionDate = decisionDate;
    }

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }

    public long getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public String getBreed() {
        return breed;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return age == pet.age && Objects.equals(breed, pet.breed) && Objects.equals(name, pet.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(breed, age, name);
    }
}
