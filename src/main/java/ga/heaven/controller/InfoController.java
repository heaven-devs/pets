package ga.heaven.controller;

import ga.heaven.model.Customer;
import ga.heaven.model.Info;
import ga.heaven.service.InfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("info")
@RestController
public class InfoController {
    private final InfoService infoService;

    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    //@GetMapping("/get-info-records")
    @GetMapping
    public ResponseEntity <List<Info>> getAllInfoRecords() {
        return ResponseEntity.ok(infoService.getAll());
    }

    @GetMapping("{id}")
    public ResponseEntity <Info> getInfoById(@PathVariable long id) {
        Info info = infoService.findInfoById(id);
        if (info == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(info);
        }
    }

    @PostMapping
    public ResponseEntity <Info> createInfo(@RequestBody Info info) {
        return ResponseEntity.ok(infoService.createInfo(info));
    }

    @PutMapping
    public ResponseEntity <Info> updateInfo(@RequestBody Info newInfo) {
        Info info = infoService.updateInfo(newInfo);
        if (info == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(info);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity <Info> removeInfo(@PathVariable Long id) {
        Info info = infoService.deleteInfoById(id);
        if (info == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(info);
    }
}
