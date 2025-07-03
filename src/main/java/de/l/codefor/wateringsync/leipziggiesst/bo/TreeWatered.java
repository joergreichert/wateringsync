package de.l.codefor.wateringsync.leipziggiesst.bo;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trees_watered")
public class TreeWatered extends PanacheEntityBase {
    @ManyToOne
    @JoinColumn(name = "tree_id")
    public Tree tree;
    @Id
    @Column(name = "watering_id")
    public String wateringId;
    public String amount;
    public LocalDateTime timestamp;
}
