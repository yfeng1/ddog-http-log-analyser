package datadog.http.listener;

import datadog.http.log.HttpLog;

public interface LogListener {
    // This will be called for each new line
    void handleLogLine(HttpLog httpLog);
    // This will be called for each log fragment for a defined period
    void handleLogBloc();
}
