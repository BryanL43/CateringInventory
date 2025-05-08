package org.fakeskymeal.dao.exception;

/**
 * DaoException
 *
 * Wrapper around any lower level Exceptions (i.e., SQLException).
 *
 * Modifications:
 *
 * 		04/20/2024 - jhui - Created
 */

public class DaoException extends Exception {

    public DaoException() {
    }

    public DaoException(String message) {
        super(message);
    }

    public DaoException(Throwable cause) {
        super(cause);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public DaoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
