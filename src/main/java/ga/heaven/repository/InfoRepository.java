package ga.heaven.repository;

import ga.heaven.model.Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InfoRepository extends JpaRepository<Info, Long> {
    /**
     *
     * @param key string value of "area" field
     * @return the found entry in the database table
     * @see Info
     */
    Optional<Info> findFirstByAreaContainingIgnoreCase(String key);

    //@Query(value = "SELECT * FROM Info ORDER BY id", nativeQuery = true)
    //List<Info> findAll();

    /**
     *
     * @param id value of "id" field
     * @return found record "Info"
     */
    Optional<Info> findInfoById(long id);

    /**
     *
     * @param area value of "area" field
     * @return found record
     */
    Optional<Info> findInfoByArea(String area);
}
