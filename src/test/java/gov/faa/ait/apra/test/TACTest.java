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

import gov.faa.ait.apra.api.TerminalAreaCharts;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.cycle.TACCycleClient;

public class TACTest {
	private TerminalAreaCharts tac;
	private final static Logger logger = LoggerFactory.getLogger(TACTest.class);

	public TACTest () {
		tac = new TerminalAreaCharts();
	}
	
	@Test 
	public void testBostonGeoChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "tiff",  "BOSTON").getEntity();
		logger.info("TAC Test for 'Tampa-Orlando' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testBostonChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("CURRENT",  "pdf",  "BOSTON").getEntity();
		logger.info("TAC Test for 'Tampa-Orlando' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}	
	
	@Test 
	public void testBWIChartGeo() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "tiff",  "Baltimore-Washington").getEntity();		
		logger.info("TAC Test for 'Baltimore-Washington' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testBWIChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "pdf",  "Baltimore-Washington").getEntity();		
		logger.info("TAC Test for 'Baltimore-Washington' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testDFWChartGeo() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "tiff",  "dallas-ft worth").getEntity();
		logger.info("TAC Test for 'dallas-ft worth' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testDFWGeoChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "pdf",  "dallas-ft worth").getEntity();
		logger.info("TAC Test for 'dallas-ft worth' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testMSPChartGeo() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "tiff",  "minneapolis-st paul").getEntity();
		logger.info("TAC Test for 'dallas-ft worth' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testMSPChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "pdf",  "minneapolis-st paul").getEntity();
		logger.info("TAC Test for 'dallas-ft worth' return url of "+ps.getEdition().get(0).getProduct().getUrl());
	}	

	@Test 
	public void testMSPChartGeoNext() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("next",  "tiff",  "minneapolis-st paul").getEntity();
		logger.info("TAC Test for 'minneapolis-st paul' return url of "+ps.getEdition().get(0).getProduct().getUrl());
	}
	
	@Test 
	public void testMSPChartNext() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("next",  "pdf",  "minneapolis-st paul").getEntity();
		logger.info("TAC Test for 'minneapolis-st paul' return url of "+ps.getEdition().get(0).getProduct().getUrl());
	}
	
	@Test 
	public void testKCChartGeo() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "tiff",  "kansas_city").getEntity();
		logger.info("TAC Test for 'kansas_city' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testKCChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "pdf",  "kansas city").getEntity();
		logger.info("TAC Test for 'kansas_city' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}	
	
	@Test 
	public void testLAXhartGeo() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "tiff",  "Los Angeles").getEntity();
		logger.info("TAC Test for 'Los Angeles' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testLAXChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "pdf",  "los angeles").getEntity();
		logger.info("TAC Test for 'los angeles' return url of "+ps.getEdition().get(0).getProduct().getUrl());
	}
	
	@Test 
	public void testVegashartGeo() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "tiff",  "las vegas").getEntity();
		logger.info("TAC Test for 'las vegas' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testVegasChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "pdf",  "Las Vegas").getEntity();
		logger.info("TAC Test for 'Las Vegas' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}

	@Test 
	public void testSLCGeo() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "tiff",  "salt lake city").getEntity();
		logger.info("TAC Test for 'salt lake city' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testSLCChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "pdf",  "salt_lake_city").getEntity();
		logger.info("TAC Test for 'salt_lake_city' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testSTLGeo() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "tiff",  "ST LOUIS").getEntity();
		logger.info("TAC Test for 'ST LOUIS' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testSTLChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "pdf",  "ST Louis").getEntity();
		logger.info("TAC Test for 'ST Louis' return url of "+ps.getEdition().get(0).getProduct().getUrl());
	}	
	
	@Test 
	public void testChicagoChartGeo() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "tiff",  "chiCago").getEntity();
		logger.info("TAC Test for 'chiCago' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testChicagoChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "pdf",  "Chicago").getEntity();
		logger.info("TAC Test for 'Chicago' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	// Start of the TAC special cases
	@Test 
	public void testPRGeoChartGeo() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "tiff",  "puerto Rico-VI").getEntity();
		logger.info("TAC Test for 'puerto Rico-IV' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}

	@Test 
	public void testPRChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "pdf",  "puerto rico-VI").getEntity();
		logger.info("TAC Test for 'puerto Rico-IV' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testDenverChartGeo() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "tiff",  "Denver-Colorado Springs").getEntity();
		logger.info("TAC Test for 'Denver' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}

	@Test 
	public void testDenverChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "pdf",  "Denver-Colorado Springs").getEntity();
		logger.info("TAC Test for 'Denver' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testAnchorageChartGeo() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "tiff",  "Anchorage-Fairbanks").getEntity();
		logger.info("TAC Test for 'Tampa-Orlando' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testAnchorageChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "pdf",  "Anchorage-Fairbanks").getEntity();
		logger.info("TAC Test for 'Anchorage-Fairbanks' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}	
	
	// General happy path stuff
	@Test 
	public void testBWI() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "pdf",  "Baltimore-Washington").getEntity();		
		logger.info("TAC Test for 'Baltimore-Washington' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testDFWChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "pdf",  "dallas-ft worth").getEntity();
		logger.info("TAC Test for 'dallas-ft worth' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}	
	
	@Test 
	public void testAnchorageLowerChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "pdf",  "anchorage-fairbanks").getEntity();
		logger.info("TAC Test for 'anchorage-fairbanks' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	// Test some null inputs to check for errors
	@Test 
	public void testTampaOrlandoChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "pdf",  "Tampa-Orlando").getEntity();
		logger.info("TAC Test for 'Tampa-Orlando' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}

	@Test 
	public void testNullsGeoChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease(null, null,  "Tampa-Orlando").getEntity();
		logger.info("TAC Test for 'Tampa-Orlando' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testNullsPDFChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current", null,  "Tampa-Orlando").getEntity();
		logger.info("TAC Test for 'Tampa-Orlando' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	@Test 
	public void testNullEditionChart2() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease(null,  "tiff",  "Tampa-Orlando").getEntity();
		logger.info("TAC Test for 'Tampa-Orlando' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
	
	// All the negative type of testing starts here
	@Test 
	public void testOmahaGeoChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "tiff",  "Omaha").getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 404);
	}
	
	@Test 
	public void testMispelledGeoChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("current",  "tiff",  "Charlote").getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 404);
	}	
	
	@Test 
	public void testFooEditionChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("foo",  "tiff",  "Omaha").getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 400);
	}
	
	@Test 
	public void testFooFormatChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("next",  "foo",  "Omaha").getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 400);
	}	
	
	@Test
	public void testNullFormatChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("next",  null,  "Omaha").getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 404);
	}
	
	@Test
	public void testNullEditionChart() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease(null, "pdf",  "Omaha").getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 404);
	}
	
	@Test
	public void testAllNullInputs() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease(null, null, null).getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 404);
	}
	
	@Test
	public void testWildcardInputs() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("*", "*", "*").getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 400);
	}
	
	@Test
	public void testJunkInputs() {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACRelease("@#$*(**#@", ")*#@$)*#@$)(*%@%$", "xsdkf928\\/242/%25").getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 400);
	}
	
	// Testing TAC Editions
	
	@Test 
	public void testDefaultEdition () {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACEdition(null,  "Chicago").getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 200);
	}
	
	@Test 
	public void testNamedEdition () {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACEdition("current",  "Baltimore-Washington").getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 200);
	}

	@Test 
	public void testNextEdition () {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACEdition("next",  "anchorage-fairbanks").getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 200);
	}
	
	@Test 
	public void testBadEdition () {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACEdition("bad",  "anchorage-fairbanks").getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 400);
	}	

	@Test 
	public void testNullGeonameEdition () {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACEdition("current",  null).getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 404);
	}
	
	@Test 
	public void testUnknownGeonameEdition () {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACEdition("current",  "Timbuktu").getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 404);
	}

	@Test 
	public void testNullsEdition () {
		tac = new TerminalAreaCharts();
		ProductSet ps = (ProductSet) tac.getTACEdition(null, null).getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 404);
	}
	
	
	// Test cycle client for complete coverage
	@Test 
	public void testCurrentEditionNumber () {
		TACCycleClient tcc = new TACCycleClient();
		String cycleNumber = tcc.getCurrentEditionNumber("Atlanta");
		logger.info("Atlanta current edition number is "+cycleNumber);
	}
	
	@Test 
	public void testNextEditionNumber () {
		TACCycleClient tcc = new TACCycleClient();
		String cycleNumber = tcc.getNextEditionNumber("Atlanta");
		logger.info("Atlanta next edition number is "+cycleNumber);
	}
	
	@Test 
	public void testCurrentEditionNumberForceUpdate () {
		TACCycleClient tcc = new TACCycleClient();
		tcc.getChartCycle(true);
		String cycleNumber = tcc.getCurrentEditionNumber("Atlanta");
		logger.info("Atlanta current edition number after forced update is "+cycleNumber);
	}
}
