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

import gov.faa.ait.apra.api.CIFP;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.cycle.ChartCycleClient;

public class CIFP2Test {
	private CIFP cifp;
	private static final Logger logger = LoggerFactory.getLogger(CIFP2Test.class);
	
	public CIFP2Test () {
		ChartCycleClient client = new ChartCycleClient();
		client.forceUpdate();
	}
	
	@Test 
	public void testCurrentRelease() {
		cifp = new CIFP();
		Response ps = cifp.getCIFPRelease("current");	
		int code = ps.getStatus();
		assertEquals(code, 200);
		ProductSet psEntity = (ProductSet) ps.getEntity();
		assertTrue(VerifyValues.verifyURL(psEntity.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testNextRelease() {
		cifp = new CIFP();
		Response ps = cifp.getCIFPRelease("next");	
		int code = ps.getStatus();
		
		// Have to allow for either a positive response or a not found due to the AJV release cycle. A "next" edition may
		// only be published 20 days in advance. Therefore, there is a time period when we may ask for "next", but it really hasn't
		// been put on the web site yet
		if (! (code == 200 || code == 404) ) {
			fail();
		}
	}
	
	@Test 
	public void testNullRelease() {
		cifp = new CIFP();
		ProductSet ps = (ProductSet) cifp.getCIFPRelease(null).getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 200);
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testFooRelease() {
		cifp = new CIFP();
		ProductSet ps = (ProductSet) cifp.getCIFPRelease("foo").getEntity();	
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 400);
	}	

	@Test 
	public void testDollarRelease() {
		cifp = new CIFP();
		ProductSet ps = (ProductSet) cifp.getCIFPEdition("$format").getEntity();	
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 400);
	}

	@Test 
	public void testWildcardRelease() {
		cifp = new CIFP();
		ProductSet ps = (ProductSet) cifp.getCIFPEdition("*").getEntity();	
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 400);
	}
	
	@Test 
	public void testSqlWildcardRelease() {
		cifp = new CIFP();
		ProductSet ps = (ProductSet) cifp.getCIFPEdition("%").getEntity();	
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 400);
	}
	
	@Test 
	public void testRandomJunkRelease() {
		cifp = new CIFP();
		ProductSet ps = (ProductSet) cifp.getCIFPEdition("%20*x%$(\\./").getEntity();	
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 400);
	}
	
	@Test
	public void testCurrentEdition() {
		cifp = new CIFP();
		ProductSet ps = (ProductSet) cifp.getCIFPEdition("current").getEntity();	
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 200);
	}	
	
	@Test
	public void testNextEdition() {
		cifp = new CIFP();
		ProductSet ps = (ProductSet) cifp.getCIFPEdition("next").getEntity();	
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 200);
	}	

	@Test 
	public void testNullEdition() {
		cifp = new CIFP();
		ProductSet ps = (ProductSet) cifp.getCIFPEdition(null).getEntity();	
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 200);
	}
	
	@Test 
	public void testFooEdition() {
		cifp = new CIFP();
		ProductSet ps = (ProductSet) cifp.getCIFPEdition("foo").getEntity();	
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 400);
	}

	@Test 
	public void testDollarEdition() {
		cifp = new CIFP();
		ProductSet ps = (ProductSet) cifp.getCIFPEdition("$format").getEntity();	
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 400);
	}

	@Test 
	public void testWildcardEdition() {
		cifp = new CIFP();
		ProductSet ps = (ProductSet) cifp.getCIFPEdition("*").getEntity();	
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 400);
	}
	
	@Test 
	public void testSqlWildcardEdition() {
		cifp = new CIFP();
		ProductSet ps = (ProductSet) cifp.getCIFPEdition("%").getEntity();	
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 400);
	}
	
	@Test 
	public void testRandomJunkEdition() {
		cifp = new CIFP();
		ProductSet ps = (ProductSet) cifp.getCIFPEdition("%2F*@#$(())@#*(@#$)").getEntity();	
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 400);
	}
	
}
