package backend;

import lombok.extern.slf4j.Slf4j;

import java.util.StringJoiner;
@Slf4j
public class ExceptionLogger {

    public static String getExceptionStackAsString(Exception exception, String exceptionDescription) {
        StringJoiner sj = new StringJoiner(System.lineSeparator());
        sj.add(exceptionDescription);
        sj.add(exception.toString());
        for (StackTraceElement s : exception.getStackTrace()) {
            sj.add(s.toString());
        }
        log.error(sj.toString());
        return "Exception occurred";
    }
}
