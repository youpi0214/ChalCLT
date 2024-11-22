package ca.ulaval.glo2004.services;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CaisseOutils {

    /**
     * echelle auquelle l'affichage est faite 80 veut dire 1 unite de longueur = 80 pixels
     */
    public static final int RATIO_TEST = 40;


    public static final String PATH_VERS_DOSSIER_RESSOURCE_IMAGES = "src/main/resource/images/";
    public static final String CHALCLT_FILE_NAME = "ChalCLT_";

    public static final String EXTENSTION = ".stl";
    public static final UniteImperiale VALEUR_BOUCHE_TROU = new UniteImperiale("");

    public CaisseOutils() {
    }

    public static final String ONLY_POSITIVE_INT_OR_DECIMAL = "^[0-9]*\\.?[0-9]+$";


    public static Point ajusterCoordonneesToDrawingPanel(float[] p_dimensionComposante, Dimension p_dimensionDrawingPanel, int p_ratio) {
        Point coordonneesAjustees = new Point();

        //TODO RATIO_TEST va etre remplacer par p_ratio dans le futur
        int squareW = (int) (p_dimensionComposante[0] * RATIO_TEST);
        int squareH = (int) (p_dimensionComposante[1] * RATIO_TEST);
        int squareX = p_dimensionDrawingPanel.width / 2 - squareW / 2;
        int squareY = p_dimensionDrawingPanel.height / 2 - squareH / 2;

        coordonneesAjustees.setLocation(squareX, squareY);

        return coordonneesAjustees;
    }

    public static Point ajusterCoordonneesToEmplacement(Point pointDeReferenceOrigine, UniteImperiale[] p_coordonneesEmplacement, int p_ratio) {
        Point coordonneesAjustees = new Point();

        //TODO RATIO_TEST va etre remplacer par p_ratio dans le futur
        int squareX = (int) (pointDeReferenceOrigine.x + p_coordonneesEmplacement[0].pieds() * RATIO_TEST);
        int squareY = (int) (pointDeReferenceOrigine.y + p_coordonneesEmplacement[1].pieds() * RATIO_TEST);

        coordonneesAjustees.setLocation(squareX, squareY);

        return coordonneesAjustees;
    }

    public static UniteImperiale[] ajusterCoordonneesToEmplacementToUniImp(Point pointDeReferenceOrigine, Point p_coordonneesEmplacement) {


        //TODO RATIO_TEST va etre remplacer par p_ratio dans le futur
        double squareX = (p_coordonneesEmplacement.x - pointDeReferenceOrigine.x) * 12 / (double) RATIO_TEST;
        double squareY = (p_coordonneesEmplacement.y - pointDeReferenceOrigine.y) * 12 / (double) RATIO_TEST;

        squareX = squareX < 0 ? 0 : squareX;
        squareY = squareY < 0 ? 0 : squareY;

        String squareXStringValue = squareX + "000000";
        String squareYStringValue = squareY + "000000";

        // Find the position of the decimal point
        int indexDecimalX = squareXStringValue.indexOf('.');
        int indexDecimalY = squareYStringValue.indexOf('.');

        // Truncate to 4 decimal places
        String truncatedXValue = squareXStringValue.substring(0, indexDecimalX + UniteImperiale.PRECISION);
        String truncatedYValue = squareYStringValue.substring(0, indexDecimalY + UniteImperiale.PRECISION);


        double squareXdecimal = Double.parseDouble(truncatedXValue);
        double squareYdecimal = Double.parseDouble(truncatedYValue);

        return new UniteImperiale[]{UniteImperiale.decimalToUnit(squareXdecimal), UniteImperiale.decimalToUnit(squareYdecimal)};
    }

    public static UniteImperiale decimalToUniImp(double valeur) {

        String squareXStringValue = valeur + "000000";

        // Find the position of the decimal point
        int indexDecimalX = squareXStringValue.indexOf('.');


        // Truncate to 4 decimal places
        String truncatedXValue = squareXStringValue.substring(0, indexDecimalX + UniteImperiale.PRECISION);


        double squareXdecimal = Double.parseDouble(truncatedXValue);

        return UniteImperiale.decimalToUnit(squareXdecimal);
    }

    public static float poucesToPieds(float pouces) {
        return pouces / 12;
    }

    public static float piedsToPouces(float pieds) {
        return pieds * 12;
    }

    public static Image loadImage(String path, Dimension p_displayDimension) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(path));
            return bufferedImage.getScaledInstance(p_displayDimension.width, p_displayDimension.height,
                    Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class Tuple<A, B> {
        public final A x;
        public final B y;

        public Tuple(A first, B second) {
            this.x = first;
            this.y = second;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }

    public static class Triplet<A, B, C> {
        public final A x;
        public final B y;
        public final C z;

        public Triplet(A first, B second, C third) {
            this.x = first;
            this.y = second;
            this.z = third;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + "," + z + ")";
        }
    }

}
