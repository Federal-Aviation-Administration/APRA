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
package gov.faa.ait.apra.json;

/**
 * This class holds the top level Supplement chart metadata object as returned by denodo. 
 * This is just a POJO data class for the denodo response of the serialized and virtualized 
 * XML metadata file for Supplement
 * 
 * @author FAA
 *
 */
public class SupplementChartMetadata {
	private String name;
	private SupplementMetadata[] elements;
	
	public SupplementChartMetadata () { 
		name = "";
		elements = new SupplementMetadata [1];
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SupplementMetadata [] getElements() {
		return elements.clone();
	}

	public void setElements(SupplementMetadata [] elements) {
		this.elements = elements.clone();
	}
}
