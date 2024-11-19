package id.nithium.api.canopus.webservice.model;

import lombok.Data;

@Data
public class Status {
    private StatusType statusType;
    private boolean locked;

    public enum StatusType {
        UP,
        DOWN;
    }
}
