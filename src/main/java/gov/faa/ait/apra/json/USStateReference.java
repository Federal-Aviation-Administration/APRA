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
 * A US state reference set of information that includes state name and abbreviation. 
 * Used by Jackson to marshal and unmarshal the JSON objects from the denodo reference data set
 *
 */
public class USStateReference {
	private String name;
	private USState [] elements;
	
	/**
	 * Default constructor initializes members
	 */
	
	public USStateReference () {
		name = "";
		elements = null; 
	}
	
	/**
	 * Get the name of the reference set
	 * @return name of the reference set
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the name of the reference set
	 * @param name the name of the reference data set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the elements of the reference set as an array. The elements contain the listing of US states and their 2-letter abbreviations
	 * @return all of the elements from the reference data set
	 */
	public USState [] getElements() {
		return elements.clone();
	}
	
	/**
	 * Set the elements of the data set. A listing of all US state names and abbreviations
	 * @param elements the list of states and abbreviations
	 */
	public void setElements(USState [] elements) {
		this.elements = elements.clone();
	}
}
