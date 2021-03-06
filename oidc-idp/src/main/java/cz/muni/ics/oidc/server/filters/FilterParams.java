package cz.muni.ics.oidc.server.filters;

import cz.muni.ics.oidc.models.Facility;
import cz.muni.ics.oidc.models.PerunUser;
import org.mitre.oauth2.model.ClientDetailsEntity;

public class FilterParams {

    private final ClientDetailsEntity client;
    private final Facility facility;
    private final PerunUser user;

    public FilterParams(ClientDetailsEntity client, Facility facility, PerunUser user) {
        this.client = client;
        this.facility = facility;
        this.user = user;
    }

    public ClientDetailsEntity getClient() {
        return client;
    }

    public Facility getFacility() {
        return facility;
    }

    public PerunUser getUser() {
        return user;
    }

    public String getClientIdentifier() {
        if (client != null) {
            return client.getClientId();
        }

        return null;
    }

}
