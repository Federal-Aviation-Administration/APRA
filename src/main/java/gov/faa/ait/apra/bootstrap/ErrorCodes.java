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

/**
 * This class is used to manage the error codes and error responses that can be returned from the application
 * @author FAA
 *
 */
public class ErrorCodes {

	// Hide the default public constructor. This is a utility class that needs no constructor
	private ErrorCodes () { }
	
	public static final String RESPONSE_200="OK";
	public static final String ERROR_400="Illegal arguments provided to service. One or more of the service parameter values is invalid";
	public static final String ERROR_404="Requested edition has not been released for download or could not be found on the FAA aeronav web site.";
	public static final String ERROR_500="Internal service error occurred which prevented a valid response from being returned.";
	public static final String DEPRECATED="This product has been deprecated and is no longer published by the FAA.";
}
