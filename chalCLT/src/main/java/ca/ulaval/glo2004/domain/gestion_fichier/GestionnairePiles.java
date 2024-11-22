package ca.ulaval.glo2004.domain.gestion_fichier;

import java.util.AbstractMap;

public class GestionnairePiles {

    private PileModification modifPrecedentes;
    private PileModification modifSuivantes;

    public GestionnairePiles() {
        modifPrecedentes = new PileModification();
        modifSuivantes = new PileModification();
    }

    protected AbstractMap.SimpleEntry<ModificationType, Object> undo() {
        AbstractMap.SimpleEntry<ModificationType, Object> state = modifPrecedentes.retirerModif();

        return state;
    }

    protected AbstractMap.SimpleEntry<ModificationType, Object> redo() {
        AbstractMap.SimpleEntry<ModificationType, Object> state = modifSuivantes.retirerModif();

        return state;
    }

    protected void addToRedo(ModificationType p_typeModification, Object p_changes) {
        modifSuivantes.ajouterModif(p_typeModification, p_changes);

    }

    protected void addToUndo(ModificationType p_typeModification, Object p_changes) {
        modifPrecedentes.ajouterModif(p_typeModification, p_changes);

    }

    /**
     * only called when a change is made by the user, this method should never be called when
     * executing an undo or redo
     *
     * @param p_typeModification
     * @param p_changes
     */
    protected void modif(ModificationType p_typeModification, Object p_changes) {
        modifPrecedentes.ajouterModif(p_typeModification, p_changes);
        modifSuivantes.viderPile();
    }

}