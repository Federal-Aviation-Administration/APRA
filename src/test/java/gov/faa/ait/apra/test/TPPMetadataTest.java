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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.json.TPPChartMetadata;
import gov.faa.ait.apra.cycle.ChartCycleClient;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;
import gov.faa.ait.apra.util.TPPMetadataClient;

public class TPPMetadataTest {
	private ChartCycleClient cycleClient;
	private static final Logger logger = LoggerFactory.getLogger(TPPMetadataTest.class);
	
	public TPPMetadataTest() {
		cycleClient = new ChartCycleClient();
		cycleClient.forceUpdate();
	}
	
	@Test
	public void getAlaskaData () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle);
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByState("Alaska");
		assertNotNull(tppMetaRoot.getElements());
	}

	@Test
	public void getAlaskaData2 () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle, false);
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByState("Alaska");
		assertNotNull(tppMetaRoot.getElements());
	}
	
	@Test
	public void getAlaskaChangedData () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle, true);
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByState("Alaska");
		assertNotNull(tppMetaRoot.getElements());	
	}

	@Test
	public void getUSChangedData () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle, true);
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByState("US");
		assertNotNull(tppMetaRoot.getElements());
	}

	
	@Test
	public void getStateOfConfusion () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle);
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByState("Confusion");
		assertNotNull(tppMetaRoot);
	}

	@Test
	public void getNewYorkData () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle);
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByState("New York");
		assertNotNull(tppMetaRoot.getElements());	
	}
	
	@Test
	public void getNewYorkData2 () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle, false);
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByState("New York");
		assertNotNull(tppMetaRoot.getElements());	
	}
	
	@Test
	public void getNewYorkChangeData () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle, true);
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByState("New York");
		assertNotNull(tppMetaRoot.getElements());	
	}	
	
	@Test
	public void getNewYorkEncodedData () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle, false);
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByState("New%20York");
		assertNotNull(tppMetaRoot.getElements());	
	}

	@Test
	public void getNewHampshireData () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle, false);
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByState("new hampshire");
		assertNotNull(tppMetaRoot.getElements());	
	}

	@Test
	public void getNewHampshirePaddedData () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle, false);
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByState(" new hampshire  ");
		assertNotNull(tppMetaRoot.getElements());	
	}
	
	@Test
	public void getEncodedStringValue () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle, false);
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByState("%20%24+%2A");
		assertNotNull(tppMetaRoot.getElements());	
	}	

	@Test
	public void getAmpersandQuery () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle, false);
		// This one should gum up the query string. 
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByState("&nebraska");
		assertNotNull(tppMetaRoot.getElements());	
	}
	
	// Get the metadata information by TPP volume
	
	@Test
	public void getNortheastVolumeQuery () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle, false);
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByVolume("NE-1");
		assertNotNull(tppMetaRoot.getElements());	
	}
	
	@Test
	public void getSC1VolumeQuery () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle, false);
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByVolume("SC-1");
		assertNotNull(tppMetaRoot.getElements());	
	}
	
	@Test
	public void getAK1VolumeQuery () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle, false);
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByVolume("AK-1");
		assertNotNull(tppMetaRoot.getElements());	
	}
	
	@Test
	public void getUnknownVolumeQuery () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle, false);
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByVolume("UNKNOWN");
		if (tppMetaRoot.getElements().length > 0) {
			logger.info("TPP metadata returned "+tppMetaRoot.getElements().length+" elements for a null volume. This is incorrect");
			fail();	
		}
	}
	
	@Test
	public void getNullVolumeQuery () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle, false);
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByVolume(null);
		if (tppMetaRoot.getElements().length > 0) {
			logger.info("TPP metadata returned "+tppMetaRoot.getElements().length+" elements for a null volume. This is incorrect");
			fail();	
		}
	}
	
	@Test
	public void getJunkVolumeQuery () {
		ChartCycleElementsJson cycle = cycleClient.getCurrent28DayCycle();
		TPPMetadataClient tppClient = new TPPMetadataClient(cycle, false);
		TPPChartMetadata tppMetaRoot = tppClient.getChartMetadataByVolume("@*?$(*@#$*))=@#&foo=$%20+\\&");
		if (tppMetaRoot != null) {
			logger.info("TPP metadata returned a non-null result for a total junk volume name of '@*?$(*@#$*))=@#&foo=$%20+\\&'. This is incorrect");
			fail();	
		}
	}
	
}
