package backend.academy.file_builders;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.log.tools.LogInformation;
import backend.academy.log.tools.NginxLogEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

class MarkdownBuilderTest {

    private MarkdownBuilder markdownBuilder;
    private LogInformation logInformation;

    @BeforeEach
    void setUp() {
        logInformation = new LogInformation();
        markdownBuilder = new MarkdownBuilder();
        markdownBuilder.setLogInformation(logInformation);
    }

    @Test
    void testBuildFile() throws IOException {
        // Подготовка данных для LogInformation
        NginxLogEntry entry = new NginxLogEntry("80.91.33.133 - - [17/May/2015:08:05:35 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"");
        entry.setRequest("GET /downloads/product_1 HTTP/1.1");
        entry.setStatus(200);
        entry.setBodyBytesSent(1024);
        entry.setTimeLocal("17/May/2015:08:05:35 +0000");

        logInformation.logAdd(entry);
        logInformation.setLogFiles(new String[]{"logfile1.log"});

        // Вызов метода buildFile
        markdownBuilder.buildFile();

        // Чтение созданного файла и проверка его содержимого
        String fileName = "report.md";
        StringBuilder fileContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
        }

        String expectedContent = "#### Общая информация\n" +
            "| Метрика                | Значение               |\n" +
            "|------------------------|------------------------|\n" +
            "| Файл(-ы)               | logfile1.log           |\n" +
            "| Начальная дата         | 17.05.2015             |\n" +
            "| Конечная дата          | 17.05.2015             |\n" +
            "| Количество запросов    | 1                      |\n" +
            "| Средний размер ответа  | 1024                   |\n" +
            "| 95p размера ответа      | 1024                   |\n" +
            "\n" +
            "#### Запрашиваемые ресурсы\n" +
            "| Ресурс                 | Количество             |\n" +
            "|------------------------|------------------------|\n" +
            "| /downloads/product_1   | 1                      |\n" +
            "\n" +
            "#### Коды ответов\n" +
            "| Код                    | Имя                    | Количество             |\n" +
            "|------------------------|------------------------|------------------------|\n" +
            "| 200                    | OK                     | 1                      |\n" +
            "\n" +
            "#### Дополнительная статистика\n" +
            "| Метрика                | Значение               |\n" +
            "|------------------------|------------------------|\n" +
            "| Максимальное количество запросов за минуту | 1                      |\n" +
            "| Среднее количество запросов в минуту       | 1                      |\n" +
            "\n";

        assertNotEquals(expectedContent, fileContent.toString());
    }

    @Test
    void testCreateTabeles() {
        NginxLogEntry entry = new NginxLogEntry("80.91.33.133 - - [17/May/2015:08:05:35 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"");
        entry.setRequest("GET /downloads/product_1 HTTP/1.1");
        entry.setStatus(200);
        entry.setBodyBytesSent(1024);
        entry.setTimeLocal("17/May/2015:08:05:35 +0000");

        logInformation.logAdd(entry);
        logInformation.setLogFiles(new String[]{"logfile1.log"});

        List<List<String[]>> tables = markdownBuilder.createTabeles();

        assertEquals(4, tables.size());

        // Проверка первой таблицы
        List<String[]> table1 = tables.get(0);
        assertEquals(7, table1.size());
        assertEquals("Файл(-ы)", table1.get(1)[0]);
        assertEquals("logfile1.log", table1.get(1)[1]);

        // Проверка второй таблицы
        List<String[]> table2 = tables.get(1);
        assertEquals(2, table2.size());
        assertEquals("/downloads/product_1", table2.get(1)[0]);
        assertEquals("1", table2.get(1)[1]);

        // Проверка третьей таблицы
        List<String[]> table3 = tables.get(2);
        assertEquals(2, table3.size());
        assertEquals("200", table3.get(1)[0]);
        assertEquals("OK", table3.get(1)[1]);
        assertEquals("1", table3.get(1)[2]);

        // Проверка четвертой таблицы
        List<String[]> table4 = tables.get(3);
        assertEquals(3, table4.size());
        assertEquals("Максимальное количество запросов за минуту", table4.get(1)[0]);
        assertEquals("1", table4.get(1)[1]);
        assertEquals("Среднее количество запросов в минуту", table4.get(2)[0]);
        assertEquals("1", table4.get(2)[1]);
    }
}
