package ga.heaven.controller;

import com.google.gson.Gson;
import ga.heaven.model.Pet;
import ga.heaven.model.Report;
import ga.heaven.model.Shelter;
import ga.heaven.model.Volunteer;
import ga.heaven.repository.ReportRepository;
import ga.heaven.service.ReportService;
import liquibase.pro.packaged.R;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReportController.class)
public class ReportControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReportRepository reportRepository;

    @SpyBean
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    @Test
    public void testFindAllReportFindAllReport() throws Exception{
        List <Report> reports = reportsForTest();
        when(reportRepository.findAll()).thenReturn(reports);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();
    }

    @Test
    public  void testFindReportById(){
        Report report1 = createTestReport(1L, "test1", LocalDate.of(2023, 3, 5).atStartOfDay(), "12.jpeg", 1000L, null, null, null);
        Report reportW = createTestReport(2L, "test2", null, null, 1050L, null, null, null);
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report1));
        when(reportRepository.findById(2L)).thenReturn(Optional.of(reportW));
        Report expected = report1;
        Report actual = reportService.findReportsById(1L);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getId()).isEqualTo(expected.getId());

        Report actualWrong= reportService.findReportsById(2L);
        Assertions.assertThat(actualWrong.getId()).isNotNull();
        Assertions.assertThat(actualWrong.getId()).isNotEqualTo(expected.getId());
    }

    @Test
    public  void testDeleteReport()throws Exception{

        Report report1 = createTestReport(1L, "test1", LocalDate.of(2023, 3, 5).atStartOfDay(), "12.jpeg", 1000L, null, null, null);

        when(reportRepository.findById(report1.getId())).thenReturn(Optional.of(report1));

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .delete("/volunteer/{id}",  report1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Gson gson = new Gson();
        Shelter actual = gson.fromJson(response.getContentAsString(), (Type) Report.class);
        assertThat(actual).isEqualTo(report1);

    }


    private List<Report> reportsForTest() {

        LocalDateTime d = LocalDate.of(2023, 3, 5).atStartOfDay();
        Report report1 = createTestReport(1L, "test1", d, "12.jpeg", 1000L, null, null, null);
        Report report2 = createTestReport(2L, "test2", null, null, 1050L, null, null, null);

        List<Report> reports = new ArrayList<>();
        reports.add(report1);
        reports.add(report2);

        return reports;
    }

    private Report createTestReport(long id, String petReport, LocalDateTime date, String filePath, Long fileSize, String mediaType, byte[] photo, Pet petId) {
        Report report = new Report();
        report.setId(id);
        report.setPetReport(petReport);
        report.setDate(date);
        report.setFilePath(filePath);
        report.setFileSize(fileSize);
        report.setMediaType(mediaType);
        report.setPhoto(photo);
        report.setPet(petId);
        return (report);
    }

}
