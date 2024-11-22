package ca.ulaval.glo2004.domain.gestion_chalet;


import ca.ulaval.glo2004.services.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.*;

public abstract class Accessoire extends Affichable implements Serializable {

    private UniteImperiale longueur;
    protected static int id_acc = 3;
    private UniteImperiale largeur;
    private UniteImperiale epaisseur;
    private Orientations murEmplacement;
    /**
     * coordonnees en unités de mesures
     */
    private UniteImperiale[] coordonneesEmplacement;

    private int accessoire_identification;

    /**
     * @param p_largeur
     * @param p_longueur
     */
    public Accessoire(UniteImperiale p_largeur, UniteImperiale p_longueur, UniteImperiale p_epaisseur, UniteImperiale[] p_coordonnesEmplacement, Orientations p_murEmplacement, int p_accessoire_identification) {
        accessoire_identification = p_accessoire_identification;
        this.largeur = p_largeur;
        this.longueur = p_longueur;
        this.epaisseur = p_epaisseur;
        this.coordonneesEmplacement = p_coordonnesEmplacement;
        this.murEmplacement = p_murEmplacement;
        this.setShape(new Area(new Rectangle2D.Double(0, 0, longueur.pieds() * CaisseOutils.RATIO_TEST, largeur.pieds() * CaisseOutils.RATIO_TEST)));

    }

    protected void refreshValeurCalcul() {
    }

    @Override
    protected void setAffichee(boolean p_affichee) {
        super.setAffichee(p_affichee);
    }

    public int getAccessoire_identification() {
        return accessoire_identification;
    }

    protected void setLargeur(UniteImperiale p_largeur) {
        this.largeur = p_largeur;
    }


    protected void setLongueur(UniteImperiale longueur) {
        this.longueur = longueur;
    }

    public Orientations getMurEmplacement() {
        return murEmplacement;
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

    //    public float[] getCoordonneesEmplacement() {
//        return coordonneesEmplacement.clone();
//    }
    public UniteImperiale[] getCoordonneesEmplacement() {
        return coordonneesEmplacement.clone();
    }

    @Override
    public Shape getShape() {
        return new Rectangle2D.Double(shape.getBounds().getX(), shape.getBounds().getY(), shape.getBounds().getWidth(), shape.getBounds().getHeight());
    }

    protected void modifierCoordonnees(UniteImperiale[] p_coordonneesEmplacementNouveau) {
        coordonneesEmplacement[0] = p_coordonneesEmplacementNouveau[0];
        coordonneesEmplacement[1] = p_coordonneesEmplacementNouveau[1];
    }

    protected void updateDimensionAccesoire(UniteImperiale largeurNew, UniteImperiale longueurNew) {
        // Met à jour les dimensions de l'accessoire avec les nouvelles valeurs de largeur et de longueur
        setLargeur(largeurNew); // Met à jour la largeur de l'accessoire
        setLongueur(longueurNew); // Met à jour la longueur de l'accessoire

        // Met à jour la forme géométrique de l'accessoire en fonction des nouvelles dimensions
        setShape(new Rectangle2D.Double(
                shape.getBounds().getX(), // Position x de la forme
                shape.getBounds().getY(), // Position y de la forme
                longueurNew.pieds() * CaisseOutils.RATIO_TEST, // Nouvelle longueur en tenant compte du ratio
                largeurNew.pieds() * CaisseOutils.RATIO_TEST // Nouvelle largeur en tenant compte du ratio

        ));
    }

    @Override
    public Shape getOriginalShape() {
        return new Area(new Rectangle2D.Double(shape.getBounds().getX(), shape.getBounds().getY(), getLongueur().pieds() * CaisseOutils.RATIO_TEST, getLargeur().pieds() * CaisseOutils.RATIO_TEST));
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        // Initialize the transient field 'shape'
        shape = new Area(new Rectangle2D.Double(0, 0, getLongueur().pieds() * CaisseOutils.RATIO_TEST, getLargeur().pieds() * CaisseOutils.RATIO_TEST));
    }
}
