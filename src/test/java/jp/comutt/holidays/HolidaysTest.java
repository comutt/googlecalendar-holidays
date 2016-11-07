package jp.comutt.holidays;

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

public class HolidaysTest {

    private Properties credentialProperties = new Properties();

    @Before
    public void setUp() throws Exception {
        credentialProperties.load(getClass().getResourceAsStream("/credential.properties"));
    }

    @Test
    public void holidays() throws Exception {

        Holidays.create()
                .calendarId("japanese__ja@holiday.calendar.google.com")
                .apiKey(credentialProperties.getProperty("apiKey"))
                .holidays(2017).forEach(holiday -> {
                    System.out.println(holiday.getStartDate() + " " + holiday.getName());
                });
    }

}