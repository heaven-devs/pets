package ga.heaven.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mediaType;
    private byte[] photo;

    @ManyToOne
    @JoinColumn(name = "id_report")
    private Report report;
}
