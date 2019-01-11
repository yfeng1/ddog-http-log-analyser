package datadog.http.listener.borad;

import com.google.common.collect.Iterators;
import datadog.http.listener.LogListener;
import datadog.http.log.HttpLog;
import datadog.http.log.Parser;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardCollector implements LogListener {
    private ZonedDateTime startMonitoringTime;
    // TODO: why not linkedHashMap directly
    private Map<String, Long> hitsBySection = new HashMap<>();
    private Map<String, Long> hitsByUsers = new HashMap<>();
    private long lastRequest = 0;
    private long lastNotFound = 0;
    private long totalRequest = 0;
    private Set<String> totalUniqueVisitor = new HashSet<>();
    private long totalNotFound = 0;
    private long totalServerError = 0; // STATUS CODE 5XX

    /**
     * Always mono-thread, no need to lock
     */
    public DashboardCollector( ZonedDateTime startMonitoringTime) {
        this.startMonitoringTime = startMonitoringTime;
    }

    @Override
    public void handleLogLine(HttpLog httpMeta) {
        ZonedDateTime zonedDateTime = httpMeta.getZonedDateTime();
        long between = ChronoUnit.SECONDS.between(startMonitoringTime, zonedDateTime);
        if (between >= 0) {
            // Hit By section
            String resource = httpMeta.getResource();
            String[] split = resource.split("/");
            String section = split.length > 2 ? split[0] + "/" + split[1] : resource;
            if (hitsBySection.containsKey(section)) {
                hitsBySection.put(section, hitsBySection.get(section) + 1);
            } else {
                hitsBySection.put(section, 1L);
            }
            // Hit By users
            String authuser = httpMeta.getAuthuser();
            if (!"-".equals(authuser)) {
                // totalUniqueVisitor
                totalUniqueVisitor.add(authuser);
                if (hitsByUsers.containsKey(authuser)) {
                    hitsByUsers.put(authuser, hitsByUsers.get(authuser) + 1);
                } else {
                    hitsByUsers.put(authuser, 1L);
                }
            }
            // lastRequest
            lastRequest ++;
            // totalRequest
            totalRequest ++;
            // lastNotFound
            if (httpMeta.getStatus() == 404) {
                lastNotFound++;
                totalNotFound++;
            }
            if (httpMeta.getStatus() >= 500) {
                totalServerError++;
            }

        }
    }

    @Override
    public void handleLogBloc() {
        ZonedDateTime now = ZonedDateTime.now();
        LinkedHashMap<String, Long> sorted = hitsBySection
                .entrySet()
                .stream()
                .sorted((e1, e2)-> e2.getValue().compareTo(e1.getValue()))
                .collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        Iterator<Map.Entry<String, Long>> iterator = sorted.entrySet().iterator();
        String sections = "";
        if (iterator.hasNext()) {
            long max = Iterators.get(iterator, 0).getValue();
            sections = sorted.entrySet().stream().filter(e -> e.getValue() == max).map(Map.Entry::getKey).collect(Collectors.joining(", "));
        }

        LinkedHashMap<String, Long> hitsByUsersSorted = hitsByUsers
                .entrySet()
                .stream()
                .sorted((e1, e2)-> e2.getValue().compareTo(e1.getValue()))
                .collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        Iterator<Map.Entry<String, Long>> hitsByUsersIt = hitsByUsersSorted.entrySet().iterator();
        String users = "";
        if (hitsByUsersIt.hasNext()) {
            long max = Iterators.get(hitsByUsersIt, 0).getValue();
            users = hitsByUsersSorted.entrySet().stream().filter(e -> e.getValue() == max).map(Map.Entry::getKey).collect(Collectors.joining(", "));
        }

        //TODO : clean up this part.
        System.out.println( "**********************************************************" + System.lineSeparator() +
                "At " + now.format(Parser.DATE_TIME_FORMATTER) + System.lineSeparator() +
                "Most hit section(s) during last 10 seconds is(are): " + sections + System.lineSeparator() +
                        "User with most hit during last 10 seconds is(are): " + users + System.lineSeparator() +
                        "Number of requests during last 10 seconds is: " + lastRequest + System.lineSeparator() +
                        "Number of requests NOT FOUND during last 10 seconds is: " + lastNotFound + System.lineSeparator() +
                        "Number of total request is: " + totalRequest + System.lineSeparator() +
                        "Number of total unique user is: " + totalUniqueVisitor.size() + System.lineSeparator() +
                        "Number of total requests NOT FOUND is: " + totalNotFound + System.lineSeparator() +
                        "Number of total requests Server error is: " + totalServerError + System.lineSeparator() +
                "**********************************************************" );
        hitsBySection.clear();
        hitsByUsers.clear();
        lastRequest = 0;
        lastNotFound = 0;
    }

}
