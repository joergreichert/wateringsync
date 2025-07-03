package de.l.codefor.wateringsync.supabase.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class WateredAndAdopted {
    @JsonProperty("tree_id")
    public String treeId;
    public String watered;
}
