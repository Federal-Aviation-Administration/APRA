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

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

import gov.faa.ait.apra.api.UsIfrVfrPlanning;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.util.TableChartClient;

public class IfrPlanningChartsTest {

	@Test
	public void test() {
		Calendar cal = Calendar.getInstance();
		//cal.set(2016, 05, 15); // careful, 0-based month
		TableChartClient client = new TableChartClient(cal.getTime());
		UsIfrVfrPlanning api = new UsIfrVfrPlanning(client);
		ProductSet result = (ProductSet) api.getIfrPlanningChart("current", "pdf").getEntity();
		assertTrue(!result.getEdition().isEmpty());
		String url = result.getEdition().get(0).getProduct().getUrl();
		assertTrue("URL was null",url!=null);
		assertTrue("URL did not match pattern",url.matches("http://aeronav.faa.gov/enroute/IFR_Planning/[0-9]{2}-[0-9]{2}-[0-9]{4}/US_IFR_Planning_pdf.zip"));
	}

}
