package ca.ulaval.glo2004.domain.gestion_chalet;

import ca.ulaval.glo2004.services.Couches;
import ca.ulaval.glo2004.services.Orientations;
import ca.ulaval.glo2004.services.UniteImperiale;

public abstract class PanneauToit extends Panneau {

    /**
     * @param p_largeur
     * @param p_longueur
     * @param p_epaisseur
     * @param p_nbreCouches
     * @param p_cote
     */
    public PanneauToit(UniteImperiale p_largeur, UniteImperiale p_longueur, UniteImperiale p_epaisseur, Couches p_nbreCouches, Orientations p_cote) {
        super(p_largeur, p_longueur, p_epaisseur, p_nbreCouches, p_cote);
    }

    protected abstract void updateShapeComposanteToit(double pointX, double pointY, float angle, Orientations vueMode);

    public abstract float getAngle();

    protected abstract void updateAngle(float angle);

    protected abstract void setOrientation(Orientations orientations);

    public abstract UniteImperiale getHauteur();


}
