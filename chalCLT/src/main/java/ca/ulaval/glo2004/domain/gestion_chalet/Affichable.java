package ca.ulaval.glo2004.domain.gestion_chalet;

import ca.ulaval.glo2004.domain.gestion_chalet.accessoires.Fenetre;
import ca.ulaval.glo2004.domain.gestion_chalet.accessoires.Porte;
import ca.ulaval.glo2004.domain.gestion_chalet.toit.Pignon;
import ca.ulaval.glo2004.domain.gestion_chalet.toit.RallongeVerticale;
import ca.ulaval.glo2004.services.Exportable;
import ca.ulaval.glo2004.services.Orientations;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public abstract class Affichable implements Exportable, Serializable {

    public static final Color COULEUR_SELECTIONNEE = new Color(102, 255, 102);
    public static final Color COULEUR_NON_SELECTIONNEE_MUR = new Color(255, 102, 0);
    public static final Color COULEUR_NON_SELECTIONNEE_FENETRE = new Color(203, 195, 227);

    public static final Color COULEUR_RALLONGE_VERTICALE = new Color(218, 112, 214);

    public static final Color COULEUR_PARTIE_SUPERIEURE = new Color(8, 126, 139);

    public static final Color COULEUR_PIGNON = Color.yellow;

    public static final Color COULEUR_NON_SELECTIONNEE_MUR_FA = new Color(0, 0, 204);
    public static final Color COULEUR_NON_SELECTIONNEE_PORTE = new Color(255, 200, 0);
    public static final Color COULEUR_ACCESSOIRE_INVALIDE = Color.RED;

    private boolean affichee;
    protected Color couleur;
    private boolean selectionnee;

    protected transient Shape shape;

    protected boolean valide;

    public Affichable() {
        this.affichee = false;
        this.selectionnee = false;
        this.valide = true;

        if (this instanceof Contour) {
            if (((Contour) this).getCote() == Orientations.FACADE || ((Contour) this).getCote() == Orientations.ARRIERE) {
                couleur = COULEUR_NON_SELECTIONNEE_MUR_FA;
            } else if (((Contour) this).getCote() == Orientations.GAUCHE || ((Contour) this).getCote() == Orientations.DROITE) {
                couleur = COULEUR_NON_SELECTIONNEE_MUR;
            }
        } else if (this instanceof Porte) {
            couleur = COULEUR_NON_SELECTIONNEE_PORTE;
        } else if (this instanceof Fenetre) {
            couleur = COULEUR_NON_SELECTIONNEE_FENETRE;
        } else if (this instanceof Pignon) {
            couleur = COULEUR_PIGNON;
        } else if (this instanceof RallongeVerticale) {
            couleur = COULEUR_RALLONGE_VERTICALE;
        }

    }

    public boolean estAffichee() {
        return affichee;
    }

    public abstract Shape getShape();

    public abstract Shape getOriginalShape();

    public Color getCouleur() {
        return couleur;
    }

    protected void setAffichee(boolean p_affichee) {
        this.affichee = p_affichee;
    }

    protected void setShape(Shape p_shape) {
        shape = p_shape;
    }

    protected void setValide(boolean valide) {
        this.valide = valide;
    }

    protected void setSelectionne(boolean p_selectionnee) {
        this.selectionnee = p_selectionnee;
        if (!(valide || selectionnee)) {
            couleur = COULEUR_ACCESSOIRE_INVALIDE;
        } else if (selectionnee) {
            couleur = COULEUR_SELECTIONNEE;
        } else {
            if (this instanceof Contour) {
                if (((Contour) this).getCote() == Orientations.FACADE || ((Contour) this).getCote() == Orientations.ARRIERE) {
                    couleur = COULEUR_NON_SELECTIONNEE_MUR_FA;
                } else if (((Contour) this).getCote() == Orientations.GAUCHE || ((Contour) this).getCote() == Orientations.DROITE) {
                    couleur = COULEUR_NON_SELECTIONNEE_MUR;
                }
            } else if (this instanceof Porte) {
                couleur = COULEUR_NON_SELECTIONNEE_PORTE;
            } else if (this instanceof Fenetre) {
                couleur = COULEUR_NON_SELECTIONNEE_FENETRE;
            } else if (this instanceof Pignon) {
                couleur = COULEUR_PIGNON;
            } else if (this instanceof RallongeVerticale) {
                couleur = COULEUR_RALLONGE_VERTICALE;
            }
        }
    }

    public boolean estSelectionne() {
        return this.selectionnee;
    }

    public boolean estValide() {
        return valide;
    }

    protected abstract void updateShapePosition(float x, float y);


}