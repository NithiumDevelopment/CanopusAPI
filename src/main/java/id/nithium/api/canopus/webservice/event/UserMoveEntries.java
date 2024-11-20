package id.nithium.api.canopus.webservice.event;

import id.nithium.api.canopus.webservice.CanopusAPI;
import id.nithium.api.canopus.webservice.model.UserMover;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Value
public class UserMoveEntries {

    CanopusAPI api = CanopusAPI.getApi();
    List<UserMover> entries = new ArrayList<>();

    public UserMover getEntry(UUID uuid) {
        for (UserMover userMover : getEntries()) {
            if (userMover.getUuid().toString().equals(uuid.toString())) {
                return userMover;
            }
        } return null;
    }

    public void update() {
        api.getNithiumAPI().PUT(api.getDataType(), "usermoverentries", api.getAPI_KEY(), this);
    }
}
