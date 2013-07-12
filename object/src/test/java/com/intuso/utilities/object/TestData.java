package com.intuso.utilities.object;

import org.junit.Ignore;

@Ignore
public class TestData extends Data<TestData> {

    String randomValue;

    public TestData(String name, TestData... childData) {
        this(name, "randomValue", childData);
    }

    public TestData(String name, String randomValue, TestData... childData) {
        super(name, childData);
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
