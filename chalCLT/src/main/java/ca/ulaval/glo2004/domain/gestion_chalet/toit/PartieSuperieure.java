package ca.ulaval.glo2004.domain.gestion_chalet.toit;

import ca.ulaval.glo2004.domain.gestion_chalet.PanneauToit;
import ca.ulaval.glo2004.services.*;
import ca.ulaval.glo2004.services.ContenuSTL.*;
import ca.ulaval.glo2004.services.CaisseOutils.*;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

public class PartieSuperieure extends PanneauToit {

    private float anglePartieSuperieure;
    private float hauteurPartieSuperieureShape;

    private float hauteurPartieSuperieure;

    Orientations vueMode;
    private double positionX;

    private double positionY;

    public Orientations getPartieSuperieureCote() {
        return partieSuperieureCote;
    }

    private void setPartieSuperieureCote(Orientations PartieSuperieureCote) {
        this.partieSuperieureCote = PartieSuperieureCote;
    }

    private Orientations partieSuperieureCote;
    private Orientations orientationToit = Orientations.ARRIERE;

    /**
     * @param p_PartieSuperieure
     */
    public PartieSuperieure(PartieSuperieure p_PartieSuperieure) {
        this(p_PartieSuperieure.getLargeur(), p_PartieSuperieure.getLongueur(), p_PartieSuperieure.getEpaisseur(), p_PartieSuperieure.getNbreCouches(), p_PartieSuperieure.getCote(), p_PartieSuperieure.getAngle());
    }

