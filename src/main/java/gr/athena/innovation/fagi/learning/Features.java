package gr.athena.innovation.fagi.learning;

import gr.athena.innovation.fagi.core.function.literal.AbbreviationAndAcronymResolver;
import gr.athena.innovation.fagi.core.normalizer.AdvancedGenericNormalizer;
import gr.athena.innovation.fagi.core.normalizer.BasicGenericNormalizer;
import gr.athena.innovation.fagi.core.similarity.Cosine;
import gr.athena.innovation.fagi.core.similarity.Jaro;
import gr.athena.innovation.fagi.core.similarity.JaroWinkler;
import gr.athena.innovation.fagi.core.similarity.Levenshtein;
import gr.athena.innovation.fagi.core.similarity.LongestCommonSubsequenceMetric;
import gr.athena.innovation.fagi.core.similarity.NGram;
import gr.athena.innovation.fagi.core.similarity.SortedJaroWinkler;
import gr.athena.innovation.fagi.core.similarity.WeightedSimilarity;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.model.WeightedPairLiteral;
import gr.athena.innovation.fagi.core.function.phone.IsSamePhoneNumberCustomNormalize;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Class with the features.
 * 
 * @author Giorgos Kostoulas
 */
public class Features {

    private boolean AcontainsB;
    private boolean BcontainsA;

    private boolean AcontainsFT;
    private String AcontainsFT_T;
    private boolean AcontainsFTpos;
    private boolean BcontainsFT;
    private String BcontainsFT_T;
    private boolean BcontainsFTpos;

    private boolean AcontainsABR;
    private boolean BcontainsABR;

    private boolean AcontainsDash;
    private boolean BcontainsDash;

    private boolean AcontainsParenthesis;
    private boolean BcontainsParenthesis;

    private int NumWordsA;
    private int NumWordsB;

    private double levenshtein;
    private double ngram;
    private double cosine;
    private double longestCommonSubseq;
    private double jaro;
    private double jaroWinkler;
    private double sortedJaroWinkler;

    private double Blevenshtein;
    private double Bngram;
    private double Bcosine;
    private double BlongestCommonSubseq;
    private double Bjaro;
    private double BjaroWinkler;
    private double BsortedJaroWinkler;

    private double Dlevenshtein;
    private double Dngram;
    private double Dcosine;
    private double DlongestCommonSubseq;
    private double Djaro;
    private double DjaroWinkler;
    private double DsortedJaroWinkler;
    private boolean samePhone;
    private boolean sameAddress;
    private double sameAddressName;

    private boolean acceptance;
    private String nameFusionAction;

