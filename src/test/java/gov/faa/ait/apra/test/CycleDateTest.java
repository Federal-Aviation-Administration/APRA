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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.util.CycleDateUtil;

public class CycleDateTest {
	private static final Logger logger = LoggerFactory.getLogger(CycleDateTest.class);

	@Test
	public void get56Day2CyclesAhead () {
		CycleDateUtil cdu = new CycleDateUtil();
		Date d = cdu.get56DayCycleDate(2);
		logger.info("Next 56 day cycle date is "+cdu.getNext56Day().toString());
		logger.info("Next after next 56 day cycle date is "+d.toString());
	}
	
	@Test
	public void getBeforeCurrent56 () {
		CycleDateUtil cdu = new CycleDateUtil();
		Date d = cdu.getPrevious56Day();
		logger.info("Current 56 day cycle date is "+cdu.getCurrent56Day().toString());
		logger.info("Cycle before current 56 day cycle date is "+d.toString());
	}
	
	@Test
	public void get28Day2CyclesAhead () {
		CycleDateUtil cdu = new CycleDateUtil();
		Date d = cdu.get28DayCycleDate(2);
		logger.info("Next 28 day cycle date is "+cdu.getNext28Day().toString());
		logger.info("Next after next 28 day cycle date is "+d.toString());
	}
	
	@Test
	public void getBeforeCurrent28 () {
		CycleDateUtil cdu = new CycleDateUtil();
		Date d = cdu.getPrevious28Day();
		logger.info("Current 28 day cycle date is "+cdu.getCurrent28Day().toString());
		logger.info("Cycle before current 28 day cycle date is "+d.toString());
	}
	
	@Test
	public void getDECCycle1 () {
		GregorianCalendar epoch = new GregorianCalendar(TimeZone.getDefault());

		// Set the epoch to September 20, 2016 04:00:00
		epoch.set(2016, 8, 20, 4, 0, 0);		
		int cycle = CycleDateUtil.getDECCycleNumber(epoch);
		
		logger.info("Calcualted cycle number "+cycle+" for date 09/20/2016");
		assertEquals(cycle, 33);
	}
	
	@Test
	public void getDECCycle2 () {
		GregorianCalendar epoch = new GregorianCalendar(TimeZone.getDefault());

		// Set the epoch to December 25, 2016 04:00:00
		epoch.set(2016, 11, 25, 4, 0, 0);		
		int cycle = CycleDateUtil.getDECCycleNumber(epoch);
		
		logger.info("Calcualted cycle number "+cycle+" for date 12/25/2016");
		
		assertEquals(cycle, 34);
	}
	
	@Test
	public void getDECCycle3 () {
		GregorianCalendar epoch = new GregorianCalendar(TimeZone.getDefault());

		// Set the epoch to March 30, 2018 04:00:00
		epoch.set(2018, 2, 30, 4, 0, 0);		
		int cycle = CycleDateUtil.getDECCycleNumber(epoch);
		logger.info("Calcualted cycle number "+cycle+" for date 03/30/2018");
		
		assertEquals(cycle, 43);
	}
	
	@Test
	public void getDECCycle4 () {
		GregorianCalendar epoch = new GregorianCalendar(TimeZone.getDefault());

		// Set the epoch to June 22, 2017 04:00:00
		epoch.set(2017, 5, 22, 4, 0, 0);		
		int cycle = CycleDateUtil.getDECCycleNumber(epoch);
		logger.info("Calcualted cycle number "+cycle+" for date 06/22/2017");
		
		assertEquals(cycle, 38);
	}
}
