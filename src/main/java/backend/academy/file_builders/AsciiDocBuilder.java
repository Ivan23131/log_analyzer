package backend.academy.file_builders;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class AsciiDocBuilder extends FileBuilder {

    @Override
    public void buildFile() {
        String fileName = "report.adoc";
        String adocContent = generateAsciiDocContent();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(adocContent);
        } catch (IOException e) {
            LOGGER.error("problem with writing", e);
        }
    }

    private String generateAsciiDocContent() {
        List<List<String[]>> tables = createTabeles();
        StringBuilder adocFileText = new StringBuilder();
        adocFileText.append("== Общая информация\n");
        adocFileText.append(createTableAdoc(tables.get(0)));
        adocFileText.append("== Запрашиваемые ресурсы\n");
        adocFileText.append(createTableAdoc(tables.get(1)));
        adocFileText.append("== Коды ответов\n");
        adocFileText.append(createTableAdoc(tables.get(2)));
        adocFileText.append("== Дополнительная статистика\n");
        //CHECKSTYLE:OFF таблица с дополнительное стастикой 3 - чекстайл ругается на маджикнабер
        adocFileText.append(createTableAdoc(tables.get(3)));
        //CHECKSTYLE:ON
        return adocFileText.toString();
    }

    private StringBuilder createTableAdoc(List<String[]> rows) {
        int[] maxLengths = new int[rows.get(0).length];

        // Вычисляем максимальную длину для каждого столбца
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                maxLengths[i] = Math.max(maxLengths[i], row[i].length());
            }
        }

        StringBuilder adocFileText = new StringBuilder();
        adocFileText.append("[options=\"header\"]\n");
        //CHECKSTYLE:OFF ругается что |=== встречается 2 раза чзх ?
        adocFileText.append("|===\n");
        //CHECKSTYLE:ON

        for (String[] row : rows) {
            for (int j = 0; j < row.length; j++) {
                adocFileText.append("| ").append(row[j]);
            }
            adocFileText.append("\n");
        }

        adocFileText.append("|===\n");
        return adocFileText;
    }
}
