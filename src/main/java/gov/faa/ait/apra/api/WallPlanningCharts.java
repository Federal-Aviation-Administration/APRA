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

import gov.faa.ait.apra.bootstrap.Config;
import gov.faa.ait.apra.bootstrap.ErrorCodes;
import gov.faa.ait.apra.jaxb.ObjectFactory;
import gov.faa.ait.apra.jaxb.ProductCodeList;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.jaxb.ProductSet.Edition;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;
import gov.faa.ait.apra.cycle.WallPlanningChartCycleClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static gov.faa.ait.apra.bootstrap.ErrorCodes.ERROR_400;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.ERROR_404;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.ERROR_500;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.RESPONSE_200;

import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Wall Planning Chart web service provides current and previous
 * downloadebel urls with zip and pdf extensions.
 * 
 * @author FAA
 *
 */
@Api(value = "US VFR Wall Planning Chart")
@Path("/vfr/wallplanning")
public class WallPlanningCharts extends BaseService {
	private ProductSet response = null;
	private URL downloadURL = null;
	private ChartCycleElementsJson cycle = null;

	private static final Logger logger = LoggerFactory
			.getLogger(WallPlanningCharts.class);

	/**
	 * This is the WallPlan chart download URL. Two parameters provided to
	 * retrieve the URL for either the current or the next edition. Current and
	 * tiff are default parameters if in url parameters are listed.
	 * 
	 * @param ed
	 * @param fmt
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	@Path("/chart")

    @ApiOperation(value="Get WallPlan Chart release information with download link by edition and format", nickname="getVFRWallPlanningRelease", response=ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
	
	public Response getProductRelease(
			@ApiParam(name="edition", value="Requested product edition. If omitted, the default current edition is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false) @QueryParam("edition") String ed, 
			@ApiParam (name="format", value="Format of the requested chart. TIFF format contains georeferenced charts contained within a zip archive and PDF is non-georeferenced charts. If omitted, the default PDF format is returned.", allowableValues="tiff, pdf", defaultValue="pdf", allowMultiple=false, required=false) @QueryParam("format") String fmt) {


		logger.info("Received call to retrieve current WallPlan product release for edition '"
				+ ed + " format'" + fmt + "'.");
		ObjectFactory of = new ObjectFactory();

		response = of.createProductSet();

    	setEdition(ed != null ? ed : CURRENT);
    	setFormat(fmt != null ? fmt : PDF);
    	setGeoname("US");
    	
		if (!validateRequest(ed, fmt)) {
	    	return Response.status(response.getStatus().getCode()).entity(response).build();
		}

		ProductSet ps = getRelease(cycle);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();
	}

	/**
	 * This is the WallPlan information download URL. Two parameters provided to
	 * retrieve the URL for either the current or the next edition. Current and
	 * tiff are default parameters if in url parameters are listed.
	 * 
	 * @param ed
	 * @param fmt
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	@Path("/info")
	@ApiOperation(value = "Get WallPlan edition date and edition number by edition type and format", 
			nickname="getVFRWallPlanningEdition", response = ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})

	public Response getProductEdition(
			@ApiParam(name="edition", value="Requested product edition. If omitted, the default current edition information is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false) @QueryParam("edition") String ed,
			@ApiParam (name="format", value="Format of the requested chart. TIFF format contains georeferenced charts in a zip archive file and PDF is non-georeferenced charts. If omitted, the default PDF format is used.", allowableValues="tiff, pdf", defaultValue="pdf", allowMultiple=false, required=false)  @QueryParam("format") String fmt) {

		logger.info("Received call to retrieve current WallPlan product release for edition '"
				+ ed + " format'" + fmt + "'.");

		ObjectFactory of = new ObjectFactory();

		response = of.createProductSet();
		
    	setEdition(ed != null ? ed : CURRENT);
    	setFormat(fmt != null ? fmt : PDF);
    	setGeoname("US");

		if (!validateRequest(ed, fmt)) {
	    	return Response.status(response.getStatus().getCode()).entity(response).build();
		}

		ProductSet ps = getEdition(cycle);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();
	}

	/**
	 * This method return product set
	 * 
	 * @param cycle
	 * @return
	 */
	public ProductSet getRelease(ChartCycleElementsJson cycle) {
		
		StringBuilder wallPlanPath = new StringBuilder(Config.getWallplanUploadFolder());
		
		if (cycle.getChart_effective_date() != null) {
			// the path looks like this:
			// /content/aeronav/grand_canyon_files/US_WallPlan_<cycle_numbe>.zip/US_WallPlan_<cycle_numbe>_P.pdf
			StringBuilder fileName = new StringBuilder("US_WallPlan_");
			fileName.append(cycle.getChart_cycle_number());
			if (PDF.equalsIgnoreCase(this.getFormat())) {
				fileName.append("_P.pdf");
			} else {
				fileName.append(".zip");
			}
			
			wallPlanPath.append("/").append(fileName);

			try {
				downloadURL = new URL(Config.getAeronavHost()
						+ wallPlanPath.toString());

				if (!verifyURL(downloadURL)) {
					logger.warn(downloadURL.toExternalForm()
							+ " returned a non 200 response code when completing a HTTP HEAD check.");
					//downloadURL = null;
				}
			} catch (MalformedURLException emalformed) {
				logger.error("getRelease", emalformed);
				return getErrorResponse(500,
						"Unable to construct a valid URL for the WallPlan product release.");
			}
		}

		return buildResponse(cycle);

	}

