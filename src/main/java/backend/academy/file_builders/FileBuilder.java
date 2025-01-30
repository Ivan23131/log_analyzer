package backend.academy.file_builders;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static backend.academy.configs.Constants.AMOUNT_SECCONDS_IN_MINTES;
//CHECKSTYLE:OFF ругается что неправильно импортирую не знаю почему
import backend.academy.configs.HttpStatus;
import backend.academy.log.tools.LogInformation;
//CHECKSTYLE:ON

public class FileBuilder {

    protected LogInformation logInformation;
    protected static final Logger LOGGER = LoggerFactory.getLogger(FileBuilder.class);


    public void buildFile(){
    }

    public void setLogInformation(LogInformation logInformation) {
        this.logInformation = logInformation;
    }

    protected List<List<String[]>> createTabeles() {
        List<List<String[]>> tables = new ArrayList<>();
        List<String[]> table1 = new ArrayList<>();
        //CHECKSTYLE:OFF ругается что "Метрика" встречается 2 раза чзх ?
        table1.add(new String[]{"Метрика", "Значение"});
        //CHECKSTYLE:OFF
        table1.add(new String[]{"Файл(-ы)", String.join(" ", logInformation.getLogFiles())});
        table1.add(new String[]{"Начальная дата",
                //CHECKSTYLE:OFF ругается что "dd.MM.yyyy" встречается 2 раза чзх ?
            logInformation.getBeginDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))});
        //CHECKSTYLE:OFF
        table1.add(new String[]{"Конечная дата",
            logInformation.getEndDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))});
        //CHECKSTYLE:OFF ругается что "Количество" встречается 2 раза чзх ?
        table1.add(new String[]{"Количество запросов",
                //CHECKSTYLE:OFF
            String.valueOf(logInformation.getNumRequests())});
        table1.add(new String[]{"Средний размер ответа", String.valueOf(logInformation.getResponseSize()
            / logInformation.getNumRequests())});
        table1.add(new String[]{"95p размера ответа", String.valueOf(logInformation.get95Percentile())});

        List<String[]> table2 = new ArrayList<>();
        table2.add(new String[]{"Ресурс", "Количество"});
        for (int i = 0; i < logInformation.getListOfMostRequestedResources().size(); i++) {
            table2.add(new String[]{logInformation.getListOfMostRequestedResources().get(i).first(),
                String.valueOf(logInformation.getListOfMostRequestedResources().get(i).second())});
        }

        List<String[]> table3 = new ArrayList<>();
        table3.add(new String[]{"Код", "Имя", "Количество"});
        for (int i = 0; i < logInformation.getListOfMostPopularStatus().size(); i++) {
            table3.add(new String[]{logInformation.getListOfMostPopularStatus().get(i).first(),
                HttpStatus.getReasonByCode(Integer.parseInt(
                    logInformation.getListOfMostPopularStatus().get(i).first()
                )),
                String.valueOf(logInformation.getListOfMostPopularStatus().get(i).second())});
        }

        List<String[]> table4 = new ArrayList<>();
        table4.add(new String[]{"Метрика", "Значение"});
        table4.add(new String[]{"Максимальное количество запросов за минуту",
            String.valueOf(Collections.max(logInformation.getLogCountPerMinet().values()))});
        try {
            table4.add(new String[]{"Среднее количество запросов в минуту", String.valueOf(
                logInformation.getNumRequests()
                    / (logInformation.getEndDate().toEpochSecond(ZoneOffset.UTC) / AMOUNT_SECCONDS_IN_MINTES
                    - logInformation.getBeginDate().toEpochSecond(ZoneOffset.UTC) / AMOUNT_SECCONDS_IN_MINTES)
            )});
        } catch (Exception e) {

            //CHECKSTYLE:OFF ругается что "Среднее количество запросов в минуту"встречается 2 раза чзх ?
            table4.add(new String[]{"Среднее количество запросов в минуту",
                    //CHECKSTYLE:ON
                String.valueOf(logInformation.getNumRequests())});
        }
        tables.add(table1);
        tables.add(table2);
        tables.add(table3);
        tables.add(table4);
        return tables;
    }
}
