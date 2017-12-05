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

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.jaxrs.config.BeanConfig;

/**
 * This is the base application for CIFP and represents the JAX-RS application for scanning of REST services through the classes that are specified.
 * @author FAA
 *
 */
public class DownloadServiceApp extends Application {
	private static final Logger logger = LoggerFactory.getLogger(DownloadServiceApp.class);

	public DownloadServiceApp () {
		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setTitle("FAA Aeronautic Product Release API");
		beanConfig.setVersion("1.1.0");
		beanConfig.setSchemes(new String [] {"https"});
		beanConfig.setHost("soa.smext.faa.gov");
		beanConfig.setBasePath("/apra");
		beanConfig.setResourcePackage("io.swagger.resources");
		beanConfig.setLicense("US Public Domain");
		beanConfig.setLicenseUrl("http://www.usa.gov/publicdomain/label/1.0/");
		beanConfig.setScan(true);
	}

	@Override
	public Set<Class<?>> getClasses() {
		if (logger.isDebugEnabled())
			logger.debug("Resource classes being retrieved from AirportStatus jersey application.");
		Set<Class<?>> s = new HashSet <>();
		s.add(gov.faa.ait.apra.api.CIFP.class);
		s.add(gov.faa.ait.apra.api.ProductApiListener.class);
		s.add(io.swagger.jaxrs.listing.ApiListingResource.class);
		s.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);
		
		//Manually adding MOXyJSONFeature
        s.add(org.glassfish.jersey.moxy.json.MoxyJsonFeature.class);
        
		return s;
	}
}
