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
import gov.faa.ait.apra.jaxb.AltitudeCategoryCodeList;
import gov.faa.ait.apra.jaxb.EditionCodeList;
import gov.faa.ait.apra.jaxb.ObjectFactory;
import gov.faa.ait.apra.jaxb.ProductCodeList;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.jaxb.ProductSet.Edition;
import gov.faa.ait.apra.jaxb.ProductSet.Status;
import gov.faa.ait.apra.path.PathElement;
import gov.faa.ait.apra.path.ProductPath;
import gov.faa.ait.apra.cycle.ChartCycleClient;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author FAA
 *
 */

@Path("/enroute")
@Api(value = "IFR Enroute Charts")
public class IFREnrouteCharts extends BaseService {
	private static final String MM_DD_YYYY2 = "MM-dd-yyyy";
	private static final String MM_DD_YYYY = "MM/dd/yyyy";
	private static final String PACIFIC = "PACIFIC";
	private static final String CARIBBEAN = "CARIBBEAN";
	private static final String AREA = "AREA";
	private static final String HIGH = "HIGH";
	private static final String LOW = "LOW";
	private static final String US = "US";
	private String seriesType = "";
	private URL downloadURL = null;
	private ProductSet response = null;
	private ChartCycleElementsJson cycle = null;
	private ChartCycleClient client;
	private static final Logger logger = LoggerFactory
			.getLogger(IFREnrouteCharts.class);
	private static final String ALASKA = "Alaska";
	
	/**
	 * Default null constructor that initializes the chart cycle
	 */
	public IFREnrouteCharts() {
		this.client = new ChartCycleClient();
	}
	
	/**
	 * Construct the IFR Enroute charts services with a specific chart cycle client already constructed and passed as a parameter.
	 * @param client the chart cycle client to use for this service
	 */
	public IFREnrouteCharts(ChartCycleClient client) {
		this.client = client;
	}

