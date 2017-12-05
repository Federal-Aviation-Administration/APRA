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
package gov.faa.ait.apra.test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.bootstrap.Config;

public class VerifyValues {
	private final static Logger logger = LoggerFactory.getLogger(VerifyValues.class);
	private final static String EMPTY_STRING="";

	private VerifyValues () { }
	
	public static boolean verifyURL (String urlString) {
		boolean ok = false; 
		HttpURLConnection connection = null;
		Proxy proxy = null;
		
		try {
			URL url = new URL(urlString);
			if (Config.getFAADMZProxyHost() != null && (! EMPTY_STRING.equals(Config.getFAADMZProxyHost())) ) {
				int port = Integer.parseInt(Config.getFAADMZProxyPort());
				InetSocketAddress proxyAddress = new InetSocketAddress(Config.getFAADMZProxyHost(), port);
				proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);			
			}
			
			if (proxy != null) {
				connection = (HttpURLConnection) url.openConnection(proxy);
			}
			else {
				connection = (HttpURLConnection) url.openConnection();
			}
			
			connection.setRequestMethod("HEAD");
			int responseCode = connection.getResponseCode();
			if (responseCode == 200 || responseCode == 302) {
			    ok = true;
			}
			else {
				logger.warn("URL HEAD check returned response code "+responseCode+" for url "+url.toExternalForm());
			}
		}
		catch (IOException eio) {
			logger.error("HEAD heck failed!", eio);
			logger.error(eio.getMessage());
			ok = false;
		}
		
		if (connection != null)
			connection.disconnect();
	
		return ok;
	}
}
