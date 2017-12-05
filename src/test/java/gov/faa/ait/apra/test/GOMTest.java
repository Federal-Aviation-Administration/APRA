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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.api.GulfOfMexicoEnrouteCharts;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.cycle.VFRChartCycleClient;

public class GOMTest {
	private final static Logger logger = LoggerFactory.getLogger(GOMTest.class);
	
	private GulfOfMexicoEnrouteCharts gom;
	
	public GOMTest () {
		gom = new GulfOfMexicoEnrouteCharts();
	}
	
	@Test
	public void testDownloadOperations() {
		VFRChartCycleClient client = new VFRChartCycleClient("IFR_PGOM");
		
		ProductSet current = gom.getRelease(client.getCurrentCycle());	
		
		if (current.getEdition().size() > 0)
			logger.info(current.getEdition().get(0).getProduct().getUrl());
		
		ProductSet next = gom.getRelease(client.getNextCycle());
		if (next.getEdition().size() > 0)
			logger.info(next.getEdition().get(0).getProduct().getUrl());
	}
	
	@Test
	public void testEditionOperations() {
		VFRChartCycleClient client = new VFRChartCycleClient("IFR_PGOM");
		
		ProductSet current = gom.getEdition(client.getCurrentCycle());
		assertEquals(Integer.valueOf(current.getStatus().getCode()), Integer.valueOf(200));	
		
		ProductSet next = gom.getEdition(client.getNextCycle());
		assertEquals(Integer.valueOf(next.getStatus().getCode()), Integer.valueOf(200));
	}	
	
	@Test
	public void testGOMWest() {
		
		ProductSet current = (ProductSet) gom.getGOMRelease("current",  null, "west").getEntity();
		
		if (current.getEdition().size() > 0)
			logger.info(current.getEdition().get(0).getProduct().getUrl());
	}	
	
	@Test
	public void testGOMCentral() {
		
		ProductSet current = (ProductSet) gom.getGOMRelease("current", "PDF", "central").getEntity();
		
		if (current.getEdition().size() > 0)
			logger.info(current.getEdition().get(0).getProduct().getUrl());
	}
	
	@Test
	public void testGOMCurrent() {
	
		ProductSet current = (ProductSet) gom.getGOMRelease("current", null, null).getEntity();
		
		if (current.getEdition().size() > 0)
			logger.info(current.getEdition().get(0).getProduct().getUrl());
	}
	
	@Test
	public void testGOMBadGeoname() {
		
		ProductSet next = (ProductSet) gom.getGOMRelease("current", "PDF", "fubar").getEntity();
		
		assertEquals(Integer.valueOf(next.getStatus().getCode()), Integer.valueOf(400));		
	}

	@Test
	public void testGOMNulls() {
		VFRChartCycleClient client = new VFRChartCycleClient("IFR_PGOM");
		client.getCurrentCycle();
	
		ProductSet current = (ProductSet) gom.getGOMRelease(null, null, null).getEntity();
		
		assertEquals(Integer.valueOf(200), Integer.valueOf(current.getStatus().getCode()));		
	}
	
	@Test
	public void testGOMJunk() {
		
		ProductSet next = (ProductSet) gom.getGOMRelease("junk", "foo", "morejunk").getEntity();
		
		assertEquals(Integer.valueOf(next.getStatus().getCode()), Integer.valueOf(400));		
	}

	
	
	@Test
	public void testGOMTIFFWest() {
		
		ProductSet current = (ProductSet) gom.getGOMRelease("current", "TIFF",  "west").getEntity();
		
		if (current.getEdition().size() > 0)
			logger.info(current.getEdition().get(0).getProduct().getUrl());
	}	
	
	@Test
	public void testGOMTIFFCentral() {
		
		ProductSet current = (ProductSet) gom.getGOMRelease("current", "TIFF", "central").getEntity();
		
		if (current.getEdition().size() > 0)
			logger.info(current.getEdition().get(0).getProduct().getUrl());
	}
	
	@Test
	public void testGOMTIFFCurrent() {
	
		ProductSet current = (ProductSet) gom.getGOMRelease("current", "TIFF", null).getEntity();
		
		if (current.getEdition().size() > 0)
			logger.info(current.getEdition().get(0).getProduct().getUrl());
	}
	
	@Test
	public void testGOMTIFFBadGeoname() {
		
		ProductSet next = (ProductSet) gom.getGOMRelease("current", "TIFF", "fubar").getEntity();
		
		assertEquals(Integer.valueOf(next.getStatus().getCode()), Integer.valueOf(400));		
	}

	@Test
	public void testGOMTIFFNulls() {
		VFRChartCycleClient client = new VFRChartCycleClient("IFR_PGOM");
		client.getCurrentCycle();
	
		ProductSet current = (ProductSet) gom.getGOMRelease(null, "TIFF", null).getEntity();
		
		assertEquals(Integer.valueOf(200), Integer.valueOf(current.getStatus().getCode()));		
	}
	
	@Test
	public void testGOMTIFFJunk() {
		
		ProductSet next = (ProductSet) gom.getGOMRelease("junk", "TIFF", "morejunk").getEntity();
		
		assertEquals(Integer.valueOf(next.getStatus().getCode()), Integer.valueOf(400));		
	}
		
	
}
