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
 * This class encapsulates the TPP metadata as served by the data virtualization server.
 * @author FAA
 *
 */
public class TPPMetadata {
	
	private String chart_cycle_period_code;
	private String chart_edition;
	private String query_date;
	private int cycle;
	private String from_edate;
	private String to_edate;
	private String id;
	private String state_fullname;
	private String volume;
	private String state_code_city_name_airport_name_id;
	private String military;
	private String airport_identifier;
	private String airport_icao_identifier;
	private String airport_name;
	private String city_name;
	private String alnum;
	private String chartseq;
	private String chart_code;
	private String chart_name;
	private String pdf_name;
	private String cn_flg;
	private String bvsection;
	private String two_colored;
	private String bvpage;
	private String procuid;
	private String civil;
	private String faanfd15;
	private String faanfd18;
	private String copter;
	private String useraction;
	private String cnpage;
	private String cnsection;
	private String filepath;
	
	public TPPMetadata () { }
	
	public String getChart_cycle_period_code() {
		return chart_cycle_period_code;
	}
	public void setChart_cycle_period_code(String chart_cycle_period_code) {
		this.chart_cycle_period_code = chart_cycle_period_code;
	}
	public String getChart_edition() {
		return chart_edition;
	}
	public void setChart_edition(String chart_edition) {
		this.chart_edition = chart_edition;
	}
	public String getQuery_date() {
		return query_date;
	}
	public void setQuery_date(String query_date) {
		this.query_date = query_date;
	}
	public int getCycle() {
		return cycle;
	}
	public void setCycle(int cycle) {
		this.cycle = cycle;
	}
	public String getFrom_edate() {
		return from_edate;
	}
	public void setFrom_edate(String from_edate) {
		this.from_edate = from_edate;
	}
	public String getTo_edate() {
		return to_edate;
	}
	public void setTo_edate(String to_edate) {
		this.to_edate = to_edate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getState_fullname() {
		return state_fullname;
	}
	public void setState_fullname(String state_fullname) {
		this.state_fullname = state_fullname;
	}
	public String getCity_name() {
		return city_name;
	}
	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}
	
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getState_code_city_name_airport_name_id() {
		return state_code_city_name_airport_name_id;
	}
	public void setState_code_city_name_airport_name_id(String state_code_city_name_airport_name_id) {
		this.state_code_city_name_airport_name_id = state_code_city_name_airport_name_id;
	}
	public String getMilitary() {
		return military;
	}
	public void setMilitary(String military) {
		this.military = military;
	}
	public String getAirport_name() {
		return airport_name;
	}
	public void setAirport_name(String airport_name) {
		this.airport_name = airport_name;
	}
	public String getAirport_identifier() {
		return airport_identifier;
	}
	public void setAirport_identifier(String airport_identifier) {
		this.airport_identifier = airport_identifier;
	}
	public String getAirport_icao_identifier() {
		return airport_icao_identifier;
	}
	public void setAirport_icao_identifier(String airport_icao_identifier) {
		this.airport_icao_identifier = airport_icao_identifier;
	}
	public String getAlnum() {
		return alnum;
	}
	public void setAlnum(String alnum) {
		this.alnum = alnum;
	}
	public String getChartseq() {
		return chartseq;
	}
	public void setChartseq(String chartseq) {
		this.chartseq = chartseq;
	}
	public String getChart_code() {
		return chart_code;
	}
	public void setChart_code(String chart_code) {
		this.chart_code = chart_code;
	}
	public String getChart_name() {
		return chart_name;
	}
	public void setChart_name(String chart_name) {
		this.chart_name = chart_name;
	}
	public String getPdf_name() {
		return pdf_name;
	}
	public void setPdf_name(String pdf_name) {
		this.pdf_name = pdf_name;
	}
	public String getCn_flg() {
		return cn_flg;
	}
	public void setCn_flg(String cn_flg) {
		this.cn_flg = cn_flg;
	}
	public String getBvsection() {
		return bvsection;
	}
	public void setBvsection(String bvsection) {
		this.bvsection = bvsection;
	}
	public String getTwo_colored() {
		return two_colored;
	}
	public void setTwo_colored(String two_colored) {
		this.two_colored = two_colored;
	}
	public String getBvpage() {
		return bvpage;
	}
	public void setBvpage(String bvpage) {
		this.bvpage = bvpage;
	}
	public String getProcuid() {
		return procuid;
	}
	public void setProcuid(String procuid) {
		this.procuid = procuid;
	}
	public String getCivil() {
		return civil;
	}
	public void setCivil(String civil) {
		this.civil = civil;
	}
	public String getFaanfd15() {
		return faanfd15;
	}
	public void setFaanfd15(String faanfd15) {
		this.faanfd15 = faanfd15;
	}
	public String getFaanfd18() {
		return faanfd18;
	}
	public void setFaanfd18(String faanfd18) {
		this.faanfd18 = faanfd18;
	}
	public String getCopter() {
		return copter;
	}
	public void setCopter(String copter) {
		this.copter = copter;
	}
	public String getUseraction() {
		return useraction;
	}
	public void setUseraction(String useraction) {
		this.useraction = useraction;
	}
	public String getCnpage() {
		return cnpage;
	}
	public void setCnpage(String cnpage) {
		this.cnpage = cnpage;
	}
	public String getCnsection() {
		return cnsection;
	}
	public void setCnsection(String cnsection) {
		this.cnsection = cnsection;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
}
