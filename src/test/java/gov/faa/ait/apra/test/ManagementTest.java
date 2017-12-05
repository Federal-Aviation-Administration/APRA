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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import gov.faa.ait.apra.api.management.ManagementControl;

public class ManagementTest {

	@Test
	public void getStatus () {
		ManagementControl check = new ManagementControl();
		ManagementControl.start();
		String state = check.getStatus();
		assertEquals(state, "ServerOK");
	}
	
	@Test
	public void getStart () {
		ManagementControl check = new ManagementControl();
		ManagementControl.start();
		String state = check.getStatus();
		assertEquals(state, "ServerOK");
	}
	
	@Test
	public void getStop () {
		ManagementControl check = new ManagementControl();
		ManagementControl.stop();
		String state = check.getStatus();
		assertEquals(state, "ServerDown");
	}
	
	@Test
	public void refresh () {
		ManagementControl check = new ManagementControl();
		assertEquals(check.refresh(), "Cycle Reload Complete");
	}
	
	@Test
	public void reloadConfig () {
		ManagementControl check = new ManagementControl();
		assertEquals(check.reloadConfig(), "Config Reload Complete");
	}
}
