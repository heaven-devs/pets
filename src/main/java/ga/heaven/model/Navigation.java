package ga.heaven.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
/*@Entity
@Value
@AllArgsConstructor
@NoArgsConstructor*/
public class Navigation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long levelView;
    private Long levelReference;
    private String endpoint;
    private String rules;
    private String text;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "shelter_id")
    Shelter shelterId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Navigation that = (Navigation) o;
        return id != null && Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
