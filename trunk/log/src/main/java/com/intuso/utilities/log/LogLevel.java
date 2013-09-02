package com.intuso.utilities.log;

/**
 * Created with IntelliJ IDEA.
 * User: ravnroot
 * Date: 07/10/12
 * Time: 20:26
 * To change this template use File | Settings | File Templates.
 */
/**
 * Different levels that log messages can be logged at
 * @author tclabon
 *
 */
public enum LogLevel {

    /**
     * debug information
     */
    DEBUG(0),

    /**
     * warning about something that might be wrong
     */
    WARN(1),

    /**
     * an error
     */
    ERROR(2),

    /**
     * a fatal error
     */
    FATAL(3),

    /**
     * Extra info - always printed
     */
    INFO(4);

    /**
     * The value of the level - used to compare the level a message was logged at to the filter level
     */
    private int level;

    /**
     * Create the level
     * @param level the value of the level
     */
    private LogLevel(int level) {
        this.level = level;
    }

    /**
     * Check if the given level is lower than this level
     * @param other the level to compare to
     * @return true if this level is strictly lower than the other one
     */
    public final boolean isLowerThan(LogLevel other) {
        return other.level < level;
    }

    /**
     * Check whether the given message should be logged if this instance is the filter
     * @param message_level the level of the log message
     * @return true if the message should be logged, false if it should be discarded
     */
    public boolean shouldLog(LogLevel message_level) {
        return message_level.level >= level;
    }
}
