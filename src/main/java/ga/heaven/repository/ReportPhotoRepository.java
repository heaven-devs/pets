package ga.heaven.repository;

import ga.heaven.model.ReportPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportPhotoRepository extends JpaRepository<ReportPhoto, Long> {

    ReportPhoto findAnyPhotoByReportId(Long id);
}
