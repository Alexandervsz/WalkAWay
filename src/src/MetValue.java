public class MetValue implements java.io.Serializable {
    private float metValue;
    private float speed;
    private String activity;

    public MetValue(float metValue, float speed, String activity) {
        this.metValue = metValue;
        this.speed = speed;
        this.activity = activity;
    }

    @Override
    public String toString() {
        return activity;
    }
}
