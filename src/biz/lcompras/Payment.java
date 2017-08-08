package biz.lcompras;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONException;
import org.json.JSONObject;
@XmlRootElement
public class Payment {
	@XmlElement(name = "username")
    String username;
    public Payment() {
    }
    public Payment(String username) {
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
