package ca.ulaval.glo2004.domain.gestion_afficheurs;


import ca.ulaval.glo2004.domain.gestion_chalet.*;
import ca.ulaval.glo2004.domain.gestion_chalet.accessoires.Porte;
import ca.ulaval.glo2004.services.CaisseOutils;
import ca.ulaval.glo2004.services.Orientations;
import ca.ulaval.glo2004.services.UniteImperiale;

import java.awt.*;
import java.awt.geom.Path2D;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Hashtable;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.List;

import static ca.ulaval.glo2004.domain.gestion_chalet.Panneau.DIMENSION_PANNEAU_DEFAUT;
import static ca.ulaval.glo2004.services.Orientations.*;


public class AfficheurMur extends Afficheur implements Serializable {

    private boolean movingAccessory;
    private Rectangle2D.Double selectedAccessoryShape;
    private UniteImperiale[] deplacementAccessoire;
    private Orientations vueCourante;

    private Color hoverColor;

    public AfficheurMur() {
        super();
    }


    public void drawShape(Rectangle2D.Double shape) {
        selectedAccessoryShape = shape;
        movingAccessory = true;
    }

    public void drawHoverShape(Rectangle2D.Double shape, Orientations p_vueCourante, Color p_hoverColor) {
        selectedAccessoryShape = shape;
        movingAccessory = true;
        vueCourante = p_vueCourante;
        hoverColor = p_hoverColor;
    }

    public void stopMovingAccessory() {
        movingAccessory = false;
        deplacementAccessoire = null;
        vueCourante = null;
    }

    public UniteImperiale[] getDeplacementAccessoire() {
        return deplacementAccessoire;
    }

    /**
     * @param g
     */
    public Hashtable<String, CaisseOutils.Tuple<Shape, Shape>> dessinerMur(Graphics g, List<Mur> list, List<PanneauToit> toit, boolean afficherVoisins) {
        float espacementRetrait = Chalet.getRetraitSupplementaire().pouces();
        Graphics2D g2 = (Graphics2D) g;
        Graphics2D g4 = (Graphics2D) g;
        Graphics2D g1 = (Graphics2D) g;
        Hashtable<String, CaisseOutils.Tuple<Shape, Shape>> p_tableau = new Hashtable<>();
        Color couleurDebordementArriere = list.get(1).getCouleur();
        Color couleurDebordementFacade = list.get(0).getCouleur();
        Color couleurDebordementDroite = list.get(2).getCouleur();
        Color couleurDebordementGauche = list.get(3).getCouleur();
        for (Mur mur : list) {
            if (mur.estAffichee()) {
                if (mur.getCote() == FACADE) {
                    if (afficherVoisins) {
                        dessinerVoisins(toit, list, mur, g);
                        if (Chalet.getSensToit() == GAUCHE || Chalet.getSensToit() == DROITE) {
                            Rectangle2D.Double debordementArrierePourGauche = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() - mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double debordementFacadePourGauche = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() + mur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double retraitDebordementArriere = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() - espacementRetrait / 24 * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), espacementRetrait / 24 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double retraitDebordementFacade = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() + mur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), espacementRetrait / 24 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double retraitGaucheduMurGauche = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX(),
                                    mur.getOriginalShape().getBounds().getY(), espacementRetrait / 24 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double retraitDroiteduMurGauche = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() + (mur.getLongueur().pieds() - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), (espacementRetrait) / 24 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);

                            Area areaRetraitGaucheduMurGauche = new Area(retraitGaucheduMurGauche);
                            Area areaRetraitDroiteduMurGauche = new Area(retraitDroiteduMurGauche);
                            Area murFacade = new Area(mur.getOriginalShape());
                            Shape shapeMurFacade = mur.getShape();
                            Area areaDebordementArrierePourGauche = new Area(debordementArrierePourGauche);
                            Area areaDebordementFacadePourGauche = new Area(debordementFacadePourGauche);
                            Area areaRetraitDebordementArriere = new Area(retraitDebordementArriere);
                            Area areaRetraitDebordementFacade = new Area(retraitDebordementFacade);
                            areaDebordementFacadePourGauche.subtract(areaRetraitDebordementFacade);
                            areaDebordementArrierePourGauche.subtract(areaRetraitDebordementArriere);
                            murFacade.subtract(areaRetraitGaucheduMurGauche);
                            murFacade.subtract(areaRetraitDroiteduMurGauche);
                            g2.setColor(mur.getCouleur());
                            g2.fill(murFacade);
                            g4.setColor(couleurDebordementGauche);
                            g4.fill(areaDebordementArrierePourGauche);
                            g1.setColor(couleurDebordementDroite);
                            g1.fill(areaDebordementFacadePourGauche);
                            p_tableau.put(FACADE.orientation + "I", new CaisseOutils.Tuple<>(areaDebordementArrierePourGauche, areaDebordementFacadePourGauche));
                            p_tableau.put(FACADE.orientation + "II", new CaisseOutils.Tuple<>(new Area(new Rectangle2D.Double(murFacade.getBounds2D().getX(), murFacade.getBounds2D().getY(), murFacade.getBounds2D().getWidth(), murFacade.getBounds2D().getHeight())), null));
                        }
                    }
                    Area murFacade = new Area(mur.getShape());
                    g2.setColor(mur.getCouleur());
                    g2.fill(murFacade);
                }
                if (mur.getCote() == ARRIERE) {
                    if (afficherVoisins) {
                        dessinerVoisins(toit, list, mur, g);
                        if (Chalet.getSensToit() == GAUCHE || Chalet.getSensToit() == DROITE) {
                            Rectangle2D.Double debordementArrierePourGauche = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() - mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double debordementFacadePourGauche = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() + mur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double retraitDebordementArriere = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() - espacementRetrait / 24 * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), espacementRetrait / 24 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double retraitDebordementFacade = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() + mur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), espacementRetrait / 24 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double retraitGaucheduMurGauche = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX(),
                                    mur.getOriginalShape().getBounds().getY(), espacementRetrait / 24 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double retraitDroiteduMurGauche = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() + (mur.getLongueur().pieds() - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), (espacementRetrait) / 24 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);

