package ga.heaven.controller;

import ga.heaven.model.Report;
import ga.heaven.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.hibernate.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation (
            summary = "Search for all report by its ID in the database.",
            responses = {
        @ApiResponse(
                responseCode = "200",
                description = "Found reports",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = List.class)
                )
        ),

},
            tags = "Report"
    )
    @GetMapping
    public ResponseEntity<List<Report>> findAllReport(){
        return ResponseEntity.ok(reportService.findAllReports());
    }

    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Search for all report by its ID in the database.",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = Report[].class)
                )
        )
        })

    @GetMapping("/{reportId}")
    public ResponseEntity<Report> findReportById(@PathVariable long id){
        Report report = reportService.findReportsById(id);
        if(report==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Deleting  for a report by its ID in the database.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Report[].class)
                    )
            )
    })
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Report> deleteReport(@PathVariable Long id){
        reportService.deleteReport(id);
        return ResponseEntity.ok().build();
    }


}
