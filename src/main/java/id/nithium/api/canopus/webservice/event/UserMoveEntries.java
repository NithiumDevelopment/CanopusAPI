package id.nithium.api.canopus.webservice.event;

import id.nithium.api.canopus.webservice.CanopusAPI;
import id.nithium.api.canopus.webservice.model.UserMover;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class UserMoveEntries {

    CanopusAPI api = CanopusAPI.getApi();
    List<UserMover> entries = new ArrayList<>();

    public void update() {
        api.getNithiumAPI().PUT(api.getDataType(), "usermoverentries", api.getAPI_KEY(), this);
    }
}
