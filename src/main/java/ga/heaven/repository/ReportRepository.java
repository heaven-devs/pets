package ga.heaven.repository;

import ga.heaven.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ReportRepository  extends JpaRepository<Report, Long> {

    /**
     *
     * @param petId value of "petId" field
     * @param startTime beginning of the time period
     * @param finishTime end of the time period
     * @return a "Report" found in a certain time period
     */
    Report findReportByPetIdAndDateBetween(Long petId, LocalDateTime startTime, LocalDateTime finishTime);

    Report findFirstByPetIdAndPetReportNotNullAndDateBetween(Long petId, LocalDateTime startTime, LocalDateTime finishTime);
}
