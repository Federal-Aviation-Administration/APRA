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

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.api.IFREnrouteCharts;
import gov.faa.ait.apra.bootstrap.ErrorCodes;
import gov.faa.ait.apra.jaxb.ProductSet;

public class IFREnrouteFunctionalTest {
	private static final Logger logger = LoggerFactory
			.getLogger(IFREnrouteFunctionalTest.class);
	private static final String CURRENT = "CURRENT";
	private static final String TIFF = "TIFF";
	private static final String CARIBBEAN = "CARIBBEAN";
	private static final String LOW = "LOW";
	private IFREnrouteCharts enroute;
	
	private String editions[] = {"current"};
	private String formats[] = {"pdf", "tiff" };
	private String geos[] = {"us", "alaska" };
	private String alts[] = {"low", "high" };

	public IFREnrouteFunctionalTest () {
		enroute = new IFREnrouteCharts();
	}
	
	@Test
	public void testProductRelese() {

		for (String edition : editions) {
			for (String format : formats) {
				for (String geo : geos) {
					for (String alt : alts) {
						logger.info("Edition: current " + "format: " + format
								+ " geo: " + geo + " alt: " + alt);
						ProductSet ps = (ProductSet) enroute.getIFREnrouteRelease(edition,
								format, geo, alt).getEntity();
						int code = ps.getStatus().getCode().intValue();
						if (code == 200) {
							logger.info("IFREnroute Product Relese test return url  "
									+ ps.getEdition().get(0).getProduct()
											.getUrl());
							assertTrue(VerifyValues.verifyURL(ps.getEdition()
									.get(0).getProduct().getUrl()));
							
						} else {
							assertEquals(code, 404);
						}
					}
				}
			}
		}
	}
	
	@Test
	public void testBadSet() {
		enroute = new IFREnrouteCharts(); 
		Response resp = enroute.getIFREnrouteRelease(CURRENT, TIFF,  CARIBBEAN, LOW);
		assertEquals(404, resp.getStatus());
		ProductSet ps = (ProductSet) resp.getEntity();
		assertEquals(Integer.valueOf(404), ps.getStatus().getCode());
		assertEquals(ErrorCodes.ERROR_404, ps.getStatus().getMessage());
		assertEquals(0, ps.getEdition().size());
	}
	
	public void testProductedition() {

		enroute = new IFREnrouteCharts();
		for (String edition : editions) {
			for (String format : formats) {
				for (String geo : geos) {
					for (String alt : alts) {
						logger.info("Edition: current " + "format: " + format
								+ " geo: " + geo + " alt: " + alt);
						ProductSet ps = (ProductSet) enroute.getIFREnrouteEdition(edition).getEntity();
						int code = ps.getStatus().getCode().intValue();
						if (code == 200) {
							assertEquals(code, 200);
						} else {
							assertEquals(code, 404);
						}
					}
				}
			}
		}
	}

}
