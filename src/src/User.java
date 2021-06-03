public class User {
    private float mets;
    private float weight;
    private float walkingSpeed;
    private float kcal;

    public User(float mets, float weight, float walkingSpeed, float kcal) {
        this.mets = mets;
        this.weight = weight;
        this.walkingSpeed = walkingSpeed;
        this.kcal = kcal;
    }

    public float getKilometers(){
        float time = kcal * 200 / (mets * 3.5f * weight);
        return time/60 * walkingSpeed;
    }
}
