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

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.api.DigitalEnrouteCharts;
import gov.faa.ait.apra.cycle.ChartCycleClient;
import gov.faa.ait.apra.jaxb.ProductSet;

public class DECTest {
	private DigitalEnrouteCharts dec;
	private static final Logger logger = LoggerFactory.getLogger(DECTest.class);
	
	public DECTest () {
		ChartCycleClient client = new ChartCycleClient();
		client.forceUpdate();
	}
	
	@Test 
	public void testCurrentRelease() {
		dec = new DigitalEnrouteCharts();
		Response ps = dec.getDECRelease("current");	
		int code = ps.getStatus();
		assertEquals(code, 200);
		ProductSet psEntity = (ProductSet) ps.getEntity();
		assertTrue(VerifyValues.verifyURL(psEntity.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testNextRelease() {
		dec = new DigitalEnrouteCharts();
		Response ps = dec.getDECRelease("next");	
		int code = ps.getStatus();
		
		// Have to allow for either a positive response or a not found due to the AJV release cycle. A "next" edition may
		// only be published 20 days in advance. Therefore, there is a time period when we may ask for "next", but it really hasn't
		// been put on the web site yet
		if (! (code == 200 || code == 404) ) {
			fail();
		}
	}
}
