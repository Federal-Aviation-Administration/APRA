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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.api.OceanicRouteCharts;
import gov.faa.ait.apra.jaxb.ProductSet;

public class OceanicTest {
	private final static Logger logger = LoggerFactory.getLogger(OceanicTest.class);

	@Test 
	public void testDefaults() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet) oceanic.getOceanicRouteChart(null, null, null).getEntity();
		logger.info("Oceanic default test return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testNARCCurrentPDF() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet) oceanic.getOceanicRouteChart("current", null, "NARC").getEntity();
		logger.info("Oceanic test NARC current PDF return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}	
	
	@Test 
	public void testPORCCurrentPDF() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet) oceanic.getOceanicRouteChart("current", null, "PORC").getEntity();
		logger.info("Oceanic test PORC current PDF return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}	

	@Test 
	public void testWATRSCurrentPDF() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet)  oceanic.getOceanicRouteChart("current", null, "WATRS").getEntity();
		logger.info("Oceanic test WATRS current PDF return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}

	@Test 
	public void testNARCCurrentTIFF() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet)  oceanic.getOceanicRouteChart("current", "TIFF", "NARC").getEntity();
		logger.info("Oceanic test NARC current TIFF return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}	
	
	@Test 
	public void testPORCCurrentTIFF() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet)  oceanic.getOceanicRouteChart("current", "TIFF", "PORC").getEntity();
		logger.info("Oceanic test PORC current TIFF return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}	

	@Test 
	public void testWATRSCurrentTIFF() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet)  oceanic.getOceanicRouteChart("current", "TIFF", "WATRS").getEntity();
		logger.info("Oceanic test WATRS current TIFF return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	// Test next editions. Next editions may be either 200 or 404 response depending on whether the 
	// early release has been published by today's date
	
	@Test 
	public void testNARCNextPDF() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet)  oceanic.getOceanicRouteChart("next", null, "NARC").getEntity();
		logger.info("Oceanic test NARC next PDF return url of "+ps.getEdition().get(0).getProduct().getUrl());
		int code = ps.getStatus().getCode().intValue();
		if (! (code == 200 || code == 404) )
			fail();
	}	
	
	@Test 
	public void testPORCNextPDF() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet)  oceanic.getOceanicRouteChart("next", null, "PORC").getEntity();
		logger.info("Oceanic test PORC next PDF return url of "+ps.getEdition().get(0).getProduct().getUrl());
		int code = ps.getStatus().getCode().intValue();
		if (! (code == 200 || code == 404) )
			fail();
	}	

	@Test 
	public void testWATRSNextPDF() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet)  oceanic.getOceanicRouteChart("next", null, "WATRS").getEntity();
		logger.info("Oceanic test WATRS next PDF return url of "+ps.getEdition().get(0).getProduct().getUrl());
		int code = ps.getStatus().getCode().intValue();
		if (! (code == 200 || code == 404) )
			fail();
	}

	@Test 
	public void testNARCNextTIFF() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet)  oceanic.getOceanicRouteChart("next", "TIFF", "NARC").getEntity();
		logger.info("Oceanic test NARC next TIFF return url of "+ps.getEdition().get(0).getProduct().getUrl());
		int code = ps.getStatus().getCode().intValue();
		if (! (code == 200 || code == 404) )
			fail();
	}	
	
	@Test 
	public void testPORCNextTIFF() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet)  oceanic.getOceanicRouteChart("next", "TIFF", "PORC").getEntity();
		logger.info("Oceanic test PORC next TIFF return url of "+ps.getEdition().get(0).getProduct().getUrl());
		int code = ps.getStatus().getCode().intValue();
		if (! (code == 200 || code == 404) )
			fail();
	}	

	@Test 
	public void testWATRSNextTIFF() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet)  oceanic.getOceanicRouteChart("next", "TIFF", "WATRS").getEntity();
		logger.info("Oceanic test WATRS next TIFF return url of "+ps.getEdition().get(0).getProduct().getUrl());
		int code = ps.getStatus().getCode().intValue();
		if (! (code == 200 || code == 404) )
			fail();
	}
	
	// start of negative chart testing. Should result in errors
	@Test 
	public void testInvalidGeoCurrentPDF() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet)  oceanic.getOceanicRouteChart("current", "PDF", "Invalid").getEntity();
		logger.info("Oceanic test Invalid current PDF");
		assertEquals(400, ps.getStatus().getCode().intValue());
	}	
	
	@Test 
	public void testInvalidFormatCurrent() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet)  oceanic.getOceanicRouteChart("current", "Invalid", "NARC").getEntity();
		logger.info("Oceanic test NARC current INVALID");
		assertEquals(400, ps.getStatus().getCode().intValue());
	}
	
	@Test 
	public void testInvalidEditionPDF() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet)  oceanic.getOceanicRouteChart("Invalid", "TIFF", "PORC").getEntity();
		logger.info("Oceanic test PORC Invalid PDF");
		assertEquals(400, ps.getStatus().getCode().intValue());
	}

	@Test 
	public void testGarbage () {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet)  oceanic.getOceanicRouteChart("&+current", "+TIFF&", "?PORC").getEntity();
		logger.info("Oceanic test of garbage input");
		assertEquals(400, ps.getStatus().getCode().intValue());
	}	
	
	// Edition info testing
	
	@Test 
	public void testEditionDefaults() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet)  oceanic.getOceanicRouteEdition(null).getEntity();
		logger.info("Oceanic default edition test");
		assertEquals(200, ps.getStatus().getCode().intValue());
	}
	
	@Test 
	public void testEditionCurrent() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet)  oceanic.getOceanicRouteEdition("current").getEntity();
		logger.info("Oceanic edition test current");
		assertEquals(200, ps.getStatus().getCode().intValue());
	}	
	
	@Test 
	public void testEditionNext() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet)  oceanic.getOceanicRouteEdition("next").getEntity();
		logger.info("Oceanic edition test next");
		assertEquals(200, ps.getStatus().getCode().intValue());
	}	
	
	@Test 
	public void testEditionGarbage() {
		OceanicRouteCharts oceanic = new OceanicRouteCharts();
		ProductSet ps = (ProductSet)  oceanic.getOceanicRouteEdition("bar+foo&current").getEntity();
		logger.info("Oceanic edition test next");
		assertEquals(200, ps.getStatus().getCode().intValue());
	}	
}
