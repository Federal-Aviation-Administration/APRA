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

import java.util.ArrayList;

/**
 * Represents a path to a file which contains multiple path elements such as directories or a file.
 * @author FAA
 *
 */
public class ProductPath {

	private ArrayList <PathElement> path;
	private boolean complete = false;
	
	/**
	 * Null constructor to create the default path
	 */
	public ProductPath () {
		this.path = new ArrayList <> ();
		this.complete = false;
	}
	
	/** 
	 * Add a new path element to this path
	 * @param pe the path element to add to this path
	 */
	public void addPathElement (PathElement pe) {
		if (!complete) {
			if (pe.isFile()) {
				this.complete = true;
			}
			path.add(pe);
		}
		
		return;
	}
	
	/**
	 * Add the final or last path element. Once the last path element is set, the path is immutable
	 * @param pe
	 */
	public void addLastElement (PathElement pe) {
		
		if (! complete) {
			this.addPathElement(pe);
			this.complete = true;
		}
		
		return;
	}
	
	/**
	 * Get an external representation of the path using the standard / path separator character. Once the final element is reached, the path is 
	 * set and returned.
	 * @return
	 */
	public String getPathAsString () {
		StringBuilder pathString = new StringBuilder ("/");
		for (PathElement pe : path) {
			pathString = pathString.append(pe.getPathElement());
			if (pe.isDirectory()) {
				pathString = pathString.append ("/");
			}
		}
		return pathString.toString();
	}
}
