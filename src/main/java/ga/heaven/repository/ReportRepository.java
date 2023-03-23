package ga.heaven.repository;

import ga.heaven.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ReportRepository  extends JpaRepository<Report, Long> {

    Report findReportByPetIdAndDateBetween(Long petId, LocalDateTime startTime, LocalDateTime finishTime);

    // todo: переделать
//    Report findReportByPetIdAndPetReportNotNullAndPhotoIsNotNullAndDateBetween(Long petId, LocalDateTime startTime, LocalDateTime finishTime);
    Report findReportByPetIdAndPetReportNotNullAndDateBetween(Long petId, LocalDateTime startTime, LocalDateTime finishTime);
}