    /**
     * Set the features. 
     * 
     * @param idA the id of A (left).
     * @param idB the id of B (right).
     * @param propertyA the property of A (left).
     * @param propertyB the property of B (right).
     * @param frequentTermsA the frequent terms of A (left).
     * @param frequentTermsB the frequent terms of B( (right).
     * @param locale the locale.
     * @param acceptance the acceptance value as a string.
     * @param nameFusionAction the name of the fusion action.
     */
    public void setFeatures(String idA, String idB, String propertyA, String propertyB, String frequentTermsA, String frequentTermsB,
            Locale locale, String acceptance, String nameFusionAction) {

        NormalizedLiteral basicA = getBasicNormalization(propertyA, propertyB, locale);
        NormalizedLiteral basicB = getBasicNormalization(propertyB, propertyA, locale);

        WeightedPairLiteral normalizedPair = getAdvancedNormalization(basicA, basicB, locale);

        AbbreviationAndAcronymResolver abrIn = AbbreviationAndAcronymResolver.getInstance();

        // Set the metrics for case (a)
        this.setLevenshtein(Levenshtein.computeSimilarity(propertyA, propertyB, null));
        this.setNgram(NGram.computeSimilarity(propertyA, propertyB, 2));
        this.setCosine(Cosine.computeSimilarity(propertyA, propertyB));
        this.setLongestCommonSubseq(LongestCommonSubsequenceMetric.computeSimilarity(propertyA, propertyB));
        this.setJaro(Jaro.computeSimilarity(propertyA, propertyB));
        this.setJaroWinkler(JaroWinkler.computeSimilarity(propertyA, propertyB));
        this.setSortedJaroWinkler(SortedJaroWinkler.computeSimilarity(propertyA, propertyB));

        // Set the metrics for case (b)
        this.setBlevenshtein(WeightedSimilarity.computeBSimilarity(basicA, basicB, "levenshtein"));
        this.setBngram(WeightedSimilarity.computeBSimilarity(basicA, basicB, "2Gram"));
        this.setBcosine(WeightedSimilarity.computeBSimilarity(basicA, basicB, "cosine"));
        this.setBlongestCommonSubseq(WeightedSimilarity.computeBSimilarity(basicA, basicB, "longestcommonsubsequence"));
        this.setBjaro(WeightedSimilarity.computeBSimilarity(basicA, basicB, "jaro"));
        this.setBjaroWinkler(WeightedSimilarity.computeBSimilarity(basicA, basicB, "jarowinkler"));
        this.setBsortedJaroWinkler(WeightedSimilarity.computeBSimilarity(basicA, basicB, "sortedjarowinkler"));

        // Set the metrics for case (d)
        this.setDlevenshtein(WeightedSimilarity.computeDSimilarity(normalizedPair, "levenshtein"));
        this.setDngram(WeightedSimilarity.computeDSimilarity(normalizedPair, "2Gram"));
        this.setDcosine(WeightedSimilarity.computeDSimilarity(normalizedPair, "cosine"));
        this.setDlongestCommonSubseq(WeightedSimilarity.computeDSimilarity(normalizedPair, "longestcommonsubsequence"));
        this.setDjaro(WeightedSimilarity.computeDSimilarity(normalizedPair, "jaro"));
        this.setDjaroWinkler(WeightedSimilarity.computeDSimilarity(normalizedPair, "jarowinkler"));
        this.setDsortedJaroWinkler(WeightedSimilarity.computeDSimilarity(normalizedPair, "sortedjarowinkler"));

        this.setAcceptance(acceptance.equals("ACCEPT"));
        this.setNameFusionAction(nameFusionAction);
        String[] lA = basicA.getNormalized().split("\\s+");
        this.setNumWordsA(lA.length);
        String[] lB = basicB.getNormalized().split("\\s+");
        this.setNumWordsB(lB.length);

        //AcontainsB
        for (String li : lA) {
            if (!basicB.getNormalized().contains(li)) {
                this.setAcontainsB(false);
                break;
            }
            this.setAcontainsB(true);
        }
        //BcontainsA
        for (String li : lB) {
            if (!basicA.getNormalized().contains(" " + li + " ")) {
                this.setBcontainsA(false);
                break;
            }
            this.setBcontainsA(true);
        }
        //AcontainsFT
        float z = 1;
        for (String li : lA) {
            if (frequentTermsA.contains(" " + li + " ")) {
                this.setAcontainsFT(true);
                this.setAcontainsFT_T(li);
                this.setAcontainsFTpos((z / this.getNumWordsA()) > 0.5);
                break;
            }
            z = z + 1;
        }
        z = 1;
        // Bcontains Frequent term
        for (String li : lB) {
            if (frequentTermsB.contains(" " + li + " ")) {
                this.setBcontainsFT(true);
                this.setBcontainsFT_T(li);
                this.setBcontainsFTpos((z / this.getNumWordsB()) > 0.5);
                break;
            }
            z = z + 1;
        }
        this.setAcontainsABR(abrIn.containsAbbreviationOrAcronym(propertyA));
        this.setBcontainsABR(abrIn.containsAbbreviationOrAcronym(propertyB));
        this.setAcontainsDash(propertyA.split("-").length > 1);
        this.setBcontainsDash(propertyB.split("-").length > 1);
        this.setAcontainsParenthesis(propertyA.split("\\(|\\)").length > 1);
        this.setBcontainsParenthesis(propertyB.split("\\(|\\)").length > 1);

    }

