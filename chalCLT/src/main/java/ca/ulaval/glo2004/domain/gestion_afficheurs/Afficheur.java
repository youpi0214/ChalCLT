package ca.ulaval.glo2004.domain.gestion_afficheurs;

import ca.ulaval.glo2004.domain.Controleur;
import ca.ulaval.glo2004.gui.HomePanel;
import ca.ulaval.glo2004.services.Orientations;

import java.awt.*;
import java.io.Serializable;


public abstract class Afficheur implements Serializable {

	protected Graphics2D graphicsZoomCourant;


	public Afficheur() {
	}
	public abstract void afficherInfoElement();

	public Graphics2D getGraphicsZoomCourant() {
		return graphicsZoomCourant;
	}

	/**
	 * 
	 * @param g
	 */
	public void draw(Graphics g) {
            
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param g
	 */
	public void dessinerGrille(Graphics g) {
	}

}