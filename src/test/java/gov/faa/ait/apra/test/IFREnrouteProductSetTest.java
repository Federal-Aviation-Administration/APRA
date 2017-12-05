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

import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.api.IFREnrouteCharts;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.jaxb.ProductSet.Edition;
import gov.faa.ait.apra.cycle.ChartCycleClient;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;
import gov.faa.ait.apra.cycle.ChartCycleData;

@RunWith(Parameterized.class)
public class IFREnrouteProductSetTest {
	
	private TestParameter testParameterSet;
	private static final Logger logger = LoggerFactory.getLogger(IFREnrouteProductSetTest.class);

	public IFREnrouteProductSetTest(TestParameter parameterSet) {
		this.testParameterSet = parameterSet;
	}
	
	@Parameterized.Parameters
	public static List<TestParameter> getTestParameters() {
		ArrayList<TestParameter> parameters = new ArrayList<TestParameter>();
		GregorianCalendar cal = new GregorianCalendar();
		ChartCycleClient cycleClient = new ChartCycleClient();
		ChartCycleElementsJson currentCycle = cycleClient.getCurrent56DayCycle();
		Date chartDate = currentCycle.getChart_effective_date();
		SimpleDateFormat sdfUsDash = new SimpleDateFormat("MM-dd-yyyy");
		ChartCycleData csj = cycleClient.getChartCycle(cal.getTime(), true);
		Date chartDate2 = csj.getElements()[0].getChart_effective_date();
		String chartDateString = sdfUsDash.format(chartDate2);
		logger.info("Chart date 2 "+sdfUsDash.format(chartDate2));
		logger.info("unformatted chart date " + chartDate.toString());
		logger.info("Current date-time "+sdfUsDash.format(cal.getTime()));
		logger.info("Using current chart date of " + chartDateString);
		// current, us, low, tiff, expected
		// case 0
		parameters.add(new TestParameter(cal.getTime(), "CURRENT", "US", "LOW", "TIFF",  
				new String [] { 
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l01.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l02.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l03.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l04.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l05.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l06.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l07.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l08.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l09.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l10.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l11.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l12.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l13.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l14.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l15.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l16.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l17.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l18.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l19.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l20.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l21.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l22.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l23.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l24.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l25.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l26.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l27.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l28.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l29.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l30.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l31.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l32.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l33.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l34.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l35.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_l36.zip"
				}) );
		// param 1
		parameters.add(new TestParameter(cal.getTime(), "CURRENT", "US", "LOW", "PDF",  
				new String [] { 
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delus1.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delus3.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delus5.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delus7.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delus9.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delus11.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delus13.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delus15.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delus17.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delus19.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delus21.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delus23.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delus25.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delus27.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delus29.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delus31.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delus33.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delus35.zip"						
		}));
		// param 2
		parameters.add(new TestParameter(cal.getTime(), "CURRENT", "US", "HIGH", "TIFF",  
				new String [] { 
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_h01.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_h02.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_h03.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_h04.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_h05.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_h06.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_h07.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_h08.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_h09.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_h10.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_h11.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_h12.zip"						
		}));
		// param 3
		parameters.add(new TestParameter(cal.getTime(), "CURRENT", "US", "HIGH", "PDF",  
				new String [] {
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/dehus1.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/dehus3.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/dehus5.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/dehus7.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/dehus9.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/dehus11.zip"						
		}));
		// param 4
		parameters.add(new TestParameter(cal.getTime(), "CURRENT", "ALASKA", "LOW", "TIFF",  
				new String [] {
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_akl01.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_akl02.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_akl03.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_akl04.zip"
		}));
		// param 5
		parameters.add(new TestParameter(cal.getTime(), "CURRENT", "ALASKA", "LOW", "PDF",  
				new String [] {
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delak1.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delak3.zip"
		}));
		// param 6
		parameters.add(new TestParameter(cal.getTime(), "CURRENT", "ALASKA", "HIGH", "TIFF",  
				new String [] {
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_akh01.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_akh02.zip"						
		}));
		// param 7
		parameters.add(new TestParameter(cal.getTime(), "CURRENT", "ALASKA", "HIGH", "PDF",  
				new String [] {
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/dehak1.zip"
		}));
		// param 8
		parameters.add(new TestParameter(cal.getTime(), "CURRENT", "PACIFIC", "HIGH", "TIFF",  
				new String [] {
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_p01.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_p02.zip"					
		}));
		// param 9
		parameters.add(new TestParameter(cal.getTime(), "CURRENT", "PACIFIC", "HIGH", "PDF",  
				new String [] {
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/dephi1.zip"
		}));
		// param 10
		parameters.add(new TestParameter(cal.getTime(), "CURRENT", "CARIBBEAN", "LOW", "PDF",  
				new String [] {		
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delcb1.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delcb3.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delcb5.zip"
		}));
		// param 11
		parameters.add(new TestParameter(cal.getTime(), "CURRENT", "CARIBBEAN", "HIGH", "PDF",  
				new String [] {		
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/dehcb1.zip"
		}));
		// param 12
		parameters.add(new TestParameter(cal.getTime(), "CURRENT", "US", "AREA", "TIFF",  
				new String [] { 
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_a01.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/enr_a02.zip"
		}));
		// param 13
		parameters.add(new TestParameter(cal.getTime(), "CURRENT", "US", "AREA", "PDF",  
				new String [] { 
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/darea.zip"
		}));		
		// param 14
		parameters.add(new TestParameter(cal.getTime(), "CURRENT", "CARIBBEAN", "AREA", "PDF",  
				new String [] { 
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delcba1.zip",
						"http://aeronav.faa.gov/enroute/"+chartDateString+"/delcb3.zip"
		}));	
		return parameters;
	}
	
