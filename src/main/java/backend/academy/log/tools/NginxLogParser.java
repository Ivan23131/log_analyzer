package backend.academy.log.tools;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NginxLogParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(NginxLogParser.class);

    public static LogInformation parseLogFile(
        String[] sources,
        PrintStream output,
        LogConditionsChecker logConditionsChecker
    ) {
        LogInformation logInformation = new LogInformation();
        logInformation.setLogFiles(sources);
        String line;
        for (String source : sources) {
            try {
                if (source.startsWith("http://") || source.startsWith("https://")) {
                    // Если это URL, читаем данные из URL
                    try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new URL(source).openStream()))) {
                        while ((line = reader.readLine()) != null) {
                            NginxLogEntry entry = parseLogEntry(line);
                            if (logConditionsChecker.isLogSatisfyConditions(entry)) {
                                logInformation.logAdd(entry);
                            }
                        }
                    }
                } else {
                    // Если это локальный файл, читаем данные из файла
                    try (BufferedReader reader = Files.newBufferedReader(Paths.get(source))) {
                        while ((line = reader.readLine()) != null) {
                            NginxLogEntry entry = parseLogEntry(line);
                            logInformation.logAdd(entry);
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Problems with rading files", e);
            }
        }
        return logInformation;
    }

    private static NginxLogEntry parseLogEntry(String line) {
        NginxLogEntry entry = new NginxLogEntry(line);
        return entry;
    }

    private NginxLogParser() {

    }
}
