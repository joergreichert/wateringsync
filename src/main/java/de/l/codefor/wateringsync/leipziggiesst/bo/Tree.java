package de.l.codefor.wateringsync.leipziggiesst.bo;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "trees")
public class Tree extends PanacheEntityBase {
    @Id
    public String id;
    public String lat;
    public String lng;
}
