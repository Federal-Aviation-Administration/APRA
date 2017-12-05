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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import gov.faa.ait.apra.cycle.ChartCycleClient;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;

@RunWith(Parameterized.class)
public class ChartCycleTest {
	private Date checkDate;
	private Integer expectedCycle;
	private ChartCycleClient client;
	
	public ChartCycleTest (Date date, Integer cycle) {
		this.checkDate = new Date(date.getTime());
		this.expectedCycle = cycle;
	}
	
	@Before
	public void initialize() {
		client = new ChartCycleClient();
	}
	
	@Parameterized.Parameters
	public static List<Object[]> cycleNumbers () {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Object [] [] params = null;
		
		try { 
			params = new Object [] [] {
				{formatter.parse("06/17/2016"), Integer.valueOf(3)},
				{formatter.parse("10/13/2016"), Integer.valueOf(5)},
				{formatter.parse("11/10/2016"), Integer.valueOf(6)},
				{formatter.parse("01/01/2017"), Integer.valueOf(6)},
				{formatter.parse("12/25/2016"), Integer.valueOf(6)},
				{formatter.parse("01/05/2017"), Integer.valueOf(1)},
				{formatter.parse("02/03/2017"), Integer.valueOf(1)},
				{formatter.parse("06/14/2017"), Integer.valueOf(3)},
				{formatter.parse("09/15/2017"), Integer.valueOf(5)},
				{formatter.parse("11/09/2017"), Integer.valueOf(6)}
			};
		}
		catch (ParseException e) {
			params = new Object [] [] {
				{new Date(System.currentTimeMillis()), Integer.valueOf(1) }
			};
		}

		return Arrays.asList(params);
	}
	
	@Test
	public void testChartCycle() {
		client.getChartCycle(checkDate, true);
		String cycleNumber = client.getCurrent56DayCycle().getChart_cycle_number();
		assertEquals (expectedCycle.intValue(), Integer.parseInt(cycleNumber));	
	}
	
	@Test
	public void getCurrent28DayCycle () {
		ChartCycleElementsJson cc = client.getCurrent28DayCycle();
		assertNotNull(cc);
	}
	
	@Test
	public void getNext28DayCycle () {
		ChartCycleElementsJson cc = client.getNext28DayCycle();
		assertNotNull(cc);
	}
	
	@Test
	public void getCurrent56DayCycle () {
		ChartCycleElementsJson cc = client.getCurrent56DayCycle();
		assertNotNull(cc);
	}
	
	@Test
	public void getNext56DayCycle () {
		ChartCycleElementsJson cc = client.getNext56DayCycle();
		assertNotNull(cc);
	}
}
