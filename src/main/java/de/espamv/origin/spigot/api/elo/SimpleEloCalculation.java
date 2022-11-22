package de.claved.origin.spigot.api.elo;

import de.claved.origin.utils.Pair;
import lombok.Getter;

@Getter
public class SimpleEloCalculation {

    private final int a;
    private final int b;

    private final double cA;
    private final double cB;

    public SimpleEloCalculation(int a, int b) {
        this.a = a;
        this.b = b;

        this.cA = calculateEloForA();
        this.cB = calculateEloForB();
    }

    /*Function for calculating the new elo value by the initial value of A.*/
    private double calculateEloForA() {
        double potency = (b - a) / 400.0;
        double nominal = 1.0 + Math.pow(10, potency);
        return Math.round((1 / nominal) * 1000.0) / 1000.0;
    }

    /*Function for calculating the new elo value by the initial value of B.*/
    private double calculateEloForB() {
        double potency = (a - b) / 400.0;
        double nominal = 1.0 + Math.pow(10, potency);
        return Math.round((1 / nominal) * 1000.0) / 1000.0;
    }

    /*Element A of the pair returns the new elo value of the player with higher elo. (loser)
    Element B of the pair returns the new elo value of the player with lower elo. (winner)*/
    public Pair<Integer, Integer> lowerEloWinner() {
        double rA = a + 10 * (-cA);
        double rB = b + 10 * (1 - cB);
        return new Pair<>((int) Math.round(rA), (int) Math.round(rB));
    }

    /*Element A of the pair returns the new elo value of the player with higher elo. (winner)
    Element B of the pair returns the new elo value of the player with lower elo. (loser)*/
    public Pair<Integer, Integer> higherEloWinner() {
        double rA = a + 10 * (1 - cA);
        double rB = b + 10 * (-cB);
        return new Pair<>((int) Math.round(rA), (int) Math.round(rB));
    }

    /*Element A of the pair returns the new elo value of the player with higher elo.
    Element B of the pair returns the new elo value of the player with lower elo.*/
    public Pair<Integer, Integer> draw() {
        double rA = a + 10 * (0.5 - cA);
        double rB = b + 10 * (0.5 - cB);
        return new Pair<>((int) Math.round(rA), (int) Math.round(rB));
    }
}
