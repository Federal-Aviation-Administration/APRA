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
package gov.faa.ait.apra.api;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.bootstrap.ErrorCodes;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;
import gov.faa.ait.apra.jaxb.EditionCodeList;
import gov.faa.ait.apra.jaxb.FormatCodeList;
import gov.faa.ait.apra.jaxb.ObjectFactory;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.jaxb.ProductSet.Edition;
import gov.faa.ait.apra.jaxb.ProductSet.Edition.Product;
import gov.faa.ait.apra.jaxb.ProductSet.Status;
import gov.faa.ait.apra.util.ChartInfoTable;
import gov.faa.ait.apra.util.ChartInfoTableKey;
import gov.faa.ait.apra.util.TableChartClient;


/**
 * This class extends BaseService and uses a concurrent hash map 
 * and Java 8 streams to output chart information.
 * @author Federal Aviation Administration
 *
 */
public abstract class AbstractTableDataService extends BaseService {
	private static final int NOT_FOUND = 404;
	private static final int OK = 200;
	private static final Logger LOGGER = 
		LoggerFactory.getLogger(AbstractTableDataService.class);
	
	private TableChartClient client;
	
	private String city = "";

	/**
	 * Returns the city, aka geoname, for the requested chart.
	 * @return
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets the city, aka geoname, for the requested chart.
	 * @param city
	 */
	public void setCity(String city) {
		this.city = city;
	}	
	
	/**
	 * Gets the denodo service client used by instance.
	 * @return
	 */
	public TableChartClient getClient() {
		return this.client;
	}
	
	/**
	 * Sets denodo service client used by instance.
	 * @param client
	 */
	public void setClient(TableChartClient client) {
		this.client = client;
	}	

	@Override
	protected ProductSet buildResponse(ChartCycleElementsJson cycle) {
		return null;
	}
	
	private ProductSet initalizeResponse() {
		ObjectFactory of = new ObjectFactory();
		ProductSet root = of.createProductSet();
		Status status = of.createProductSetStatus();
		status.setCode(OK);
		status.setMessage("OK");
		root.setStatus(status);
		return root;
	}
	
	/**
	 * This implementation is used to construct a chart response based upon the requested 
	 * chart format, edition, and type. 
	 * 
	 * @param format the chart product format of either PDF or ZIP
	 * @param edition the chart edition of CURRENT or NEXT
	 * @param chartType the type of chart such as enroute low, high, area
	 * @return a ProductSet object that encapsulates the chart response
	 */
	protected ProductSet buildChart(String format, 
			String edition, String chartType) {
		
		if (format != null) {
			setFormat(format);
		}
		else {
			setFormat(PDF);
		}
		
		if (edition != null) {
			setEdition(edition);
		}
		else {
			setEdition(CURRENT);
		}
		
		ProductSet root = this.initalizeResponse();
		if (!verifyParameters()) {
			return getIllegalArgumentError();
		} 
		if (!verifyGeoName()) {
			return getIllegalCityError();
		}
		ChartInfoTable table = TableChartClient.getTable(client);
		this.buildChartResponse(root, table, chartType, OutputMode.PRODUCT);
		return root;
	}
	
	private ProductSet getIllegalCityError() {
    	return getErrorResponse (NOT_FOUND, ErrorCodes.ERROR_404);   	
	}

	/**
	 * This implementation is used to construct a chart info response based upon the requested 
	 * chart edition and type. 
	 * 
	 * @param edition the chart edition of CURRENT or NEXT
	 * @param chartType the type of chart such as enroute low, high, area
	 * @return a ProductSet object that encapsulates the chart response information
	 */
	protected ProductSet buildInfo(String edition, String chartType) {
 
		this.setFormat(null);
		
		if (edition != null) {
			setEdition(edition);
		}
		else {
			setEdition(CURRENT);
		}
 
		ProductSet root = this.initalizeResponse();
		if (!this.verifyEdition()) {
			return getIllegalArgumentError();
		}
		ChartInfoTable table = TableChartClient.getTable(client);
		this.buildChartResponse(root, table, chartType, OutputMode.EDITION);
		return root;
	}
	
