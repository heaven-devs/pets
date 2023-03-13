package ga.heaven.service;

import org.springframework.http.HttpStatus;

public class ExceptionsService {
    public static HttpStatus statusByException(String exceptionMsg) {
        HttpStatus result = HttpStatus.I_AM_A_TEAPOT;
        result = exceptionMsg.matches("(?i).*" + "SQL" + ".*") ? HttpStatus.BAD_REQUEST : result;
        result = exceptionMsg.matches("(?i).*" + "constraint" + ".*") ? HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE : result;
        return result;
    }
}
