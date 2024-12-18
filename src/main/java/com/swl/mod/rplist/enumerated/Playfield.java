package com.swl.mod.rplist.enumerated;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.HashMap;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Playfield {

    UNKNOWN("Unknown", 0, 100000),

    LONDON("London", 1000, 100),
    CRUSADES("London - Crusades", 7160, 105),
    TABULA_RASA("London - Tabula Rasa", 7080, 110),
    ALBION("London - Albion", 7070, 130),
    ALBION_REHEARSAL("London - Albion Rehearsal Stage", 7075, 135),
    MUSEUM("British Museum of the Occult", 7190, 150),
    LONDON_FIGHT_CLUB("London Fight Club", 7020, 160),

    NEW_YORK("New York", 1100, 200),
    NEW_YORK_FIGHT_CLUB("New York Fight Club", 7230, 210),

    SEOUL("Seoul", 1200, 300),
    SEOUL_FIGHT_CLUB("Seoul Fight Club", 5811, 350),

    THE_SUNKEN_LIBRARY("The Sunken Library", 1300, 400),

    NEW_DAWN("New Dawn", 7800, 800),
    NEW_DAWN_BY_NIGHT("New Dawn by Night", 3155, 810),

    AGARTHA("Agartha", 5060, 1000),

    KINGSMOUTH("Kingsmouth", 3030, 1100),
    SAVAGE_COAST("Savage Coast", 3040, 1120),
    BLUE_MOUNTAIN("Blue Mountain", 3050, 1130),

    SCORCHED_DESERT("Scorched Desert", 3090, 1200),
    CITY_SUN_GOD("City of the Sun God", 3100, 1210),

    BESIEGED_FARMLANDS("Besieged Farmlands", 3120, 1300),
    SHADOWY_FOREST("Shadowy Forest", 3130, 1310),
    CARPATHIAN_FANGS("Carpathian Fangs", 3140, 1320),

    KAIDAN("Kaidan", 3070, 2000),
    KAIDAN_OROCHI_TOWER("The Orochi Tower", 6805 , 2010),

    // Seasonal
    LONELY_PATRIOT("Dungeon - The Lonely Patriot", 6407, 3000), // Samhain
    WAREHOUSE_BASEMENT("Warehouse Basement", 7290, 3010), // Winter
    NIFLHEIM("Dungeon - Niflheim", 6700, 3020), // Winter
    DEEP_AGARTHA("Raid - Gatekeeper Deep Agartha", 5740, 3030),
    FRAGMENT_IN_BETWEEN("Dungeon - A Fragment In Between", 7750, 3040), // Valentine's day

    ;

    private static final Map<Integer, Playfield> FROM_ID_MAP = new HashMap<>();
    static {
        for (Playfield playfield : values()) {
            FROM_ID_MAP.put(playfield.getPlayfieldId(), playfield);
        }
    }

    private final String name;
    private final int playfieldId;
    private final int priority;

    Playfield(String name, int playfieldId, int priority) {
        this.name = name;
        this.playfieldId = playfieldId;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public int getPlayfieldId() {
        return playfieldId;
    }

    public int getPriority() {
        return priority;
    }

    public static Playfield fromId(Integer id) {
        return FROM_ID_MAP.get(id);
    }

}
