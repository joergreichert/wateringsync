package de.l.codefor.wateringsync.supabase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.l.codefor.wateringsync.leipziggiesst.bo.TreeWatered;
import de.l.codefor.wateringsync.supabase.bo.Watering;
import de.l.codefor.wateringsync.giessdenkiez.GiessDenKiezTodaysWateringsRestClient;
import de.l.codefor.wateringsync.giessdenkiez.GiessDenKiezTreesRestClient;
import de.l.codefor.wateringsync.giessdenkiez.GiessDenKiezTreesWateringsClient;
import de.l.codefor.wateringsync.giessdenkiez.to.TreeRequest;
import de.l.codefor.wateringsync.leipziggiesst.LeipzigGiesstRestClient;
import de.l.codefor.wateringsync.mdgiesst.MagdeburgGiesstTodaysWateringsRestClient;
import de.l.codefor.wateringsync.mdgiesst.MagdeburgGiesstTreesRestClient;
import de.l.codefor.wateringsync.mdgiesst.MagdeburgGiesstTreesWateringsClient;
import de.l.codefor.wateringsync.supabase.to.WaterType;
import de.l.codefor.wateringsync.supabase.to.WateringTO;
import io.quarkus.panache.common.Parameters;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

@ApplicationScoped
public class WateringSyncCron {
    private static final Logger LOGGER = LoggerFactory.getLogger(WateringSyncCron.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    @RestClient
    LeipzigGiesstRestClient leipzigGiesstRestClient;

    @Inject
    @RestClient
    GiessDenKiezTodaysWateringsRestClient giessDenKiezTodaysWateringsRestClient;

    @Inject
    @RestClient
    GiessDenKiezTreesRestClient giessDenKiezTreesRestClient;

    @Inject
    @RestClient
    GiessDenKiezTreesWateringsClient giessDenKiezTreesWateringsClient;

    @Inject
    @RestClient
    MagdeburgGiesstTodaysWateringsRestClient magdeburgGiesstTodaysWateringsRestClient;

    @Inject
    @RestClient
    MagdeburgGiesstTreesRestClient magdeburgGiesstTreesRestClient;

    @Inject
    @RestClient
    MagdeburgGiesstTreesWateringsClient magdeburgGiesstTreesWateringsClient;

    @Scheduled(cron = "{cron.expr}")
    void cronJobWithExpressionInConfig() {
        LOGGER.info("Zeitzone zuvor {}", TimeZone.getDefault().getDisplayName());
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));
        LOGGER.info("Zeitzone danach {}", TimeZone.getDefault().getDisplayName());
        handleLeipzigGiesstWaterings();
        handleGiessDenKiezWaterings();
        handleMagdeburgGiesstWaterings();
    }

    private LocalDateTime getToday() {
        return LocalDateTime.of(2025, 7, 4, 0, 0, 0);
    }

    private void handleGiessDenKiezWaterings() {
        String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imllb2t4YnF2cWVkcGN5dndtcnNiIiwicm9sZSI6ImFub24iLCJpYXQiOjE2Njc5OTkxMTQsImV4cCI6MTk4MzU3NTExNH0.ESC1pG1DvzxN8e6rmS9jdEnbuffwMIAIhGZ3g7sOhyQ";
        String bearerToken = "Bearer " + apiKey;
        var todays = giessDenKiezTodaysWateringsRestClient.getTodaysWaterings(apiKey, bearerToken);
        LOGGER.info("GiessDenKiez Watered: {}", todays.size());
        var todayDate = getToday();
        var tomorrow = todayDate.plusDays(1);
        for (var today : todays) {
            var id = "in.(" + today.treeId + ")";
            var location = giessDenKiezTreesRestClient.getTreesLocations(apiKey, bearerToken, id);
            var treeRequest = new TreeRequest();
            treeRequest.treeId = today.treeId;
            var waterings = giessDenKiezTreesWateringsClient.getWaterings(apiKey, bearerToken, treeRequest);
            for (var watering : waterings) {
                var timestamp = watering.timestamp.toLocalDateTime();
                if (timestamp.isEqual(todayDate) || (timestamp.isAfter(todayDate) && timestamp.isBefore(tomorrow))) {
                    Watering tdgWatering = new Watering();
                    tdgWatering.created = LocalDateTime.now();
                    WateringTO tdgWateringTO = new WateringTO();
                    tdgWateringTO.date = tdgWatering.created;
                    tdgWateringTO.latitude = location.getLast().lng; // needs to be switched, as GdK has it still wrong
                    tdgWateringTO.longitude = location.getLast().lat;
                    tdgWateringTO.name = "GiessDenKiez-WateringId-" + watering.treeId + "__" + watering.id;
                    tdgWateringTO.liter = watering.amount;
                    tdgWateringTO.watertype = WaterType.NOT_SPECIFIED;
                    fillEntity(tdgWateringTO, tdgWatering, treeRequest.treeId);
                }
            }
        }
    }

    @Transactional
    public void fillEntity(WateringTO tdgWateringTO, Watering tdgWatering, String treeId) {
        if (tdgWateringTO.longitude != null && tdgWateringTO.latitude != null) {
            GeometryFactory geometryFactory = new GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326);
            Coordinate coordinate = new Coordinate(tdgWateringTO.longitude, tdgWateringTO.latitude);
            tdgWatering.geom = geometryFactory.createPoint(coordinate);
            try {
                tdgWatering.properties = objectMapper.writeValueAsString(tdgWateringTO);
                var query = Watering.getEntityManager().createNativeQuery("select w from waterings w WHERE w.properties->> 'name' = '" + tdgWateringTO.name + "'");
                int size = query.getResultList().size();
                if (size == 0) {
                    tdgWatering.persistAndFlush();
                    try {
                        LOGGER.info("Derived TdG Watering: {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tdgWatering));
                    } catch (JsonProcessingException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            } catch (JsonProcessingException e) {
                LOGGER.error(e.getMessage(), e);
            }
        } else {
            LOGGER.warn("No lat or lon found for tree {}. Skipping.", treeId);
        }
    }

    private void handleMagdeburgGiesstWaterings() {
        String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InR4dXB3cWlydHd3cWpsZWNlc2hxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyNDA1MzksImV4cCI6MjA1NTgxNjUzOX0.Cc_TFsvs7-J5GOk2tnuU_osDu5PJrz4rDMRAISRzp0c";
        String bearerToken = "Bearer " + apiKey;
        var todays = magdeburgGiesstTodaysWateringsRestClient.getTodaysWaterings(apiKey, bearerToken);
        LOGGER.info("MagdeburgGiesst Watered: {}", todays.size());
        var todayDate = getToday();
        for (var today : todays) {
            var id = "in.(" + today.treeId + ")";
            var location = magdeburgGiesstTreesRestClient.getTreesLocations(apiKey, bearerToken, id);
            var treeRequest = new TreeRequest();
            treeRequest.treeId = today.treeId;
            var waterings = magdeburgGiesstTreesWateringsClient.getWaterings(apiKey, bearerToken, treeRequest);
            for (var watering : waterings) {
                var timestamp = watering.timestamp.toLocalDateTime();
                if (timestamp.isEqual(todayDate) || timestamp.isAfter(todayDate)) {
                    Watering tdgWatering = new Watering();
                    tdgWatering.created = LocalDateTime.now();
                    WateringTO tdgWateringTO = new WateringTO();
                    tdgWateringTO.date = tdgWatering.created;
                    tdgWateringTO.latitude = location.getLast().lat;
                    tdgWateringTO.longitude = location.getLast().lng;
                    tdgWateringTO.name = "MagdeburgGiesst-WateringId-" + watering.treeId + "__" + watering.id;
                    tdgWateringTO.liter = watering.amount;
                    tdgWateringTO.watertype = WaterType.NOT_SPECIFIED;
                    fillEntity(tdgWateringTO, tdgWatering, treeRequest.treeId);
                }
            }
        }
    }

    private void handleLeipzigGiesstWaterings() {
        var today = getToday();
        List<TreeWatered> treesWatered = TreeWatered.list("SELECT tw FROM TreeWatered tw WHERE tw.timestamp >= :date", Parameters.with("date", today)).stream().map(tw -> (TreeWatered) tw).toList();
        LOGGER.info("LeipzigGiesst Watered: {}", treesWatered.size());
        for (var treeWatered : treesWatered) {
            Watering tdgWatering = new Watering();
            tdgWatering.created = treeWatered.timestamp;
            WateringTO tdgWateringTO = new WateringTO();
            tdgWateringTO.date = tdgWatering.created;
            tdgWateringTO.latitude = Float.parseFloat(treeWatered.tree.lat);
            tdgWateringTO.longitude = Float.parseFloat(treeWatered.tree.lng);
            tdgWateringTO.name = "LeipzigGiesst-WateringId-" + treeWatered.tree.id + "__" + treeWatered.wateringId;
            tdgWateringTO.liter = Integer.parseInt(treeWatered.amount);
            tdgWateringTO.watertype = WaterType.NOT_SPECIFIED;
            fillEntity(tdgWateringTO, tdgWatering, treeWatered.tree.id);
        }
    }
}
