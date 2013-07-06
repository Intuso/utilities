package com.intuso.utilities.wrapper;

import org.junit.Ignore;

/**
 * Created with IntelliJ IDEA.
 * User: ravnroot
 * Date: 08/07/12
 * Time: 13:32
 *
 * Data object for a test wrappable
 */
@Ignore
public class TestData extends Data<TestData> {

    String randomValue;

    public TestData(String name, TestData... subWrappables) {
        this(name, "randomValue", subWrappables);
    }

    public TestData(String name, String randomValue, TestData... subWrappables) {
        super(name, subWrappables);
        this.randomValue = randomValue;
    }

    /**
     * Get the random value
     * @return the random value
     */
    public String getRandomValue() {
        return randomValue;
    }
}
