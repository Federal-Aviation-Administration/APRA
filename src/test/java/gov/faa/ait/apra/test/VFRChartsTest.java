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
import static org.junit.Assert.fail;
import gov.faa.ait.apra.api.VFRCharts;
import gov.faa.ait.apra.jaxb.ProductSet;


import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public class VFRChartsTest {
	@SuppressWarnings("unused")
	private Date releaseDate = null;
	private  static final Logger logger = LoggerFactory.getLogger(VFRChartsTest.class);
	private VFRCharts vfr;

	public VFRChartsTest (Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.clear();
		cal.set(2015,  5, 1);
		releaseDate = cal.getTime();
	}
/**
 * initialize
 */
	@Before
	public void initialize() {
		vfr = new VFRCharts();
	}
/**
 * cycleNumbers
 * @return
 */
	@Parameterized.Parameters
	public static Collection<Date> cycleNumbers() {
		Date[] params = new Date[10];

		GregorianCalendar cal = new GregorianCalendar();
		cal.clear();
		cal.set(2015, 12, 11);

		params[0] = cal.getTime();

		for (int i = 1; i < 6; i++) {
			cal.add(Calendar.DATE, 56);
			params[i] = cal.getTime();
		}

		cal.set(2016, 0, 7);
		params[6] = cal.getTime();
		cal.set(2016, 1, 4);
		params[7] = cal.getTime();
		cal.set(2016, 2, 3);
		params[8] = cal.getTime();
		cal.set(2016, 2, 31);
		params[9] = cal.getTime();

		return Arrays.asList(params);
	}

	/**
	 * testDownloadOperations
	 */
	@Test
	public void testDownloadOperations() {
		
		ProductSet current = (ProductSet) vfr.getGrandCanyonRelease("current").getEntity();
		if (current.getEdition().size() > 0)
			logger.info(current.getEdition().get(0).getProduct().getUrl());
		assertEquals(Integer.valueOf(current.getStatus().getCode()), 
				Integer.valueOf(200));
		ProductSet next = (ProductSet) vfr.getGrandCanyonRelease("next").getEntity();
		if (next.getEdition().size() > 0)
			logger.info(next.getEdition().get(0).getProduct().getUrl());
		assertEquals(Integer.valueOf(next.getStatus().getCode()), Integer.valueOf(404));
	}

	/**
	 * testEditionOperations
	*/
	@Test
	public void testEditionOperations() {
		ProductSet current = (ProductSet) vfr.getGrandCanyonEdition("current").getEntity();
		logger.info("Grand canyon edition current returned HTTP status code "+current.getStatus().getCode());
		assertEquals(new Integer(200), new Integer(current.getStatus().getCode()));

		ProductSet next = (ProductSet) vfr.getGrandCanyonEdition("Next").getEntity();
		logger.info("Grand canyon edition next returned HTTP status code "+current.getStatus().getCode());
		
		int code = next.getStatus().getCode().intValue();
		
		switch (code) {
			case 200: assertEquals(Integer.valueOf(200), Integer.valueOf(next.getStatus().getCode()));
				break;
			case 404: assertEquals(Integer.valueOf(404), Integer.valueOf(next.getStatus().getCode()));
				break;
			
			default:
				fail();
		}
		
	}

}
