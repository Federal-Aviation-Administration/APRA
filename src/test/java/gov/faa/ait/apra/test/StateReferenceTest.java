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

import static org.junit.Assert.fail;

import java.util.Locale;

import org.apache.commons.lang3.text.WordUtils;
import org.junit.Test;


import gov.faa.ait.apra.bootstrap.USStateReferenceData;

public class StateReferenceTest {

	@Test
	public void getNebraskaByAbbr () {
		String nebraska = USStateReferenceData.getStateName("NE");
		if (! "NEBRASKA".equals(nebraska))
			fail();
	}

	@Test
	public void getNebraskaByName () {
		String nebraska = USStateReferenceData.getAbbreviation("NEBRASKA");
		if (! "NE".equals(nebraska))
			fail();
	}
	
	@Test
	public void getNewYorkByAbbr () {
		String newYork = USStateReferenceData.getStateName("NY");
		if (! "NEW YORK".equals(newYork))
			fail();
	}
	
	@Test
	public void getNewYorkCapitalized () {
		String newYork = USStateReferenceData.getStateName("NY");
		char[] separators = {'-', '_', ' '};
		String capitalized = WordUtils.capitalize(newYork.toLowerCase(Locale.ENGLISH), separators);
		if (! "New York".equals(capitalized))
			fail();
	}
	
}