	@Override
	protected ProductSet buildResponse(ChartCycleElementsJson cycle) {
		
		ProductSet responseXml = getEdition(cycle);
    	Edition.Product product = new Edition.Product();  	
    	product.setProductName(ProductCodeList.WALLPLANNING);
    	
     	validateAndSetUrl(downloadURL.toExternalForm(), responseXml, product);
    	
    	responseXml.getEdition().get(0).setProduct(product);
    	
		return responseXml;

	}

	/**
	 * This method return product set
	 * 
	 * @param cycle
	 * @return
	 */
	public ProductSet getEdition(ChartCycleElementsJson cycle) {
    	ProductSet responseXml = initPositiveResponse();
    	
    	ProductSet.Edition ed = initEdition(cycle);
    	
    	responseXml.getEdition().add(ed);    	
    	return responseXml;   	
	}

	private boolean validateRequest(String ed, String fmt) {

		if (!verifyEdition()) {
			logger.error("Expected edition 'current or next' not received '"
					+ ed
					+ "' instead. Error response being generated and returned back.");
			response = getIllegalArgumentError();
			return false;
		}
    	if (!verifyFormat()) {
    		logger.error("Expected format of 'tiff' or 'pdf'. Received format '"+fmt+"' instead. Error response being generated and returned");
    		response = getIllegalArgumentError();
    		return false;
    	}

		if (CURRENT.equalsIgnoreCase(this.getEdition())) {
			cycle = new WallPlanningChartCycleClient().getCurrentCycle();
			if (cycle == null) 
				logger.warn("Chart cycle for Wall Planning chart CURRENT edition is NULL!");
		} else {
			cycle = new WallPlanningChartCycleClient().getNextCycle();
			if (cycle == null) 
				logger.warn("Chart cycle for Wall Planning chart CURRENT edition is NULL!");
		}

		if (cycle == null) {
			logger.warn("Unable to locate " + this.getEdition()
					+ " edition chart for " + this.getFormat());
			response = this.getErrorResponse(404, ErrorCodes.ERROR_404);
			return false;
		}

		if (!validateParameters(cycle)) {
			logger.error("Parameters validation failed in getProductRelease.");
			response = getErrorResponse(404, ErrorCodes.ERROR_404);
			return false;
		}

		return true;
	}

	private boolean validateParameters(ChartCycleElementsJson cycle) {
		return cycle.getChart_effective_date() != null;
	}

}
