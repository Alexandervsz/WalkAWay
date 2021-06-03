public class MetValue {
    private int metValue;
    private int speed;
    private String activity;

    public MetValue(int metValue, int speed, String activity) {
        this.metValue = metValue;
        this.speed = speed;
        this.activity = activity;
    }

    @Override
    public String toString() {
        return activity;
    }
}
