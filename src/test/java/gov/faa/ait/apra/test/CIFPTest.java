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

import gov.faa.ait.apra.api.BaseService;
import gov.faa.ait.apra.api.CIFP;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.cycle.ChartCycleClient;
import gov.faa.ait.apra.cycle.ChartCycleData;

@RunWith(Parameterized.class)
public class CIFPTest {
	private Date releaseDate = null;
	
	private final static Logger logger = LoggerFactory.getLogger(CIFPTest.class);
	private ChartCycleClient client;
	private CIFP cifp;
	
	public CIFPTest (Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.clear();
		cal.set(2015,  5, 1);
		this.releaseDate = cal.getTime();
	}
	
	@Before
	public void initialize() {
		client = new ChartCycleClient();
		cifp = new CIFP();
	}
	
	@Parameterized.Parameters
	public static Collection<Date> cycleNumbers () {
		Date [] params = new Date [10];	

			GregorianCalendar cal = new GregorianCalendar();
			cal.clear();
			cal.set(2015, 0, 01);
			
			params[0] = cal.getTime();
			for (int i = 1; i < 6; i++) {		
				cal.add(Calendar.DATE, 56);
				params[i] = cal.getTime();
			}
			
			cal.set(2016,  0, 7);
			params[6] = cal.getTime();
			cal.set(2016, 1, 4);
			params[7] = cal.getTime();
			cal.set (2016, 2, 3);
			params[8] = cal.getTime();
			cal.set(2016,  2, 31);
			params[9] = cal.getTime();


		return Arrays.asList(params);
	}
	
	@Test
	public void testDownloadOperations() {
		ChartCycleData cycle = client.getChartCycle(releaseDate, true);
		if (logger.isDebugEnabled()) {
			logger.debug("Reloaded cycle "+cycle.getName());
		}
		
		cifp.setFormat(BaseService.ZIP);
		cifp.setEdition(BaseService.CURRENT);
		
		ProductSet current = cifp.getRelease(client.getCurrent28DayCycle());	
		
		if (current.getEdition().size() > 0)
			logger.info(current.getEdition().get(0).getProduct().getUrl());
		
		
		ProductSet next = cifp.getRelease(client.getNext28DayCycle());
		if (next.getEdition().size() > 0)
			logger.info(next.getEdition().get(0).getProduct().getUrl());
	}
	
	@Test
	public void testEditionOperations() {
		ChartCycleData cycle = client.getChartCycle(releaseDate, true);
		if (logger.isDebugEnabled()) {
			logger.debug("Reloaded cycle "+cycle.getName());
		}
		
		cifp.setFormat(BaseService.ZIP);
		cifp.setEdition(BaseService.CURRENT);		
		ProductSet current = cifp.getEdition(client.getCurrent28DayCycle());
		assertEquals(new Integer(current.getStatus().getCode()), Integer.valueOf(200));	
		
		ProductSet next = cifp.getEdition(client.getNext28DayCycle());
		assertEquals(new Integer(next.getStatus().getCode()), Integer.valueOf(200));
	}	
	
}
