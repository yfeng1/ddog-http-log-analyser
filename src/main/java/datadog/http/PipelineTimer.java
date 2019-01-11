package datadog.http;

import datadog.http.listener.LogListener;
import datadog.http.log.HttpLog;
import datadog.http.log.Parser;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PipelineTimer extends TailerListenerAdapter implements TimeListener{
    private final Map<LogListener, ExecutorService> listeners = new HashMap<>();

    public PipelineTimer() {
    }

    public void subscribe(LogListener listener) {
        // TODO: study multi-thread
        this.listeners.put(listener, Executors.newSingleThreadExecutor());
    }

    @Override
    public void handle(String line) {
        HttpLog httpMeta = Parser.parse(line);
        // The line cannot be parsed as HTTP log will be ignored.
        if (httpMeta != null) {
            listeners.forEach((key, value) -> value.submit(() -> key.handleLogLine(httpMeta)));
        }
    }

    @Override
    public void handleLogs() {
        listeners.forEach((key, value) -> value.submit(key::handleLogBloc));
    }

}
