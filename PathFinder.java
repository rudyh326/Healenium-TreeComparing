package com.epam.healenium.treecomparing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathFinder {
    private static final Logger log = LoggerFactory.getLogger(PathFinder.class);
    private final PathDistance pathDistance;
    private final NodeDistance nodeDistance;

    public PathFinder(PathDistance pathDistance, NodeDistance nodeDistance) {
        this.pathDistance = pathDistance;
        this.nodeDistance = nodeDistance;
    }

    public Node findNearest(Path path, Node newSource) {
        List<Scored<Node>> found = this.find(path, newSource, 1);
        return found.isEmpty() ? null : (Node)((Scored)found.get(0)).getValue();
    }

    public List<Scored<Node>> find(Path path, Node newSource, int bestGuessesCount) {
        return this.getSortedNodes((Map)this.findScoresToNodes(path, newSource).getValue(), bestGuessesCount, -1.0D);
    }

    public SimpleImmutableEntry<Integer, Map<Double, List<SimpleImmutableEntry<Node, Integer>>>> findScoresToNodes(Path path, Node newSource) {
        List<Path> destinationLeaves = this.findAllLeafPaths(newSource);
        Node byPath = path.getLastNode();
        int pathLength = path.getNodes().length;
        List<SimpleImmutableEntry<Path, Integer>> paths = new ArrayList();
        int maxLCSDistance = 0;
        Iterator var8 = destinationLeaves.iterator();

        while(var8.hasNext()) {
            Path destinationLeaf = (Path)var8.next();
            int distance = this.pathDistance.distance(path, destinationLeaf);
            if (distance >= 1) {
                maxLCSDistance = Math.max(maxLCSDistance, distance);
                paths.add(new SimpleImmutableEntry(destinationLeaf, distance));
            }
        }

        int pathLengthToCheck = Math.min(maxLCSDistance, pathLength);
        Map<Double, List<SimpleImmutableEntry<Node, Integer>>> scoresToNodes = (Map)paths.stream().map((pathPair) -> {
            return new SimpleImmutableEntry((Node[])Arrays.copyOfRange(((Path)pathPair.getKey()).getNodes(), (Integer)pathPair.getValue() - 1, ((Path)pathPair.getKey()).getNodes().length), (Integer)pathPair.getValue());
        }).flatMap((pathPair) -> {
            return Arrays.stream((Node[])pathPair.getKey()).map((it) -> {
                return new SimpleImmutableEntry(it, (Integer)pathPair.getValue());
            });
        }).collect(Collectors.groupingBy((nodePair) -> {
            return this.nodeDistance.distance(byPath, (Node)nodePair.getKey(), (Integer)nodePair.getValue(), pathLengthToCheck);
        }));
        return new SimpleImmutableEntry(pathLengthToCheck, scoresToNodes);
    }

    public List<Scored<Node>> getSortedNodes(Map<Double, List<SimpleImmutableEntry<Node, Integer>>> scoresToNodes, int bestGuessesCount, double guessCap) {
        int nodeLimit = this.normalizeLimit(bestGuessesCount);
        double scoreLimit = this.normalizeScoreCap(guessCap);
        return (List)scoresToNodes.keySet().stream().sorted(Comparator.reverseOrder()).filter(StreamUtils.logFiltered((score) -> {
            return score >= scoreLimit;
        }, (score) -> {
            log.debug("Skipping nodes, because their score={} less then {}", score, scoreLimit);
        })).flatMap((score) -> {
            return ((List)scoresToNodes.get(score)).stream().map((it) -> {
                return new Scored(score, (Node)it.getKey());
            });
        }).limit((long)nodeLimit).collect(Collectors.toList());
    }

    private List<Path> findAllLeafPaths(Node node) {
        List<Path> paths = new ArrayList();
        this.addLeafPath(paths, new Path(node));
        return paths;
    }

    private void addLeafPath(List<Path> leaves, Path current) {
        Deque<Path> paths = new ArrayDeque();
        paths.addFirst(current);

        while(true) {
            while(!paths.isEmpty()) {
                Path path = (Path)paths.removeLast();
                Node node = path.getLastNode();
                if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                    Iterator var6 = node.getChildren().iterator();

                    while(var6.hasNext()) {
                        Node child = (Node)var6.next();
                        paths.addFirst(Utils.addNode(path, child));
                    }
                } else {
                    leaves.add(path);
                }
            }

            return;
        }
    }

    private double normalizeScoreCap(double value) {
        if (value > 1.0D) {
            log.warn("Required min score value={} will be ignored, because exceed allowed value. It must be in range [0..1]", value);
            return -1.0D;
        } else {
            return value;
        }
    }

    private int normalizeLimit(int value) {
        if (value < 0) {
            log.warn("Desired number of result nodes={} will be reset to 1, because it must be positive", value);
            return 1;
        } else {
            return value;
        }
    }
}
