package ca.ulaval.glo2004.services;

import ca.ulaval.glo2004.domain.gestion_chalet.Mur;

import java.util.*;

public class ContenuSTL {
    public static final String SOLID = "solid\n\n";
    public static final String SOLIDEND = "endsolid";
    public final String fileName;
    public final String fileDestination;

    public ArrayList<RectangleSTL> rectangleSTLArrayList;
    public ArrayList<TriangleSTL> triangleSTLArrayList;

    @Override
    public String toString() {
        StringBuilder contenu = new StringBuilder(SOLID);
        for (RectangleSTL rectangleSTL : rectangleSTLArrayList)
            contenu.append(rectangleSTL.toString());
        for (TriangleSTL triangleSTL : triangleSTLArrayList)
            contenu.append(triangleSTL.toString());

        return contenu + SOLIDEND;
    }

    public ContenuSTL(String p_fileName, String p_fileDestination) {
        this.fileName = p_fileName;
        this.fileDestination = p_fileDestination + "\\" + p_fileName;
        rectangleSTLArrayList = new ArrayList<>();
        triangleSTLArrayList = new ArrayList<>();
    }

    public void ajouterContenuSolide(RectangleSTL... rectangleSTL) {
        rectangleSTLArrayList.addAll(Arrays.asList(rectangleSTL));
    }

    public void ajouterContenuSolide(TriangleSTL... triangleSTL) {
        triangleSTLArrayList.addAll(Arrays.asList(triangleSTL));
    }


    public static class PointSTL {
        public static final String VERTEX = "\t\t\tvertex ";
        public final double x;
        public final double y;
        public final double z;

        public PointSTL(double x1, double y1, double z1) {
            this.x = x1;
            this.y = y1;
            this.z = z1;
        }

        public String toStringSTL() {
            return VERTEX + this;
        }

        @Override
        public String toString() {
            return x + " " + y + " " + z + "\n";
        }

    }

    public static class TriangleSTL {
        public static final String FACET_NORMAL = "\tfacet normal ";
        public static final String ENDFACET = "\tendfacet\n";
        public static final String OUTER_LOOP = "\t\touter loop\n";
        public static final String ENDLOOP = "\t\tendloop\n";
        public final PointSTL point1;
        public final PointSTL point2;
        public final PointSTL point3;
        public final PointSTL normale;


        public TriangleSTL(PointSTL p1, PointSTL p2, PointSTL p3) {

            this.point1 = p1;
            this.point2 = p2;
            this.point3 = p3;
            this.normale = calulerNormale();
        }


        @Override
        public String toString() {
            return FACET_NORMAL + normale
                    + OUTER_LOOP
                    + point1.toStringSTL()
                    + point2.toStringSTL()
                    + point3.toStringSTL()
                    + ENDLOOP
                    + ENDFACET;
        }

        private PointSTL calulerNormale() {
            //Premier Vecteur Point 2 - Point 1
            PointSTL vecteur1 = new PointSTL(this.point2.x - this.point1.x, this.point2.y - this.point1.y, this.point2.z - this.point1.z);
            //Deuxieme Vecteur Point 3 - Point 1
            PointSTL vecteur2 = new PointSTL(this.point3.x - this.point1.x, this.point3.y - this.point1.y, this.point3.z - this.point1.z);
            //Produit Vectoriel
            double x = vecteur1.y * vecteur2.z - vecteur1.z * vecteur2.y;
            double y = vecteur1.x * vecteur2.z - vecteur1.z * vecteur2.x;
            double z = vecteur1.x * vecteur2.y - vecteur1.y * vecteur2.x;
            float magnitude = (float) Math.sqrt(x * x + y * y + z * z);
            //On retourne le vecteur normale unitaire
            return new PointSTL(x / magnitude, y / magnitude, z / magnitude);
        }

        private PointSTL getNormale() {
            return this.normale;
        }

    }

    public static class RectangleSTL {

        public final TriangleSTL triangle1;
        public final TriangleSTL triangle2;
        public final PointSTL normale;

        PointSTL[] listesPointsSTL;

        /**
         *
         */
        public RectangleSTL(PointSTL p1, PointSTL p2, PointSTL p3, PointSTL p4) {
            listesPointsSTL = new PointSTL[]{p1, p2, p3, p4};
            this.triangle1 = new TriangleSTL(listesPointsSTL[0], listesPointsSTL[1], listesPointsSTL[3]);
            this.triangle2 = new TriangleSTL(listesPointsSTL[1], listesPointsSTL[2], listesPointsSTL[3]);
            this.normale = this.triangle1.normale;
        }

        @Override
        public String toString() {
            return "" + triangle1 + triangle2;
        }
    }


}