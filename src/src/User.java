
public class User {
    //private double time;
    private final double distance;
    private final double lon;
    private final double lat;
    private final double kcalPerMinute;
    private final double walkingSpeed;
    private final boolean isRandom;

    /**
     * Create a new user object
     *
     * @param mets         Mets value of the user's chosen activity.
     * @param weight       The user's weight in kilograms.
     * @param walkingSpeed The user's walking speed in kilometers per hour.
     * @param kcal         The amount of calories the walk needs to burn.
     * @param lon          The user's longitude.
     * @param lat          The user's latitude.
     * @param isRandom     Whether the user wants a random path or not.
     */
    public User(double mets, double weight, double walkingSpeed, double kcal, double lon, double lat, boolean isRandom) {
        double time = 0;
        if (mets != 0 && weight != 0) {
            time = kcal * 200 / (mets * 3.5 * weight); //in minutes.
        }
        this.isRandom = isRandom;
        this.kcalPerMinute = mets * 3.5 * weight / 200.0;
        this.walkingSpeed = walkingSpeed;
        this.distance = (time / 60 * walkingSpeed) * 1000;
        this.lon = lon;
        this.lat = lat;
    }

    /**
     * @return The required distance of the walk in meters.
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Calculates the amount of kcal the user burns when walking given distance in meters.
     *
     * @param distance The amount of distance traveled while doing user's chosen activity.
     * @return The estimated amount of calories burnt.
     */
    public double getEstimatedKcal(Double distance) {
        if (distance == 0) {
            return 0;
        }
        return distance / 1000 / walkingSpeed * 60 * kcalPerMinute;
    }

    public boolean isRandom() {
        return isRandom;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public double getTime(Double distance) {
        if (distance == 0){
            return 0;
        }
        return distance / 1000 / walkingSpeed * 60;
    }

    public double getKcalPerMinute() {
        return kcalPerMinute;
    }
}


