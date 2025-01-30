package backend.academy.log.tools;

import com.tdunning.math.stats.MergingDigest;
import com.tdunning.math.stats.TDigest;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static backend.academy.configs.Constants.AMOUNT_OF_POPULAR;
import static backend.academy.configs.Constants.DATE_LOG_FORMATE;
import static backend.academy.configs.Constants.MAX_PRECENTIL;
import static backend.academy.configs.Constants.PRECENTIL;
import static backend.academy.configs.Constants.PREC_BUFFER;

public class LogInformation {
    //храним ресурсы которы запршиваются и количество обращений соответственно
    private Map<String, Integer> requestedResources = new HashMap<>();
    private Map<Integer, Integer> statuses = new HashMap<>();
    TDigest tDigest = new MergingDigest(PREC_BUFFER);
    private int numRequests = 0;
    private long responseSize = 0;
    private Map<LocalDateTime, Integer> logCountPerMinet = new HashMap<>();
    private List<Pair<String, Integer>> mostRequestedResources = new ArrayList<>();
    private List<Pair<String, Integer>> mostPopularStatus = new ArrayList<>();
    private LocalDateTime beginDate = LocalDateTime.MAX;
    private LocalDateTime endDate = LocalDateTime.MIN;
    private String[] logFiles;

    public String[] getLogFiles() {
        return logFiles;
    }

    public LocalDateTime getBeginDate() {
        return beginDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public Map<String, Integer> getRequestedResources() {
        return requestedResources;
    }

    public Map<Integer, Integer> getStatuses() {
        return statuses;
    }

    public Map<LocalDateTime, Integer> getLogCountPerMinet() {
        return logCountPerMinet;
    }

    public int get95Percentile() {
        return (int) tDigest.quantile(PRECENTIL / MAX_PRECENTIL);
    }

    // функция которые берет именно 3 самых запрашиываемых ресурса отсюда и условия на < 3
    public List<Pair<String, Integer>> getListOfMostRequestedResources() {
        if (mostRequestedResources.isEmpty()) {
            getSomeItemsOfMore(AMOUNT_OF_POPULAR, requestedResources, mostRequestedResources);
        }
        return mostRequestedResources;
    }

    public List<Pair<String, Integer>> getListOfMostPopularStatus() {
        if (mostPopularStatus.isEmpty()) {
            getSomeItemsOfMore(AMOUNT_OF_POPULAR, statuses, mostPopularStatus);
        }
        return mostPopularStatus;
    }

    private <T> void getSomeItemsOfMore(int count, Map<T, Integer> map, List<Pair<String, Integer>> listOfItems) {
        List<Integer> valuesList = new ArrayList<>(map.values());
        Collections.sort(valuesList);
        int countOfSomething = 0;
        for (int i = valuesList.size() - 1; i >= 0; i--) {
            List<T> nameOfMostItems = getKeysByValue(map, valuesList.get(i));
            for (T resource : nameOfMostItems) {
                if (countOfSomething < count) {
                    Pair resourcePair = new ObjectObjectImmutablePair<>(String.valueOf(resource), map.get(resource));
                    if (!listOfItems.contains(resourcePair)) {
                        listOfItems.add(resourcePair);
                        countOfSomething++;
                    }
                } else {
                    break;
                }
            }
            if (countOfSomething >= count) {
                break;
            }
        }
    }

    public static <K, V> List<K> getKeysByValue(Map<K, V> map, V value) {
        List<K> keys = new ArrayList<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    public void logAdd(NginxLogEntry entry) {
        numRequests++;
        resourceAnalysis(entry);
        statusesAnalysis(entry);
        responseSize += entry.getBodyBytesSent();
        tDigest.add(entry.getBodyBytesSent());
        dateAnalysis(entry);
    }

    public int getNumRequests() {
        return numRequests;
    }

    public long getResponseSize() {
        return responseSize;
    }

    private void resourceAnalysis(NginxLogEntry entry) {
        if (!requestedResources.containsKey(entry.getRequest().split(" ")[1])) {
            requestedResources.put(entry.getRequest().split(" ")[1], 0);
        }
        requestedResources.put(entry.getRequest().split(" ")[1],
            requestedResources.get(entry.getRequest().split(" ")[1]) + 1);
    }

    private void statusesAnalysis(NginxLogEntry entry) {
        if (!statuses.containsKey(entry.getStatus())) {
            statuses.put(entry.getStatus(), 0);
        }
        statuses.put(entry.getStatus(), statuses.get(entry.getStatus()) + 1);
    }

    public void setLogFiles(String[] logFiles) {
        this.logFiles = logFiles;
    }

    private void dateAnalysis(NginxLogEntry entry) {
        // Обрезаем строку до нужного формата "dd/MMM/yyyy:HH:mm"
        String trimmedDateString = entry.getTimeLocal().substring(0,
            entry.getTimeLocal().indexOf(" ") - AMOUNT_OF_POPULAR);
        // Создаем DateTimeFormatter для разбора строки даты и времени
        LocalDateTime localDateTime = LocalDateTime.parse(trimmedDateString, DATE_LOG_FORMATE);
        if (!logCountPerMinet.containsKey(localDateTime)) {
            logCountPerMinet.put(localDateTime, 0);
        }
        logCountPerMinet.put(localDateTime, logCountPerMinet.get(localDateTime) + 1);
        if (endDate.compareTo(localDateTime) < 0) {
            endDate = localDateTime;
        }
        if (beginDate.compareTo(localDateTime) > 0) {
            beginDate = localDateTime;
        }
    }
}
