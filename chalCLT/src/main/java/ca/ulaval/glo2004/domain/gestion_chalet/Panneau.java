package ca.ulaval.glo2004.domain.gestion_chalet;


import ca.ulaval.glo2004.services.CaisseOutils;
import ca.ulaval.glo2004.services.Couches;
import ca.ulaval.glo2004.services.Orientations;
import ca.ulaval.glo2004.services.UniteImperiale;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Panneau extends Affichable implements Serializable {

    public static UniteImperiale DIMENSION_PANNEAU_DEFAUT = new UniteImperiale("20\"");

    public static UniteImperiale DIMENSION_PANNEAU_FENETRE = new UniteImperiale("20\"");
    public static UniteImperiale DIMENSION_PANNEAU_PORTE = new UniteImperiale("60\"");
    public static UniteImperiale DIMENSION_PANNEAU_PORTEL = new UniteImperiale("20\"");
    private UniteImperiale longueur;
    private UniteImperiale largeur;
    private UniteImperiale epaisseur;
    private Couches nbreCouches;

    protected List<Rainure> rainures;
    private Orientations cote;

    /**
     * @param p_largeur
     * @param p_longueur
     * @param p_epaisseur
     * @param p_nbreCouches
     * @param p_cote
     */
    public Panneau(UniteImperiale p_largeur, UniteImperiale p_longueur, UniteImperiale p_epaisseur, Couches p_nbreCouches, Orientations p_cote) {

        this.largeur = p_largeur;
        this.longueur = p_longueur;
        this.epaisseur = p_epaisseur;
        this.nbreCouches = p_nbreCouches; //TODO delete
        this.cote = p_cote;
        this.rainures = new ArrayList<>();
        float longueur = p_longueur.pieds();
        float largeur = p_largeur.pieds();
        this.setShape(new Area(new Rectangle2D.Double(0, 0, longueur * CaisseOutils.RATIO_TEST, largeur * CaisseOutils.RATIO_TEST)));
    }

    protected void setCote(Orientations cote) {
        this.cote = cote;
    }

    public List<Rainure> getRainures() {
        return rainures;
    }

    public UniteImperiale getLongueur() {
        return longueur;
    }

    public UniteImperiale getLargeur() {
        return largeur;
    }

    public UniteImperiale getEpaisseur() {
        return epaisseur;
    }

    public Orientations getCote() {
        return cote;
    }

    public Couches getNbreCouches() {
        return nbreCouches;
    }

    protected void setLongueur(UniteImperiale longueur) {
        this.longueur = longueur;
    }

    protected void setLargeur(UniteImperiale largeur) {
        this.largeur = largeur;
    }

    protected void setEpaisseur(UniteImperiale epaisseur) {
        this.epaisseur = epaisseur;
    }


}