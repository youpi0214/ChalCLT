package ca.ulaval.glo2004.domain.gestion_chalet.toit;

import ca.ulaval.glo2004.domain.gestion_chalet.Chalet;
import ca.ulaval.glo2004.domain.gestion_chalet.PanneauToit;
import ca.ulaval.glo2004.services.*;

import ca.ulaval.glo2004.services.ContenuSTL.*;
import ca.ulaval.glo2004.services.CaisseOutils.*;

import java.awt.*;
import java.awt.geom.Path2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import static ca.ulaval.glo2004.services.Orientations.*;

public class Pignon extends PanneauToit {

    private float anglePignon;
    private float hauteurPignonShape;

    private float hauteurPignon;
    private double positionX;

    private double positionY;

    private Orientations vueMode;

    private Orientations orientationToit = Orientations.ARRIERE;

    /**
     * @param p_pignon
     */
    public Pignon(Pignon p_pignon) {
        this(p_pignon.getLargeur(), p_pignon.getLongueur(), p_pignon.getEpaisseur(), p_pignon.getNbreCouches(), p_pignon.getCote(), p_pignon.getAngle());
    }


    public Pignon(UniteImperiale p_largeurChalet, UniteImperiale p_longueurChalet, UniteImperiale p_epaisseur, Couches p_nbreCouches, Orientations p_cote, float angle) {
        super(new UniteImperiale("0/1"), p_largeurChalet, p_epaisseur, p_nbreCouches, p_cote);
        this.anglePignon = angle;
        hauteurPignon = (float) (Math.tan(Math.toRadians(angle))) * getLongueur().pouces();
        hauteurPignonShape = (float) Math.tan(Math.toRadians(anglePignon)) * getLongueur().pieds() * CaisseOutils.RATIO_TEST;
        // TODO - implement Pignon.Pignon
    }

