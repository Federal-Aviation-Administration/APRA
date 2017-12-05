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
package gov.faa.ait.apra.util;

import java.util.Locale;

/**
 * This class covers special cases of TAC files that don't conform to naming conventions such as Denver-Colorado Springs being shortened to Denver in the file name.
 * As naming conventions become more consistent, it is hoped that this class is deprecated and no longer required.
 * @author FAA
 *
 */
public class TACSpecialCase {

	private static final int PUERTO_RICO_VI = 0;
	private static final int DENVER_COLORADO_SPRINGS = 1;
	private static final int ANCHORAGE_FAIRBANKS = 3; 
	private static final String ZIP = ".zip";
	
	private TACSpecialCase () { } 
	
	public static String getTACFileName (int specialCase, String editionNumber, String format) {
		
		if ("pdf".equalsIgnoreCase(format)) {
			switch (specialCase) {
			case DENVER_COLORADO_SPRINGS:
				return "Denver_TAC_"+editionNumber+"_P.pdf";
				
			case ANCHORAGE_FAIRBANKS:
				return "Anchorage-Fairbanks_TAC_"+editionNumber+"_P.pdf";
			
			case PUERTO_RICO_VI:
				return "Puerto_Rico-VI_TAC_"+editionNumber+"_P.pdf";
			
			default:
				break;
			
			}
		}
		else if ("tiff".equalsIgnoreCase(format)) {
			switch (specialCase) {
			case DENVER_COLORADO_SPRINGS:
				return "Denver_TAC_"+editionNumber+ZIP;
						
			case ANCHORAGE_FAIRBANKS:
				return "Anchorage-Fairbanks_TAC_"+editionNumber+ZIP;
				
			case PUERTO_RICO_VI:
				return "Puerto_Rico-VI_TAC_"+editionNumber+ZIP;
				
			default:
				break;
				
			}			
		}
		
		return "";
	}

	public static int getSpecialCase (String geoname) {
		
		if (geoname.toLowerCase(Locale.ENGLISH).startsWith("puerto")) {
			return PUERTO_RICO_VI;
		}

		if (geoname.toLowerCase(Locale.ENGLISH).startsWith("denver")) {
			return DENVER_COLORADO_SPRINGS;
		}
			
		if (geoname.toLowerCase(Locale.ENGLISH).startsWith("anchorage")) {
			return ANCHORAGE_FAIRBANKS;
		}	
		
		return -1;
	}
}
