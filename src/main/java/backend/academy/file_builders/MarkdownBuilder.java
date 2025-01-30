package backend.academy.file_builders;

import backend.academy.log.tools.LogInformation;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MarkdownBuilder extends FileBuilder {

    private LogInformation logInformation;

    @Override
    public void buildFile() {
        String fileName = "report.md";
        String markdownContent = generateMarkdownContent();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(markdownContent);
        } catch (IOException e) {
            LOGGER.error("problem with writing", e);
        }
    }

    private String generateMarkdownContent() {
        List<List<String[]>> tables = createTabeles();
        StringBuilder mkFileText = new StringBuilder();
        mkFileText.append("#### Общая информация\n");
        mkFileText.append(createTabelMk(tables.get(0)));
        mkFileText.append("#### Запрашиваемые ресурсы\n");
        mkFileText.append(createTabelMk(tables.get(1)));
        mkFileText.append("#### Коды ответов\n");
        mkFileText.append(createTabelMk(tables.get(2)));
        mkFileText.append("#### Дополнительная статистика\n");
        //CHECKSTYLE:OFF таблица с дополнительное стастикой 3 - чекстайл ругается на маджикнабер
        mkFileText.append(createTabelMk(tables.get(3)));
        //CHECKSTYLE:ON
        return mkFileText.toString();
    }

    private StringBuilder createTabelMk(List<String[]> rows) {
        int[] maxLengths = new int[rows.get(0).length];

        // Вычисляем максимальную длину для каждого столбца
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                maxLengths[i] = Math.max(maxLengths[i], row[i].length());
            }
        }
        StringBuilder mkFileText = new StringBuilder();
        for (int i = 0; i < rows.size(); i++) {
            String[] row = rows.get(i);
            mkFileText.append("|");
            for (int j = 0; j < row.length; j++) {
                //CHECKSTYLE:OFF ругатеся что " %-" встречается 2 раза
                mkFileText.append(String.format(" %-" + maxLengths[j] + "s |", row[j]));
                //CHECKSTYLE:ON
            }
            mkFileText.append("\n");

            // Добавляем разделительную строку после заголовка
            if (i == 0) {
                mkFileText.append("|");
                for (int j = 0; j < row.length; j++) {
                    mkFileText.append(String.format(" %-" + maxLengths[j] + "s |", "").replace(' ', '-'));
                }
                mkFileText.append("\n");
            }
        }
        return mkFileText;
    }
}
