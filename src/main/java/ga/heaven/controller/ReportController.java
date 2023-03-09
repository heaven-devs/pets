package ga.heaven.controller;

import ga.heaven.model.Report;
import ga.heaven.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
@Tag(name = "\uD83D\uDCCB Report store", description = "Report dependence model CRUD endpoints")
public class ReportController {
    private final ReportService reportService;
    
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    
    @Operation(
            tags = "\uD83D\uDCCB Report store",
            summary = "Search for all reports",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Found reports",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = List.class)
                            )
                    ),
                
            }
    )
    @GetMapping
    public ResponseEntity<List<Report>> findAllReportFindAllReport() {
        return ResponseEntity.ok(reportService.findAllReports());
    }
    
    @Operation(
            tags = "\uD83D\uDCCB Report store",
            summary = "Search a report by its ID in the database.",
            responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Report JSON",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Report[].class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Report> findReportById(@PathVariable Long id) {
        Report report = reportService.findReportsById(id);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }
    
    @Operation(
            tags = "\uD83D\uDCCB Report store",
            summary = "Deleting  for a report by its ID in the database.",
            responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Report JSON",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Report[].class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Report> deleteReport(@PathVariable Long id) {
        Report report = reportService.deleteReport(id);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }
    
    
}
