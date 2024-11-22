package ca.ulaval.glo2004.domain.gestion_chalet.toit;

import ca.ulaval.glo2004.domain.gestion_chalet.Chalet;
import ca.ulaval.glo2004.domain.gestion_chalet.PanneauToit;
import ca.ulaval.glo2004.services.*;
import ca.ulaval.glo2004.services.ContenuSTL.*;
import ca.ulaval.glo2004.services.CaisseOutils.*;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class RallongeVerticale extends PanneauToit {

    private float angleRallongeVerticale;
    private float hauteurRallongeVerticaleShape;

    private float hauteurRallongeVerticale;

    Orientations vueMode;
    private double positionX;

    private double positionY;

    public Orientations getRallongeVerticalCote() {
        return rallongeVerticalCote;
    }

    private void setRallongeVerticalCote(Orientations rallongeVerticalCote) {
        this.rallongeVerticalCote = rallongeVerticalCote;
    }

    private Orientations rallongeVerticalCote;
    private Orientations orientationToit = Orientations.ARRIERE;

    /**
     * @param p_rallongeVerticale
     */
    public RallongeVerticale(RallongeVerticale p_rallongeVerticale) {
        this(p_rallongeVerticale.getLargeur(), p_rallongeVerticale.getLongueur(), p_rallongeVerticale.getEpaisseur(), p_rallongeVerticale.getNbreCouches(), p_rallongeVerticale.getCote(), p_rallongeVerticale.getAngle());
    }

    public RallongeVerticale(UniteImperiale p_largeurChalet, UniteImperiale p_longueurChalet, UniteImperiale p_epaisseur, Couches p_nbreCouches, Orientations p_cote, float angle) {
        super(p_largeurChalet, p_longueurChalet, p_epaisseur, p_nbreCouches, p_cote);
        this.angleRallongeVerticale = angle;
        rallongeVerticalCote = p_cote;
        hauteurRallongeVerticale = (float) (Math.tan(Math.toRadians(angle))) * (getLargeur().pouces() + getEpaisseur().pouces() / 2);
        hauteurRallongeVerticaleShape = (float) Math.tan(Math.toRadians(angleRallongeVerticale)) * (getLargeur().pieds() + getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST;
    }

    @Override
    protected void updateShapeComposanteToit(double pointX, double pointY, float angle, Orientations vueMode) {
        hauteurRallongeVerticaleShape = (float) Math.tan(Math.toRadians(angleRallongeVerticale)) * (getLargeur().pieds() + getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST;
        if (this.getRallongeVerticalCote() == Orientations.GAUCHE) {
            positionX = pointX;
            positionY = pointY;
            float[] pointsX = new float[]{(float) (positionX - getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST), (float) (positionX), (float) (positionX), (float) (positionX - getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST)};
            float[] pointsY = new float[]{(float) positionY, (float) positionY, (float) positionY - (float) Math.tan(Math.toRadians(angleRallongeVerticale)) * getLargeur().pieds() * CaisseOutils.RATIO_TEST, (float) positionY - hauteurRallongeVerticaleShape};
            Path2D.Float rallongeVerticale = new Path2D.Float();
            rallongeVerticale.moveTo(pointsX[0], pointsY[0]);
            for (int i = 1; i < pointsX.length; i++) {
                rallongeVerticale.lineTo(pointsX[i], pointsY[i]);
            }
            rallongeVerticale.closePath();
            this.shape = rallongeVerticale;
        } else if (this.getRallongeVerticalCote() == Orientations.DROITE) {
            positionX = pointX;
            positionY = pointY;
            float[] pointsX = new float[]{(float) positionX, (float) positionX + getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, (float) positionX + getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, (float) positionX};
            float[] pointsY = new float[]{(float) positionY, (float) positionY, (float) positionY - hauteurRallongeVerticaleShape, (float) positionY - (float) Math.tan(Math.toRadians(angleRallongeVerticale)) * getLargeur().pieds() * CaisseOutils.RATIO_TEST};
            Path2D.Float rallongeVerticale = new Path2D.Float();
            rallongeVerticale.moveTo(pointsX[0], pointsY[0]);
            for (int i = 1; i < pointsX.length; i++) {
                rallongeVerticale.lineTo(pointsX[i], pointsY[i]);
            }
            rallongeVerticale.closePath();
            this.shape = rallongeVerticale;
        }
        else if (this.getRallongeVerticalCote() == Orientations.ARRIERE && vueMode == Orientations.ARRIERE) {
            this.shape = new Rectangle2D.Double(0, pointY - (Math.tan(Math.toRadians(angle)) * (getLargeur().pieds() + getEpaisseur().pieds() / 2)) * CaisseOutils.RATIO_TEST, getLongueur().pieds() * CaisseOutils.RATIO_TEST, (Math.tan(Math.toRadians(angle)) * (getLargeur().pieds() + getEpaisseur().pieds() / 2)) * CaisseOutils.RATIO_TEST);
        }
    }

    @Override
    public float getAngle() {
        return angleRallongeVerticale;
    }

    @Override
    protected void updateAngle(float angle) {
        angleRallongeVerticale = angle;
        hauteurRallongeVerticale = (float) Math.tan(Math.toRadians(angle)) * (getLargeur().pouces() + getEpaisseur().pouces() / 2);
        updateShapeComposanteToit(positionX, positionY, angle, vueMode);
    }

    @Override
    protected void setOrientation(Orientations orientations) {
        setRallongeVerticalCote(orientations);
    }

    @Override
    public UniteImperiale getHauteur() {
        return CaisseOutils.decimalToUniImp(Math.tan(Math.toRadians(getAngle())) * (getLargeur().pouces() + getEpaisseur().pouces() / 2));
    }

    @Override
    protected void updateShapePosition(float x, float y) {

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
    public ContenuSTL genererVersionBrut(String fileDestination, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension) {
        String filename = CaisseOutils.CHALCLT_FILE_NAME + ExportationMode.BRUT + "_R" + CaisseOutils.EXTENSTION;
        ContenuSTL contenu_STL = new ContenuSTL(filename, fileDestination);

        double epaisseurPanneau = getEpaisseur().pouces();
        double angle = getAngle();
        double longueurRallonge = p_chaletDimmension.y.pouces();
        double largeurRallonge = Math.abs(Math.tan(Math.toRadians(angle)) * p_chaletDimmension.x.pouces());

        PointSTL origin = new PointSTL(0, 0, 0);
        PointSTL backLeft_top = new PointSTL(origin.x, origin.y, origin.z + largeurRallonge);

        PointSTL backRight_bottom = new PointSTL(origin.x, origin.y + epaisseurPanneau, origin.z);
        PointSTL backRight_top = new PointSTL(backRight_bottom.x, backRight_bottom.y, backRight_bottom.z + largeurRallonge);

        PointSTL frontLeft_bottom = new PointSTL(origin.x + longueurRallonge, origin.y, origin.z);
        PointSTL frontLeft_top = new PointSTL(frontLeft_bottom.x, frontLeft_bottom.y, frontLeft_bottom.z + largeurRallonge);

        PointSTL frontRight_bottom = new PointSTL(frontLeft_bottom.x, frontLeft_bottom.y + epaisseurPanneau, frontLeft_bottom.z);
        PointSTL frontRight_top = new PointSTL(frontRight_bottom.x, frontRight_bottom.y, frontRight_bottom.z + largeurRallonge);

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
        String filename = CaisseOutils.CHALCLT_FILE_NAME + ExportationMode.RETRAIT + "_R" + CaisseOutils.EXTENSTION;
        ContenuSTL contenu_STL = new ContenuSTL(filename, fileDestination);

        double epaisseurPanneau = getEpaisseur().pouces();
        double demiEpaisseur = epaisseurPanneau / 2;
        double angle = getAngle();
        double longueurRallonge = p_chaletDimmension.y.pouces();
        double largeurRallonge = Math.abs(Math.tan(Math.toRadians(angle)) * p_chaletDimmension.x.pouces());
        double ajustementAngle = Math.abs(Math.tan(Math.toRadians(angle)) * demiEpaisseur);

        PointSTL origin = new PointSTL(0, 0, 0);
        PointSTL backLeft_top = new PointSTL(origin.x, origin.y, origin.z + largeurRallonge);

        PointSTL backRight_bottom = new PointSTL(origin.x, origin.y + demiEpaisseur, origin.z);
        PointSTL backRight_top = new PointSTL(backRight_bottom.x, backRight_bottom.y, backRight_bottom.z + largeurRallonge);

        PointSTL originInnerLeft = new PointSTL(origin.x + demiEpaisseur, origin.y, origin.z);
        PointSTL backInnerLeft_top = new PointSTL(backLeft_top.x + demiEpaisseur, backLeft_top.y, backLeft_top.z - demiEpaisseur - ajustementAngle * 2);

        PointSTL backInnerRight_bottom = new PointSTL(backRight_bottom.x + demiEpaisseur, backRight_bottom.y, backRight_bottom.z);
        PointSTL backInnerRight_top = new PointSTL(backRight_top.x + demiEpaisseur, backRight_top.y, backRight_top.z - demiEpaisseur);

        PointSTL frontLeft_bottom = new PointSTL(origin.x + longueurRallonge, origin.y, origin.z);
        PointSTL frontLeft_top = new PointSTL(frontLeft_bottom.x, frontLeft_bottom.y, frontLeft_bottom.z + largeurRallonge);

        PointSTL frontRight_bottom = new PointSTL(frontLeft_bottom.x, frontLeft_bottom.y + demiEpaisseur, frontLeft_bottom.z);
        PointSTL frontRight_top = new PointSTL(frontRight_bottom.x, frontRight_bottom.y, frontRight_bottom.z + largeurRallonge);

        PointSTL frontInnerLeft_bottom = new PointSTL(frontLeft_bottom.x - demiEpaisseur, frontLeft_bottom.y, frontLeft_bottom.z);
        PointSTL frontInnerLeft_top = new PointSTL(frontLeft_top.x - demiEpaisseur, frontLeft_top.y, frontLeft_top.z - demiEpaisseur - ajustementAngle * 2);

        PointSTL frontInnerRight_bottom = new PointSTL(frontRight_bottom.x - demiEpaisseur, frontRight_bottom.y, frontRight_bottom.z);
        PointSTL frontInnerRight_top = new PointSTL(frontRight_top.x - demiEpaisseur, frontRight_top.y, frontRight_top.z - demiEpaisseur);

        PointSTL backExtra = new PointSTL(backRight_top.x, backRight_top.y + demiEpaisseur, backRight_top.z);
        PointSTL frontExtra = new PointSTL(frontRight_top.x, frontRight_top.y + demiEpaisseur, frontRight_top.z);

        PointSTL backBottomExtra = new PointSTL(backRight_top.x, backRight_top.y, backRight_top.z - ajustementAngle * 2);
        PointSTL frontBottomExtra = new PointSTL(frontRight_top.x, frontRight_top.y, frontRight_top.z - ajustementAngle * 2);

        contenu_STL.ajouterContenuSolide(new RectangleSTL(backRight_top, backExtra, frontExtra, frontRight_top));
        contenu_STL.ajouterContenuSolide(new TriangleSTL(backExtra, backRight_top, backBottomExtra));
        contenu_STL.ajouterContenuSolide(new TriangleSTL(frontExtra, frontRight_top, frontBottomExtra));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(backBottomExtra, frontBottomExtra, frontExtra, backExtra));


        contenu_STL.ajouterContenuSolide(new RectangleSTL(origin, backLeft_top, backRight_top, backRight_bottom));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(origin, originInnerLeft, backInnerLeft_top, backLeft_top));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(backInnerLeft_top, backLeft_top, frontLeft_top, frontInnerLeft_top));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(frontLeft_top, frontRight_top, backRight_top, backLeft_top));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(frontLeft_bottom, frontLeft_top, frontRight_top, frontRight_bottom));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(frontLeft_bottom, frontInnerLeft_bottom, frontInnerRight_bottom, frontRight_bottom));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(frontInnerLeft_bottom, frontInnerLeft_top, frontInnerRight_top, frontInnerRight_bottom));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(frontInnerLeft_bottom, frontInnerLeft_top, frontLeft_top, frontLeft_bottom));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(frontInnerRight_bottom, frontInnerRight_top, frontRight_top, frontRight_bottom));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(frontInnerRight_top, frontRight_top, backRight_top, backInnerRight_top));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(frontInnerLeft_top, frontInnerRight_top, backInnerRight_top, backInnerLeft_top));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(backInnerLeft_top, backInnerRight_top, backInnerRight_bottom, originInnerLeft));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(backInnerRight_top, backInnerRight_bottom, backRight_bottom, backRight_top));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(origin, originInnerLeft, backInnerRight_bottom, backRight_bottom));

        return contenu_STL;
    }

    @Override
    public ContenuSTL genererVersionFini(String fileDestination, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension) {

        String filename = CaisseOutils.CHALCLT_FILE_NAME + ExportationMode.FINI + "_R" + CaisseOutils.EXTENSTION;
        ContenuSTL contenu_STL = new ContenuSTL(filename, fileDestination);

        Triplet<Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>> faceExterne = genererTableauDePoint(p_chaletDimmension, false);

        Triplet<Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>> faceInterne = genererTableauDePoint(p_chaletDimmension, true);
        remplirContenuSTL(contenu_STL, faceExterne, faceInterne);

        return contenu_STL;
    }

    public void remplirContenuSTL(ContenuSTL contenuSTL, Triplet<Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>> faceExterne, Triplet<Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>> faceInterne) {

        Triplet<Double, Double, Double>[][] tableauDePoint3D_Ext = faceExterne.x;
        Triplet<Double, Double, Double>[][] tableauDePoint3D_Ext_Middle = faceExterne.y;

        Triplet<Double, Double, Double>[][] tableauDePoint3D_Int = faceInterne.x;
        Triplet<Double, Double, Double>[][] tableauDePoint3D_Int_Middle = faceInterne.y;

        linkPointSTL(contenuSTL, tableauDePoint3D_Ext, tableauDePoint3D_Ext_Middle);
        linkPointSTL(contenuSTL, tableauDePoint3D_Int, tableauDePoint3D_Int_Middle);

        contenuSTL.ajouterContenuSolide(new ContenuSTL.RectangleSTL(// small part Gauche
                new ContenuSTL.PointSTL(tableauDePoint3D_Ext_Middle[0][0].x, tableauDePoint3D_Ext_Middle[0][0].y, tableauDePoint3D_Ext_Middle[0][0].z), new ContenuSTL.PointSTL(tableauDePoint3D_Ext_Middle[1][0].x, tableauDePoint3D_Ext_Middle[1][0].y, tableauDePoint3D_Ext_Middle[1][0].z), new ContenuSTL.PointSTL(tableauDePoint3D_Int_Middle[1][0].x, tableauDePoint3D_Int_Middle[1][0].y, tableauDePoint3D_Int_Middle[1][0].z), new ContenuSTL.PointSTL(tableauDePoint3D_Int_Middle[0][0].x, tableauDePoint3D_Int_Middle[0][0].y, tableauDePoint3D_Int_Middle[0][0].z)));
        contenuSTL.ajouterContenuSolide(new ContenuSTL.RectangleSTL(// small part droite
                new ContenuSTL.PointSTL(tableauDePoint3D_Ext_Middle[0][1].x, tableauDePoint3D_Ext_Middle[0][1].y, tableauDePoint3D_Ext_Middle[0][1].z), new ContenuSTL.PointSTL(tableauDePoint3D_Ext_Middle[1][1].x, tableauDePoint3D_Ext_Middle[1][1].y, tableauDePoint3D_Ext_Middle[1][1].z), new ContenuSTL.PointSTL(tableauDePoint3D_Int_Middle[1][1].x, tableauDePoint3D_Int_Middle[1][1].y, tableauDePoint3D_Int_Middle[1][1].z), new ContenuSTL.PointSTL(tableauDePoint3D_Int_Middle[0][1].x, tableauDePoint3D_Int_Middle[0][1].y, tableauDePoint3D_Int_Middle[0][1].z)));
        contenuSTL.ajouterContenuSolide(new ContenuSTL.RectangleSTL(// small part haut
                new ContenuSTL.PointSTL(tableauDePoint3D_Ext_Middle[1][0].x, tableauDePoint3D_Ext_Middle[1][0].y, tableauDePoint3D_Ext_Middle[1][0].z), new ContenuSTL.PointSTL(tableauDePoint3D_Ext_Middle[1][1].x, tableauDePoint3D_Ext_Middle[1][1].y, tableauDePoint3D_Ext_Middle[1][1].z), new ContenuSTL.PointSTL(tableauDePoint3D_Int_Middle[1][1].x, tableauDePoint3D_Int_Middle[1][1].y, tableauDePoint3D_Int_Middle[1][1].z), new ContenuSTL.PointSTL(tableauDePoint3D_Int_Middle[1][0].x, tableauDePoint3D_Int_Middle[1][0].y, tableauDePoint3D_Int_Middle[1][0].z)));

    }

    private void linkPointSTL(ContenuSTL contenuSTL, Triplet<Double, Double, Double>[][] tableauDePoint3D, Triplet<Double, Double, Double>[][] tableauDePoint3D_Middle) {
        contenuSTL.ajouterContenuSolide(new ContenuSTL.RectangleSTL(// ext
                new ContenuSTL.PointSTL(tableauDePoint3D[0][0].x, tableauDePoint3D[0][0].y, tableauDePoint3D[0][0].z), new ContenuSTL.PointSTL(tableauDePoint3D[0][1].x, tableauDePoint3D[0][1].y, tableauDePoint3D[0][1].z), new ContenuSTL.PointSTL(tableauDePoint3D[1][1].x, tableauDePoint3D[1][1].y, tableauDePoint3D[1][1].z), new ContenuSTL.PointSTL(tableauDePoint3D[1][0].x, tableauDePoint3D[1][0].y, tableauDePoint3D[1][0].z)));
        contenuSTL.ajouterContenuSolide(new ContenuSTL.RectangleSTL( // ext dessous
                new ContenuSTL.PointSTL(tableauDePoint3D[0][0].x, tableauDePoint3D[0][0].y, tableauDePoint3D[0][0].z), new ContenuSTL.PointSTL(tableauDePoint3D_Middle[0][0].x, tableauDePoint3D_Middle[0][0].y, tableauDePoint3D_Middle[0][0].z), new ContenuSTL.PointSTL(tableauDePoint3D_Middle[0][1].x, tableauDePoint3D_Middle[0][1].y, tableauDePoint3D_Middle[0][1].z), new ContenuSTL.PointSTL(tableauDePoint3D[0][1].x, tableauDePoint3D[0][1].y, tableauDePoint3D[0][1].z)));
        contenuSTL.ajouterContenuSolide(new ContenuSTL.RectangleSTL( // ext gauche
                new ContenuSTL.PointSTL(tableauDePoint3D[0][0].x, tableauDePoint3D[0][0].y, tableauDePoint3D[0][0].z), new ContenuSTL.PointSTL(tableauDePoint3D[1][0].x, tableauDePoint3D[1][0].y, tableauDePoint3D[1][0].z), new ContenuSTL.PointSTL(tableauDePoint3D_Middle[1][0].x, tableauDePoint3D_Middle[1][0].y, tableauDePoint3D_Middle[1][0].z), new ContenuSTL.PointSTL(tableauDePoint3D_Middle[0][0].x, tableauDePoint3D_Middle[0][0].y, tableauDePoint3D_Middle[0][0].z)));

        contenuSTL.ajouterContenuSolide(new ContenuSTL.RectangleSTL( // ext droite
                new ContenuSTL.PointSTL(tableauDePoint3D[0][1].x, tableauDePoint3D[0][1].y, tableauDePoint3D[0][1].z), new ContenuSTL.PointSTL(tableauDePoint3D[1][1].x, tableauDePoint3D[1][1].y, tableauDePoint3D[1][1].z), new ContenuSTL.PointSTL(tableauDePoint3D_Middle[1][1].x, tableauDePoint3D_Middle[1][1].y, tableauDePoint3D_Middle[1][1].z), new ContenuSTL.PointSTL(tableauDePoint3D_Middle[0][1].x, tableauDePoint3D_Middle[0][1].y, tableauDePoint3D_Middle[0][1].z)));

        contenuSTL.ajouterContenuSolide(new ContenuSTL.RectangleSTL( // ext haut
                new ContenuSTL.PointSTL(tableauDePoint3D[1][0].x, tableauDePoint3D[1][0].y, tableauDePoint3D[1][0].z), new ContenuSTL.PointSTL(tableauDePoint3D[1][1].x, tableauDePoint3D[1][1].y, tableauDePoint3D[1][1].z), new ContenuSTL.PointSTL(tableauDePoint3D_Middle[1][1].x, tableauDePoint3D_Middle[1][1].y, tableauDePoint3D_Middle[1][1].z), new ContenuSTL.PointSTL(tableauDePoint3D_Middle[1][0].x, tableauDePoint3D_Middle[1][0].y, tableauDePoint3D_Middle[1][0].z)));

    }

    public Triplet<Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>> genererTableauDePoint(Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension, boolean faceInterieur) {
        double epaisseurPanneau = getEpaisseur().pouces();
        double demiEpaisseur = epaisseurPanneau / 2;
        double angle = getAngle();

        double hauteurChalet = p_chaletDimmension.z.pouces();
        double longueurRallonge = p_chaletDimmension.y.pouces();
        double largeurRallonge = Math.abs(Math.tan(Math.toRadians(angle)) * p_chaletDimmension.x.pouces());
        ;
        double retrait = Chalet.getRetraitSupplementaire().pouces() / 2;

        double ajustementAngle = Math.abs(Math.tan(Math.toRadians(angle)) * demiEpaisseur);
        Triplet<Double, Double, Double> pointOrigineMur3D;
        Tuple<Double, Double> pointOriginePlanDuMur2D;
        int murPlanXtranslator;


        /**** determination des parametres de calcul selon le cote ou se trouve le mur *****/

        longueurRallonge = longueurRallonge - (faceInterieur ? epaisseurPanneau : 0);
        largeurRallonge = faceInterieur ? largeurRallonge - ajustementAngle - demiEpaisseur - retrait : largeurRallonge;
        pointOrigineMur3D = new Triplet<>(0.0 + (faceInterieur ? epaisseurPanneau : 0), longueurRallonge + (faceInterieur ? demiEpaisseur : 0), hauteurChalet);
        // le systeme de point de coordonne des accessoires sera sur le plan (y,z)
        pointOriginePlanDuMur2D = new Tuple<>(pointOrigineMur3D.y, pointOrigineMur3D.z);
        murPlanXtranslator = -1;


        /****  tracage de lignes extrapolées des contoures des accessoires *****/

        // creation du tableau de points
        Tuple<Double, Double>[][] tableauDePointFaceExterne = new Tuple[2][2];


        tableauDePointFaceExterne[0][0] = new Tuple<>(pointOriginePlanDuMur2D.x, pointOriginePlanDuMur2D.y);
        tableauDePointFaceExterne[0][1] = new Tuple<>(pointOriginePlanDuMur2D.x + (murPlanXtranslator * longueurRallonge), pointOriginePlanDuMur2D.y);
        tableauDePointFaceExterne[1][1] = new Tuple<>(pointOriginePlanDuMur2D.x + (murPlanXtranslator * longueurRallonge), pointOriginePlanDuMur2D.y + largeurRallonge);
        tableauDePointFaceExterne[1][0] = new Tuple<>(pointOriginePlanDuMur2D.x, pointOriginePlanDuMur2D.y + largeurRallonge);


        /**** conversion des points 2D extraits en points 3D, le tout organisé et ordonné dans un tableau *****/
        Triplet<Double, Double, Double>[][] tableauDePoint3D_Ext = new Triplet[tableauDePointFaceExterne.length][tableauDePointFaceExterne[0].length];
        Triplet<Double, Double, Double>[][] tableauDePoint3D_Middle = new Triplet[tableauDePointFaceExterne.length][tableauDePointFaceExterne[0].length];

        demiEpaisseur = faceInterieur ? demiEpaisseur * -1 : demiEpaisseur;
        for (int i = 0; i < tableauDePointFaceExterne.length; i++)
            for (int j = 0; j < tableauDePointFaceExterne[0].length; j++) {
                // y,z
                Tuple<Double, Double> cas1Point2D = tableauDePointFaceExterne[i][j];
                double retraitSides = j == 0 ? retrait * -1 : retrait;
                double pointZ = cas1Point2D.y - (i == 1 ? ajustementAngle : 0);
                double PointY = cas1Point2D.x + (faceInterieur ? retraitSides : 0);
                if (faceInterieur) {
                    tableauDePoint3D_Ext[i][j] = new Triplet<>(pointOrigineMur3D.x, PointY, pointZ);
                    tableauDePoint3D_Middle[i][j] = new Triplet<>(pointOrigineMur3D.x + demiEpaisseur - retrait, PointY, cas1Point2D.y);
                } else {
                    tableauDePoint3D_Ext[i][j] = new Triplet<>(pointOrigineMur3D.x, PointY, cas1Point2D.y);
                    tableauDePoint3D_Middle[i][j] = new Triplet<>(pointOrigineMur3D.x + demiEpaisseur - retrait, PointY, pointZ);
                }
            }

        return new Triplet<>(tableauDePoint3D_Ext, tableauDePoint3D_Middle, pointOrigineMur3D);
    }
}