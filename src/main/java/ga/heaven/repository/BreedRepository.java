package ga.heaven.repository;

import ga.heaven.model.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Long> {
}
