package ga.heaven.model;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CustomerContext {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Context dialogContext;
    private long currentPetId;

    public enum Context {
        FREE,
        WAIT_PET_ID,
        WAIT_REPORT
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerContext that = (CustomerContext) o;
        return id == that.id && currentPetId == that.currentPetId && Objects.equals(dialogContext, that.dialogContext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dialogContext, currentPetId);
    }

}
