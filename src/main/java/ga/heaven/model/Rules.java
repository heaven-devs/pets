package ga.heaven.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Rules {
    @Id
    private String area; // область правил (key): транспортировка, обустройства дома щенка, взрослого, советы кинолога, проверенные кинологи, причина отказа
    private String instructions; // правила (инструкции)

    public String getArea() {
        return area;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rules rules = (Rules) o;
        return Objects.equals(area, rules.area) && Objects.equals(instructions, rules.instructions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(area, instructions);
    }
}
