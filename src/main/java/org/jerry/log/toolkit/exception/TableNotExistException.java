package org.jerry.log.toolkit.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TableNotExistException extends RuntimeException{
    public TableNotExistException(String message) {
        super(message);
    }

    public TableNotExistException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
