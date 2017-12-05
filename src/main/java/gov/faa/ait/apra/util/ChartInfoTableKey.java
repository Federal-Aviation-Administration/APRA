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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChartInfoTableKey {
	private static final Logger logger = LoggerFactory.getLogger(ChartInfoTableKey.class);
	private String cityRegion;
	private String periodCode;
	private String chartType;
	
	/**
	 * Default constructor nulls fields
	 */
	public ChartInfoTableKey() {
		cityRegion=null;
		periodCode=null;
		chartType=null;
	}
	
	/**
	 * Property value constructor
	 * @param cityRegion the geo area to use
	 * @param periodCode current or next
	 * @param chartType type of chart
	 */
	public ChartInfoTableKey(String cityRegion, String periodCode, String chartType) {
		
		if (logger.isDebugEnabled())
			logger.debug("Creating Key("+cityRegion+","+periodCode+","+chartType+")");
		
		this.cityRegion = cityRegion;
		this.periodCode = periodCode;
		this.chartType = chartType;
	}
	
	/**
	 * return the chartType
	 * @return
	 */
	public String getChartType() {
		return this.chartType;
	}
	/**
	 * @param chartType the chartType to set
	 */
	public void setChartType(String chartType) {
		this.chartType = chartType;
	}
	
	/**
	 * @return the cityRegion
	 */
	public String getCityRegion() {
		return cityRegion;
	}
	/**
	 * @param cityRegion the cityRegion to set
	 */
	public void setCityRegion(String cityRegion) {
		this.cityRegion = cityRegion;
	}
	/**
	 * @return the periodCode
	 */
	public String getPeriodCode() {
		return periodCode;
	}
	/**
	 * @param periodCode the periodCode to set
	 */
	public void setPeriodCode(String periodCode) {
		
		this.periodCode = periodCode;
	}
	
	/**
	 * override the toString method for debugging and logging
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Key-")
			.append(this.cityRegion).append("::").append(this.periodCode)
			.append("::").append(this.chartType);
		return sb.toString();
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chartType == null) ? 0 : chartType.hashCode());
		result = prime * result + ((cityRegion == null) ? 0 : cityRegion.hashCode());
		result = prime * result + ((periodCode == null) ? 0 : periodCode.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		
		/*
		if (!(obj instanceof ChartInfoTableKey)) {
			return false;
		}
		*/
		
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		
		ChartInfoTableKey other = (ChartInfoTableKey) obj;
		if (chartType == null) {
			if (other.chartType != null) {
				return false;
			}
		} else if (!chartType.equals(other.chartType)) {
			return false;
		}
		if (cityRegion == null) {
			if (other.cityRegion != null) {
				return false;
			}
		} else if (!cityRegion.equals(other.cityRegion)) {
			return false;
		}
		if (periodCode == null) {
			if (other.periodCode != null) {
				return false;
			}
		} else if (!periodCode.equals(other.periodCode)) {
			return false;
		}
		return true;
	}

}