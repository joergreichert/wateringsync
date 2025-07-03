package de.l.codefor.wateringsync.giessdenkiez.to;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class GdkWatering {
    public Integer amount;
    public OffsetDateTime timestamp;
    public String username;
    public Long id;
    @JsonProperty("tree_id")
    public String treeId;
}
