package biz.lcompras.model;



import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Produces("application/json")
@XmlRootElement(name = "invoices")
@XmlType(propOrder={"status", "tid", "messages","tkt","invoices"})
public class InvoiceResponse {
	@XmlElement(name = "status")
	private String status;
	@XmlElement(name = "tid")
	private String tid;	
	@XmlElement(name = "messages")
	private Message[] message;
	@XmlElement(name = "tkt")
	private String tkt;
	@XmlElement(name = "invoices")
	private Invoice[] invoices;
	

	
	
	
	
	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getTkt() {
		return tkt;
	}

	public void setTkt(String tkt) {
		this.tkt = tkt;
	}


	public Invoice[] getInvoices() {
		return invoices;
	}

	public void setInvoices(Invoice[] invoices) {
		this.invoices = invoices;
	}

	public Message[] getMessage() {
		return message;
	}

	public void setMessage(Message[] message) {
		this.message = message;
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
			json.put("status", this.status);
			json.put("tid", this.tid);
			json.put("messages", this.message);
			json.put("invoices", this.invoices);			
			return json.toString();
		} catch (JSONException e) {
			return e.toString();
		}
	}
}
