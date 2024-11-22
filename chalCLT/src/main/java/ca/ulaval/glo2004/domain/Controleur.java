package ca.ulaval.glo2004.domain;


import ca.ulaval.glo2004.domain.gestion_chalet.*;
import ca.ulaval.glo2004.domain.gestion_fichier.GestionnaireFichiers;
import ca.ulaval.glo2004.domain.gestion_fichier.ModificationType;
import ca.ulaval.glo2004.services.*;


import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.AbstractMap;
import java.util.Hashtable;
import java.util.List;

import static ca.ulaval.glo2004.domain.gestion_chalet.Chalet.RETRAIT_SUPPLEMENTAIRE_DEFAUT;
import static ca.ulaval.glo2004.domain.gestion_fichier.ModificationType.DEPLACER;
import static ca.ulaval.glo2004.domain.gestion_fichier.ModificationType.MODIFIER_RETRAIT;

public class Controleur implements Serializable {

    private Chalet chalet;
    private GestionnaireFichiers gestionnaireFichiers; //sera utile dans le futur

    public Controleur() {

    }

    public void nouveauProjet() {
        this.chalet = new Chalet();
        gestionnaireFichiers = new GestionnaireFichiers();
    }

    /**
     * @param p_largeur
     * @param p_longueur
     * @param p_orientation
     */
    public void creationFenetre(UniteImperiale p_largeur, UniteImperiale p_longueur, UniteImperiale[] p_emplacement, Orientations p_orientation) {
        Accessoire accessoire = chalet.ajouterAccessoire(p_largeur, p_longueur, p_emplacement, p_orientation, CreationAccessoireMode.FENETRE);
        if (accessoire == null) {
            JOptionPane.showMessageDialog(null, "Creation Fenetre invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
        } else {
            gestionnaireFichiers.modif(ModificationType.AJOUTER, accessoire);
        }
    }

    public void creationPorte(UniteImperiale p_largeur, UniteImperiale p_longueur, UniteImperiale p_emplacementX, Orientations p_orientation) {
        Accessoire accessoire = chalet.ajouterAccessoire(p_largeur, p_longueur, new UniteImperiale[]{p_emplacementX, null}, p_orientation, CreationAccessoireMode.PORTE);
        if (accessoire == null) {
            JOptionPane.showMessageDialog(null, "Creation Porte invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
        } else {
            gestionnaireFichiers.modif(ModificationType.AJOUTER, accessoire);
        }
    }

    public void supprimerAccessoire(UniteImperiale[] p_coordonneesEmplacement, Orientations p_orientation) {
        Accessoire accessoire = chalet.supprimerAccessoire(p_coordonneesEmplacement, p_orientation);
        if (accessoire != null) gestionnaireFichiers.modif(ModificationType.EFFACER, accessoire);
    }

    public void sauvegarder(String nomFichier) {
        try {
            FileOutputStream fos = new FileOutputStream(nomFichier);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(chalet);
            out.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void charger(String nomFichier) {
        try {
            FileInputStream fis = new FileInputStream(nomFichier);
            ObjectInputStream in = new ObjectInputStream(fis);
            this.chalet = (Chalet) in.readObject();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        gestionnaireFichiers = new GestionnaireFichiers();
    }

    public void undo() {
        // TODO - implement Controleur.undo
        AbstractMap.SimpleEntry<ModificationType, Object> undoState = gestionnaireFichiers.undo();
        if (undoState != null)
            handle_undo_redo(undoState, true);
    }

    public void redo() {
        // TODO - implement Controleur.redo
        AbstractMap.SimpleEntry<ModificationType, Object> redoState = gestionnaireFichiers.redo();
        if (redoState != null)
            handle_undo_redo(redoState, false);
    }

    private void handle_undo_redo(AbstractMap.SimpleEntry<ModificationType, Object> state, boolean undo) {
        switch (state.getKey()) {
            case EFFACER:
                if (undo) {
                    gestionnaireFichiers.addToRedo(state.getKey(), state.getValue());
                    chalet.ajouterAccessoire((Accessoire) state.getValue());
                } else {
                    gestionnaireFichiers.addToUndo(state.getKey(), state.getValue());
                    chalet.supprimerAccessoire(((Accessoire) state.getValue()).getCoordonneesEmplacement(), ((Accessoire) state.getValue()).getMurEmplacement());
                }
                break;
            case AJOUTER:
                if (undo) {
                    gestionnaireFichiers.addToRedo(state.getKey(), state.getValue());
                    chalet.supprimerAccessoire(((Accessoire) state.getValue()).getCoordonneesEmplacement(), ((Accessoire) state.getValue()).getMurEmplacement());
                } else {
                    gestionnaireFichiers.addToUndo(state.getKey(), state.getValue());
                    chalet.ajouterAccessoire((Accessoire) state.getValue());
                }
                break;
            case MODIFIER_RETRAIT:
                if (undo)
                    gestionnaireFichiers.addToRedo(state.getKey(), getMur(((Mur) state.getValue()).getCote()));
                else
                    gestionnaireFichiers.addToUndo(state.getKey(), Chalet.getRetraitSupplementaire());
                chalet.changerRetraitSupplementaire((UniteImperiale) state.getValue());
                // Add cases for other ModificationType values as needed
                break;
            case DEPLACER:
                if (undo) {

                } else {

                }
                break;
        }
    }


    /**
     * @param p_largeur
     * @param p_longueur
     */
    public void modifierMur(Dimension p_nouvDimensionDrawingPanel, UniteImperiale p_largeur, UniteImperiale p_longueur, UniteImperiale p_epaisseur, UniteImperiale p_espacement) {
        if (getMur(Orientations.DROITE).getEpaisseur().pouces() != p_epaisseur.pouces())
            changerRetraitSupplementaire(RETRAIT_SUPPLEMENTAIRE_DEFAUT);
        chalet.modifierMur(p_nouvDimensionDrawingPanel, p_largeur, p_longueur, p_epaisseur, p_espacement, false, null);
    }

    public void changerRetraitSupplementaire(UniteImperiale p_nouvRetraitSupplementaire) {
        UniteImperiale ancienRetrait = Chalet.getRetraitSupplementaire();
        if (!chalet.changerRetraitSupplementaire(p_nouvRetraitSupplementaire)) {
            JOptionPane.showMessageDialog(null, "Le retrait doit être inférieur à l'épaisseur du Mur", "Erreur", JOptionPane.ERROR_MESSAGE);
        } else
            gestionnaireFichiers.modif(MODIFIER_RETRAIT, ancienRetrait);
    }

    public void zoomIn() {
        // TODO - implement Controleur.zoomIn
        throw new UnsupportedOperationException();
    }

    public void zoomOut() {
        // TODO - implement Controleur.zoomOut
        throw new UnsupportedOperationException();
    }

    /**
     * @param p_clickedPointInInches
     * @param p_vueMode
     */
    public Affichable selectionnerComposanteChalet(Point2D p_clickedPointInInches, Orientations p_vueMode) {
        return chalet.selectionnerComposanteChalet(p_clickedPointInInches, p_vueMode);
    }

    public Affichable survolMur(Point2D p_hoveredPointInInches, Orientations p_vueMode) {
        return chalet.survolMur(p_hoveredPointInInches, p_vueMode);
    }

    public void deselectionnerToutSauf() {
        chalet.deselectionnerTout();
    }

    public void fermerProjet() {
        // TODO - implement Controleur.fermerProjet
    }


    public List<Mur> getComposantes() {
        return chalet.getDonneesMur();
    }

    public List<PanneauToit> getToit() {
        return chalet.getToit();
    }

    /**
     * permet d'ajuster les coordonnées des contours selon la taille du drawingPanel
     *
     * @param p_nouvDimensionDrawingPanel
     */
    public void updateContourPosition(Dimension p_nouvDimensionDrawingPanel, int p_ratio) {
        chalet.updateContourPosition(p_nouvDimensionDrawingPanel, p_ratio);
    }

    public void setVueChalet(Orientations p_vueMode) {
        chalet.setVueChalet(p_vueMode);
    }

    public void updateCoordonnees(Accessoire pAcc, UniteImperiale[] p_coordonneesEmplacementNouveau, int p_ratio) {
        UniteImperiale[] oldPosition = pAcc.getCoordonneesEmplacement();
        if (!(chalet.updateCoordonnees(pAcc, p_coordonneesEmplacementNouveau, p_ratio))) {
            JOptionPane.showMessageDialog(null, "Emplacement Invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
        } else
            gestionnaireFichiers.modif(DEPLACER, new CaisseOutils.Tuple<Accessoire, UniteImperiale[]>(pAcc, oldPosition));


    }

    public void updateAccessoireDimension(Accessoire pAcc, UniteImperiale p_largeur, UniteImperiale p_longueur) {
        if (!(chalet.updateAccessoireDimension(pAcc, p_largeur, p_longueur))) {
            JOptionPane.showMessageDialog(null, "Dimensions invalides", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Mur getMur(Orientations pOrientations) {
        return chalet.getMur(pOrientations);
    }

    public Toit getToitEntier(){
        return  chalet.getToitEntier();
    }

    public Mur getMurSelectionnne() {
        return chalet.getMurSelectionne();
    }

    public void updateShapeVueDessus(Hashtable<Orientations, Shape> p_tableau) {
        for (Orientations p_oritentation : Orientations.values()) {
            Shape murTempShape = (p_tableau.get(p_oritentation));
            Mur murTemp = getMur(p_oritentation);
            chalet.updateShapeVueDessus(murTemp, murTempShape);
        }
    }

    public void updateShapeDebordement(Hashtable<String, CaisseOutils.Tuple<Shape, Shape>> p_tableau) {
        chalet.updateShapeDebordement(p_tableau);
    }

    public void exporter(ExportationMode exportationMode, String p_dossierDestination) {
        gestionnaireFichiers.exporter(chalet.exporter(exportationMode, p_dossierDestination));
    }

    public boolean contientAccessoiresInvalide() {
        return chalet.contientAccessoiresInvalide();
    }

    public void redimenssionnerChalet(Dimension p_nouvDimensionDrawingPanel, UniteImperiale p_largeur, UniteImperiale p_longeur, UniteImperiale p_hauteur, UniteImperiale p_epaisseur) {
        if (getMur(Orientations.DROITE).getEpaisseur().pouces() != p_epaisseur.pouces())
            changerRetraitSupplementaire(RETRAIT_SUPPLEMENTAIRE_DEFAUT);
        chalet.redimenssionnerChalet(p_nouvDimensionDrawingPanel, p_largeur, p_longeur, p_hauteur, p_epaisseur);
    }

    public void changerOrientationToit(Orientations orientation) {
        chalet.changerSensToit(orientation);
    }

    public void modifierAngleToit(float angle) {
        chalet.modifieAngleToit(angle);
    }
}