    public PartieSuperieure(UniteImperiale p_largeurChalet, UniteImperiale p_longueurChalet, UniteImperiale p_epaisseur, Couches p_nbreCouches, Orientations p_cote, float angle) {
        super(p_largeurChalet, p_longueurChalet, p_epaisseur, p_nbreCouches, p_cote);
        this.anglePartieSuperieure = angle;
        partieSuperieureCote = p_cote;
        hauteurPartieSuperieure = (float) (Math.tan(Math.toRadians(angle))) * (getLargeur().pouces() + getEpaisseur().pouces() / 2);
        hauteurPartieSuperieureShape = (float) Math.tan(Math.toRadians(anglePartieSuperieure)) * (getLargeur().pieds() + getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST;
        // TODO - implement Pignon.Pignon
    }

    @Override
    protected void updateShapeComposanteToit(double pointX, double pointY, float angle, Orientations vueMode) {
        positionX = pointX;
        positionY = pointY;
        hauteurPartieSuperieureShape = (float) Math.tan(Math.toRadians(anglePartieSuperieure)) * (getLargeur().pieds() + getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST;
        this.shape = new Rectangle2D.Double(positionX,
                pointY - hauteurPartieSuperieureShape, getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                hauteurPartieSuperieureShape);
    }

    @Override
    public float getAngle() {
        return anglePartieSuperieure;
    }

    @Override
    protected void updateAngle(float angle) {
        anglePartieSuperieure = angle;
        hauteurPartieSuperieure = (float) Math.tan(Math.toRadians(angle)) * (getLargeur().pouces() + getEpaisseur().pouces() / 2);
        updateShapeComposanteToit(positionX, positionY, angle, vueMode);
    }

    @Override
    protected void setOrientation(Orientations orientations) {
        setPartieSuperieureCote(orientations);
    }

    @Override
    public UniteImperiale getHauteur() {
        return CaisseOutils.decimalToUniImp(Math.tan(Math.toRadians(getAngle())) * getLongueur().pouces());
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
        String filename = CaisseOutils.CHALCLT_FILE_NAME + ExportationMode.BRUT + "_T" + CaisseOutils.EXTENSTION;
        ContenuSTL contenu_STL = new ContenuSTL(filename, fileDestination);

        double angle = getAngle();
        double epaisseurPanneau = getEpaisseur().pouces();
        double epaisseurIncline = epaisseurPanneau * 1.5 / Math.abs(Math.cos(15));

        double longueurChalet = p_chaletDimmension.y.pouces();
        double largeurChalet = p_chaletDimmension.x.pouces();
        double hauteurPartSupp = Math.abs(Math.tan(Math.toRadians(angle)) * largeurChalet);

        PointSTL origin = new PointSTL(0, 0, 0);
        PointSTL backLeft_top = new PointSTL(origin.x, origin.y, origin.z + epaisseurIncline);

        PointSTL backRight_bottom = new PointSTL(origin.x, origin.y + longueurChalet, origin.z);
        PointSTL backRight_top = new PointSTL(backRight_bottom.x, backRight_bottom.y, backRight_bottom.z + epaisseurIncline);

        PointSTL frontLeft_bottom = new PointSTL(origin.x + largeurChalet + epaisseurPanneau / 2, origin.y, origin.z + hauteurPartSupp);
        PointSTL frontLeft_top = new PointSTL(frontLeft_bottom.x, frontLeft_bottom.y, frontLeft_bottom.z + epaisseurIncline);

        PointSTL frontRight_bottom = new PointSTL(frontLeft_bottom.x, frontLeft_bottom.y + longueurChalet, frontLeft_bottom.z);
        PointSTL frontRight_top = new PointSTL(frontRight_bottom.x, frontRight_bottom.y, frontRight_bottom.z + epaisseurIncline);

        contenu_STL.ajouterContenuSolide(new RectangleSTL(origin, backLeft_top, backRight_top, backRight_bottom));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(origin, frontLeft_bottom, frontRight_bottom, backRight_bottom));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(origin, frontLeft_bottom, frontLeft_top, backLeft_top));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(frontRight_bottom, backRight_bottom, backRight_top, frontRight_top));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(frontRight_bottom, frontLeft_bottom, frontLeft_top, frontRight_top));
        contenu_STL.ajouterContenuSolide(new RectangleSTL(frontRight_top, frontLeft_top, backLeft_top, backRight_top));


        return contenu_STL;
    }

    @Override
    public ContenuSTL genererVersionFini(String fileDestination, CaisseOutils.Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension) {
        String filename = CaisseOutils.CHALCLT_FILE_NAME + ExportationMode.FINI + "_T" + CaisseOutils.EXTENSTION;
        ContenuSTL contenu_STL = new ContenuSTL(filename, fileDestination);

        double angle = getAngle();
        double epaisseurPanneau = getEpaisseur().pouces();
        double demiEpaisseur = epaisseurPanneau / 2;
        double epaisseurIncline = epaisseurPanneau / Math.abs(Math.cos(Math.toRadians(angle)));

        double longueurChalet = p_chaletDimmension.y.pouces();
        double largeurChalet = p_chaletDimmension.x.pouces();
        double hauteurChalet = p_chaletDimmension.z.pouces();
        double hauteurPartSupp = Math.abs(Math.tan(Math.toRadians(angle)) * largeurChalet);
        double longueurInterne = longueurChalet - epaisseurPanneau;


        Triplet<Double, Double, Double> originPoint = new Triplet<>(largeurChalet + demiEpaisseur, 0.0, hauteurChalet);

        // facade down bit
        PointSTL jonctionGauche_GF = new PointSTL(originPoint.x, originPoint.y, originPoint.z);
        PointSTL jonctionDroite_GF = new PointSTL(originPoint.x, originPoint.y + longueurChalet, originPoint.z);
        PointSTL pointDroite_F = new PointSTL(originPoint.x + demiEpaisseur, originPoint.y + longueurChalet, originPoint.z);
        PointSTL pointGauche_F = new PointSTL(originPoint.x + demiEpaisseur, originPoint.y, originPoint.z);
        contenu_STL.ajouterContenuSolide(new RectangleSTL(jonctionGauche_GF, jonctionDroite_GF, pointDroite_F, pointGauche_F));

        // facade front bit
        double removedTip = Math.abs(Math.tan(angle) * demiEpaisseur);
        PointSTL pointDroiteUP_F = new PointSTL(pointDroite_F.x, pointDroite_F.y, pointDroite_F.z + epaisseurIncline);
        PointSTL pointGaucheUP_F = new PointSTL(pointGauche_F.x, pointGauche_F.y, pointGauche_F.z + epaisseurIncline);
        contenu_STL.ajouterContenuSolide(new RectangleSTL(pointGaucheUP_F, pointDroiteUP_F, pointDroite_F, pointGauche_F));

        // left Side
        PointSTL pointGauche_A = new PointSTL(originPoint.x - largeurChalet - demiEpaisseur, originPoint.y, originPoint.z + hauteurPartSupp);
        PointSTL pointGaucheUP_A = new PointSTL(pointGauche_A.x, pointGauche_A.y, pointGauche_A.z + epaisseurIncline);
        contenu_STL.ajouterContenuSolide(new RectangleSTL(pointGauche_A, pointGaucheUP_A, pointGaucheUP_F, pointGauche_F));
        contenu_STL.ajouterContenuSolide(new TriangleSTL(pointGauche_A, pointGauche_F, jonctionGauche_GF));

        // right Side
        PointSTL pointDroite_A = new PointSTL(pointGauche_A.x, pointGauche_A.y + longueurChalet, pointGauche_A.z);
        PointSTL pointDroiteUP_A = new PointSTL(pointGauche_A.x, pointGauche_A.y + longueurChalet, pointGauche_A.z + epaisseurIncline);
        contenu_STL.ajouterContenuSolide(new RectangleSTL(pointDroite_A, pointDroiteUP_A, pointDroiteUP_F, pointDroite_F));
        contenu_STL.ajouterContenuSolide(new TriangleSTL(pointDroite_A, pointDroite_F, jonctionDroite_GF));

        // back side
        contenu_STL.ajouterContenuSolide(new RectangleSTL(pointDroite_A, pointDroiteUP_A, pointGaucheUP_A, pointGauche_A));

        // top side
        contenu_STL.ajouterContenuSolide(new RectangleSTL(pointDroiteUP_A, pointGaucheUP_A, pointGaucheUP_F, pointDroiteUP_F));

        /** middle **/
        // back middle
        PointSTL pointMidGauche_A = new PointSTL(originPoint.x - largeurChalet, originPoint.y + demiEpaisseur, originPoint.z + hauteurPartSupp);
        PointSTL pointMidDroite_A = new PointSTL(pointMidGauche_A.x, pointMidGauche_A.y + longueurInterne, pointMidGauche_A.z);
        contenu_STL.ajouterContenuSolide(new RectangleSTL(pointGauche_A, pointDroite_A, pointMidDroite_A, pointMidGauche_A));

        // left middle
        PointSTL pointMidJonctionGauche_F = new PointSTL(jonctionGauche_GF.x, jonctionGauche_GF.y + demiEpaisseur, jonctionGauche_GF.z);
        contenu_STL.ajouterContenuSolide(new RectangleSTL(jonctionGauche_GF, pointMidJonctionGauche_F, pointMidGauche_A, pointGauche_A));

        // right middle
        PointSTL pointMidJonctionDroite_F = new PointSTL(jonctionDroite_GF.x, jonctionDroite_GF.y - demiEpaisseur, jonctionDroite_GF.z);
        contenu_STL.ajouterContenuSolide(new RectangleSTL(jonctionDroite_GF, pointMidJonctionDroite_F, pointMidDroite_A, pointDroite_A));

        /** bottom **/
        //bottom left side
        PointSTL pointBottomGauche_A = new PointSTL(pointMidGauche_A.x, pointMidGauche_A.y, pointMidGauche_A.z - demiEpaisseur);
        PointSTL pointBottomGaucheJonction_GF = new PointSTL(pointMidJonctionGauche_F.x - demiEpaisseur, pointMidJonctionGauche_F.y, pointMidJonctionGauche_F.z);
        contenu_STL.ajouterContenuSolide(new RectangleSTL(pointBottomGauche_A, pointBottomGaucheJonction_GF, pointMidJonctionGauche_F, pointMidGauche_A));

        //bottom right side
        PointSTL pointBottomDroit_A = new PointSTL(pointMidDroite_A.x, pointMidDroite_A.y, pointMidDroite_A.z - demiEpaisseur);
        PointSTL pointBottomDroitJonction_GF = new PointSTL(pointMidJonctionDroite_F.x - demiEpaisseur, pointMidJonctionDroite_F.y, pointMidJonctionDroite_F.z);
        contenu_STL.ajouterContenuSolide(new RectangleSTL(pointBottomDroit_A, pointBottomDroitJonction_GF, pointMidJonctionDroite_F, pointMidDroite_A));

        //bottom back side
        contenu_STL.ajouterContenuSolide(new RectangleSTL(pointBottomGauche_A, pointMidGauche_A, pointMidDroite_A, pointBottomDroit_A));

        //bottom front side
        contenu_STL.ajouterContenuSolide(new RectangleSTL(pointMidJonctionGauche_F, pointBottomGaucheJonction_GF, pointBottomDroitJonction_GF, pointMidJonctionDroite_F));
        //bottom front side
        contenu_STL.ajouterContenuSolide(new RectangleSTL(pointBottomDroit_A, pointBottomGauche_A, pointBottomGaucheJonction_GF, pointBottomDroitJonction_GF));


        return contenu_STL;
    }

    @Override
    public ContenuSTL genererVersionRetrait(String fileDestination, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension, int sequentiel) {
        return null;
    }
}