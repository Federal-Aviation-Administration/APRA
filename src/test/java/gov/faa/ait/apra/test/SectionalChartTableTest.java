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

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;

import gov.faa.ait.apra.cycle.ChartCycleElementsJson;
import gov.faa.ait.apra.cycle.ChartCycleData;
import gov.faa.ait.apra.util.ChartInfoTable;
import gov.faa.ait.apra.util.ChartInfoTableKey;

public class SectionalChartTableTest {

//	@Test
//	public void testLoad() {
//		SectionalChartCycleJson jsonData = buildJson();
//		SectionalChartTable table = new SectionalChartTable(jsonData);
//		fail("Not yet implemented");
//	}

	@Test
	public void testContainsKey() throws ParseException {
		ChartCycleData jsonData = buildJson();
		ChartInfoTable table = new ChartInfoTable(jsonData);
		ChartInfoTableKey key = new ChartInfoTableKey("ALBUQUERQUE", "CURRENT", "SECTIONAL");
		assertTrue("Expected Key not found", table.containsKey(key));
		ChartInfoTableKey key2 = new ChartInfoTableKey("ALBUQUERQUE", "NEXT", "SECTIONAL");
		assertTrue("Expected Key not found", table.containsKey(key2));
		
	}
	
	private ChartCycleData buildJson() throws ParseException {
		ChartCycleData cc = new ChartCycleData();
		//SimpleDateFormat sdfUS = new SimpleDateFormat("MM/dd/yyyy");
		cc.setName("SECTIONAL");
		ChartCycleElementsJson element = buildElement("Albuquerque", "current", "Sectional");
		ChartCycleElementsJson[] elemArray = new ChartCycleElementsJson[2];
		elemArray[0] = element;
		ChartCycleElementsJson element2 = buildElement("Albuquerque", "next", "Sectional");
		elemArray[1] = element2;
		cc.setElements(elemArray);
		return cc;
	}
	
	private ChartCycleElementsJson buildElement(String city, String period, String type) throws ParseException {
		SimpleDateFormat sdfISO = new SimpleDateFormat("yyyy-dd-MM");
		ChartCycleElementsJson element = new ChartCycleElementsJson();
		element.setChart_city_name(city);
		element.setChart_cycle_number("97");
		element.setChart_cycle_period_code(period);
		element.setChart_cycle_type_code(type);
		element.setChart_effective_date(sdfISO.parse("2016-04-28"));
		element.setQuery_date("2016-06-16");	
		return element;
	}

}
