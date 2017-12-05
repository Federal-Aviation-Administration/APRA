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

import java.util.Calendar;

import org.junit.Test;

import gov.faa.ait.apra.api.SectionalCharts;
import gov.faa.ait.apra.bootstrap.ErrorCodes;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.jaxb.ProductSet.Edition;

public class SectionalChartsTest {

	@Test
	public void test() {
		SectionalCharts api = new SectionalCharts();
		ProductSet result = (ProductSet) api.getSectionalChart("Albuquerque", "current", "pdf").getEntity();
		assertTrue(!result.getEdition().isEmpty());
		Edition ed = result.getEdition().get(0);
		assertEquals(100,ed.getEditionNumber());
	}
	
	@Test
	public void testInvalidCity() {
		SectionalCharts api = new SectionalCharts();
		ProductSet result = (ProductSet) api.getSectionalChart("Albuquer", "current", "pdf").getEntity();
		assertEquals(Integer.valueOf(404),result.getStatus().getCode());
		assertEquals(ErrorCodes.ERROR_404, result.getStatus().getMessage());
	}

}
