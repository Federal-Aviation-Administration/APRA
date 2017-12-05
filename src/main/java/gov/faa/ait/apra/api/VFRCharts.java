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
import gov.faa.ait.apra.jaxb.EditionCodeList;
import gov.faa.ait.apra.jaxb.ObjectFactory;
import gov.faa.ait.apra.jaxb.ProductCodeList;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.jaxb.ProductSet.Edition;
import gov.faa.ait.apra.jaxb.ProductSet.Status;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;
import gov.faa.ait.apra.cycle.VFRChartCycleClient;
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
import java.text.SimpleDateFormat;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides the VFR charts and specifically charts for the Grand Canyon
 * area. 
 * 
 * @author FAA
 *
 */

@Api(value = "Grand Canyon VFR Chart")
@Path("/vfr/grandcanyon")
public class VFRCharts extends BaseService {
	private URL downloadURL = null;
	private ProductSet response = null;
	private ChartCycleElementsJson cycle = null;

	private static final Logger logger = LoggerFactory
			.getLogger(VFRCharts.class);

	/**
	 * This is the VFR chart download URL. A single parameter is provided to
	 * retrieve the URL for either the current or the next edition
	 * 
	 * @param ed the edition of either CURRENT or NEXT
	 * @param geo the geographic area desired
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	@Path("/chart")
	@ApiOperation(value = "Get VFR Grand Canyon chart edition information and download link", nickname="getGrandCanyonProductRelease", response = ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
	
	public Response getGrandCanyonRelease(
			@ApiParam(name="edition", value="Requested product edition. If omitted, the default current edition is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false) @QueryParam("edition") String ed) {

		logger.info("Received call to retrieve current VFR product release for edition '"
				+ ed + "'.");
		this.setGeoname("Grand_Canyon");
		ObjectFactory of = new ObjectFactory();

		response = of.createProductSet();

		if (!validateRequest(ed)) {
	    	return Response.status(response.getStatus().getCode()).entity(response).build();
		}

		ProductSet ps = getRelease(cycle);
		return Response.status(ps.getStatus().getCode()).entity(ps).build();
	}

	/**
	 * 
	 * This method produces the edition response for the Grand Canyon charts, but
	 * omits the download URL for the charts. This is the edition information only.
	 * 
	 * @param ed the edition to be returned either CURRENT or NEXT
	 * @param geo the target geographic area for the response
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	@Path("/info")
	@ApiOperation(value = "Get VFR edition date and edition number by edition type of current or next", nickname="getGrandCanyonEdition", response = ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})

	public Response getGrandCanyonEdition(
			@ApiParam(name="edition", value="Requested product edition. If omitted, the default current edition information is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false) @QueryParam("edition") String ed) {

		logger.info("Received call to retrieve current VFR product edition for edition '"
				+ ed + "'.");

		this.setGeoname("Grand_Canyon");
		ObjectFactory of = new ObjectFactory();

		response = of.createProductSet();

		if (!validateRequest(ed)) {
	    	return Response.status(response.getStatus().getCode()).entity(response).build();

		}

		ProductSet ps = getEdition(cycle);
		return Response.status(ps.getStatus().getCode()).entity(ps).build();
	}

	/**
	 * This method builds the chart release URL using the cycle infomration and edition
	 * requested.
	 * 
	 * @param cycle the airspace cycle information for the desired release
	 * @return
	 */

	public ProductSet getRelease(ChartCycleElementsJson cycle) {
		StringBuilder vfrPath = new StringBuilder(Config.getVFRUploadFolder());
		
		try {

			// the path looks like this:
			// /content/aeronav/grand_canyon_files/Grand_Canyon_<cycle_numbe>.zip
			StringBuilder fileName = new StringBuilder(getGeoname());
			fileName.append("_");
			fileName.append(cycle.getChart_cycle_number());
			fileName.append(".zip");
			
			vfrPath.append("/").append(fileName);

			downloadURL = new URL(Config.getAeronavHost()
					+ vfrPath.toString());
			if (!verifyURL(downloadURL)) {
				logger.warn(downloadURL.toExternalForm()
						+ " returned a non 200 response code when completing a HTTP HEAD check.");
				downloadURL = null;
			}
		} catch (MalformedURLException emalformed) {
			logger.error("getRelease", emalformed);
			response = getErrorResponse(500,
					"Unable to construct a valid URL for the VFR product release.");
		}

		return buildResponse(cycle);

	}

