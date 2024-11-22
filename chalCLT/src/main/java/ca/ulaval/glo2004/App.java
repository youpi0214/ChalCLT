package ca.ulaval.glo2004;


import ca.ulaval.glo2004.gui.HomePanel;

import javax.swing.*;
import java.awt.*;


public class App {
    //Exemple de creation d'une fenetre et d'un bouton avec swing. Lorsque vous allez creer votre propre GUI
    // Vous n'aurez pas besoin d'ecrire tout ce code, il sera genere automatiquement par intellij ou netbeans
    // Par contre vous aurez a creer les actions listener pour vos boutons et etc.

    public static void main(String[] args) {
//		UIManager.put("Synthetica.window.decoration", Boolean.FALSE);
//		try {
//			UIManager.setLookAndFeel(new SyntheticaAluOxideLookAndFeel());
//		} catch (UnsupportedLookAndFeelException | ParseException e) {
//			e.printStackTrace();
//		}

        // how to maximize right off the bat
//        homePanel.setExtendedState(homePanel.getExtendedState()
//                | JFrame.MAXIMIZED_BOTH);

    
        HomePanel homePanel = new HomePanel();
        homePanel.pack();
        homePanel.setVisible(true);

    }
}

