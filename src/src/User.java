public class User {
    private final double distance;
    private final double lon;
    private final double lat;
    private final double kcalPerMinute;
    private final double walkingSpeed;

    public User(double mets, double weight, double walkingSpeed, double kcal, double lon, double lat) {
        double time = kcal * 200 / (mets * 3.5f * weight);
        this.kcalPerMinute = mets * 3.5 * weight / 200;
        this.walkingSpeed = walkingSpeed;
        this.distance = (time / 60 * walkingSpeed) * 1000;
        this.lon = lon;
        this.lat = lat;
    }

    public double getDistance() {
        return distance;
    }

    public double getEstimatedKcal(Double distance){
        return distance / 1000 / walkingSpeed * 60 * kcalPerMinute;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }
}


