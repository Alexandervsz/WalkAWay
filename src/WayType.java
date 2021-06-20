/**
 * Data class for types of way, used in building queries for overpass.
 */
public record WayType(String mainType, String subType) {

    /**
     * The main type of way (eg highway or bicycle).
     * @return The main type.
     */
    public String getMainType() {
        return mainType;
    }

    /**
     * The sub type of way (eg footpath)
     * @return The subtype.
     */
    public String getSubType() {
        return subType;
    }
}