	/**
	 * This implementation is called by buildChart method to construct the actual response.
	 * 
	 * @param response the ProductSet object to be populated with values for the response
	 * @param table the chart information table used to lookup values from a hash
	 * @param chartType the type of chart response to be constructed
	 * @param mode is PRODUCT when the chart URL is included in the response in addition 
	 * to information metadata
	 */
	protected void buildChartResponse(ProductSet response, ChartInfoTable table, String chartType, OutputMode mode ) {
		LOGGER.info("Building chart response using "+this.getCity());
		if(this.getCity()==null || this.getCity().length()==0) {
			// add all sectional with the edition
			table.entrySet().stream().filter( entry -> entry.getKey().getChartType().equals(chartType)
					&& entry.getKey().getPeriodCode().equals(this.getEdition()))
				.sorted((entry1, entry2) -> entry1.getKey().getCityRegion().compareTo(entry2.getKey().getCityRegion()))
				.forEach(entry -> {
					Edition ed = this.createEdition(entry.getValue());
					if(mode.equals(OutputMode.PRODUCT)) {
						ed.setProduct(this.createProduct(entry.getValue()));
					}
					response.getEdition().add(ed);
			});
		} else {
			LOGGER.info("Building chart response using "+
				this.getCity().toUpperCase(Locale.ENGLISH)+" "+this.getEdition().toUpperCase()+" "+chartType);
			
			ChartInfoTableKey key = new ChartInfoTableKey(
				this.getCity().toUpperCase(Locale.ENGLISH), this.getEdition().toUpperCase(), chartType);
			
			if(table.containsKey(key)) {
				ChartCycleElementsJson element = table.get(key);
				Edition ed = this.createEdition(element);
				if(mode.equals(OutputMode.PRODUCT)) {
					ed.setProduct(this.createProduct(element));
					if (EMPTY_STRING.equals(ed.getProduct().getUrl())) {
						response.getStatus().setCode(NOT_FOUND);
						response.getStatus().setMessage(ErrorCodes.ERROR_404);
					}
				}
				response.getEdition().add(ed);
			}
			else {
				LOGGER.warn("Table data key not found for key "
					+key.toString()+". Returning a 404 not found for this request.");
				response.getStatus().setCode(NOT_FOUND);
				response.getStatus().setMessage(ErrorCodes.ERROR_404);
			}
		}
	}
	
	/**
	 * This implementation is used to construct the edition response for a requested chart.
	 * 
	 * @param element the chart cycle information used to construct the edition response
	 * @return
	 */
	protected Edition createEdition(ChartCycleElementsJson element) {
		gov.faa.ait.apra.jaxb.ObjectFactory of = 
				new gov.faa.ait.apra.jaxb.ObjectFactory();
		
		SimpleDateFormat sdfUSA = new SimpleDateFormat("MM/dd/yyyy");
		Edition ed = of.createProductSetEdition();		
		ed.setGeoname(element.getChart_city_name());
		
		if(element.getChart_effective_date()!=null) {
			ed.setEditionDate(sdfUSA.format(
				element.getChart_effective_date()));
		}
		
		ed.setEditionName(EditionCodeList.fromValue(
			element.getChart_cycle_period_code())); 
		
		if(!this.getFormat().equals(BaseService.EMPTY_STRING)) {
			ed.setFormat(FormatCodeList.fromValue(
				this.getFormat().toUpperCase()));
		}
		
		ed.setEditionNumber(Integer.parseInt(
			element.getChart_cycle_number()));
		return ed;
	}
	
	protected abstract boolean verifyGeoName();
	
	protected abstract Product createProduct(
			ChartCycleElementsJson element);
	
	protected enum OutputMode {
		PRODUCT, EDITION;
	}
}
