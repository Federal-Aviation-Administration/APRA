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
package gov.faa.ait.apra.json;

/**
 * Supplement Metadata pojo object.
 *
 */

public class SupplementMetadata {

	protected String state;
	protected String aptname;
	protected String aptcity;
	protected String aptid;
	protected String pdf;
	protected String navidname;
	protected String chartDate;
	protected String volumeAbbreviation;
	protected String volumeName;

	public SupplementMetadata () {
		state = "";
		aptname = "";
		aptcity = "";
		aptid = "";
		pdf = "";
		navidname = "";
		chartDate = "";
		volumeAbbreviation = "";
		volumeName = "";
	}
	
	public String getState() {
		return state;
	}

	public void setState(String value) {
		this.state = value;
	}

	public String getAptname() {
		return aptname;
	}

	public void setAptname(String value) {
		this.aptname = value;
	}

	public String getAptcity() {
		return aptcity;
	}

	public void setAptcity(String value) {
		this.aptcity = value;
	}

	public String getAptid() {
		return aptid;
	}

	public void setAptid(String value) {
		this.aptid = value;
	}

	public String getPdf() {
		return pdf;
	}

	public void setPdf(String value) {
		this.pdf = value;
	}

	public String getNavidname() {
		return navidname;
	}

	public void setNavidname(String value) {
		this.navidname = value;
	}

	public String getChartDate() {
		return chartDate;
	}

	public void setChartDate(String value) {
		this.chartDate = value;
	}

	public String getVolumeAbbreviation() {
		return volumeAbbreviation;
	}

	public void setVolumeAbbreviation(String value) {
		this.volumeAbbreviation = value;
	}

	public String getVolumeName() {
		return volumeName;
	}

	public void setVolumeName(String value) {
		this.volumeName = value;
	}

}
