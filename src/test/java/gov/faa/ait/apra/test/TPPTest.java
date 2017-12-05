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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.api.TerminalProcedureCharts;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.cycle.ChartCycleClient;
import gov.faa.ait.apra.cycle.ChartCycleData;

public class TPPTest {
	private static boolean setupComplete = false; 
	private TerminalProcedureCharts tpp;
	private final static Logger logger = LoggerFactory.getLogger(TPPTest.class);

	public TPPTest () {
		TPPTest.setup();
		tpp = new TerminalProcedureCharts();
	}
	
	private static void setup () {
		if (TPPTest.setupComplete) {
			return;
		}
		ChartCycleClient client = new ChartCycleClient();
		ChartCycleData cycle = client.getChartCycle(new Date (System.currentTimeMillis()), true);
		logger.info("Updated chart cycle in prep for TPP tests "+cycle.getName());
		TPPTest.setupComplete = true;
	}
	
	@Test 
	public  void testRelease() {
		ProductSet ps = (ProductSet) tpp.getTPPRelease("current",  "US").getEntity();
		
		for (int i = 0; i < 5; i++) {
			logger.info("TPP test for entire US digital product set "+ps.getEdition().get(i).getProduct().getUrl());
			assertTrue(VerifyValues.verifyURL(ps.getEdition().get(i).getProduct().getUrl()));
		}
	}

	@Test 
	public  void testNextRelease() {
		tpp = new TerminalProcedureCharts();
		ProductSet ps = (ProductSet) tpp.getTPPRelease("next",  "US").getEntity();
		
		for (int i = 0; i < 5; i++) {
			logger.info("TPP test for entire US digital product set "+ps.getEdition().get(i).getProduct().getUrl());
		}
	}
	
	@Test 
	public  void testAlaskaRelease() {
		logger.info("Start testAlaskaRelease()");
		ProductSet ps = (ProductSet) tpp.getTPPRelease("current",  "Alaska").getEntity();
		assertNotNull(ps.getEdition());
		logger.info("Alaska has "+ps.getEdition().size()+" charts as a response.");
		if (ps.getEdition().size() < 1)
			fail();
	}
	
	@Test 
	public void testUSChangesetRelease() {
		ProductSet ps = (ProductSet) tpp.getTPPRelease("changeset",  "US").getEntity();
		assertNotNull(ps.getEdition());
		logger.info("US has "+ps.getEdition().size()+" changed charts as a response.");
		if (ps.getEdition().size() < 1)
			fail();
	}
	
	@Test 
	public  void testNewYorkRelease() {
		logger.info("Start testNewYorkRelease()");
		ProductSet ps = (ProductSet) tpp.getTPPRelease("current",  "new york").getEntity();
		assertNotNull(ps.getEdition());
		logger.info("new york has "+ps.getEdition().size()+" charts as a response.");
		if (ps.getEdition().size() < 1)
			fail();
	}	

	@Test 
	public  void testCaliforniaRelease() {
		logger.info("Start testCaliforniaRelease()");
		ProductSet ps = (ProductSet) tpp.getTPPRelease("current",  "california").getEntity();
		assertNotNull(ps.getEdition());
		logger.info("California has "+ps.getEdition().size()+" charts as a response.");
		if (ps.getEdition().size() < 1)
			fail();
	}
	

	@Test 
	public  void testCaliforniaChangeRelease() {
		logger.info("Start testCaliforniaChangeRelease()");
		ProductSet ps = (ProductSet) tpp.getTPPRelease("changeset",  "california").getEntity();
		assertNotNull(ps.getEdition());
		logger.info("California change set has "+ps.getEdition().size()+" charts as a response.");
		if (ps.getEdition().size() < 1)
			fail();
	}
	
	@Test 
	public  void testMixedCaseRelease() {
		logger.info("Start testMixedCasekRelease()");
		ProductSet ps = (ProductSet) tpp.getTPPRelease("current",  "neW yOrk").getEntity();
		assertNotNull(ps.getEdition());
		logger.info("neW yOrk has "+ps.getEdition().size()+" charts as a response.");
		if (ps.getEdition().size() < 1) {
			logger.info(ps.toString());
			fail();
		}
	}	
	
	@Test 
	public void testAmpersandStateRelease() {
		ProductSet ps = (ProductSet) tpp.getTPPRelease("current",  "&nebraska").getEntity();
		assertNotNull(ps.getEdition());
		if (ps.getEdition().size() > 0)
			fail();
	}	

	@Test 
	public void testJunkStateRelease() {
		ProductSet ps = (ProductSet) tpp.getTPPRelease("current",  "@*?$(*@#$*))=@#&foo=$%20+\\&").getEntity();
		assertNotNull(ps.getEdition());
		if (ps.getEdition().size() > 0)
			fail();
	}
	
	@Test 
	public  void testPlusSignStateRelease() {
		ProductSet ps = (ProductSet) tpp.getTPPRelease("current",  "+california").getEntity();
		assertNotNull(ps.getEdition());
		if (ps.getEdition().size() > 0)
			fail();
	}	

