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
import gov.faa.ait.apra.api.SupplementCharts;
import gov.faa.ait.apra.jaxb.ProductSet;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * test urls
 * http://localhost:8080/apra/supplement/info?edition=current&volume=NORTHWEST
 * http://localhost:8080/apra/supplement/info?edition=current&volume=SOUTHWEST
 * http://localhost:8080/apra/supplement/info?edition=current&volume=NORTH
 * CENTRAL
 * http://localhost:8080/apra/supplement/info?edition=current&volume=SOUTH
 * CENTRAL
 * http://localhost:8080/apra/supplement/info?edition=current&volume=EAST
 * CENTRAL
 * http://localhost:8080/apra/supplement/info?edition=current&volume=SOUTHEAST
 * http://localhost:8080/apra/supplement/info?edition=current&volume=NORTHEAST
 * http://localhost:8080/apra/supplement/info?edition=current&volume=PACIFIC
 * http://localhost:8080/apra/supplement/info?edition=current&volume=ALASKA
 * http://localhost:8080/apra/supplement/info?edition=next&volume=NORTHWEST
 * http://localhost:8080/apra/supplement/info?edition=next&volume=SOUTHWEST
 * http://localhost:8080/apra/supplement/info?edition=next&volume=NORTH CENTRAL
 * http://localhost:8080/apra/supplement/info?edition=next&volume=SOUTH CENTRAL
 * http://localhost:8080/apra/supplement/info?edition=next&volume=EAST CENTRAL
 * http://localhost:8080/apra/supplement/info?edition=next&volume=SOUTHEAST
 * http://localhost:8080/apra/supplement/info?edition=next&volume=NORTHEAST
 * http://localhost:8080/apra/supplement/info?edition=next&volume=PACIFIC
 * http://localhost:8080/apra/supplement/info?edition=next&volume=ALASKA
 * 
 * @author FAA
 *
 */
public class SupplementFunctionalTest {
	private static final Logger logger = LoggerFactory
			.getLogger(SupplementFunctionalTest.class);
	private SupplementCharts supplement;
	private String cities[] = { "NORTHWEST", "SOUTHWEST", "NORTH CENTRAL",
			"SOUTH CENTRAL", "EAST CENTRAL", "SOUTHEAST", "NORTHEAST",
			"PACIFIC", "ALASKA" };

	public SupplementFunctionalTest () {
		supplement = new SupplementCharts();
	}
	
	@Test
	public void testProductReleseDefault() {

		for (String city : cities) {
			ProductSet ps = (ProductSet) supplement.getSupplementRelease("", city).getEntity();

			if ((ps.getEdition() != null && !ps.getEdition().isEmpty() && ps
					.getEdition().get(0).getEditionDate() != null)
					&& (!ps.getEdition().get(0).getEditionDate().isEmpty())) {
				logger.info("SupplementCharts Product Relese test for 'Default parameters' return url of "
						+ ps.getEdition().get(0).getProduct().getUrl());
				assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0)
						.getProduct().getUrl()));
			}
		}

	}

	@Test
	public void testProductEditionDefault() {
		supplement = new SupplementCharts();
		for (String city : cities) {
			ProductSet ps = (ProductSet) supplement.getSupplementEdition("", city).getEntity();
			logger.info("Helicopter Product Edition Test for 'Default parameters' return code "
					+ ps.getStatus().getCode().intValue());
			int code = ps.getStatus().getCode().intValue();
			assertEquals(code, 200);
		}
	}

	@Test
	public void testProductReleseDefaultUS() {

		supplement = new SupplementCharts();
		ProductSet ps = (ProductSet) supplement.getSupplementRelease("", "").getEntity();

		if ((ps.getEdition() != null && !ps.getEdition().isEmpty() && ps
				.getEdition().get(0).getEditionDate() != null)
				&& (!ps.getEdition().get(0).getEditionDate().isEmpty())) {
			logger.info("SupplementCharts Product Relese test for 'Default parameters' return url of "
					+ ps.getEdition().get(0).getProduct().getUrl());
			assertTrue(VerifyValues.verifyURL(ps.getEdition().get(0)
					.getProduct().getUrl()));
		}

	}

	@Test
	public void testProductEditionDefaultUS() {
		supplement = new SupplementCharts();
		ProductSet ps = (ProductSet) supplement.getSupplementEdition("", "").getEntity();
		logger.info("Helicopter Product Edition Test for 'Default parameters' return code "
				+ ps.getStatus().getCode().intValue());
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 200);
	}

	@Test
	public void testProductReleseVOLTypo() {

		supplement = new SupplementCharts();
		ProductSet ps = (ProductSet) supplement.getSupplementRelease("", "Typo").getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 404);
	}

	@Test
	public void testProductEditionVOLTypo() {
		supplement = new SupplementCharts();
		ProductSet ps = (ProductSet) supplement.getSupplementEdition("", "typo").getEntity();
		logger.info("Helicopter Product Edition Test for 'Default parameters' return code "
				+ ps.getStatus().getCode().intValue());
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 404);
	}

	@Test
	public void testProductReleseEDTypo() {

		supplement = new SupplementCharts();
		ProductSet ps = (ProductSet) supplement.getSupplementRelease("TYPO", "Typo").getEntity();
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 400);
	}

	@Test
	public void testProductEditionEDTypo() {
		supplement = new SupplementCharts();
		ProductSet ps = (ProductSet) supplement.getSupplementEdition("TYPO", "typo").getEntity();
		logger.info("Helicopter Product Edition Test for 'Default parameters' return code "
				+ ps.getStatus().getCode().intValue());
		int code = ps.getStatus().getCode().intValue();
		assertEquals(code, 400);
	}
}
