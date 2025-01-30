package backend.academy.log.tools;

import backend.academy.file_builders.FileBuilder;
import backend.academy.file_builders.FileFactory;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static backend.academy.configs.Constants.DATE_KEY_FORMATE;
import static backend.academy.configs.Constants.DATE_LOG_FORMATE;

public class LogController {
    private LogInformation logInformation;
    private LogConditionsChecker logConditionsChecker = new LogConditionsChecker();
    private PrintStream output;
    private String[] sources;
    //если тип файла не укажут то будем делать markdown
    //CHECKSTYLE:OFF ругается что в файле есть 2 "markdown" чзх?
    private String typeFileToBuild = "markdown";
    //CHECKSTYLE:ON

    public void start(String[] args, PrintStream output) {
        analysisArgs(args);
        logInformation = NginxLogParser.parseLogFile(sources, output, logConditionsChecker);
        FileBuilder fileBuilder = FileFactory.createFileBuilder(typeFileToBuild);
        fileBuilder.setLogInformation(logInformation);
        fileBuilder.buildFile();
    }

    private void analysisArgs(String[] args) {
        // тип массив строк - т.к ключ аргумента у условного path может быть не один файл или URL а множество
        // , поэтому надо хранить именно массив аргументов
        String[] valueOfKey;
        valueOfKey = getKeyValue("--from", args);
        if (valueOfKey.length != 0) {
            logConditionsChecker.setFrom(LocalDate.parse(valueOfKey[0], DATE_KEY_FORMATE));
        }
        valueOfKey = getKeyValue("--to", args);
        if (valueOfKey.length != 0) {
            logConditionsChecker.setTo(LocalDate.parse(valueOfKey[0], DATE_LOG_FORMATE));
        }
        valueOfKey = getKeyValue("--path", args);
        if (valueOfKey.length == 0) {
            throw new RuntimeException("path is not given");
        }
        sources = valueOfKey;
        valueOfKey = getKeyValue("--format", args);
        if (valueOfKey.length != 0) {
            if (valueOfKey[0].equals("markdown") || valueOfKey[0].equals("adoc")) {
                typeFileToBuild = valueOfKey[0];
            } else {
                throw new RuntimeException("format is not correct");
            }
        }
        valueOfKey = getKeyValue("--filter-field", args);
        if (valueOfKey.length != 0) {
            logConditionsChecker.setFilterField(valueOfKey[0]);
        }
        valueOfKey = getKeyValue("--filter-value", args);
        if (valueOfKey.length != 0) {
            logConditionsChecker.setFilterValue(valueOfKey[0]);
        }
        //проверяем что если указано --filter-field или --filter-value то указаны 2 параметра
        if (logConditionsChecker.getFilterField() != null ^ logConditionsChecker.getFilterValue() != null) {
            throw new RuntimeException("--filter-field or --filter-value is not given");
        }
    }

    private String[] getKeyValue(String key, String[] args) {
        List<String> result = new ArrayList<>();
        boolean foundKeyValue = false;

        for (String element : args) {
            if (foundKeyValue) {
                if (element.startsWith("-")) {
                    break;
                }
                result.add(element);
            } else if (element.equals(key)) {
                foundKeyValue = true;
            }
        }
        return result.toArray(new String[0]);
    }
}
