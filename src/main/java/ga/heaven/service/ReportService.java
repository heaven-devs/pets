package ga.heaven.service;

import ga.heaven.model.Report;
import ga.heaven.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {

        this.reportRepository = reportRepository;
    }

    public List<Report> findAllReports(){
        return reportRepository.findAll();
    }

    public Report findReportsById(long id){
        return reportRepository.findById(id).orElse(null);
    }

    public void deleteReport(long id){
        reportRepository.deleteById(id);
    }

}

