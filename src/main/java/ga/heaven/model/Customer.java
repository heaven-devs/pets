package ga.heaven.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.Objects;

// Таблица: Пользователь (Customer) в БД
@Entity
@Getter
@Setter
@ToString
public class Customer {
    
    public enum CustomerStatus {
        GUEST,
        ON_PROBATION,
        INELIGIBLE,
        PARENT
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // уникальный id
    private long chatId; // id Telegram чата
    private String surname; // фамилия
    private String name; // имя
    private String secondName; // отчество
    private String phone; // тлф формата +70000000000
    private String address; // адрес

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CustomerContext customerContext;

    public void setCustomerContext(CustomerContext customerContext) {
        this.customerContext = customerContext;
        this.customerContext.setCustomer(this);
    }

    @Override
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
    }

}