    private NormalizedLiteral getBasicNormalization(String literalA, String literalB, Locale locale) {
        BasicGenericNormalizer bgn = new BasicGenericNormalizer();
        NormalizedLiteral normalizedLiteral = bgn.getNormalizedLiteral(literalA, literalB, locale);
        return normalizedLiteral;
    }

    private WeightedPairLiteral getAdvancedNormalization(NormalizedLiteral normA, NormalizedLiteral normB, Locale locale) {
        AdvancedGenericNormalizer agn = new AdvancedGenericNormalizer();
        WeightedPairLiteral weightedPairLiteral = agn.getWeightedPair(normA, normB, locale);
        return weightedPairLiteral;
    }

    /**
     * Set the phone features.
     * 
     * @param a value for phone a.
     * @param b value for phone b.
     */
    public void setphoneFeatures(String a, String b) {
        IsSamePhoneNumberCustomNormalize ph = new IsSamePhoneNumberCustomNormalize();
        Literal literalA = ResourceFactory.createStringLiteral(a);
        Literal literalB = ResourceFactory.createStringLiteral(b);
        setSamePhone(ph.evaluate(literalA, literalB));
    }

    /**
     * Set the address features.
     * 
     * @param a value for address a.
     * @param b value for address b.
     */
    public void setaddrFeature(String a, String b) {
        if (StringUtils.isBlank(a) || StringUtils.isBlank(b)) {
            setSameAddress(false);
        } else if (a.equals(b)) {
            setSameAddress(true);
        }
    }

    /**
     * Set address name feature as the levenstein similarity between the address values.
     * 
     * @param a the value of address a.
     * @param b the value of address b.
     */
    public void setaddrNameFeature(String a, String b) {
        if (StringUtils.isBlank(a) || StringUtils.isBlank(b)) {
            setSameAddressName(0);
        } else {
            setSameAddressName(Levenshtein.computeSimilarity(a, b, null));
        }
    }

    @Override
    public String toString() {
        return isAcontainsB() + "," + isBcontainsA() + "," + isAcontainsFT() + "," + getAcontainsFT_T() + ","
                + isAcontainsFTpos() + "," + isBcontainsFT() + "," + getBcontainsFT_T() + "," + isBcontainsFTpos()
                + "," + isAcontainsABR() + "," + isBcontainsABR() + "," + isAcontainsDash() + "," + isBcontainsDash()
                + "," + isAcontainsParenthesis() + "," + isBcontainsParenthesis() + "," + getNumWordsA() + ","
                + getNumWordsB() + "," + getLevenshtein() + "," + getNgram() + "," + getCosine() + ","
                + getLongestCommonSubseq() + "," + getJaro() + "," + getJaroWinkler() + "," + getSortedJaroWinkler()
                + "," + getBlevenshtein() + "," + getBngram() + "," + getBcosine() + "," + getBlongestCommonSubseq()
                + "," + getBjaro() + "," + getBjaroWinkler() + "," + getBsortedJaroWinkler() + "," + getDlevenshtein()
                + "," + getDngram() + "," + getDcosine() + "," + getDlongestCommonSubseq() + "," + getDjaro() + ","
                + getDjaroWinkler() + "," + getDsortedJaroWinkler() + "," + isSamePhone() + "," + isSameAddress() + ","
                + getSameAddressName() + "," + isAcceptance() + "," + getNameFusionAction();
    }

    /**
     * @return the AcontainsB
     */
    public boolean isAcontainsB() {
        return AcontainsB;
    }

    /**
     * @param AcontainsB the AcontainsB to set
     */
    public void setAcontainsB(boolean AcontainsB) {
        this.AcontainsB = AcontainsB;
    }

    /**
     * @return the BcontainsA
     */
    public boolean isBcontainsA() {
        return BcontainsA;
    }

    /**
     * @param BcontainsA the BcontainsA to set
     */
    public void setBcontainsA(boolean BcontainsA) {
        this.BcontainsA = BcontainsA;
    }

    /**
     * @return the AcontainsFT
     */
    public boolean isAcontainsFT() {
        return AcontainsFT;
    }

