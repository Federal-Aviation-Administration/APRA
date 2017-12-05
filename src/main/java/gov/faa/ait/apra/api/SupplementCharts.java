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
import gov.faa.ait.apra.jaxb.FormatCodeList;
import gov.faa.ait.apra.jaxb.ObjectFactory;
import gov.faa.ait.apra.jaxb.ProductCodeList;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.jaxb.ProductSet.Edition;
import gov.faa.ait.apra.jaxb.ProductSet.Edition.Product;
import gov.faa.ait.apra.json.SupplementMetadata;

import gov.faa.ait.apra.cycle.ChartCycleClient;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;

import gov.faa.ait.apra.util.SupplementMetadataClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static gov.faa.ait.apra.bootstrap.ErrorCodes.ERROR_400;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.ERROR_404;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.ERROR_500;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.RESPONSE_200;

import java.text.SimpleDateFormat;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class services requests for the Supplement Chart. Currently, the allowed
 * publication sets are US complete set and state complete set.
 * 
 * @author FAA
 */
@Path("/supplement")
@Api(value = "Supplement Chart ")
public class SupplementCharts extends BaseService {
	private static final Logger logger = LoggerFactory
			.getLogger(SupplementCharts.class);
	private static final String US = "US";
	private static final String ERRMSGSUFFIX = " instead. Error response being generated and returned.";
	private String volume = "";
	private static final String CSALL = "CS_ALL_";

/**
 * Get Supplement chart download information
 * @param ed - edition	
 * @param vol - volume
 * @return
 */

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	@Path("/chart")
	@ApiOperation(value = "Get Supplement chart download information by requesting an edition with a valid US volume.", 
			notes="The Supplement chart is distributed in two formats - zip and pdf. The US complete set is returned as a ZIP file while all other volumes consist of individual PDF files."			
			+ "Requests for charts by volume other than US complete set returns a list of download URLs which can be quite extensive."
			, response = ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
	public Response getSupplementRelease(
			@ApiParam(name = "edition", value = "Requested product edition. If omitted, the default current edition is returned.", allowableValues = "current, next", defaultValue = "current", allowMultiple = false, required = false) @QueryParam("edition") String ed,
			@ApiParam(name = "volume", value = "Requested volume of Supplement chart set. If omitted, the complete US set is returned.", allowableValues = "NORTHWEST, SOUTHWEST, NORTH CENTRAL, SOUTH CENTRAL, EAST CENTRAL, SOUTHEAST, NORTHEAST, PACIFIC, ALASKA", allowMultiple = false, required = false) @QueryParam("volume") String vol) {
		ChartCycleElementsJson cycle;

		logger.info("Received call to retrieve current Supplement release for edition '"
				+ ed + "' volume '"+ vol+"'.");

		if (vol == null || vol.isEmpty()) {
			this.setVolume(US);
			setFormat(ZIP);
		} else {
			this.setVolume(vol);
		}

		if (ed == null || ed.isEmpty()) {
			this.setEdition(CURRENT);
		} else {
			this.setEdition(ed);
		}

		cycle = initParameters();

		if (!verifyEdition()) {
			logger.error("Expected edition current, next, or changeset and received '"
					+ ed + "'" + ERRMSGSUFFIX);
    		return Response.status(400).entity(getErrorResponse(400,
					"Edition must be current, next, or changeset.")).build();
		}

		if ( !isUnitedStates()) {
			logger.info("Retrieving individual Supplement charts rather than full US set. User asked for a volume or US changes.");
			setFormat(PDF);
			ProductSet ps = getChartProductSet(cycle);
	    	return Response.status(ps.getStatus().getCode()).entity(ps).build();

		}
		// By default, we return the zippped US product set
		ProductSet ps =  buildResponse(cycle);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();

	}

	/**
	 * Get Supplement chart edition information
	 * @param ed - edition
	 * @param vol - volume
	 * @return
	 */

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	@Path("/info")
	@ApiOperation(value = "Get Supplement chart edition information by requesting an edition and volume.", response = ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
	public Response getSupplementEdition(
			@ApiParam(name = "edition", value = "Requested product edition. If omitted, the default current edition is returned.", allowableValues = "current, next", defaultValue = "current", allowMultiple = false, required = false) @QueryParam("edition") String ed,
			@ApiParam(name = "volume", value = "Requested volume of Supplement chart set. If omitted, the edition information for the complete US set is returned.", allowableValues = "NORTHWEST, SOUTHWEST, NORTH CENTRAL, SOUTH CENTRAL, EAST CENTRAL, SOUTHEAST, NORTHEAST, PACIFIC, ALASKA", allowMultiple = false, required = false) @QueryParam("volume") String vol) {
		ChartCycleElementsJson cycle;

		logger.info("Received call to retrieve current SUPPLEMENT product release for edition '"
				+ ed + "'.");

		if (vol == null || vol.isEmpty()) {
			this.setVolume(US);
			setFormat(ZIP);
		} else {
			this.setVolume(vol);
			setFormat(PDF);
		}

		if (ed == null || ed.isEmpty()) {
			this.setEdition(CURRENT);
		} else {
			this.setEdition(ed);
		}

		cycle = initParameters();

		if (!verifyEdition()) {
			logger.error("Expected edition 'current' or 'next' and received '"
					+ ed
					+ "' instead. Error response being generated and returned.");
			
	   		return Response.status(400).entity(getErrorResponse(400,
					"Edition must be current, next, or changeset.")).build();
		}
		if ( !isUnitedStates()) {
			logger.info("Retrieving individual Supplement charts rather than full US set. User asked for a volume or US changes.");
			ProductSet ps = getEditionResponse(cycle);
	    	return Response.status(ps.getStatus().getCode()).entity(ps).build();

		}
		ProductSet ps = getUSEditionResponse(cycle);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();

	}

	@Override
	protected ProductSet buildResponse(ChartCycleElementsJson cycle) {
		StringBuilder path = new StringBuilder(Config.getSUPUSPath());
		ObjectFactory of = new ObjectFactory();
		ProductSet ps = initPositiveResponse();
		Edition ed = initEdition(cycle);
		Product product = of.createProductSetEditionProduct();
		path.append("/").append(CSALL);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		path.append(
				formatter.format(cycle.getChart_effective_date())).append(".zip");
		product.setProductName(ProductCodeList.SUPPLEMENT);
		validateAndSetUrl(Config.getAeronavHost() + path.toString(), ps, product);
		ed.setProduct(product);
		ps.getEdition().add(ed);

		return ps;
	}

	private boolean getChartEditiontSet(ChartCycleElementsJson cycle) {

		SupplementMetadataClient supplementClient = new SupplementMetadataClient(
				cycle);
		SupplementMetadata[] elements = supplementClient
				.getChartMetadataByVolume(getVolume()).getElements();

		if (elements == null || elements.length == 0){
			return false;
		}
		return true;
	}
	
	private ProductSet getChartProductSet(ChartCycleElementsJson cycle) {
		String wcf = " with change flag = ";
		logger.info("Getting the chart product set for " + getEdition() + " "
				+ capitalizeGeoname() + wcf + isChangeFlag());
		ObjectFactory of = new ObjectFactory();
		SupplementMetadataClient supplementClient = new SupplementMetadataClient(
				cycle);
		SupplementMetadata[] elements = supplementClient
				.getChartMetadataByVolume(getVolume()).getElements();

		if (elements == null || elements.length == 0)
			return getErrorResponse(404, ErrorCodes.ERROR_404);

		logger.info(elements.length + " total charts found for " + getEdition()
				+ " " + capitalizeGeoname() + wcf + isChangeFlag());

		ProductSet ps = initPositiveResponse();

		for (int i = 0; i < elements.length; i++) {
			StringBuilder path = new StringBuilder(Config.getSUPChartPath());
			Edition ed = initEdition(cycle);
			ed.setFormat(FormatCodeList.PDF);
			ed.setGeoname(elements[i].getState());
			ed.setVolume(elements[i].getVolumeName());

			Product product = of.createProductSetEditionProduct();
			product.setProductName(ProductCodeList.SUPPLEMENT);
			SimpleDateFormat formatter = new SimpleDateFormat("ddMMMyyyy");
			path.append("/").append(
					formatter.format(cycle.getChart_effective_date()));
			path.append("/").append(elements[i].getPdf());

			product.setUrl(Config.getAeronavHost() + path.toString());

			if (Config.getSUPCheckFlag()) {
				logger.warn("URL validation check is enabled for the Supplement product set. This can cause serious performance issues for the Supplement product responses."
						+ " Consider changing the configuration parameter gov.faa.ait.sup.check.flag = false and re-deploy.");
				validateAndSetUrl(Config.getAeronavHost() + path.toString(),
						ps, product);
			}

			ed.setProduct(product);
			ps.getEdition().add(ed);
		}

		return ps;
	}

	private ChartCycleElementsJson initParameters() {
		ChartCycleElementsJson cycle;

		if (CURRENT.equalsIgnoreCase(this.getEdition())) {
			cycle = new ChartCycleClient().getCurrent56DayCycle();
		} else {
			cycle = new ChartCycleClient().getNext56DayCycle();
		}

		return cycle;
	}

	/**
	 * Get the edition information response using the specified chart cycle
	 * retrieved from the chart cycle resource
	 * 
	 * @param cycle
	 *            the chart cycle of either current or next 28 day cycle
	 * @return
	 */
	public ProductSet getEditionResponse(ChartCycleElementsJson cycle) {
		ProductSet response = initPositiveResponse();
		if(getChartEditiontSet(cycle)){
			Edition ed = initEdition(cycle);
			ed.setVolume(getVolume());
			response.getEdition().add(ed);
		}else{
			response = this.getErrorResponse(404, ErrorCodes.ERROR_404);
		}
		return response;
	}
	
	private ProductSet getUSEditionResponse(ChartCycleElementsJson cycle) {
		ProductSet response = initPositiveResponse();
		Edition ed = initEdition(cycle);
		ed.setVolume(getVolume());
		response.getEdition().add(ed);		
		return response;
	}

	private boolean isUnitedStates() {
		return US.equalsIgnoreCase(getVolume());
	}

	private void setVolume(String vol) {
		this.volume = vol;
	}

	private String getVolume() {
		return this.volume;
	}
}
