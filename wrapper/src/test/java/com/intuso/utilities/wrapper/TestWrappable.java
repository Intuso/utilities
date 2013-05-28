package com.intuso.utilities.wrapper;

import org.junit.Ignore;

/**
 * Created with IntelliJ IDEA.
 * User: ravnroot
 * Date: 08/07/12
 * Time: 13:32
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class TestWrappable extends Wrappable<TestWrappable> {

    String randomValue;

    public TestWrappable(String name, TestWrappable ... subWrappables) {
        this(name, "randomValue", subWrappables);
    }

    public TestWrappable(String name, String randomValue, TestWrappable ... subWrappables) {
        super(name, subWrappables);
        this.randomValue = randomValue;
    }

    public String getRandomValue() {
        return randomValue;
    }
}
