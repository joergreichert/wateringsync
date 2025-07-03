package de.l.codefor.wateringsync.giessdenkiez;

import de.l.codefor.wateringsync.giessdenkiez.to.Today;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "gdk-watered-today-api")
public interface GiessDenKiezTodaysWateringsRestClient {

    @GET
    List<Today> getTodaysWaterings(@HeaderParam("apikey") String apiKey, @HeaderParam("authorization") String authoruation);
}
