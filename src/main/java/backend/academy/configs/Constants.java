package backend.academy.configs;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

public class Constants {
    public static final Pattern LOG_PATTERN = Pattern.compile(
        "^(\\S+) (\\S+) (\\S+) \\[([^\\]]+)\\] \"([^\"]+)\" (\\d+) (\\d+) \"([^\"]+)\" \"([^\"]+)\"");
    public static final DateTimeFormatter DATE_KEY_FORMATE = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
    public static final DateTimeFormatter DATE_LOG_FORMATE =
        DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm", Locale.ENGLISH);
    public static final int PREC_BUFFER = 200;
    public static final double PRECENTIL = 95.0;
    public static final double MAX_PRECENTIL = 100.0;
    public static final int AMOUNT_OF_POPULAR = 3;
    public static final int AMOUNT_SECCONDS_IN_MINTES = 60;

    private Constants() {

    }
}