    /**
     * @param AcontainsFT the AcontainsFT to set
     */
    public void setAcontainsFT(boolean AcontainsFT) {
        this.AcontainsFT = AcontainsFT;
    }

    /**
     * @return the AcontainsFT_T
     */
    public String getAcontainsFT_T() {
        return AcontainsFT_T;
    }

    /**
     * @param AcontainsFT_T the AcontainsFT_T to set
     */
    public void setAcontainsFT_T(String AcontainsFT_T) {
        this.AcontainsFT_T = AcontainsFT_T;
    }

    /**
     * @return the AcontainsFTpos
     */
    public boolean isAcontainsFTpos() {
        return AcontainsFTpos;
    }

    /**
     * @param AcontainsFTpos the AcontainsFTpos to set
     */
    public void setAcontainsFTpos(boolean AcontainsFTpos) {
        this.AcontainsFTpos = AcontainsFTpos;
    }

    /**
     * @return the BcontainsFT
     */
    public boolean isBcontainsFT() {
        return BcontainsFT;
    }

    /**
     * @param BcontainsFT the BcontainsFT to set
     */
    public void setBcontainsFT(boolean BcontainsFT) {
        this.BcontainsFT = BcontainsFT;
    }

    /**
     * @return the BcontainsFT_T
     */
    public String getBcontainsFT_T() {
        return BcontainsFT_T;
    }

    /**
     * @param BcontainsFT_T the BcontainsFT_T to set
     */
    public void setBcontainsFT_T(String BcontainsFT_T) {
        this.BcontainsFT_T = BcontainsFT_T;
    }

    /**
     * @return the BcontainsFTpos
     */
    public boolean isBcontainsFTpos() {
        return BcontainsFTpos;
    }

    /**
     * @param BcontainsFTpos the BcontainsFTpos to set
     */
    public void setBcontainsFTpos(boolean BcontainsFTpos) {
        this.BcontainsFTpos = BcontainsFTpos;
    }

    /**
     * @return the AcontainsABR
     */
    public boolean isAcontainsABR() {
        return AcontainsABR;
    }

    /**
     * @param AcontainsABR the AcontainsABR to set
     */
    public void setAcontainsABR(boolean AcontainsABR) {
        this.AcontainsABR = AcontainsABR;
    }

    /**
     * @return the BcontainsABR
     */
    public boolean isBcontainsABR() {
        return BcontainsABR;
    }

    /**
     * @param BcontainsABR the BcontainsABR to set
     */
    public void setBcontainsABR(boolean BcontainsABR) {
        this.BcontainsABR = BcontainsABR;
    }

    /**
     * @return the AcontainsDash
     */
    public boolean isAcontainsDash() {
        return AcontainsDash;
    }

    /**
     * @param AcontainsDash the AcontainsDash to set
     */
    public void setAcontainsDash(boolean AcontainsDash) {
        this.AcontainsDash = AcontainsDash;
    }

    /**
     * @return the BcontainsDash
     */
    public boolean isBcontainsDash() {
        return BcontainsDash;
    }

    /**
     * @param BcontainsDash the BcontainsDash to set
     */
    public void setBcontainsDash(boolean BcontainsDash) {
        this.BcontainsDash = BcontainsDash;
    }

    /**
     * @return the AcontainsParenthesis
     */
    public boolean isAcontainsParenthesis() {
        return AcontainsParenthesis;
    }

    /**
     * @param AcontainsParenthesis the AcontainsParenthesis to set
     */
    public void setAcontainsParenthesis(boolean AcontainsParenthesis) {
        this.AcontainsParenthesis = AcontainsParenthesis;
    }

    /**
     * @return the BcontainsParenthesis
     */
    public boolean isBcontainsParenthesis() {
        return BcontainsParenthesis;
    }

    /**
     * @param BcontainsParenthesis the BcontainsParenthesis to set
     */
    public void setBcontainsParenthesis(boolean BcontainsParenthesis) {
        this.BcontainsParenthesis = BcontainsParenthesis;
    }

    /**
     * @return the NumWordsA
     */
    public int getNumWordsA() {
        return NumWordsA;
    }

