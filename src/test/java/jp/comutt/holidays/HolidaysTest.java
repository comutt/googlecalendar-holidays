package jp.comutt.holidays;

import org.junit.Test;

public class HolidaysTest {

    private Holidays sut = new Holidays();

    @Test
    public void holidays() throws Exception {
        sut.holidays(2017);
    }

}