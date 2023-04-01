package com.epam.healenium.treecomparing;

import java.util.Arrays;

public class Path {
    private Node[] nodes;

    public Path(Node[] nodes) {
        this.nodes = nodes;
    }

    public Path(Node node) {
        this.nodes = new Node[]{node};
    }

    public Node[] getNodes() {
        return this.nodes;
    }

    public void setNodes(Node[] nodes) {
        this.nodes = nodes;
    }

    public Node getLastNode() throws ArrayIndexOutOfBoundsException {
        return this.nodes[this.nodes.length - 1];
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Path path = (Path)o;
            return Arrays.equals(this.nodes, path.nodes);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Arrays.hashCode(this.nodes);
    }

    public String toString() {
        return "Path{nodes=" + Arrays.toString(this.nodes) + '}';
    }
}
