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
package gov.faa.ait.apra.bootstrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * This class loads the configuration values and properties that we prefer not to hard code. 
 * Some of the values are changing infrequently, but you never know. The hostnames, in 
 * particular, could change. So default values are provided in static finals and overridden 
 * by the values in the properties file that is delivered by Ansible.
 * 
 * @author FAA
 *
 */
public class Config {
	private static final Logger logger  = LoggerFactory.getLogger(Config.class);
	private static final Properties cfg = new Properties();
	private static final String AERONAV_HOST="http://aeronav.faa.gov";
	private static final String DDOF_HOST="http://tod.faa.gov";
	private static final String NFDC_HOST="http://nfdc.faa.gov";
	
	private static final String CIFP_FILE_PREFIX="cifp_";

	private static final String DENODO_HOST="https://soadev.smext.faa.gov";
	private static final String FAA_DMZ_PROXY_HOST=null;
	private static final String FAA_DMZ_PROXY_PORT="8080";	
	private static final String CYCLE_AGE_LIMIT = "1";
	private static final String AERONAV_SECTIONAL_FOLDER="/content/aeronav/sectional_files";
	private static final String WALLPLAN_UPLOAD_FOLDER="/content/aeronav/grand_canyon_files";
	private static final String VFR_UPLOAD_FOLDER="/content/aeronav/grand_canyon_files";
	private static final String DENODO_VIEW_PATH="/denodo/apra/server/ifpa/edai/views";
	private static final String DENODO_CYCLE_RESOURCE="/denodo/apra/server/ifpa/edai/views/chart_cycle";
	private static final String DENODO_VFR_CYCLE_RESOURCE="/denodo/apra/server/ifpa/edai/views/vfr_chart_cycle";
	private static final String ENROUTE_FOLDER = "enroute";
	private static final String TPP_CHECK_FLAG = "false";
	private static final String SUP_CHECK_FLAG = "false";
	private static final String SEC_CHECK_FLAG = "false";
	private static final String NASR_CHECK_FLAG = "true";

	// These are the variables related to the path construction for various chart sets. Each path is contained within the 
	// configuration properties file and can be overridden or adjusted in the properties file if a path changes or is otherwise
	// broken
	//private static final String NFDC_NASR_PATH="/webContent/56DaySub";

	private static final String NFDC_NASR_PATH="/webContent/28DaySub";
	private static final String CIFP_PATH="/Upload_313-d/cifp";
	private static final String GOM_PATH="/enroute/GoM";
	private static final String TAC_PATH="/content/aeronav/tac_files";
	private static final String TAC_PDF_PATH="/content/aeronav/tac_files/PDFs";
	private static final String TPP_US_PATH="/upload_313-d/terminal";
	private static final String TPP_CHART_PATH="/d-tpp";
	private static final String SUP_PDF_PATH="/afd";
	private static final String SUP_US_PATH="/upload_313-d/supplements";
	private static final String DDOF_PATH="/tod";
	private static final String DEC_PATH="/enroute";
	private static final String HELICOPTER_TIFF_PATH="/content/aeronav/heli_files";
	private static final String HELICOPTER_PDF_PATH="/content/aeronav/heli_files/PDFs";
	private static final String DERS_PATH="/enroute";
	
	// These are the variables related to the file name construction for various charts. Each name is contained within the 
	// configuration properties file and can be overridden or adjusted in the properties file if a file naming convention changes or is otherwise
	// broken
	private static final String GOM_WEST_PDF_FILE="gom_west_pdf.zip";
	private static final String GOM_CENTRAL_PDF_FILE="gom_central_pdf.zip";
	private static final String GOM_WEST_TIFF_FILE="gom_west_tif.zip";
	private static final String GOM_CENTRAL_TIFF_FILE="gom_central_tif.zip";
	// private static final String NASR_FILE_PREFIX="56DySubscription_";
	private static final String NASR_FILE_PREFIX="28DaySubscription_Effective_";
	private static final String DDOF_FILE="DAILY_DOF.ZIP";	
	private static final String DDOF_DAILY_CHANGE_FILE="DOF_DAILY_CHANGE_UPDATE.ZIP";
	private static final String NASR_DATE_FORMAT="yyyy-MM-dd";
	private static final String TPP_US_PREFIX="DDTPP";
	private static final String DEC_FILE_PREFIX="DDECUS";
	private static final String DERS_FILE_PREFIX="DERS_";
	
	private Config () { }
	
	static {
		Config.loadConfig();
	}
	
	/**
	 * Load the configuration properties from the standard location on disk. The location of the properties file is controlled through the Ansible deploy of APRA.
	 * Ansible guarantees that the file is created and will exist with appropriate values per environment. 
	 */
	public static void loadConfig () {
		if (logger.isDebugEnabled())
			logger.debug("Setting configuration of properties for proxy server and URLs");
		
		if (logger.isDebugEnabled())
			logger.debug("Attempting load of properties resources");
		
		try {
			FileInputStream fis = new FileInputStream (new File("/opt/apra/conf/config.properties"));
			cfg.load(fis);	
			logger.info("Configuration properties loaded successfully from /opt/apra/conf/config.properties");
			logger.info("TPP HEAD CHECK FLAG is "+cfg.getProperty("gov.faa.ait.tpp.check.flag"));
			logger.info("TPP HEAD CHECK FLAG is "+Config.getTPPCheckFlag());
		}
		catch (IOException eio) {
			logger.warn("Using default configuration properties. File /opt/apra/conf/config.properties not found.", eio);
		}
	}
	
