package seedsearch;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class CustomFormatter extends SimpleFormatter {
    @Override
    public String format(LogRecord record) {
        return String.format("[%s] %s: %s%n",
                record.getLevel(),
                record.getSourceClassName(),
                record.getMessage());
    }
}