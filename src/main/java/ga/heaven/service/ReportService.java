package ga.heaven.service;

import ga.heaven.model.Report;
import ga.heaven.repository.ReportRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {

        this.reportRepository = reportRepository;
    }

    /**
     * Search for all reports from the database. (Table - Report)
     * The repository method is used{@link JpaRepository#findAll()}
     * @return - found reports
     */
    public List<Report> findAllReports(){
        return reportRepository.findAll();
    }

    /**
     *Search for a report by its ID in the database. (Table - Report)
     * The repository method is used{@link JpaRepository#findById(Object)}
     * @param id - ID of the report we are looking for.
     * @return - found report
     */
    public Report findReportsById(long id){
        return reportRepository.findById(id).orElse(null);
    }

    /**
     * Deleting  for a report by its ID in the database. (Table - Report)
     * The repository method is used{@link JpaRepository#deleteById(Object)}
     * @param id - ID of the report we want to delete.
     */

    public void deleteReport(long id){
        reportRepository.deleteById(id);
    }

}

