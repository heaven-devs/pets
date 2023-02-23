package ga.heaven.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.*;
import java.util.Objects;

@Entity
public class Volunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // уникальный id
    private long chatId; // id Telegram чата
    private String surname; // фамилия
    private String name; // имя
    private String secondName; // отчество
    private String phone; // тлф формата +70000000000
    private String address; // адрес

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
    }
}
