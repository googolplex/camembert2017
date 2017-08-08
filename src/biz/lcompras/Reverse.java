package biz.lcompras;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONException;
import org.json.JSONObject;
@XmlRootElement
public class Reverse {
	@XmlElement(name = "username")
    String username;
    public Reverse() {
    }
    public Reverse(String username) {
            this.username = username;
    }
    @Override
    public String toString() {
            try {
                    return new JSONObject().put("username", username).toString();
            } catch (JSONException e) {
                    return null;
            }
    }
}
