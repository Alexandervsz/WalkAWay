public class NodeType {
    private final String mainType;
    private final String subType;

    public NodeType(String mainType, String subType) {
        this.mainType = mainType;
        this.subType = subType;
    }

    public String getMainType() {
        return mainType;
    }

    public String getSubType() {
        return subType;
    }
}
