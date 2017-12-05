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

import static gov.faa.ait.apra.bootstrap.ErrorCodes.ERROR_400;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.ERROR_404;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.ERROR_500;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.RESPONSE_200;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.bootstrap.Config;
import gov.faa.ait.apra.jaxb.ProductCodeList;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.jaxb.ProductSet.Edition.Product;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;
import gov.faa.ait.apra.util.TableChartClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This class is used to implement US IFR Planning service.
 * It extends the TableDataService class, implementing the edition and product methods.
 * @author FAA
 *
 */
@Api(value="IFR Planning Charts")
@Path("/ifr/planning")
public class UsIfrVfrPlanning extends AbstractTableDataService {

	private static final String GEONAME_CONUS = "CONUS";
	private static final String CHART_TYPE_IFR_PLANNING = "IFR_PLANNING";
	private static final Logger logger = LoggerFactory.getLogger(UsIfrVfrPlanning.class);
	
	/**
	 * Default constructor
	 * Sets city to CONUS 
	 */
	public UsIfrVfrPlanning() {
		setClient(new TableChartClient());
		this.setCity(GEONAME_CONUS);
	}
	
	/**
	 * This constructor allows a specific chart client to be used.  Mainly for test purposes.
	 * @param client
	 */
	public UsIfrVfrPlanning(TableChartClient client) {
		setClient(client);
		this.setCity(GEONAME_CONUS);
	}

	/**
	 * API method to get product download links + edition information
	 * @param edition current (default) or next
	 * @param format pdf (default) or tiff
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	@Path("/chart")
    @ApiOperation(value="Get IFR planning download link by edition and format", 
    		notes="TIFF formatted files are geo-referenced while PDF format is not geo-referenced. "
    				+ " The specific chart returned by this operation is the IFR PLANNING chart found "
    				+ "on the FAA public web site at FAA Home > Air Traffic > Flight Information > Aeronautical Information Services "
    				+ "> Digital Products > IFR Charts and DERS > Planning tab", 
    		response=ProductSet.class)
	
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})

	public Response getIfrPlanningChart(
			@ApiParam(name="edition", value="Requested product edition. If omitted, the default current edition is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false)  @QueryParam("edition") String edition, 
			@ApiParam (name="format", value="Format of the requested chart. TIFF is georeferenced and PDF is not georeferenced. If omitted, the default PDF format is returned.", allowableValues="tiff, pdf", defaultValue="pdf", allowMultiple=false, required=false) @QueryParam("format") String format) {

		ProductSet ps = super.buildChart(format, edition, CHART_TYPE_IFR_PLANNING);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();
	}

	/**
	 * API method to get edition information, such as chart date, edition number
	 * @param edition current or next
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	@Path("/info")
    @ApiOperation(value="Get Planning Chart edition date and edition number by edition type", 
    	response=ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})

	
	public Response getIfrPlanningInfo(
			@ApiParam(name="edition", value="Requested product edition. If omitted, the default current edition information is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false) 
			@QueryParam("edition") String edition) {

		ProductSet ps = super.buildInfo(edition, CHART_TYPE_IFR_PLANNING);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();
	}

	@Override
	protected Product createProduct(ChartCycleElementsJson element) {
		SimpleDateFormat sdfUSA = new SimpleDateFormat("MM-dd-yyyy");
		gov.faa.ait.apra.jaxb.ObjectFactory of = new gov.faa.ait.apra.jaxb.ObjectFactory();
		Product prod = of.createProductSetEditionProduct();
		prod.setProductName(ProductCodeList.IFR_PLANNING);
		StringBuilder productUrl = new StringBuilder();
		productUrl.append(Config.getAeronavHost())
			.append("/")
			.append(Config.getEnrouteFolder())
			.append("/IFR_Planning/")
			.append(sdfUSA.format(element.getChart_effective_date()) );
		productUrl.append("/").append("US_IFR_Planning");
		if("PDF".equalsIgnoreCase(this.getFormat())) {
			productUrl.append("_pdf.zip");
		} else if ("TIFF".equalsIgnoreCase(this.getFormat()) || "ZIP".equalsIgnoreCase(this.getFormat())) {
			productUrl.append("_tif.zip");
		}
		try {
			if(this.verifyURL(new URL(productUrl.toString()))) {
				prod.setUrl(productUrl.toString());
			}
		} catch (MalformedURLException emalformed) {
    		logger.warn("The download URL is not valid", emalformed);
		}	
		return prod;
	}

	@Override
	protected boolean verifyGeoName() {
		return true;  // CONUS is hard-coded 
	}

}