	/**
	 * This is the base chart download URL. A single parameter is provided to
	 * retrieve the URL for either the current or the next edition
	 * 
	 * @param ed
	 *            - edition is either current or next, default one is current
	 * @param fmt
	 *            - format is either pdf or tiff, default one is pdf. This
	 *            parameter is mandatory
	 * @param geo
	 *            - geoname is either US or Alaska. This parameter is mandatory
	 * @param seriesType
	 *            - seriesType is either low, high, or area. This parameter is mandatory
	 * @return ProductSet with product details.
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	@Path("/chart")
	@ApiOperation(value = "Get IFR Enroute Charts download link by edition, format, geoname, and seriesType", 
		notes = "TIFF formatted files are geo-referenced while PDF format is not geo-referenced. Geoname is either US, Alaska, Pacific, or Caribbean, "
			+ "depending on the desired chart. A list of available charts by format, geoname, and series type can be found "
			+ "on the FAA public web site at FAA Home > Air Traffic > Flight Information > Aeronautical Information Services "
			+ " > Digital Products > IFR Charts and DERS > Low, High Areas tab. "
			+ " The valid values for seriesType are Low, High, or Area.", response = ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
	public Response getIFREnrouteRelease(
			@ApiParam(name = "edition", value = "Requested product edition. If omitted, the default current edition is returned.", allowableValues = "current, next", defaultValue = "current", allowMultiple = false, required = false) @QueryParam("edition") String ed,
			@ApiParam(name = "format", value = "Format of the requested chart. TIFF is georeferenced and PDF is not georeferenced"
					+ "If omitted, the default format of PDF is returned.", allowableValues = "tiff, pdf", defaultValue = "pdf", allowMultiple = false, required = false) @QueryParam("format") String fmt,
			@ApiParam(name = "geoname", value = "Geographic region for requested chart", allowableValues="US, Alaska, Pacific, Caribbean", required = true) @QueryParam("geoname") String geo,
			@ApiParam(name = "seriesType", value = "The series type", allowableValues="low, high, area", required = true) @QueryParam("seriesType") String seriesType) {

		logger.info("Received call to retrieve current IFR Enroute Charts product release for '"
				+ ed + "', '" + fmt + "', '" + geo + "', '" + seriesType + "'");
		ObjectFactory of = new ObjectFactory();

		response = of.createProductSet();

		if (!validateRequest(ed, fmt, geo, seriesType)) {
	    	return Response.status(response.getStatus().getCode()).entity(response).build();

		}

		ProductSet ps = getRelease(cycle);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();

	}

	/**
	 * This is the base chart download URL. A single parameter is provided to
	 * retrieve the URL for either the current or the next edition
	 * 
	 * @param ed
	 *            - edition is either current or next, default one is current
	 * @return ProductSet with product details.
	 */

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	@Path("/info")
	@ApiOperation(value = "Get IFR Enroute Charts edition date and edition number by edition type of current or next", response = ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
	public Response getIFREnrouteEdition(
			@ApiParam(name = "edition", value = "Requested product edition", allowableValues = "current, next", defaultValue = "current", 
				allowMultiple = false, required = false) @QueryParam("edition") String ed) {

		logger.info("Received call to retrieve current IFR Enroute Charts edition release for '"
				+ ed);

		ObjectFactory of = new ObjectFactory();

		response = of.createProductSet();

		if (!this.validateRequest(ed, PDF, ALASKA, LOW)) {
	    	return Response.status(response.getStatus().getCode()).entity(response).build();
		}
		this.setGeoname(null);
		this.setSeriesType(null);
		this.setFormat(null);
		ProductSet ps = getEdition(cycle);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();

	}

	/**
	 * 
	 * @param cycle
	 * @return
	 */

	public ProductSet getRelease(ChartCycleElementsJson cycle) {
		ObjectFactory of = new ObjectFactory();
		Status status = of.createProductSetStatus();
		status.setCode(200);
		status.setMessage("OK");
		this.response.setStatus(status);


		// get set count by format, high-low, and geo area
		int setCount = Config.getEnrouteSetCount(this.getGeoname(), this.getSeriesType(), this.getFormat());
		
		int step = PDF.equalsIgnoreCase(this.getFormat()) ? 2 : 1;
		boolean anyUrlSet = false;
		for(int i=1; i<=setCount; i+=step) {
			ProductPath vfrPath = new ProductPath();
			vfrPath.addPathElement(new PathElement(Config.getEnrouteFolder()));
			ProductSet.Edition ed = of.createProductSetEdition();

			Edition.Product product = new Edition.Product();

			SimpleDateFormat sdfUS = new SimpleDateFormat(MM_DD_YYYY);
			ed.setEditionDate(sdfUS.format(cycle.getChart_effective_date()));
			ed.setEditionNumber(Integer.valueOf(cycle.getChart_cycle_number()));
			ed.setEditionName(EditionCodeList.valueOf(cycle
					.getChart_cycle_period_code()));
			ed.setGeoname(this.getGeoname());
			ed.setFormat(gov.faa.ait.apra.jaxb.FormatCodeList.valueOf(this.getFormat()));
			try {
				SimpleDateFormat sdfUSDash = new SimpleDateFormat(MM_DD_YYYY2);
				PathElement peDir = new PathElement(sdfUSDash.format(cycle
						.getChart_effective_date()));
				vfrPath.addPathElement(peDir);
				
				String fileName = this.buildFileName(this.getGeoname(), this.getFormat(), this.seriesType, i);
				PathElement pe = new PathElement(fileName);
				pe.setFile();
				vfrPath.addPathElement(pe);
	
				downloadURL = new URL(Config.getAeronavHost()
						+ vfrPath.getPathAsString());
				if (!verifyURL(downloadURL)) {
					logger.warn(downloadURL.toExternalForm()
							+ " returned a non 200 response code when completing a HTTP HEAD check.");
					downloadURL = null;
				}
				if(downloadURL != null) {
					anyUrlSet = true;
					product.setUrl(downloadURL.toString());
				}
				ed.setProduct(product);
			} catch (MalformedURLException emalformed) {
				logger.error("getRelease", emalformed);
				response = getErrorResponse(500,
						"Unable to construct a valid URL for the VFR product release.");
			}
			response.getEdition().add(ed);
		}
		
		if(!anyUrlSet) {
			response = getErrorResponse(404, ErrorCodes.ERROR_404);
		}
		return response;

	}
	/**
	 * builds filename (not directory)
	 * @param area
	 * @param format
	 * @param altLevel
	 * @param setIndex
	 * @return
	 */
	private String buildFileName(String geoname, String format, String altLevel, int setIndex) {
		StringBuilder filename = new StringBuilder();
		if(PDF.equalsIgnoreCase(format)) {
			filename.append("d");
		}
		logger.debug("computing filename for "+ geoname + ", "+format+", "+ altLevel +", "+ setIndex);
		if(US.equalsIgnoreCase(geoname) ) {
			if( TIFF.equalsIgnoreCase(format) ) {
				if (LOW.equalsIgnoreCase(altLevel)) {
					filename.append("enr_l").append(String.format("%02d", setIndex));
				} else if (HIGH.equalsIgnoreCase(altLevel)) {
					filename.append("enr_h").append(String.format("%02d", setIndex));
				} else if (US.equalsIgnoreCase(geoname) && TIFF.equalsIgnoreCase(format) && AREA.equalsIgnoreCase(altLevel)) {
					filename.append("enr_a").append(String.format("%02d", setIndex));
				}
			} else if (PDF.equalsIgnoreCase(format) ) {
				if (LOW.equalsIgnoreCase(altLevel)) {
					filename.append("elus").append(setIndex);
				} else if (HIGH.equalsIgnoreCase(altLevel)) {
					filename.append("ehus").append(setIndex);
				} else if (US.equalsIgnoreCase(geoname) && PDF.equalsIgnoreCase(format) && AREA.equalsIgnoreCase(altLevel)) {
					filename.append("area");
				}
			}
		} else if(ALASKA.equalsIgnoreCase(geoname)) {
			if (TIFF.equalsIgnoreCase(format)) {
				if(LOW.equalsIgnoreCase(altLevel)) {
					filename.append("enr_akl").append(String.format("%02d", setIndex));
				} else if(HIGH.equalsIgnoreCase(altLevel)) {
					filename.append("enr_akh").append(String.format("%02d", setIndex));
				}
			} else if(PDF.equalsIgnoreCase(format)) {
				if (LOW.equalsIgnoreCase(altLevel)) {
					filename.append("elak").append(setIndex);
				} else if(HIGH.equalsIgnoreCase(altLevel)) {
					filename.append("ehak").append(setIndex);
				}
			}
		} else if (PACIFIC.equalsIgnoreCase(geoname)) {
			if (TIFF.equalsIgnoreCase(format)) {
				if (HIGH.equalsIgnoreCase(altLevel)) {
					filename.append("enr_p").append(String.format("%02d", setIndex));
				}
			} else if (PDF.equalsIgnoreCase(format) ) {
				if (HIGH.equalsIgnoreCase(altLevel)) {
					filename.append("ephi").append(setIndex);
				}	
			}
		} else if (CARIBBEAN.equalsIgnoreCase(geoname)) {
			if (PDF.equalsIgnoreCase(format)) {
				if (LOW.equalsIgnoreCase(altLevel)) {
					filename.append("elcb").append(setIndex);
				} else if (HIGH.equalsIgnoreCase(altLevel)) {
					filename.append("ehcb").append(setIndex);
				} else if (AREA.equalsIgnoreCase(altLevel) ) {
					if (setIndex == 3) {
						filename.append("elcb3"); // special case where area and low are zipped together
					} else {
						filename.append("elcba").append(setIndex);
					}
				}
			}
		}
		
		filename.append(".zip");
		
		return filename.toString();
	}

	@Override
	public ProductSet buildResponse(ChartCycleElementsJson cycle) {
		ObjectFactory of = new ObjectFactory();
		Status status = of.createProductSetStatus();
		status.setCode(200);
		status.setMessage("OK");

		ProductSet.Edition ed = of.createProductSetEdition();

		Edition.Product product = new Edition.Product();

		SimpleDateFormat formatter = new SimpleDateFormat(MM_DD_YYYY);
		ed.setEditionDate(formatter.format(cycle.getChart_effective_date()));
		ed.setEditionNumber(Integer.valueOf(cycle.getChart_cycle_number()));
		ed.setEditionName(EditionCodeList.valueOf(cycle
				.getChart_cycle_period_code()));
		ed.setGeoname(this.getGeoname());
		if(HIGH.equalsIgnoreCase(this.getSeriesType())){
			ed.setAltitude(AltitudeCategoryCodeList.HIGH);
		}else{
			ed.setAltitude(AltitudeCategoryCodeList.LOW);
		}
		product.setProductName(ProductCodeList.IFR_ENROUTE);

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
	 * 
	 * @param cycle
	 * @return
	 */

	public ProductSet getEdition(ChartCycleElementsJson cycle) {
		ObjectFactory of = new ObjectFactory();
		Status status = of.createProductSetStatus();

		ProductSet.Edition ed = of.createProductSetEdition();
		SimpleDateFormat formatter = new SimpleDateFormat(MM_DD_YYYY);
		ed.setEditionDate(formatter.format(cycle.getChart_effective_date()));
		ed.setEditionNumber(Integer.valueOf(cycle.getChart_cycle_number()));
		ed.setEditionName(EditionCodeList.valueOf(cycle
				.getChart_cycle_period_code()));
		response.getEdition().add(ed);
		status.setCode(200);
		status.setMessage("OK");
		response.setStatus(status);

		return response;
	}

	private boolean validateParameters(ChartCycleElementsJson cycle) {

		return cycle.getChart_effective_date() != null;
	}

	private boolean validateRequest(String ed, String fmt, String geo,
			String seriesType) {

		if (ed == null || ed.isEmpty()) {
			this.setEdition(CURRENT);
		} else {
			this.setEdition(ed);
		}

		if (fmt == null || fmt.isEmpty()) {
			this.setFormat("pdf");
		} else {
			this.setFormat(fmt);
		}

		this.setGeoname(geo);

		this.setSeriesType(seriesType);

		if (!verifyEdition()) {
			logger.error("Expected edition 'current or next' not received '"
					+ ed
					+ "' instead. Error response being generated and returned back.");
			response = getIllegalArgumentError();
			return false;
		}

		if (!verifyFormat()) {
			logger.error("Expected format of 'tiff' or 'pdf'. Received format '"
					+ fmt
					+ "' instead. Error response being generated and returned");
			response = getIllegalArgumentError();
			return false;
		}

		if (!verifyGeo()) {
			logger.error("Expected 'geo' value, but it is null or empty '"
					+ geo
					+ "' Geoname which is a city for which the chart is requested.");
			response = this
					.getErrorResponse(
							404,
							"A Geoname value  is either 'US', 'Alaska', 'Pacific' or 'Caribbean', must be specified for IFR Enroute charts.");
			return false;

		}

		if (!verifySeriesType()) {
			logger.error("Expected 'alt' value, but it is null or empty '"
					+ seriesType
					+ "' seriesType the chart is requested which is either 'Low', 'high', or 'area'.");
			response = this
					.getErrorResponse(
							404,
							"A seriesType value  is either 'Low','high', or 'area', must be specified for IFR Enroute charts.");
			return false;

		}

		
		if (CURRENT.equalsIgnoreCase(this.getEdition())) {
			cycle = this.client.getCurrent56DayCycle();
		} else {
			cycle = this.client.getNext56DayCycle();
		}

		if (cycle == null) {
			logger.warn("Unable to locate " + this.getEdition()
					+ " edition chart for geoname " + this.getGeoname());
			response = this
					.getErrorResponse(
							404,
							"Unable to locate " + this.getEdition()
									+ " edition chart for geoname "
									+ this.getGeoname());
			return false;
		}

		if (!validateParameters(cycle)) {
			logger.error("Parameters validation failed in getProductRelease.");
			response = getErrorResponse(404,
					ErrorCodes.ERROR_404);
			return false;
		}

		return true;
	}

	private boolean verifyGeo() {
		return "Alaska".equalsIgnoreCase(this.getGeoname()) || US.equalsIgnoreCase(this.getGeoname()) || CARIBBEAN.equalsIgnoreCase(this.getGeoname()) || PACIFIC.equalsIgnoreCase(this.getGeoname()	);		
	}

	private boolean verifySeriesType() {
		return "Low".equalsIgnoreCase(this.getSeriesType()) || "High".equalsIgnoreCase(this.getSeriesType()) || AREA.equalsIgnoreCase(this.getSeriesType());		
	}

	private void setSeriesType(String seriesType) {
		this.seriesType = seriesType;
	}

	private String getSeriesType() {
		return this.seriesType;
	}

}
