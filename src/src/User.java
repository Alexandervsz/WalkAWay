public class User {
    private float mets;
    private float weight;
    private float walkingSpeed;
    private float kcal;
    private float distance;

    public User(float mets, float weight, float walkingSpeed, float kcal) {
        this.mets = mets;
        this.weight = weight;
        this.walkingSpeed = walkingSpeed;
        this.kcal = kcal;
        float time = kcal * 200 / (mets * 3.5f * weight);
        this.distance = time/60 * walkingSpeed;
    }

    public float getDistance() {
        return distance;
    }
}
