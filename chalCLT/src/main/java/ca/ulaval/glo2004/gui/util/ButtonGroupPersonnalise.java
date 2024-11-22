package ca.ulaval.glo2004.gui.util;

import javax.swing.*;

public class ButtonGroupPersonnalise extends ButtonGroup {

    @Override
    public void setSelected(ButtonModel model, boolean selected) {
        if (selected) {
            super.setSelected(model, selected);
        } else {
            clearSelection();
        }
    }

}