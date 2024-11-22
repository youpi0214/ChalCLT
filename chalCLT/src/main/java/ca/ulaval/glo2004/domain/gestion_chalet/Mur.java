package ca.ulaval.glo2004.domain.gestion_chalet;

import ca.ulaval.glo2004.domain.gestion_chalet.accessoires.Fenetre;
import ca.ulaval.glo2004.domain.gestion_chalet.accessoires.Porte;
import ca.ulaval.glo2004.services.*;
import ca.ulaval.glo2004.services.ContenuSTL.*;
import ca.ulaval.glo2004.services.CaisseOutils.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static ca.ulaval.glo2004.services.Orientations.*;

import static ca.ulaval.glo2004.services.Orientations.ARRIERE;
import static ca.ulaval.glo2004.services.Orientations.FACADE;

public class Mur extends Contour implements Serializable {

    public static final UniteImperiale ESPACEMENT_ACCESSOIRES_DEFAUT = new UniteImperiale(0, 3, 0, 1);  // 3"

    private List<Accessoire> accessoiresListes;

    private UniteImperiale espacementAccessoires;

    private transient Shape shapeVueDessus;

    private transient Hashtable<Orientations, Shape> shapeVueDebordement;
    private static int id_rainure = 1;

    private ConcurrentLinkedQueue<Accessoire> accessoiresInvalideListes;
    private Orientations coteVirtuel;
    private static int id_maker;

    /**
     * @param p_mur
     */
    public Mur(Mur p_mur) {
        this(p_mur.getLargeur(), p_mur.getLongueur(), p_mur.getEpaisseur(), p_mur.getEspacementAccessoires(), p_mur.getNbreCouches(), p_mur.getCote());

        p_mur.accessoiresListes.forEach(accessoire -> accessoiresListes.add(accessoire instanceof Porte ? new Porte((Porte) accessoire) : new Fenetre((Fenetre) accessoire)));
        retirerAccessoireDeLaListeInvalides(null);
        coteVirtuel = p_mur.coteVirtuel;

    }

    /**
     * @param p_largeur
     * @param p_longueur
     * @param p_epaisseur
     * @param p_nbreCouches
     * @param p_cote
     */
    public Mur(UniteImperiale p_largeur, UniteImperiale p_longueur, UniteImperiale p_epaisseur, UniteImperiale p_espacement, Couches p_nbreCouches, Orientations p_cote) {
        super(p_largeur, p_longueur, p_epaisseur, p_nbreCouches, p_cote);
        if (p_espacement.estValide() && p_espacement.pieds() >= ESPACEMENT_ACCESSOIRES_DEFAUT.pieds()) {
            this.accessoiresListes = new ArrayList<>();
            espacementAccessoires = p_espacement;
            accessoiresInvalideListes = new ConcurrentLinkedQueue<>();
            coteVirtuel = p_cote;
            preparerShapeVueDessus();
            shapeVueDebordement = new Hashtable<>();
        } else
            System.err.println("Espacement invalide lors de la creation du mur");
        ;

    }


    private void preparerShapeVueDessus() {
        if (getCote() == FACADE || getCote() == ARRIERE)
            shapeVueDessus = new Rectangle2D.Double(0, 0, getLongueur().pieds() * CaisseOutils.RATIO_TEST, getEpaisseur().pieds() * CaisseOutils.RATIO_TEST);
        else
            shapeVueDessus = new Rectangle2D.Double(0, 0, getEpaisseur().pieds() * CaisseOutils.RATIO_TEST, getLongueur().pieds() * CaisseOutils.RATIO_TEST);
    }

    protected static int getId_maker() {
        return ++id_maker;
    }

    public UniteImperiale getEspacementAccessoires() {
        return espacementAccessoires;
    }

    /**
     * @return une copie de la forme du mur
     */
    @Override
    public Shape getShape() {
        // return new Area(new Rectangle2D.Double(shape.getBounds().getX(), shape.getBounds().getY(), getLongueur().pieds() * CaisseOutils.RATIO_TEST, getLargeur().pieds() * CaisseOutils.RATIO_TEST));
        return new Rectangle2D.Double(shape.getBounds().getX(), shape.getBounds().getY(), shape.getBounds().getWidth(), shape.getBounds().getHeight());
    }

    public Shape getOriginalShape() {
        return new Area(new Rectangle2D.Double(shape.getBounds().getX(), shape.getBounds().getY(), getLongueur().pieds() * CaisseOutils.RATIO_TEST, getLargeur().pieds() * CaisseOutils.RATIO_TEST));
    }

    protected void updateShape() {
        this.setShape(new Area(new Rectangle2D.Double(shape.getBounds().getX(), shape.getBounds().getY(), getLongueur().pieds() * CaisseOutils.RATIO_TEST, getLargeur().pieds() * CaisseOutils.RATIO_TEST)));
    }

    protected void updateShape(Shape shape) {
        this.setShape(shape);
    }


    public Shape getShapeVueDessus() {
        return shapeVueDessus;
    }

    @Override
    protected void updateShapeDebordement(Orientations p_orientations, Shape shape) {
        shapeVueDebordement.put(p_orientations, shape);
    }

    @Override
    public Hashtable<Orientations, Shape> getShapeDebordement() {
        return (Hashtable<Orientations, Shape>) shapeVueDebordement.clone();
    }

    protected void updateShapeVueDessus(Shape p_shape) {
        shapeVueDessus = p_shape;
    }

    protected void updateCoteVirtuel(Orientations p_coteVirtuel) {
        this.coteVirtuel = p_coteVirtuel;
    }

    @Override
    public ContenuSTL genererVersionBrut(String fileDestination, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension) {
        String filename = CaisseOutils.CHALCLT_FILE_NAME + ExportationMode.BRUT + "_" + getCote().orientation.charAt(0) + CaisseOutils.EXTENSTION;
        ContenuSTL contenu_STL = new ContenuSTL(filename, fileDestination);
        RectangleSTL rectangleFace = new RectangleSTL(new PointSTL(0, 0, 0), new PointSTL(0, 0, getLargeur().pouces()), new PointSTL(getLongueur().pouces(), 0, getLargeur().pouces()), new PointSTL(getLongueur().pouces(), 0, 0));
        RectangleSTL rectangleArriere = new RectangleSTL(new PointSTL(0, getEpaisseur().pouces(), 0), new PointSTL(getLongueur().pouces(), getEpaisseur().pouces(), 0), new PointSTL(getLongueur().pouces(), getEpaisseur().pouces(), getLargeur().pouces()), new PointSTL(0, getEpaisseur().pouces(), getLargeur().pouces()));
        RectangleSTL rectangleGauche = new RectangleSTL(new PointSTL(0, 0, 0), new PointSTL(0, 0, getLargeur().pouces()), new PointSTL(0, getEpaisseur().pouces(), getLargeur().pouces()), new PointSTL(0, getEpaisseur().pouces(), 0));
        RectangleSTL rectangleDroite = new RectangleSTL(new PointSTL(getLongueur().pouces(), 0, 0), new PointSTL(getLongueur().pouces(), getEpaisseur().pouces(), 0), new PointSTL(getLongueur().pouces(), getEpaisseur().pouces(), getLargeur().pouces()), new PointSTL(getLongueur().pouces(), 0, getLargeur().pouces()));
        RectangleSTL rectangleDessus = new RectangleSTL(new PointSTL(0, 0, getLargeur().pouces()), new PointSTL(getLongueur().pouces(), 0, getLargeur().pouces()), new PointSTL(getLongueur().pouces(), getEpaisseur().pouces(), getLargeur().pouces()), new PointSTL(0, getEpaisseur().pouces(), getLargeur().pouces()));
        RectangleSTL rectangleBase = new RectangleSTL(new PointSTL(0, 0, 0), new PointSTL(getLongueur().pouces(), 0, 0), new PointSTL(getLongueur().pouces(), getEpaisseur().pouces(), 0), new PointSTL(0, getEpaisseur().pouces(), 0));
        contenu_STL.ajouterContenuSolide(rectangleBase, rectangleFace, rectangleArriere, rectangleDessus, rectangleDroite, rectangleGauche);
        return contenu_STL;
    }


