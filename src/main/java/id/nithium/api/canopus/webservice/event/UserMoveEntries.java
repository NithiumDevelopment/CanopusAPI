package id.nithium.api.canopus.webservice.event;

import id.nithium.api.canopus.webservice.CanopusAPI;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class UserMoveEntries {

    CanopusAPI api = CanopusAPI.getApi();
    List<UserMoveEvent> entries = new ArrayList<>();

    public void update() {
        api.getNithiumAPI().PUT(api.getDataType(), "usermoverentries", api.getAPI_KEY(), this);
    }
}
