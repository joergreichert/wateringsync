package de.l.codefor.wateringsync.supabase.to;

import java.time.LocalDateTime;

public class WateringTO {
    public String name;
    public Integer liter;
    public LocalDateTime date;
    public WaterType watertype;
    public Float longitude;
    public Float latitude;
}
