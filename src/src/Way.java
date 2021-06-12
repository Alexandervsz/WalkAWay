import java.util.Arrays;
import java.util.Objects;

public class Way {
    public String id;
    public String[] type;

    public Way(String id,  String[] type) {
        this.id = id;
        this.type = type;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Way way = (Way) o;
        return Objects.equals(id, way.id);
    }

    public String[] getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id);
        result = 31 * result + Arrays.hashCode(type);
        return result;
    }

    public String getid() {
        return this.id;
    }
}
