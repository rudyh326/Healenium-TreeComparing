package com.epam.healenium.treecomparing;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

public class JsoupHTMLParser implements DocumentParser {
    public JsoupHTMLParser() {
    }

    public Node parse(InputStream inputStream) {
        try {
            Document document = Jsoup.parse(inputStream, "UTF-8", "/").normalise();
            org.jsoup.nodes.Node html = this.findHtml(Collections.singletonList(document.root()));
            int startIndex = 0;
            Deque<NodeBuilder> treeDepth = new ArrayDeque();
            return this.traverse(html, Integer.valueOf(startIndex), treeDepth);
        } catch (IOException var6) {
            throw new IllegalStateException(var6);
        }
    }

    private Node traverse(org.jsoup.nodes.Node node, Integer index, Deque<NodeBuilder> treeDepth) {
        Map<String, String> attributesMap = (Map)node.attributes().asList().stream().collect(Collectors.toMap(Attribute::getKey, Attribute::getValue));
        NodeBuilder builder = (new NodeBuilder()).setTag(node.nodeName()).setIndex(index).setAttributes(attributesMap);
        treeDepth.push(builder);
        int indexCounter = 0;
        Iterator var7 = node.childNodes().iterator();

        while(true) {
            while(var7.hasNext()) {
                org.jsoup.nodes.Node child = (org.jsoup.nodes.Node)var7.next();
                if (child instanceof Element) {
                    builder.addChild(this.traverse(child, indexCounter++, treeDepth));
                } else if (child instanceof TextNode) {
                    String text = ((TextNode)child).text();
                    Iterator var10 = treeDepth.iterator();

                    while(var10.hasNext()) {
                        NodeBuilder parentBuilder = (NodeBuilder)var10.next();
                        parentBuilder.addContent(text);
                    }
                }
            }

            treeDepth.pop();
            return builder.build();
        }
    }

    private org.jsoup.nodes.Node findHtml(List<org.jsoup.nodes.Node> nodes) {
        Iterator var2 = nodes.iterator();

        while(var2.hasNext()) {
            org.jsoup.nodes.Node node = (org.jsoup.nodes.Node)var2.next();
            if (node.nodeName().equals("html")) {
                return node;
            }
        }

        List<org.jsoup.nodes.Node> newNodes = new LinkedList();
        Iterator var6 = nodes.iterator();

        while(var6.hasNext()) {
            org.jsoup.nodes.Node node = (org.jsoup.nodes.Node)var6.next();
            newNodes.addAll(node.childNodes());
        }

        return this.findHtml(newNodes);
    }
}
