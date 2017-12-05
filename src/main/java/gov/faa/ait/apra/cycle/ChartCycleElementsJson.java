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
package gov.faa.ait.apra.cycle;

import java.util.Date;

public class ChartCycleElementsJson {
	private String chart_cycle_period_code;
	private String chart_cycle_type_code;
	private Date chart_effective_date;
	private String chart_cycle_number;
	private String query_date;
	private String chart_city_name;
	
	public ChartCycleElementsJson () {
		/*
		chart_cycle_period_code = "";
		chart_cycle_type_code = "";
		chart_effective_date = new Date (System.currentTimeMillis());
		chart_cycle_number = "";
		query_date = "";
		chart_city_name = "";
		*/
	}
	
	public String getChart_cycle_period_code() {
		return chart_cycle_period_code;
	}
	public void setChart_cycle_period_code(String chart_cycle_period_code) {
		this.chart_cycle_period_code = chart_cycle_period_code;
	}
	public String getChart_cycle_type_code() {
		return chart_cycle_type_code;
	}
	public void setChart_cycle_type_code(String chart_cycle_type_code) {
		this.chart_cycle_type_code = chart_cycle_type_code;
	}
	public Date getChart_effective_date() {
		if (chart_effective_date == null)
			return new Date(System.currentTimeMillis());
		
		return new Date (chart_effective_date.getTime());
	}
	public void setChart_effective_date(Date chart_effective_date) {
		if (chart_effective_date == null) 
			return;
		
		this.chart_effective_date = new Date(chart_effective_date.getTime());
	}
	public String getChart_cycle_number() {
		return chart_cycle_number;
	}
	public void setChart_cycle_number(String chart_cycle_number) {
		this.chart_cycle_number = chart_cycle_number;
	}
	public String getQuery_date() {
		return query_date;
	}
	public void setQuery_date(String query_date) {
		this.query_date = query_date;
	}
	public String getChart_city_name() {
		return chart_city_name;
	}
	public void setChart_city_name(String chart_city_name) {
		this.chart_city_name = chart_city_name;
	}
}
