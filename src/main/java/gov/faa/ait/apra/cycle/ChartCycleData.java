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
package gov.faa.ait.apra.cycle;

public class ChartCycleData {
	private String name;
	private ChartCycleElementsJson [] elements;
	
	public ChartCycleData () {
		name = "";
		elements = new ChartCycleElementsJson [1];
	}
	
	public ChartCycleData (String name, ChartCycleElementsJson [] elements) {
		this.name = new String (name);
		this.elements = elements.clone();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ChartCycleElementsJson [] getElements() {
		return elements.clone();
	}

	public void setElements(ChartCycleElementsJson [] elements) {
		this.elements = elements.clone();
	}
}
