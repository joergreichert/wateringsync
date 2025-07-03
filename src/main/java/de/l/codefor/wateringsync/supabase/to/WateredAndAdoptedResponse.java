package de.l.codefor.wateringsync.supabase.to;
import java.util.List;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class WateredAndAdoptedResponse {
    public List<WateredAndAdopted> data;
}