	public static String getAeronavHost () {
		return cfg.getProperty("gov.faa.ait.aeronav.host", AERONAV_HOST);
	}
	
	public static String getDDOFHost () {
		return cfg.getProperty("gov.faa.ait.ddof.host", DDOF_HOST);
	}
	
	public static String getNFDCHost () {
		return cfg.getProperty("gov.faa.ait.nfdc.host", NFDC_HOST);
	}
	
	public static String getAeronavSectionalFolder () {
		return cfg.getProperty("gov.faa.ait.aeronav.sectional.folder", AERONAV_SECTIONAL_FOLDER);
	}

	public static String getNfdcNasrPath () {
		return cfg.getProperty("gov.faa.ait.nfdc.nasr.path", NFDC_NASR_PATH);
	}
	
	public static String getWallplanUploadFolder () {
		return cfg.getProperty("gov.faa.ait.aeronav.wallplan.upload.folder", WALLPLAN_UPLOAD_FOLDER);
	}
	
	public static String getHelicopterTIFFPath () {
		return cfg.getProperty("gov.faa.ait.aeronav.helicopter.tiff.path", HELICOPTER_TIFF_PATH);
	}
	
	public static String getHelicopterPDFPath () {
		return cfg.getProperty("gov.faa.ait.aeronav.helicopter.pdf.path", HELICOPTER_PDF_PATH);
	}
	
	public static String getVFRUploadFolder () {
		return cfg.getProperty("gov.faa.ait.aeronav.vfr.upload.folder", VFR_UPLOAD_FOLDER);
	}	
	
	public static String getDenodoHost () {
		return cfg.getProperty("gov.faa.ait.denodo.host", DENODO_HOST);
	}	
	
	public static String getDenodoViewPath () {
		return cfg.getProperty("gov.faa.ait.denodo.view.path", DENODO_VIEW_PATH);
	}
	
	public static String getDenodoCycleResource () {
		return cfg.getProperty("gov.faa.ait.denodo.cycle.resource", DENODO_CYCLE_RESOURCE);
	}	
	
	public static String getDenodoVFRCycleResource () {
		return cfg.getProperty("gov.faa.ait.denodo.vfr.cycle.resource", DENODO_VFR_CYCLE_RESOURCE);
	}	
	
	public static String getFAADMZProxyHost () {
		return cfg.getProperty("gov.faa.dmz.proxy.host", FAA_DMZ_PROXY_HOST);
	}
	
	public static String getFAADMZProxyPort () {
		return cfg.getProperty("gov.faa.dmz.proxy.port", FAA_DMZ_PROXY_PORT);
	}
	
	public static int getCycleAgeLimit () {
		String intValue = cfg.getProperty("gov.faa.ait.cycle.ageLimit", CYCLE_AGE_LIMIT);
		return Integer.valueOf(intValue);
	}

	public static String getEnrouteFolder() {
		return cfg.getProperty("gov.faa.ait.aeronav.enroute.upload.folder", ENROUTE_FOLDER);		
	}
	
	public static boolean getTPPCheckFlag() {
		String flag = cfg.getProperty("gov.faa.ait.tpp.check.flag", TPP_CHECK_FLAG);
		
		if (flag == null) 
			return false;
		
		return Boolean.valueOf(flag);	
	}
	
	public static boolean getSectioanlCheckFlag() {
		String flag = cfg.getProperty("gov.faa.ait.sectional.check.flag", SEC_CHECK_FLAG);
		
		if (flag == null) 
			return false;
		
		return Boolean.valueOf(flag);	
	}
	
	public static String getNASRDateFormat() {
		return cfg.getProperty("gov.faa.ait.nasr.date.format", NASR_DATE_FORMAT);		
	}

	public static String getNASRFilePrefix() {
		return cfg.getProperty("gov.faa.ait.nasr.file.prefix", NASR_FILE_PREFIX);		
	}	
	
	public static String getCIFPPath() {
		return cfg.getProperty("gov.faa.ait.cifp.path", CIFP_PATH);		
	}

	public static String getCIFPFilePrefix() {
		return cfg.getProperty("gov.faa.ait.cifp.file.prefix", CIFP_FILE_PREFIX);		
	}
	
	public static String getDECPath() {
		return cfg.getProperty("gov.faa.ait.dec.path", DEC_PATH);		
	}

	public static String getDECFilePrefix() {
		return cfg.getProperty("gov.faa.ait.dec.file.prefix", DEC_FILE_PREFIX);		
	}

	public static String getDERSPath() {
		return cfg.getProperty("gov.faa.ait.ders.path", DERS_PATH);		
	}

