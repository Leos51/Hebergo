package training.afpa.cda24060.squatrbnb.utilitaires;

import org.slf4j.Logger;

public class LogUtils {

    public static void trace(Logger logger, String message) {
        logger.trace(message);
    }

    public static void trace(Logger logger, String message, Exception e) {
        logger.trace(message, e);
    }

    public static void trace(Logger logger, String format, Object... args) {
        if (args == null || args.length == 0) {
            logger.trace(format);
        } else {
            logger.trace(format, args);
        }
    }

    public static void debug(Logger logger, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    public static void debug(Logger logger, String message, Exception e) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, e);
        }
    }

    public static void debug(Logger logger, String format, Object... args) {
        if (logger.isDebugEnabled()) {
            if (args == null || args.length == 0) {
                logger.debug(format);
            } else {
                logger.debug(format, args);
            }
        }
    }

    public static void info(Logger logger, String message) {
        logger.info(message);
    }

    public static void info(Logger logger, String message, Exception e) {
        logger.info(message, e);
    }

    public static void info(Logger logger, String format, Object... args) {
        if (args == null || args.length == 0) {
            logger.info(format);
        } else {
            logger.info(format, args);
        }
    }

    public static void warn(Logger logger, String message) {
        logger.warn(message);
    }

    public static void warn(Logger logger, String message, Exception e) {
        logger.warn(message, e);
    }

    public static void warn(Logger logger, String format, Object... args) {
        if (args == null || args.length == 0) {
            logger.warn(format);
        } else {
            logger.warn(format, args);
        }
    }

    public static void error(Logger logger, String message) {
        logger.error(message);
    }

    public static void error(Logger logger, String message, Exception e) {
        logger.error(message, e);
    }

    public static void error(Logger logger, String format, Object... args) {
        if (args == null || args.length == 0) {
            logger.error(format);
        } else {
            logger.error(format, args);
        }
    }
}
