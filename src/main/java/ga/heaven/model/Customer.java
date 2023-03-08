package ga.heaven.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

// Таблица: Клиент (Customer) в БД
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    
    public enum CustomerStatus {
        GUEST,
        ON_PROBATION,
        INELIGIBLE,
        PARENT
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private String surname;
    private String name;
    private String secondName;
    private String phone;
    private String address;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private CustomerContext customerContext;

    public void setCustomerContext(CustomerContext customerContext) {
        this.customerContext = customerContext;
        this.customerContext.setCustomer(this);
    }

    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return  // id == customer.id &&
                // chatId == customer.chatId &&
                Objects.equals(surname, customer.surname) &&
                Objects.equals(name, customer.name)
                && Objects.equals(secondName, customer.secondName)
                //&& Objects.equals(phone, customer.phone)
                && Objects.equals(address, customer.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, surname, name, secondName, phone, address);
    }*/
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Customer customer = (Customer) o;
        return id != null && Objects.equals(id, customer.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
