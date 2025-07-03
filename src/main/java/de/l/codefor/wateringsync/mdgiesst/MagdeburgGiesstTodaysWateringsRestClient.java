package de.l.codefor.wateringsync.mdgiesst;

import de.l.codefor.wateringsync.giessdenkiez.to.Today;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "mg-watered-today-api")
public interface MagdeburgGiesstTodaysWateringsRestClient {

    @GET
    List<Today> getTodaysWaterings(@HeaderParam("apikey") String apiKey, @HeaderParam("authorization") String authoruation);
}
