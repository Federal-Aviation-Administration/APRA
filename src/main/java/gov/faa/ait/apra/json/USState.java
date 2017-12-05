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
 * This class represents a US state. It contains the US state full name and its 2-letter abbreviation. This is the 
 * class that Jackson serializes from a JSON object set into a POJO
 *
 */
public class USState {
	private String abbreviation;
	private String name;
	
	/**
	 * Default null constructor returns District of Columbia, DC
	 */
	public USState () {
		abbreviation = "DC";
		name = "District of Columbia";
	}
	
	/**
	 * Get the state 2-letter abbreviation
	 * @return the 2-letter abbreviation as a string
	 */
	public String getAbbreviation() {
		return abbreviation;
	}
	
	/**
	 * Set the state 2-letter abbreviation
	 * @param abbreviation
	 */
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}
	
	/**
	 * Get the state proper name
	 * @return proper name of the state or territory
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the state proper name
	 * @param name state or territory name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
}