    @Override
    public void updateShapeComposanteToit(double pointX, double pointY, float angle, Orientations vueMode) {
        positionX = pointX;
        positionY = pointY;
        hauteurPignonShape = (float) Math.tan(Math.toRadians(anglePignon)) * getLongueur().pieds() * CaisseOutils.RATIO_TEST;
        if (this.getCote() == GAUCHE) {
            if (orientationToit == Orientations.ARRIERE || orientationToit == GAUCHE) {
                float[] pointsX = {(float) pointX,
                        (float) pointX + getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        (float) pointX};
                float[] pointsY = {(float) pointY,
                        (float) pointY,
                        (float) pointY - hauteurPignonShape};
                Path2D.Float pignon = new Path2D.Float();
                pignon.moveTo(pointsX[0], pointsY[0]);
                for (int i = 1; i < pointsX.length; i++) {
                    pignon.lineTo(pointsX[i], pointsY[i]);
                }
                pignon.closePath();
                this.shape = pignon;
            } else {
                if (orientationToit == FACADE && vueMode == GAUCHE) {
                    float[] pointsX = {(float) pointX,
                            (float) pointX + getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                            (float) pointX + getLongueur().pieds() * CaisseOutils.RATIO_TEST};
                    float[] pointsY = {(float) pointY,
                            (float) pointY,
                            (float) pointY - hauteurPignonShape};
                    Path2D.Float pignon = new Path2D.Float();
                    pignon.moveTo(pointsX[0], pointsY[0]);
                    for (int i = 1; i < pointsX.length; i++) {
                        pignon.lineTo(pointsX[i], pointsY[i]);
                    }
                    pignon.closePath();
                    this.shape = pignon;
                }
                if (orientationToit == FACADE && vueMode == DROITE) {
                    float[] pointsX = {(float) pointX,
                            (float) pointX + getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                            (float) pointX};
                    float[] pointsY = {(float) pointY,
                            (float) pointY,
                            (float) pointY - hauteurPignonShape};
                    Path2D.Float pignon = new Path2D.Float();
                    pignon.moveTo(pointsX[0], pointsY[0]);
                    for (int i = 1; i < pointsX.length; i++) {
                        pignon.lineTo(pointsX[i], pointsY[i]);
                    }
                    pignon.closePath();
                    this.shape = pignon;
                }
                if (orientationToit == DROITE && vueMode == ARRIERE) {
                    float[] pointsX = {(float) pointX,
                            (float) pointX + getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                            (float) pointX};
                    float[] pointsY = {(float) pointY,
                            (float) pointY,
                            (float) pointY - hauteurPignonShape};
                    Path2D.Float pignon = new Path2D.Float();
                    pignon.moveTo(pointsX[0], pointsY[0]);
                    for (int i = 1; i < pointsX.length; i++) {
                        pignon.lineTo(pointsX[i], pointsY[i]);
                    }
                    pignon.closePath();
                    this.shape = pignon;
                }
            }
        } else if (this.getCote() == DROITE) {
            if (orientationToit == FACADE && vueMode == GAUCHE) {
                float[] pointsX = {(float) pointX,
                        (float) pointX + getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        (float) pointX + getLongueur().pieds() * CaisseOutils.RATIO_TEST};
                float[] pointsY = {(float) pointY,
                        (float) pointY,
                        (float) pointY - hauteurPignonShape};
                Path2D.Float pignon = new Path2D.Float();
                pignon.moveTo(pointsX[0], pointsY[0]);
                for (int i = 1; i < pointsX.length; i++) {
                    pignon.lineTo(pointsX[i], pointsY[i]);
                }
                pignon.closePath();
                this.shape = pignon;
            } else {
                float[] pointsX = {(float) pointX,
                        (float) pointX + getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        (float) pointX + getLongueur().pieds() * CaisseOutils.RATIO_TEST};
                float[] pointsY = {(float) pointY,
                        (float) pointY,
                        (float) pointY - hauteurPignonShape};
                Path2D.Float pignon = new Path2D.Float();
                pignon.moveTo(pointsX[0], pointsY[0]);
                for (int i = 1; i < pointsX.length; i++) {
                    pignon.lineTo(pointsX[i], pointsY[i]);
                }
                pignon.closePath();
                this.shape = pignon;
            }
        }
    }

    @Override
    public float getAngle() {
        return anglePignon;
    }

    @Override
    protected void updateAngle(float angle) {
        anglePignon = angle;
        hauteurPignon = (float) Math.tan(Math.toRadians(angle)) * getLongueur().pouces();
        updateShapeComposanteToit(positionX, positionY, angle, vueMode);
    }

    @Override
    protected void setOrientation(Orientations orientations) {
        this.orientationToit = orientations;
    }

