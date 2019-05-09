package br.com.uppersystems.uptrace.trace;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Data class that represents the Filter details.
 *
 * @author Filipe Germano
 *
 */
@Data
public class FilterDetail {

    private String name;

    private long timeInMillisRun;

    private long timeInMillisShould;

    private String status;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private long totalTimeInMillis;


    /**
     * Returns the total time in milliseconds.
     *
     * @return Time in milliseconds
     */
    public long getTotalTimeInMillis() {

        return timeInMillisRun + timeInMillisShould;
    }
}
