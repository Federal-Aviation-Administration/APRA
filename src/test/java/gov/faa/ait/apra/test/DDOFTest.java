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

import javax.ws.rs.core.Response;

import org.junit.Test;

import gov.faa.ait.apra.api.DailyDigitalObstacleFile;

public class DDOFTest {

	@Test
	public void releaseTest () {
		DailyDigitalObstacleFile ddof = new DailyDigitalObstacleFile();
		Response response = ddof.getDDOFRelease();
		if (response.getStatus() != 200)
			fail();
	}
	
	@Test
	public void editionTest () {
		DailyDigitalObstacleFile ddof = new DailyDigitalObstacleFile();
		Response response = ddof.getDDOFEdition();
		if (response.getStatus() != 200)
			fail();
	}
}
