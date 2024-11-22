package ca.ulaval.glo2004.services;

import ca.ulaval.glo2004.services.CaisseOutils.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UniteImperiale implements Serializable {


    public static final Pattern FORMAT_UNITE_IMPERIAL = Pattern.compile("^((\\d+)'\\s*?)?" + "((\\d+)\"\\s*?)?" + "((\\d+)/([0-9]*))?$");
    public static final int PRECISION = 4;
    private static final int PIEDS_POS = 2;
    private static final int POUCES_POS = 4;
    private static final int NUMERATEUR_POS = 6;
    private static final int DENOMINATEUR_POS = 7;
    private int pieds;
    private int pouce;
    private int numerateur;
    private int denominateur;
    private boolean validiteDeValeur;


    /**
     * Convertir une valeur entrée sous forme de fraction vers un float tel que:
     * (12' 20" 33/64 ).
     */
    public UniteImperiale(String valeurEnImperial) {
        parse(valeurEnImperial);
    }

    public UniteImperiale(int p_pieds, int p_pouce, int p_numerateur, int p_denominateur) {
        parse(p_pieds + "\'" + p_pouce + "\"" + p_numerateur + "/" + p_denominateur);

    }


    private void parse(String valeurEnImperial) {
        Matcher matcher = FORMAT_UNITE_IMPERIAL.matcher(valeurEnImperial);
        if (matcher.find()) {
            pieds = matcher.group(PIEDS_POS) != null ? Integer.parseInt(matcher.group(PIEDS_POS)) : 0;
            pouce = matcher.group(POUCES_POS) != null ? Integer.parseInt(matcher.group(POUCES_POS)) : 0;
            numerateur = matcher.group(NUMERATEUR_POS) != null ? Integer.parseInt(matcher.group(NUMERATEUR_POS)) : 0;
            denominateur = matcher.group(DENOMINATEUR_POS) != null ? Integer.parseInt(matcher.group(DENOMINATEUR_POS)) : 1;
            validiteDeValeur = denominateur > 0;
            optimisation();
        } else
            validiteDeValeur = false;
    }

    private void optimisation() {
        if (validiteDeValeur) {
            // optimise la fracton
            int numeratorTemp = numerateur;
            numerateur = numerateur < denominateur ? numerateur : numerateur % denominateur;
            pouce += numeratorTemp >= denominateur ? (int) Math.floor(numeratorTemp / (float) denominateur) : 0;

            // optimise le pouce et le pieds
            int pouceTemp = pouce;
            pouce = pouce < 12 ? pouce : pouce % 12;
            pieds += pouceTemp >= 12 ? (int) Math.floor(pouceTemp / (float) 12) : 0;
            int pgcd = pgcd(numerateur, denominateur);
            numerateur = numerateur / pgcd;
            denominateur = denominateur / pgcd;
        } else {
            pieds = 0;
            pouce = 0;
            numerateur = 0;
            denominateur = 1;
        }
    }

    public static UniteImperiale decimalToUnit(double valeur){
        // Convert the decimal to a BigDecimal for precision
        BigDecimal bd = BigDecimal.valueOf(valeur);

        // Get the scale (number of decimal places)
        int scale = bd.scale();

        // Create a fraction with the decimal as numerator and 1 as denominator
        Fraction fraction = new Fraction(bd.unscaledValue(), BigInteger.TEN.pow(scale));

        // Simplify the fraction
        fraction = fraction.simplify();

        return new UniteImperiale(0,0,fraction.numerator.intValue(),fraction.denominator.intValue());
    }
    private static class Fraction {
        private BigInteger numerator;
        private BigInteger denominator;

        public Fraction(BigInteger numerator, BigInteger denominator) {
            this.numerator = numerator;
            this.denominator = denominator;
        }

        public BigInteger getNumerator() {
            return numerator;
        }

        public BigInteger getDenominator() {
            return denominator;
        }

        public Fraction simplify() {
            BigInteger gcd = numerator.gcd(denominator);
            return new Fraction(numerator.divide(gcd), denominator.divide(gcd));
        }

        @Override
        public String toString() {
            return numerator + "/" + denominator;
        }
    }
    private int pgcd(int numerateur, int denominateur) {
        return denominateur == 0 ? numerateur : pgcd(denominateur, numerateur % denominateur);
    }

    public static float arrondir(float p_valeurBrut) {
        BigDecimal bigDecimal = new BigDecimal(p_valeurBrut);
        return bigDecimal.setScale(PRECISION, RoundingMode.HALF_UP).floatValue();
    }

    private Tuple<Integer, Integer> toFraction() {
        int pouceTotal = (pieds * 12) + pouce;
        Tuple<Integer, Integer> fraction = new Tuple<>((pouceTotal * denominateur) + numerateur, denominateur);
        return fraction;
    }

    public UniteImperiale additionner(UniteImperiale p_valeur) {
        if (validiteDeValeur) {
            Tuple<Integer, Integer> fraction = toFraction();
            Tuple<Integer, Integer> fractionP_valeur = p_valeur.toFraction();

            int numerateurTemp = fraction.x * fractionP_valeur.y + fractionP_valeur.x * fraction.y;
            int denominateurTemp = fractionP_valeur.y * fraction.y;

            return new UniteImperiale(0, 0, numerateurTemp, denominateurTemp);
        }
        return new UniteImperiale(0, 0, 0, 1);
    }

    public UniteImperiale soustraire(UniteImperiale p_valeur) {
        if (validiteDeValeur) {
            Tuple<Integer, Integer> fraction = toFraction();
            Tuple<Integer, Integer> fractionP_valeur = p_valeur.toFraction();

            int numerateurTemp = fraction.x * fractionP_valeur.y - fractionP_valeur.x * fraction.y;
            int denominateurTemp = fractionP_valeur.y * fraction.y;

            return new UniteImperiale(0, 0, numerateurTemp < 0 ? numerateurTemp * -1 : numerateurTemp, denominateurTemp);
        }
        return new UniteImperiale(0, 0, 0, 1);
    }

    public UniteImperiale multiplierPar(UniteImperiale p_valeur) {
        if (validiteDeValeur) {
            Tuple<Integer, Integer> fraction = toFraction();
            Tuple<Integer, Integer> fractionP_valeur = p_valeur.toFraction();

            int numerateurTemp = fraction.x * fractionP_valeur.x;
            int denominateurTemp = fraction.y * fractionP_valeur.y;

            return new UniteImperiale(0, 0, numerateurTemp, denominateurTemp);
        }
        return new UniteImperiale(0, 0, 0, 1);
    }

    public UniteImperiale diviserPar(UniteImperiale p_valeur) {
        if (validiteDeValeur) {
            Tuple<Integer, Integer> fraction = toFraction();
            Tuple<Integer, Integer> fractionP_valeur = p_valeur.toFraction();

            int numerateurTemp = fraction.x * fractionP_valeur.y;
            int denominateurTemp = fraction.y * fractionP_valeur.x;

            return new UniteImperiale(0, 0, numerateurTemp, denominateurTemp);
        }
        return new UniteImperiale(0, 0, 0, 1);
    }


    public float pieds() {
        return arrondir(pieds + ((float) pouce + (float) numerateur / denominateur) / 12);
    }

    public static float pieds(float p_pouces) {
        return arrondir(p_pouces / 12);
    }

    public float pouces() {
        return arrondir(pieds * 12 + (float) pouce + (float) numerateur / denominateur);
    }

    public static float pouces(float p_pieds) {
        return arrondir(p_pieds * 12);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Object clone = super.clone();
        return new UniteImperiale(pieds, pouce, numerateur, denominateur);
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof UniteImperiale) {
            UniteImperiale imperiale = (UniteImperiale) o;
            return pieds == imperiale.pieds && pouce == imperiale.pouce && numerateur == imperiale.numerateur && denominateur == imperiale.denominateur;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieds, pouce, numerateur, denominateur);
    }

    public boolean estValide() {
        return validiteDeValeur;
    }

    public boolean isValeurEgaleZero() {
        return pieds == 0 && pouce == 0 && numerateur == 0;
    }

    public String toString() {
        String valeurString = "Valeur Invalide";

        if (estValide()) {

            valeurString = pieds > 0 ? pieds + "\' " : "";
            valeurString += pouce > 0 ? pouce + "\" " : "";
            valeurString += numerateur > 0 ? numerateur + "/" + denominateur : "";
            valeurString += pieds == 0 && pouce == 0 && numerateur > 0 ? "\"" : "";
        }

        return valeurString;
    }

}