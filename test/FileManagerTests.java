import algorithm.Node;
import data.FileManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test class which tests data.FileManager.
 */
public class FileManagerTests {
    /**
     * Tests if the bounding box is correct at the north pole.
     */
    @Test
    public void testBboxNorthPole() {
        Node northPole = new Node("north", 0, 90.0000);
        testBbox(northPole);


    }

    /**
     * Tests if the bounding box is correct at the equator.
     */
    @Test
    public void testBboxEquator() {
        Node equator = new Node("null", 0, 0);
        testBbox(equator);

    }

    /**
     * Tests if the bounding box is correct at the eastern most point of earth
     */
    @Test
    public void testBboxEast() {
        Node equator = new Node("null", 180, 0);
        testBbox(equator);

    }

    /**
     * Tests whether the bounding box is correct in the Netherlands.
     */
    @Test
    public void testBboxNl() {
        Node netherlands = new Node("nl", 5, 52);
        testBbox(netherlands);
    }

    /**
     * Tests the distance between the generated bbox points, it should be at least x2 the input distance in case of
     * worst case scenario path (straight line). Therefore tests fail when the generated bbox is too small.
     * Since it uses the haversine formula twice, which leads to a delta of 6%, and inaccuracies of the bounding box
     * generating algorithm, a delta of 15% is used.
     *
     * @param node A position on the map to verify the bbox for.
     */
    public void testBbox(Node node) {
        FileManager fileManager = new FileManager();
        boolean test1 = verifyBbox(1, node) > 2;
        boolean test2 = verifyBbox(10, node) > 20;
        boolean test3 = verifyBbox(100, node) > 200;
        boolean test4 = verifyBbox(1000, node) > 2000;
        boolean test5 = verifyBbox(10000, node) > 20000;
        boolean test6 = verifyBbox(50000, node) > 100000;
        boolean test7 = verifyBbox(100000, node) > 200000;
        Assertions.assertAll(
                () -> Assertions.assertEquals("0.0,0.0,0.0,0.0", fileManager.generateBbox(new Node("0", 0, 0), 0, false)),
                () -> Assertions.assertEquals(2, verifyBbox(1, node), 2 * 0.15),
                () -> Assertions.assertEquals(20, verifyBbox(10, node), 20 * 0.15),
                () -> Assertions.assertEquals(200, verifyBbox(100, node), 200 * 0.15),
                () -> Assertions.assertEquals(2000, verifyBbox(1000, node), 2000 * 0.15),
                () -> Assertions.assertEquals(20000, verifyBbox(10000, node), 20000 * 0.15),
                () -> Assertions.assertEquals(100000, verifyBbox(50000, node), 100000 * 0.15),
                () -> Assertions.assertEquals(200000, verifyBbox(100000, node), 200000 * 0.15),
                () -> Assertions.assertTrue(test1, String.valueOf(verifyBbox(1, node))),
                () -> Assertions.assertTrue(test2, String.valueOf(verifyBbox(10, node))),
                () -> Assertions.assertTrue(test3, String.valueOf(verifyBbox(100, node))),
                () -> Assertions.assertTrue(test4, String.valueOf(verifyBbox(1000, node))),
                () -> Assertions.assertTrue(test5, String.valueOf(verifyBbox(10000, node))),
                () -> Assertions.assertTrue(test6, String.valueOf(verifyBbox(50000, node))),
                () -> Assertions.assertTrue(test7, String.valueOf(verifyBbox(100000, node)))

        );
    }

    /**
     * Tests whether the json file is parsed correctly. Also tests if the IO error is raised correctly.
     *
     * @throws Exception When the test file cannot be found.
     */
    @Test
    public void testJson() throws Exception {
        FileManager fileManager = new FileManager();
        FileReader fileReader = new FileReader("res/SampleJson.json");
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);
        fileManager.parseJson(jsonObject);
        Assertions.assertAll(
                () -> Assertions.assertNotEquals(new HashSet<>(), fileManager.getNodeSet()),
                () -> Assertions.assertNotEquals(new HashSet<>(), fileManager.getWaySet()),
                () -> Assertions.assertEquals(1146, fileManager.getNodeSet().size()),
                () -> Assertions.assertEquals(337, fileManager.getWaySet().size())
        );
        try {
            FileReader fileReader2 = new FileReader("res/SampleJsonNodeRemoved.json");
            JSONObject jsonObject2 = (JSONObject) jsonParser.parse(fileReader2);
            fileManager.parseJson(jsonObject2);
            fail("Method did not throw an exception on missing node.");
        } catch (IOException ignored) {
        }
    }

    /**
     * Tests whether the gpx file is successfully generated. Can't be tested with sample data since the generation
     * of the gpx file uses system time. Therefore an empty set is tested.
     *
     * @throws IOException If something is wrong with the filepath.
     */
    @Test
    public void testGPX() throws IOException {
        // Since current time is used during generating of path, only empty file can be verified.
        FileManager fileManager = new FileManager();
        List<Node> emptyPoints = new ArrayList<>();
        File fileEmpty = new File("res/empty.gpx");
        String filename = "test";
        fileManager.generateGpx(fileEmpty, filename, emptyPoints);
        String actual = Files.readString(fileEmpty.toPath());
        Assertions.assertEquals("""
                <?xml version="1.0" encoding="UTF-8" standalone="no" ?><gpx xmlns="http://www.topografix.com/GPX/1/1" creator="WalkAWay" version="1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  xsi:schemaLocation="http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd"><trk>
                <name>test</name><trkseg>
                </trkseg></trk></gpx>""", actual);
    }

    /**
     * Generates a Bbox, and then returns the distance between the edge points.
     *
     * @param totalDistance The total distance to be walked.
     * @param testNode      The starting point to base the Bbox on.
     * @return The distance between the extremities of the bounding box.
     */
    public double verifyBbox(double totalDistance, Node testNode) {
        FileManager fileManager = new FileManager();
        String Bbox1 = fileManager.generateBbox(testNode, totalDistance, false);
        String[] points = Bbox1.split(",");
        Node test1point1 = new Node("test1", Double.parseDouble(points[0]), Double.parseDouble(points[1]));
        Node test1point2 = new Node("test1", Double.parseDouble(points[2]), Double.parseDouble(points[3]));
        test1point1.getDistanceTo(test1point2);
        return test1point1.getDistanceToCurrentNode();
    }
}