	public static String getDERSFilePrefix() {
		return cfg.getProperty("gov.faa.ait.ders.file.prefix", DERS_FILE_PREFIX);		
	}
	
	public static String getTACPath() {
		return cfg.getProperty("gov.faa.ait.tac.path", TAC_PATH);		
	}
	
	public static String getTACPdfPath() {
		return cfg.getProperty("gov.faa.ait.tac.pdf.path", TAC_PDF_PATH);		
	}
	
	public static String getGOMPath() {
		return cfg.getProperty("gov.faa.ait.gom.path", GOM_PATH);		
	}
	
	public static String getGOMCentralPDFFile() {
		return cfg.getProperty("gov.faa.ait.gom.central.pdf.file", GOM_CENTRAL_PDF_FILE);		
	}
	
	public static String getGOMWestPDFFile() {
		return cfg.getProperty("gov.faa.ait.gom.west.pdf.file", GOM_WEST_PDF_FILE);		
	}
	
	public static String getGOMCentralTIFFFile() {
		return cfg.getProperty("gov.faa.ait.gom.central.tiff.file", GOM_CENTRAL_TIFF_FILE);		
	}
	
	public static String getGOMWestTIFFFile() {
		return cfg.getProperty("gov.faa.ait.gom.west.tiff.file", GOM_WEST_TIFF_FILE);		
	}
	
	public static String getTPPUSPath() {
		return cfg.getProperty("gov.faa.ait.tpp.us.path", TPP_US_PATH);		
	}
	
	public static String getTPPChartPath() {
		return cfg.getProperty("gov.faa.ait.tpp.chart.path", TPP_CHART_PATH);		
	}

	public static String getTPPUSPrefix() {
		return cfg.getProperty("gov.faa.ait.tpp.us.prefix", TPP_US_PREFIX);		
	}
	
	public static String getSUPUSPath() {
		return cfg.getProperty("gov.faa.ait.sup.us.path", SUP_US_PATH);		
	}
	
	public static String getSUPChartPath() {
		return cfg.getProperty("gov.faa.ait.sup.chart.path", SUP_PDF_PATH);		
	}

	public static String getDDOFPath() {
		return cfg.getProperty("gov.faa.ait.ddof.path", DDOF_PATH);		
	}
	
	public static String getDDOFFile() {
		return cfg.getProperty("gov.faa.ait.ddof.file", DDOF_FILE);		
	}
	
	public static String getDDOFDailyChangeFile() {
		return cfg.getProperty("gov.faa.ait.ddof.change.file", DDOF_DAILY_CHANGE_FILE);		
	}	
	
	public static boolean getSUPCheckFlag() {
		String flag = cfg.getProperty("gov.faa.ait.sup.check.flag", SUP_CHECK_FLAG);
		
		if (flag == null) 
			return false;
		
		return Boolean.valueOf(flag);	
	}

	public static boolean getNASRCheckFlag() {
		String flag = cfg.getProperty("gov.faa.ait.nasr.check.flag", NASR_CHECK_FLAG);
		
		if (flag == null) 
			return false;
		
		return Boolean.valueOf(flag);	
	}
	
	public static int getEnrouteSetCount(String area, String highlow, String format) {
		int defaultCount=0;
		String propertySuffix = area.toLowerCase(Locale.ENGLISH) +"."+highlow.toLowerCase(Locale.ENGLISH)+"."+format.toLowerCase(Locale.ENGLISH);
		switch(propertySuffix) {
		case "us.area.pdf":
			defaultCount=1;
			break;
		case "us.area.tiff":
			defaultCount=2;
			break;
		case "us.low.tiff":
			defaultCount=36;
			break;
		case "us.low.pdf":
			defaultCount=36 ;
			break;
		case "us.high.tiff":
			defaultCount=12;
			break;
		case "us.high.pdf":
			defaultCount=12;
			break;
		case "alaska.low.tiff":
			defaultCount=4;
			break;
		case "alaska.low.pdf":
			defaultCount=4;
			break;
		case "alaska.high.tiff":
			defaultCount=2;
			break;
		case "alaska.high.pdf":
			defaultCount=2;
			break;
		case "pacific.low.tiff":
			defaultCount=0;
			break;
		case "pacific.low.pdf":
			defaultCount=0;
			break;
		case "pacific.high.tiff":
			defaultCount=2;
			break;
		case "pacific.high.pdf":
			defaultCount=2;
			break;
		case "caribbean.area.pdf":
			defaultCount=4;
			break;
		case "caribbean.area.tiff":
			defaultCount=0;
			break;
		case "caribbean.low.pdf":
			defaultCount=6;
			break;
		case "caribbean.high.pdf":
			defaultCount=2;
			break;
		case "caribbean.low.tiff":
			defaultCount=0;
			break;
		case "caribbean.high.tiff":
			defaultCount=0;
			break;
			
		default:
			break;
		}
		String result = cfg.getProperty("gov.faa.ait.enroute.count."+propertySuffix);
		return result != null ? Integer.parseInt(result.trim()) : defaultCount;
	}
	
}

