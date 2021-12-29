package com.icurety.bigbang;

import org.bukkit.Location;
import org.bukkit.World;

public class Explosion {

    private Location location;
    private boolean igniteFire = true;

    public Explosion(Location location) {
        this.location = location;
    }

    public boolean isIgniteFire() {
        return igniteFire;
    }

    public void setIgniteFire(boolean igniteFire) {
        this.igniteFire = igniteFire;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
