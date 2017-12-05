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
package gov.faa.ait.apra.path;

/**
 * This is a convenience class to hold the element of a URL such as a folder, directory, or file
 * @author FAA
 *
 */
public class PathElement {

	private String pathName;
	private boolean isFile;
		
	/**
	 * Create a default path element with a directory
	 * @param dir
	 */
	public PathElement (String dir) {
		this.pathName = dir;
		this.isFile = false;
	}
	
	/**
	 * The null constructor where the path element name can be set later
	 */
	public PathElement () {
		this.pathName = "";
		this.isFile = false;
	}
	
	/**
	 * Get the path element name
	 * @return the name of the path element
	 */
	public String getPathElement () {
		return pathName;
	}
	
	/**
	 * Set or reset the path element name to some string
	 * @param pe the string name of the path element to set
	 * @return the path element to enable chaining of sets
	 */
	public PathElement setPathElement (String pe) {
		this.pathName = pe;
		return this;
	}
	
	/**
	 * The file is the last part of the path element. Setting this marks the path as complete and no further additions to the path are allowed.
	 */
	public void setFile() {
		isFile = true;
	}
	
	/**
	 * Returns true if this particular path element is a file marking the end of the path
	 * @return true if a file, false otherwise
	 */
	public boolean isFile () {
		return isFile;
	}
	
	/** 
	 * Marks this path element as a directory so additional path elements can be created and added to a path
	 */
	public void setDirectory () {
		isFile = false;
	}
	
	/**
	 * Returns true if this particular path element is a directory 
	 * @return true if a directory
	 */
	public boolean isDirectory() {
		return ! isFile;
	}
}
