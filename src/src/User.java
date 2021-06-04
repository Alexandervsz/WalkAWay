public class User {
    private final float distance;

    public User(float mets, float weight, float walkingSpeed, float kcal) {
        float time = kcal * 200 / (mets * 3.5f * weight);
        this.distance = time / 60 * walkingSpeed;
    }

    public float getDistance() {
        return distance;
    }
}
