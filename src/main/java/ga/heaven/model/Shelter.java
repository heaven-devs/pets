package ga.heaven.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Shelter { // Таблица: Приют
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // уникальный id

    private String name; // название приюта питомцев
    private String address; // адрес
    private String locationMap; // ссылка на схему проезда

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shelter shelter = (Shelter) o;

        if (id != shelter.id) return false;
        if (name != null ? !name.equals(shelter.name) : shelter.name != null) return false;
        if (address != null ? !address.equals(shelter.address) : shelter.address != null) return false;
        return locationMap != null ? locationMap.equals(shelter.locationMap) : shelter.locationMap == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (locationMap != null ? locationMap.hashCode() : 0);
        return result;
    }

}
