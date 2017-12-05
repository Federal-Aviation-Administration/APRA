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
package gov.faa.ait.apra.api.management;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import gov.faa.ait.apra.bootstrap.Config;
import gov.faa.ait.apra.cycle.ChartCycleClient;
import gov.faa.ait.apra.cycle.TACCycleClient;
import gov.faa.ait.apra.util.URLCache;
import gov.faa.ait.apra.cycle.VFRChartCycleClient;
import gov.faa.ait.apra.cycle.WallPlanningChartCycleClient;

@Path("/management")
/**
 * This class provides management and control functions to assist with configuration reload, cache flush, start, stop, and health
 * @author FAA
 *
 */
public class ManagementControl {
	private static int mode = 1;

	@Path("/health")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
	/**
	 * Return the status of the application. This is the healthcheck URL for the application.
	 * @return the string ServerOK or ServerDown depending on the state of the application
	 */
	public String getStatus () {
		if (ManagementControl.mode == 0) 
			return "ServerDown";
		
		return "ServerOK";
	}
	
	@Path("/stop") 
    @GET
    @Produces(MediaType.TEXT_PLAIN)
	
	/**
	 * Stop the service. This will cause the service healthcheck to return "ServerDown" indicating to the load balancer that the service is offline
	 * @return the String ServerDown
	 */
	public static String stop() {
		ManagementControl.mode = 0;
		return "ServerDown";
	}
	
	@Path("/start") 
    @GET
    @Produces(MediaType.TEXT_PLAIN)
	/**
	 * Start or restart the service. This will cause the healthcheck to return "ServerOK" indicating to the load balancer that the service is online
	 * @return the String ServerOK
	 */
	public static String start() {
		ManagementControl.mode = 1;
		return "ServerOK";
	}
	
	@Path("/flush")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
	/**
	 * This method flushes the cache for the ChartCycleClient, TAC cycle, VFR cycle, and Wall Planning cycle
	 * @return the string "Cycle reload complete"
	 */
	public String refresh() {
		ChartCycleClient cycleClient = new ChartCycleClient();
		cycleClient.forceUpdate();
		TACCycleClient tacCycleClient = new TACCycleClient();
		tacCycleClient.forceUpdate();
		VFRChartCycleClient vfrClient = new VFRChartCycleClient();
		vfrClient.forceUpdate();
		WallPlanningChartCycleClient wpClient = new WallPlanningChartCycleClient();
		wpClient.forceUpdate();
		
		URLCache.getInstance().flush();
		
		return "Cycle Reload Complete";
	}

	@Path("/config")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
	/**
	 * This causes the application to reload its configuration from disk. This reloads the properties file without an app restart
	 * @return the string Config Reload Complete
	 */
	public String reloadConfig() {
		Config.loadConfig();		
		return "Config Reload Complete";
	}
}
