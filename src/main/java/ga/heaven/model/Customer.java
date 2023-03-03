package ga.heaven.model;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.Objects;

// Таблица: Пользователь (Customer) в БД
@Entity
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
    private String status; // статус диалога с пользователем

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Customer() {
    }

    public long getId() {
        return id;
    }

    public long getChatId() {
        return chatId;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
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
