package ga.heaven.service;

import ga.heaven.repository.InfoRepository;
import org.springframework.stereotype.Service;

@Service
public class InfoService {
    private final InfoRepository infoRepository;

    public InfoService(InfoRepository infoRepository) {
        this.infoRepository = infoRepository;
    }
}
