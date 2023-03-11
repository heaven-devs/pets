package ga.heaven.model;

import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
        if (!(o instanceof Shelter)) return false;
        Shelter that = (Shelter) o;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(name, that.name);
        eb.append(address, that.address);
        eb.append(locationMap, that.locationMap);
        return eb.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(name);
        hcb.append(address);
        hcb.append(locationMap);
        return hcb.toHashCode();
    }

}
