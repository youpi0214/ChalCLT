package ca.ulaval.glo2004.domain.gestion_chalet.accessoires;

import ca.ulaval.glo2004.domain.gestion_chalet.Accessoire;
import ca.ulaval.glo2004.services.*;
import ca.ulaval.glo2004.services.CaisseOutils.*;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class Porte extends Accessoire implements Serializable {


    public Porte(Porte p_porte) {
        this(p_porte.getLargeur(), p_porte.getLongueur(), p_porte.getEpaisseur(), p_porte.getCoordonneesEmplacement(), p_porte.getMurEmplacement(), p_porte.getAccessoire_identification());
    }

    public Porte(UniteImperiale p_largeur, UniteImperiale p_longueur, UniteImperiale p_epaisseur, UniteImperiale[] p_coordonnesEmplacement, Orientations p_murEmplacement, int p_accessoire_identification) {
        super(p_largeur, p_longueur, p_epaisseur, p_coordonnesEmplacement, p_murEmplacement, p_accessoire_identification);
        refreshValeurCalcul();
    }

    protected void refreshValeurCalcul() {
        super.refreshValeurCalcul();
    }

    @Override
    public ContenuSTL genererVersionBrut(String fileDestination, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension) {
        return null;
    }

    @Override
    public ContenuSTL genererVersionFini(String fileDestination, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension) {
        return null;
    }

    @Override
    public ContenuSTL genererVersionRetrait(String fileDestination, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension, int nbrAccessoire) {
        if (id_acc >= nbrAccessoire + 3) {
            id_acc = 3;
        }
        String filename = CaisseOutils.CHALCLT_FILE_NAME + ExportationMode.RETRAIT + "_" + getMurEmplacement().orientation.charAt(0) + "_" + id_acc + CaisseOutils.EXTENSTION;
        ContenuSTL contenu_STL = new ContenuSTL(filename, fileDestination);
        ContenuSTL.RectangleSTL rectangleFace = new ContenuSTL.RectangleSTL(new ContenuSTL.PointSTL(0, 0, 0), new ContenuSTL.PointSTL(0, 0, getLargeur().pouces()), new ContenuSTL.PointSTL(getLongueur().pouces(), 0, getLargeur().pouces()), new ContenuSTL.PointSTL(getLongueur().pouces(), 0, 0));
        ContenuSTL.RectangleSTL rectangleArriere = new ContenuSTL.RectangleSTL(new ContenuSTL.PointSTL(0, getEpaisseur().pouces(), 0), new ContenuSTL.PointSTL(getLongueur().pouces(), getEpaisseur().pouces(), 0), new ContenuSTL.PointSTL(getLongueur().pouces(), getEpaisseur().pouces(), getLargeur().pouces()), new ContenuSTL.PointSTL(0, getEpaisseur().pouces(), getLargeur().pouces()));
        ContenuSTL.RectangleSTL rectangleGauche = new ContenuSTL.RectangleSTL(new ContenuSTL.PointSTL(0, 0, 0), new ContenuSTL.PointSTL(0, 0, getLargeur().pouces()), new ContenuSTL.PointSTL(0, getEpaisseur().pouces(), getLargeur().pouces()), new ContenuSTL.PointSTL(0, getEpaisseur().pouces(), 0));
        ContenuSTL.RectangleSTL rectangleDroite = new ContenuSTL.RectangleSTL(new ContenuSTL.PointSTL(getLongueur().pouces(), 0, 0), new ContenuSTL.PointSTL(getLongueur().pouces(), getEpaisseur().pouces(), 0), new ContenuSTL.PointSTL(getLongueur().pouces(), getEpaisseur().pouces(), getLargeur().pouces()), new ContenuSTL.PointSTL(getLongueur().pouces(), 0, getLargeur().pouces()));
        ContenuSTL.RectangleSTL rectangleDessus = new ContenuSTL.RectangleSTL(new ContenuSTL.PointSTL(0, 0, getLargeur().pouces()), new ContenuSTL.PointSTL(getLongueur().pouces(), 0, getLargeur().pouces()), new ContenuSTL.PointSTL(getLongueur().pouces(), getEpaisseur().pouces(), getLargeur().pouces()), new ContenuSTL.PointSTL(0, getEpaisseur().pouces(), getLargeur().pouces()));
        ContenuSTL.RectangleSTL rectangleBase = new ContenuSTL.RectangleSTL(new ContenuSTL.PointSTL(0, 0, 0), new ContenuSTL.PointSTL(getLongueur().pouces(), 0, 0), new ContenuSTL.PointSTL(getLongueur().pouces(), getEpaisseur().pouces(), 0), new ContenuSTL.PointSTL(0, getEpaisseur().pouces(), 0));
        contenu_STL.ajouterContenuSolide(rectangleBase, rectangleFace, rectangleArriere, rectangleDessus, rectangleDroite, rectangleGauche);
        id_acc++;
        return contenu_STL;
    }


    @Override
    protected void updateShapePosition(float x, float y) {
        setShape(new Rectangle2D.Double(x, y, shape.getBounds2D().getWidth(), shape.getBounds2D().getHeight()));
    }
}