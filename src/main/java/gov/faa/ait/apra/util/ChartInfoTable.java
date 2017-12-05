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
package gov.faa.ait.apra.util;

import java.util.concurrent.ConcurrentHashMap;

import gov.faa.ait.apra.cycle.ChartCycleData;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;

/**
 * This class stores Chart Info from ChartCycleJson in a map format
 * @author FAA
 *
 */
public class ChartInfoTable extends ConcurrentHashMap<ChartInfoTableKey, ChartCycleElementsJson> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4599811904037606947L;

	/**
	 * Default constructor, empty map with default capacity
	 */
	public ChartInfoTable() {
		super();
	}
	
	/**
	 * Constructor for default capacity, with data to load
	 * @param chartCycle the chart cycle information in json model class
	 */
	public ChartInfoTable(ChartCycleData chartCycle) {
		super();
		load(chartCycle);
	}
	
	/**
	 * Method to reload chart cycle data
	 * @param chartCycle Chart cycle data in json model eclass
	 */
	public void load(ChartCycleData chartCycle) {
		for(ChartCycleElementsJson element: chartCycle.getElements()) {
			//System.out.println(element.getChart_city_name()+"; "+element.getChart_cycle_number()+"; "+element.getChart_cycle_period_code()+"; "+element.getChart_cycle_type_code());
			ChartInfoTableKey key = new ChartInfoTableKey(
				element.getChart_city_name().toUpperCase(),
				element.getChart_cycle_period_code().toUpperCase(),
				element.getChart_cycle_type_code().toUpperCase());
			this.put(key, element);
		}
	}
	
}