    @Override
    public UniteImperiale getHauteur() {
        return CaisseOutils.decimalToUniImp(Math.tan(Math.toRadians(getAngle())) * getLongueur().pouces());
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public Shape getOriginalShape() {
        return null;
    }


    @Override
    protected void updateShapePosition(float x, float y) {

    }

    @Override
    public ContenuSTL genererVersionBrut(String fileDestination, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension) {
        String filename = CaisseOutils.CHALCLT_FILE_NAME + ExportationMode.BRUT + "_P" + getCote().orientation.charAt(0) + CaisseOutils.EXTENSTION;
        ContenuSTL contenu_STL = new ContenuSTL(filename, fileDestination);

        double epaisseurPanneau = getEpaisseur().pouces();
        double largeurChalet = p_chaletDimmension.x.pouces();

        double angle = getAngle();
        double longueurPignon = getLongueur().pouces();
        double largeurPignon = Math.abs(Math.tan(Math.toRadians(angle)) * largeurChalet);
        double ajustementAngle = Math.abs(Math.tan(Math.toRadians(angle)) * epaisseurPanneau / 2);
        largeurPignon = largeurPignon - ajustementAngle;

        PointSTL origin = new PointSTL(0, 0, 0);
        PointSTL backLeft_top = new PointSTL(origin.x, origin.y, origin.z + largeurPignon + ajustementAngle);

        PointSTL backRight_bottom = new PointSTL(origin.x, origin.y + epaisseurPanneau, origin.z);
        PointSTL backRight_top = new PointSTL(backRight_bottom.x, backRight_bottom.y, backRight_bottom.z + largeurPignon + ajustementAngle);

        PointSTL frontLeft_bottom = new PointSTL(origin.x + longueurPignon, origin.y, origin.z);
        PointSTL frontLeft_top = new PointSTL(frontLeft_bottom.x, frontLeft_bottom.y, frontLeft_bottom.z);

        PointSTL frontRight_bottom = new PointSTL(frontLeft_bottom.x, frontLeft_bottom.y + epaisseurPanneau, frontLeft_bottom.z);
        PointSTL frontRight_top = new PointSTL(frontRight_bottom.x, frontRight_bottom.y, frontRight_bottom.z);

        contenu_STL.ajouterContenuSolide(new RectangleSTL(origin, backLeft_top, backRight_top, backRight_bottom));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(origin, frontLeft_bottom, frontRight_bottom, backRight_bottom));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(origin, frontLeft_bottom, frontLeft_top, backLeft_top));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(frontRight_bottom, backRight_bottom, backRight_top, frontRight_top));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(frontRight_bottom, frontLeft_bottom, frontLeft_top, frontRight_top));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(frontRight_top, frontLeft_top, backLeft_top, backRight_top));

        return contenu_STL;
    }

    @Override
    public ContenuSTL genererVersionRetrait(String fileDestination, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension, int sequentiel) {
        String filename = CaisseOutils.CHALCLT_FILE_NAME + ExportationMode.RETRAIT + "_P" + getCote().orientation.charAt(0) + CaisseOutils.EXTENSTION;
        ContenuSTL contenu_STL = new ContenuSTL(filename, fileDestination);

        double epaisseurPanneau = getEpaisseur().pouces();
        double demiEpaisseur = epaisseurPanneau / 2;

        double largeurChalet = p_chaletDimmension.x.pouces();

        double angle = getAngle();
        double longueurPignon = getLongueur().pouces();
        double largeurPignon = Math.abs(Math.tan(Math.toRadians(angle)) * largeurChalet);
        double ajustementAngle = Math.abs(Math.tan(Math.toRadians(angle)) * demiEpaisseur);
        largeurPignon = largeurPignon - ajustementAngle;


        PointSTL origin = new PointSTL(0, 0, 0);
        PointSTL backLeft_top = new PointSTL(origin.x, origin.y, origin.z + largeurPignon + ajustementAngle);

        PointSTL backRight_bottom = new PointSTL(origin.x, origin.y + demiEpaisseur, origin.z);
        PointSTL backRight_top = new PointSTL(backRight_bottom.x, backRight_bottom.y, backRight_bottom.z + largeurPignon + ajustementAngle);

        PointSTL originInnerLeft = new PointSTL(origin.x + demiEpaisseur, origin.y, origin.z);
        PointSTL backInnerLeft_top = new PointSTL(backLeft_top.x + demiEpaisseur, backLeft_top.y, backLeft_top.z - demiEpaisseur - ajustementAngle); //  - ajustementAngle

        PointSTL backInnerRight_bottom = new PointSTL(backRight_bottom.x + demiEpaisseur, backRight_bottom.y, backRight_bottom.z);
        PointSTL backInnerRight_top = new PointSTL(backRight_top.x + demiEpaisseur, backRight_top.y, backRight_top.z - demiEpaisseur - ajustementAngle);


        PointSTL frontLeft_bottom = new PointSTL(origin.x + longueurPignon, origin.y, origin.z);
        PointSTL frontRight_bottom = new PointSTL(frontLeft_bottom.x, frontLeft_bottom.y + epaisseurPanneau / 2, frontLeft_bottom.z);
        PointSTL frontInnerLeft_bottom = new PointSTL(frontLeft_bottom.x - demiEpaisseur, frontLeft_bottom.y, frontLeft_bottom.z);
        PointSTL frontInnerRight_bottom = new PointSTL(frontRight_bottom.x - demiEpaisseur, frontRight_bottom.y, frontRight_bottom.z);


        contenu_STL.ajouterContenuSolide(new RectangleSTL(origin, backLeft_top, backRight_top, backRight_bottom));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(origin, originInnerLeft, backInnerLeft_top, backLeft_top));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(backInnerLeft_top, backLeft_top, frontLeft_bottom, frontInnerLeft_bottom));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(frontLeft_bottom, frontRight_bottom, backRight_top, backLeft_top));

        contenu_STL.ajouterContenuSolide(new RectangleSTL(frontLeft_bottom, frontInnerLeft_bottom, frontInnerRight_bottom, frontRight_bottom));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(frontInnerLeft_bottom, backInnerLeft_top, backInnerRight_top, frontInnerRight_bottom));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(frontInnerLeft_bottom, backInnerLeft_top, backInnerRight_top, frontInnerRight_bottom));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(backInnerRight_top, backRight_top, frontRight_bottom, frontInnerRight_bottom));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(backRight_bottom, backInnerRight_bottom, backInnerRight_top, backRight_top));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(originInnerLeft, backInnerRight_bottom, backInnerRight_top, backInnerLeft_top));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(originInnerLeft, backInnerRight_bottom, backRight_bottom, origin));

        return contenu_STL;
    }

    @Override
    public ContenuSTL genererVersionFini(String fileDestination, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension) {
        String filename = CaisseOutils.CHALCLT_FILE_NAME + ExportationMode.FINI + "_P" + getCote().orientation.charAt(0) + CaisseOutils.EXTENSTION;
        ContenuSTL contenu_STL = new ContenuSTL(filename, fileDestination);

        Triplet<Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>>
                faceExterne = genererTableauDePoint(p_chaletDimmension, getCote(), false);

        Triplet<Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>>
                faceInterne = genererTableauDePoint(p_chaletDimmension, getCote(), true);
        remplirContenuSTL(contenu_STL, faceExterne, faceInterne);

        return contenu_STL;
    }


    public void remplirContenuSTL(ContenuSTL contenuSTL, Triplet<Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>> faceExterne,
                                  Triplet<Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>> faceInterne) {

        Triplet<Double, Double, Double>[][] tableauDePoint3D_Ext = faceExterne.x;
        Triplet<Double, Double, Double>[][] tableauDePoint3D_Ext_Middle = faceExterne.y;

        Triplet<Double, Double, Double>[][] tableauDePoint3D_Int = faceInterne.x;
        Triplet<Double, Double, Double>[][] tableauDePoint3D_Int_Middle = faceInterne.y;

        linkPointSTL(contenuSTL, tableauDePoint3D_Ext, tableauDePoint3D_Ext_Middle);
        linkPointSTL(contenuSTL, tableauDePoint3D_Int, tableauDePoint3D_Int_Middle);

        contenuSTL.ajouterContenuSolide(new ContenuSTL.RectangleSTL(// small part Gauche
                new ContenuSTL.PointSTL(tableauDePoint3D_Ext_Middle[0][0].x, tableauDePoint3D_Ext_Middle[0][0].y, tableauDePoint3D_Ext_Middle[0][0].z),
                new ContenuSTL.PointSTL(tableauDePoint3D_Ext_Middle[1][0].x, tableauDePoint3D_Ext_Middle[1][0].y, tableauDePoint3D_Ext_Middle[1][0].z),
                new ContenuSTL.PointSTL(tableauDePoint3D_Int_Middle[1][0].x, tableauDePoint3D_Int_Middle[1][0].y, tableauDePoint3D_Int_Middle[1][0].z),
                new ContenuSTL.PointSTL(tableauDePoint3D_Int_Middle[0][0].x, tableauDePoint3D_Int_Middle[0][0].y, tableauDePoint3D_Int_Middle[0][0].z)
        ));
        contenuSTL.ajouterContenuSolide(new ContenuSTL.RectangleSTL(// small part droite
                new ContenuSTL.PointSTL(tableauDePoint3D_Ext_Middle[0][1].x, tableauDePoint3D_Ext_Middle[0][1].y, tableauDePoint3D_Ext_Middle[0][1].z),
                new ContenuSTL.PointSTL(tableauDePoint3D_Ext_Middle[1][1].x, tableauDePoint3D_Ext_Middle[1][1].y, tableauDePoint3D_Ext_Middle[1][1].z),
                new ContenuSTL.PointSTL(tableauDePoint3D_Int_Middle[1][1].x, tableauDePoint3D_Int_Middle[1][1].y, tableauDePoint3D_Int_Middle[1][1].z),
                new ContenuSTL.PointSTL(tableauDePoint3D_Int_Middle[0][1].x, tableauDePoint3D_Int_Middle[0][1].y, tableauDePoint3D_Int_Middle[0][1].z)
        ));
        contenuSTL.ajouterContenuSolide(new ContenuSTL.RectangleSTL(// small part haut
                new ContenuSTL.PointSTL(tableauDePoint3D_Ext_Middle[1][0].x, tableauDePoint3D_Ext_Middle[1][0].y, tableauDePoint3D_Ext_Middle[1][0].z),
                new ContenuSTL.PointSTL(tableauDePoint3D_Ext_Middle[1][1].x, tableauDePoint3D_Ext_Middle[1][1].y, tableauDePoint3D_Ext_Middle[1][1].z),
                new ContenuSTL.PointSTL(tableauDePoint3D_Int_Middle[1][1].x, tableauDePoint3D_Int_Middle[1][1].y, tableauDePoint3D_Int_Middle[1][1].z),
                new ContenuSTL.PointSTL(tableauDePoint3D_Int_Middle[1][0].x, tableauDePoint3D_Int_Middle[1][0].y, tableauDePoint3D_Int_Middle[1][0].z)
        ));

    }

    private void linkPointSTL(ContenuSTL contenuSTL, Triplet<Double, Double, Double>[][] tableauDePoint3D, Triplet<Double, Double, Double>[][] tableauDePoint3D_Middle) {
        contenuSTL.ajouterContenuSolide(new ContenuSTL.RectangleSTL(// ext
                new ContenuSTL.PointSTL(tableauDePoint3D[0][0].x, tableauDePoint3D[0][0].y, tableauDePoint3D[0][0].z),
                new ContenuSTL.PointSTL(tableauDePoint3D[0][1].x, tableauDePoint3D[0][1].y, tableauDePoint3D[0][1].z),
                new ContenuSTL.PointSTL(tableauDePoint3D[1][1].x, tableauDePoint3D[1][1].y, tableauDePoint3D[1][1].z),
                new ContenuSTL.PointSTL(tableauDePoint3D[1][0].x, tableauDePoint3D[1][0].y, tableauDePoint3D[1][0].z)
        ));
        contenuSTL.ajouterContenuSolide(new ContenuSTL.RectangleSTL( // ext dessous
                new ContenuSTL.PointSTL(tableauDePoint3D[0][0].x, tableauDePoint3D[0][0].y, tableauDePoint3D[0][0].z),
                new ContenuSTL.PointSTL(tableauDePoint3D_Middle[0][0].x, tableauDePoint3D_Middle[0][0].y, tableauDePoint3D_Middle[0][0].z),
                new ContenuSTL.PointSTL(tableauDePoint3D_Middle[0][1].x, tableauDePoint3D_Middle[0][1].y, tableauDePoint3D_Middle[0][1].z),
                new ContenuSTL.PointSTL(tableauDePoint3D[0][1].x, tableauDePoint3D[0][1].y, tableauDePoint3D[0][1].z)
        ));
        contenuSTL.ajouterContenuSolide(new ContenuSTL.RectangleSTL( // ext gauche
                new ContenuSTL.PointSTL(tableauDePoint3D[0][0].x, tableauDePoint3D[0][0].y, tableauDePoint3D[0][0].z),
                new ContenuSTL.PointSTL(tableauDePoint3D[1][0].x, tableauDePoint3D[1][0].y, tableauDePoint3D[1][0].z),
                new ContenuSTL.PointSTL(tableauDePoint3D_Middle[1][0].x, tableauDePoint3D_Middle[1][0].y, tableauDePoint3D_Middle[1][0].z),
                new ContenuSTL.PointSTL(tableauDePoint3D_Middle[0][0].x, tableauDePoint3D_Middle[0][0].y, tableauDePoint3D_Middle[0][0].z)
        ));

        contenuSTL.ajouterContenuSolide(new ContenuSTL.RectangleSTL( // ext droite
                new ContenuSTL.PointSTL(tableauDePoint3D[0][1].x, tableauDePoint3D[0][1].y, tableauDePoint3D[0][1].z),
                new ContenuSTL.PointSTL(tableauDePoint3D[1][1].x, tableauDePoint3D[1][1].y, tableauDePoint3D[1][1].z),
                new ContenuSTL.PointSTL(tableauDePoint3D_Middle[1][1].x, tableauDePoint3D_Middle[1][1].y, tableauDePoint3D_Middle[1][1].z),
                new ContenuSTL.PointSTL(tableauDePoint3D_Middle[0][1].x, tableauDePoint3D_Middle[0][1].y, tableauDePoint3D_Middle[0][1].z)
        ));

        contenuSTL.ajouterContenuSolide(new ContenuSTL.RectangleSTL( // ext haut
                new ContenuSTL.PointSTL(tableauDePoint3D[1][0].x, tableauDePoint3D[1][0].y, tableauDePoint3D[1][0].z),
                new ContenuSTL.PointSTL(tableauDePoint3D[1][1].x, tableauDePoint3D[1][1].y, tableauDePoint3D[1][1].z),
                new ContenuSTL.PointSTL(tableauDePoint3D_Middle[1][1].x, tableauDePoint3D_Middle[1][1].y, tableauDePoint3D_Middle[1][1].z),
                new ContenuSTL.PointSTL(tableauDePoint3D_Middle[1][0].x, tableauDePoint3D_Middle[1][0].y, tableauDePoint3D_Middle[1][0].z)
        ));

    }


    public Triplet<Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>> genererTableauDePoint(Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension, Orientations face, boolean faceInterieur) {
        double epaisseurPanneau = getEpaisseur().pouces();
        double demiEpaisseur = epaisseurPanneau / 2;

        double largeurChalet = p_chaletDimmension.x.pouces();
        double longueurChalet = p_chaletDimmension.y.pouces();
        double hauteurChalet = p_chaletDimmension.z.pouces();

        double angle = getAngle();
        double longueurPignon = getLongueur().pouces();
        double largeurPignon = Math.abs(Math.tan(Math.toRadians(angle)) * largeurChalet);
        double retrait = Chalet.getRetraitSupplementaire().pouces() / 2;

        double ajustementAngle = Math.abs(Math.tan(Math.toRadians(angle)) * demiEpaisseur);
        largeurPignon = largeurPignon - ajustementAngle;
        Triplet<Double, Double, Double> pointOrigineMur3D;
        Tuple<Double, Double> pointOriginePlanDuMur2D;
        int murPlanXtranslator;
        int drawingDirection = 0;


        /**** determination des parametres de calcul selon le cote ou se trouve le mur *****/


        if (face.equals(GAUCHE)) {
            longueurPignon = longueurPignon - (faceInterieur ? epaisseurPanneau + retrait * 2 : retrait * 2);
            largeurPignon = faceInterieur ? largeurPignon - ajustementAngle - demiEpaisseur - retrait : largeurPignon;
            pointOrigineMur3D = new Triplet<>((faceInterieur ? epaisseurPanneau : demiEpaisseur + retrait), 0.0 + (faceInterieur ? epaisseurPanneau : 0), hauteurChalet);
            // le systeme de point de coordonne des accessoires sera sur le plan (x,z)
            pointOriginePlanDuMur2D = new Tuple<>(pointOrigineMur3D.x, pointOrigineMur3D.z);
            murPlanXtranslator = 1;
            drawingDirection = 1;
        } else {
            longueurPignon = longueurPignon - (faceInterieur ? epaisseurPanneau + retrait * 2 : retrait * 2);
            largeurPignon = faceInterieur ? largeurPignon - ajustementAngle - demiEpaisseur - retrait : largeurPignon;
            pointOrigineMur3D = new Triplet<>((faceInterieur ? epaisseurPanneau : demiEpaisseur + retrait), longueurChalet, hauteurChalet);
            // le systeme de point de coordonne des accessoires sera sur le plan (x,z)
            pointOriginePlanDuMur2D = new Tuple<>(pointOrigineMur3D.x, pointOrigineMur3D.z);
            murPlanXtranslator = 1;
            drawingDirection = -1;
        }


        /****  tracage de lignes extrapolées des contoures des accessoires *****/

        // creation du tableau de points
        Tuple<Double, Double>[][] tableauDePointFaceExterne = new Tuple[2][2];


        tableauDePointFaceExterne[0][0] = new Tuple<>(pointOriginePlanDuMur2D.x, pointOriginePlanDuMur2D.y);
        tableauDePointFaceExterne[0][1] = new Tuple<>(pointOriginePlanDuMur2D.x + (murPlanXtranslator * longueurPignon), pointOriginePlanDuMur2D.y);
        tableauDePointFaceExterne[1][1] = new Tuple<>(pointOriginePlanDuMur2D.x + (murPlanXtranslator * longueurPignon), pointOriginePlanDuMur2D.y + 0);
        tableauDePointFaceExterne[1][0] = new Tuple<>(pointOriginePlanDuMur2D.x, pointOriginePlanDuMur2D.y + largeurPignon);


        /**** conversion des points 2D extraits en points 3D, le tout organisé et ordonné dans un tableau *****/
        Triplet<Double, Double, Double>[][] tableauDePoint3D_Ext = new Triplet[tableauDePointFaceExterne.length][tableauDePointFaceExterne[0].length];
        Triplet<Double, Double, Double>[][] tableauDePoint3D_Middle = new Triplet[tableauDePointFaceExterne.length][tableauDePointFaceExterne[0].length];

        demiEpaisseur = faceInterieur ? demiEpaisseur * -1 : demiEpaisseur;
        for (int i = 0; i < tableauDePointFaceExterne.length; i++)
            for (int j = 0; j < tableauDePointFaceExterne[0].length; j++) {
                // x,z
                Tuple<Double, Double> cas1Point2D = tableauDePointFaceExterne[i][j];
                double retraitSides = j == 0 ? retrait * -1 : retrait;
                double PointX = cas1Point2D.x + (faceInterieur ? retraitSides : 0);
                double PointY = pointOrigineMur3D.y + demiEpaisseur * drawingDirection - retrait * drawingDirection;
                double pointZ = cas1Point2D.y;
                if (faceInterieur) {
                    tableauDePoint3D_Ext[i][j] = new Triplet<>(PointX, pointOrigineMur3D.y - (face.equals(DROITE) ? epaisseurPanneau : 0), pointZ);
                    tableauDePoint3D_Middle[i][j] = new Triplet<>(PointX, PointY - (face.equals(DROITE) ? epaisseurPanneau : 0), cas1Point2D.y);
                } else {
                    tableauDePoint3D_Ext[i][j] = new Triplet<>(PointX, pointOrigineMur3D.y, cas1Point2D.y);
                    tableauDePoint3D_Middle[i][j] = new Triplet<>(PointX, PointY, pointZ);
                }
            }


        return new Triplet<>(tableauDePoint3D_Ext, tableauDePoint3D_Middle, pointOrigineMur3D);
    }

}