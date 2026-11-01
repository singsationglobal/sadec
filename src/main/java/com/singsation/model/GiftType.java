package com.singsation.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Central source of truth for all karaoke gift instruments.
 * The client can NEVER dictate a price — it only sends the giftType,
 * and the server looks up the cost here.
 *
 * Changing a price only requires editing this file and redeploying.
 */
public enum GiftType {

    MIC("Microphone", "🎤", 1),
    MUSIC_NOTE("Music Note", "🎵", 2),
    VIOLIN("Violin", "🎻", 3),
    TRUMPET("Trumpet", "🎺", 5),
    GUITAR("Guitar", "🎸", 10),
    PIANO("Piano", "🎹", 15),
    DRUMS("Drum Kit", "🥁", 25),
    SAXOPHONE("Saxophone", "🎷", 50),
    ORCHESTRA("Grand Orchestra", "🎼", 100),
    LEGEND("Legend Status", "👑", 500);

    private final String displayName;
    private final String icon;
    private final int coinCost;

    GiftType(String displayName, String icon, int coinCost) {
        this.displayName = displayName;
        this.icon = icon;
        this.coinCost = coinCost;
    }

    public String getDisplayName() { return displayName; }
    public String getIcon() { return icon; }
    public int getCoinCost() { return coinCost; }

    private static final Map<String, GiftType> LOOKUP = new HashMap<>();
    static {
        for (GiftType g : values()) LOOKUP.put(g.name(), g);
    }

    /**
     * Case-insensitive lookup by name. Returns null if unknown.
     */
    public static GiftType fromString(String name) {
        if (name == null) return null;
        return LOOKUP.get(name.trim().toUpperCase());
    }
}
