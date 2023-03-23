package ga.heaven.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Data
public class Report { // Таблица: Отчет (Report) (о питомце)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный id
    private String petReport; // отчет текстовый: рацион, самочувствие, поведение питомца

    private LocalDateTime date; // дата сдачи отчета

    @ManyToOne
    @JoinColumn(name = "id_pet")
    private Pet pet; // id питомца (из таблицы Pet) (one-to-one)

}
