package com.epam.healenium.treecomparing;

@FunctionalInterface
public interface NodeDistance {
    double distance(Node var1, Node var2, int var3, int var4);
}