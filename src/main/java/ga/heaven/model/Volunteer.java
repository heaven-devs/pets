package ga.heaven.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class Volunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private String surname;
    private String name;
    private String secondName;
    private String phone;
    private String address;
    @JsonIgnore
    @ManyToMany
    @JoinColumn(name = "id_shelter")
    private Set<Shelter> shelter;

    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Volunteer volunteer = (Volunteer) o;
        return //id == volunteer.id &&
                //chatId == volunteer.chatId &&
                Objects.equals(surname, volunteer.surname) &&
                Objects.equals(name, volunteer.name) &&
                Objects.equals(secondName, volunteer.secondName) &&
                //Objects.equals(phone, volunteer.phone) &&
                Objects.equals(address, volunteer.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, surname, name, secondName, phone, address);
    }*/
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Volunteer volunteer = (Volunteer) o;
        return id != null && Objects.equals(id, volunteer.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
