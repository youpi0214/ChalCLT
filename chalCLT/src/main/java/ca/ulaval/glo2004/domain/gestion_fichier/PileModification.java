package ca.ulaval.glo2004.domain.gestion_fichier;

import ca.ulaval.glo2004.domain.gestion_fichier.ModificationType;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Stack;

public class PileModification {

    private Stack<AbstractMap.SimpleEntry<ModificationType, Object>> pileModifications;

    public PileModification() {
        this.pileModifications = new Stack<>();
    }

    /**
     * Adds a modification to the stack.
     *
     * @param typeModif The type of modification (e.g., EFFACER, AJOUTER, etc.).
     * @param p_modif   The details of the modification as a HashMap.
     */
    protected void ajouterModif(ModificationType typeModif, Object p_modif) {
        pileModifications.push(new AbstractMap.SimpleEntry<>(typeModif, p_modif));
    }

    /**
     * Removes and returns the last modification from the stack.
     *
     * @return An AbstractMap.SimpleEntry containing the type of modification and its details.
     */
    protected AbstractMap.SimpleEntry<ModificationType, Object> retirerModif() {
        if (!pileModifications.isEmpty()) {
            return pileModifications.pop();
        }
        return null; // or throw an exception, depending on your requirements
    }

    public int size() {
        return pileModifications.size();
    }

    /**
     * Clears the stack of modifications.
     */
    protected void viderPile() {
        pileModifications.clear();
    }
}
