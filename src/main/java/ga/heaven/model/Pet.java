package ga.heaven.model;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.Objects;

@Entity
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // уникальный id
    String breed; // порода питомца (many-to-one к табл Pets_care_recommendations)
    int age; // возраст (месяцев)
    String name; // имя питомца
    @OneToOne
    @JoinColumn(name = "id_customer")
    private Customer customer;

    public long getId() {
        return id;
    }

    public String getBreed() {
        return breed;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return id == pet.id && age == pet.age && Objects.equals(breed, pet.breed) && Objects.equals(name, pet.name) && Objects.equals(customer, pet.customer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, breed, age, name, customer);
    }
}
