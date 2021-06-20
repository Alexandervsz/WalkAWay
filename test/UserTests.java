import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class UserTests {
    User test1 = new User(3.0, 70, 5.1, 200, 5.0626701, 52.6492016, false);
    User test2 = new User(3.5, 80, 4.9, 65, 0, 0, false);
    User test3 = new User(8.3, 60, 8, 500, 0, 0, false);
    User test4 = new User(1, 1, 1, 1.75, 0, 0, false);
    User test5 = new User(0, 0, 0, 0, 0, 0, false);

    @Test
    public void testTime() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(54.421768707, test1.getTime(test1.getDistance()), 0.0000000009),
                () -> Assertions.assertEquals(13.265306122, test2.getTime(test2.getDistance()), 0.0000000009),
                () -> Assertions.assertEquals(57.372346528, test3.getTime(test3.getDistance()), 0.000000001),
                () -> Assertions.assertEquals(100, test4.getTime(test4.getDistance())),
                () -> Assertions.assertEquals(0, test5.getTime(test5.getDistance()))
        );
    }

    @Test
    public void testCalories() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(3.675, test1.getKcalPerMinute()),
                () -> Assertions.assertEquals(4.9, test2.getKcalPerMinute()),
                () -> Assertions.assertEquals(8.715, test3.getKcalPerMinute(), 0.0001),
                () -> Assertions.assertEquals(0.0175, test4.getKcalPerMinute()),
                () -> Assertions.assertEquals(0, test5.getKcalPerMinute())
        );
    }

    @Test
    public void testDistance() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(4625.8503401, test1.getDistance(), 0.0000001),
                () -> Assertions.assertEquals(1083.3333333, test2.getDistance(), 0.0000001),
                () -> Assertions.assertEquals(7649.6462038, test3.getDistance(), 0.0000001),
                () -> Assertions.assertEquals(1666.6666666, test4.getDistance(), 0.0000001),
                () -> Assertions.assertEquals(0, test5.getDistance())
        );
    }

    @Test
    public void testEstimatedKcal() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(200, test1.getEstimatedKcal(4625.8503401), 0.0000001),
                () -> Assertions.assertEquals(65, test2.getEstimatedKcal(1083.3333333), 0.0000001),
                () -> Assertions.assertEquals(500, test3.getEstimatedKcal(7649.6462038), 0.0000001),
                () -> Assertions.assertEquals(1.75, test4.getEstimatedKcal(1666.6666666), 0.0000001),
                () -> Assertions.assertEquals(0, test5.getEstimatedKcal(0.0))
        );
    }

    @Test
    public void testLatLong(){
        Assertions.assertAll(
                () -> Assertions.assertEquals(5.0626701, test1.getLon()),
                () -> Assertions.assertEquals(52.6492016, test1.getLat())
        );
    }


}