    @Override
    public ContenuSTL genererVersionRetrait(String fileDestination, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension, int nbrAccessoire) {
        // une rainure
        float espacementRetrait = Chalet.getRetraitSupplementaire().pouces();
        String contenuSTL = ContenuSTL.SOLID;
        float coteRainure = getEpaisseur().pouces() / 2;
        String filename = CaisseOutils.CHALCLT_FILE_NAME + ExportationMode.RETRAIT + "_" + getCote().orientation.charAt(0) + "_" + id_rainure + CaisseOutils.EXTENSTION;
        ContenuSTL contenu_STL = new ContenuSTL(filename, fileDestination);
        RectangleSTL rectangleFace = new RectangleSTL(new PointSTL(0, 0, 0), new PointSTL(0, 0, getLargeur().pouces()), new PointSTL(coteRainure + espacementRetrait / 2, 0, getLargeur().pouces()), new PointSTL(coteRainure + espacementRetrait / 2, 0, 0));
        RectangleSTL rectangleArriere = new RectangleSTL(new PointSTL(0, coteRainure + espacementRetrait / 2, 0), new PointSTL(coteRainure + espacementRetrait / 2, coteRainure + espacementRetrait / 2, 0), new PointSTL(coteRainure + espacementRetrait / 2, coteRainure + espacementRetrait / 2, getLargeur().pouces()), new PointSTL(0, coteRainure + espacementRetrait / 2, getLargeur().pouces()));
        RectangleSTL rectangleGauche = new RectangleSTL(new PointSTL(0, 0, 0), new PointSTL(0, 0, getLargeur().pouces()), new PointSTL(0, coteRainure + espacementRetrait / 2, getLargeur().pouces()), new PointSTL(0, coteRainure + espacementRetrait / 2, 0));
        RectangleSTL rectangleDroite = new RectangleSTL(new PointSTL(coteRainure + espacementRetrait / 2, 0, 0), new PointSTL(coteRainure + espacementRetrait / 2, coteRainure + espacementRetrait / 2, 0), new PointSTL(coteRainure + espacementRetrait / 2, coteRainure + espacementRetrait / 2, getLargeur().pouces()), new PointSTL(coteRainure + espacementRetrait / 2, 0, getLargeur().pouces()));
        RectangleSTL rectangleDessus = new RectangleSTL(new PointSTL(0, 0, getLargeur().pouces()), new PointSTL(coteRainure + espacementRetrait / 2, 0, getLargeur().pouces()), new PointSTL(coteRainure + espacementRetrait / 2, coteRainure + espacementRetrait / 2, getLargeur().pouces()), new PointSTL(0, coteRainure + espacementRetrait / 2, getLargeur().pouces()));
        RectangleSTL rectangleBase = new RectangleSTL(new PointSTL(0, 0, 0), new PointSTL(coteRainure + espacementRetrait / 2, 0, 0), new PointSTL(coteRainure + espacementRetrait / 2, coteRainure + espacementRetrait / 2, 0), new PointSTL(0, coteRainure + espacementRetrait / 2, 0));
        contenu_STL.ajouterContenuSolide(rectangleBase, rectangleFace, rectangleArriere, rectangleDessus, rectangleDroite, rectangleGauche);
        id_rainure++;
        if (id_rainure == 3) {
            id_rainure = 1;
        }
        return contenu_STL;

    }

    /**
     * @param fileDestination    largeur,longueur, hauteur (largeur is on the x axis, longueur on the y axis and hauteur on the z axis)
     * @param p_chaletDimmension
     * @return
     */
    @Override
    public ContenuSTL genererVersionFini(String fileDestination, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension) {
        String filename = CaisseOutils.CHALCLT_FILE_NAME + ExportationMode.FINI + "_" + getCote().orientation.charAt(0) + CaisseOutils.EXTENSTION;
        ContenuSTL contenu_STL = new ContenuSTL(filename, fileDestination);
        ArrayList<Triplet<Double, Double, Point2D>> accessoireInfoListe = new ArrayList<>();

        // rassemble les information des accessoires
        accessoiresListes.forEach(accessoire -> {
            double coordYAjuste = p_chaletDimmension.z.pouces() - (accessoire.getCoordonneesEmplacement()[1].pouces() + accessoire.getLargeur().pouces());
            Point2D coordonnees = new Point2D.Double(accessoire.getCoordonneesEmplacement()[0].pouces(), coordYAjuste);
            accessoireInfoListe.add(new Triplet<>((double) accessoire.getLongueur().pouces(), (double) accessoire.getLargeur().pouces(), coordonnees));
        });

        Tuple<Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>>
                faceExterne = genererTableauDePoint(getCoteVirtuel(), accessoireInfoListe, p_chaletDimmension, false);
        remplirContenuSTL(contenu_STL, accessoireInfoListe, faceExterne.x, getCoteVirtuel(), faceExterne.y, p_chaletDimmension, true);

        Tuple<Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>>
                faceInterne = genererTableauDePoint(getCoteVirtuel(), accessoireInfoListe, p_chaletDimmension, true);
        remplirContenuSTL(contenu_STL, accessoireInfoListe, faceInterne.x, getCoteVirtuel(), faceInterne.y, p_chaletDimmension, false);
        return contenu_STL;
    }


