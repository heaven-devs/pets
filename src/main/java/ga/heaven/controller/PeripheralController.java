package ga.heaven.controller;

import ga.heaven.service.PeripheralService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@Tag(name = "⚙️ Peripheral endpoints", description = "")
public class PeripheralController {
    private final PeripheralService peripheralService;
    
    public PeripheralController(PeripheralService peripheralService) {
        this.peripheralService = peripheralService;
    }
    
    @Hidden
    @GetMapping()
    public String showIndexPage() {
        return peripheralService.Index();
    }
    
}
