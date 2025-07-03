package de.l.codefor.wateringsync.bo;

import com.fasterxml.jackson.annotation.JsonRawValue;
import de.l.codefor.wateringsync.to.WateringConverterJson;
import de.l.codefor.wateringsync.to.WateringTO;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "waterings")
@Cacheable
public class Watering extends PanacheEntity {

    @Column(name = "properties", columnDefinition = "jsonb")
    @Convert(converter = WateringConverterJson.class)
    public WateringTO properties;

    public LocalDateTime created;

    //public org.postgresql.geometric.PGpoint geom;
}
