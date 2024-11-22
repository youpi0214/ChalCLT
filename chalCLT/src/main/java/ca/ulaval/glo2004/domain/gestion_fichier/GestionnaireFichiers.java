package ca.ulaval.glo2004.domain.gestion_fichier;

import ca.ulaval.glo2004.services.ContenuSTL;

import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GestionnaireFichiers {


    private GestionnairePiles gestionnairePiles;

    public GestionnaireFichiers() {
        gestionnairePiles = new GestionnairePiles();
    }



    public void exporter(ConcurrentLinkedQueue<ContenuSTL> listeContenuSTL) {
        for (ContenuSTL contenuSTL : listeContenuSTL) {
            try (FileWriter writer = new FileWriter(contenuSTL.fileDestination)) {
                writer.write(contenuSTL.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public AbstractMap.SimpleEntry<ModificationType, Object> undo() {
        return gestionnairePiles.undo();
    }

    public AbstractMap.SimpleEntry<ModificationType, Object> redo() {
        return gestionnairePiles.redo();
    }

    public void addToRedo(ModificationType p_typeModification, Object p_changes) {
        gestionnairePiles.addToRedo(p_typeModification, p_changes);
    }

    public void addToUndo(ModificationType p_typeModification, Object p_changes) {
        gestionnairePiles.addToUndo(p_typeModification, p_changes);
    }

    /**
     * only called when a change is made by the user, this method should never be called when
     * executing an undo or redo
     *
     * @param p_typeModification
     * @param p_changes
     */
    public void modif(ModificationType p_typeModification, Object p_changes) {
        gestionnairePiles.modif(p_typeModification, p_changes);

    }


}