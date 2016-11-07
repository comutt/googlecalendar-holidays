package jp.comutt.holidays;

import java.time.LocalDateTime;

/**
 * Holiday
 */
public class Holiday {

    /** Start date */
    private final LocalDateTime startDate;
    /** End date */
    private final LocalDateTime endDate;
    /** Name */
    private final String name;

    /**
     * Constructor
     * @param startDate Start date
     * @param endDate End date
     * @param name Name
     */
    public Holiday(LocalDateTime startDate, LocalDateTime endDate, String name) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public String getName() {
        return name;
    }


}
