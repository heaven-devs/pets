package ga.heaven.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
public class Breed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String breed; // порода питомца (key field)
    private String recommendationsChild; // рекомендации по уходу за щенком
    private String recommendationsAdult; // рекомендации по уходу за взрослой собакой
    
    private int adultPetFromAge; // ≥ возраст взрослой собаки (признак взрослой собаки, если больше этого значения)

    public Breed() {
    }

    public Breed(long id, String breed, String recommendationsChild, String recommendationsAdult, int adultPetFromAge) {
        this.id = id;
        this.breed = breed;
        this.recommendationsChild = recommendationsChild;
        this.recommendationsAdult = recommendationsAdult;
        this.adultPetFromAge = adultPetFromAge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Breed that = (Breed) o;
        return adultPetFromAge == that.adultPetFromAge && Objects.equals(breed, that.breed) && Objects.equals(recommendationsChild, that.recommendationsChild) && Objects.equals(recommendationsAdult, that.recommendationsAdult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(breed, recommendationsChild, recommendationsAdult, adultPetFromAge);
    }
}
