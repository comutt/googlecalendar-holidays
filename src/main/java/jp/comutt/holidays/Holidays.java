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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static jdk.nashorn.internal.codegen.OptimisticTypesPersistence.load;

public class Holidays {

    private static final String CALENDAR_URL = "https://www.googleapis.com/calendar/v3/calendars/%s/events";
    private static final String CALENDAR_ID = "japanese__ja@holiday.calendar.google.com";
    private static final ZoneId JST = ZoneId.of("Asia/Tokyo");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        new Holidays().run(args);
    }

    public void run(String[] args) {

    }


    public void holidays(int year) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        ZonedDateTime timeMin = LocalDateTime.of(year, 1, 1, 0, 0).atZone(JST);
        ZonedDateTime timeMax = timeMin.plusYears(1).minusSeconds(1);

        Properties credentialProperties = new Properties();
        credentialProperties.load(getClass().getResourceAsStream("/credential.properties"));

        URI uri = new URIBuilder(String.format(CALENDAR_URL, CALENDAR_ID))
                .addParameter("key", credentialProperties.getProperty("apiKey"))
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
            items.forEach(entry -> {
                Map<String, Object> start = (Map<String, Object>) entry.get("start");
                System.out.println(start.get("date") + " " + entry.get("summary"));
            });
        }
    }

}
