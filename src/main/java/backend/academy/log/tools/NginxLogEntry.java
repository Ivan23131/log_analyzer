package backend.academy.log.tools;

import backend.academy.configs.LogParseException;
import java.util.regex.Matcher;
import static backend.academy.configs.Constants.LOG_PATTERN;

public class NginxLogEntry {
    private String remoteAddr;
    private String remoteUser;
    private String timeLocal;
    private String request;
    private int status;
    private int bodyBytesSent;
    private String httpReferer;
    private String httpUserAgent;

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    // чейкстайл ругается на количество аргументов у функции ЧЗХ????
    // CHECKSTYLE:OFF
    public NginxLogEntry(
        String remoteAddr,
        String remoteUser,
        String timeLocal,
        String request,
        int status,
        int bodyBytesSent,
        String httpReferer,
        String httpUserAgent
    ) {
        this.remoteAddr = remoteAddr;
        this.remoteUser = remoteUser;
        this.timeLocal = timeLocal;
        this.request = request;
        this.status = status;
        this.bodyBytesSent = bodyBytesSent;
        this.httpReferer = httpReferer;
        this.httpUserAgent = httpUserAgent;
    }
    // CHECKSTYLE:ON

    public NginxLogEntry(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (!matcher.find()) {
            throw new LogParseException("Invalid log format: " + line);
        }
        //CHECKSTYLE:OFF ругается на магические числа но эти индексы отвечают за положения информации в Nginx логгире
        setRemoteAddr(matcher.group(1));
        setRemoteUser(matcher.group(2));
        setTimeLocal(matcher.group(4));
        setRequest(matcher.group(5));
        setStatus(Integer.parseInt(matcher.group(6)));
        setBodyBytesSent(Integer.parseInt(matcher.group(7)));
        setHttpReferer(matcher.group(8));
        setHttpUserAgent(matcher.group(9));
        //CHECKSTYLE:ON
    }


    public String getRemoteUser() {
        return remoteUser;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    public String getTimeLocal() {
        return timeLocal;
    }

    public void setTimeLocal(String timeLocal) {
        this.timeLocal = timeLocal;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBodyBytesSent() {
        return bodyBytesSent;
    }

    public void setBodyBytesSent(int bodyBytesSent) {
        this.bodyBytesSent = bodyBytesSent;
    }

    public String getHttpReferer() {
        return httpReferer;
    }

    public void setHttpReferer(String httpReferer) {
        this.httpReferer = httpReferer;
    }

    public String getHttpUserAgent() {
        return httpUserAgent;
    }

    public void setHttpUserAgent(String httpUserAgent) {
        this.httpUserAgent = httpUserAgent;
    }

    @Override
    public String toString() {
        return "NginxLogEntry{"
            + "remoteAddr='" + remoteAddr + '\''
            + ", remoteUser='" + remoteUser + '\''
            + ", timeLocal='" + timeLocal + '\''
            + ", request='" + request + '\''
            + ", status=" + status
            + ", bodyBytesSent=" + bodyBytesSent
            + ", httpReferer='" + httpReferer + '\''
            + ", httpUserAgent='" + httpUserAgent + '\''
            + '}';
    }
}
