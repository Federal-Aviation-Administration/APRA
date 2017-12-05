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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.cycle.ChartCycleElementsJson;
import gov.faa.ait.apra.cycle.ChartCycleData;
import gov.faa.ait.apra.util.ChartInfoTable;
import gov.faa.ait.apra.util.ChartInfoTableKey;
import gov.faa.ait.apra.util.TableChartClient;

@RunWith(Parameterized.class)
public class SectionalChartClientTest {

	//private static final Logger logger = LoggerFactory.getLogger(SectionalChartClientTest.class);

	//private static final String CURRENT_CODE = "CURRENT";
	
	private TableChartClient client;
	private Date targetDate;
	private String cityKey;
	private String editionKey;
	private String typeKey;
	private Integer expectedVersion;
	
	public SectionalChartClientTest(Date targetDate, String city, String edition, String type, Integer cycle) {
		this.targetDate = new Date(targetDate.getTime());
		this.cityKey = city;
		this.editionKey = edition;
		this.typeKey = type;
		this.expectedVersion = cycle;
	}
	
	@Before
	public void init() {
		this.client = new TableChartClient();
	}
	
	@Parameterized.Parameters
	public static List<Object[]> cycleNumbers () {
		//ArrayList <Date> arrayList = new ArrayList <Date>();	
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Object [] [] params = null;
		
		try { 
			params = new Object [] [] {
				{formatter.parse("06/15/2016"), "ALBUQUERQUE", "CURRENT", "SECTIONAL", Integer.valueOf(97)},
				{formatter.parse("06/15/2016"), "ALBUQUERQUE", "NEXT", "SECTIONAL", Integer.valueOf(98)}
				/*
				{formatter.parse("11/10/2016"), new Integer(6)},
				{formatter.parse("01/01/2017"), new Integer(6)},
				{formatter.parse("12/25/2016"), new Integer(6)},
				{formatter.parse("01/05/2017"), new Integer(1)},
				{formatter.parse("02/03/2017"), new Integer(1)},
				{formatter.parse("06/14/2017"), new Integer(3)},
				{formatter.parse("09/15/2017"), new Integer(5)},
				{formatter.parse("11/09/2017"), new Integer(6)}
				
				*/
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
	public void test() {
		
		ChartCycleData jsonChart = TableChartClient.callResource(targetDate);
		ChartInfoTable table = new ChartInfoTable(jsonChart);
		ChartInfoTableKey key = new ChartInfoTableKey();
		key.setCityRegion(this.cityKey);
		key.setPeriodCode(this.editionKey);
		key.setChartType(this.typeKey);
		ChartCycleElementsJson element = table.get(key);
		assertEquals(this.expectedVersion, Integer.valueOf(Integer.parseInt(element.getChart_cycle_number())));
	}

}
