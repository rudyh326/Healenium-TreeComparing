package com.epam.healenium.treecomparing;

@FunctionalInterface
public interface PathDistance {
    int distance(Path var1, Path var2);
}