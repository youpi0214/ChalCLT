package ca.ulaval.glo2004.services;

public enum Orientations {
    DROITE("Droite"), GAUCHE("Gauche"), FACADE("Façade"), ARRIERE("Arrière"), DESSUS("Dessus");


    public final String orientation;

    Orientations(String p_orientation) {
        orientation = p_orientation;
    }

    public static String[] getListeOrientationsString() {
        String[] orientationsListe = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            orientationsListe[i] = values()[i].orientation;
        }
        return orientationsListe;
    }

    @Override
    public String toString() {
        return orientation;
    }
}