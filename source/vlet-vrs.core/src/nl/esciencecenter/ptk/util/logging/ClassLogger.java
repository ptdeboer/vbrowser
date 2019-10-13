/*
 * Copyright 2012-2014 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the following location:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For the full license, see: LICENSE.txt (located in the root folder of this distribution).
 * ---
 */
// source:

package nl.esciencecenter.ptk.util.logging;

import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;

/**
 * SLF4J Logger facade.<br>
 * Refactored from Java Logger style ClassLogger. <br>
 * Still contains Java Logging Levels for backwards
 * compatibility reasons. All logging will be moved to slf4j.
 */
public class ClassLogger {

    // ==================
    // Class Fields
    // ==================

    public static final Level DEBUG = Level.FINE;

    public static final Level INFO = Level.INFO;

    public static final Level WARN = Level.WARNING;

    public static final Level ERROR = Level.SEVERE;

    public static final Level FATAL = Level.ALL;

    private static Map<String, ClassLogger> classLoggers = new Hashtable<>();

    private static ClassLogger rootLogger = null;

    // ==================
    // Class Methods
    // ==================

    public static synchronized ClassLogger getLogger(String name) {
        //
        synchronized (classLoggers) {
            ClassLogger logger = classLoggers.get(name);
            if (logger == null) {
                logger = new ClassLogger(name);
                classLoggers.put(name, logger);
            }
            return logger;
        }
    }

    public static ClassLogger getLogger(Class<?> clazz) {
        return getLogger(clazz.getCanonicalName());
    }

    static {
        rootLogger = new ClassLogger("VletRootLogger");
    }

    public static ClassLogger getRootLogger() {
        return rootLogger;
    }

    // ==================
    // Instance
    // ==================

    /** Redirect logging to slf4j */
    private org.slf4j.Logger logger;

    // redirect level not yet implemented
    private Level redirectLevel = null;

    protected ClassLogger(String name) {
        this.logger = LoggerFactory.getLogger(name);
    }

    // ==========================================================================================
    // Manipulations
    // ==========================================================================================

    public void setLevelToDebug() {
        this.redirectLevel = Level.FINE;
    }

    public void setLevelToInfo() {
        this.redirectLevel = Level.INFO;
    }

    public void setLevelToWarn() {
        this.redirectLevel = Level.WARNING;
    }

    public void setLevelToError() {
        this.redirectLevel = Level.SEVERE;
    }

    public void setLevelToFatal() {
        this.redirectLevel = Level.ALL;
    }

    public boolean isLevelDebug() {
        return logger.isDebugEnabled();
    }

    // ==========================================================================================
    // Logging
    // ==========================================================================================

    /**
     * Log using java.util.Loggin.Level levels
     */
    public void log(Level jLevel, String format, Object... args) {
        int level = jLevel.intValue();
        // Java Logger compatible logging levels:
        if (level <= Level.FINE.intValue())
            logger.debug(String.format(format, args));
        else if (level <= Level.INFO.intValue())
            logger.info(String.format(format, args));
        else if (level <= Level.WARNING.intValue())
            logger.warn(String.format(format, args));
        else if (level <= Level.SEVERE.intValue())
            logger.error(String.format(format, args));
        else
            logger.error(String.format(format, args));
    }

    public boolean isLoggable(Level jLevel) {
        // Java Logger compatible logging levels:
        int level = jLevel.intValue();
        return (((level <= Level.FINE.intValue()) && logger.isDebugEnabled())
                || ((level <= Level.INFO.intValue()) && logger.isInfoEnabled())
                || ((level <= Level.WARNING.intValue()) && logger.isWarnEnabled()) || ((level <= Level.SEVERE
                .intValue()) && logger.isErrorEnabled()));
    }

    public boolean hasEffectiveLevel(Level level) {
        return isLoggable(level);
    }

    public boolean hasDebugLevel() {
        return logger.isDebugEnabled();
    }

    // ==========================================================================================
    // Formatting logging methods
    // ==========================================================================================

    /**
     * Old printf() invocations add newlines. slf4j/log4j adds them according to the log pattern
     * conversion.
     */
    private String formatNoEndline(String format, Object[] args) {
        if ((format != null) && (format.endsWith("\n"))) {
            format = format.substring(0, format.length() - 1);
        }
        return String.format(format, args);
    }

    public void logException(Level level, Object source, Throwable e, String format, Object... args) {
        if (this.isLoggable(level) == false)
            return;

        log(level, "Exception:" + formatNoEndline(format, args));
    }

    public void logException(Level level, Throwable e, String format, Object... args) {
        if (this.isLoggable(level) == false)
            return;

        log(level, "Exception:" + formatNoEndline(format, args));
    }

    public void debugPrintf(String format, Object... args) {
        if (!logger.isDebugEnabled())
            return;
        logger.debug(formatNoEndline(format, args));
    }

    public void infoPrintf(String format, Object... args) {
        if (!logger.isInfoEnabled())
            return;
        logger.info(formatNoEndline(format, args));
    }

    public void warnPrintf(String format, Object... args) {
        if (!logger.isWarnEnabled())
            return;
        logger.debug(formatNoEndline(format, args));
    }

    public void errorPrintf(String format, Object... args) {
        if (!logger.isErrorEnabled())
            return;

        this.logger.debug(formatNoEndline(format, args));
    }

    // SLF4J Interface:

    public void fatal(String format, Object... args) {
        logger.error("FATAL:" + format, args);
    }

    public void error(String format, Object... args) {
        logger.error(format, args);
    }

    public void warn(String format, Object... args) {
        logger.warn(format, args);
    }

    public void info(String format, Object... args) {
        logger.info(format, args);
    }

    public void debug(String format, Object... args) {
        logger.debug(format, args);
    }

    public void trace(String format, Object... args) {
        logger.trace(format, args);
    }

}
