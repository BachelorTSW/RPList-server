package com.swl.mod.rplist.enumerated;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.HashMap;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Playfield {

    UNKNOWN("Unknown", 0, 100000),

    LONDON("London", 1000, 100),
    TABULA_RASA("London - Tabula Rasa", 7080, 110),
    ALBION("London - Albion", 7070, 130),
    ALBION_REHEARSAL("London - Albion Rehearsal Stage", 7075, 135),

    NEW_YORK("New York", 1100, 200),

    SEOUL("Seoul", 1200, 300),

    THE_SUNKEN_LIBRARY("The Sunken Library", 1300, 400),

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

    ;

    private static final Map<Integer, Playfield> FROM_ID_MAP = new HashMap<>();
    static {
        for (Playfield playfield : values()) {
            FROM_ID_MAP.put(playfield.getPlayfieldId(), playfield);
        }
    }

    private String name;
    private int playfieldId;
    private int priority;

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
