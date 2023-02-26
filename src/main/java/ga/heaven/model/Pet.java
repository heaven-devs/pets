package ga.heaven.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Pet {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный id
    
//    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_breed")
    private Breed breed; // порода питомца (many-to-one к табл Pets_care_recommendations)
    private Integer age; // возраст (месяцев)
    private String name; // имя питомца
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_customer")
    private Customer customer;
    
    // ------------------ фото -----------------
    // Описание файла с фото питомца
    private String filePath;
    private Long fileSize;
    private String mediaType;
    private byte[] photo; // фото
    // ------------------ фото -----------------
    
    private LocalDateTime decisionDate; // дата принятия решения по усыновлению
    
//    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_shelter")
    private Shelter shelter; // ссылка на приют питомца
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Pet pet = (Pet) o;
        return id != null && Objects.equals(id, pet.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
