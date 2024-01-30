package backend;

import lombok.extern.slf4j.Slf4j;

import java.util.StringJoiner;
@Slf4j
public class ExceptionLogger {

    public static String logAsError(Exception exception, String exceptionDescription) {
        StringJoiner sj = new StringJoiner(System.lineSeparator());
        sj.add(exceptionDescription);
        sj.add(exception.toString());
        for (StackTraceElement s : exception.getStackTrace()) {
            sj.add(s.toString());
        }
        log.error(sj.toString());
        return "Exception occurred";
    }

    public static String logAsDebug(Exception exception, String exceptionDescription) {
        StringJoiner sj = new StringJoiner(System.lineSeparator());
        sj.add(exceptionDescription);
        sj.add(exception.toString());
        for (StackTraceElement s : exception.getStackTrace()) {
            sj.add(s.toString());
        }
        log.debug(sj.toString());
        return "Exception occurred";
    }

    public static void logAsError2(Exception exception) {
        log.error(exception.toString(),exception);
    }
}
