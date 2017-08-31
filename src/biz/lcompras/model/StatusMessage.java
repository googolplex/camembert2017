package biz.lcompras.model;


import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Produces("application/json")
@XmlType(propOrder={"status", "tid", "messages","tkt","aut_cod"})
public class StatusMessage {
	@XmlElement(name = "status")
	private String status;	
	@XmlElement(name = "tid")
	private String tid;	
	@XmlElement(name = "tkt")
	private String tkt;		
	@XmlElement(name = "messages")
	private Message[] message;
	@XmlElement(name = "aut_cod")
	private String aut_cod;

	public StatusMessage() {
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Message[] getMessage() {
		return message;
	}

	public void setMessage(Message[] message) {
		this.message = message;
	}

	
	public String getTkt() {
		return tkt;
	}

	public void setTkt(String tkt) {
		this.tkt = tkt;
	}

	public String getAut_cod() {
		return aut_cod;
	}

	public void setAut_cod(String aut_cod) {
		this.aut_cod = aut_cod;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

}