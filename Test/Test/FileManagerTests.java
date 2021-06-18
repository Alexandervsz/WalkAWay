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

public class FileManagerTests {
    @Test
    public void testBbox() {
        FileManager fileManager = new FileManager();
        boolean test1 = verifyBbox(10, 20);
        boolean test2 = verifyBbox(100, 200);
        boolean test3 = verifyBbox(1000, 2000);
        boolean test4 = verifyBbox(10000, 20000);
        boolean test5 = verifyBbox(100000, 200000);
        Assertions.assertAll(
                () -> Assertions.assertEquals("0.0,0.0,0.0,0.0", fileManager.generateBbox(new Node("0", 0, 0), 0)),
                () -> Assertions.assertTrue(test1),
                () -> Assertions.assertTrue(test2),
                () -> Assertions.assertTrue(test3),
                () -> Assertions.assertTrue(test4),
                () -> Assertions.assertTrue(test5)
        );
    }

    @Test
    public void testJson() throws Exception {
        FileManager fileManager = new FileManager();
        FileReader fileReader = new FileReader("SampleJson.json");
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
            fileManager.parseJson(new JSONObject());
            fail("Method did not throw an exception on invalid Json");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testGPX() throws IOException {
        // Since current time is used during generating of path, only empty file can be verified.
        FileManager fileManager = new FileManager();
        List<Node> emptyPoints = new ArrayList<>();
        File fileEmpty = new File("Test/empty.gpx");
        String filename = "test";
        fileManager.generateGpx(fileEmpty, filename, emptyPoints);
        String actual = Files.readString(fileEmpty.toPath());
        Assertions.assertEquals("""
                <?xml version="1.0" encoding="UTF-8" standalone="no" ?><gpx xmlns="http://www.topografix.com/GPX/1/1" creator="MapSource 6.15.5" version="1.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  xsi:schemaLocation="http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd"><trk>
                <name>test</name><trkseg>
                </trkseg></trk></gpx>""", actual);
    }

    public boolean verifyBbox(double totaldistance, double expected) {
        Node testNode = new Node("test", 5, 52);
        FileManager fileManager = new FileManager();
        String Bbox1 = fileManager.generateBbox(testNode, totaldistance);
        String[] points = Bbox1.split(",");
        Node test1point1 = new Node("test1", Double.parseDouble(points[0]), Double.parseDouble(points[1]));
        Node test1point2 = new Node("test1", Double.parseDouble(points[2]), Double.parseDouble(points[3]));
        test1point1.getDistanceTo(test1point2);
        double errorMargin = 2.5; // The formula used in generateBbox has an error margin of ~5m.
        return (test1point1.getDistanceToCurrentNode() > expected - errorMargin && test1point1.getBearingToCurrentNode() < expected + errorMargin);
    }
}
