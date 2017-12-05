/*
 * Federal Aviation Administration (FAA) public work 
 * 
 * As a work of the United States Government, this project is in the 
 * public domain within the United States. Additionally, we waive copyright 
 * and related rights in the work worldwide 
 * through the Creative Commons 0 (CC0) 1.0 Universal public domain dedication
 * 
 * APRA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package gov.faa.ait.apra.test;

import static org.junit.Assert.*;

import org.junit.Test;

import gov.faa.ait.apra.util.ChartInfoTableKey;

public class KeyTest {

	@Test
	public void test() {
		ChartInfoTableKey k1 = new ChartInfoTableKey("ALBUQUERQUE", "CURRENT", "SECTIONAL");
		ChartInfoTableKey k2 = new ChartInfoTableKey("ALBUQUERQUE", "NEXT", "SECTIONAL");
		
		assertEquals(k1,k1);
		assertEquals(k2,k2);
		assertEquals(k1.hashCode(), k1.hashCode());
		assertEquals(k2.hashCode(), k2.hashCode());
		assertNotEquals(k1,k2);
		assertNotEquals(k1.hashCode(), k2.hashCode());
	}

}
