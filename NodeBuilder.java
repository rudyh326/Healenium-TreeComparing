package com.epam.healenium.treecomparing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NodeBuilder {
    private static final Set<String> MAIN_ATTRIBUTED = new HashSet(Arrays.asList("id", "class"));
    private String tag;
    private String id = "";
    private Set<String> classes = Collections.emptySet();
    private Integer index = 0;
    private Map<String, String> otherAttributes = Collections.emptyMap();
    private List<String> content = new ArrayList();
    private List<Node> children = new ArrayList();

    public NodeBuilder() {
    }

    public NodeBuilder setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public NodeBuilder setIndex(int index) {
        this.index = index;
        return this;
    }

    public NodeBuilder setAttributes(Map<String, String> attributes) {
        this.id = (String)attributes.getOrDefault("id", "");
        this.classes = (Set)((Stream)Optional.ofNullable((String)attributes.get("class")).map((s) -> {
            return s.split(" ");
        }).map(Arrays::stream).orElse(Stream.empty())).map(String::trim).filter((next) -> {
            return !next.isEmpty();
        }).collect(Collectors.toSet());
        this.otherAttributes = (Map)attributes.entrySet().stream().filter((s) -> {
            return !MAIN_ATTRIBUTED.contains(s.getKey()) && !((String)s.getKey()).trim().isEmpty();
        }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        return this;
    }

    public NodeBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public NodeBuilder setClasses(Set<String> classes) {
        this.classes = classes;
        return this;
    }

    public NodeBuilder setOtherAttributes(Map<String, String> otherAttributes) {
        this.otherAttributes = otherAttributes;
        return this;
    }

    public NodeBuilder addChild(Node child) {
        this.children.add(child);
        return this;
    }

    public NodeBuilder addChildren(List<Node> children) {
        this.children.addAll(children);
        return this;
    }

    public NodeBuilder setChildren(List<Node> children) {
        this.children = children;
        return this;
    }

    public NodeBuilder addContent(String content) {
        this.content.add(content);
        return this;
    }

    public NodeBuilder setContent(List<String> content) {
        this.content = content;
        return this;
    }

    public NodeBuilder copy() {
        NodeBuilder copy = new NodeBuilder();
        copy.tag = this.tag;
        copy.id = this.id;
        copy.classes = new HashSet(this.classes);
        copy.index = this.index;
        copy.otherAttributes = new HashMap(this.otherAttributes);
        copy.content = new ArrayList(this.content);
        copy.children = new ArrayList(this.children);
        return copy;
    }

    public Node build() {
        String fullContent = String.join(System.lineSeparator(), this.content);
        Node node = new Node(this.tag, this.id, this.classes, this.index, this.otherAttributes, this.children, fullContent);
        Iterator var3 = node.getChildren().iterator();

        while(var3.hasNext()) {
            Node child = (Node)var3.next();
            child.setParent(node);
        }

        return node;
    }
}
