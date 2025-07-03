package de.l.codefor.wateringsync.mdgiesst;

import de.l.codefor.wateringsync.giessdenkiez.to.Tree;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.List;

@RegisterRestClient(configKey = "mg-trees-api")
public interface MagdeburgGiesstTreesRestClient {

    // &id=in.(%s)
    @GET
    List<Tree> getTreesLocations(@HeaderParam("apikey") String apiKey, @HeaderParam("authorization") String authoruation, @RestQuery String id);
}