    /**
     * @param NumWordsA the NumWordsA to set
     */
    public void setNumWordsA(int NumWordsA) {
        this.NumWordsA = NumWordsA;
    }

    /**
     * @return the NumWordsB
     */
    public int getNumWordsB() {
        return NumWordsB;
    }

    /**
     * @param NumWordsB the NumWordsB to set
     */
    public void setNumWordsB(int NumWordsB) {
        this.NumWordsB = NumWordsB;
    }

    /**
     * @return the levenshtein
     */
    public double getLevenshtein() {
        return levenshtein;
    }

    /**
     * @param levenshtein the levenshtein to set
     */
    public void setLevenshtein(double levenshtein) {
        this.levenshtein = levenshtein;
    }

    /**
     * @return the ngram
     */
    public double getNgram() {
        return ngram;
    }

    /**
     * @param ngram the ngram to set
     */
    public void setNgram(double ngram) {
        this.ngram = ngram;
    }

    /**
     * @return the cosine
     */
    public double getCosine() {
        return cosine;
    }

    /**
     * @param cosine the cosine to set
     */
    public void setCosine(double cosine) {
        this.cosine = cosine;
    }

    /**
     * @return the longestCommonSubseq
     */
    public double getLongestCommonSubseq() {
        return longestCommonSubseq;
    }

    /**
     * @param longestCommonSubseq the longestCommonSubseq to set
     */
    public void setLongestCommonSubseq(double longestCommonSubseq) {
        this.longestCommonSubseq = longestCommonSubseq;
    }

    /**
     * @return the jaro
     */
    public double getJaro() {
        return jaro;
    }

    /**
     * @param jaro the jaro to set
     */
    public void setJaro(double jaro) {
        this.jaro = jaro;
    }

    /**
     * @return the jaroWinkler
     */
    public double getJaroWinkler() {
        return jaroWinkler;
    }

    /**
     * @param jaroWinkler the jaroWinkler to set
     */
    public void setJaroWinkler(double jaroWinkler) {
        this.jaroWinkler = jaroWinkler;
    }

    /**
     * @return the sortedJaroWinkler
     */
    public double getSortedJaroWinkler() {
        return sortedJaroWinkler;
    }

    /**
     * @param sortedJaroWinkler the sortedJaroWinkler to set
     */
    public void setSortedJaroWinkler(double sortedJaroWinkler) {
        this.sortedJaroWinkler = sortedJaroWinkler;
    }

    /**
     * @return the Blevenshtein
     */
    public double getBlevenshtein() {
        return Blevenshtein;
    }

    /**
     * @param Blevenshtein the Blevenshtein to set
     */
    public void setBlevenshtein(double Blevenshtein) {
        this.Blevenshtein = Blevenshtein;
    }

    /**
     * @return the Bngram
     */
    public double getBngram() {
        return Bngram;
    }

    /**
     * @param Bngram the Bngram to set
     */
    public void setBngram(double Bngram) {
        this.Bngram = Bngram;
    }

    /**
     * @return the Bcosine
     */
    public double getBcosine() {
        return Bcosine;
    }

    /**
     * @param Bcosine the Bcosine to set
     */
    public void setBcosine(double Bcosine) {
        this.Bcosine = Bcosine;
    }

    /**
     * @return the BlongestCommonSubseq
     */
    public double getBlongestCommonSubseq() {
        return BlongestCommonSubseq;
    }

    /**
     * @param BlongestCommonSubseq the BlongestCommonSubseq to set
     */
    public void setBlongestCommonSubseq(double BlongestCommonSubseq) {
        this.BlongestCommonSubseq = BlongestCommonSubseq;
    }

    /**
     * @return the Bjaro
     */
    public double getBjaro() {
        return Bjaro;
    }

    /**
     * @param Bjaro the Bjaro to set
     */
    public void setBjaro(double Bjaro) {
        this.Bjaro = Bjaro;
    }

    /**
     * @return the BjaroWinkler
     */
    public double getBjaroWinkler() {
        return BjaroWinkler;
    }

    /**
     * @param BjaroWinkler the BjaroWinkler to set
     */
    public void setBjaroWinkler(double BjaroWinkler) {
        this.BjaroWinkler = BjaroWinkler;
    }

