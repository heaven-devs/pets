package ga.heaven.repository;

import ga.heaven.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReportRepository  extends JpaRepository<Report, Long> {

    Report findReportByPetIdAndDateBetween(Long petId, LocalDateTime startTime, LocalDateTime finishTime);

    List<Report> findAllByDateBetween(LocalDateTime startTime, LocalDateTime finishTime);


}
