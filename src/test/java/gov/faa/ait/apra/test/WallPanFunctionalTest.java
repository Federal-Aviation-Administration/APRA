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

import gov.faa.ait.apra.api.WallPlanningCharts;
import gov.faa.ait.apra.jaxb.ProductSet;

public class WallPanFunctionalTest {
	private static final  Logger logger = LoggerFactory.getLogger(WallPanFunctionalTest.class);
	private WallPlanningCharts wallPlan;

	public WallPanFunctionalTest () {
		wallPlan = new WallPlanningCharts();
	}
	
	@Test 
	public void testProductReleseDefault() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductRelease(null, null).getEntity();
		logger.info("WallPlan Product Relese test for 'Default parameters' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}
		
	@Test 
	public void testProductReleseCurrent() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductRelease("current", null).getEntity();
		logger.info("WallPlan Product Relese Test for 'current' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}	
	
	@Test 
	public void testProductReleseNoEditionwithFmtZip() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductRelease(null, "tiff").getEntity();
		logger.info("WallPlan Product Relese Test for 'No Edition with Fmt tiff' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}	
	
	@Test 
	public void testProductReleseNoEditionwithFmtPdf() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductRelease(null, "pdf").getEntity();
		logger.info("WallPlan Product Relese Test for 'No Edition with Fmt pdf' return url of "+ps.getEdition().get(0).getProduct().getUrl());
		assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
	}	
	

	@Test 
	public void testProductEditionDefault() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductEdition(null, null).getEntity();
		logger.info("WallPlan Product Edition Test for 'Default parameters' return code "+ps.getStatus().getCode().intValue());
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 200);
	}


