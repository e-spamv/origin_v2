package de.claved.origin.utils;

import lombok.Getter;

@Getter
public class Pair<A, B> {

    private final A a;
    private final B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public boolean equals(Pair pair) {
        return getA().equals(pair.getA()) && getB().equals(pair.getB());
    }
}
