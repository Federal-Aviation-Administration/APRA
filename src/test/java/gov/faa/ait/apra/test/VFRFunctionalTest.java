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

import gov.faa.ait.apra.api.VFRCharts;
import gov.faa.ait.apra.jaxb.ProductSet;

public class VFRFunctionalTest {
	private static final  Logger logger = LoggerFactory.getLogger(VFRFunctionalTest.class);
	private VFRCharts vfr;
	
	public VFRFunctionalTest () {
		vfr = new VFRCharts();
	}
	
	@Test 
	public void testProductReleseCurrent() {
		vfr = new VFRCharts();
		ProductSet ps = (ProductSet) vfr.getGrandCanyonRelease("current").getEntity();
		logger.info("VFR Product Relese Test for 'current' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}	

	@Test 
	public void testProductEditionCurrent() {
		vfr = new VFRCharts();
		ProductSet ps = (ProductSet) vfr.getGrandCanyonEdition("current").getEntity();
		logger.info("VFR Product Edition Test for 'current' return code "+ps.getStatus().getCode().intValue());
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 200);
	}	
	
	@Test 
	public void testProductReleseNext() {
		vfr = new VFRCharts();
		ProductSet ps = (ProductSet) vfr.getGrandCanyonRelease("next").getEntity();
		logger.info("VFR Product Edition Test for 'Next' return code "+ps.getStatus().getCode().intValue());
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 404);
	}

	@Test 
	public void testProductEditionNext() {
		vfr = new VFRCharts();
		ProductSet ps = (ProductSet) vfr.getGrandCanyonEdition("next").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("VFR Product Edition Test for 'next' return code "+ps.getStatus().getCode().intValue());
		
		switch (code) {
			case 200: assertEquals(new Integer(200), new Integer(code));
				break;
			case 404: assertEquals(new Integer(404), new Integer(code));
				break;
		
			default:
				fail();
		}
	}

	
	
	// Negative tests
	
	@Test 
	public void testProductEditionTypo() {
		vfr = new VFRCharts();
		ProductSet ps = (ProductSet) vfr.getGrandCanyonEdition("TYPO").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("VFR Product Edition Test for 'incorrect edition' return code "+ps.getStatus().getCode().intValue());
		assertEquals(code, 400);
	}	

	@Test 
	public void testProductReleaseTypo() {
		vfr = new VFRCharts();
		ProductSet ps = (ProductSet) vfr.getGrandCanyonRelease("TYPO").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("VFR Product Relese Test for 'incorrect Release' return code "+ps.getStatus().getCode().intValue());
		assertEquals(code, 400);
	}	
	
	@Test 
	public void testProductEditionGeoTypo() {
		vfr = new VFRCharts();
		ProductSet ps = (ProductSet) vfr.getGrandCanyonEdition("").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("VFR Product Edition Test for 'incorrect Geo' return code "+ps.getStatus().getCode().intValue());
		assertEquals(code, 200);
	}	
	
	@Test 
	public void testProductReleaseGeoTypo() {
		vfr = new VFRCharts();
		ProductSet ps = (ProductSet) vfr.getGrandCanyonRelease("").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("VFR Product Release Test for 'incorrect Geo' return code "+ps.getStatus().getCode().intValue());
		assertEquals(code, 200);
	}

	@Test 
	public void testProductEditionTypoTypo() {
		vfr = new VFRCharts();
		ProductSet ps = (ProductSet) vfr.getGrandCanyonEdition("Typo").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("VFR Product Edition Test for 'incorrect edition and geo' return code "+ps.getStatus().getCode().intValue());
		assertEquals(code, 400);
	}	
	
	@Test 
	public void testProductReleaseFmtTypoTypo() {
		vfr = new VFRCharts();
		ProductSet ps = (ProductSet) vfr.getGrandCanyonRelease("Typo").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("VFR Product Release Test for 'incorrect Release and geo' return code "+ps.getStatus().getCode().intValue());
		assertEquals(code, 400);
	}
	
	@Test 
	public void testProductReleseDefault() {
		vfr = new VFRCharts();
		ProductSet ps = (ProductSet) vfr.getGrandCanyonRelease("").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("VFR Product Edition Test for 'empty and empty' return code "+ps.getStatus().getCode().intValue());
		assertEquals(code, 200);
	}
	
}
