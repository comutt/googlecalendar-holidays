package jp.comutt.holidays;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Holidays {

    private static final String CALENDAR_URL = "https://www.googleapis.com/calendar/v3/calendars/%s/events";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private String calendarId;
    private String apiKey;

    private Holidays() {
    }

    public static Holidays create() {
        Holidays holidays = new Holidays();
        return holidays;
    }

    public Holidays calendarId(String calendarId) {
        this.calendarId = calendarId;
        return this;
    }

    public Holidays apiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public List<Holiday> holidays(int year) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        OffsetDateTime timeMin = LocalDateTime.of(year, 1, 1, 0, 0).atOffset(ZoneOffset.UTC);
        OffsetDateTime timeMax = timeMin.plusYears(1).minusSeconds(1);

        URI uri = new URIBuilder(String.format(CALENDAR_URL, calendarId))
                .addParameter("key", apiKey)
                .addParameter("timeMin", timeMin.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .addParameter("timeMax", timeMax.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .addParameter("orderBy", "startTime")
                .addParameter("singleEvents", "true")
                .build();

        HttpGet httpGet = new HttpGet(uri);
        try (CloseableHttpResponse response = client.execute(httpGet)) {
            InputStream content = response.getEntity().getContent();
            Type type = new TypeToken<Map<String, Object>>(){}.getType();

            Map<String, Object> jsonMap = GSON.fromJson(new InputStreamReader(content), type);
            List<Map<String, Object>> items = (List<Map<String, Object>>) jsonMap.get("items");
            return items.stream().map(this::toHoliday).collect(Collectors.toList());
        }
    }

    private Holiday toHoliday(Map<String, Object> entry) {
        Map<String, String> start = (Map<String, String>) entry.get("start");
        Map<String, String> end = (Map<String, String>) entry.get("end");

        return new Holiday(
                LocalDate.from(DateTimeFormatter.ISO_DATE.parse(start.get("date"))).atStartOfDay(),
                LocalDate.from(DateTimeFormatter.ISO_DATE.parse(end.get("date"))).atStartOfDay(),
                (String) entry.get("summary"));
    }

}
