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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.api.TerminalAreaCharts;

public class CityNameConversionTest {
	private final static Logger logger = LoggerFactory.getLogger(CityNameConversionTest.class);

	@Test
	public void convertPR () {
		TerminalAreaCharts tac = new TerminalAreaCharts();
		tac.setCity("puerto Rico-IV");
		logger.info("puerto Rico-IV converted to "+tac.getCity());
	}
	
	@Test
	public void convertTampa () {
		TerminalAreaCharts tac = new TerminalAreaCharts();
		tac.setCity("tampa-orlando");
		logger.info("tampa-orlando converted to "+tac.getCity());
	}
	

	@Test
	public void convertDallas () {
		TerminalAreaCharts tac = new TerminalAreaCharts();
		tac.setCity("dallas-ft worth");
		logger.info("dallas-ft worth converted to "+tac.getCity());
	}


	@Test
	public void convertMSP () {
		TerminalAreaCharts tac = new TerminalAreaCharts();
		tac.setCity("Minneapolis-St Paul");
		logger.info("Minneapolis-St Paul converted to "+tac.getCity());
	}

	@Test
	public void convertMSPlower () {
		TerminalAreaCharts tac = new TerminalAreaCharts();
		tac.setCity("minneapolis-st paul");
		logger.info("minneapolis-st paul converted to "+tac.getCity());
	}	

	@Test
	public void convertDEN () {
		TerminalAreaCharts tac = new TerminalAreaCharts();
		tac.setCity("Denver-Colorado Springs");
		logger.info("Denver-Colorado Springs converted to "+tac.getCity());
	}

	@Test
	public void convertDENmixed () {
		TerminalAreaCharts tac = new TerminalAreaCharts();
		tac.setCity("denver-Colorado springs");
		logger.info("denver-Colorado springs converted to "+tac.getCity());
	}
	
	@Test
	public void formatPR () {
		TerminalAreaCharts tac = new TerminalAreaCharts();
		tac.setCity("puerto Rico-IV");
		String val = tac.formatCity();
		logger.info("puerto Rico-IV formatted as "+val);
	}
	
	@Test
	public void formatPRlower () {
		TerminalAreaCharts tac = new TerminalAreaCharts();
		tac.setCity("puerto rico-iv");
		String val = tac.formatCity();
		logger.info("puerto rico-iv formatted as "+val);
	}
	
	@Test
	public void formatTampa () {
		TerminalAreaCharts tac = new TerminalAreaCharts();
		tac.setCity("tampa-orlando");
		String val = tac.formatCity();
		logger.info("tampa-orlando formatted as "+val);
	}
	

	@Test
	public void formatDallas () {
		TerminalAreaCharts tac = new TerminalAreaCharts();
		tac.setCity("dallas-ft worth");
		String val = tac.formatCity();
		logger.info("dallas-ft worth formatted as "+val);
	}


	@Test
	public void formatMSP () {
		TerminalAreaCharts tac = new TerminalAreaCharts();
		tac.setCity("Minneapolis-St Paul");
		String val = tac.formatCity();
		logger.info("Minneapolis-St Paul formatted as "+val);
	}

	@Test
	public void formatMSPlower () {
		TerminalAreaCharts tac = new TerminalAreaCharts();
		tac.setCity("minneapolis-st paul");
		String val = tac.formatCity();
		logger.info("minneapolis-st paul formatted as "+val);
	}	

	@Test
	public void formatDEN () {
		TerminalAreaCharts tac = new TerminalAreaCharts();
		tac.setCity("Denver-Colorado Springs");
		String val = tac.formatCity();
		logger.info("Denver-Colorado Springs converted to "+val);
	}

	@Test
	public void formatDENmixed () {
		TerminalAreaCharts tac = new TerminalAreaCharts();
		tac.setCity("denver-Colorado springs");
		String val = tac.formatCity();
		logger.info("denver-Colorado springs converted to "+val);
	}	
}
