package ga.heaven.controller;

import ga.heaven.model.Customer;
import ga.heaven.model.Info;
import ga.heaven.model.Pet;
import ga.heaven.service.InfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("info")
@RestController
@Tag(name = "\uD83D\uDCC4 Info store", description = "Info dependence model CRUD endpoints")
public class InfoController {
    private final InfoService infoService;

    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    //@GetMapping("/get-info-records")
    @Operation(
            summary = "Gets all records",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Info JSON",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Info.class)
                            )
                    ),
            },
            tags = "\uD83D\uDCC4 Info store"
    )
    @GetMapping
    public ResponseEntity <List<Info>> getAllInfoRecords() {
        return ResponseEntity.ok(infoService.getAll());
    }
    
    @Operation(
            summary = "Gets record by ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Info JSON",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Info.class)
                            )
                    ),
            },
            tags = "\uD83D\uDCC4 Info store"
    )
    @GetMapping("{id}")
    public ResponseEntity <Info> getInfoById(@PathVariable long id) {
        Info info = infoService.findInfoById(id);
        if (info == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(info);
        }
    }
    
    @Operation(
            summary = "Create record",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Info JSON",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Info.class)
                            )
                    ),
            },
            tags = "\uD83D\uDCC4 Info store"
    )
    @PostMapping
    public ResponseEntity <Info> createInfo(@RequestBody Info info) {
        return ResponseEntity.ok(infoService.createInfo(info));
    }
    
    @Operation(
            summary = "Edits record",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Info JSON",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Info.class)
                            )
                    ),
            },
            tags = "\uD83D\uDCC4 Info store"
    )
    @PutMapping
    public ResponseEntity <Info> updateInfo(@RequestBody Info newInfo) {
        Info info = infoService.updateInfo(newInfo);
        if (info == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(info);
        }
    }
    
    @Operation(
            summary = "Deletes record by ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Info JSON",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Info.class)
                            )
                    ),
            },
            tags = "\uD83D\uDCC4 Info store"
    )
    @DeleteMapping("{id}")
    public ResponseEntity <Info> removeInfo(@PathVariable Long id) {
        Info info = infoService.deleteInfoById(id);
        if (info == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(info);
    }
}
