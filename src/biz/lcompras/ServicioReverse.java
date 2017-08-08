package biz.lcompras;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.json.JSONException;
@Path("/reverse")
public class ServicioReverse {
	private static final long serialVersionUID = 1L;
    @GET
    @Produces("application/json")
    public Response getUsers() throws JSONException {
            List<Payment> users = new ArrayList<>();
            users.add(new Payment("admin"));
            users.add(new Payment("john"));
            users.add(new Payment("usuario2"));
            return Response.status(200).entity(users.toString()).build();
    }
}
