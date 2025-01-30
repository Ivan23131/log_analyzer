package backend.academy.log.tools;

import it.unimi.dsi.fastutil.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

class LogInformationTest {

    private LogInformation logInformation;

    @BeforeEach
    void setUp() {
        logInformation = new LogInformation();
    }

    @Test
    void testLogAdd() {
        NginxLogEntry entry = new NginxLogEntry("80.91.33.133 - - [17/May/2015:08:05:35 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"");
        entry.setRequest("GET /downloads/product_1 HTTP/1.1");
        entry.setStatus(200);
        entry.setBodyBytesSent(1024);
        entry.setTimeLocal("17/May/2015:08:05:35 +0000");

        logInformation.logAdd(entry);

        assertEquals(1, logInformation.getNumRequests());
        assertEquals(1024, logInformation.getResponseSize());
        assertEquals(1, logInformation.getRequestedResources().size());
        assertEquals(1, logInformation.getStatuses().size());
        assertEquals(1, logInformation.getLogCountPerMinet().size());
    }

    @Test
    void testResourceAnalysis() {
        NginxLogEntry entry = new NginxLogEntry("80.91.33.133 - - [17/May/2015:08:05:35 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"");
        entry.setRequest("GET /downloads/product_1 HTTP/1.1");
        logInformation.logAdd(entry);

        assertEquals(1, logInformation.getRequestedResources().get("/downloads/product_1"));
    }

    @Test
    void testStatusesAnalysis() {
        NginxLogEntry entry = new NginxLogEntry("80.91.33.133 - - [17/May/2015:08:05:35 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"");
        entry.setStatus(200);
        logInformation.logAdd(entry);

        assertEquals(1, logInformation.getStatuses().get(200));
    }

    @Test
    void testDateAnalysis() {
        NginxLogEntry entry = new NginxLogEntry("80.91.33.133 - - [17/May/2015:08:05:35 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"");
        entry.setTimeLocal("17/May/2015:08:05:35 +0000");
        logInformation.logAdd(entry);

        LocalDateTime expectedDateTime = LocalDateTime.of(2015, 5, 17, 8, 5);
        assertEquals(1, logInformation.getLogCountPerMinet().get(expectedDateTime));
    }

    @Test
    void testGetListOfMostRequestedResources() {
        NginxLogEntry entry1 = new NginxLogEntry("80.91.33.133 - - [17/May/2015:08:05:35 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"");
        entry1.setRequest("GET /downloads/product_1 HTTP/1.1");
        logInformation.logAdd(entry1);

        NginxLogEntry entry2 = new NginxLogEntry("80.91.33.133 - - [17/May/2015:08:05:35 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"");
        entry2.setRequest("GET /downloads/product_2 HTTP/1.1");
        logInformation.logAdd(entry2);

        NginxLogEntry entry3 = new NginxLogEntry("80.91.33.133 - - [17/May/2015:08:05:35 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"");
        entry3.setRequest("GET /downloads/product_1 HTTP/1.1");
        logInformation.logAdd(entry3);

        List<Pair<String, Integer>> mostRequestedResources = logInformation.getListOfMostRequestedResources();
        assertEquals(2, mostRequestedResources.get(0).right());
        assertEquals("/downloads/product_1", mostRequestedResources.get(0).left());
    }

    @Test
    void testGetListOfMostPopularStatus() {
        NginxLogEntry entry1 = new NginxLogEntry("80.91.33.133 - - [17/May/2015:08:05:35 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"");
        entry1.setStatus(200);
        logInformation.logAdd(entry1);

        NginxLogEntry entry2 = new NginxLogEntry("80.91.33.133 - - [17/May/2015:08:05:35 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"");
        entry2.setStatus(404);
        logInformation.logAdd(entry2);

        NginxLogEntry entry3 = new NginxLogEntry("80.91.33.133 - - [17/May/2015:08:05:35 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"");
        entry3.setStatus(200);
        logInformation.logAdd(entry3);

        List<Pair<String, Integer>> mostPopularStatus = logInformation.getListOfMostPopularStatus();
        assertEquals(2, mostPopularStatus.get(0).right());
        assertEquals("200", mostPopularStatus.get(0).left());
    }
}
