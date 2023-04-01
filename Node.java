package com.epam.healenium.treecomparing;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

public class Node {
    private final String tag;
    private final String id;
    private final Set<String> classes;
    private final Integer index;
    private final Map<String, String> otherAttributes;
    private final String innerText;
    private Node parent;
    private List<Node> children;

    Node(String tag, String id, Set<String> classes, Integer index, Map<String, String> otherAttributes, List<Node> children, String innerText) {
        this.tag = tag;
        this.id = id;
        this.classes = classes;
        this.index = index;
        this.otherAttributes = otherAttributes;
        this.children = children;
        this.innerText = innerText;
    }

    public String getTag() {
        return this.tag;
    }

    public String getId() {
        return this.id;
    }

    public Set<String> getClasses() {
        return this.classes;
    }

    public Integer getIndex() {
        return this.index;
    }

    public Map<String, String> getOtherAttributes() {
        return this.otherAttributes;
    }

    public String getInnerText() {
        return this.innerText;
    }

    public List<Node> getChildren() {
        return this.children;
    }

    public Node getParent() {
        return this.parent;
    }

    void setParent(Node parent) {
        this.parent = parent;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Node node = (Node)o;
            return Objects.equals(this.tag, node.tag) && Objects.equals(this.id, node.id);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.tag, this.id});
    }

    public String toString() {
        return (new StringJoiner(", ", Node.class.getSimpleName() + "[", "]")).add("tag='" + this.tag + "'").add("id='" + this.id + "'").add("classes=" + this.classes).add("index=" + this.index).add("innerText='" + this.innerText + "'").add("otherAttributes=" + this.otherAttributes).toString();
    }
}
