package biz.lcompras.dao;

import javax.ws.rs.core.Response;
import biz.lcompras.model.Reverse;

public interface ReverseDAO {
	
	//public Response getReverse(String sub_id);
	public Response createReverse(Reverse reverse);	
	//public Response getAllReverses();
	
}
