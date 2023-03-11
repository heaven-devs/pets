package ga.heaven.model;

import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Breed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String breed; // порода питомца (key field)
    private String recommendationsChild; // рекомендации по уходу за щенком
    private String recommendationsAdult; // рекомендации по уходу за взрослой собакой
    
    private int adultPetFromAge; // ≥ возраст взрослой собаки (признак взрослой собаки, если больше этого значения)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Breed)) return false;
        Breed that = (Breed) o;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(breed, that.breed);
        eb.append(recommendationsChild, that.recommendationsChild);
        eb.append(recommendationsAdult, that.recommendationsAdult);
        eb.append(adultPetFromAge, that.adultPetFromAge);
        return eb.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(breed)
                .append(recommendationsChild)
                .append(recommendationsAdult)
                .append(adultPetFromAge);
        return hcb.toHashCode();
    }
}
