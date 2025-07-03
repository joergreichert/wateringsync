package de.l.codefor.wateringsync.leipziggiesst;

import de.l.codefor.wateringsync.to.WateredAndAdoptedResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "lg-watering-api")
public interface LeipzigGiesstRestClient {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    WateredAndAdoptedResponse getLast30DaysWaterings();
}