    public void remplirContenuSTL(ContenuSTL contenuSTL, ArrayList<Triplet<Double, Double, Point2D>> accessoireListe,
                                  Triplet<Double, Double, Double>[][] tableauDePoint3D, Orientations face,
                                  Triplet<Double, Double, Double> pointOrigineMur3D, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension, boolean outerWall) {

        /* p_chaletDimmension := largeur,longueur, hauteur (largeur is on the x axis, longueur on the y axis and hauteur on the z axis*/
        float espacementRetrait = Chalet.getRetraitSupplementaire().pouces() / 2;

        Hashtable<Triplet<Double, Double, Point2D>, Boolean> accessoireListeATracer = new Hashtable<>();
        accessoireListe.forEach(accessoire -> accessoireListeATracer.put(accessoire, false));
        boolean boundaryDone = false;

        for (int i = 0; i < tableauDePoint3D.length - 1; i++) {
            for (int j = 0; j < tableauDePoint3D[0].length - 1; j++) {
                switch (face) {
                    // plan y,z
                    case FACADE:
                    case ARRIERE:
                        espacementRetrait = (j == 0 || j == (tableauDePoint3D[0].length - 1)) ? Chalet.getRetraitSupplementaire().pouces() / 2 : 0;
                        double espacementRetraitGauche = espacementRetrait;
                        double espacementRetraitDroit = espacementRetrait * -1;
                        Point2D p1Cas1 = new Point2D.Double(tableauDePoint3D[i][j].y, tableauDePoint3D[i][j].z);
                        Point2D p2Cas1 = new Point2D.Double(tableauDePoint3D[i][j + 1].y, tableauDePoint3D[i][j + 1].z);
                        Point2D p3Cas1 = new Point2D.Double(tableauDePoint3D[i + 1][j + 1].y, tableauDePoint3D[i + 1][j + 1].z);
                        Point2D p4Cas1 = new Point2D.Double(tableauDePoint3D[i + 1][j].y, tableauDePoint3D[i + 1][j].z);
                        Tuple<Boolean, Triplet<Double, Double, Point2D>> presenceDaccessoireAA = checkPointTousSurAccessoire(accessoireListe, pointOrigineMur3D.y, face, p1Cas1, p2Cas1, p3Cas1, p4Cas1);

                        if (!presenceDaccessoireAA.x) {// dessine la face interne et externe du mur
                            espacementRetraitGauche = !outerWall ? espacementRetraitGauche : 0;
                            espacementRetraitDroit = !outerWall ? espacementRetraitDroit : 0;

                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(tableauDePoint3D[i][j].x, p1Cas1.getX() + espacementRetraitGauche, p1Cas1.getY()),
                                    new PointSTL(tableauDePoint3D[i][j + 1].x, p2Cas1.getX() + espacementRetraitDroit, p2Cas1.getY()),
                                    new PointSTL(tableauDePoint3D[i + 1][j + 1].x, p3Cas1.getX() + espacementRetraitDroit, p3Cas1.getY()),
                                    new PointSTL(tableauDePoint3D[i + 1][j].x, p4Cas1.getX() + espacementRetraitGauche, p4Cas1.getY())));
                        } else if (outerWall && presenceDaccessoireAA.y != null && !accessoireListeATracer.get(presenceDaccessoireAA.y)) { // dessine les rectangles internes de l'accessoire
                            double addition = face.equals(ARRIERE) ? getEpaisseur().pouces() : -1 * getEpaisseur().pouces();
                            double longueurAcc = presenceDaccessoireAA.y.x;
                            double largeurAcc = presenceDaccessoireAA.y.y;

                            contenuSTL.ajouterContenuSolide(new RectangleSTL(// bas
                                    new PointSTL(tableauDePoint3D[i][j].x, p1Cas1.getX(), p1Cas1.getY()),
                                    new PointSTL(tableauDePoint3D[i + 1][j].x, p1Cas1.getX() + longueurAcc, p1Cas1.getY()),
                                    new PointSTL(tableauDePoint3D[i + 1][j].x + addition, p1Cas1.getX() + longueurAcc, p1Cas1.getY()),
                                    new PointSTL(tableauDePoint3D[i][j].x + addition, p1Cas1.getX(), p1Cas1.getY())));
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(   // droite
                                    new PointSTL(tableauDePoint3D[i][j + 1].x, p1Cas1.getX() + longueurAcc, p1Cas1.getY()),
                                    new PointSTL(tableauDePoint3D[i + 1][j + 1].x, p1Cas1.getX() + longueurAcc, p1Cas1.getY() + largeurAcc),
                                    new PointSTL(tableauDePoint3D[i + 1][j + 1].x + addition, p1Cas1.getX() + longueurAcc, p1Cas1.getY() + largeurAcc),
                                    new PointSTL(tableauDePoint3D[i][j + 1].x + addition, p1Cas1.getX() + longueurAcc, p1Cas1.getY())));
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(// haut
                                    new PointSTL(tableauDePoint3D[i + 1][j + 1].x, p1Cas1.getX() + longueurAcc, p1Cas1.getY() + largeurAcc),
                                    new PointSTL(tableauDePoint3D[i + 1][j].x, p1Cas1.getX(), p1Cas1.getY() + largeurAcc),
                                    new PointSTL(tableauDePoint3D[i + 1][j].x + addition, p1Cas1.getX(), p1Cas1.getY() + largeurAcc),
                                    new PointSTL(tableauDePoint3D[i + 1][j + 1].x + addition, p1Cas1.getX() + longueurAcc, p1Cas1.getY() + largeurAcc)));
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(   // gauche
                                    new PointSTL(tableauDePoint3D[i + 1][j].x, p1Cas1.getX(), p1Cas1.getY() + largeurAcc),
                                    new PointSTL(tableauDePoint3D[i][j].x, p1Cas1.getX(), p1Cas1.getY()),
                                    new PointSTL(tableauDePoint3D[i][j].x + addition, p1Cas1.getX(), p1Cas1.getY()),
                                    new PointSTL(tableauDePoint3D[i + 1][j].x + addition, p1Cas1.getX(), p1Cas1.getY() + largeurAcc)));
                            accessoireListeATracer.put(presenceDaccessoireAA.y, true);
                        }
                        if (outerWall && !boundaryDone) { // dessine le contour du mur
                            double demiEpaisseur = face.equals(ARRIERE) ? getEpaisseur().pouces() / 2 : -1 * getEpaisseur().pouces() / 2;
                            double longueurMur = p_chaletDimmension.y.pouces();
                            double largeurMur = p_chaletDimmension.z.pouces();
                            espacementRetrait = face.equals(FACADE) ? espacementRetrait : espacementRetrait * -1;

                            Triplet<Double, Double, Double> originPoint = tableauDePoint3D[0][0];
                            Triplet<Double, Double, Double> point2 = new Triplet<>(originPoint.x, originPoint.y + longueurMur, originPoint.z);
                            Triplet<Double, Double, Double> point3 = new Triplet<>(originPoint.x + demiEpaisseur + espacementRetrait, originPoint.y + longueurMur, originPoint.z);
                            Triplet<Double, Double, Double> point4 = new Triplet<>(originPoint.x + demiEpaisseur + espacementRetrait, originPoint.y, originPoint.z);
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x, originPoint.y, originPoint.z),
                                    new PointSTL(point2.x, point2.y, point2.z),
                                    new PointSTL(point3.x, point3.y, point3.z),
                                    new PointSTL(point4.x, point4.y, point4.z)));
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x, originPoint.y, originPoint.z + largeurMur),
                                    new PointSTL(point2.x, point2.y, point2.z + largeurMur),
                                    new PointSTL(point3.x, point3.y, point3.z + largeurMur),
                                    new PointSTL(point4.x, point4.y, point4.z + largeurMur)));
                            point2 = new Triplet<>(originPoint.x, originPoint.y, originPoint.z + largeurMur);
                            point3 = new Triplet<>(originPoint.x + demiEpaisseur + espacementRetrait, originPoint.y, originPoint.z + largeurMur);
                            point4 = new Triplet<>(originPoint.x + demiEpaisseur + espacementRetrait, originPoint.y, originPoint.z);
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x, originPoint.y, originPoint.z),
                                    new PointSTL(point2.x, point2.y, point2.z),
                                    new PointSTL(point3.x, point3.y, point3.z),
                                    new PointSTL(point4.x, point4.y, point4.z)));
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x, originPoint.y + longueurMur, originPoint.z),
                                    new PointSTL(point2.x, point2.y + longueurMur, point2.z),
                                    new PointSTL(point3.x, point3.y + longueurMur, point3.z),
                                    new PointSTL(point4.x, point4.y + longueurMur, point4.z)));
                            boundaryDone = true;
                        } else if (!outerWall && !boundaryDone) {
                            double demiEpaisseur = face.equals(ARRIERE) ? getEpaisseur().pouces() / 2 : -1 * getEpaisseur().pouces() / 2;
                            double longueurMur = p_chaletDimmension.y.pouces() - getEpaisseur().pouces() / 2 * 3;
                            double largeurMur = p_chaletDimmension.z.pouces();
                            double epaisseur = getEpaisseur().pouces();
                            double murXajustement = face.equals(ARRIERE) ? -1 * getEpaisseur().pouces() / 2 : getEpaisseur().pouces() / 2;
                            espacementRetrait = face.equals(FACADE) ? espacementRetrait : espacementRetrait * -1;

                            Triplet<Double, Double, Double> originPoint = new Triplet<>(tableauDePoint3D[0][0].x + murXajustement, tableauDePoint3D[0][0].y, tableauDePoint3D[0][0].z);
                            Triplet<Double, Double, Double> point2 = new Triplet<>(originPoint.x, originPoint.y + longueurMur + epaisseur / 2, originPoint.z);
                            Triplet<Double, Double, Double> point3 = new Triplet<>(originPoint.x + demiEpaisseur, originPoint.y + longueurMur + epaisseur / 2, originPoint.z);
                            Triplet<Double, Double, Double> point4 = new Triplet<>(originPoint.x + demiEpaisseur, originPoint.y, originPoint.z);
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x + espacementRetrait, originPoint.y + espacementRetraitGauche, originPoint.z),
                                    new PointSTL(point2.x + espacementRetrait, point2.y + espacementRetraitDroit, point2.z),
                                    new PointSTL(point3.x, point3.y + espacementRetraitDroit, point3.z),
                                    new PointSTL(point4.x, point4.y + espacementRetraitGauche, point4.z)));
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x + espacementRetrait, originPoint.y + espacementRetraitGauche, originPoint.z + largeurMur),
                                    new PointSTL(point2.x + espacementRetrait, point2.y + espacementRetraitDroit, point2.z + largeurMur),
                                    new PointSTL(point3.x, point3.y + espacementRetraitDroit, point3.z + largeurMur),
                                    new PointSTL(point4.x, point4.y + espacementRetraitGauche, point4.z + largeurMur)));
                            point2 = new Triplet<>(originPoint.x, originPoint.y, originPoint.z + largeurMur);
                            point3 = new Triplet<>(originPoint.x + demiEpaisseur, originPoint.y, originPoint.z + largeurMur);
                            point4 = new Triplet<>(originPoint.x + demiEpaisseur, originPoint.y, originPoint.z);
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x + espacementRetrait, originPoint.y + espacementRetraitGauche, originPoint.z),
                                    new PointSTL(point2.x + espacementRetrait, point2.y + espacementRetraitGauche, point2.z),
                                    new PointSTL(point3.x, point3.y + espacementRetraitGauche, point3.z),
                                    new PointSTL(point4.x, point4.y + espacementRetraitGauche, point4.z)));
                            double translate = face.equals(ARRIERE) ? demiEpaisseur : -1 * demiEpaisseur;
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x + espacementRetrait, originPoint.y + espacementRetraitDroit + longueurMur + translate, originPoint.z),
                                    new PointSTL(point2.x + espacementRetrait, point2.y + espacementRetraitDroit + longueurMur + translate, point2.z),
                                    new PointSTL(point3.x, point3.y + espacementRetraitDroit + longueurMur + translate, point3.z),
                                    new PointSTL(point4.x, point4.y + espacementRetraitDroit + longueurMur + translate, point4.z)));
                            boundaryDone = true;
                            demiEpaisseur = -1 * getEpaisseur().pouces() / 2;
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x + espacementRetrait, originPoint.y + espacementRetraitGauche, originPoint.z),
                                    new PointSTL(originPoint.x + espacementRetrait, originPoint.y + demiEpaisseur, originPoint.z),
                                    new PointSTL(originPoint.x + espacementRetrait, originPoint.y + demiEpaisseur, originPoint.z + largeurMur),
                                    new PointSTL(originPoint.x + espacementRetrait, originPoint.y + espacementRetraitGauche, originPoint.z + largeurMur)));
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x + espacementRetrait, originPoint.y + longueurMur + epaisseur, originPoint.z),
                                    new PointSTL(originPoint.x + espacementRetrait, originPoint.y + espacementRetraitDroit + demiEpaisseur + longueurMur + epaisseur, originPoint.z),
                                    new PointSTL(originPoint.x + espacementRetrait, originPoint.y + espacementRetraitDroit + demiEpaisseur + longueurMur + epaisseur, originPoint.z + largeurMur),
                                    new PointSTL(originPoint.x + espacementRetrait, originPoint.y + longueurMur + epaisseur, originPoint.z + largeurMur)));
                        }
                        break;
                    // plan x,z
                    case GAUCHE:
                    case DROITE:
                        espacementRetrait = (j == 0 || j == (tableauDePoint3D[0].length - 1)) ? Chalet.getRetraitSupplementaire().pouces() / 2 : 0;
                        espacementRetraitGauche = espacementRetrait;
                        espacementRetraitDroit = espacementRetrait * -1;
                        Point2D p1Cas2 = new Point2D.Double(tableauDePoint3D[i][j + 1].x, tableauDePoint3D[i][j + 1].z);
                        Point2D p2Cas2 = new Point2D.Double(tableauDePoint3D[i][j].x, tableauDePoint3D[i][j].z);
                        Point2D p3Cas2 = new Point2D.Double(tableauDePoint3D[i + 1][j].x, tableauDePoint3D[i + 1][j].z);
                        Point2D p4Cas2 = new Point2D.Double(tableauDePoint3D[i + 1][j + 1].x, tableauDePoint3D[i + 1][j + 1].z);
                        Tuple<Boolean, Triplet<Double, Double, Point2D>> presenceDaccessoireGD = checkPointTousSurAccessoire(accessoireListe, pointOrigineMur3D.x, face, p1Cas2, p2Cas2, p3Cas2, p4Cas2);
                        if (!presenceDaccessoireGD.x) {
                            double espacementRetraitGauche1 = !outerWall ? espacementRetraitGauche * 2 : espacementRetraitGauche;
                            double espacementRetraitDroit1 = !outerWall ? espacementRetraitDroit * 2 : espacementRetraitDroit;
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(p1Cas2.getX() + espacementRetraitDroit1, tableauDePoint3D[i][j + 1].y, p1Cas2.getY()),
                                    new PointSTL(p2Cas2.getX() + espacementRetraitGauche1, tableauDePoint3D[i][j].y, p2Cas2.getY()),
                                    new PointSTL(p3Cas2.getX() + espacementRetraitGauche1, tableauDePoint3D[i + 1][j].y, p3Cas2.getY()),
                                    new PointSTL(p4Cas2.getX() + espacementRetraitDroit1, tableauDePoint3D[i + 1][j + 1].y, p4Cas2.getY())));
                        } else if (outerWall && presenceDaccessoireGD.y != null && !accessoireListeATracer.get(presenceDaccessoireGD.y)) { // dessine les rectangles internes de l'accessoire
                            double addition = face.equals(GAUCHE) ? getEpaisseur().pouces() : -1 * getEpaisseur().pouces();
                            double longueurAcc = presenceDaccessoireGD.y.x;
                            double largeurAcc = presenceDaccessoireGD.y.y;

                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(p2Cas2.getX(), tableauDePoint3D[i][j].y, p2Cas2.getY()),
                                    new PointSTL(p2Cas2.getX() + longueurAcc, tableauDePoint3D[i + 1][j].y, p2Cas2.getY()),
                                    new PointSTL(p2Cas2.getX() + longueurAcc, tableauDePoint3D[i + 1][j].y + addition, p2Cas2.getY()),
                                    new PointSTL(p2Cas2.getX(), tableauDePoint3D[i][j].y + addition, p2Cas2.getY())));
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(p2Cas2.getX() + longueurAcc, tableauDePoint3D[i][j + 1].y, p2Cas2.getY()),
                                    new PointSTL(p2Cas2.getX() + longueurAcc, tableauDePoint3D[i + 1][j + 1].y, p2Cas2.getY() + largeurAcc),
                                    new PointSTL(p2Cas2.getX() + longueurAcc, tableauDePoint3D[i + 1][j + 1].y + addition, p2Cas2.getY() + largeurAcc),
                                    new PointSTL(p2Cas2.getX() + longueurAcc, tableauDePoint3D[i][j + 1].y + addition, p2Cas2.getY())));
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(p2Cas2.getX() + longueurAcc, tableauDePoint3D[i + 1][j + 1].y, p2Cas2.getY() + largeurAcc),
                                    new PointSTL(p2Cas2.getX(), tableauDePoint3D[i + 1][j].y, p2Cas2.getY() + largeurAcc),
                                    new PointSTL(p2Cas2.getX(), tableauDePoint3D[i + 1][j].y + addition, p2Cas2.getY() + largeurAcc),
                                    new PointSTL(p2Cas2.getX() + longueurAcc, tableauDePoint3D[i + 1][j + 1].y + addition, p2Cas2.getY() + largeurAcc)));
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(p2Cas2.getX(), tableauDePoint3D[i + 1][j].y, p2Cas2.getY() + largeurAcc),
                                    new PointSTL(p2Cas2.getX(), tableauDePoint3D[i][j].y, p2Cas2.getY()),
                                    new PointSTL(p2Cas2.getX(), tableauDePoint3D[i][j].y + addition, p2Cas2.getY()),
                                    new PointSTL(p2Cas2.getX(), tableauDePoint3D[i + 1][j].y + addition, p2Cas2.getY() + largeurAcc)));
                            accessoireListeATracer.put(presenceDaccessoireGD.y, true);
                        }
                        if (outerWall && !boundaryDone) { // dessine le contour du mur
                            double demiEpaisseur = face.equals(GAUCHE) ? getEpaisseur().pouces() / 2 : -1 * getEpaisseur().pouces() / 2;
                            double longueurMur = p_chaletDimmension.x.pouces();
                            double largeurMur = p_chaletDimmension.z.pouces();
                            espacementRetrait = face.equals(GAUCHE) ? espacementRetrait * -1 : espacementRetrait;

                            Triplet<Double, Double, Double> originPoint = new Triplet<>(tableauDePoint3D[0][0].x, tableauDePoint3D[0][0].y, tableauDePoint3D[0][0].z);
                            Triplet<Double, Double, Double> point2 = new Triplet<>(originPoint.x + longueurMur, originPoint.y, originPoint.z);
                            Triplet<Double, Double, Double> point3 = new Triplet<>(originPoint.x + longueurMur, originPoint.y + espacementRetrait + demiEpaisseur, originPoint.z);
                            Triplet<Double, Double, Double> point4 = new Triplet<>(originPoint.x, originPoint.y + espacementRetrait + demiEpaisseur, originPoint.z);
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x + espacementRetraitGauche, originPoint.y, originPoint.z),
                                    new PointSTL(point2.x + espacementRetraitDroit, point2.y, point2.z),
                                    new PointSTL(point3.x + espacementRetraitDroit, point3.y, point3.z),
                                    new PointSTL(point4.x + espacementRetraitGauche, point4.y, point4.z)));
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x + espacementRetraitGauche, originPoint.y, originPoint.z + largeurMur),
                                    new PointSTL(point2.x + espacementRetraitDroit, point2.y, point2.z + largeurMur),
                                    new PointSTL(point3.x + espacementRetraitDroit, point3.y, point3.z + largeurMur),
                                    new PointSTL(point4.x + espacementRetraitGauche, point4.y, point4.z + largeurMur)));
                            point2 = new Triplet<>(originPoint.x, originPoint.y, originPoint.z + largeurMur);
                            point3 = new Triplet<>(originPoint.x, originPoint.y + demiEpaisseur + espacementRetrait, originPoint.z + largeurMur);
                            point4 = new Triplet<>(originPoint.x, originPoint.y + demiEpaisseur + espacementRetrait, originPoint.z);
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x + espacementRetraitGauche, originPoint.y, originPoint.z),
                                    new PointSTL(point2.x + espacementRetraitGauche, point2.y, point2.z),
                                    new PointSTL(point3.x + espacementRetraitGauche, point3.y, point3.z),
                                    new PointSTL(point4.x + espacementRetraitGauche, point4.y, point4.z)));
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x + longueurMur + espacementRetraitDroit, originPoint.y, originPoint.z),
                                    new PointSTL(point2.x + longueurMur + espacementRetraitDroit, point2.y, point2.z),
                                    new PointSTL(point3.x + longueurMur + espacementRetraitDroit, point3.y, point3.z),
                                    new PointSTL(point4.x + longueurMur + espacementRetraitDroit, point4.y, point4.z)));
                            boundaryDone = true;
                        } else if (!outerWall && !boundaryDone) {
                            double demiEpaisseur = face.equals(GAUCHE) ? getEpaisseur().pouces() / 2 : -1 * getEpaisseur().pouces() / 2;
                            double longueurMur = p_chaletDimmension.x.pouces() - getEpaisseur().pouces();
                            double largeurMur = p_chaletDimmension.z.pouces();
                            espacementRetrait = face.equals(GAUCHE) ? espacementRetrait * -1 : espacementRetrait;

                            Triplet<Double, Double, Double> originPoint = new Triplet<>(tableauDePoint3D[0][0].x, tableauDePoint3D[0][0].y - demiEpaisseur, tableauDePoint3D[0][0].z);
                            Triplet<Double, Double, Double> point2 = new Triplet<>(originPoint.x + longueurMur, originPoint.y, originPoint.z);
                            Triplet<Double, Double, Double> point3 = new Triplet<>(originPoint.x + longueurMur, originPoint.y + demiEpaisseur, originPoint.z);
                            Triplet<Double, Double, Double> point4 = new Triplet<>(originPoint.x, originPoint.y + demiEpaisseur, originPoint.z);
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x + espacementRetraitGauche * 2, originPoint.y + espacementRetrait, originPoint.z),
                                    new PointSTL(point2.x + espacementRetraitDroit * 2, point2.y + espacementRetrait, point2.z),
                                    new PointSTL(point3.x + espacementRetraitDroit * 2, point3.y, point3.z),
                                    new PointSTL(point4.x + espacementRetraitGauche * 2, point4.y, point4.z)));
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x + espacementRetraitGauche * 2, originPoint.y + espacementRetrait, originPoint.z + largeurMur),
                                    new PointSTL(point2.x + espacementRetraitDroit * 2, point2.y + espacementRetrait, point2.z + largeurMur),
                                    new PointSTL(point3.x + espacementRetraitDroit * 2, point3.y, point3.z + largeurMur),
                                    new PointSTL(point4.x + espacementRetraitGauche * 2, point4.y, point4.z + largeurMur)));
                            point2 = new Triplet<>(originPoint.x, originPoint.y, originPoint.z + largeurMur);
                            point3 = new Triplet<>(originPoint.x, originPoint.y + demiEpaisseur, originPoint.z + largeurMur);
                            point4 = new Triplet<>(originPoint.x, originPoint.y + demiEpaisseur, originPoint.z);
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x + espacementRetraitGauche * 2, originPoint.y + espacementRetrait, originPoint.z),
                                    new PointSTL(point2.x + espacementRetraitGauche * 2, point2.y + espacementRetrait, point2.z),
                                    new PointSTL(point3.x + espacementRetraitGauche * 2, point3.y, point3.z),
                                    new PointSTL(point4.x + espacementRetraitGauche * 2, point4.y, point4.z)));
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x + espacementRetraitDroit * 2 + longueurMur, originPoint.y + espacementRetrait, originPoint.z),
                                    new PointSTL(point2.x + espacementRetraitDroit * 2 + longueurMur, point2.y + espacementRetrait, point2.z),
                                    new PointSTL(point3.x + espacementRetraitDroit * 2 + longueurMur, point3.y, point3.z),
                                    new PointSTL(point4.x + espacementRetraitDroit * 2 + longueurMur, point4.y, point4.z)));
                            boundaryDone = true;
                            demiEpaisseur = -1 * getEpaisseur().pouces() / 2;

                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x + espacementRetraitGauche * 2, originPoint.y + espacementRetrait, originPoint.z),
                                    new PointSTL(originPoint.x + demiEpaisseur + espacementRetraitGauche, originPoint.y + espacementRetrait, originPoint.z),
                                    new PointSTL(originPoint.x + demiEpaisseur + espacementRetraitGauche, originPoint.y + espacementRetrait, originPoint.z + largeurMur),
                                    new PointSTL(originPoint.x + espacementRetraitGauche * 2, originPoint.y + espacementRetrait, originPoint.z + largeurMur)));
                            contenuSTL.ajouterContenuSolide(new RectangleSTL(
                                    new PointSTL(originPoint.x + longueurMur + espacementRetraitDroit * 2, originPoint.y + espacementRetrait, originPoint.z),
                                    new PointSTL(originPoint.x + longueurMur - demiEpaisseur + espacementRetraitDroit, originPoint.y + espacementRetrait, originPoint.z),
                                    new PointSTL(originPoint.x + longueurMur - demiEpaisseur + espacementRetraitDroit, originPoint.y + espacementRetrait, originPoint.z + largeurMur),
                                    new PointSTL(originPoint.x + longueurMur + espacementRetraitDroit * 2, originPoint.y + espacementRetrait, originPoint.z + largeurMur)));
                        }
                        break;
                }
            }
        }
    }


    public Tuple<Boolean, Triplet<Double, Double, Point2D>> checkPointTousSurAccessoire(List<Triplet<Double, Double, Point2D>> p_accessoireListe, Double murPlanXtranslator, Orientations face, Point2D... p_points) {
        boolean pointsSurAccessoire = false;
        Triplet<Double, Double, Point2D> accessoireTrouve = null;
        for (Triplet<Double, Double, Point2D> accessoire : p_accessoireListe) {
            double shapeX = accessoire.z.getX() + murPlanXtranslator;
            double shapeWidth = accessoire.x + 0.00001;
            if (face.equals(ARRIERE) || face.equals(DROITE)) {
                shapeX = murPlanXtranslator - accessoire.z.getX() - accessoire.x;
                shapeWidth = accessoire.x + 0.00001;
            }
            Rectangle2D accessoireShape = new Rectangle2D.Double(
                    shapeX,
                    accessoire.z.getY(),
                    shapeWidth,                       // longueur accessoire
                    accessoire.y + 0.00001);          // largeur accessoire
            for (Point2D pPoint : p_points)
                if (accessoireShape.contains(pPoint)) pointsSurAccessoire = true;
                else {
                    pointsSurAccessoire = false;
                    break;
                }
            if (pointsSurAccessoire) {
                accessoireTrouve = accessoire;
                break;
            }
        }
        return new Tuple<>(pointsSurAccessoire, accessoireTrouve);
    }

    public Tuple<Triplet<Double, Double, Double>[][], Triplet<Double, Double, Double>> genererTableauDePoint(Orientations p_face, ArrayList<Triplet<Double, Double, Point2D>> accessoireListe,
                                                                                                             Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension, boolean faceInterieur) {
        double largeurChalet = p_chaletDimmension.x.pouces();
        double longueurChalet = p_chaletDimmension.y.pouces();
        double largeurMur = p_chaletDimmension.z.pouces();

        double epaisseurMur = getEpaisseur().pouces();
        double demiEpaisseur = epaisseurMur / 2;

        Orientations face = p_face;
        Triplet<Double, Double, Double> pointOrigineMur3D = null; // utilisé pour savoir ou se trouve le mur par rapport au chalet
        Tuple<Double, Double> pointOriginePlanDuMur2D = null;     // utilisé pour savoir ou les accesoires sont disposé sur le mur
        int murPlanXtranslator = 0;


        double longueurMur = 0;


        /**** determination des parametres de calcul selon le cote ou se trouve le mur *****/
        switch (face) {
            case FACADE:
                longueurMur = longueurChalet - (faceInterieur ? epaisseurMur : 0);
                pointOrigineMur3D = new Triplet<>(largeurChalet + (faceInterieur ? 0 : epaisseurMur), 0.0 + (faceInterieur ? epaisseurMur / 2 : 0), 0.0);
                // le systeme de point de coordonne des accessoires sera sur le plan (y,z)
                pointOriginePlanDuMur2D = new Tuple<>(pointOrigineMur3D.y, pointOrigineMur3D.z);
                murPlanXtranslator = 1;

                break;
            case ARRIERE:
                longueurMur = longueurChalet - (faceInterieur ? epaisseurMur : 0);
                pointOrigineMur3D = new Triplet<>(0.0 + (faceInterieur ? epaisseurMur : 0), longueurChalet + (faceInterieur ? -1 * epaisseurMur / 2 : 0), 0.0);
                // le systeme de point de coordonne des accessoires sera sur le plan (y,z)
                pointOriginePlanDuMur2D = new Tuple<>(pointOrigineMur3D.y, pointOrigineMur3D.z);
                murPlanXtranslator = -1;

                break;
            case GAUCHE:
                longueurMur = largeurChalet - (faceInterieur ? epaisseurMur : 0);
                pointOrigineMur3D = new Triplet<>(demiEpaisseur + (faceInterieur ? demiEpaisseur : 0), 0.0 + (faceInterieur ? epaisseurMur : 0), 0.0);
                // le systeme de point de coordonne des accessoires sera sur le plan (x,z)
                pointOriginePlanDuMur2D = new Tuple<>(pointOrigineMur3D.x, pointOrigineMur3D.z);
                murPlanXtranslator = 1;

                break;
            case DROITE:
                longueurMur = largeurChalet - (faceInterieur ? epaisseurMur : 0);
                pointOrigineMur3D = new Triplet<>(largeurChalet + (faceInterieur ? 0 : demiEpaisseur), longueurChalet - (faceInterieur ? epaisseurMur : 0), 0.0);
                // le systeme de point de coordonne des accessoires sera sur le plan (x,z)
                pointOriginePlanDuMur2D = new Tuple<>(pointOrigineMur3D.x, pointOrigineMur3D.z);
                murPlanXtranslator = -1;

                break;
        }

        // ajustement de la coordonnée x des accessoires pour la face interne des murs
        if (faceInterieur && (face.equals(FACADE) || face.equals(ARRIERE)))
            accessoireListe.forEach(accessoire -> accessoire.z.setLocation(accessoire.z.getX() - demiEpaisseur, accessoire.z.getY()));
        else if (faceInterieur && (face.equals(GAUCHE) || face.equals(DROITE)))
            accessoireListe.forEach(accessoire -> accessoire.z.setLocation(accessoire.z.getX() - demiEpaisseur, accessoire.z.getY()));

        /****  tracage de lignes extrapolées des contoures des accessoires *****/
        Tuple<ArrayList<Line2D>, ArrayList<Line2D>> lignesHoriEtVertAccessoire = extrapolerLignesAccessoire(accessoireListe, pointOriginePlanDuMur2D, murPlanXtranslator, longueurMur, largeurMur);
        ArrayList<Line2D> listeLigneHorizontal = lignesHoriEtVertAccessoire.x;
        ArrayList<Line2D> listeLigneVertical = lignesHoriEtVertAccessoire.y;

        // creation du tableau de points
        Tuple<Double, Double>[][] tableauDePoint = new Tuple[listeLigneHorizontal.size()][listeLigneVertical.size()];

        for (int i = 0; i < listeLigneHorizontal.size(); i++) {
            Line2D lineH = listeLigneHorizontal.get(i);
            for (int j = 0; j < listeLigneVertical.size(); j++) {
                Line2D lineV = listeLigneVertical.get(j);
                double intersectionX = lineV.getX1();                           // peu importe x1 ou x2 on ils ont la meme valeur
                double intersectionY = lineH.getY1();                           // peu importe y1 ou y2 on ils ont la meme valeur
                tableauDePoint[i][j] = new Tuple<>(intersectionX, intersectionY);
            }
        }

        /**** conversion des points 2D extraits en points 3D, le tout organisé et ordonné dans un tableau *****/
        Triplet<Double, Double, Double>[][] tableauDePoint3D = new Triplet[tableauDePoint.length][tableauDePoint[0].length];

        for (int i = 0; i < tableauDePoint.length; i++)
            for (int j = 0; j < tableauDePoint[0].length; j++)
                switch (face) {
                    // y,z
                    case FACADE:
                    case ARRIERE:
                        Tuple<Double, Double> cas1Point2D = tableauDePoint[i][j];
                        tableauDePoint3D[i][j] = new Triplet<>(pointOrigineMur3D.x, cas1Point2D.x, cas1Point2D.y);
                        break;
                    // x,z
                    case GAUCHE:
                    case DROITE:
                        Tuple<Double, Double> cas2Point2D = tableauDePoint[i][j];
                        tableauDePoint3D[i][j] = new Triplet<>(cas2Point2D.x, pointOrigineMur3D.y, cas2Point2D.y);
                        break;
                }

        return new Tuple<>(tableauDePoint3D, pointOrigineMur3D);
    }

    public Tuple<ArrayList<Line2D>, ArrayList<Line2D>> extrapolerLignesAccessoire(
            ArrayList<Triplet<Double, Double, Point2D>> accessoireListe,
            Tuple<Double, Double> pointOriginePlanDuMur2D, int murPlanXtranslator, double longueurMur, double largeurMur) {

        /**** tracage de lignes horizontales *****/
        // Sorting the list based on y in increasing order
        accessoireListe.sort(Comparator.comparingDouble(triplet -> triplet.z.getY()));

        ArrayList<Line2D> listeLigneHorizontal = new ArrayList<>();

        // tracage de ligne bas du mur
        Line2D basDuMur = new Line2D.Double(pointOriginePlanDuMur2D.x,                // x1
                pointOriginePlanDuMur2D.y,                                            // y1
                pointOriginePlanDuMur2D.x + (murPlanXtranslator * longueurMur),       // x2
                pointOriginePlanDuMur2D.y);                                           // y2
        listeLigneHorizontal.add(basDuMur);

        // tracage de ligne pour chaque accessoire
        for (Triplet<Double, Double, Point2D> accessoire : accessoireListe) {
            Line2D lineH = new Line2D.Double(pointOriginePlanDuMur2D.x,               // x1
                    accessoire.z.getY(),                                              // y1
                    pointOriginePlanDuMur2D.x + (murPlanXtranslator * longueurMur),   // x2
                    accessoire.z.getY());                                             // y2
            if (!ligneExitesDeja(lineH, listeLigneHorizontal))                        // rajoute la ligne si elle n'existe pas déjà
                listeLigneHorizontal.add(lineH);

            Line2D lineH2 = new Line2D.Double(pointOriginePlanDuMur2D.x,              // x1
                    accessoire.z.getY() + accessoire.y,                               // y1
                    pointOriginePlanDuMur2D.x + (murPlanXtranslator * longueurMur),   // x2
                    accessoire.z.getY() + accessoire.y);                              // y2
            if (!ligneExitesDeja(lineH2, listeLigneHorizontal))                       // rajoute la ligne si elle n'existe pas déjà
                listeLigneHorizontal.add(lineH2);
        }

        // tracage de ligne haut du mur
        Line2D hautDuMur = new Line2D.Double(pointOriginePlanDuMur2D.x,               // x1
                pointOriginePlanDuMur2D.y + largeurMur,                               // y1
                pointOriginePlanDuMur2D.x + (murPlanXtranslator * longueurMur),       // x2
                pointOriginePlanDuMur2D.y + largeurMur);                              // y2
        listeLigneHorizontal.add(hautDuMur);

        // Sorting the list based on the x-coordinate of the starting point
        Collections.sort(listeLigneHorizontal, Comparator.comparingDouble(Line2D::getY1));

        /**** tracage de lignes verticales *****/
        // Sorting the list based on x in increasing order
        Collections.sort(accessoireListe, Comparator.comparingDouble(triplet -> triplet.z.getY()));


        ArrayList<Line2D> listeLigneVertical = new ArrayList<>();

        // tracage de ligne de gauche du mur
        Line2D ligneGaucheMur = new Line2D.Double(
                pointOriginePlanDuMur2D.x,                                            // x1
                pointOriginePlanDuMur2D.y,                                            // y1
                pointOriginePlanDuMur2D.x,                                            // x2
                pointOriginePlanDuMur2D.y + largeurMur);                              // y2
        listeLigneVertical.add(ligneGaucheMur);

        // tracage de ligne pour chaque accessoire
        for (Triplet<Double, Double, Point2D> accessoire : accessoireListe) {
            Line2D lineV = new Line2D.Double(
                    pointOriginePlanDuMur2D.x + (murPlanXtranslator * accessoire.z.getX()),                     // x1
                    pointOriginePlanDuMur2D.y,                                                                      // y1
                    pointOriginePlanDuMur2D.x + (murPlanXtranslator * accessoire.z.getX()),                         // x2
                    pointOriginePlanDuMur2D.y + largeurMur);                                                        // y2
            if (!ligneExitesDeja(lineV, listeLigneVertical))                                                        // rajoute la ligne si elle n'existe pas déjà
                listeLigneVertical.add(lineV);

            Line2D lineV2 = new Line2D.Double(
                    pointOriginePlanDuMur2D.x + (murPlanXtranslator * (accessoire.z.getX() + accessoire.x)),     // x1
                    pointOriginePlanDuMur2D.y,                                                                      // y1
                    pointOriginePlanDuMur2D.x + (murPlanXtranslator * (accessoire.z.getX() + accessoire.x)),        // x2
                    pointOriginePlanDuMur2D.y + largeurMur);                                                        // y2
            if (!ligneExitesDeja(lineV2, listeLigneVertical))                                                       // rajoute la ligne si elle n'existe pas déjà
                listeLigneVertical.add(lineV2);
        }

        // tracage de ligne de droite du mur
        Line2D ligneDroiteMur = new Line2D.Double(
                pointOriginePlanDuMur2D.x + (murPlanXtranslator * longueurMur),    // x1
                pointOriginePlanDuMur2D.y,                                             // y1
                pointOriginePlanDuMur2D.x + (murPlanXtranslator * longueurMur),        // x2
                pointOriginePlanDuMur2D.y + largeurMur);                               // y2
        listeLigneVertical.add(ligneDroiteMur);

        // Sorting the list based on the x-coordinate of the starting point
        listeLigneVertical.sort(Comparator.comparingDouble(Line2D::getX1));

        return new Tuple<>(listeLigneHorizontal, listeLigneVertical);
    }

    public boolean ligneExitesDeja(Line2D p_ligne_A_Verifier, ArrayList<Line2D> listeDeLignes) {
        boolean existeDeja = false;

        //Eviter la duplication des lignes pour ne pas dupliquer les points
        for (Line2D ligne : listeDeLignes)
            if (p_ligne_A_Verifier.getX1() == ligne.getX1()
                    && p_ligne_A_Verifier.getX2() == ligne.getX2()
                    && p_ligne_A_Verifier.getY1() == ligne.getY1()
                    && p_ligne_A_Verifier.getY2() == ligne.getY2()) {
                existeDeja = true;
            }

        return existeDeja;
    }


    public boolean contientAccessoiresInvalide() {
        return !accessoiresInvalideListes.isEmpty();
    }


    public ConcurrentLinkedQueue<ContenuSTL> demandeGenererAccessoiresSTL(String p_dossierDestination, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension) {
        ConcurrentLinkedQueue<ContenuSTL> listContenuSTL = new ConcurrentLinkedQueue<>();

        for (Exportable accessoire : accessoiresListes) {
            listContenuSTL.add(accessoire.genererVersionRetrait(p_dossierDestination, p_chaletDimmension, getNbrAccessoire()));
        }

        return listContenuSTL;
    }

    public List<Accessoire> getAccessoiresListes() {
        return accessoiresListes;
    }

    public int getNbrAccessoire() {
        return accessoiresListes.size();
    }

    public int getNbrPorte() {
        int nombreDePortes = 0;
        for (Object accessoire : accessoiresListes) {
            if (accessoire instanceof Porte) {
                nombreDePortes++;
            }
        }
        return nombreDePortes;
    }

    public int getNbrFenetre() {
        int nombreDeFenetres = 0;
        for (Accessoire accessoire : accessoiresListes) {
            if (accessoire instanceof Fenetre) {
                nombreDeFenetres++;
            }
        }
        return nombreDeFenetres;
    }

    /**
     * @param positionAccesoire{x, y}
     * @param dimensionAccessoire  {longueur, largeur}
     */
    public boolean validerFenetre(UniteImperiale[] positionAccesoire, float[] dimensionAccessoire) {

        Rectangle2D murConteneur = new Rectangle2D.Double(
                0,
                0,
                getLongueur().pieds(),
                getLargeur().pieds());

        Rectangle2D nouvelAccessoire = new Rectangle2D.Double(
                positionAccesoire[0].pieds() - espacementAccessoires.pieds(),
                positionAccesoire[1].pieds() - espacementAccessoires.pieds(),
                dimensionAccessoire[0] + espacementAccessoires.pieds() * 2,
                dimensionAccessoire[1] + espacementAccessoires.pieds() * 2);

        if (murConteneur.contains(nouvelAccessoire))
            return true;
        else return false;
    }

    /**
     * @param positionAccesoire
     * @param dimensionAccessoire {longueur, largeur}
     * @return
     */
    protected boolean validerPorte(UniteImperiale[] positionAccesoire, float[] dimensionAccessoire) {

        boolean dansLeCadreX = positionAccesoire[0].pieds() >= espacementAccessoires.pieds() &&
                positionAccesoire[0].pieds() <= getLongueur().pieds() - (dimensionAccessoire[0] + espacementAccessoires.pieds());
        boolean dansLeCadreY = positionAccesoire[1].pieds() >= espacementAccessoires.pieds();

        return dansLeCadreX && dansLeCadreY;
    }

    /**
     * @param p_accessoire
     * @param positionAccesoire
     * @param dimensionAccessoire {longueur, largeur}
     * @return
     */
    protected boolean validerPoximiteAccessoire(Accessoire p_accessoire, UniteImperiale[] positionAccesoire, float[] dimensionAccessoire) {

        for (Accessoire accessoire : accessoiresListes) {
            if (p_accessoire != accessoire) {
                float[] posAccDejaPresent = {accessoire.getCoordonneesEmplacement()[0].pieds(), accessoire.getCoordonneesEmplacement()[1].pieds()};

                Rectangle2D accessoireDejaPresent = new Rectangle2D.Double(
                        posAccDejaPresent[0] - espacementAccessoires.pieds(),
                        posAccDejaPresent[1] - espacementAccessoires.pieds(),
                        accessoire.getLongueur().pieds() + (espacementAccessoires.pieds() * 2),
                        accessoire.getLargeur().pieds() + (espacementAccessoires.pieds() * 2));

                Rectangle2D nouvelAccessoire = new Rectangle2D.Double(
                        positionAccesoire[0].pieds(),
                        positionAccesoire[1].pieds(),
                        dimensionAccessoire[0],
                        dimensionAccessoire[1]);

                if (accessoireDejaPresent.intersects(nouvelAccessoire))
                    return false;
            }
        }

        return true;
    }

    /**
     * @param p_accessoire
     * @param p_coordonneesEmplacementNouveau {longueur, largeur}
     * @return
     */
    protected boolean validerDeplacementAccessoire(Accessoire p_accessoire, UniteImperiale[] p_coordonneesEmplacementNouveau) {

        boolean estPorteEtEstValide = p_accessoire instanceof Porte && validerPorte(p_coordonneesEmplacementNouveau, new float[]{p_accessoire.getLongueur().pieds(), p_accessoire.getLargeur().pieds()});
        boolean estFenetreEtEstValide = p_accessoire instanceof Fenetre && validerFenetre(p_coordonneesEmplacementNouveau, new float[]{p_accessoire.getLongueur().pieds(), p_accessoire.getLargeur().pieds()});
        boolean pasTropProche = validerPoximiteAccessoire(p_accessoire, p_coordonneesEmplacementNouveau, new float[]{p_accessoire.getLongueur().pieds(), p_accessoire.getLargeur().pieds()});
        if (!pasTropProche) {
            p_accessoire.setValide(false);
            accessoiresInvalideListes.add(p_accessoire);

        } else {
            retirerAccessoireDeLaListeInvalides(p_accessoire);
        }
        return (estPorteEtEstValide || estFenetreEtEstValide);
    }


    /**
     * @param p_accessoire
     */
    protected boolean ajouterAccessoire(Accessoire p_accessoire) {
        boolean estAjouter = false;
        boolean estPorteEtEstValide = p_accessoire instanceof Porte && validerPorte(p_accessoire.getCoordonneesEmplacement(), new float[]{p_accessoire.getLongueur().pieds(), p_accessoire.getLargeur().pieds()});
        boolean estFenetreEtEstValide = p_accessoire instanceof Fenetre && validerFenetre(p_accessoire.getCoordonneesEmplacement(), new float[]{p_accessoire.getLongueur().pieds(), p_accessoire.getLargeur().pieds()});
        boolean pasTropProche = validerPoximiteAccessoire(p_accessoire, p_accessoire.getCoordonneesEmplacement(), new float[]{p_accessoire.getLongueur().pieds(), p_accessoire.getLargeur().pieds()});
        if (pasTropProche && (estFenetreEtEstValide || estPorteEtEstValide)) {
            accessoiresListes.add(p_accessoire);
            estAjouter = true;
        } else
            System.err.println("Exception lancé: Accesoire invalide");
        return estAjouter;
    }

    @Override
    protected void updateShapePosition(float x, float y) {
        setShape(new Rectangle2D.Double(x, y, shape.getBounds2D().getWidth(), shape.getBounds().getHeight()));
        //shape.setRect(x, y, shape.getWidth(), shape.getHeight());
    }


    /**
     * tente une modification et retourne true si la modification a été réaliser avec succès
     *
     * @param p_largeur
     * @param p_longueur
     * @param p_epaisseur
     * @param p_espacement
     * @return
     */
    protected boolean modifierMur(UniteImperiale p_largeur, UniteImperiale p_longueur, UniteImperiale p_epaisseur, UniteImperiale p_espacement) {

        //save les anciennes valeurs
        UniteImperiale savedAncienneLongueur = this.getLongueur();
        UniteImperiale savedAncienneLargeur = this.getLargeur();
        UniteImperiale savedAncienneEspacement = this.getEspacementAccessoires();
        UniteImperiale savedAncienneEpaisseur = this.getEpaisseur();
        ArrayList<UniteImperiale[]> ancienEmplacementAccessoires = new ArrayList<>();

        for (Accessoire accessoire : accessoiresListes) {
            ancienEmplacementAccessoires.add(accessoire.getCoordonneesEmplacement());
        }

        //set les nouvelles valeurs
        setLargeur(p_largeur);
        setLongueur(p_longueur);
        setEpaisseur(p_epaisseur);
        this.espacementAccessoires = p_espacement;

        //nouvelles coordonnées
        for (Accessoire acc : accessoiresListes) {
            UniteImperiale ratioX = acc.getCoordonneesEmplacement()[0].diviserPar(savedAncienneLongueur).multiplierPar(getLongueur());
            UniteImperiale ratioY = null;
            if (acc instanceof Fenetre) {
                ratioY = acc.getCoordonneesEmplacement()[1].diviserPar(savedAncienneLargeur).multiplierPar(getLargeur());
            } else if (acc instanceof Porte) {
                //S'assure que la porte reste au bas du mur lors de la redimension
                ratioY = acc.getCoordonneesEmplacement()[1].additionner(getLargeur()).soustraire(savedAncienneLargeur);
            }
            acc.modifierCoordonnees(new UniteImperiale[]{ratioX, ratioY});
        }

        //validation
        for (Accessoire acc : accessoiresListes) {
            boolean porteValide = (acc instanceof Porte && validerPorte(new UniteImperiale[]{acc.getCoordonneesEmplacement()[0], acc.getCoordonneesEmplacement()[1]},
                    new float[]{acc.getLongueur().pieds(), acc.getLargeur().pieds()}));
            boolean deborde = !(validerFenetre(new UniteImperiale[]{acc.getCoordonneesEmplacement()[0], acc.getCoordonneesEmplacement()[1]},
                    new float[]{acc.getLongueur().pieds(), acc.getLargeur().pieds()})
                    && (porteValide || !(acc instanceof Porte)));
            boolean accessoireContenuDansUnAutre = validerAccessoireContenuDansUnAutre(acc, acc.getCoordonneesEmplacement(), new float[]{acc.getLongueur().pieds(), acc.getLargeur().pieds()});
            if (deborde || accessoireContenuDansUnAutre) {
                defaireModificationMur(savedAncienneLargeur, savedAncienneLongueur, savedAncienneEpaisseur, savedAncienneEspacement, ancienEmplacementAccessoires);
                System.err.println("modification invalide car un ou des accessoires " + (deborde ? "deborde du mur" : " s'emboitent completement danms un autre accessoire"));
                return false;
            } else {
                //valider tous les accessoires
                if (!validerPoximiteAccessoire(acc, acc.getCoordonneesEmplacement(), new float[]{acc.getLongueur().pieds(), acc.getLargeur().pieds()})) {
                    if (!accessoiresInvalideListes.contains(acc))
                        accessoiresInvalideListes.add(acc);
                    acc.setValide(false);
                } else {
                    // retire l'accessoire de la liste invalide si jamais il est présent
                    retirerAccessoireDeLaListeInvalides(acc);
                }
            }
        }
        return true;
    }

    private boolean validerAccessoireContenuDansUnAutre(Accessoire p_accessoire, UniteImperiale[] positionAccesoire, float[] dimensionAccessoire) {

        for (Accessoire accessoire : accessoiresListes) {
            if (p_accessoire != accessoire) {
                float[] posAccDejaPresent = {accessoire.getCoordonneesEmplacement()[0].pieds(), accessoire.getCoordonneesEmplacement()[1].pieds()};

                Rectangle2D accessoireDejaPresent = new Rectangle2D.Double(
                        posAccDejaPresent[0],
                        posAccDejaPresent[1],
                        accessoire.getLongueur().pieds(),
                        accessoire.getLargeur().pieds());

                Rectangle2D nouvelAccessoire = new Rectangle2D.Double(
                        positionAccesoire[0].pieds(),
                        positionAccesoire[1].pieds(),
                        dimensionAccessoire[0],
                        dimensionAccessoire[1]);

                if (accessoireDejaPresent.contains(nouvelAccessoire))
                    return true;
            }
        }

        return false;
    }

    protected void retirerAccessoireDeLaListeInvalides(Accessoire p_accessoire) {
        if (p_accessoire != null) {
            accessoiresInvalideListes.remove(p_accessoire);
            p_accessoire.setValide(true);
        }

        for (Accessoire accessoireInvalide : accessoiresInvalideListes) {
            boolean estPorteEtEstValide = accessoireInvalide instanceof Porte && validerPorte(accessoireInvalide.getCoordonneesEmplacement(), new float[]{accessoireInvalide.getLongueur().pieds(), accessoireInvalide.getLargeur().pieds()});
            boolean estFenetreEtEstValide = accessoireInvalide instanceof Fenetre && validerFenetre(accessoireInvalide.getCoordonneesEmplacement(), new float[]{accessoireInvalide.getLongueur().pieds(), accessoireInvalide.getLargeur().pieds()});
            boolean pasTropProche = validerPoximiteAccessoire(accessoireInvalide, accessoireInvalide.getCoordonneesEmplacement(), new float[]{accessoireInvalide.getLongueur().pieds(), accessoireInvalide.getLargeur().pieds()});
            if (pasTropProche && (estFenetreEtEstValide || estPorteEtEstValide)) {
                accessoireInvalide.setValide(true);
                accessoiresInvalideListes.remove(p_accessoire);
            }
        }
    }

    private void defaireModificationMur(UniteImperiale p_largeur, UniteImperiale p_longueur, UniteImperiale p_epaisseur, UniteImperiale p_espacement, ArrayList<UniteImperiale[]> p_mplacementAccessoires) {
        //set les anciennes valeurs
        setLargeur(p_largeur);
        setLongueur(p_longueur);
        setEpaisseur(p_epaisseur);
        this.espacementAccessoires = p_espacement;

        int index = 0;
        for (Accessoire acc : accessoiresListes) {
            acc.modifierCoordonnees(p_mplacementAccessoires.get(index));
            index++;
        }
    }

    @Override
    protected void setAffichee(boolean p_affichee) {
        super.setAffichee(p_affichee);
        accessoiresListes.forEach(accessoire -> accessoire.setAffichee(p_affichee));
    }

    @Override
    protected Accessoire supprimerAccessoire(UniteImperiale[] p_coordonneesEmplacement) {

        // On crée un itérateur pour supprimer l'accessoire
        Iterator<Accessoire> iterator = accessoiresListes.iterator();
        while (iterator.hasNext()) {
            Accessoire accessoire = iterator.next();
            UniteImperiale[] coordonnes = accessoire.getCoordonneesEmplacement();
            UniteImperiale distancex = coordonnes[0].additionner(accessoire.getLongueur());
            UniteImperiale distancey = coordonnes[1].additionner(accessoire.getLargeur());

            if (coordonnes[0].pieds() <= p_coordonneesEmplacement[0].pieds() && p_coordonneesEmplacement[0].pieds() <= distancex.pieds()
                    && coordonnes[1].pieds() <= p_coordonneesEmplacement[1].pieds() && p_coordonneesEmplacement[1].pieds() <= distancey.pieds()) {
                //On utilise l'itérateur pour supprimer l'accessoire
                iterator.remove();
                return accessoire;
            }
        }
        return null;
    }

    protected Accessoire supprimerAccessoire(int accessoire_id) {
        Iterator<Accessoire> iterator = accessoiresListes.iterator();
        while (iterator.hasNext()) {
            Accessoire accessoire = iterator.next();

            if (accessoire.getAccessoire_identification() == accessoire_id) {
                //On utilise l'itérateur pour supprimer l'accessoire
                iterator.remove();
                return accessoire;
            }
        }
        return null;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        // Initialize the transient field 'shape'
        shape = new Area(new Rectangle2D.Double(0, 0, getLongueur().pieds() * CaisseOutils.RATIO_TEST, getLargeur().pieds() * CaisseOutils.RATIO_TEST));
        shapeVueDebordement = new Hashtable<>();
    }

    public Orientations getCoteVirtuel() {
        return coteVirtuel;
    }
}