package ga.heaven.repository;

import ga.heaven.model.Customer;
import ga.heaven.model.Pet;
import ga.heaven.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    /**
     * Список питомцев по типу(породе) животного.
     */
    List<Pet> findPetsByBreed(String breed);
    /**
     * Поиск по бд питомцев по усыновителю.
     */
    List<Pet> findPetsByCustomer(Customer customer);
    List<Pet> findPetsByCustomerOrderById(Customer customer);

    //@Query("SELECT p FROM pet LEFT JOIN customer c ON pet.id_customer = c.id WHERE c.chat_id = :#{#customer} AND pet.id NOT IN (SELECT pet.id FROM pet LEFT JOIN report ON pet.id = report.id_pet WHERE (date BETWEEN :#{#startTime} AND :#{#finishTime}))")
    //List<Pet> findCustomerPetsWithoutReportsToday(@Param("customerId") Long customerId, @Param("startTime") LocalDateTime startTime, @Param("finishTime") LocalDateTime finishTime);
}
