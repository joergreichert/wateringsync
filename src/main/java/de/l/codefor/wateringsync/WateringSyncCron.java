package de.l.codefor.wateringsync;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.l.codefor.wateringsync.bo.Watering;
import de.l.codefor.wateringsync.to.WateringTO;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.IOException;

@ApplicationScoped
public class WateringSyncCron {
    @Inject
    ObjectMapper objectMapper;

    @Inject
    @RestClient
    LeipzigGiesstRestClient restClient;

    @Scheduled(cron = "{cron.expr}")
    void cronJobWithExpressionInConfig() {
        var watered = restClient.getLast30DaysWaterings().data.stream().filter(e -> !"0".equals(e.watered)).toList();
        System.out.println("Watered: " + watered.size());
        var result = Watering.findAll();
        try {
            var first = (Watering) result.list().getFirst();
            //var firstWatering = objectMapper.reader().readValue(first.properties, WateringTO.class);
            System.out.println("Waterings: " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(first));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
