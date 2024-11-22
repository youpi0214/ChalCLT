package ca.ulaval.glo2004.domain.gestion_chalet;

import ca.ulaval.glo2004.services.*;

import java.awt.*;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Contour extends Panneau implements Serializable {

    public Contour(UniteImperiale p_largeur, UniteImperiale p_longueur, UniteImperiale p_epaisseur, Couches p_nbreCouches, Orientations p_cote) {
        super(p_largeur, p_longueur, p_epaisseur, p_nbreCouches, p_cote);
    }


    protected abstract boolean ajouterAccessoire(Accessoire p_accessoire);

    protected abstract boolean modifierMur(UniteImperiale p_largeur, UniteImperiale p_longueur, UniteImperiale p_epaisseur, UniteImperiale p_espacement);

    public abstract List<Accessoire> getAccessoiresListes();

    public abstract boolean validerFenetre(UniteImperiale[] position, float[] dimension);

    protected abstract boolean validerPorte(UniteImperiale[] positionAccesoire, float[] dimensionAccessoire);

    protected abstract boolean validerPoximiteAccessoire(Accessoire p_accessoire, UniteImperiale[] positionAccesoire, float[] dimensionAccessoire);

    protected abstract boolean validerDeplacementAccessoire(Accessoire p_accessoire, UniteImperiale[] p_coordonneesEmplacementNouveau);

    protected abstract void updateShapeVueDessus(Shape p_shapeVueDessus);

    public abstract Shape getShapeVueDessus();

    public abstract UniteImperiale getEspacementAccessoires();

    protected abstract void updateShape();

    protected abstract void updateShape(Shape shape);

    protected abstract void updateShapeDebordement(Orientations p_orientations, Shape shape);

    public abstract Hashtable<Orientations, Shape> getShapeDebordement();

    protected abstract Accessoire supprimerAccessoire(UniteImperiale[] p_coordonneesEmplacement);

    protected abstract Accessoire supprimerAccessoire(int accessoire_id);

    public abstract ConcurrentLinkedQueue<ContenuSTL> demandeGenererAccessoiresSTL(String p_dossierDestination, CaisseOutils.Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension);

    protected abstract void retirerAccessoireDeLaListeInvalides(Accessoire p_accessoire);

    public abstract boolean contientAccessoiresInvalide();



}
