package de.l.codefor.wateringsync.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class WateredAndAdopted {
    @JsonProperty("tree_id")
    public String treeId;
    public String watered;
}
