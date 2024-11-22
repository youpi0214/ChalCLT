package ca.ulaval.glo2004.domain.gestion_chalet;

import ca.ulaval.glo2004.domain.gestion_chalet.toit.PartieSuperieure;
import ca.ulaval.glo2004.domain.gestion_chalet.toit.Pignon;
import ca.ulaval.glo2004.domain.gestion_chalet.toit.RallongeVerticale;
import ca.ulaval.glo2004.services.*;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Toit implements Serializable {


    private PanneauToit rallongeVerticale;
    private PanneauToit partieSuperieure;
    private PanneauToit pignonGauche;
    private PanneauToit pignonDroite;

    public float getAngle() {
        return angle;
    }

    private float angle;

    public Orientations getOrientation() {
        return orientation;
    }

    private Orientations orientation;


    public Toit(float p_angle, Orientations p_oritenation, UniteImperiale largeurChalet, UniteImperiale longueurChalet, UniteImperiale epaisseurChalet) {
        this.angle = p_angle;
        this.orientation = p_oritenation;
        this.pignonGauche = new Pignon(largeurChalet, longueurChalet, epaisseurChalet, Couches.CINQ, Orientations.GAUCHE, angle);
        this.pignonDroite = new Pignon(largeurChalet, longueurChalet, epaisseurChalet, Couches.CINQ, Orientations.DROITE, angle);
//        this.pignonDroite.updateShapeComposanteToit(p_oritenation,p_angle,largeurChalet.pieds(),longueurChalet.pieds(), epaisseurChalet.pieds(), pointX, pointY);
        this.rallongeVerticale = new RallongeVerticale(largeurChalet, longueurChalet, epaisseurChalet, Couches.CINQ, Orientations.GAUCHE, angle);
        this.partieSuperieure = new PartieSuperieure(largeurChalet, longueurChalet, epaisseurChalet, Couches.CINQ, Orientations.GAUCHE, angle);
        for (PanneauToit p : donneesToit()) {
            p.setOrientation(getOrientation());
        }
    }

    public Toit(RallongeVerticale p_rallongeVerticale, PartieSuperieure p_partieSuperieure, Pignon p_pignonGauche, Pignon p_pignonDroite, float p_angle) {
        this.rallongeVerticale = p_rallongeVerticale;
        this.partieSuperieure = p_partieSuperieure;
        this.pignonGauche = p_pignonGauche;
        this.pignonDroite = p_pignonDroite;
        this.angle = p_angle;
        this.orientation = Orientations.ARRIERE;
    }

    /**
     * @param p_toit
     */
    public Toit(Toit p_toit) {
        this.rallongeVerticale = new RallongeVerticale((RallongeVerticale) p_toit.rallongeVerticale);
        this.partieSuperieure = new PartieSuperieure((PartieSuperieure) p_toit.partieSuperieure);
        this.pignonGauche = new Pignon((Pignon) p_toit.pignonGauche);
        this.pignonDroite = new Pignon((Pignon) p_toit.pignonDroite);
        this.angle = p_toit.angle;
        this.orientation = p_toit.orientation;
    }

    /**
     * @param p_angle
     */
    void modifierAngle(float p_angle) {
        this.angle = p_angle;
        for (PanneauToit p : donneesToit()) {
            p.updateAngle(angle);
        }
    }

    /**
     * @param orientation
     */
    void changerOrientation(Orientations orientation) {
        this.orientation = orientation;
        for (PanneauToit p : donneesToit()) {
            p.setOrientation(orientation);
        }
    }

    protected void updateShapePositionComposantes(Mur murFacadeVirtuel, Mur murGaucheVirtuel, Mur murArriereVirtuel, Mur murDroiteVirtuel) {
        List<Mur> murList = new ArrayList<>();
        murList.add(murArriereVirtuel);
        murList.add(murFacadeVirtuel);
        murList.add(murGaucheVirtuel);
        murList.add(murDroiteVirtuel);
        pignonGauche.updateShapeComposanteToit(murGaucheVirtuel.getShape().getBounds().getX(), murGaucheVirtuel.getShape().getBounds().getY(), getAngle(), murGaucheVirtuel.getCote());
        pignonDroite.updateShapeComposanteToit(murDroiteVirtuel.getShape().getBounds().getX(), murDroiteVirtuel.getShape().getBounds().getY(), getAngle(), murDroiteVirtuel.getCote());
        if (murGaucheVirtuel.estAffichee()) {
            rallongeVerticale.updateShapeComposanteToit(murGaucheVirtuel.getShape().getBounds().getX(), murGaucheVirtuel.getShape().getBounds().getY(), getAngle(), murGaucheVirtuel.getCote());
            rallongeVerticale.setOrientation(Orientations.GAUCHE);
        } else if (murDroiteVirtuel.estAffichee()) {
            rallongeVerticale.updateShapeComposanteToit(murDroiteVirtuel.getShape().getBounds().getX() + murDroiteVirtuel.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                    murDroiteVirtuel.getShape().getBounds().getY(), getAngle(), murDroiteVirtuel.getCote());
            rallongeVerticale.setOrientation(Orientations.DROITE);
        } else {
            rallongeVerticale.updateShapeComposanteToit(murArriereVirtuel.getShape().getBounds().getX(), murArriereVirtuel.getShape().getBounds().getY(), getAngle(), murArriereVirtuel.getCote());
            rallongeVerticale.setOrientation(Orientations.ARRIERE);
            if (murFacadeVirtuel.estAffichee()) {
                partieSuperieure.updateShapeComposanteToit(murFacadeVirtuel.getShape().getBounds().getX(), murFacadeVirtuel.getShape().getBounds().getY(), getAngle(), murFacadeVirtuel.getCote());
            }
        }
    }


    protected void updateDimensionsComposantes(Mur murFacadeVirtuel, Mur murGaucheVirtuel) {
        pignonGauche.setLongueur(murGaucheVirtuel.getLongueur());
        pignonDroite.setLongueur(murGaucheVirtuel.getLongueur());
        rallongeVerticale.setLongueur(murFacadeVirtuel.getLongueur());
        rallongeVerticale.setLargeur(murGaucheVirtuel.getLongueur());
        partieSuperieure.setLongueur(murFacadeVirtuel.getLongueur());
        for (PanneauToit p : donneesToit()) {
            p.setEpaisseur(murFacadeVirtuel.getEpaisseur());
        }
    }

    public List<PanneauToit> donneesToit() {
        List<PanneauToit> composantesToit = new ArrayList<>();
        composantesToit.add(this.pignonGauche);
        composantesToit.add(this.pignonDroite);
        composantesToit.add(this.rallongeVerticale);
        composantesToit.add(this.partieSuperieure);
        return composantesToit;
    }

    protected void afficherComposantes(List<Mur> listMurs, Orientations pVueMode) {
        for (PanneauToit p : donneesToit()) {
            p.setAffichee(false);
        }
        for (Mur mur : listMurs) {
            if (mur.estAffichee()) {
                if (mur.getCoteVirtuel() == Orientations.FACADE) {
                    partieSuperieure.setAffichee(true);
               } else if (mur.getCoteVirtuel() == Orientations.ARRIERE) {
                    rallongeVerticale.setAffichee(true);
                    rallongeVerticale.setOrientation(Orientations.ARRIERE);
                } else if (mur.getCoteVirtuel() == Orientations.GAUCHE) {
                    rallongeVerticale.setAffichee(true);
                    rallongeVerticale.setOrientation(Orientations.GAUCHE);
                    pignonGauche.setAffichee(true);
                    pignonDroite.setAffichee(false);
                } else {
                    rallongeVerticale.setAffichee(true);
                    rallongeVerticale.setOrientation(Orientations.DROITE);
                    pignonDroite.setAffichee(true);
                    pignonGauche.setAffichee(false);
                }
            }
        }
    }

    public Affichable selectionnerComposanteToit(Point2D pClickedPointInInches) {
        Affichable composanteSeletionner = null;
        for (PanneauToit p : donneesToit()) {
                    if (p.getShape().contains(pClickedPointInInches)) {
                        p.setSelectionne(true);
                        composanteSeletionner = p;
                    }
        }
        return composanteSeletionner;
    }

    protected ConcurrentLinkedQueue<ContenuSTL> preparerSTLToit(ExportationMode exportationMode, String p_dossierDestination, CaisseOutils.Triplet<UniteImperiale, UniteImperiale, UniteImperiale> chaletDimension) {
        ConcurrentLinkedQueue<ContenuSTL> listContenuSTL = new ConcurrentLinkedQueue<>();

        if (exportationMode == ExportationMode.FINI) {
            listContenuSTL.add(partieSuperieure.genererVersionFini(p_dossierDestination, chaletDimension));
            listContenuSTL.add(rallongeVerticale.genererVersionFini(p_dossierDestination, chaletDimension));
            listContenuSTL.add(pignonGauche.genererVersionFini(p_dossierDestination, chaletDimension));
            listContenuSTL.add(pignonDroite.genererVersionFini(p_dossierDestination, chaletDimension));
        } else if (exportationMode == ExportationMode.RETRAIT) {
//            listContenuSTL.add(partieSuperieure.genererVersionRetrait(p_dossierDestination, chaletDimension, 0));
            listContenuSTL.add(rallongeVerticale.genererVersionRetrait(p_dossierDestination, chaletDimension, 0));
            listContenuSTL.add(pignonGauche.genererVersionRetrait(p_dossierDestination, chaletDimension, 0));
            listContenuSTL.add(pignonDroite.genererVersionRetrait(p_dossierDestination, chaletDimension, 0));
        } else if (exportationMode == ExportationMode.BRUT) {
            listContenuSTL.add(partieSuperieure.genererVersionBrut(p_dossierDestination, chaletDimension));
            listContenuSTL.add(rallongeVerticale.genererVersionBrut(p_dossierDestination, chaletDimension));
            listContenuSTL.add(pignonGauche.genererVersionBrut(p_dossierDestination, chaletDimension));
            listContenuSTL.add(pignonDroite.genererVersionBrut(p_dossierDestination, chaletDimension));
        }
        return listContenuSTL;
    }

    public Affichable surVolComposantes(Point2D pHoveredPointInInches) {
        Affichable composante = null;
        for (PanneauToit p : donneesToit()) {
            if (p.getShape() != null) {
                if (p.estAffichee()) {
                    if (p.getShape().contains(pHoveredPointInInches)) {
                        composante = p;
                    }
                }
            }
        }
        return  composante;
    }
}