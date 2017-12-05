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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.api.HelicopterCharts;
import gov.faa.ait.apra.jaxb.ProductSet;

public class HelicopterFunctionalTest {
	private static final Logger logger = LoggerFactory
			.getLogger(HelicopterFunctionalTest.class);
	private HelicopterCharts helicopter;
	private String cities[] = {

	"Boston Heli", "Chicago Heli", "Detroit Heli", "Houston Heli",
			"Los Angeles Heli", "New York Heli", "Baltimore Washington Heli",
			"Dallas Ft. Worth Heli", "U.S Gulf Coast" };

	public HelicopterFunctionalTest () { 
		helicopter = new HelicopterCharts();
	}
	
	@Test
	public void testProductReleseDefault() {

		for (String city : cities) {
			ProductSet ps = (ProductSet) helicopter.getHelicopterRelease("", null, city).getEntity();
		
			if ((ps.getEdition() != null && !ps.getEdition().isEmpty() &&  ps.getEdition().get(0).getEditionDate() != null)
					&& (!ps.getEdition().get(0).getEditionDate().isEmpty())) {
				logger.info("Helicopter Product Relese test for 'Default parameters' return url of "
						+ ps.getEdition().get(0).getProduct().getUrl());
			assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0)
					.getProduct().getUrl()));
			}
		}
		
	}

	
	@Test
	public void testProductReleseCurrent() {
		helicopter = new HelicopterCharts();
		for (String city : cities) {
			ProductSet ps = (ProductSet) helicopter.getHelicopterRelease("current", "PDF", city).getEntity();
			logger.info("Helicopter Product Relese Test for 'current' return url of "
					+ ps.getEdition().get(0).getProduct().getUrl());
			if (ps.getEdition().get(0).getProduct().getUrl() != null) {

			assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0)
					.getProduct().getUrl()));
			}
		}
	}

	@Test
	public void testProductEditionDefault() {
		helicopter = new HelicopterCharts();
		for (String city : cities) {
			ProductSet ps = (ProductSet)  helicopter.getHelicopterEdition("", city).getEntity();
			logger.info("Helicopter Product Edition Test for 'Default parameters' return code "
					+ ps.getStatus().getCode().intValue());
			int code = ps.getStatus().getCode().intValue();
			assertEquals(code, 400);
		}
	}

	@Test
	public void testProductEditionCurrent() {
		helicopter = new HelicopterCharts();
		for (String city : cities) {
			ProductSet ps = (ProductSet) helicopter.getHelicopterEdition("current", city).getEntity();
			logger.info("Helicopter Product Edition Test for 'current' return code "
					+ ps.getStatus().getCode().intValue());
			int code = ps.getStatus().getCode().intValue();
			assertEquals(code, 200);
		}
	}

	// Negative tests
	@Test
	public void testProductEditionTypo() {
		helicopter = new HelicopterCharts();
		ProductSet ps = (ProductSet) helicopter.getHelicopterEdition("TYPO", "Boston Heli").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("Helicopter Product Edition Test for 'incorrect edition' return code "
				+ ps.getStatus().getCode().intValue());
		assertEquals(code, 400);
	}

	@Test
	public void testProductReleaseTypo() {
		helicopter = new HelicopterCharts();
		ProductSet ps = (ProductSet) helicopter.getHelicopterRelease("TYPO", "PDF", "Boston Heli").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("Helicopter Product Relese Test for 'incorrect Release' return code "
				+ ps.getStatus().getCode().intValue());
		assertEquals(code, 400);
	}

	@Test
	public void testProductEditionGeoTypo() {
		helicopter = new HelicopterCharts();
		ProductSet ps = (ProductSet) helicopter.getHelicopterEdition("", "Typo").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("Helicopter Product Edition Test for 'incorrect city' return code "
				+ ps.getStatus().getCode().intValue());
		assertEquals(code, 400);
	}

	@Test
	public void testProductReleaseGeoTypo() {
		helicopter = new HelicopterCharts();
		ProductSet ps = (ProductSet) helicopter.getHelicopterRelease("", "PDF", "Typo").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("Helicopter Product Release Test for 'incorrect city' return code "
				+ ps.getStatus().getCode().intValue());
		assertEquals(code, 400);
	}

	@Test
	public void testProductEditionTypoTypo() {
		helicopter = new HelicopterCharts();
		ProductSet ps = (ProductSet) helicopter.getHelicopterEdition("Typo", "Typo").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("Helicopter Product Edition Test for 'incorrect edition and Fmt' return code "
				+ ps.getStatus().getCode().intValue());
		assertEquals(code, 400);
	}

	@Test
	public void testProductReleaseFmtTypoTypo() {
		helicopter = new HelicopterCharts();
		ProductSet ps = (ProductSet) helicopter.getHelicopterRelease("Typo", "PDF", "Typo").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("Helicopter Product Release Test for 'incorrect Release and city' return code "
				+ ps.getStatus().getCode().intValue());
		assertEquals(code, 400);
	}

}