	@Test 
	public void testProductEditionCurrent() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductEdition("current", null).getEntity();
		logger.info("WallPlan Product Edition Test for 'current' return code "+ps.getStatus().getCode().intValue());
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 200);
	}	
	
	@Test 
	public void testProductEditionNoEditionwithFmtZip() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductEdition(null, "tiff").getEntity();
		logger.info("WallPlan Product Edition Test for 'No Edition with Fmt Zip' return code "+ps.getStatus().getCode().intValue());
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 200);
	}	
	
	@Test 
	public void testProductEditionNoEditionwithFmtPdf() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductEdition(null, "pdf").getEntity();
		logger.info("WallPlan Product Edition Test for 'No Edition with Fmt pdf' return code "+ps.getStatus().getCode().intValue());
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 200);
	}

	@Test 
	public void testProductReleseNext() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductRelease("next", null).getEntity();
		logger.info("WallPlan Product Edition Test for 'Next' return code "+ps.getStatus().getCode().intValue());
		int code = ps.getStatus().getCode().intValue();
		if(code == 200){
			if((ps.getEdition().get(0).getEditionDate() != null) && (!ps.getEdition().get(0).getEditionDate().isEmpty())){
				logger.info("WallPlan Product Relese Test for 'Next' return url of "+ps.getEdition().get(0).getProduct().getUrl());
				assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
			}
		}else{
			assertEquals(code, 404);
		}
	}

	@Test 
	public void testProductReleseNextNoEditionwithFmtZip() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductRelease("next", "tiff").getEntity();
		logger.info("WallPlan Product Edition Test for 'Next with tiff' return code "+ps.getStatus().getCode().intValue());
		int code = ps.getStatus().getCode().intValue();
		if(code == 200){
		if((ps.getEdition().get(0).getEditionDate() != null) && (!ps.getEdition().get(0).getEditionDate().isEmpty())){
			logger.info("WallPlan Product Relese Test for 'Next with tiff' return url of "+ps.getEdition().get(0).getProduct().getUrl());
			assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
		}
		}else{
			assertEquals(code, 404);
		}
	}
	
	@Test 
	public void testProductReleseNextNoEditionwithFmtPdf() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductRelease("next", "pdf").getEntity();
		logger.info("WallPlan Product Edition Test for 'Next with pdf' return code "+ps.getStatus().getCode().intValue());
		int code = ps.getStatus().getCode().intValue();
		if(code == 200){
		if((ps.getEdition().get(0).getEditionDate() != null) && (!ps.getEdition().get(0).getEditionDate().isEmpty())){
			logger.info("WallPlan Product Relese Test for 'Next with pdf' return url of "+ps.getEdition().get(0).getProduct().getUrl());
			assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0).getProduct().getUrl()));
		}
		}else{
			assertEquals(code, 404);
		}
	}
	
	@Test 
	public void testProductEditionNext() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductEdition("next", null).getEntity();
		logger.info("WallPlan Product Edition Test for 'next' return code "+ps.getStatus().getCode().intValue());
		int code = ps.getStatus().getCode().intValue();
		logger.info("WallPlanFunctional Test product edition next with zip return code "+code);
		if(code == 200){
		if((ps.getEdition().get(0).getEditionDate() != null) && (!ps.getEdition().get(0).getEditionDate().isEmpty())){
			//logger.info("WallPlan Product Edition Test for 'Next ' return url of "+ps.getEdition().get(0).getProduct().getUrl());
			assertEquals(code, 200);
		}
		}else{
			assertEquals(code, 404);
		}
	}
	
	@Test 
	public void testProductEditionNextwithZip() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductEdition("next", "tiff").getEntity();
		logger.info("testProductEditionNextwithZip() WallPlan Product Edition Test for 'next with tiff' return code "+ps.getStatus().getCode().intValue());
		int code = ps.getStatus().getCode().intValue();
		
		logger.info("testProductEditionNextwithZip() WallPlanFunctional Test product edition next with zip return code "+code);
		if(code == 200){
			if((ps.getEdition().get(0).getEditionDate() != null) && (!ps.getEdition().get(0).getEditionDate().isEmpty())){
				//logger.info("testProductEditionNextwithZip() WallPlan Product Edition Test for 'next with tiff ' return url of "+ps.getEdition().get(0).getProduct().getUrl());
				assertEquals(code, 200);
			}
		}
		else{
			assertEquals(code, 404);
		}
	}	
	
	@Test 
	public void testProductEditionNextwithPdf() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductEdition("next", "pdf").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("testProductEditionNextwithPdf() WallPlan Product Edition Test for 'next with pdf' return code "+ps.getStatus().getCode().intValue());
		logger.info("testProductEditionNextwithPdf() WallPlanFunctional Test product edition next with zip return code "+code);
		if(code == 200){
		if((ps.getEdition().get(0).getEditionDate() != null) && (!ps.getEdition().get(0).getEditionDate().isEmpty())){
			//logger.info("testProductEditionNextwithPdf() WallPlan Product Edition Test for 'next with pdf ' return url of "+ps.getEdition().get(0).getProduct().getUrl());
			assertEquals(code, 200);
		}
		}else{
			assertEquals(code, 404);
		}
	}	
	
	
	// Negative tests
	
	@Test 
	public void testProductEditionTypo() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductEdition("TYPO", null).getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("WallPlan Product Edition Test for 'incorrect edition' return code "+ps.getStatus().getCode().intValue());
		assertEquals(code, 400);
	}	
	
	@Test 
	public void testProductReleaseTypo() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductRelease("TYPO", null).getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("WallPlan Product Relese Test for 'incorrect Release' return code "+ps.getStatus().getCode().intValue());
		assertEquals(code, 400);
	}	
	
	@Test 
	public void testProductEditionFmtTypo() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductEdition(null, "Typo").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("WallPlan Product Edition Test for 'incorrect Fmt' return code "+ps.getStatus().getCode().intValue());
		assertEquals(code, 400);
	}	
	
	@Test 
	public void testProductReleaseFmtTypo() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductRelease(null, "Typo").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("WallPlan Product Release Test for 'incorrect Fmt' return code "+ps.getStatus().getCode().intValue());
		assertEquals(code, 400);
	}
	
	@Test 
	public void testProductEditionTypoTypo() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductEdition("Typo", "Typo").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("WallPlan Product Edition Test for 'incorrect edition and Fmt' return code "+ps.getStatus().getCode().intValue());
		assertEquals(code, 400);
	}	
	
	@Test 
	public void testProductReleaseFmtTypoTypo() {
		wallPlan = new WallPlanningCharts();
		ProductSet ps = (ProductSet) wallPlan.getProductRelease("Typo", "Typo").getEntity();
		int code = ps.getStatus().getCode().intValue();
		logger.info("WallPlan Product Release Test for 'incorrect Release and Fmt' return code "+ps.getStatus().getCode().intValue());
		assertEquals(code, 400);
	}	

}
