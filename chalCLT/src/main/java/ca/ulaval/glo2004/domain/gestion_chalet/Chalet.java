package ca.ulaval.glo2004.domain.gestion_chalet;


import ca.ulaval.glo2004.domain.gestion_chalet.accessoires.Fenetre;
import ca.ulaval.glo2004.domain.gestion_chalet.accessoires.Porte;

import static ca.ulaval.glo2004.services.Couches.CINQ;

import ca.ulaval.glo2004.services.*;

import static ca.ulaval.glo2004.services.Orientations.ARRIERE;
import static ca.ulaval.glo2004.services.Orientations.DROITE;
import static ca.ulaval.glo2004.services.Orientations.FACADE;
import static ca.ulaval.glo2004.services.Orientations.GAUCHE;

import ca.ulaval.glo2004.services.CaisseOutils.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.security.PrivilegedExceptionAction;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Chalet implements Serializable {

    public static final UniteImperiale RETRAIT_SUPPLEMENTAIRE_DEFAUT = new UniteImperiale(0, 0, 0, 1); // 1 pouces

    private List<Contour> contourChalet;
    protected static UniteImperiale retraitSupplementaire;

    public static Orientations getSensToit() {
        return sensToit;
    }

    private static Orientations sensToit;

    private Toit toit;


    public Chalet() {
        contourChalet = new ArrayList<>();
        retraitSupplementaire = RETRAIT_SUPPLEMENTAIRE_DEFAUT;
        UniteImperiale largeurTemporaire = new UniteImperiale(8, 0, 0, 1);     // 8'
        UniteImperiale longueur10Temporaire = new UniteImperiale(10, 0, 0, 1); // 10'
        UniteImperiale longueur95Temporaire = new UniteImperiale(9, 6, 0, 1);  // 9'6" = 9.5'
        UniteImperiale epaisseurTemporaire = new UniteImperiale(0, 6, 0, 1);   // 6"
        UniteImperiale espacementTemporaire = new UniteImperiale(0, 3, 0, 1);  // 3"
        contourChalet.add(new Mur(largeurTemporaire, longueur10Temporaire, epaisseurTemporaire, espacementTemporaire, CINQ, FACADE));
        contourChalet.add(new Mur(largeurTemporaire, longueur10Temporaire, epaisseurTemporaire, espacementTemporaire, CINQ, ARRIERE));
        contourChalet.add(new Mur(largeurTemporaire, longueur95Temporaire, epaisseurTemporaire, espacementTemporaire, CINQ, DROITE));
        contourChalet.add(new Mur(largeurTemporaire, longueur95Temporaire, epaisseurTemporaire, espacementTemporaire, CINQ, GAUCHE));
        toit = new Toit(15, ARRIERE, longueur95Temporaire, longueur10Temporaire, epaisseurTemporaire);
        sensToit = toit.getOrientation();
        updatePositionShapeToit();
    }

    public Chalet(Chalet p_chalet) {
        contourChalet = new ArrayList<>();
        p_chalet.contourChalet.forEach(mur -> contourChalet.add(new Mur((Mur) mur)));
        toit = new Toit(p_chalet.toit);
        sensToit = toit.getOrientation();
        updatePositionShapeToit();
    }

    public static UniteImperiale getRetraitSupplementaire() {
        return retraitSupplementaire;
    }

    public boolean changerRetraitSupplementaire(UniteImperiale p_nouvRetraitSupplementaire) {
        boolean estUpdated = false;
        if (p_nouvRetraitSupplementaire.pouces() < contourChalet.get(0).getEpaisseur().pouces()) {
            retraitSupplementaire = p_nouvRetraitSupplementaire;
            estUpdated = true;
        }
        return estUpdated;
    }

    public Mur getMur(Orientations pOrientations) {
        for (Mur mur : getDonneesMur()) {
            if (mur.getCote() == pOrientations) {
                return mur;
            }
        }
        return null;
    }

    public Mur getMurVirtuel(Orientations pOrientations) {
        for (Mur mur : getDonneesMur()) {
            if (mur.getCoteVirtuel() == pOrientations) {
                return mur;
            }
        }
        return null;
    }

    public Toit getToitEntier(){
        return toit;
    }


    private boolean modifierMurOpposes(Orientations p_coteCheck, Orientations p_coteCheckOppose, Orientations p_coteMurSelectionne, UniteImperiale... donneDeModif) {
        boolean modificationAvecSucces = true;
        boolean check = p_coteMurSelectionne == p_coteCheck;
        Contour murSelectionne = getMur(p_coteMurSelectionne);
        Contour murOppose = getMur(check ? p_coteCheckOppose : p_coteCheck);
        if (murSelectionne.modifierMur(donneDeModif[0], donneDeModif[1], donneDeModif[2], donneDeModif[3])) {
            if (!murOppose.modifierMur(donneDeModif[0], donneDeModif[1], donneDeModif[2], donneDeModif[3])) {
                murSelectionne.modifierMur(murOppose.getLargeur(), murOppose.getLongueur(), murOppose.getEpaisseur(), murOppose.getEspacementAccessoires());
                modificationAvecSucces = false;
            }
        } else
            modificationAvecSucces = false;

        return modificationAvecSucces;
    }

    public boolean modifierMur(Dimension p_nouvDimensionDrawingPanel, UniteImperiale p_largeur, UniteImperiale p_longueur, UniteImperiale p_epaisseur, UniteImperiale p_espacement, boolean estRedimensionChalet, Orientations p_coteVoulu) {
        boolean modificationAvecSucces = true;
        Orientations p_coteMurSelectionne = null;

        if (!estRedimensionChalet) {
            for (Contour mur : contourChalet)
                if (mur.estSelectionne()) p_coteMurSelectionne = mur.getCote();
        } else
            p_coteMurSelectionne = p_coteVoulu;

        if ((p_coteMurSelectionne == FACADE || p_coteMurSelectionne == ARRIERE)) {
            if (modifierMurOpposes(FACADE, ARRIERE, p_coteMurSelectionne, p_largeur, p_longueur, p_epaisseur, p_espacement)) {
                updateContourPosition(p_nouvDimensionDrawingPanel, CaisseOutils.RATIO_TEST);
                contourChalet.forEach(mur -> {
                    updateShapeVueDessus(mur, mur.getShapeVueDessus());
                    mur.updateShape();
                });
                modificationAvecSucces = false;
            }

        } else if ((p_coteMurSelectionne == DROITE || p_coteMurSelectionne == GAUCHE)) {
            if (modifierMurOpposes(DROITE, GAUCHE, p_coteMurSelectionne, p_largeur, p_longueur, p_epaisseur, p_espacement)) {
                updateContourPosition(p_nouvDimensionDrawingPanel, CaisseOutils.RATIO_TEST);
                contourChalet.forEach(mur -> {
                    updateShapeVueDessus(mur, mur.getShapeVueDessus());
                    mur.updateShape();
                });
                modificationAvecSucces = false;
            }
        }

        boolean estModifAvantArriere = p_coteMurSelectionne == FACADE || p_coteMurSelectionne == ARRIERE;
        Contour murAdjacent_A = getMur(estModifAvantArriere ? GAUCHE : FACADE);
        Contour murAdjacent_B = getMur(estModifAvantArriere ? DROITE : ARRIERE);
        if (murAdjacent_A.modifierMur(p_largeur, murAdjacent_A.getLongueur(), p_epaisseur, p_espacement)) {
            if (!murAdjacent_B.modifierMur(p_largeur, murAdjacent_B.getLongueur(), p_epaisseur, p_espacement)) {
                murAdjacent_A.modifierMur(murAdjacent_B.getLargeur(), murAdjacent_B.getLongueur(), murAdjacent_B.getEpaisseur(), murAdjacent_B.getEspacementAccessoires());
                modificationAvecSucces = false;
            }
        } else
            modificationAvecSucces = false;

        updateContourPosition(p_nouvDimensionDrawingPanel, CaisseOutils.RATIO_TEST);
        contourChalet.forEach(mur -> {
            updateShapeVueDessus(mur, mur.getShapeVueDessus());
            mur.updateShape();
        });

        return modificationAvecSucces;
    }

    public Affichable selectionnerComposanteChalet(Point2D p_clickedPointInInches, Orientations p_vueMode) {
        Affichable composanteSeletionner = null;
        deselectionnerTout();
        if (p_vueMode != Orientations.DESSUS) {
            Contour mur = null;
            for (Contour auto_element : contourChalet) {
                auto_element.setSelectionne(false);
                if (auto_element.getCote() == p_vueMode) {
                    mur = auto_element;
                }
            }

            if (mur.getShape().contains(p_clickedPointInInches)) {
                boolean accSelectionnee = false;
                Accessoire selected = null;
                for (Accessoire pAcc : mur.getAccessoiresListes()) {
                    pAcc.setSelectionne(false);
                    if (pAcc.getShape().contains(p_clickedPointInInches)) {
                        pAcc.setSelectionne(true);
                        accSelectionnee = pAcc.estSelectionne();
                        selected = pAcc;
                        composanteSeletionner = pAcc; // composante qui sera retourner pour qu'on affiche ses details
                        break; // on stop la boucle parce que ce n'est plus necaisse de la continuer = moins de temps de traitement
                    }
                }
                if (!accSelectionnee) {
                    mur.setSelectionne(true);
                    composanteSeletionner = mur; // mur est retourner vu qu'aucun accessoire n'est selected

                }

            } else {
                boolean selectionerDebordement = false;
                for (Map.Entry<Orientations, Shape> element : mur.getShapeDebordement().entrySet()) {
                    if (element.getValue().contains(p_clickedPointInInches)) {
                        selectionerDebordement = true;
                        getMur(element.getKey()).setSelectionne(true);
                        composanteSeletionner = getMur(element.getKey());
                    }
                }
                if (!selectionerDebordement) {
                    composanteSeletionner = toit.selectionnerComposanteToit(p_clickedPointInInches);
                }
            }
        } else {
            for (Contour mur : contourChalet) {
                mur.setSelectionne(false);
                if (mur.getShapeVueDessus().contains(p_clickedPointInInches)) {
                    mur.setSelectionne(true);
                    composanteSeletionner = mur;
                }
            }
        }
        return composanteSeletionner;
    }

    public Affichable survolMur(Point2D p_hoveredPointInInches, Orientations p_vueMode) {
        Affichable composanteSurvolee = null;

        if (p_vueMode != Orientations.DESSUS) {
            Contour mur = null;
            for (Contour auto_element : contourChalet) {
                if (auto_element.getCote() == p_vueMode) {
                    mur = auto_element;
                }
            }

            if (mur.getShape().contains(p_hoveredPointInInches)) {
                for (Accessoire pAcc : mur.getAccessoiresListes()) {
                    if (pAcc.getShape().contains(p_hoveredPointInInches)) {
                        // Composante survolée trouvée
                        composanteSurvolee = pAcc;
                        break; // Arrêtez la boucle car une composante est trouvée
                    }
                }

                if (composanteSurvolee == null) {
                    // Si aucune composante accessoire n'est survolée, alors le mur est survolé
                    composanteSurvolee = mur;
                }
            } else {
                boolean debordement = false;
                for (Map.Entry<Orientations, Shape> element : mur.getShapeDebordement().entrySet()) {
                    if (element.getValue().contains(p_hoveredPointInInches)) {
                        // Composante survolée trouvée
                        composanteSurvolee = getMur(element.getKey());
                        debordement = true;
                        break; // Arrêtez la boucle car une composante est trouvée
                    }
                }
                if (!debordement) {
                   composanteSurvolee =  toit.surVolComposantes(p_hoveredPointInInches);
                }
            }
        } else {
            for (Contour mur : contourChalet) {
                if (mur.getShapeVueDessus().contains(p_hoveredPointInInches)) {
                    // Composante survolée trouvée
                    composanteSurvolee = mur;
                    break; // Arrêtez la boucle car une composante est trouvée
                }
            }
        }

        return composanteSurvolee;
    }

    public void deselectionnerTout() {
        contourChalet.forEach(mur -> {
            mur.setSelectionne(false);
            mur.getAccessoiresListes().forEach(accessoire -> {
                accessoire.setSelectionne(false);
            });
        });
        for (PanneauToit p :
                toit.donneesToit()) {
            p.setSelectionne(false);
        }
    }

    private void updatePositionShapeToit() {
        toit.updateShapePositionComposantes(getMurVirtuel(FACADE), getMurVirtuel(GAUCHE), getMurVirtuel(ARRIERE), getMurVirtuel(DROITE));
    }

    /**
     * @param p_coordonneesEmplacement
     */
    public Accessoire supprimerAccessoire(UniteImperiale[] p_coordonneesEmplacement, Orientations p_cote) {
        for (Contour mur : contourChalet) {
            if (mur.getCote() == p_cote && mur instanceof Mur) {
                return mur.supprimerAccessoire(p_coordonneesEmplacement);
            }
        }
        return null;
    }

    public Accessoire supprimerAccessoire(int accessoire_id, Orientations p_cote) {
        for (Contour mur : contourChalet) {
            if (mur.getCote() == p_cote && mur instanceof Mur) {
                return mur.supprimerAccessoire(accessoire_id);
            }
        }
        return null;
    }

    /**
     * @param p_largeur
     * @param p_longueur
     */
    public Accessoire ajouterAccessoire(UniteImperiale p_largeur, UniteImperiale p_longueur, UniteImperiale[] emplacement, Orientations p_cote, CreationAccessoireMode p_mode) {
        boolean estUpdated = false;
        for (Contour mur : contourChalet) {

            if (mur.getCote() == p_cote) {
                //Va chercher l'épaisseur et le nombre de couches via le mur courant
                UniteImperiale epaisseur = mur.getEpaisseur();
                //si la somme de l'emplacement de l'accessoire et sa longueur donne la longueur du mur alors, l'accessoire touche le sol donc c'est une porte
                if (p_mode == CreationAccessoireMode.PORTE) {
                    UniteImperiale distMinimumSol = new UniteImperiale("3\"");
                    emplacement[1] = mur.getLargeur().soustraire(p_largeur.additionner(distMinimumSol));
                    // Créez une porte avec les valeurs
                    Porte nouvellePorte = new Porte(p_largeur, p_longueur, epaisseur, emplacement, mur.getCote(), Mur.getId_maker());
                    //Ajoute la porte à la liste d'accessoire du mur (avoir le mur courant)
                    //La validation de l'accessoire est faite dans la classe mur
                    if (mur.ajouterAccessoire(nouvellePorte))
                        return nouvellePorte;
                } else if (p_mode == CreationAccessoireMode.FENETRE) {
                    // Créez une fenêtre avec les valeurs
                    Fenetre nouvelleFenetre = new Fenetre(p_largeur, p_longueur, epaisseur, emplacement, mur.getCote(), Mur.getId_maker());
                    //Ajoute la fenêtre à la liste d'accessoire du mur (avoir le mur courant)
                    //La validation de l'accessoire est faite dans la classe Mur
                    if (mur.ajouterAccessoire(nouvelleFenetre))
                        return nouvelleFenetre;
                }

            }
        }
        return null;
    }

    public Accessoire ajouterAccessoire(Accessoire accessoire) {
        CreationAccessoireMode mode = accessoire instanceof Fenetre ? CreationAccessoireMode.FENETRE : CreationAccessoireMode.PORTE;
        return ajouterAccessoire(accessoire.getLargeur(), accessoire.getLongueur(), accessoire.getCoordonneesEmplacement(), accessoire.getMurEmplacement(), mode);
    }


    public List<Mur> getDonneesMur() {
        List<Mur> listeMurs = new ArrayList<>();
        for (Contour mur : contourChalet) {
            listeMurs.add((Mur) mur);
        }
        return listeMurs;
    }

    public void updateContourPosition(Dimension p_nouvDimensionDrawingPanel, float p_ratio) {
        for (Contour mur : contourChalet) {
            // met à jour les coordonnées du mur par rapport au drawing panel
            float[] dimensionMur = {mur.getLongueur().pieds(), mur.getLargeur().pieds()};
            Point coordonneesAjustees = CaisseOutils.ajusterCoordonneesToDrawingPanel(dimensionMur, p_nouvDimensionDrawingPanel, CaisseOutils.RATIO_TEST);
            mur.updateShapePosition(coordonneesAjustees.x, coordonneesAjustees.y);

            // met à jour les coordonnées du tout les accessoire de ce mur par rapport a lui-même comme point de reférence
            ((Mur) mur).getAccessoiresListes().forEach(accessoire -> updateAccesssoirePosition(accessoire, coordonneesAjustees, p_ratio));
        }
        toit.updateShapePositionComposantes(getMurVirtuel(FACADE), getMurVirtuel(GAUCHE), getMurVirtuel(ARRIERE), getMurVirtuel(DROITE));
        toit.updateDimensionsComposantes(getMurVirtuel(FACADE), getMurVirtuel(GAUCHE));
    }

    public void updateCoteVirtuelDesMurs() {
        if (sensToit == ARRIERE) {
            for (Contour mur : contourChalet) {
                ((Mur) mur).updateCoteVirtuel(mur.getCote());
            }
        } else if (sensToit == FACADE) {
            getMur(ARRIERE).updateCoteVirtuel(FACADE);
            getMur(FACADE).updateCoteVirtuel(ARRIERE);
            getMur(GAUCHE).updateCoteVirtuel(DROITE);
            getMur(DROITE).updateCoteVirtuel(GAUCHE);
        } else if (sensToit == GAUCHE) {
            getMur(ARRIERE).updateCoteVirtuel(DROITE);
            getMur(FACADE).updateCoteVirtuel(GAUCHE);
            getMur(GAUCHE).updateCoteVirtuel(ARRIERE);
            getMur(DROITE).updateCoteVirtuel(FACADE);
        } else {
            getMur(ARRIERE).updateCoteVirtuel(GAUCHE);
            getMur(FACADE).updateCoteVirtuel(DROITE);
            getMur(GAUCHE).updateCoteVirtuel(FACADE);
            getMur(DROITE).updateCoteVirtuel(ARRIERE);
        }
    }

    public void updateShapeVueDessus(Contour p_mur, Shape p_shapeVueDessus) {
        if (p_mur != null)
            p_mur.updateShapeVueDessus(p_shapeVueDessus);
    }

    public void updateShapeDebordement(Hashtable<String, CaisseOutils.Tuple<Shape, Shape>> p_tableau) {
        CaisseOutils.Tuple<Shape, Shape> murGaucheShapes = (p_tableau.get(GAUCHE.orientation + "I"));
        if (murGaucheShapes != null) {
            Mur murGauche = getMur(GAUCHE);
            murGauche.updateShapeDebordement(ARRIERE, murGaucheShapes.x);
            murGauche.updateShapeDebordement(FACADE, murGaucheShapes.y);
            murGauche.updateShape(p_tableau.get(GAUCHE.orientation + "II").x);
        }
        CaisseOutils.Tuple<Shape, Shape> murDroiteShapes = (p_tableau.get(DROITE.orientation + "I"));
        if (murDroiteShapes != null) {
            Mur murDroite = getMur(DROITE);
            murDroite.updateShapeDebordement(FACADE, murDroiteShapes.x);
            murDroite.updateShapeDebordement(ARRIERE, murDroiteShapes.y);
            murDroite.updateShape(p_tableau.get(DROITE.orientation + "II").x);
        }

        CaisseOutils.Tuple<Shape, Shape> murArriereShapes = (p_tableau.get(ARRIERE.orientation + "I"));
        if (murArriereShapes != null) {
            Mur murArriere = getMur(ARRIERE);
            murArriere.updateShapeDebordement(DROITE, murArriereShapes.x);
            murArriere.updateShapeDebordement(GAUCHE, murArriereShapes.y);
            murArriere.updateShape(p_tableau.get(ARRIERE.orientation + "II").x);
        }

        CaisseOutils.Tuple<Shape, Shape> murFacadeShapes = (p_tableau.get(FACADE.orientation + "I"));
        if (murFacadeShapes != null) {
            Mur murFacade = getMur(FACADE);
            murFacade.updateShapeDebordement(GAUCHE, murFacadeShapes.x);
            murFacade.updateShapeDebordement(DROITE, murFacadeShapes.y);
            murFacade.updateShape(p_tableau.get(FACADE.orientation + "II").x);
        }
    }


    private void updateAccesssoirePosition(Accessoire p_accessoire, Point pointDeReferenceOrigine, float p_ratio) {
        Point coordonneesAjustees = CaisseOutils.ajusterCoordonneesToEmplacement(pointDeReferenceOrigine, p_accessoire.getCoordonneesEmplacement(), CaisseOutils.RATIO_TEST);
        p_accessoire.updateShapePosition(coordonneesAjustees.x, coordonneesAjustees.y);
    }

    public boolean updateCoordonnees(Accessoire pAcc, UniteImperiale[] p_coordonneesEmplacementNouveau, float p_ratio) {
        boolean estUpdated = false;
        for (Contour mur : contourChalet) {
            // Vérifie si l'accessoire est sélectionné et s'il est sur le mur actuel
            if (pAcc.estSelectionne() && mur.getCote() == pAcc.getMurEmplacement()) {
                // Vérifie s'il s'agit d'une porte pour ajuster la coordonnée Y si nécessaire
                if (pAcc instanceof Porte) {
                    UniteImperiale distMinimumSol = new UniteImperiale("3\"");
                    p_coordonneesEmplacementNouveau[1] = mur.getLargeur().soustraire(pAcc.getLargeur().additionner(distMinimumSol));
                }

                // Valide le déplacement de l'accessoire sur le mur actuel
                if (mur.validerDeplacementAccessoire(pAcc, p_coordonneesEmplacementNouveau)) {
                    // Obtient les coordonnées du mur pour l'accessoire
                    Point coordonnee_mur = new Point((int) mur.getShape().getBounds().getX(), (int) mur.getShape().getBounds().getY());
                    pAcc.modifierCoordonnees(p_coordonneesEmplacementNouveau); // Modifie les coordonnées de l'accessoire (backend)
                    updateAccesssoirePosition(pAcc, coordonnee_mur, p_ratio); // Met à jour la position de l'accessoire (la vue)
                    estUpdated = true;
                    mur.getAccessoiresListes().forEach(accessoire -> {
                        mur.validerDeplacementAccessoire(accessoire, accessoire.getCoordonneesEmplacement());
                    });
                }
            }
        }
        return estUpdated;
    }

    public void setVueChalet(Orientations pVueMode) {
        for (Panneau mur : contourChalet) {
            if (mur.getCote() == pVueMode) {
                mur.setAffichee(true);
            } else {
                mur.setAffichee(false);
            }
        }
        setVueToit(pVueMode);
    }

    private void setVueToit(Orientations pVueMode) {
        toit.afficherComposantes(getDonneesMur(), pVueMode);
    }


    public boolean updateAccessoireDimension(Accessoire pAcc, UniteImperiale p_largeur, UniteImperiale p_longueur) {
//        float p_largeurdecimal = p_largeur.pouces()/12;
        boolean estUpdated = false;
        for (Contour mur : contourChalet) {
            if (pAcc.estSelectionne() && mur.getCote() == pAcc.getMurEmplacement()) {

                UniteImperiale[] nouvCoordonneesEmplacementAccesoire = new UniteImperiale[2];
                nouvCoordonneesEmplacementAccesoire[0] = pAcc.getCoordonneesEmplacement()[0];

                if (p_largeur.pieds() >= pAcc.getLargeur().pieds())
                    nouvCoordonneesEmplacementAccesoire[1] = pAcc.getCoordonneesEmplacement()[1].soustraire(p_largeur.soustraire(pAcc.getLargeur()));

                else if (p_largeur.pieds() <= pAcc.getLargeur().pieds()) {
                    UniteImperiale soustraction = p_largeur.soustraire(pAcc.getLargeur());
                    boolean negatif = soustraction.pouces() < 0;
                    soustraction = negatif ? pAcc.getLargeur().soustraire(p_largeur) : soustraction;
                    nouvCoordonneesEmplacementAccesoire[1] = pAcc.getCoordonneesEmplacement()[1].additionner(soustraction);
                }

                boolean estAccessoireDejaMarqueInvalide = !pAcc.estValide();
                // validation pour modification sur une porte
                boolean estPorteEtEstValide = pAcc instanceof Porte && mur.validerPorte(nouvCoordonneesEmplacementAccesoire, new float[]{p_longueur.pieds(), p_largeur.pieds()}); // new float[]{p_longueur.pouces() / 12, p_largeurdecimal}
                boolean pasTropProchePorte = mur.validerPoximiteAccessoire(pAcc, nouvCoordonneesEmplacementAccesoire, new float[]{p_longueur.pieds(), p_largeur.pieds()}); // new float[]{p_longueur.pouces() / 12, p_largeurdecimal}

                // validation pour modification sur une fenetre
                boolean estFenetreEtEstValide = pAcc instanceof Fenetre && mur.validerFenetre(pAcc.getCoordonneesEmplacement(), new float[]{p_longueur.pieds(), p_largeur.pieds()}); // new float[]{p_longueur.pouces() / 12, p_largeurdecimal}
                boolean pasTropProcheFenetre = mur.validerPoximiteAccessoire(pAcc, pAcc.getCoordonneesEmplacement(), new float[]{p_longueur.pieds(), p_largeur.pieds()}); // new float[]{p_longueur.pouces() / 12, p_largeurdecimal}


                if (((pasTropProcheFenetre || estAccessoireDejaMarqueInvalide) && estFenetreEtEstValide) || ((pasTropProchePorte || estAccessoireDejaMarqueInvalide) && estPorteEtEstValide)) {
                    pAcc.updateDimensionAccesoire(p_largeur, p_longueur);
                    estUpdated = true;
                    if (pAcc instanceof Porte) {
                        Point coordonnee_mur = new Point((int) mur.getShape().getBounds().getX(), (int) mur.getShape().getBounds().getY());
                        pAcc.modifierCoordonnees(nouvCoordonneesEmplacementAccesoire);
                        Point nouvPosPorte = CaisseOutils.ajusterCoordonneesToEmplacement(coordonnee_mur, nouvCoordonneesEmplacementAccesoire, CaisseOutils.RATIO_TEST);
                        pAcc.updateShapePosition((float) nouvPosPorte.getX(), (float) nouvPosPorte.getY());
                        estUpdated = true;
                    }
                    if (((pasTropProcheFenetre && estFenetreEtEstValide) || (pasTropProchePorte && estPorteEtEstValide)))
                        mur.retirerAccessoireDeLaListeInvalides(pAcc);
                }
            }
        }
        return estUpdated;
    }

    public ConcurrentLinkedQueue<ContenuSTL> exporter(ExportationMode exportationMode, String p_dossierDestination) {

        ConcurrentLinkedQueue<ContenuSTL> listContenuSTL = new ConcurrentLinkedQueue<>();
        Triplet<UniteImperiale, UniteImperiale, UniteImperiale> chaletDimension = null;

        if (toit.getOrientation() == FACADE || toit.getOrientation() == ARRIERE) {
            chaletDimension = new Triplet<>(getMur(GAUCHE).getLongueur(), getMur(FACADE).getLongueur(), getMur(GAUCHE).getLargeur());
        } else
            chaletDimension = new Triplet<>(getMur(FACADE).getLongueur(), getMur(GAUCHE).getLongueur(), getMur(GAUCHE).getLargeur());

        for (Contour contour : contourChalet) {
            if (exportationMode == ExportationMode.FINI) {
                listContenuSTL.add(contour.genererVersionFini(p_dossierDestination, chaletDimension));
            } else if (exportationMode == ExportationMode.RETRAIT) {
                listContenuSTL.add(contour.genererVersionRetrait(p_dossierDestination, chaletDimension, ((Mur) contour).getNbrAccessoire()));
                listContenuSTL.add(contour.genererVersionRetrait(p_dossierDestination, chaletDimension, ((Mur) contour).getNbrAccessoire()));
                listContenuSTL.addAll(contour.demandeGenererAccessoiresSTL(p_dossierDestination, chaletDimension));
            } else if (exportationMode == ExportationMode.BRUT) {
                listContenuSTL.add(contour.genererVersionBrut(p_dossierDestination, chaletDimension));
            }
        }
        listContenuSTL.addAll(toit.preparerSTLToit(exportationMode, p_dossierDestination, chaletDimension));

        return listContenuSTL;
    }

    public boolean contientAccessoiresInvalide() {
        for (Contour mur : contourChalet) if ((mur.contientAccessoiresInvalide())) return true;
        return false;
    }

    public Mur getMurSelectionne() {
        for (Contour mur : contourChalet) {
            if (mur.estSelectionne())
                return (Mur) mur;
        }
        return null;
    }

    public void redimenssionnerChalet(Dimension p_nouvDimensionDrawingPanel, UniteImperiale p_largeur, UniteImperiale p_longeur, UniteImperiale p_hauteur, UniteImperiale p_epaisseur) {

        // modifier epaisseur, longueur et hauteur des mur (largeur)
        Mur murTemp = getMur(FACADE);
        modifierMur(p_nouvDimensionDrawingPanel, p_hauteur, p_longeur, p_epaisseur, murTemp.getEspacementAccessoires(), true, FACADE);

        // modifier largeur (longueur de mur gauche droite)
        murTemp = getMur(GAUCHE);
        modifierMur(p_nouvDimensionDrawingPanel, p_hauteur, p_largeur, p_epaisseur, murTemp.getEspacementAccessoires(), true, GAUCHE);
    }


    public List<PanneauToit> getToit() {
        List<PanneauToit> list = new ArrayList<>();
        for (PanneauToit p : toit.donneesToit()) {
            list.add(p);
        }
        return list;
    }

    public void changerSensToit(Orientations p_oritenation) {
        toit.changerOrientation(p_oritenation);
        sensToit = p_oritenation;
        updateCoteVirtuelDesMurs();
    }

    public void modifieAngleToit(float angle) {
        toit.modifierAngle(angle);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (retraitSupplementaire == null) {
            retraitSupplementaire = UniteImperiale.decimalToUnit(0);  // Or another appropriate non-null value
        }
    }

    public Object clone() {

        return new Chalet(this);
    }
}