	@Test
	public void testIFREnrouteProduct() {
		logger.info("Testing " + this.testParameterSet.getGeoname() + ", " + this.testParameterSet.getEdition() + ", " + this.testParameterSet.getFormat()+ ", "+ this.testParameterSet.getSeriesType());
		ChartCycleClient client = new ChartCycleClient();
		client.getChartCycle(this.testParameterSet.getQueryDate(), true);
		IFREnrouteCharts chartService = new IFREnrouteCharts(client);
		ProductSet productSet = (ProductSet) chartService.getIFREnrouteRelease(this.testParameterSet.getEdition(), this.testParameterSet.getFormat(), this.testParameterSet.getGeoname(), this.testParameterSet.getSeriesType()).getEntity();
		int i = 0;
		assertTrue(productSet.getEdition()!=null);
		assertEquals(this.testParameterSet.getExpectedUrls().length, productSet.getEdition().size());
		for(Edition ed : productSet.getEdition()) {
			assertTrue(ed.getProduct()!=null);
			Edition.Product product = ed.getProduct();
			assertEquals(this.testParameterSet.getExpectedUrls()[i], product.getUrl());
			i++;
		}
	}
	
	
	/**
	 * This is more or less a struct to carry test parameters for this test case
	 * @author Leonard CTR Wester
	 *
	 */
	private static class TestParameter {
		private Date queryDate;
		private String edition;
		private String geoname;
		private String seriesType;
		private String format;
		private String [] expectedUrls;
		
		public TestParameter(Date queryDate, String edition, String geoname, String seriesType, String format, String [] expectedUrls) {
			this.queryDate = new Date(queryDate.getTime());
			this.edition = new String(edition);
			this.geoname = new String (geoname);
			this.format = new String(format);
			this.seriesType = new String(seriesType);
			this.expectedUrls = expectedUrls.clone();
		}
		
		public Date getQueryDate () {
			return new Date (queryDate.getTime());
		}
		
		public String getEdition() {
			return new String (edition);
		}
		
		public String getGeoname () {
			return new String (geoname);
		}
		
		public String getSeriesType () {
			return new String (seriesType);
		}
		
		public String getFormat() {
			return new String (format);
		}
		
		public String [] getExpectedUrls () {
			return expectedUrls.clone();
		}
	}

}