                            Area areaRetraitGaucheduMurGauche = new Area(retraitGaucheduMurGauche);
                            Area areaRetraitDroiteduMurGauche = new Area(retraitDroiteduMurGauche);
                            Area murGauche = new Area(mur.getOriginalShape());
                            Shape shapeMurGauche = murGauche;
                            Area areaDebordementArrierePourGauche = new Area(debordementArrierePourGauche);
                            Area areaDebordementFacadePourGauche = new Area(debordementFacadePourGauche);
                            Area areaRetraitDebordementArriere = new Area(retraitDebordementArriere);
                            Area areaRetraitDebordementFacade = new Area(retraitDebordementFacade);
                            areaDebordementFacadePourGauche.subtract(areaRetraitDebordementFacade);
                            areaDebordementArrierePourGauche.subtract(areaRetraitDebordementArriere);
                            murGauche.subtract(areaRetraitGaucheduMurGauche);
                            murGauche.subtract(areaRetraitDroiteduMurGauche);
                            g2.setColor(mur.getCouleur());
                            g2.fill(murGauche);
                            g4.setColor(couleurDebordementDroite);
                            g4.fill(areaDebordementArrierePourGauche);
                            g1.setColor(couleurDebordementGauche);
                            g1.fill(areaDebordementFacadePourGauche);
                            p_tableau.put(ARRIERE.orientation + "I", new CaisseOutils.Tuple<>(areaDebordementArrierePourGauche, areaDebordementFacadePourGauche));
                            p_tableau.put(ARRIERE.orientation + "II", new CaisseOutils.Tuple<>(new Area(new Rectangle2D.Double(shapeMurGauche.getBounds2D().getX(), shapeMurGauche.getBounds2D().getY(), shapeMurGauche.getBounds2D().getWidth(), shapeMurGauche.getBounds2D().getHeight())), null));
                        }
                    }
                    Area murArriere = new Area(mur.getShape());
                    g2.setColor(mur.getCouleur());
                    g2.fill(murArriere);
                }
                if (mur.getCote() == GAUCHE) {
                    if (afficherVoisins) {
                        dessinerVoisins(toit, list, mur, g);
                        if (Chalet.getSensToit() == FACADE || Chalet.getSensToit() == ARRIERE) {
                            Rectangle2D.Double debordementArrierePourGauche = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() - mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double debordementFacadePourGauche = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() + mur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double retraitDebordementArriere = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() - espacementRetrait / 24 * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), espacementRetrait / 24 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double retraitDebordementFacade = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() + mur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), espacementRetrait / 24 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double retraitGaucheduMurGauche = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX(),
                                    mur.getOriginalShape().getBounds().getY(), espacementRetrait / 24 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double retraitDroiteduMurGauche = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() + (mur.getLongueur().pieds() - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), (espacementRetrait) / 24 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);

                            Area areaRetraitGaucheduMurGauche = new Area(retraitGaucheduMurGauche);
                            Area areaRetraitDroiteduMurGauche = new Area(retraitDroiteduMurGauche);
                            Area murGauche = new Area(mur.getOriginalShape());
                            Shape shapeMurGauche = murGauche;
                            Area areaDebordementArrierePourGauche = new Area(debordementArrierePourGauche);
                            Area areaDebordementFacadePourGauche = new Area(debordementFacadePourGauche);
                            Area areaRetraitDebordementArriere = new Area(retraitDebordementArriere);
                            Area areaRetraitDebordementFacade = new Area(retraitDebordementFacade);
                            areaDebordementFacadePourGauche.subtract(areaRetraitDebordementFacade);
                            areaDebordementArrierePourGauche.subtract(areaRetraitDebordementArriere);
                            murGauche.subtract(areaRetraitGaucheduMurGauche);
                            murGauche.subtract(areaRetraitDroiteduMurGauche);
                            g2.setColor(mur.getCouleur());
                            g2.fill(murGauche);
                            g4.setColor(couleurDebordementArriere);
                            g4.fill(areaDebordementArrierePourGauche);
                            g1.setColor(couleurDebordementFacade);
                            g1.fill(areaDebordementFacadePourGauche);
                            p_tableau.put(GAUCHE.orientation + "I", new CaisseOutils.Tuple<>(areaDebordementArrierePourGauche, areaDebordementFacadePourGauche));
                            p_tableau.put(GAUCHE.orientation + "II", new CaisseOutils.Tuple<>(new Area(new Rectangle2D.Double(shapeMurGauche.getBounds2D().getX(), shapeMurGauche.getBounds2D().getY(), shapeMurGauche.getBounds2D().getWidth(), shapeMurGauche.getBounds2D().getHeight())), null));
                        }
                    }
                    Area murGauche = new Area(mur.getOriginalShape());
                    g2.setColor(mur.getCouleur());
                    g2.fill(murGauche);
                } else if (mur.getCote() == DROITE) {
                    if (afficherVoisins) {
                        dessinerVoisins(toit, list, mur, g);
                        if (Chalet.getSensToit() == FACADE || Chalet.getSensToit() == ARRIERE) {
                            Rectangle2D.Double debordementFacadePourDroite = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() - mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double debordementArrierePourDroite = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() + mur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double retraitDebordementFacade = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() - espacementRetrait / 24 * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), espacementRetrait / 24 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double retraitDebordementArriere = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() + mur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), espacementRetrait / 24 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double retraitGaucheduMurDroite = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX(),
                                    mur.getOriginalShape().getBounds().getY(), espacementRetrait / 24 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Rectangle2D.Double retraitDroiteduMurDroite = new Rectangle2D.Double(mur.getOriginalShape().getBounds().getX() + (mur.getLongueur().pieds() - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                    mur.getOriginalShape().getBounds().getY(), (espacementRetrait) / 24 * CaisseOutils.RATIO_TEST, mur.getLargeur().pieds() * CaisseOutils.RATIO_TEST);
                            Area areaRetraitGaucheduMurDroite = new Area(retraitGaucheduMurDroite);
                            Area areaRetraitDroiteduMurDroite = new Area(retraitDroiteduMurDroite);
                            Area murDroite = new Area(mur.getOriginalShape());
                            Shape shapeMurDroite = murDroite;
                            Area areaDebordementFacadePourDroite = new Area(debordementFacadePourDroite);
                            Area areaDebordementArrierePourDroite = new Area(debordementArrierePourDroite);
                            Area areaRetraitDebordementArriere = new Area(retraitDebordementArriere);
                            Area areaRetraitDebordementFacade = new Area(retraitDebordementFacade);
                            areaDebordementFacadePourDroite.subtract(areaRetraitDebordementFacade);
                            areaDebordementArrierePourDroite.subtract(areaRetraitDebordementArriere);
                            murDroite.subtract(areaRetraitGaucheduMurDroite);
                            murDroite.subtract(areaRetraitDroiteduMurDroite);
                            g2.setColor(mur.getCouleur());
                            g2.fill(murDroite);
                            g4.setColor(couleurDebordementFacade);
                            g4.fill(areaDebordementFacadePourDroite);
                            g1.setColor(couleurDebordementArriere);
                            g1.fill(areaDebordementArrierePourDroite);
                            p_tableau.put(DROITE.orientation + "I", new CaisseOutils.Tuple<>(areaDebordementFacadePourDroite, areaDebordementArrierePourDroite));
                            p_tableau.put(DROITE.orientation + "II", new CaisseOutils.Tuple<>(new Area(new Rectangle2D.Double(shapeMurDroite.getBounds2D().getX(), shapeMurDroite.getBounds2D().getY(), shapeMurDroite.getBounds2D().getWidth(), shapeMurDroite.getBounds2D().getHeight())), null));
                        }
                    }
                    Area murDroite = new Area(mur.getOriginalShape());
                    g2.setColor(mur.getCouleur());
                    g2.fill(murDroite);
                }
                List<Accessoire> listes = mur.getAccessoiresListes();
                for (Accessoire pAcc : listes) {
                    Graphics2D g3 = (Graphics2D) g;
                    if (pAcc instanceof Porte)
                        g3.setColor(pAcc.getCouleur());
                    else
                        g3.setColor(pAcc.getCouleur());
                    if (movingAccessory && pAcc.estSelectionne()) {

                    } else
                        g3.fill(pAcc.getShape());
                }

                /* gere l'affichement de l'accessoire déplacé*/
                Accessoire accessoireSelectionne = null;
                UniteImperiale[] coordonneeAccDeplacement = null;

                if (movingAccessory) {
                    for (Accessoire accessoire : mur.getAccessoiresListes()) {
                        if (accessoire.estSelectionne()
                                && selectedAccessoryShape.getBounds().getLocation().x > mur.getShape().getBounds().getLocation().x
                                && selectedAccessoryShape.getBounds().getLocation().y > mur.getShape().getBounds().getLocation().y) {
                            g4.setColor(accessoire.getCouleur());
                            accessoireSelectionne = accessoire;
                            coordonneeAccDeplacement = CaisseOutils.ajusterCoordonneesToEmplacementToUniImp(mur.getShape().getBounds().getLocation(),
                                    selectedAccessoryShape.getBounds().getLocation());
                            deplacementAccessoire = coordonneeAccDeplacement;
                            break;
                        }
                    }
                    if (selectedAccessoryShape.getBounds().getLocation().x > mur.getShape().getBounds().getLocation().x
                            && selectedAccessoryShape.getBounds().getLocation().y > mur.getShape().getBounds().getLocation().y) {
                        coordonneeAccDeplacement = CaisseOutils.ajusterCoordonneesToEmplacementToUniImp(mur.getShape().getBounds().getLocation(),
                                selectedAccessoryShape.getBounds().getLocation());
                        deplacementAccessoire = coordonneeAccDeplacement;
                        if (vueCourante != null && mur.validerFenetre(coordonneeAccDeplacement, new float[]{DIMENSION_PANNEAU_DEFAUT.pieds(), DIMENSION_PANNEAU_DEFAUT.pieds()})) {
                            g4.setColor(hoverColor);
                            g4.fill(selectedAccessoryShape);
                        }
                    }
                }


                if (accessoireSelectionne != null && movingAccessory
                        && mur.validerFenetre(coordonneeAccDeplacement, new float[]{accessoireSelectionne.getLongueur().pieds(), accessoireSelectionne.getLargeur().pieds()})) {
                    g4.setColor(accessoireSelectionne.getCouleur());
                    g4.fill(selectedAccessoryShape);
                }

            }
        }
        graphicsZoomCourant = (Graphics2D) g.create();

        return p_tableau;
    }


    private void dessinerVoisins(List<PanneauToit> pToit, List<Mur> list, Mur pMur, Graphics g) {
        Mur murGauche = list.get(2);
        PanneauToit pignonGauche = pToit.get(0);
        PanneauToit pignonDroite = pToit.get(1);
        PanneauToit rallongeVerticalee = pToit.get(2);
        PanneauToit partieSuperieuree = pToit.get(3);
        float angle = (float) ((Math.tan(Math.toRadians(pignonGauche.getAngle()))));
        float anglePartieSuperieureBas = (float) ((Math.tan(Math.toRadians(37.5))));
        float longueurPartieSuperieureHaut = (float) ((pMur.getEpaisseur().pieds() / 2) / (Math.cos(Math.toRadians(15))));
//        System.out.println(angle);
        if (Chalet.getSensToit() == ARRIERE) {
            if (pMur.getCote() == GAUCHE) {
                // Dessiner Toit :
                // Dessiner PignonGauche
                float[] pointsX = {(float) pMur.getOriginalShape().getBounds().getX(),
                        (float) pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getX()};
                float[] pointsY = {(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST};
//                Path2D.Float pignonDroite = new Path2D.Float();
//                pignonDroite.moveTo(pointsX[0], pointsY[0]);
//                for (int i = 1; i < pointsX.length; i++) {
//                    pignonDroite.lineTo(pointsX[i], pointsY[i]);
//                }
//                pignonDroite.closePath();
                // Dessiner RallongeVerticale
//                pointsX = new float[]{(float) (pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST),
//                        (float) (pMur.getOriginalShape().getBounds().getX()), (float) (pMur.getOriginalShape().getBounds().getX()),
//                        (float) (pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST)};
//                pointsY = new float[]{(float) pMur.getOriginalShape().getBounds().getY(),
//                        (float) pMur.getOriginalShape().getBounds().getY(),
//                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
//                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST - angle * pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST};
//                Path2D.Float rallongeVerticale = new Path2D.Float();
//                rallongeVerticale.moveTo(pointsX[0], pointsY[0]);
//                for (int i = 1; i < pointsX.length; i++) {
//                    rallongeVerticale.lineTo(pointsX[i], pointsY[i]);
//                }
//                rallongeVerticale.closePath();
                // Dessiner PartieSuperieure
                pointsX = new float[]{(float) (pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST)
                };
                pointsY = new float[]{(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - anglePartieSuperieureBas * (pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST),
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST - anglePartieSuperieureBas * pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST
                };
                Path2D.Float partieSuperieure = new Path2D.Float();
                partieSuperieure.moveTo(pointsX[0], pointsY[0]);
                for (int i = 1; i < pointsX.length; i++) {
                    partieSuperieure.lineTo(pointsX[i], pointsY[i]);
                }
                partieSuperieure.closePath();
                Graphics2D g5 = (Graphics2D) g;
                if (pignonGauche.estAffichee()) {
                    g5.setColor(pignonGauche.getCouleur());
                    g5.fill(pignonGauche.getShape());
                }
                if (rallongeVerticalee.estAffichee()) {
                    g5.setColor(rallongeVerticalee.getCouleur());
                    g5.fill(rallongeVerticalee.getShape());
                }
                g5.setColor(Affichable.COULEUR_PARTIE_SUPERIEURE);
                g5.fill(partieSuperieure);
            } else if (pMur.getCote() == DROITE) {
                //Dessiner Toit :
                // Dessiner PignonDroite
                float[] pointsX = {(float) pMur.getOriginalShape().getBounds().getX(),
                        (float) pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST};
                float[] pointsY = {(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST};
//                Path2D.Float pignonDroite = new Path2D.Float();
//                pignonDroite.moveTo(pointsX[0], pointsY[0]);
//                for (int i = 1; i < pointsX.length; i++) {
//                    pignonDroite.lineTo(pointsX[i], pointsY[i]);
//                }
//                pignonDroite.closePath();
                // Dessiner RallongeVerticale
//                pointsX = new float[]{(float) (pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST),
//                        (float) pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST, (float) pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
//                        (float) (pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST)};
//                pointsY = new float[]{(float) pMur.getOriginalShape().getBounds().getY(),
//                        (float) pMur.getOriginalShape().getBounds().getY(),
//                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
//                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST};
//                Path2D.Float rallongeVerticale = new Path2D.Float();
//                rallongeVerticale.moveTo(pointsX[0], pointsY[0]);
//                for (int i = 1; i < pointsX.length; i++) {
//                    rallongeVerticale.lineTo(pointsX[i], pointsY[i]);
//                }
//                rallongeVerticale.closePath();
                // Dessiner PartieSuperieure
                pointsX = new float[]{(float) pMur.getOriginalShape().getBounds().getX(),
                        (float) pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) (pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST),
                };
                pointsY = new float[]{(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        //TODO 2.05 est modifiable avec un calcul trigonometrique
                        //TODO les lignes suivantes doivent etre partieSuperieure.getEpaisseur() et non pas mur.getEpaisseur()
                        (float) pMur.getOriginalShape().getBounds().getY() - anglePartieSuperieureBas * pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST - anglePartieSuperieureBas * pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST
                };
                Path2D.Float partieSuperieure = new Path2D.Float();
                partieSuperieure.moveTo(pointsX[0], pointsY[0]);
                for (int i = 1; i < pointsX.length; i++) {
                    partieSuperieure.lineTo(pointsX[i], pointsY[i]);
                }
                partieSuperieure.closePath();
                Graphics2D g5 = (Graphics2D) g;
                if (pignonDroite.estAffichee()) {
                    g5.setColor(pignonDroite.getCouleur());
                    g5.fill(pignonDroite.getShape());
                }
                if (rallongeVerticalee.estAffichee()) {
                    g5.setColor(rallongeVerticalee.getCouleur());
                    g5.fill(rallongeVerticalee.getShape());
                }
                g5.setColor(Affichable.COULEUR_PARTIE_SUPERIEURE);
                g5.fill(partieSuperieure);
            } else if (pMur.getCote() == FACADE) {
                Rectangle2D.Double partieSuperieure = new Rectangle2D.Double(pMur.getOriginalShape().getBounds().getX(),
                        pMur.getOriginalShape().getBounds().getY() - ((angle * (murGauche.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2)) + longueurPartieSuperieureHaut) * CaisseOutils.RATIO_TEST, pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        ((angle * (murGauche.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2)) + longueurPartieSuperieureHaut) * CaisseOutils.RATIO_TEST);
                Graphics2D g3 = (Graphics2D) g;
                g3.setColor(Affichable.COULEUR_PARTIE_SUPERIEURE);
                g3.fill(partieSuperieure);
            } else if (pMur.getCote() == ARRIERE) {
                Rectangle2D.Double partieSuperieure = new Rectangle2D.Double(pMur.getOriginalShape().getBounds().getX(),
                        pMur.getOriginalShape().getBounds().getY() - ((angle * (murGauche.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2)) + longueurPartieSuperieureHaut) * CaisseOutils.RATIO_TEST, pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        longueurPartieSuperieureHaut * CaisseOutils.RATIO_TEST);
                Rectangle2D.Double rallongeVerticale = new Rectangle2D.Double(pMur.getOriginalShape().getBounds().getX(),
                        pMur.getOriginalShape().getBounds().getY() - (angle * (murGauche.getLongueur().pieds() + murGauche.getEpaisseur().pieds() / 2)) * CaisseOutils.RATIO_TEST, pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        (angle * (murGauche.getLongueur().pieds() + murGauche.getEpaisseur().pieds() / 2)) * CaisseOutils.RATIO_TEST);
                Graphics2D g3 = (Graphics2D) g;
                g3.setColor(Affichable.COULEUR_PARTIE_SUPERIEURE);
                g3.fill(partieSuperieure);
                g3.setColor(rallongeVerticalee.getCouleur());
                g3.fill(rallongeVerticale);
            }
        } else if (Chalet.getSensToit() == FACADE) {
            if (pMur.getCote() == GAUCHE) {
                //Dessiner Toit :
                // Dessiner PignonDroite
                float[] pointsX = {(float) pMur.getOriginalShape().getBounds().getX(),
                        (float) pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST};
                float[] pointsY = {(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST};
//                Path2D.Float pignonDroite = new Path2D.Float();
//                pignonDroite.moveTo(pointsX[0], pointsY[0]);
//                for (int i = 1; i < pointsX.length; i++) {
//                    pignonDroite.lineTo(pointsX[i], pointsY[i]);
//                }
//                pignonDroite.closePath();
                // Dessiner RallongeVerticale
                pointsX = new float[]{(float) (pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST),
                        (float) pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST, (float) pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                        (float) (pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST)};
                pointsY = new float[]{(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST};
                Path2D.Float rallongeVerticale = new Path2D.Float();
                rallongeVerticale.moveTo(pointsX[0], pointsY[0]);
                for (int i = 1; i < pointsX.length; i++) {
                    rallongeVerticale.lineTo(pointsX[i], pointsY[i]);
                }
                rallongeVerticale.closePath();
                // Dessiner PartieSuperieure
                pointsX = new float[]{(float) pMur.getOriginalShape().getBounds().getX(),
                        (float) pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) (pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST),
                };
                pointsY = new float[]{(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - anglePartieSuperieureBas * pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST - anglePartieSuperieureBas * pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST
                };
                Path2D.Float partieSuperieure = new Path2D.Float();
                partieSuperieure.moveTo(pointsX[0], pointsY[0]);
                for (int i = 1; i < pointsX.length; i++) {
                    partieSuperieure.lineTo(pointsX[i], pointsY[i]);
                }
                partieSuperieure.closePath();
                Graphics2D g5 = (Graphics2D) g;
                System.out.println("I AM HERE IN IF FACADE (AFFICHER) : MON PIGNON GAUCHE EST AFFICHEE =" + pignonGauche.estAffichee());
                if (pignonDroite.estAffichee()) {
                    g5.setColor(pignonDroite.getCouleur());
                    g5.fill(pignonDroite.getShape());
                }
                if (rallongeVerticalee.estAffichee()) {
                    g5.setColor(rallongeVerticalee.getCouleur());
                    g5.fill(rallongeVerticale);
                }
                g5.setColor(Affichable.COULEUR_PARTIE_SUPERIEURE);
                g5.fill(partieSuperieure);
            } else if (pMur.getCote() == DROITE) {
                // Dessiner Toit :
                // Dessiner PignonGauche
                float[] pointsX = {(float) pMur.getOriginalShape().getBounds().getX(),
                        (float) pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getX()};
                float[] pointsY = {(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST};
//                Path2D.Float pignonDroite = new Path2D.Float();
//                pignonDroite.moveTo(pointsX[0], pointsY[0]);
//                for (int i = 1; i < pointsX.length; i++) {
//                    pignonDroite.lineTo(pointsX[i], pointsY[i]);
//                }
//                pignonDroite.closePath();
                // Dessiner RallongeVerticale
                pointsX = new float[]{(float) (pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX()), (float) (pMur.getOriginalShape().getBounds().getX()),
                        (float) (pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST)};
                pointsY = new float[]{(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST - angle * pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST};
                Path2D.Float rallongeVerticale = new Path2D.Float();
                rallongeVerticale.moveTo(pointsX[0], pointsY[0]);
                for (int i = 1; i < pointsX.length; i++) {
                    rallongeVerticale.lineTo(pointsX[i], pointsY[i]);
                }
                rallongeVerticale.closePath();
                // Dessiner PartieSuperieure
                pointsX = new float[]{(float) (pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST)
                };
                pointsY = new float[]{(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - anglePartieSuperieureBas * (pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST),
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST - anglePartieSuperieureBas * pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST
                };
                Path2D.Float partieSuperieure = new Path2D.Float();
                partieSuperieure.moveTo(pointsX[0], pointsY[0]);
                for (int i = 1; i < pointsX.length; i++) {
                    partieSuperieure.lineTo(pointsX[i], pointsY[i]);
                }
                partieSuperieure.closePath();
                Graphics2D g5 = (Graphics2D) g;
                if (pignonGauche.estAffichee()) {
                    g5.setColor(pignonGauche.getCouleur());
                    g5.fill(pignonGauche.getShape());
                }
                if (rallongeVerticalee.estAffichee()) {
                    g5.setColor(rallongeVerticalee.getCouleur());
                    g5.fill(rallongeVerticalee.getShape());
                }
                g5.setColor(Affichable.COULEUR_PARTIE_SUPERIEURE);
                g5.fill(partieSuperieure);
            } else if (pMur.getCote() == FACADE) {
                Rectangle2D.Double partieSuperieure = new Rectangle2D.Double(pMur.getOriginalShape().getBounds().getX(),
                        pMur.getOriginalShape().getBounds().getY() - ((angle * (murGauche.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2)) + longueurPartieSuperieureHaut) * CaisseOutils.RATIO_TEST, pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        longueurPartieSuperieureHaut * CaisseOutils.RATIO_TEST);
                Rectangle2D.Double rallongeVerticale = new Rectangle2D.Double(pMur.getOriginalShape().getBounds().getX(),
                        pMur.getOriginalShape().getBounds().getY() - (angle * (murGauche.getLongueur().pieds() + murGauche.getEpaisseur().pieds() / 2)) * CaisseOutils.RATIO_TEST, pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        (angle * (murGauche.getLongueur().pieds() + murGauche.getEpaisseur().pieds() / 2)) * CaisseOutils.RATIO_TEST);
                Graphics2D g3 = (Graphics2D) g;
                g3.setColor(Affichable.COULEUR_PARTIE_SUPERIEURE);
                g3.fill(partieSuperieure);
                g3.setColor(Affichable.COULEUR_RALLONGE_VERTICALE);
                g3.fill(rallongeVerticale);
            } else if (pMur.getCote() == ARRIERE) {
                Rectangle2D.Double partieSuperieure = new Rectangle2D.Double(pMur.getOriginalShape().getBounds().getX(),
                        pMur.getOriginalShape().getBounds().getY() - ((angle * (murGauche.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2)) + longueurPartieSuperieureHaut) * CaisseOutils.RATIO_TEST, pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        ((angle * (murGauche.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2)) + longueurPartieSuperieureHaut) * CaisseOutils.RATIO_TEST);
                Graphics2D g3 = (Graphics2D) g;
                g3.setColor(Affichable.COULEUR_PARTIE_SUPERIEURE);
                g3.fill(partieSuperieure);
            }
        } else if (Chalet.getSensToit() == GAUCHE) {
            if (pMur.getCote() == GAUCHE) {
                Rectangle2D.Double partieSuperieure = new Rectangle2D.Double(pMur.getOriginalShape().getBounds().getX(),
                        pMur.getOriginalShape().getBounds().getY() - ((angle * (murGauche.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2)) + longueurPartieSuperieureHaut) * CaisseOutils.RATIO_TEST, pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        longueurPartieSuperieureHaut * CaisseOutils.RATIO_TEST);
                Rectangle2D.Double rallongeVerticale = new Rectangle2D.Double(pMur.getOriginalShape().getBounds().getX(),
                        pMur.getOriginalShape().getBounds().getY() - (angle * (murGauche.getLongueur().pieds() + murGauche.getEpaisseur().pieds() / 2)) * CaisseOutils.RATIO_TEST, pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        (angle * (murGauche.getLongueur().pieds() + murGauche.getEpaisseur().pieds() / 2)) * CaisseOutils.RATIO_TEST);
                Graphics2D g3 = (Graphics2D) g;
                g3.setColor(Affichable.COULEUR_PARTIE_SUPERIEURE);
                g3.fill(partieSuperieure);
                g3.setColor(Affichable.COULEUR_RALLONGE_VERTICALE);
                g3.fill(rallongeVerticale);
            } else if (pMur.getCote() == DROITE) {
                Rectangle2D.Double partieSuperieure = new Rectangle2D.Double(pMur.getOriginalShape().getBounds().getX(),
                        pMur.getOriginalShape().getBounds().getY() - ((angle * (murGauche.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2)) + longueurPartieSuperieureHaut) * CaisseOutils.RATIO_TEST, pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        ((angle * (murGauche.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2)) + longueurPartieSuperieureHaut) * CaisseOutils.RATIO_TEST);
                Graphics2D g3 = (Graphics2D) g;
                g3.setColor(Affichable.COULEUR_PARTIE_SUPERIEURE);
                g3.fill(partieSuperieure);
            } else if (pMur.getCote() == FACADE) {
                // Dessiner Toit :
                // Dessiner PignonGauche
                float[] pointsX = {(float) pMur.getOriginalShape().getBounds().getX(),
                        (float) pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getX()};
                float[] pointsY = {(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST};
//                Path2D.Float pignonDroite = new Path2D.Float();
//                pignonDroite.moveTo(pointsX[0], pointsY[0]);
//                for (int i = 1; i < pointsX.length; i++) {
//                    pignonDroite.lineTo(pointsX[i], pointsY[i]);
//                }
//                pignonDroite.closePath();
                // Dessiner RallongeVerticale
                pointsX = new float[]{(float) (pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX()), (float) (pMur.getOriginalShape().getBounds().getX()),
                        (float) (pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST)};
                pointsY = new float[]{(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST - angle * pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST};
                Path2D.Float rallongeVerticale = new Path2D.Float();
                rallongeVerticale.moveTo(pointsX[0], pointsY[0]);
                for (int i = 1; i < pointsX.length; i++) {
                    rallongeVerticale.lineTo(pointsX[i], pointsY[i]);
                }
                rallongeVerticale.closePath();
                // Dessiner PartieSuperieure
                pointsX = new float[]{(float) (pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST)
                };
                pointsY = new float[]{(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - anglePartieSuperieureBas * (pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST),
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST - anglePartieSuperieureBas * pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST
                };
                Path2D.Float partieSuperieure = new Path2D.Float();
                partieSuperieure.moveTo(pointsX[0], pointsY[0]);
                for (int i = 1; i < pointsX.length; i++) {
                    partieSuperieure.lineTo(pointsX[i], pointsY[i]);
                }
                partieSuperieure.closePath();
                Graphics2D g5 = (Graphics2D) g;
                if (pignonGauche.estAffichee()) {
                    g5.setColor(pignonGauche.getCouleur());
                    g5.fill(pignonGauche.getShape());
                }
                if (rallongeVerticalee.estAffichee()) {
                    g5.setColor(rallongeVerticalee.getCouleur());
                    g5.fill(rallongeVerticale);
                }
                g5.setColor(Affichable.COULEUR_PARTIE_SUPERIEURE);
                g5.fill(partieSuperieure);

            } else if (pMur.getCote() == ARRIERE) {
                //Dessiner Toit :
                // Dessiner PignonDroite
                float[] pointsX = {(float) pMur.getOriginalShape().getBounds().getX(),
                        (float) pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST};
                float[] pointsY = {(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST};
//                Path2D.Float pignonDroite = new Path2D.Float();
//                pignonDroite.moveTo(pointsX[0], pointsY[0]);
//                for (int i = 1; i < pointsX.length; i++) {
//                    pignonDroite.lineTo(pointsX[i], pointsY[i]);
//                }
//                pignonDroite.closePath();
                // Dessiner RallongeVerticale
                pointsX = new float[]{(float) (pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST),
                        (float) pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST, (float) pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                        (float) (pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST)};
                pointsY = new float[]{(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST};
                Path2D.Float rallongeVerticale = new Path2D.Float();
                rallongeVerticale.moveTo(pointsX[0], pointsY[0]);
                for (int i = 1; i < pointsX.length; i++) {
                    rallongeVerticale.lineTo(pointsX[i], pointsY[i]);
                }
                rallongeVerticale.closePath();
                // Dessiner PartieSuperieure
                pointsX = new float[]{(float) pMur.getOriginalShape().getBounds().getX(),
                        (float) pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) (pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST),
                };
                pointsY = new float[]{(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - anglePartieSuperieureBas * pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST - anglePartieSuperieureBas * pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST
                };
                Path2D.Float partieSuperieure = new Path2D.Float();
                partieSuperieure.moveTo(pointsX[0], pointsY[0]);
                for (int i = 1; i < pointsX.length; i++) {
                    partieSuperieure.lineTo(pointsX[i], pointsY[i]);
                }
                partieSuperieure.closePath();
                Graphics2D g5 = (Graphics2D) g;
                if (pignonDroite.estAffichee()) {
                    g5.setColor(pignonDroite.getCouleur());
                    g5.fill(pignonDroite.getShape());
                }
                if (rallongeVerticalee.estAffichee()) {
                    g5.setColor(rallongeVerticalee.getCouleur());
                    g5.fill(rallongeVerticalee.getShape());
                }
                g5.setColor(Affichable.COULEUR_PARTIE_SUPERIEURE);
                g5.fill(partieSuperieure);
            }
        } else if (Chalet.getSensToit() == DROITE) {
            if (pMur.getCote() == GAUCHE) {
                Rectangle2D.Double partieSuperieure = new Rectangle2D.Double(pMur.getOriginalShape().getBounds().getX(),
                        pMur.getOriginalShape().getBounds().getY() - ((angle * (murGauche.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2)) + longueurPartieSuperieureHaut) * CaisseOutils.RATIO_TEST, pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        ((angle * (murGauche.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2)) + longueurPartieSuperieureHaut) * CaisseOutils.RATIO_TEST);
                Graphics2D g3 = (Graphics2D) g;
                g3.setColor(Affichable.COULEUR_PARTIE_SUPERIEURE);
                g3.fill(partieSuperieure);
            } else if (pMur.getCote() == DROITE) {
                Rectangle2D.Double partieSuperieure = new Rectangle2D.Double(pMur.getOriginalShape().getBounds().getX(),
                        pMur.getOriginalShape().getBounds().getY() - ((angle * (murGauche.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2)) + longueurPartieSuperieureHaut) * CaisseOutils.RATIO_TEST, pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        longueurPartieSuperieureHaut * CaisseOutils.RATIO_TEST);
                Rectangle2D.Double rallongeVerticale = new Rectangle2D.Double(pMur.getOriginalShape().getBounds().getX(),
                        pMur.getOriginalShape().getBounds().getY() - (angle * (murGauche.getLongueur().pieds() + murGauche.getEpaisseur().pieds() / 2)) * CaisseOutils.RATIO_TEST, pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        (angle * (murGauche.getLongueur().pieds() + murGauche.getEpaisseur().pieds() / 2)) * CaisseOutils.RATIO_TEST);
                Graphics2D g3 = (Graphics2D) g;
                g3.setColor(Affichable.COULEUR_PARTIE_SUPERIEURE);
                g3.fill(partieSuperieure);
                g3.setColor(Affichable.COULEUR_RALLONGE_VERTICALE);
                g3.fill(rallongeVerticale);
            } else if (pMur.getCote() == FACADE) {
                //Dessiner Toit :
                // Dessiner PignonDroite
                float[] pointsX = {(float) pMur.getOriginalShape().getBounds().getX(),
                        (float) pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST};
                float[] pointsY = {(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST};
//                Path2D.Float pignonDroite = new Path2D.Float();
//                pignonDroite.moveTo(pointsX[0], pointsY[0]);
//                for (int i = 1; i < pointsX.length; i++) {
//                    pignonDroite.lineTo(pointsX[i], pointsY[i]);
//                }
//                pignonDroite.closePath();
                // Dessiner RallongeVerticale
                pointsX = new float[]{(float) (pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST),
                        (float) pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST, (float) pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                        (float) (pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST)};
                pointsY = new float[]{(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST};
                Path2D.Float rallongeVerticale = new Path2D.Float();
                rallongeVerticale.moveTo(pointsX[0], pointsY[0]);
                for (int i = 1; i < pointsX.length; i++) {
                    rallongeVerticale.lineTo(pointsX[i], pointsY[i]);
                }
                rallongeVerticale.closePath();
                // Dessiner PartieSuperieure
                pointsX = new float[]{(float) pMur.getOriginalShape().getBounds().getX(),
                        (float) pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) (pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST),
                };
                pointsY = new float[]{(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        //TODO 2.05 est modifiable avec un calcul trigonometrique
                        //TODO les lignes suivantes doivent etre partieSuperieure.getEpaisseur() et non pas mur.getEpaisseur()
                        (float) pMur.getOriginalShape().getBounds().getY() - anglePartieSuperieureBas * pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST - anglePartieSuperieureBas * pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST
                };
                Path2D.Float partieSuperieure = new Path2D.Float();
                partieSuperieure.moveTo(pointsX[0], pointsY[0]);
                for (int i = 1; i < pointsX.length; i++) {
                    partieSuperieure.lineTo(pointsX[i], pointsY[i]);
                }
                partieSuperieure.closePath();
                Graphics2D g5 = (Graphics2D) g;
                if (pignonDroite.estAffichee()) {
                    g5.setColor(pignonDroite.getCouleur());
                    g5.fill(pignonDroite.getShape());
                }
                if (rallongeVerticalee.estAffichee()) {
                    g5.setColor(rallongeVerticalee.getCouleur());
                    g5.fill(rallongeVerticalee.getShape());
                }
                g5.setColor(Affichable.COULEUR_PARTIE_SUPERIEURE);
                g5.fill(partieSuperieure);
            } else if (pMur.getCote() == ARRIERE) {
                /// Dessiner Toit :
                // Dessiner PignonGauche
                float[] pointsX = {(float) pMur.getOriginalShape().getBounds().getX(),
                        (float) pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getX()};
                float[] pointsY = {(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST};
//                Path2D.Float pignonDroite = new Path2D.Float();
//                pignonDroite.moveTo(pointsX[0], pointsY[0]);
//                for (int i = 1; i < pointsX.length; i++) {
//                    pignonDroite.lineTo(pointsX[i], pointsY[i]);
//                }
//                pignonDroite.closePath();
                // Dessiner RallongeVerticale
                pointsX = new float[]{(float) (pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX()), (float) (pMur.getOriginalShape().getBounds().getX()),
                        (float) (pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST)};
                pointsY = new float[]{(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST - angle * pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST};
                Path2D.Float rallongeVerticale = new Path2D.Float();
                rallongeVerticale.moveTo(pointsX[0], pointsY[0]);
                for (int i = 1; i < pointsX.length; i++) {
                    rallongeVerticale.lineTo(pointsX[i], pointsY[i]);
                }
                rallongeVerticale.closePath();
                // Dessiner PartieSuperieure
                pointsX = new float[]{(float) (pMur.getOriginalShape().getBounds().getX() + pMur.getLongueur().pieds() * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() + (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST),
                        (float) (pMur.getOriginalShape().getBounds().getX() - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST)
                };
                pointsY = new float[]{(float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY(),
                        (float) pMur.getOriginalShape().getBounds().getY() - (pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST),
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST - pMur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                        (float) pMur.getOriginalShape().getBounds().getY() - angle * (pMur.getLongueur().pieds() + pMur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST
                };
                Path2D.Float partieSuperieure = new Path2D.Float();
                partieSuperieure.moveTo(pointsX[0], pointsY[0]);
                for (int i = 1; i < pointsX.length; i++) {
                    partieSuperieure.lineTo(pointsX[i], pointsY[i]);
                }
                partieSuperieure.closePath();
                Graphics2D g5 = (Graphics2D) g;
                if (pignonGauche.estAffichee()) {
                    g5.setColor(pignonGauche.getCouleur());
                    g5.fill(pignonGauche.getShape());
                }
                if (rallongeVerticalee.estAffichee()) {
                    g5.setColor(rallongeVerticalee.getCouleur());
                    g5.fill(rallongeVerticalee.getShape());
                }
                g5.setColor(Affichable.COULEUR_PARTIE_SUPERIEURE);
                g5.fill(partieSuperieure);
            }
        }
    }

    @Override
    public void afficherInfoElement() {

    }

}