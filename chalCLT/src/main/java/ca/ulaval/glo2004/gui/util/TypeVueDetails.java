package ca.ulaval.glo2004.gui.util;

public enum TypeVueDetails {

    PORTE("Porte"), MUR("Mur"), FENETRE("Fenetre"), VIDE("Vide"), STL("stl"),GRILLE("Grille");

    public final String type;

    TypeVueDetails(String p_type) {
        type = p_type;
    }

    public static String[] getListeTypeVueDetailsString() {
        String[] typeListe = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            typeListe[i] = values()[i].type;
        }
        return typeListe;
    }

    @Override
    public String toString() {
        return type;
    }
}
