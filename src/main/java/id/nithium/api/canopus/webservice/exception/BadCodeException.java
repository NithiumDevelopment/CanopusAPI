package id.nithium.api.canopus.webservice.exception;

import id.nithium.api.exception.NithiumException;
import id.nithium.api.type.DataType;

public class BadCodeException extends NithiumException {

    public BadCodeException(DataType dataType, int code) {
        super("Unable to execute for status of " + dataType.getUrl() + " because return of " + code);
    }
}
