public class Ref {
    public static Ref singleton = new Ref();

    private double minStrength;
    private double maxStrength;
    private double minCurrentHP;
    private double maxCurrentHP;
    private double minMaxHP;
    private double maxMaxHP;
    private double minSpeed;
    private double maxSpeed;

    private double avgStrength;
    private double avgCurrentHP;
    private double avgMaxHP;
    private double avgSpeed;

    public void init() {
        singleton = this;
    }

    public double getMinStrength() {
        return minStrength;
    }

    public void setMinStrength(double minStrength) {
        this.minStrength = minStrength;
    }

    public double getMaxStrength() {
        return maxStrength;
    }

    public void setMaxStrength(double maxStrength) {
        this.maxStrength = maxStrength;
    }

    public double getMinCurrentHP() {
        return minCurrentHP;
    }

    public void setMinCurrentHP(double minCurrentHP) {
        this.minCurrentHP = minCurrentHP;
    }

    public double getMaxCurrentHP() {
        return maxCurrentHP;
    }

    public void setMaxCurrentHP(double maxCurrentHP) {
        this.maxCurrentHP = maxCurrentHP;
    }

    public double getMinMaxHP() {
        return minMaxHP;
    }

    public void setMinMaxHP(double minMaxHP) {
        this.minMaxHP = minMaxHP;
    }

    public double getMaxMaxHP() {
        return maxMaxHP;
    }

    public void setMaxMaxHP(double maxMaxHP) {
        this.maxMaxHP = maxMaxHP;
    }

    public double getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(double minSpeed) {
        this.minSpeed = minSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
}
