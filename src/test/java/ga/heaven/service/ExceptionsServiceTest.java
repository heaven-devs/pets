package ga.heaven.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static ga.heaven.service.ExceptionsService.statusByException;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionsServiceTest {
    public static final String UNKNOWN_EXCEPTION_STRING =
            "2023-03-07 12:37:42.575 ERROR 2222461 ---blabla... abrakadabra ...blabla...";
    public static final String EXCEPTION_MATCHING_REGEXP =
            "2023-03-07 12:37:42.575 ERROR 2222461 ---blabla... constraint ...blabla...";
    @Test
    void statusByExceptionTest() {
        HttpStatus actual = statusByException(UNKNOWN_EXCEPTION_STRING);
        HttpStatus expected = HttpStatus.I_AM_A_TEAPOT;
        assertEquals(expected, actual);
    
        actual = statusByException(EXCEPTION_MATCHING_REGEXP);
        expected = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
        assertEquals(expected, actual);
    }
}