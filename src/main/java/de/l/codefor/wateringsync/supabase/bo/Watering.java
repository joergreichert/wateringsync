package de.l.codefor.wateringsync.supabase.bo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometryDeserializer;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometrySerializer;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Entity
@Table(name = "waterings")
@Cacheable
public class Watering extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WateringsSeqId")
    @SequenceGenerator(name = "WateringsSeqId", sequenceName = "waterings_SEQ")
    public Long id;

    @Type(JsonType.class)
    @Column(name = "properties", columnDefinition = "jsonb")
    public String properties;

    public LocalDateTime created;

    @Column(columnDefinition = "geometry(Point,4326)")
    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    public Point geom;
}
