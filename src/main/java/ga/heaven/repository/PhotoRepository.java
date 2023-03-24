package ga.heaven.repository;

import ga.heaven.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    Photo findAnyPhotoByReportId(Long id);
}
