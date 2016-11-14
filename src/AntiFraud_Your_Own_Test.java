import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

/*
    Test data used in insight_testsuite/tests/your-own-test/paymo_input/batch_payment.txt
    ------------------------------------
    time, id1, id2, amount, message
    2016-11-02 09:38:53, 49466, 6989, 23.74, ðŸ¦„               # O degrees of separation
    2016-11-02 09:38:53, 6989, 8552, 23.74, ðŸ¦„                # 1 degrees of separation
    2016-11-02 09:38:53, 8552, 2562, 37.10, Pitcher             # 2 degrees of separation
    2016-11-02 09:38:53, 2562, 32639, 18.68, ðŸš•               # 3 degrees of separation
    2016-11-02 09:38:53, 32639, 15381, 18.68, ðŸš•              # 4 degrees of separation
    2016-11-02 09:38:53, 15381, 13167, 20.92, For your wife.    # 5 degrees of separation

    Expected JUnit results
    ----------------------
    Feature1TestPass(): trusted, 49466 -> 32639: (Friend link exists)
    Feature1TestFail(): unverified, 49466 -> xxxxx: (Friend link Non-Existent)
    Feature2TestPass(): trusted, 49466 -> 6989: Degree Of Separation == 0 (Direct Friends)
    Feature2TestFail(): unverified, 49466 -> 8552: Degree Of Separation = 1 (Friend of Friend)
    Feature3TestPass(): trusted, 49466 -> 15381: Degree Of Separation == 4
    Feature3TestFail(): unverified, 49466 -> 13167: Degree Of Separation > 4
 */


public class AntiFraud_Your_Own_Test
{
    AntiFraud a;
    Graph g;

    @Before
    public void Initialize()
    {
        a = new AntiFraud();
        g = a.BuildInitialStateUsingBatchPaymentTxt("insight_testsuite/tests/your-own-test/paymo_input/batch_payment.txt");
    }

    @Test
    public void testOrder1() {
        Feature1TestPass();
        Feature1TestFail();
        Feature2TestPass();
        Feature2TestFail();
        Feature3TestPass();
        Feature3TestFail();
    }

    /*
        FEATURE 1 Tests
    */
    public void Feature1TestPass()
    {
        String person1 = "49466";
        String person2 = "32639";

        String result = a.Feature1Test(g, person1, person2);
        System.out.println("Feature1TestPass(): " + result);
        assertThat(result, startsWith("trusted,"));
    }

    public void Feature1TestFail()
    {
        String person1 = "49466";
        String person2 = "xxxxx";

        String result = a.Feature1Test(g, person1, person2);
        System.out.println("Feature1TestFail(): " + result);
        assertThat(result, startsWith("unverified,"));
    }

    /*
        FEATURE 2 Tests
    */
    public void Feature2TestPass()
    {
        String person1 = "49466";
        String person2 = "6989";

        String result = a.Feature2Test(g, person1, person2);
        System.out.println("Feature2TestPass(): " + result);
        assertThat(result, startsWith("trusted,"));
    }

    public void Feature2TestFail()
    {
        String person1 = "49466";
        String person2 = "8552";

        String result = a.Feature2Test(g, person1, person2);
        System.out.println("Feature2TestFail(): " + result);
        assertThat(result, startsWith("unverified,"));
    }

    /*
        FEATURE 3 Tests
    */
    public void Feature3TestPass()
    {
        String person1 = "49466";
        String person2 = "15381";

        String result = a.Feature3Test(g, person1, person2);
        System.out.println("Feature3TestPass(): " + result);
        assertThat(result, startsWith("trusted,"));
    }

    public void Feature3TestFail()
    {
        String person1 = "49466";
        String person2 = "13167";

        String result = a.Feature3Test(g, person1, person2);
        System.out.println("Feature3TestFail(): " + result);
        assertThat(result, startsWith("unverified,"));
    }
}