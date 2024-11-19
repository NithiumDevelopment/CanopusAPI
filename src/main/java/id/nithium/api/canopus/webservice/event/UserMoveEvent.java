package id.nithium.api.canopus.webservice.event;

import id.nithium.api.event.Event;
import lombok.Data;

import java.util.UUID;

@Data
public class UserMoveEvent extends Event {

    private final UUID uuid;
    private final String from;
    private final String to;
}
