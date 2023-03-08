package ga.heaven.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PeripheralServiceImplTest {
    public static final String GREETING_HAS_STRING =
            "frontend will be here soon";
    private final PeripheralServiceImpl out = new PeripheralServiceImpl();
    
    @Test
    void indexTest() {
        assertTrue(out.index().contains(GREETING_HAS_STRING));
    }
}