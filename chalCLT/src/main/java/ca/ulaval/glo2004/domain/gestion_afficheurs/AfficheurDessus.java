package ca.ulaval.glo2004.domain.gestion_afficheurs;


import ca.ulaval.glo2004.domain.gestion_chalet.Chalet;
import ca.ulaval.glo2004.domain.gestion_chalet.Mur;
import ca.ulaval.glo2004.services.CaisseOutils;
import ca.ulaval.glo2004.services.Orientations;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;

import static ca.ulaval.glo2004.services.Orientations.DROITE;
import static ca.ulaval.glo2004.services.Orientations.GAUCHE;

public class AfficheurDessus extends Afficheur implements Serializable {



    public AfficheurDessus() {
        super();
    }

    public Hashtable<Orientations, Shape> dessinerMurs(Graphics g, List<Mur> list, Mur murArriere, Mur murGauche) {
        float espacementRetrait = Chalet.getRetraitSupplementaire().pouces();
        Hashtable<Orientations, Shape> p_tableau = new Hashtable<>();
        Graphics2D g2 = (Graphics2D) g;
        for (Mur mur : list) {
            switch (mur.getCote()) {
                case ARRIERE:
                    g2.setColor(mur.getCouleur());
                    Rectangle2D.Double rectArriere = new Rectangle2D.Double(mur.getShape().getBounds().getX(),
                            mur.getShape().getBounds().getY(), mur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                            mur.getEpaisseur().pieds() * CaisseOutils.RATIO_TEST);
                    Rectangle2D.Double rainure1Arriere = new Rectangle2D.Double(mur.getShape().getBounds().getX(),
                            mur.getShape().getBounds().getY() + mur.getEpaisseur().pieds() * CaisseOutils.RATIO_TEST / 2,
                            mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST);
                    Rectangle2D.Double rainure2Arriere = new Rectangle2D.Double(mur.getShape().getBounds().getX() + (mur.getLongueur().pieds() - mur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                            mur.getShape().getBounds().getY() + mur.getEpaisseur().pieds() * CaisseOutils.RATIO_TEST / 2,
                            mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST);
                    Area areaMurArriere = new Area(rectArriere);
                    Area areaRainure1A = new Area(rainure1Arriere);
                    Area areaRainure2A = new Area(rainure2Arriere);
                    areaMurArriere.subtract(areaRainure1A);
                    areaMurArriere.subtract(areaRainure2A);
                    if (Chalet.getSensToit() == Orientations.FACADE || Chalet.getSensToit() == Orientations.ARRIERE) {
                        //Rectangle Horizontale 1/2 pouce
                        Rectangle2D.Double retrait1Arriere = new Rectangle2D.Double(mur.getShape().getBounds().getX(),
                                mur.getShape().getBounds().getY() + (mur.getEpaisseur().pieds() / 2 - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, espacementRetrait / 24 * CaisseOutils.RATIO_TEST);
                        //Rectangle Horizontale 1/2 pouce
                        Rectangle2D.Double retrait2Arriere = new Rectangle2D.Double(mur.getShape().getBounds().getX() + (mur.getLongueur().pieds() - mur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                                mur.getShape().getBounds().getY() + (mur.getEpaisseur().pieds() / 2 - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, espacementRetrait / 24 * CaisseOutils.RATIO_TEST);
                        //Rectangle Verticale 1/2 pouce
                        Rectangle2D.Double retrait3Arriere = new Rectangle2D.Double(mur.getShape().getBounds().getX() + (mur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                                mur.getShape().getBounds().getY() + (mur.getEpaisseur().pieds() / 2 - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                espacementRetrait / 24 * CaisseOutils.RATIO_TEST, (mur.getEpaisseur().pieds() / 2 + espacementRetrait / 24) * CaisseOutils.RATIO_TEST);
                        //Rectangle Verticale 1/2 pouce
                        Rectangle2D.Double retrait4Arriere = new Rectangle2D.Double(mur.getShape().getBounds().getX() + (mur.getLongueur().pieds() - mur.getEpaisseur().pieds() / 2 - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                mur.getShape().getBounds().getY() + (mur.getEpaisseur().pieds() / 2 - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                espacementRetrait / 24 * CaisseOutils.RATIO_TEST, (mur.getEpaisseur().pieds() / 2 + espacementRetrait / 24) * CaisseOutils.RATIO_TEST);
                        Area areaRetrait1A = new Area(retrait1Arriere);
                        Area areaRetrait2A = new Area(retrait2Arriere);
                        Area areaRetrait3A = new Area(retrait3Arriere);
                        Area areaRetrait4A = new Area(retrait4Arriere);
                        areaMurArriere.subtract(areaRetrait1A);
                        areaMurArriere.subtract(areaRetrait2A);
                        areaMurArriere.subtract(areaRetrait3A);
                        areaMurArriere.subtract(areaRetrait4A);
                    } else {
                        Rectangle2D.Double retrait1Arriere = new Rectangle2D.Double(mur.getShape().getBounds().getX(),
                                mur.getShape().getBounds().getY(),
                                espacementRetrait/24 * CaisseOutils.RATIO_TEST, mur.getEpaisseur().pieds()/2 * CaisseOutils.RATIO_TEST);
                        //Rectangle Horizontale 1/2 pouce
                        Rectangle2D.Double retrait2Arriere = new Rectangle2D.Double(mur.getShape().getBounds().getX(),
                                mur.getShape().getBounds().getY() + (mur.getEpaisseur().pieds() / 2 - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, espacementRetrait / 24 * CaisseOutils.RATIO_TEST);
                        //Rectangle Verticale 1/2 pouce
                        Rectangle2D.Double retrait3Arriere = new Rectangle2D.Double(mur.getShape().getBounds().getX() + (mur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                                mur.getShape().getBounds().getY() + (mur.getEpaisseur().pieds() / 2 - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                espacementRetrait / 12 * CaisseOutils.RATIO_TEST, (mur.getEpaisseur().pieds() / 2 + espacementRetrait / 24) * CaisseOutils.RATIO_TEST);
                        Rectangle2D.Double retrait4Arriere = new Rectangle2D.Double(mur.getShape().getBounds().getX()+(mur.getLongueur().pieds()-espacementRetrait/24)*CaisseOutils.RATIO_TEST,
                                mur.getShape().getBounds().getY(),
                                espacementRetrait/24 * CaisseOutils.RATIO_TEST, mur.getEpaisseur().pieds()/2 * CaisseOutils.RATIO_TEST);
                        //Rectangle Horizontale 1/2 pouce
                        Rectangle2D.Double retrait5Arriere = new Rectangle2D.Double(mur.getShape().getBounds().getX()+(mur.getLongueur().pieds()-mur.getEpaisseur().pieds()/2)*CaisseOutils.RATIO_TEST,
                                mur.getShape().getBounds().getY() + (mur.getEpaisseur().pieds() / 2 - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, espacementRetrait / 24 * CaisseOutils.RATIO_TEST);
                        //Rectangle Verticale 1/2 pouce
                        Rectangle2D.Double retrait6Arriere = new Rectangle2D.Double(mur.getShape().getBounds().getX() + (mur.getLongueur().pieds()-mur.getEpaisseur().pieds()/2-espacementRetrait/12)*CaisseOutils.RATIO_TEST,
                                mur.getShape().getBounds().getY() + (mur.getEpaisseur().pieds() / 2 - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                espacementRetrait / 12 * CaisseOutils.RATIO_TEST, (mur.getEpaisseur().pieds() / 2 + espacementRetrait / 24) * CaisseOutils.RATIO_TEST);
                        Area areaRetrait1A = new Area(retrait1Arriere);
                        Area areaRetrait2A = new Area(retrait2Arriere);
                        Area areaRetrait3A = new Area(retrait3Arriere);
                        Area areaRetrait4A = new Area(retrait4Arriere);
                        Area areaRetrait5A = new Area(retrait5Arriere);
                        Area areaRetrait6A = new Area(retrait6Arriere);
                        areaMurArriere.subtract(areaRetrait1A);
                        areaMurArriere.subtract(areaRetrait2A);
                        areaMurArriere.subtract(areaRetrait3A);
                        areaMurArriere.subtract(areaRetrait4A);
                        areaMurArriere.subtract(areaRetrait5A);
                        areaMurArriere.subtract(areaRetrait6A);
                        AffineTransform deplaceMurA = AffineTransform.getTranslateInstance(0, mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST);
                        areaMurArriere.transform(deplaceMurA);
                    }
                    g2.fill(areaMurArriere);
                    p_tableau.put(Orientations.ARRIERE, areaMurArriere);
                    break;
                case GAUCHE:
                    g2.setColor(mur.getCouleur());
                    Rectangle2D.Double rectGauche = new Rectangle2D.Double(murArriere.getShape().getBounds().getX(),
                            murArriere.getShape().getBounds().getY() + mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, mur.getEpaisseur().pieds() * CaisseOutils.RATIO_TEST,
                            mur.getLongueur().pieds() * CaisseOutils.RATIO_TEST);
                    Rectangle2D.Double rainure1Gauche = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                            murArriere.getShape().getBounds().getY() + mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                            mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST);
                    Rectangle2D.Double rainure2Gauche = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (mur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                            murArriere.getShape().getBounds().getY() + (mur.getLongueur().pieds()) * CaisseOutils.RATIO_TEST,
                            mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST);
                    Area areaMurGauche = new Area(rectGauche);
                    Area areaRainure1Gauche = new Area(rainure1Gauche);
                    Area areaRainure2Gauche = new Area(rainure2Gauche);
                    areaMurGauche.subtract(areaRainure1Gauche);
                    areaMurGauche.subtract(areaRainure2Gauche);
                    if (Chalet.getSensToit() == Orientations.FACADE || Chalet.getSensToit() == Orientations.ARRIERE) {
                        //Rectangle Horizontale 1/2 pouce
                        Rectangle2D.Double retrait1Gauche = new Rectangle2D.Double(murArriere.getShape().getBounds().getX(),
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                                murArriere.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, espacementRetrait / 24 * CaisseOutils.RATIO_TEST);
                        //Rectangle Horizontale 1/2 pouce
                        Rectangle2D.Double retrait2Gauche = new Rectangle2D.Double(murArriere.getShape().getBounds().getX(),
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() / 2 + mur.getLongueur().pieds() - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                murArriere.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, espacementRetrait / 24 * CaisseOutils.RATIO_TEST);
                        //Rectangle Verticale 1/2 pouce
                        Rectangle2D.Double retrait3Gauche = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (murArriere.getEpaisseur().pieds() / 2 - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() / 2 - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                espacementRetrait / 24 * CaisseOutils.RATIO_TEST, (murArriere.getEpaisseur().pieds() / 2 + espacementRetrait / 24) * CaisseOutils.RATIO_TEST);
                        //Rectangle Verticale 1/2 pouce
                        Rectangle2D.Double retrait4Gauche = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (murArriere.getEpaisseur().pieds() / 2 - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() / 2 + mur.getLongueur().pieds() - murArriere.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                                espacementRetrait / 24 * CaisseOutils.RATIO_TEST, (murArriere.getEpaisseur().pieds() / 2 + espacementRetrait / 24) * CaisseOutils.RATIO_TEST);
                        //Rectangle Horizontale 1 pouce
                        Rectangle2D.Double retrait5Gauche = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (murArriere.getEpaisseur().pieds() / 2 - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST,
                                (murArriere.getEpaisseur().pieds() / 2 + espacementRetrait / 24) * CaisseOutils.RATIO_TEST, espacementRetrait / 12 * CaisseOutils.RATIO_TEST);
                        //Rectangle Horizontale 1 pouce
                        Rectangle2D.Double retrait6Gauche = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (murArriere.getEpaisseur().pieds() / 2 - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() / 2 + mur.getLongueur().pieds() - murArriere.getEpaisseur().pieds() / 2 - espacementRetrait / 12) * CaisseOutils.RATIO_TEST,
                                (murArriere.getEpaisseur().pieds() / 2 + espacementRetrait / 24) * CaisseOutils.RATIO_TEST, espacementRetrait / 12 * CaisseOutils.RATIO_TEST);
                        Area areaRetrait1Gauche = new Area(retrait1Gauche);
                        Area areaRetrait2Gauche = new Area(retrait2Gauche);
                        Area areaRetrait3Gauche = new Area(retrait3Gauche);
                        Area areaRetrait4Gauche = new Area(retrait4Gauche);
                        Area areaRetrait5Gauche = new Area(retrait5Gauche);
                        Area areaRetrait6Gauche = new Area(retrait6Gauche);
                        areaMurGauche.subtract(areaRetrait1Gauche);
                        areaMurGauche.subtract(areaRetrait2Gauche);
                        areaMurGauche.subtract(areaRetrait3Gauche);
                        areaMurGauche.subtract(areaRetrait4Gauche);
                        areaMurGauche.subtract(areaRetrait5Gauche);
                        areaMurGauche.subtract(areaRetrait6Gauche);
                    } else {
                        //Rectangle Verticale 1/2 pouce
                        Rectangle2D.Double retrait1Gauche = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (mur.getEpaisseur().pieds()/2-espacementRetrait/24)*CaisseOutils.RATIO_TEST,
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                                espacementRetrait/24*CaisseOutils.RATIO_TEST, mur.getEpaisseur().pieds()/2 * CaisseOutils.RATIO_TEST);
                        //Rectangle Horizontale 1/2 pouce
                        Rectangle2D.Double retrait2Gauche = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (mur.getEpaisseur().pieds()/2-espacementRetrait/24)*CaisseOutils.RATIO_TEST,
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST,
                                (murArriere.getEpaisseur().pieds() / 2+espacementRetrait/24) * CaisseOutils.RATIO_TEST, espacementRetrait / 24 * CaisseOutils.RATIO_TEST);
                        //Rectangle Verticale 1/2 pouce
                        Rectangle2D.Double retrait3Gauche = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (mur.getEpaisseur().pieds()/2-espacementRetrait/24)*CaisseOutils.RATIO_TEST,
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() / 2 + mur.getLongueur().pieds()-mur.getEpaisseur().pieds()/2) * CaisseOutils.RATIO_TEST,
                                espacementRetrait/24*CaisseOutils.RATIO_TEST, mur.getEpaisseur().pieds()/2 * CaisseOutils.RATIO_TEST);
                        //Rectangle Horizontale 1/2 pouce
                        Rectangle2D.Double retrait4Gauche = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (mur.getEpaisseur().pieds()/2-espacementRetrait/24)*CaisseOutils.RATIO_TEST,
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() + mur.getLongueur().pieds()-mur.getEpaisseur().pieds()-espacementRetrait/24) * CaisseOutils.RATIO_TEST,
                                (murArriere.getEpaisseur().pieds() / 2+espacementRetrait/24) * CaisseOutils.RATIO_TEST, espacementRetrait / 24 * CaisseOutils.RATIO_TEST);
                        Area areaRetrait1Gauche = new Area(retrait1Gauche);
                        Area areaRetrait2Gauche = new Area(retrait2Gauche);
                        Area areaRetrait3Gauche = new Area(retrait3Gauche);
                        Area areaRetrait4Gauche = new Area(retrait4Gauche);
                        areaMurGauche.subtract(areaRetrait1Gauche);
                        areaMurGauche.subtract(areaRetrait2Gauche);
                        areaMurGauche.subtract(areaRetrait3Gauche);
                        areaMurGauche.subtract(areaRetrait4Gauche);
                        AffineTransform deplaceMurG = AffineTransform.getTranslateInstance(-mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, 0);
                        areaMurGauche.transform(deplaceMurG);
                    }
                    g2.fill(areaMurGauche);
                    p_tableau.put(GAUCHE, areaMurGauche);
                    break;
                case DROITE:
                    g2.setColor(mur.getCouleur());
                    Rectangle2D.Double rectDroite = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (murArriere.getLongueur().pieds() - murArriere.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST,
                            murArriere.getShape().getBounds().getY() + murArriere.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, mur.getEpaisseur().pieds() * CaisseOutils.RATIO_TEST,
                            mur.getLongueur().pieds() * CaisseOutils.RATIO_TEST);
                    Rectangle2D.Double rainure1Droite = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (murArriere.getLongueur().pieds() - murArriere.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST,
                            murArriere.getShape().getBounds().getY() + murArriere.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST,
                            mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST);
                    Rectangle2D.Double rainure2Droite = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (murArriere.getLongueur().pieds() - murArriere.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST,
                            murArriere.getShape().getBounds().getY() + mur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                            mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST);
                    Area areaMurDroite = new Area(rectDroite);
                    Area areaRainure1Droite = new Area(rainure1Droite);
                    Area areaRainure2Droite = new Area(rainure2Droite);
                    areaMurDroite.subtract(areaRainure1Droite);
                    areaMurDroite.subtract(areaRainure2Droite);
                    if (Chalet.getSensToit() == Orientations.FACADE || Chalet.getSensToit() == Orientations.ARRIERE) {
                        //Rectangle Horizontale 1/2 pouce
                        Rectangle2D.Double retrait1Droite = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (murArriere.getLongueur().pieds() - mur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                                murArriere.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, espacementRetrait / 24 * CaisseOutils.RATIO_TEST);
                        //Rectangle Horizontale 1/2 pouce
                        Rectangle2D.Double retrait2Droite = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (murArriere.getLongueur().pieds() - mur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() / 2 + mur.getLongueur().pieds() - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                murArriere.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, espacementRetrait / 24 * CaisseOutils.RATIO_TEST);
                        //Rectangle Verticale 1/2 pouce
                        Rectangle2D.Double retrait3Droite = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (murArriere.getLongueur().pieds() - murArriere.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() / 2 - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                espacementRetrait / 24 * CaisseOutils.RATIO_TEST, (murArriere.getEpaisseur().pieds() / 2 + espacementRetrait / 24) * CaisseOutils.RATIO_TEST);
                        //Rectangle Verticale 1/2 pouce
                        Rectangle2D.Double retrait4Droite = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (murArriere.getLongueur().pieds() - murArriere.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() / 2 + mur.getLongueur().pieds() - murArriere.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                                espacementRetrait / 24 * CaisseOutils.RATIO_TEST, (murArriere.getEpaisseur().pieds() / 2 + espacementRetrait / 24) * CaisseOutils.RATIO_TEST);
                        //Rectangle Horizontale 1 pouce
                        Rectangle2D.Double retrait5Droite = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (murArriere.getLongueur().pieds() - mur.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST,
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST,
                                (murArriere.getEpaisseur().pieds() / 2 + espacementRetrait / 24) * CaisseOutils.RATIO_TEST, espacementRetrait / 12 * CaisseOutils.RATIO_TEST);
                        //Rectangle Horizontale 1 pouce
                        Rectangle2D.Double retrait6Droite = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (murArriere.getLongueur().pieds() - mur.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST,
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() / 2 + mur.getLongueur().pieds() - murArriere.getEpaisseur().pieds() / 2 - espacementRetrait / 12) * CaisseOutils.RATIO_TEST,
                                (murArriere.getEpaisseur().pieds() / 2 + espacementRetrait / 24) * CaisseOutils.RATIO_TEST, espacementRetrait / 12 * CaisseOutils.RATIO_TEST);
                        Area areaRetrait1Droite = new Area(retrait1Droite);
                        Area areaRetrait2Droite = new Area(retrait2Droite);
                        Area areaRetrait3Droite = new Area(retrait3Droite);
                        Area areaRetrait4Droite = new Area(retrait4Droite);
                        Area areaRetrait5Droite = new Area(retrait5Droite);
                        Area areaRetrait6Droite = new Area(retrait6Droite);
                        areaMurDroite.subtract(areaRetrait1Droite);
                        areaMurDroite.subtract(areaRetrait2Droite);
                        areaMurDroite.subtract(areaRetrait3Droite);
                        areaMurDroite.subtract(areaRetrait4Droite);
                        areaMurDroite.subtract(areaRetrait5Droite);
                        areaMurDroite.subtract(areaRetrait6Droite);
                    } else {
                        //Rectangle Verticale 1/2 pouce
                        Rectangle2D.Double retrait1Droite = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (murArriere.getLongueur().pieds()-mur.getEpaisseur().pieds()/2)*CaisseOutils.RATIO_TEST,
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                                espacementRetrait/24*CaisseOutils.RATIO_TEST, (mur.getEpaisseur().pieds()/2+espacementRetrait/24) * CaisseOutils.RATIO_TEST);
                        //Rectangle Horizontale 1/2 pouce
                        Rectangle2D.Double retrait2Droite = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (murArriere.getLongueur().pieds()-mur.getEpaisseur().pieds()-espacementRetrait/24)*CaisseOutils.RATIO_TEST,
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST,
                                (murArriere.getEpaisseur().pieds() / 2+espacementRetrait/24) * CaisseOutils.RATIO_TEST, espacementRetrait / 24 * CaisseOutils.RATIO_TEST);
                        //Rectangle Verticale 1/2 pouce
                        Rectangle2D.Double retrait3Droite = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (murArriere.getLongueur().pieds()-mur.getEpaisseur().pieds()/2)*CaisseOutils.RATIO_TEST,
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() / 2 + mur.getLongueur().pieds()-mur.getEpaisseur().pieds()/2-espacementRetrait/24) * CaisseOutils.RATIO_TEST,
                                espacementRetrait/24*CaisseOutils.RATIO_TEST, (mur.getEpaisseur().pieds()/2+espacementRetrait/24) * CaisseOutils.RATIO_TEST);
                        //Rectangle Horizontale 1/2 pouce
                        Rectangle2D.Double retrait4Droite = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (murArriere.getLongueur().pieds()-mur.getEpaisseur().pieds()-espacementRetrait/24)*CaisseOutils.RATIO_TEST,
                                murArriere.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() + mur.getLongueur().pieds()-mur.getEpaisseur().pieds()-espacementRetrait/24) * CaisseOutils.RATIO_TEST,
                                (murArriere.getEpaisseur().pieds() / 2+espacementRetrait/24) * CaisseOutils.RATIO_TEST, espacementRetrait / 24 * CaisseOutils.RATIO_TEST);
                        Area areaRetrait1Droite = new Area(retrait1Droite);
                        Area areaRetrait2Droite = new Area(retrait2Droite);
                        Area areaRetrait3Droite = new Area(retrait3Droite);
                        Area areaRetrait4Droite = new Area(retrait4Droite);
                        areaMurDroite.subtract(areaRetrait1Droite);
                        areaMurDroite.subtract(areaRetrait2Droite);
                        areaMurDroite.subtract(areaRetrait3Droite);
                        areaMurDroite.subtract(areaRetrait4Droite);
                        AffineTransform deplaceMurD = AffineTransform.getTranslateInstance(mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, 0);
                        areaMurDroite.transform(deplaceMurD);
                    }
                    /* TEST D'AFFICHAGE de retraits */

                    g2.fill(areaMurDroite);
                    p_tableau.put(DROITE, areaMurDroite);
                    break;
                case FACADE:
                    g2.setColor(mur.getCouleur());
                    Rectangle2D.Double rectFacade = new Rectangle2D.Double(murArriere.getShape().getBounds().getX(),
                            murArriere.getShape().getBounds().getY() + (murGauche.getLongueur().pieds()) * CaisseOutils.RATIO_TEST, mur.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                            mur.getEpaisseur().pieds() * CaisseOutils.RATIO_TEST);
                    Rectangle2D.Double rainure1Facade = new Rectangle2D.Double(murArriere.getShape().getBounds().getX(),
                            murArriere.getShape().getBounds().getY() + murGauche.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                            mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST);
                    Rectangle2D.Double rainure2Facade = new Rectangle2D.Double(murArriere.getShape().getBounds().getX() + (mur.getLongueur().pieds() - mur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                            murArriere.getShape().getBounds().getY() + murGauche.getLongueur().pieds() * CaisseOutils.RATIO_TEST,
                            mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST);
                    Area areaMurFacade = new Area(rectFacade);
                    Area areaRainure1Facade = new Area(rainure1Facade);
                    Area areaRainure2Facade = new Area(rainure2Facade);
                    areaMurFacade.subtract(areaRainure1Facade);
                    areaMurFacade.subtract(areaRainure2Facade);
                    if (Chalet.getSensToit() == Orientations.FACADE || Chalet.getSensToit() == Orientations.ARRIERE) {

                        Rectangle2D.Double retrait1Facade = new Rectangle2D.Double(mur.getShape().getBounds().getX(),
                                mur.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() / 2 + murGauche.getLongueur().pieds()) * CaisseOutils.RATIO_TEST,
                                mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, espacementRetrait / 24 * CaisseOutils.RATIO_TEST);
                        Rectangle2D.Double retrait2Facade = new Rectangle2D.Double(mur.getShape().getBounds().getX() + (mur.getLongueur().pieds() - mur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                                mur.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() / 2 + murGauche.getLongueur().pieds()) * CaisseOutils.RATIO_TEST,
                                mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, espacementRetrait / 24 * CaisseOutils.RATIO_TEST);
                        Rectangle2D.Double retrait3Facade = new Rectangle2D.Double(mur.getShape().getBounds().getX() + (mur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                                mur.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() + murGauche.getLongueur().pieds() - murArriere.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST,
                                espacementRetrait / 24 * CaisseOutils.RATIO_TEST, (mur.getEpaisseur().pieds() / 2 + espacementRetrait / 24) * CaisseOutils.RATIO_TEST);
                        Rectangle2D.Double retrait4Facade = new Rectangle2D.Double(mur.getShape().getBounds().getX() + (mur.getLongueur().pieds() - mur.getEpaisseur().pieds() / 2 - espacementRetrait / 24) * CaisseOutils.RATIO_TEST,
                                mur.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds() + murGauche.getLongueur().pieds() - murArriere.getEpaisseur().pieds()) * CaisseOutils.RATIO_TEST,
                                espacementRetrait / 24 * CaisseOutils.RATIO_TEST, (mur.getEpaisseur().pieds() / 2 + espacementRetrait / 24) * CaisseOutils.RATIO_TEST);
                        Area areaRetrait1F = new Area(retrait1Facade);
                        Area areaRetrait2F = new Area(retrait2Facade);
                        Area areaRetrait3F = new Area(retrait3Facade);
                        Area areaRetrait4F = new Area(retrait4Facade);
                        areaMurFacade.subtract(areaRetrait1F);
                        areaMurFacade.subtract(areaRetrait2F);
                        areaMurFacade.subtract(areaRetrait3F);
                        areaMurFacade.subtract(areaRetrait4F);
                    } else {
                        //Rectangle Verticale 1/2 pouce
                        Rectangle2D.Double retrait1Facade = new Rectangle2D.Double(mur.getShape().getBounds().getX(),
                                mur.getShape().getBounds().getY()+(murArriere.getEpaisseur().pieds()/2+murGauche.getLongueur().pieds())*CaisseOutils.RATIO_TEST,
                                espacementRetrait/24 * CaisseOutils.RATIO_TEST, mur.getEpaisseur().pieds()/2 * CaisseOutils.RATIO_TEST);
                        //Rectangle Horizontale 1/2 pouce
                        Rectangle2D.Double retrait2Facade = new Rectangle2D.Double(mur.getShape().getBounds().getX(),
                                mur.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds()/2+murGauche.getLongueur().pieds()) * CaisseOutils.RATIO_TEST,
                                mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, espacementRetrait / 24 * CaisseOutils.RATIO_TEST);
                        //Rectangle Verticale 1/2 pouce
                        Rectangle2D.Double retrait3Facade = new Rectangle2D.Double(mur.getShape().getBounds().getX() + (mur.getEpaisseur().pieds() / 2) * CaisseOutils.RATIO_TEST,
                                mur.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds()/2+murGauche.getLongueur().pieds()-mur.getEpaisseur().pieds()/2) * CaisseOutils.RATIO_TEST,
                                espacementRetrait / 12 * CaisseOutils.RATIO_TEST, (mur.getEpaisseur().pieds() / 2 +espacementRetrait/24) * CaisseOutils.RATIO_TEST);
                        //Rectangle Verticale 1/2 pouce
                        Rectangle2D.Double retrait4Facade = new Rectangle2D.Double(mur.getShape().getBounds().getX()+(mur.getLongueur().pieds()-espacementRetrait/24)*CaisseOutils.RATIO_TEST,
                                mur.getShape().getBounds().getY()+(murArriere.getEpaisseur().pieds()/2+murGauche.getLongueur().pieds())*CaisseOutils.RATIO_TEST,
                                espacementRetrait/24 * CaisseOutils.RATIO_TEST, mur.getEpaisseur().pieds()/2 * CaisseOutils.RATIO_TEST);
                        //Rectangle Horizontale 1/2 pouce
                        Rectangle2D.Double retrait5Facade = new Rectangle2D.Double(mur.getShape().getBounds().getX()+(mur.getLongueur().pieds()-mur.getEpaisseur().pieds()/2)*CaisseOutils.RATIO_TEST,
                                mur.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds()/2+murGauche.getLongueur().pieds()) * CaisseOutils.RATIO_TEST,
                                mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST, espacementRetrait / 24 * CaisseOutils.RATIO_TEST);
                        //Rectangle Verticale 1/2 pouce
                        Rectangle2D.Double retrait6Facade = new Rectangle2D.Double(mur.getShape().getBounds().getX() + (mur.getLongueur().pieds()-mur.getEpaisseur().pieds()/2-espacementRetrait/12)*CaisseOutils.RATIO_TEST,
                                mur.getShape().getBounds().getY() + (murArriere.getEpaisseur().pieds()/2+murGauche.getLongueur().pieds()-mur.getEpaisseur().pieds()/2) * CaisseOutils.RATIO_TEST,
                                espacementRetrait / 12 * CaisseOutils.RATIO_TEST, (mur.getEpaisseur().pieds() / 2 +espacementRetrait/24) * CaisseOutils.RATIO_TEST);
                        Area areaRetrait1F = new Area(retrait1Facade);
                        Area areaRetrait2F = new Area(retrait2Facade);
                        Area areaRetrait3F = new Area(retrait3Facade);
                        Area areaRetrait4F = new Area(retrait4Facade);
                        Area areaRetrait5F = new Area(retrait5Facade);
                        Area areaRetrait6F = new Area(retrait6Facade);
                        areaMurFacade.subtract(areaRetrait1F);
                        areaMurFacade.subtract(areaRetrait2F);
                        areaMurFacade.subtract(areaRetrait3F);
                        areaMurFacade.subtract(areaRetrait4F);
                        areaMurFacade.subtract(areaRetrait5F);
                        areaMurFacade.subtract(areaRetrait6F);
                        AffineTransform deplaceMurF = AffineTransform.getTranslateInstance(0, -mur.getEpaisseur().pieds() / 2 * CaisseOutils.RATIO_TEST);
                        areaMurFacade.transform(deplaceMurF);
                    }
                    g2.fill(areaMurFacade);
                    p_tableau.put(Orientations.FACADE, areaMurFacade);
                    break;
            }
        }
        return p_tableau;
    }

    @Override
    public void afficherInfoElement() {

    }

}