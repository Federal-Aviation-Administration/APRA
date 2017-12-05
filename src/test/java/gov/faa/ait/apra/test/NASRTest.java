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

import gov.faa.ait.apra.api.NASRSubscription;
import gov.faa.ait.apra.jaxb.ProductSet;

public class NASRTest {
	private final static Logger logger = LoggerFactory.getLogger(NASRTest.class);

	@Test 
	public void testDefaults() {
		NASRSubscription nasr = new NASRSubscription();
		ProductSet ps = (ProductSet) nasr.getNASRSubscription(null).getEntity();
		logger.info("NASR default test return url of "+ps.getEdition().get(0).getProduct().getUrl());
		int code = ps.getStatus().getCode().intValue();
		assertEquals(200, code);
	}
	
	@Test 
	public void testCurrentRelease() {
		NASRSubscription nasr = new NASRSubscription();
		ProductSet ps = (ProductSet) nasr.getNASRSubscription("current").getEntity();
		logger.info("NASR default test return url of "+ps.getEdition().get(0).getProduct().getUrl());
		int code = ps.getStatus().getCode().intValue();
		assertEquals(200, code);
	}
	
	@Test 
	public void testNextRelease() {
		NASRSubscription nasr = new NASRSubscription();
		ProductSet ps = (ProductSet) nasr.getNASRSubscription("next").getEntity();
		logger.info("NASR default test return url of "+ps.getEdition().get(0).getProduct().getUrl());
	}

	@Test 
	public void testDefaultEdition() {
		NASRSubscription nasr = new NASRSubscription();
		ProductSet ps = (ProductSet) nasr.getNASREdition(null).getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(200, code);
	}
	
	@Test 
	public void testCurrentEdition() {
		NASRSubscription nasr = new NASRSubscription();
		ProductSet ps = (ProductSet) nasr.getNASREdition("current").getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(200, code);
	}
	
	@Test 
	public void testNextEdition() {
		NASRSubscription nasr = new NASRSubscription();
		ProductSet ps = (ProductSet) nasr.getNASRSubscription("next").getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(200, code);
	}
	
	@Test 
	public void testInvalidEdition() {
		NASRSubscription nasr = new NASRSubscription();
		ProductSet ps = (ProductSet) nasr.getNASRSubscription("Invalid").getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(400, code);
	}
}
