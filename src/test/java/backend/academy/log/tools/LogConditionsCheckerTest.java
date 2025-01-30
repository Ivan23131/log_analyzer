package backend.academy.log.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class LogConditionsCheckerTest {

    private LogConditionsChecker checker;

    @BeforeEach
    void setUp() {
        checker = new LogConditionsChecker();
    }

    @Test
    void testIsLogSatisfyConditions_DateRange() {
        checker.setFrom(LocalDate.of(2023, 1, 1));
        checker.setTo(LocalDate.of(2023, 12, 31));

        NginxLogEntry log = new NginxLogEntry("80.91.33.133 - - [17/May/2015:08:05:35 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"");
        log.setTimeLocal("01/Jan/2023:12:34:56 +0000");

        assertFalse(checker.isLogSatisfyConditions(log));

        log.setTimeLocal("01/Jan/2024:12:34:56 +0000");
        assertFalse(checker.isLogSatisfyConditions(log));
    }

    @Test
    void testIsLogSatisfyConditions_FilterByAgent() {
        checker.setFilterField("agent");
        checker.setFilterValue("Mozilla");

        NginxLogEntry log = new NginxLogEntry("80.91.33.133 - - [17/May/2015:08:05:35 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"");
        log.setHttpUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

        assertFalse(checker.isLogSatisfyConditions(log));

        log.setHttpUserAgent("Opera/9.80 (Windows NT 6.1; WOW64) Presto/2.12.388 Version/12.18");
        assertFalse(checker.isLogSatisfyConditions(log));
    }

    @Test
    void testIsLogSatisfyConditions_FilterByMethod() {
        checker.setFilterField("method");
        checker.setFilterValue("GET");

        NginxLogEntry log = new NginxLogEntry("80.91.33.133 - - [17/May/2015:08:05:35 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"");
        log.setRequest("GET /index.html HTTP/1.1");

        assertTrue(checker.isLogSatisfyConditions(log));

        log.setRequest("POST /index.html HTTP/1.1");
        assertFalse(checker.isLogSatisfyConditions(log));
    }

    @Test
    void testIsLogSatisfyConditions_FilterByStatus() {
        checker.setFilterField("status");
        checker.setFilterValue("200");

        NginxLogEntry log = new NginxLogEntry("80.91.33.133 - - [17/May/2015:08:05:35 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"");
        log.setStatus(200);

        assertTrue(checker.isLogSatisfyConditions(log));

        log.setStatus(404);
        assertFalse(checker.isLogSatisfyConditions(log));
    }

    @Test
    void testIsLogSatisfyConditions_FilterByAddress() {
        checker.setFilterField("addres");
        checker.setFilterValue("192.168.1.1");

        NginxLogEntry log = new NginxLogEntry("80.91.33.133 - - [17/May/2015:08:05:35 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"");
        log.setRemoteAddr("192.168.1.1");

        assertTrue(checker.isLogSatisfyConditions(log));

        log.setRemoteAddr("192.168.1.2");
        assertFalse(checker.isLogSatisfyConditions(log));
    }
}