	@Test 
	public  void testEncodedStateRelease() {
		ProductSet ps = (ProductSet) tpp.getTPPRelease("current",  "&+california++").getEntity();
		assertNotNull(ps.getEdition());
		logger.info("&+california++ has "+ps.getEdition().size()+" charts as a response.");
		if (ps.getEdition().size() > 0)
			fail();
	}
	
	@Test 
	public  void testReleaseDefaultValues() {
		tpp = new TerminalProcedureCharts();
		ProductSet ps = (ProductSet) tpp.getTPPRelease(null, null).getEntity();
		
		for (int i = 0; i < 5; i++) {
			logger.info("TPP test for entire US digital product set "+ps.getEdition().get(i).getProduct().getUrl());
			assertTrue(VerifyValues.verifyURL(ps.getEdition().get(i).getProduct().getUrl()));
		}
	}	
	
	@Test
	public  void testReleaseBadGeoname () {
		tpp = new TerminalProcedureCharts();
		ProductSet ps = (ProductSet) tpp.getTPPRelease(null, "BAD").getEntity();		
		assertEquals (400, ps.getStatus().getCode().intValue());
	}
	
	@Test
	public  void testReleaseFubarNext () {
		tpp = new TerminalProcedureCharts();
		ProductSet ps = (ProductSet) tpp.getTPPRelease("next", "fubar").getEntity();		
		assertEquals (400, ps.getStatus().getCode().intValue());
	}

	@Test
	public  void testReleaseFubarCurrent () {
		tpp = new TerminalProcedureCharts();
		ProductSet ps = (ProductSet) tpp.getTPPRelease("current", "fubar").getEntity();		
		assertEquals (400, ps.getStatus().getCode().intValue());
	}
	
	@Test
	public void testReleaseBadEdition () {
		tpp = new TerminalProcedureCharts();
		ProductSet ps = (ProductSet) tpp.getTPPRelease("NoSuchEdition", "US").getEntity();		
		assertEquals (400, ps.getStatus().getCode().intValue());
	}
	
	@Test 
	public  void testEdition() {
		tpp = new TerminalProcedureCharts();
		ProductSet ps = (ProductSet) tpp.getTPPEdition("current",  "US").getEntity();
		assertEquals(200, ps.getStatus().getCode().intValue());
	}

	@Test 
	public  void testNextEdition() {
		tpp = new TerminalProcedureCharts();
		ProductSet ps = (ProductSet) tpp.getTPPEdition("next",  "US").getEntity();
		assertEquals(200, ps.getStatus().getCode().intValue());
	}
	
	@Test 
	public  void testAlaskaEdition() {
		tpp = new TerminalProcedureCharts();
		ProductSet ps = (ProductSet) tpp.getTPPEdition("current",  "Alaska").getEntity();
		assertEquals(200, ps.getStatus().getCode().intValue());
	}
	
	@Test 
	public  void testAlaskaNextEdition() {
		tpp = new TerminalProcedureCharts();
		ProductSet ps = (ProductSet) tpp.getTPPEdition("next",  "alaska").getEntity();
		assertEquals(200, ps.getStatus().getCode().intValue());
	}

	@Test 
	public void testNewYorkEdition() {
		tpp = new TerminalProcedureCharts();
		ProductSet ps = (ProductSet) tpp.getTPPEdition("current",  "neW York").getEntity();
		assertEquals(200, ps.getStatus().getCode().intValue());
	}
	
	@Test 
	public void testNewYorkNextEdition() {
		tpp = new TerminalProcedureCharts();
		ProductSet ps = (ProductSet) tpp.getTPPEdition("next",  "new york").getEntity();
		assertEquals(200, ps.getStatus().getCode().intValue());
	}
	
	@Test 
	public void testCamelcaseEdition() {
		tpp = new TerminalProcedureCharts();
		ProductSet ps = (ProductSet) tpp.getTPPEdition("next",  "NewYork").getEntity();
		assertEquals(400, ps.getStatus().getCode().intValue());
	}
	
	@Test 
	public void testEditionDefaultValues() {
		tpp = new TerminalProcedureCharts();
		ProductSet ps = (ProductSet) tpp.getTPPEdition(null, null).getEntity();
		assertEquals(200, ps.getStatus().getCode().intValue());
	}	
	
	@Test
	public void testEditionBadGeoname () {
		tpp = new TerminalProcedureCharts();
		ProductSet ps = (ProductSet) tpp.getTPPEdition("current",  "BAD").getEntity();
		assertEquals(400, ps.getStatus().getCode().intValue());		
	}

	@Test
	public void testEditionBadEdition () {
		tpp = new TerminalProcedureCharts();
		ProductSet ps = (ProductSet) tpp.getTPPEdition("NoSuchEdition", null).getEntity();
		assertEquals(400, ps.getStatus().getCode().intValue());		
	}

}
