package ga.heaven.repository;

import ga.heaven.model.Report;
import ga.heaven.model.ReportPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportPhotoRepository extends JpaRepository<ReportPhoto, Long> {

    ReportPhoto findFirstByReportId(Long id);

    List<ReportPhoto> findAllByReport(Report report);
}
