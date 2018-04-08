package com.commafeed.backend;

import java.util.Comparator;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FixedSizeSortedSetTest {

	private FixedSizeSortedSet<String> set;
	private static Comparator<String> COMP = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			return ObjectUtils.compare(o1, o2);
		}
	};

	@Before
	public void init() {
		set = new FixedSizeSortedSet<String>(3, COMP);
	}

	@Test
	public void testSimpleAdd() {
		set.add("0");
		set.add("1");
		set.add("2");

		Assert.assertEquals("0", set.asList().get(0));
		Assert.assertEquals("1", set.asList().get(1));
		Assert.assertEquals("2", set.asList().get(2));
	}

	@Test
	public void testIsFull() {
		set.add("0");
		set.add("1");

		Assert.assertFalse(set.isFull());
		set.add("2");
		Assert.assertTrue(set.isFull());
	}

	@Test
	public void testOrder() {
		set.add("2");
		set.add("1");
		set.add("0");

		Assert.assertEquals("0", set.asList().get(0));
		Assert.assertEquals("1", set.asList().get(1));
		Assert.assertEquals("2", set.asList().get(2));
	}

	@Test
	public void testEviction() {
		set.add("7");
		set.add("8");
		set.add("9");

		set.add("0");
		set.add("1");
		set.add("2");

		Assert.assertEquals("0", set.asList().get(0));
		Assert.assertEquals("1", set.asList().get(1));
		Assert.assertEquals("2", set.asList().get(2));
	}

	@Test
	public void testCapacity() {
		set.add("0");
		set.add("1");
		set.add("2");
		set.add("3");

		Assert.assertEquals(3, set.asList().size());
	}

	@Test
	public void testLast() {
		set.add("0");
		set.add("1");
		set.add("2");

		Assert.assertEquals("2", set.last());

		set.add("3");

		Assert.assertEquals("2", set.last());
	}

    /**
     * The custom made fixedSizeSortedSet should handle/declare exception from empty
     * upon getting .last() call, at least we should warn that the method could throw an exception
     *
     * Refactor: As .last() is returning an object, let's return "null" if the current set is null
     */
	@Test
    public void testLastWithEmptySet(){
	    try{
	        set.last();
        }catch (Exception ee){
	        ee.printStackTrace();
            Assert.fail();

        }


    }

    /**
     * The custom made fixedSizeSortedSet should handle/declare exception from empty
     * upon getting .isFull(), at least we should warn that the method could throw an exception
     *
     * Refactor: As isFull return a Boolean, let's throw an exception if the set is not initialized
     */
    @Test
    public void testIsFullWithEmptySet(){
        try{
            set.isFull();
        }catch (Exception ee){
            ee.printStackTrace();
        }
        Assert.fail();
    }
}
