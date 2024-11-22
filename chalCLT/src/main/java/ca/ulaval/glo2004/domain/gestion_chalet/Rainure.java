package ca.ulaval.glo2004.domain.gestion_chalet;

import ca.ulaval.glo2004.services.CaisseOutils;
import ca.ulaval.glo2004.services.ContenuSTL;
import ca.ulaval.glo2004.services.Exportable;
import ca.ulaval.glo2004.services.UniteImperiale;
import ca.ulaval.glo2004.services.CaisseOutils.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class Rainure extends Affichable implements Exportable, Serializable {

	private float longueur;
	private float largeur;
	private float epaisseur;

	private float[] coordonneesEmplacement;

	/**
	 * 
	 * @param p_rainure
	 */
	public Rainure(Rainure p_rainure) {
		this(p_rainure.getLargeur(), p_rainure.getLongueur(), p_rainure.getEpaisseur(), p_rainure.getCoordonneesEmplacement());
	}

	/**
	 * 
	 * @param p_longueur
	 * @param p_largeur
	 * @param p_epaisseur
	 */
	public Rainure(float p_longueur, float p_largeur, float p_epaisseur, float[] p_coordonnesEmplacement) {
		// TODO - implement Rainure.Rainure
		this.longueur = p_longueur;
		this.largeur = p_largeur;
		this.epaisseur = p_epaisseur;
		coordonneesEmplacement = p_coordonnesEmplacement;
		this.setShape(new Area(new Rectangle2D.Double(0, 0, longueur * CaisseOutils.RATIO_TEST, largeur * CaisseOutils.RATIO_TEST)));
	}

	public float getLongueur() {
		return longueur;
	}

	public float getLargeur() {
		return largeur;
	}

	public float getEpaisseur() {
		return epaisseur;
	}

	public float[] getCoordonneesEmplacement() {
		return coordonneesEmplacement.clone();
	}


	@Override
	public ContenuSTL genererVersionBrut(String fileDestination, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension) {
		return null;
	}

	@Override
	public ContenuSTL genererVersionFini(String fileDestination, Triplet<UniteImperiale,UniteImperiale, UniteImperiale> p_chaletDimmension) {
		return null;
	}

	@Override
	public ContenuSTL genererVersionRetrait(String fileDestination, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension, int sequentiel) {
		return null;
	}

	@Override
	public Shape getShape() {
		return new Rectangle2D.Double(shape.getBounds().getX(), shape.getBounds().getY(), shape.getBounds().getWidth(), shape.getBounds().getHeight());
	}

	@Override
	public Shape getOriginalShape() {
		return null;
	}


	@Override
	protected void updateShapePosition(float x, float y) {

	}
}