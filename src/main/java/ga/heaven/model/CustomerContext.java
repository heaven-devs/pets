package ga.heaven.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class CustomerContext {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String dialogContext;
    private long petId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDialogContext() {
        return dialogContext;
    }

    public void setDialogContext(String dialogContext) {
        this.dialogContext = dialogContext;
    }

    public long getPetId() {
        return petId;
    }

    public void setPetId(long petId) {
        this.petId = petId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerContext that = (CustomerContext) o;
        return id == that.id && petId == that.petId && Objects.equals(dialogContext, that.dialogContext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dialogContext, petId);
    }

    @Override
    public String toString() {
        return "CustomerContext{" +
                "id=" + id +
                ", dialogContext='" + dialogContext + '\'' +
                ", petId=" + petId +
                '}';
    }
}
