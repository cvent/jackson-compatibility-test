import com.cvent.enumdeserializer.EnumDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.junit.Test;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import static org.junit.Assert.assertEquals;

/**
 * Test that this enum deserializes as expected
 */
public class TestTimeZone296 {

    /**
     * Mixin to use custom deserializer we want to test
     */
//    @JsonDeserialize(using = EnumDeserializer.class)
    private static class TimeZoneMixin {
    }

    /**
     * Class to deserialize in tests
     */
    private static class TestClass {

        private TimeZone timeZone;

        @JsonProperty("timeZone")
        public TimeZone getTimeZone() {
            return timeZone;
        }

        @JsonProperty("timeZone")
        public void setTimeZone(TimeZone timeZone) {
            this.timeZone = timeZone;
        }
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.addMixInAnnotations(TimeZone.class, TimeZoneMixin.class);
    }

    @Test
    public void testDeserializeFromInt() throws Exception {
        TestClass timeZone = OBJECT_MAPPER.readValue("{ \"timeZone\": 285 }", TestClass.class);
        assertEquals(TimeZone.FijiIslandsTime, timeZone.getTimeZone());
    }

    @Test
    public void testDeserializeFromIntString() throws Exception {
        TestClass timeZone = OBJECT_MAPPER.readValue("{ \"timeZone\": \"285\" }", TestClass.class);
        assertEquals(TimeZone.FijiIslandsTime, timeZone.getTimeZone());
    }

    @Test
    public void testDeserializeFromName() throws Exception {
        TestClass timeZone = OBJECT_MAPPER.readValue("{ \"timeZone\": \"FijiIslandsTime\" }", TestClass.class);
        assertEquals(TimeZone.FijiIslandsTime, timeZone.getTimeZone());
    }

    // Root-level test cases only supportable for jackson-databind>=2.5
    @Test
    public void testDeserializeFromIntRootLevel() throws Exception {
        TimeZone timeZone = OBJECT_MAPPER.readValue("285", TimeZone.class);
        assertEquals(TimeZone.FijiIslandsTime, timeZone);
    }

    @Test
    public void testDeserializeFromIntStringRootLevel() throws Exception {
        TimeZone timeZone = OBJECT_MAPPER.readValue("\"285\"", TimeZone.class);
        assertEquals(TimeZone.FijiIslandsTime, timeZone);
    }

    @Test
    public void testDeserializeFromNameRootLevel() throws Exception {
        TimeZone timeZone = OBJECT_MAPPER.readValue("\"FijiIslandsTime\"", TimeZone.class);
        assertEquals(TimeZone.FijiIslandsTime, timeZone);
    }

    /**
     * An enum db type
     */
    public enum TimeZone {

        DatelineTime(0),
        SamoaTime(1),
        HawaiianTime(2),
        AlaskanTime(3),
        PacificTime(4),
        MountainTime(10),
        Mexico2Time(13),
        USMountainTime(15),
        CentralTime(20),
        CanadaCentralTime(25),
        MexicoTime(30),
        CentralAmericaTime(33),
        EasternTime(35),
        USEasternTime(40),
        SAPacificTime(45),
        AtlanticTime(50),
        SAWesternTime(55),
        PacificSATime(56),
        NewfoundlandTime(60),
        ESouthAmericaTime(65),
        SAEasternTime(70),
        GreenlandTime(73),
        MidAtlanticTime(75),
        AzoresTime(80),
        CapeVerdeTime(83),
        GMTTime(85),
        GreenwichTime(90),
        CentralEuropeTime(95),
        RomanceTime(105),
        WEuropeTime(110),
        WCentralAfricaTime(113),
        EEuropeTime(115),
        EgyptTime(120),
        FLETime(125),
        GTBTime(130),
        JerusalemTime(135),
        SouthAfricaTime(140),
        RussianTime(145),
        ArabTime(150),
        EAfricaTime(155),
        ArabicTime(158),
        IranTime(160),
        ArabianTime(165),
        CaucasusTime(170),
        AfghanistanTime(175),
        EkaterinburgTime(180),
        WestAsiaTime(185),
        IndiaTime(190),
        NepalTime(193),
        CentralAsiaTime(195),
        SriLankaTime(200),
        NCentralAsiaTime(201),
        MyanmarTime(203),
        SEAsiaTime(205),
        NorthAsiaTime(207),
        ChinaTime(210),
        MalayPeninsulaTime(215),
        TaipeiTime(220),
        WAustraliaTime(225),
        NorthAsiaEastTime(227),
        KoreaTime(230),
        TokyoTime(235),
        YakutskTime(240),
        AUSCentralTime(245),
        CenAustraliaTime(250),
        AUSEasternTime(255),
        EAustraliaTime(260),
        TasmaniaTime(265),
        VladivostokTime(270),
        WestPacificTime(275),
        CentralPacificTime(280),
        FijiIslandsTime(285),
        NewZealandTime(290),
        TongaTime(300),
        UTC(360);

        private final int value;

        private TimeZone(int value) {
            this.value = value;
        }

        @JsonCreator
        public static TimeZone fromInt(int value) {
            for (TimeZone i : TimeZone.values()) {
                if (i.getValue() == value) {
                    return i;
                }
            }
            throw new IllegalArgumentException(String.format("Invalid Timezone %s", value));
        }

        @JsonValue
        public int getValue() {
            return value;
        }

    }

}
