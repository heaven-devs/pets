package ga.heaven.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PeripheralServiceImpl implements PeripheralService {
    
    private final Logger LOGGER = LoggerFactory.getLogger(PeripheralService.class);
    
    public PeripheralServiceImpl() {
    
    }
    
    @Override
    public String Index() {
        return null;
    }
}
