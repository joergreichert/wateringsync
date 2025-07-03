package de.l.codefor.wateringsync.mdgiesst;

import de.l.codefor.wateringsync.giessdenkiez.to.GdkWatering;
import de.l.codefor.wateringsync.giessdenkiez.to.TreeRequest;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "mg-waterings-for-tree-api")
public interface MagdeburgGiesstTreesWateringsClient {

    @POST
    List<GdkWatering> getWaterings(@HeaderParam("apikey") String apiKey, @HeaderParam("authorization") String authorization, TreeRequest request);
}
