package gr.athena.innovation.fagi.learning;

/**
 *
 * @author Giorgos Kostoulas
 */
public class TrainingSet {

    private String idA;
    private String idB;

    private String distanceMeters;

    private String nameA;
    private String nameB;
    private String nameFusionAction;

    private String streetA;
    private String streetB;
    private String streetFusionAction;

    private String streetNumberA;
    private String streetNumberB;

    private String phoneA;
    private String phoneB;
    private String phoneFusionAction;

    private String emailA;
    private String emailB;
    private String emailFusionAction;

    private String websiteA;
    private String websiteB;
    private String websiteFusionAction;

    private String score;
    private String names1;
    private String acceptance;

    public TrainingSet(String[] spl) {
        this.idA = spl[0];
        this.idB = spl[1];

        this.distanceMeters = spl[2];

        this.nameA = spl[3];
        this.nameB = spl[4];
        this.nameFusionAction = spl[5];

        this.streetA = spl[6];
        this.streetB = spl[7];
        this.streetFusionAction = spl[8];

        this.streetNumberA = spl[9];
        this.streetNumberB = spl[10];

        this.phoneA = spl[11];
        this.phoneB = spl[12];
        this.phoneFusionAction = spl[13];

        this.emailA = spl[14];
        this.emailB = spl[15];
        this.emailFusionAction = spl[16];

        this.websiteA = spl[17];
        this.websiteB = spl[18];
        this.websiteFusionAction = spl[19];

        this.score = spl[20];
        this.names1 = spl[21];
        this.acceptance = spl[22];
    }

    public String getIdA() {
        return idA;
    }

    public void setIdA(String idA) {
        this.idA = idA;
    }

    public String getIdB() {
        return idB;
    }

    public void setIdB(String idB) {
        this.idB = idB;
    }

    public String getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(String distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public String getNameA() {
        return nameA;
    }

    public void setNameA(String nameA) {
        this.nameA = nameA;
    }

    public String getNameB() {
        return nameB;
    }

    public void setNameB(String nameB) {
        this.nameB = nameB;
    }

    public String getNameFusionAction() {
        return nameFusionAction;
    }

    public void setNameFusionAction(String nameFusionAction) {
        this.nameFusionAction = nameFusionAction;
    }

    public String getStreetA() {
        return streetA;
    }

    public void setStreetA(String streetA) {
        this.streetA = streetA;
    }

    public String getStreetB() {
        return streetB;
    }

    public void setStreetB(String streetB) {
        this.streetB = streetB;
    }

    public String getStreetFusionAction() {
        return streetFusionAction;
    }

    public void setStreetFusionAction(String streetFusionAction) {
        this.streetFusionAction = streetFusionAction;
    }

    public String getStreetNumberA() {
        return streetNumberA;
    }

    public void setStreetNumberA(String streetNumberA) {
        this.streetNumberA = streetNumberA;
    }

    public String getStreetNumberB() {
        return streetNumberB;
    }

    public void setStreetNumberB(String streetNumberB) {
        this.streetNumberB = streetNumberB;
    }

    public String getPhoneA() {
        return phoneA;
    }

    public void setPhoneA(String phoneA) {
        this.phoneA = phoneA;
    }

    public String getPhoneB() {
        return phoneB;
    }

    public void setPhoneB(String phoneB) {
        this.phoneB = phoneB;
    }

    public String getPhoneFusionAction() {
        return phoneFusionAction;
    }

    public void setPhoneFusionAction(String phoneFusionAction) {
        this.phoneFusionAction = phoneFusionAction;
    }

    public String getEmailA() {
        return emailA;
    }

    public void setEmailA(String emailA) {
        this.emailA = emailA;
    }

    public String getEmailB() {
        return emailB;
    }

    public void setEmailB(String emailB) {
        this.emailB = emailB;
    }

    public String getEmailFusionAction() {
        return emailFusionAction;
    }

    public void setEmailFusionAction(String emailFusionAction) {
        this.emailFusionAction = emailFusionAction;
    }

    public String getWebsiteA() {
        return websiteA;
    }

    public void setWebsiteA(String websiteA) {
        this.websiteA = websiteA;
    }

    public String getWebsiteB() {
        return websiteB;
    }

    public void setWebsiteB(String websiteB) {
        this.websiteB = websiteB;
    }

    public String getWebsiteFusionAction() {
        return websiteFusionAction;
    }

    public void setWebsiteFusionAction(String websiteFusionAction) {
        this.websiteFusionAction = websiteFusionAction;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getNames1() {
        return names1;
    }

    public void setNames1(String names1) {
        this.names1 = names1;
    }

    public String getAcceptance() {
        return acceptance;
    }

    public void setAcceptance(String acceptance) {
        this.acceptance = acceptance;
    }
}
