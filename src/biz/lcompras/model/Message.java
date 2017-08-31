package biz.lcompras.model;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.json.JSONException;
import org.json.JSONObject;
@Produces("application/json")

@XmlType(propOrder={"level", "key", "desc"})
public class Message {
	@XmlElement(name = "level")
	private String level;
	@XmlElement(name = "key")
	private String key;
	@XmlElement(name = "dsc")
	private String[] dsc;
	
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String[] getDsc() {
		return dsc;
	}
	public void setDsc(String[] dsc) {
		this.dsc = dsc;
	}
	@Override
	public String toString() {
		try {
			JSONObject json = new JSONObject();
			json.put("level", this.level);
			json.put("key", this.key);
			json.put("dsc", this.dsc);			
			return json.toString();
		} catch (JSONException e) {
			return e.toString();
		}
	}
	
}
