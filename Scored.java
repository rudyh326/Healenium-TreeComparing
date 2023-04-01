package com.epam.healenium.treecomparing;

public class Scored<T> {
    private final double score;
    private final T value;

    public double getScore() {
        return this.score;
    }

    public T getValue() {
        return this.value;
    }

    public Scored(double score, T value) {
        this.score = score;
        this.value = value;
    }

    public String toString() {
        return "Scored(score=" + this.getScore() + ", value=" + this.getValue() + ")";
    }
}