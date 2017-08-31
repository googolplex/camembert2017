package biz.lcompras.model;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


import org.json.JSONObject;

@Produces("application/json")
@XmlRootElement(name = "invoicerequest")
public class InvoiceRequest {
	@XmlElement(name = "tid")
	private String tid;
	@XmlElement(name = "prd_id")
	private int prd_id;
	@XmlElement(name = "sub_id")
	private String sub_id;
	@XmlElement(name = "addl")
	private JSONObject addl;
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public int getPrd_id() {
		return prd_id;
	}
	public void setPrd_id(int prd_id) {
		this.prd_id = prd_id;
	}
	public String getSub_id() {
		return sub_id;
	}
	public void setSub_id(String sub_id) {
		this.sub_id = sub_id;
	}
	public JSONObject getAddl() {
		return addl;
	}
	public void setAddl(JSONObject addl) {
		this.addl = addl;
	}
	
	
}
