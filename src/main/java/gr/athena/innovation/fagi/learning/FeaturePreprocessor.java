package gr.athena.innovation.fagi.learning;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;

public class FeaturePreprocessor {

    private static double getSim(String left, String right) {
        return BigramSimCalculator.calcBigramSim(left.toUpperCase(), right.toUpperCase());
    }

    private static double getWordsDiff(String left, String right) {
        return left.split(" ").length - right.split(" ").length;
    }

    private static double getLengthDiff(String left, String right) {
        return left.length() - right.length();
    }

    private static int countBroken(String str) {
        String temp = str.replaceAll("[a-zA-ZäöüßÄÖÜα-ωΑ-Ω&\\-(){}\\s]", "");
        return temp.length();
    }

    private String stripPhone(String str) {
        return str.replaceAll("[^0-9]", "");
    }

    private static int getBroken(String left, String right) {
        return countBroken(left) - countBroken(right);
    }

    public static DenseInstance createNameInst(String left, String right) {

        ArrayList<Attribute> atts = createNameAtts();
        Instances insts = new Instances("name", atts, 0);
        insts.setClass(atts.get(atts.size() - 1));
        DenseInstance inst = new DenseInstance(atts.size());
        inst.setDataset(insts);
        inst.setValue(0, left.length());
        inst.setValue(1, right.length());
        inst.setValue(2, getLengthDiff(left, right));
        inst.setValue(3, getWordsDiff(left, right));
        inst.setValue(4, getBroken(left, right));
        inst.setValue(5, getSim(left, right));
        return inst;

    }

    public DenseInstance createStreetInst(String left, String right) {

        ArrayList<Attribute> atts = createStreetAtts();
        Instances insts = new Instances("street", atts, 0);
        insts.setClass(atts.get(atts.size() - 1));
        DenseInstance inst = new DenseInstance(atts.size());
        inst.setDataset(insts);
        inst.setValue(0, getBroken(left, right));
        inst.setValue(1, getSim(left, right));
        return inst;

    }

    public DenseInstance createEmailInst(String left, String right) {

        ArrayList<Attribute> atts = createEmailAtts();
        Instances insts = new Instances("email", atts, 0);
        insts.setClass(atts.get(atts.size() - 1));
        DenseInstance inst = new DenseInstance(atts.size());
        inst.setDataset(insts);
        inst.setValue(0, left.length());
        inst.setValue(1, right.length());
        inst.setValue(2, left.length() - right.length());
        inst.setValue(3, getSim(left, right));
        return inst;

    }

    public DenseInstance createWebInst(String left, String right) {

        ArrayList<Attribute> atts = createWebAtts();
        Instances insts = new Instances("web", atts, 0);
        insts.setClass(atts.get(atts.size() - 1));
        DenseInstance inst = new DenseInstance(atts.size());
        inst.setDataset(insts);
        inst.setValue(0, left.length());
        inst.setValue(1, right.length());
        inst.setValue(2, left.length() - right.length());
        inst.setValue(3, getSim(left, right));
        return inst;

    }

    public DenseInstance createTeleInst(String left, String right) {

        ArrayList<Attribute> atts = createTeleAtts();
        Instances insts = new Instances("tele", atts, 0);
        insts.setClass(atts.get(atts.size() - 1));
        left = stripPhone(left);
        right = stripPhone(right);
        DenseInstance inst = new DenseInstance(atts.size());
        inst.setDataset(insts);
        inst.setValue(0, left.length());
        inst.setValue(1, right.length());
        inst.setValue(2, left.length() - right.length());
        inst.setValue(3, getSim(left, right));
        return inst;

    }

    private ArrayList<Attribute> createTeleAtts() {
        ArrayList<Attribute> teleAtts = new ArrayList<>();
        teleAtts.add(new Attribute("lenR"));
        teleAtts.add(new Attribute("lenL"));
        teleAtts.add(new Attribute("lenDiff"));
        teleAtts.add(new Attribute("sim"));
        teleAtts.add(new Attribute("lab", Arrays.asList("0", "1", "2", "3")));
        return teleAtts;
    }

    private ArrayList<Attribute> createWebAtts() {
        ArrayList<Attribute> webAtts = new ArrayList<>();
        webAtts.add(new Attribute("lenR"));
        webAtts.add(new Attribute("lenL"));
        webAtts.add(new Attribute("lenDiff"));
        webAtts.add(new Attribute("sim"));
        webAtts.add(new Attribute("lab", Arrays.asList("0", "1", "2", "3")));
        return webAtts;
    }

    private ArrayList<Attribute> createEmailAtts() {
        ArrayList<Attribute> emailAtts = new ArrayList<>();
        emailAtts.add(new Attribute("lenR"));
        emailAtts.add(new Attribute("lenL"));
        emailAtts.add(new Attribute("lenDiff"));
        emailAtts.add(new Attribute("sim"));
        emailAtts.add(new Attribute("lab", Arrays.asList("0", "1", "2", "3")));
        return emailAtts;
    }

    private ArrayList<Attribute> createStreetAtts() {
        ArrayList<Attribute> streetAtts = new ArrayList<>();
        streetAtts.add(new Attribute("brChar"));
        streetAtts.add(new Attribute("sim"));
        streetAtts.add(new Attribute("lab", Arrays.asList("0", "1", "2", "3")));
        return streetAtts;
    }

    private static ArrayList<Attribute> createNameAtts() {
        ArrayList<Attribute> nameAtts = new ArrayList<>();
        nameAtts.add(new Attribute("lenL"));
        nameAtts.add(new Attribute("lenR"));
        nameAtts.add(new Attribute("lenDiff"));
        nameAtts.add(new Attribute("wordsDiff"));
        nameAtts.add(new Attribute("brChar"));
        nameAtts.add(new Attribute("sim"));
        nameAtts.add(new Attribute("lab", Arrays.asList("0", "1", "2", "3")));
        return nameAtts;
    }
}