	@Override
	public ProductSet buildResponse(ChartCycleElementsJson cycle) {
		ObjectFactory of = new ObjectFactory();
		Status status = of.createProductSetStatus();
		status.setCode(200);
		status.setMessage("OK");

		ProductSet.Edition ed = of.createProductSetEdition();

		Edition.Product product = new Edition.Product();

		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		ed.setEditionDate(formatter.format(cycle.getChart_effective_date()));
		ed.setEditionNumber(Integer.valueOf(cycle.getChart_cycle_number()));
		ed.setEditionName(EditionCodeList.valueOf(cycle
				.getChart_cycle_period_code()));
		ed.setGeoname(this.getGeoname());
		product.setProductName(ProductCodeList.VFR);

		if (downloadURL != null) {
			product.setUrl(downloadURL.toExternalForm());
		} else {
			status.setCode(404);
			status.setMessage(ErrorCodes.ERROR_404);
			product.setUrl("");
		}
		ed.setProduct(product);
		response.setStatus(status);
		response.getEdition().add(ed);

		return response;

	}

	/**
	 * Get the edition information block as a response including the date, format, 
	 * and edition information.
	 * 
	 * @param cycle the airspace cycle information for the edition to be returned
	 * @return
	 */

	public ProductSet getEdition(ChartCycleElementsJson cycle) {
		ObjectFactory of = new ObjectFactory();
		Status status = of.createProductSetStatus();

		ProductSet.Edition ed = of.createProductSetEdition();

		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		ed.setEditionDate(formatter.format(cycle.getChart_effective_date()));
		ed.setEditionNumber(Integer.valueOf(cycle.getChart_cycle_number()));
		ed.setEditionName(EditionCodeList.valueOf(cycle
				.getChart_cycle_period_code()));
		ed.setGeoname(this.getGeoname());
		response.getEdition().add(ed);
		status.setCode(200);
		status.setMessage("OK");
		response.setStatus(status);

		return response;
	}

	private boolean validateParameters(ChartCycleElementsJson cycle) {
		if (logger.isDebugEnabled()) {
			logger.debug("Cycle city = "+cycle.getChart_city_name()+" this.formatCity() = "+this.formatCity());
			logger.debug("Chart cycle effective date is "+cycle.getChart_effective_date());
			logger.debug("Validation result is "+(cycle.getChart_effective_date() != null && cycle.getChart_city_name().equalsIgnoreCase(this.formatCity())));
		}
		return cycle.getChart_effective_date() != null
				&& cycle.getChart_city_name().equalsIgnoreCase(this.formatCity());
	}

	private boolean validateRequest(String ed) {

		if (ed == null || ed.isEmpty()) {
			this.setEdition(CURRENT);
		} else {
			this.setEdition(ed);
		}

		if (!verifyEdition()) {
			logger.error("Expected edition 'current or next' not received '"
					+ ed
					+ "' instead. Error response being generated and returned back.");
			response = getIllegalArgumentError();
			return false;
		}

		if (CURRENT.equalsIgnoreCase(this.getEdition())) {
			cycle = new VFRChartCycleClient().getCurrentCycle();
		} else {
			cycle = new VFRChartCycleClient().getNextCycle();
		}

		if (cycle == null) {
			logger.warn("Chart cycle infomration was NULL for "+this.getEdition()+" "+this.getGeoname());
			logger.warn("Unable to locate " + this.getEdition()
					+ " edition chart for " + this.getGeoname());
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

	private String formatCity() {
		String scratch = new String(this.getGeoname());
		char[] separators = { '-', '_', ' ' };

		String retVal = scratch.replace(" ", "_");
		retVal = WordUtils.capitalizeFully(retVal, separators);

		logger.info("Converted " + this.getGeoname() + " to " + retVal);

		if (logger.isDebugEnabled())
			logger.debug("Converted " + this.getGeoname() + " to " + retVal);

		return retVal;
	}
}
