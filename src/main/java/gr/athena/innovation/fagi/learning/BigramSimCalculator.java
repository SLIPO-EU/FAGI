package gr.athena.innovation.fagi.learning;

import java.util.ArrayList;
import java.util.List;

public class BigramSimCalculator {

    public static double calcBigramSim(String s1, String s2) {

        if (s1.length() > 1 && s2.length() > 1) {
            List<char[]> b1 = bigram(s1);
            List<char[]> b2 = bigram(s2);
            return bigramSimScore(b1, b2);
        } else if (s1.length() == 1 && s2.length() == 1) {
            if (s2.contains(s1)) {
                return (1);
            } else {
                return (0);
            }
        } else if (s1.length() == 1 && s2.length() > 1) {
            if (s2.contains(s1)) {
                return (1 / (s2.length() + 1));
            } else {
                return (0);
            }
        } else if (s1.contains(s2)) {
            return (1 / (s1.length() + 1));
        } else {
            return (0);
        }

    }

    private static List<char[]> bigram(String input) {
        ArrayList<char[]> bigram = new ArrayList<>();
        for (int i = 0; i < input.length() - 1; i++) {
            char[] chars = new char[2];
            chars[0] = input.charAt(i);
            chars[1] = input.charAt(i + 1);
            bigram.add(chars);
        }
        return bigram;
    }

    private static double bigramSimScore(List<char[]> bigram1, List<char[]> bigram2) {
        List<char[]> copy = new ArrayList<>(bigram2);
        int matches = 0;
        for (int i = bigram1.size(); --i >= 0;) {
            char[] bigram = bigram1.get(i);
            for (int j = copy.size(); --j >= 0;) {
                char[] toMatch = copy.get(j);
                if (bigram[0] == toMatch[0] && bigram[1] == toMatch[1]) {
                    copy.remove(j);
                    matches += 2;
                    break;
                }
            }
        }
        return (double) matches / (bigram1.size() + bigram2.size());
    }

}
