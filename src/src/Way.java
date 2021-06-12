import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Way {
    public String id;
    public List<Node> nodes;
    public String[] type;

    public Way(String id, List<Node> nodes, String[] type) {
        this.id = id;
        this.nodes = nodes;
        this.type = type;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Way way = (Way) o;
        return Objects.equals(id, way.id);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, nodes);
        result = 31 * result + Arrays.hashCode(type);
        return result;
    }

    public String getid() {
        return this.id;
    }
}
