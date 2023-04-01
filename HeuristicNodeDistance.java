package com.epam.healenium.treecomparing;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class HeuristicNodeDistance implements NodeDistance {
    private static final double POINTS_FOR_TAG = 100.0D;
    private static final double POINTS_FOR_LCS = 100.0D;
    private static final double POINTS_FOR_ID = 50.0D;
    private static final double POINTS_FOR_CLASS = 40.0D;
    private static final double POINTS_FOR_VALUE = 30.0D;
    private static final double POINTS_FOR_INDEX = 0.0D;
    private static final double POINTS_FOR_OTHER_ATTRIBUTE = 30.0D;

    public HeuristicNodeDistance() {
    }

    public double distance(Node node1, Node node2, int LCSDistance, int curPathHeight) {
        double score = 0.0D;
        if (curPathHeight != 0 && (curPathHeight <= 5 || !((double)LCSDistance / (double)curPathHeight < 0.7D))) {
            score += (double)LCSDistance / (double)curPathHeight * 100.0D;
            Set<String> propertyNames = Utils.union(node1.getOtherAttributes().keySet(), node2.getOtherAttributes().keySet());
            Set<String> classNames = Utils.union(node1.getClasses(), node2.getClasses());
            double maximumScore = 350.0D;
            if (StringUtils.equalsIgnoreCase(node1.getTag(), node2.getTag())) {
                score += 100.0D;
            }

            if (Objects.equals(node1.getIndex(), node2.getIndex())) {
                score += 0.0D;
            }

            if (node1.getId() != null && node2.getId() != null) {
                score += 50.0D * this.calculateLevenshteinScore(node1.getId(), node2.getId(), 0.3D);
            }

            score += 30.0D * this.calculateLevenshteinScore(node1.getInnerText(), node2.getInnerText(), 0.3D);
            Set<String> classesIntersect = Utils.intersect(node1.getClasses(), node2.getClasses());
            double intersectScore = (double)classesIntersect.size() * 40.0D;
            if (classNames.size() > 0) {
                intersectScore /= (double)classNames.size();
                score += intersectScore;
            } else {
                score += 40.0D;
            }

            Set<String> node1classesDifference = Utils.difference(node1.getClasses(), node2.getClasses());
            Set<String> node2classesDifference = Utils.difference(node2.getClasses(), node1.getClasses());
            int lengthDifference = Utils.union(node1classesDifference, node2classesDifference).size();
            double otherAttributesScore;
            if (lengthDifference > 0) {
                otherAttributesScore = 0.0D;
                if (node1classesDifference.size() > 0) {
                    otherAttributesScore = this.calculateClassesIntersectionByLevenshtein(node1classesDifference, node2.getClasses());
                } else {
                    otherAttributesScore = this.calculateClassesIntersectionByLevenshtein(node1.getClasses(), node2classesDifference);
                }

                if (classNames.size() > 0) {
                    otherAttributesScore /= (double)classNames.size();
                }

                score += (double)lengthDifference * 40.0D * otherAttributesScore;
            }

            otherAttributesScore = 0.0D;

            String propertyName;
            for(Iterator var19 = propertyNames.iterator(); var19.hasNext(); otherAttributesScore += 30.0D * this.calculateLevenshteinScore((String)node1.getOtherAttributes().get(propertyName), (String)node2.getOtherAttributes().get(propertyName), 0.75D)) {
                propertyName = (String)var19.next();
            }

            if (propertyNames.size() > 0) {
                otherAttributesScore /= (double)propertyNames.size();
                score += otherAttributesScore;
            } else {
                score += 30.0D;
            }

            return score / maximumScore;
        } else {
            return 0.0D;
        }
    }

    private double calculateClassesIntersectionByLevenshtein(Set<String> nodeClasses1, Set<String> nodeClasses2) {
        int comparisonsNumber = 0;
        double scores = 0.0D;
        Iterator var6 = nodeClasses1.iterator();

        while(var6.hasNext()) {
            String classNameFirst = (String)var6.next();

            for(Iterator var8 = nodeClasses2.iterator(); var8.hasNext(); ++comparisonsNumber) {
                String classNameSecond = (String)var8.next();
                scores += this.calculateLevenshteinScore(classNameFirst, classNameSecond, 0.75D);
            }
        }

        if (comparisonsNumber == 0) {
            return 0.0D;
        } else {
            return scores / (double)comparisonsNumber;
        }
    }

    private double calculateLevenshteinScore(String innerText1, String innerText2, double thresholdPercent) {
        if (innerText1 != null && innerText2 != null) {
            innerText1 = innerText1.toLowerCase();
            innerText2 = innerText2.toLowerCase();
            int length = Math.max(innerText1.length(), innerText2.length());
            if (length == 0) {
                return 1.0D;
            } else {
                int threshold = this.calculateLevenshteinThreshold(length, thresholdPercent);
                LevenshteinDistance levenshtein = new LevenshteinDistance(threshold);
                Integer distance = levenshtein.apply(innerText1, innerText2);
                return distance < 0 ? 0.0D : ((double)length - distance.doubleValue()) / (double)length;
            }
        } else {
            return 0.0D;
        }
    }

    private int calculateLevenshteinThreshold(int maxTextLength, double thresholdPercent) {
        return (int)((double)maxTextLength * thresholdPercent + 1.0D);
    }
}
