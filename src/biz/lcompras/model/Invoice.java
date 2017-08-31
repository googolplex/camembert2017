package biz.lcompras.model;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONException;
import org.json.JSONObject;

@Produces("application/json")
@XmlRootElement(name = "invoice")
public class Invoice {
	@XmlElement(name = "status")
	private String status;
	@XmlElement(name = "due")
	private String due;
	@XmlElement(name = "amt")
	private double amt;	
	@XmlElement(name = "min_amt")
	private double min_amt;
	@XmlElement(name = "sub_id")
	private String[] sub_id;
	@XmlElement(name = "inv_id")
	private String[] inv_id;
	@XmlElement(name = "addl")
	private String[] addl;
	@XmlElement(name = "next_dues")
	private NextDue[] nextdue;
	@XmlElement(name = "curr")
	private String curr;	
	@XmlElement(name = "cm_amt")
	private double cm_amt;
	@XmlElement(name = "cm_curr")
	private String cm_curr;
	@XmlElement(name = "dsc")
	private String dsc;
	
	
	
	public String[] getSub_id() {
		return sub_id;
	}



	public void setSub_id(String[] sub_id) {
		this.sub_id = sub_id;
	}



	public String getDue() {
		return due;
	}



	public void setDue(String due) {
		this.due = due;
	}



	public double getAmt() {
		return amt;
	}



	public void setAmt(double amt) {
		this.amt = amt;
	}



	public double getMin_amt() {
		return min_amt;
	}



	public void setMin_amt(double min_amt) {
		this.min_amt = min_amt;
	}



	public String[] getInv_id() {
		return inv_id;
	}



	public void setInv_id(String[] inv_id) {
		this.inv_id = inv_id;
	}



	public String[] getAddl() {
		return addl;
	}



	public void setAddl(String[] addl) {
		this.addl = addl;
	}



	public NextDue[] getNextdue() {
		return nextdue;
	}



	public void setNextdue(NextDue[] nextdue) {
		this.nextdue = nextdue;
	}



	public String getCurr() {
		return curr;
	}



	public void setCurr(String curr) {
		this.curr = curr;
	}



	public double getCm_amt() {
		return cm_amt;
	}



	public void setCm_amt(double cm_amt) {
		this.cm_amt = cm_amt;
	}



	public String getCm_curr() {
		return cm_curr;
	}



	public void setCm_curr(String cm_curr) {
		this.cm_curr = cm_curr;
	}



	public String getDsc() {
		return dsc;
	}



	public void setDsc(String dsc) {
		this.dsc = dsc;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	@Override public String toString() {
		try {
			JSONObject json = new JSONObject();			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate dateTime = LocalDate.parse(this.due, formatter);
			json.put("due", dateTime.format(formatter) );
			json.put("amt", this.amt);
			json.put("min_amt", this.min_amt);
			json.put("sub_id", this.sub_id);
			json.put("inv", this.inv_id);
			json.put("addl", this.addl);
			json.put("next_dues", this.nextdue);
			json.put("curr", this.curr);			
			json.put("dsc", this.dsc);
			return json.toString();

		} catch (JSONException e) {
			return e.toString();
		}
	}

}