    /**
     * @return the BsortedJaroWinkler
     */
    public double getBsortedJaroWinkler() {
        return BsortedJaroWinkler;
    }

    /**
     * @param BsortedJaroWinkler the BsortedJaroWinkler to set
     */
    public void setBsortedJaroWinkler(double BsortedJaroWinkler) {
        this.BsortedJaroWinkler = BsortedJaroWinkler;
    }

    /**
     * @return the Dlevenshtein
     */
    public double getDlevenshtein() {
        return Dlevenshtein;
    }

    /**
     * @param Dlevenshtein the Dlevenshtein to set
     */
    public void setDlevenshtein(double Dlevenshtein) {
        this.Dlevenshtein = Dlevenshtein;
    }

    /**
     * @return the Dngram
     */
    public double getDngram() {
        return Dngram;
    }

    /**
     * @param Dngram the Dngram to set
     */
    public void setDngram(double Dngram) {
        this.Dngram = Dngram;
    }

    /**
     * @return the Dcosine
     */
    public double getDcosine() {
        return Dcosine;
    }

    /**
     * @param Dcosine the Dcosine to set
     */
    public void setDcosine(double Dcosine) {
        this.Dcosine = Dcosine;
    }

    /**
     * @return the DlongestCommonSubseq
     */
    public double getDlongestCommonSubseq() {
        return DlongestCommonSubseq;
    }

    /**
     * @param DlongestCommonSubseq the DlongestCommonSubseq to set
     */
    public void setDlongestCommonSubseq(double DlongestCommonSubseq) {
        this.DlongestCommonSubseq = DlongestCommonSubseq;
    }

    /**
     * @return the Djaro
     */
    public double getDjaro() {
        return Djaro;
    }

    /**
     * @param Djaro the Djaro to set
     */
    public void setDjaro(double Djaro) {
        this.Djaro = Djaro;
    }

    /**
     * @return the DjaroWinkler
     */
    public double getDjaroWinkler() {
        return DjaroWinkler;
    }

    /**
     * @param DjaroWinkler the DjaroWinkler to set
     */
    public void setDjaroWinkler(double DjaroWinkler) {
        this.DjaroWinkler = DjaroWinkler;
    }

    /**
     * @return the DsortedJaroWinkler
     */
    public double getDsortedJaroWinkler() {
        return DsortedJaroWinkler;
    }

    /**
     * @param DsortedJaroWinkler the DsortedJaroWinkler to set
     */
    public void setDsortedJaroWinkler(double DsortedJaroWinkler) {
        this.DsortedJaroWinkler = DsortedJaroWinkler;
    }

    /**
     * @return the samePhone
     */
    public boolean isSamePhone() {
        return samePhone;
    }

    /**
     * @param samePhone the samePhone to set
     */
    public void setSamePhone(boolean samePhone) {
        this.samePhone = samePhone;
    }

    /**
     * @return the sameAddress
     */
    public boolean isSameAddress() {
        return sameAddress;
    }

    /**
     * @param sameAddress the sameAddress to set
     */
    public void setSameAddress(boolean sameAddress) {
        this.sameAddress = sameAddress;
    }

    /**
     * @return the sameAddressName
     */
    public double getSameAddressName() {
        return sameAddressName;
    }

    /**
     * @param sameAddressName the sameAddressName to set
     */
    public void setSameAddressName(double sameAddressName) {
        this.sameAddressName = sameAddressName;
    }

    /**
     * @return the acceptance
     */
    public boolean isAcceptance() {
        return acceptance;
    }

    /**
     * @param acceptance the acceptance to set
     */
    public void setAcceptance(boolean acceptance) {
        this.acceptance = acceptance;
    }

    /**
     * @return the nameFusionAction
     */
    public String getNameFusionAction() {
        return nameFusionAction;
    }

    /**
     * @param nameFusionAction the nameFusionAction to set
     */
    public void setNameFusionAction(String nameFusionAction) {
        this.nameFusionAction = nameFusionAction;
    }

}
