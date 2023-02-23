package ga.heaven.model;

import javax.persistence.*;

@Entity
public class Report { // Таблица: Отчет (Report) (о питомце)
    @Id
    private long id; // уникальный id
    private String foodRation; // рацион
    private String health; // самочувствие питомца
    private String petBehavior; // поведение питомца
    @OneToOne
    @JoinColumn(name = "id_customer")
    private Customer customer; // id Пользователя (из таблицы Customer) (one-to-one)

    @OneToOne
    @JoinColumn(name = "id_pet")
    private Pet pet; // id питомца (из таблицы Pet) (one-to-one)

}
