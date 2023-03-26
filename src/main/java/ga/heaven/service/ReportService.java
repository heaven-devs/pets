package ga.heaven.service;

import ga.heaven.model.Customer;
import ga.heaven.model.Pet;
import ga.heaven.model.Report;
import ga.heaven.model.ReportPhoto;
import ga.heaven.repository.ReportPhotoRepository;
import ga.heaven.repository.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class ReportService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ReportService.class);

    private final ReportRepository reportRepository;
    private final ReportPhotoRepository reportPhotoRepository;
    private final PetService petService;

    public ReportService(ReportRepository reportRepository, ReportPhotoRepository reportPhotoRepository, PetService petService) {

        this.reportRepository = reportRepository;
        this.reportPhotoRepository = reportPhotoRepository;
        this.petService = petService;
    }

    /**
     * Search for all reports from the database. (Table - Report)
     * The repository method is used{@link JpaRepository#findAll()}
     *
     * @return - found the reports
     */
    public List<Report> findAllReports() {
        return reportRepository.findAll();
    }

    /**
     * Search for a report by its ID in the database. (Table - Report)
     * The repository method is used{@link JpaRepository#findById(Object)}
     *
     * @param id - ID of the report we are looking for.
     * @return - found the report
     */
    public Report findReportById(long id) {
        return reportRepository.findById(id).orElse(null);
    }

    /**
     * Deleting  for a report by its ID in the database. (Table - Report)
     * The repository method is used{@link JpaRepository#deleteById(Object)}
     *
     * @param id - ID of the report we want to delete.
     */

    public Report deleteReport(long id) {
        Report report = findReportById(id);
        if (report != null) {
            reportRepository.delete(report);
        }
        return report;
    }

    /**
     * Create a report and add it to the database. (Table - Report)
     * The repository method is used{@link JpaRepository#save(Object)}
     *
     * @param report - The entity of the report we want to create.
     * @return - created the report
     */
    public Report createReport(Report report) {
        return reportRepository.save(report);
    }

    /**
     * Update an existing report in the database.
     * The repository method is used{@link JpaRepository#save(Object)
     *
     * @param report - ID of the volunteer we want to update.
     * @return - updated a report
     */
    public Report updateReport(Report report) {
        return reportRepository.save(report);
    }

    public Report findTodayCompletedReportsByPetId(Long petId) {
        LocalDate localDate = LocalDate.now();
        LocalDateTime startTime = localDate.atStartOfDay();
        LocalDateTime finishTime = LocalTime.MAX.atDate(localDate);
        Report todayReport = reportRepository.findFirstByPetIdAndPetReportNotNullAndDateBetween(petId, startTime, finishTime);
        if (todayReport == null) {
            return null;
        }
        ReportPhoto todayReportPhoto = reportPhotoRepository.findFirstByReportId(todayReport.getId());
        if (todayReportPhoto == null) {
            return null;
        }
        return todayReport;
    }

    public Report findTodayReportByPetId(Long petId) {
        LocalDate localDate = LocalDate.now();
        LocalDateTime startTime = localDate.atStartOfDay();
        LocalDateTime finishTime = LocalTime.MAX.atDate(localDate);
        return reportRepository.findReportByPetIdAndDateBetween(petId, startTime, finishTime);
    }


    /**
     * Метод ищет питомцев пользователя, для которых сегодня не был сдан отчет.
     *
     * @return список питомцев
     */
    List<Pet> findPetsWithoutTodayReport(Customer customer) {
        List<Pet> petWithoutReportList = new ArrayList<>();
        for (Pet pet : petService.findPetsByCustomer(customer)) {
            Report report = findTodayCompletedReportsByPetId(pet.getId());
            if (null == report) {
                petWithoutReportList.add(pet);
            }
        }
        return petWithoutReportList;
    }
}

