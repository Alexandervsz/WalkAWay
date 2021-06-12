import java.util.List;
import java.util.Map;

public class Way {
    public String id;
    public List<Node> nodes;
    public Map<String, String> type;

    public Way(String id, List<Node> nodes, Map<String, String> type) {
        this.id = id;
        this.nodes = nodes;
        this.type = type;
    }
}
