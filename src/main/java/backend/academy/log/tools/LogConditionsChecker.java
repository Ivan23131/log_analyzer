package backend.academy.log.tools;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LogConditionsChecker {
    private LocalDate from = LocalDate.MIN;
    private LocalDate to = LocalDate.MAX;
    private String filterField = new String("");
    private String filterValue = new String("");

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public String getFilterField() {
        return filterField;
    }

    public void setFilterField(String filterField) {
        this.filterField = filterField;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public boolean isLogSatisfyConditions(NginxLogEntry log) {
        // проверяем что лог находится в диапазоне заданном пользователем
        boolean res = true;
        LocalDate localDate = LocalDate.parse(log.getTimeLocal().substring(0, log.getTimeLocal().indexOf(":")),
            DateTimeFormatter.ofPattern("dd/MMM/yyyy", Locale.ENGLISH));
        if (!(localDate.isAfter(from) && localDate.isBefore(to))) {
            res = false;
        }
        if (filterField.compareTo("agent") == 0) {
            if (log.getHttpUserAgent().split(" ")[0].compareTo(filterValue) != 0) {
                res = false;
            }
        } else if (filterField.compareTo("method") == 0) {
            if (log.getRequest().split(" ")[0].compareTo(filterValue) != 0) {
                res = false;
            }
        } else if (filterField.compareTo("status") == 0) {
            if (log.getStatus() != Integer.parseInt(filterValue)) {
                res = false;
            }
        } else if (filterField.compareTo("addres") == 0) {
            if (log.getRemoteAddr().compareTo(filterValue) != 0) {
                res = false;
            }
        }
        return res;
    }

}
