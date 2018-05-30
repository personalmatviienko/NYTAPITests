import org.junit.Assert;
import org.junit.Test;

public class NYCTests extends BaseTest {

    //required test
    @Test
    public void sectionNumberTest() {
        Assert.assertTrue("Request for one section is not map request for all section",
                restUtils.verifySectionNumber());
    }

    // additional negative test cases
    @Test
    public void invalidPeriodTest() {
        Assert.assertTrue("Incorrect request state and body for invalid period param",
                restUtils.verifyInvalidPeriod());
    }

    @Test
    public void invalidSectionTest() {
        Assert.assertTrue("Incorrect request state and body for invalid section param",
                restUtils.verifyInvalidSection());
    }

}
