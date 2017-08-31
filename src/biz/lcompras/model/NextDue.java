package biz.lcompras.model;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


import org.json.JSONException;
import org.json.JSONObject;

@Produces("application/json")
@XmlRootElement(name = "nextdue")
public class NextDue {
	@XmlElement(name = "due")
	private String due;
	@XmlElement(name = "amt")
	private double amt;
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
	@Override public String toString() {
		try {
			JSONObject json = new JSONObject();			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate dateTime = LocalDate.parse(this.due, formatter);
			json.put("due", dateTime.format(formatter));
			json.put("amt", this.amt);
			return json.toString();

		} catch (JSONException e) {
			return e.toString();
		}
	}
}
