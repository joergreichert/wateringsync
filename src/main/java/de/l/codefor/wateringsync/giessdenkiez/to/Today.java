package de.l.codefor.wateringsync.giessdenkiez.to;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Today {
    @JsonProperty("tree_id")
    public String treeId;
    @JsonProperty("total_amount")
    public Integer totalAmount;
}
