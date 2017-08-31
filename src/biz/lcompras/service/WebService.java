package biz.lcompras.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.sun.istack.internal.NotNull;
import biz.lcompras.dao.InvoiceDAO;
import biz.lcompras.dao.PaymentDAO;
import biz.lcompras.dao.impl.InvoiceDAOImpl;
import biz.lcompras.dao.impl.PaymentDAOImpl;
import biz.lcompras.dao.impl.ReverseDAOImpl;
import biz.lcompras.model.InvoiceRequest;
import biz.lcompras.model.Message;
import biz.lcompras.model.Payment;
import biz.lcompras.model.Reverse;
import biz.lcompras.model.StatusMessage;
import biz.lcompras.util.Database;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

@Path("/")
public class WebService {
	private Logger logger = Logger.getLogger(WebService.class);
	private DataSource datasource = Database.getDataSource();
	@SuppressWarnings("unused")
	private ResultSet rs;

	@Path("status")
	@GET
	@Produces("application/json")
	public Response getStatus() {
		logger.info("Inside getStatus()...");
		Connection conn = null;
		PreparedStatement ps = null;
		rs = null;
		StatusMessage statusMessage = new StatusMessage();
		Message msg = new Message();
		try {
			conn = datasource.getConnection();
			ps = conn.prepareStatement("select count(*) from payment;");
			rs = ps.executeQuery();
			statusMessage = new StatusMessage();			
			msg.setKey("QueryProcessed");
			String[] dsc = {"Webservice Terere is online"};
			msg.setDsc(dsc);
			msg.setLevel("info");			
			statusMessage.setStatus("success");
			Message[] mensajeFeliz = new Message[1];
			mensajeFeliz[0] = msg;
			statusMessage.setMessage(mensajeFeliz);				
			return Response.status(202).entity(statusMessage).build();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("HostTransactionError, database error");
			statusMessage = new StatusMessage();
			String[] dsc = {"Error: La base de datos no se encuentra disponible"};
			msg.setDsc(dsc);
			msg.setLevel("error");			
			statusMessage.setStatus("error");
			Message[] mensajeFeliz = new Message[1];
			mensajeFeliz[0] = msg;
			statusMessage.setMessage(mensajeFeliz);		
			return Response.status(500).entity(statusMessage).build();

		}
	}

	@GET
	@Path("invoices")
	@Produces("application/json")
	public Response getInvoices(@Context UriInfo ui)  {
		logger.info("Inside getCustomer...");
		StatusMessage statusMessage = new StatusMessage();
		Message msg = new Message();		
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
		String sub_id = null;
		String s1 = queryParams.getFirst("sub_id[]");
		String s2 = queryParams.getFirst("sub_id");
		if(s1 == null) {
			sub_id = s2;
		}
		else {
			sub_id = s1;
		}
		
		String tid  = queryParams.getFirst("tid");
		String prd_id  = queryParams.getFirst("prd_id");
		if(sub_id == null || sub_id.equals("")) {
			logger.error("MissingParameters: sub_id");						
			msg.setKey("MissingParameters");
			String[] dsc = {"Parametros requeridos no fueron recibidos: sub_id"};
			msg.setDsc(dsc);
			msg.setLevel("info");			
			statusMessage.setStatus("success");
			Message[] mensajeFeliz = new Message[1];
			mensajeFeliz[0] = msg;
			statusMessage.setMessage(mensajeFeliz);
			return Response.status(422).entity(statusMessage).build();
		}
		if(tid == null || tid.equals("")) {
			logger.error("MissingParameters: tid");						
			msg.setKey("MissingParameters");
			String[] dsc = {"Parametros requeridos no fueron recibidos: tid"};
			msg.setDsc(dsc);
			msg.setLevel("info");			
			statusMessage.setStatus("success");
			Message[] mensajeFeliz = new Message[1];
			mensajeFeliz[0] = msg;
			statusMessage.setMessage(mensajeFeliz);
			return Response.status(422).entity(statusMessage).build();
		}
		if(prd_id == null || prd_id.equals("")) {
			logger.error("MissingParameters: prd_id");						
			msg.setKey("MissingParameters");
			String[] dsc = {"Parametros requeridos no fueron recibidos: prd_id"};
			msg.setDsc(dsc);
			msg.setLevel("info");			
			statusMessage.setStatus("success");
			Message[] mensajeFeliz = new Message[1];
			mensajeFeliz[0] = msg;
			statusMessage.setMessage(mensajeFeliz);
			return Response.status(422).entity(statusMessage).build();
		}
		InvoiceDAO invoice = new InvoiceDAOImpl();
		InvoiceRequest request = new InvoiceRequest();	
		request.setSub_id(sub_id);
		request.setTid(tid);
		request.setPrd_id(Integer.parseInt(prd_id));
		Response resp;
		resp = invoice.getInvoice(request);
		return resp;
	}

	@POST
	@Path("payment")
	@Consumes("application/json")
	@Produces("application/json")
	@NotNull
	public Response createPayment(String json) {
		Payment p;
		StatusMessage statusMessage = new StatusMessage();
		Message msg = new Message();		
		p = new Payment();
		JSONObject o;
		try {
			if (json == null) {
				
				logger.error("MissingParameters: no existe parametros");						
				msg.setKey("MissingParameters");
				String[] dsc = {"Parametros requeridos no fueron recibidos: parametros nulos"};
				msg.setDsc(dsc);
				msg.setLevel("info");			
				statusMessage.setStatus("success");
				Message[] mensajeFeliz = new Message[1];
				mensajeFeliz[0] = msg;
				statusMessage.setMessage(mensajeFeliz);
				return Response.status(422).entity(statusMessage).build();
			}
			o = new JSONObject(json);
			if(!o.has("tid")) {
				logger.error("MissingParameters: tid");						
				msg.setKey("MissingParameters");
				String[] dsc = {"Parametros requeridos no fueron recibidos: tid"};
				msg.setDsc(dsc);
				msg.setLevel("info");			
				statusMessage.setStatus("success");
				Message[] mensajeFeliz = new Message[1];
				mensajeFeliz[0] = msg;
				statusMessage.setMessage(mensajeFeliz);
				return Response.status(422).entity(statusMessage).build();
			}
			if(!o.has("amt")) {
				logger.error("MissingParameters: no existe parametros");						
				msg.setKey("MissingParameters");
				String[] dsc = {"Parametros requeridos no fueron recibidos: amt"};
				msg.setDsc(dsc);
				msg.setLevel("info");			
				statusMessage.setStatus("success");
				Message[] mensajeFeliz = new Message[1];
				mensajeFeliz[0] = msg;
				statusMessage.setMessage(mensajeFeliz);
				return Response.status(422).entity(statusMessage).build();
			}
			if(!o.has("prd_id")) {
				logger.error("MissingParameters: no existe parametros");						
				msg.setKey("MissingParameters");
				String[] dsc = {"Parametros requeridos no fueron recibidos: prd_id"};
				msg.setDsc(dsc);
				msg.setLevel("info");			
				statusMessage.setStatus("success");
				Message[] mensajeFeliz = new Message[1];
				mensajeFeliz[0] = msg;
				statusMessage.setMessage(mensajeFeliz);
				return Response.status(422).entity(statusMessage).build();
			}
			if(!o.has("curr")) {
				logger.error("MissingParameters: no existe parametros");						
				msg.setKey("MissingParameters");
				String[] dsc = {"Parametros requeridos no fueron recibidos: curr"};
				msg.setDsc(dsc);
				msg.setLevel("info");			
				statusMessage.setStatus("success");
				Message[] mensajeFeliz = new Message[1];
				mensajeFeliz[0] = msg;
				statusMessage.setMessage(mensajeFeliz);
				return Response.status(422).entity(statusMessage).build();
			}
			if(!o.has("inv_id")) {
				logger.error("MissingParameters: inv_id");						
				msg.setKey("MissingParameters");
				String[] dsc = {"Parametros requeridos no fueron recibidos: inv_id"};
				msg.setDsc(dsc);
				msg.setLevel("info");			
				statusMessage.setStatus("success");
				Message[] mensajeFeliz = new Message[1];
				mensajeFeliz[0] = msg;
				statusMessage.setMessage(mensajeFeliz);
				return Response.status(422).entity(statusMessage).build();
			}
			if(!o.has("trn_dat")) {
				logger.error("MissingParameters: no existe parametros");						
				msg.setKey("MissingParameters");
				String[] dsc = {"Parametros requeridos no fueron recibidos: trn_dat"};
				msg.setDsc(dsc);
				msg.setLevel("info");			
				statusMessage.setStatus("success");
				Message[] mensajeFeliz = new Message[1];
				mensajeFeliz[0] = msg;
				statusMessage.setMessage(mensajeFeliz);
				return Response.status(422).entity(statusMessage).build();
			}
			if(!o.has("trn_hou")) {
				logger.error("MissingParameters: no existe parametros");						
				msg.setKey("MissingParameters");
				String[] dsc = {"Parametros requeridos no fueron recibidos: trn_hou"};
				msg.setDsc(dsc);
				msg.setLevel("info");			
				statusMessage.setStatus("success");
				Message[] mensajeFeliz = new Message[1];
				mensajeFeliz[0] = msg;
				statusMessage.setMessage(mensajeFeliz);
				return Response.status(422).entity(statusMessage).build();
			}
			if(!o.has("cm_amt")) {
				logger.error("MissingParameters: no existe parametros");						
				msg.setKey("MissingParameters");
				String[] dsc = {"Parametros requeridos no fueron recibidos: cm_amt"};
				msg.setDsc(dsc);
				msg.setLevel("info");			
				statusMessage.setStatus("success");
				Message[] mensajeFeliz = new Message[1];
				mensajeFeliz[0] = msg;
				statusMessage.setMessage(mensajeFeliz);
				return Response.status(422).entity(statusMessage).build();
			}
			if(!o.has("cm_curr")) {
				logger.error("MissingParameters: no existe parametros");						
				msg.setKey("MissingParameters");
				String[] dsc = {"Parametros requeridos no fueron recibidos: cm_curr"};
				msg.setDsc(dsc);
				msg.setLevel("info");			
				statusMessage.setStatus("success");
				Message[] mensajeFeliz = new Message[1];
				mensajeFeliz[0] = msg;
				statusMessage.setMessage(mensajeFeliz);
				return Response.status(422).entity(statusMessage).build();
			}
			p.setTid(Integer.toString(o.getInt("tid")));
			p.setAmt(o.getDouble("amt"));
			p.setPrd_id(o.getInt("prd_id"));
			JSONArray ja = o.getJSONArray("inv_id");
			int length = ja.length();
			String[] r = new String[length];
			double cm_amt = 0;
			String cm_curr = "";
			JSONObject addl = new JSONObject();
			if (length > 0) {

				for (int i = 0; i < length; i++) {
					r[i] = ja.getString(i);
				}
			}
			p.setInv_id(r);			
			p.setCurr(o.getString("curr"));
			p.setTrn_dat(o.getString("trn_dat"));
			p.setTrn_hou(Integer.toString(o.getInt("trn_hou")));
			if(!o.has("type")) {
				p.setType(o.getString("type"));
			}
			else {
				p.setType("none");
			}
			if (o.has("addl")) {
				addl = o.getJSONObject("addl");
				if (addl.has("cm_amt") && addl.has("cm_curr")) {
					cm_amt = addl.getDouble("cm_amt");
					cm_curr = addl.getString("cm_curr");

				}
			} else {
				addl = new JSONObject();
				cm_amt = 0;
				cm_curr = "PYG";
				addl.put("cm_amt", cm_amt);
				addl.put(cm_curr, cm_curr);

			}
			p.setAddl(addl);
			p.setCm_amt(cm_amt);
			p.setCm_curr(cm_curr);
			ja = o.getJSONArray("sub_id");
			length = ja.length();
			r = new String[length];
			if (length > 0) {

				for (int i = 0; i < length; i++) {
					r[i] = ja.getString(i);
				}
			}
			p.setSub_id(r);
		} catch (JSONException e) {
			e.printStackTrace();
			logger.error("Error JSON");						
			msg.setKey("Error JSON");
			String[] dsc = {"Error en json"};
			msg.setDsc(dsc);
			msg.setLevel("error");			
			statusMessage.setStatus("error");
			Message[] mensajeFeliz = new Message[1];
			mensajeFeliz[0] = msg;
			statusMessage.setMessage(mensajeFeliz);
			return Response.status(403).entity(statusMessage).build();
		}
		
		PaymentDAO daoImpl = new PaymentDAOImpl();
		logger.info("Inside createPayment...");
		Response resp = daoImpl.createPayment(p);
		return resp;

	}

	@POST
	@NotNull
	@Path("reverse")
	@Consumes("application/json")
	@Produces("application/json")
	public Response createReverse(String json) {
		Reverse reverse;
		Response resp = null;
		String tid;		
		double cm_amt = 0;
		String cm_curr = "";
		JSONObject addl = new JSONObject();
		StatusMessage statusMessage = new StatusMessage();;
		Message msg = new Message();
		if (json == null) {			
			logger.error("MissingParameters: no existe parametros");
			msg.setKey("MissingParameters");
			String[] dsc = {"Parametros requeridos no fueron recibidos"};
			msg.setDsc(dsc);
			msg.setLevel("info");			
			statusMessage.setStatus("success");
			Message[] mensajeFeliz = new Message[1];
			mensajeFeliz[0] = msg;
			statusMessage.setMessage(mensajeFeliz);				
			return Response.status(403).entity(statusMessage).build();
		}
		reverse = new Reverse();

		try {
			JSONObject o = new JSONObject(json);
			if (o.has("tid")) {
				tid = Integer.toString(o.getInt("tid"));
			} else {				
				logger.error("MissingParameters: no existe parametros tid");
				msg.setKey("MissingParameters");
				String[] dsc = {"Parametros requeridos no fueron recibidos"};
				msg.setDsc(dsc);
				msg.setLevel("info");			
				statusMessage.setStatus("success");
				Message[] mensajeFeliz = new Message[1];
				mensajeFeliz[0] = msg;
				statusMessage.setMessage(mensajeFeliz);
				return Response.status(403).entity(statusMessage).build();
			}
			if (o.has("prd_id")) {
				reverse.setPrd_id(o.getInt("prd_id"));
			} else {
				reverse.setPrd_id(-1);
			}
			if (o.has("sub_id")) {
				JSONArray ja = o.getJSONArray("sub_id");
				int length = ja.length();
				String [] r = new String[length];
				if (length > 0) {

					for (int i = 0; i < length; i++) {
						r[i] = ja.getString(i);
					}
				}

				reverse.setSub_id(r[0]);
			} else {
				reverse.setSub_id("-1");
			}			
			if (o.has("addl")) {
				addl = o.getJSONObject("addl");
				if (addl.has("cm_amt") && addl.has("cm_curr")) {
					cm_amt = addl.getDouble("cm_amt");
					cm_curr = addl.getString("cm_curr");
				}
			} else {
				addl = new JSONObject();
				cm_amt = 0;
				cm_curr = "PYG";
				addl.put("cm_amt", cm_amt);
				addl.put(cm_curr, cm_curr);
			}

			if(o.has("inv_id")) {
				JSONArray ja = o.getJSONArray("inv_id");
				int length = ja.length();
				String[] r = new String[length];				
				if (length > 0) {

					for (int i = 0; i < length; i++) {
						r[i] = ja.getString(i);
					}
				}
				reverse.setInv_id(r);			
			}
			else {
				String [] inv_id = {"-1"};
				reverse.setInv_id(inv_id);
			}
			if(o.has("amt")) {
				reverse.setAmt(o.getInt("amt"));
			}	
			else {
				reverse.setAmt(0);
			}
			if(o.has("cm_amt")) {
				reverse.setCm_amt(o.getInt("cm_amt"));
			}
			else {
				reverse.setCm_amt(0);
			}
			if(o.has("trn_dat")) {
				reverse.setTrn_dat(o.getString("trn_dat"));
			}
			else {
				reverse.setTrn_dat("none");
			}
			if(o.has("trn_hou")) {
				reverse.setTrn_hou(Integer.toString(o.getInt("trn_hou")));
			}
			else {
				reverse.setTrn_hou("none");
			}
			if(o.has("cm_curr")) {
				reverse.setCm_curr(o.getString("cm_curr"));
			}
			else {
				reverse.setCm_curr("PYG");
			}
			if(o.has("type")) {
				reverse.setType(o.getString("type"));
			}
			else {
				reverse.setCm_curr("none");
			}
			if(o.has("addl"))
			{
				reverse.setAddl(o.getJSONObject("addl"));
			}
			else
			{
				reverse.setAddl(o);
			}
			reverse.setTid(tid);
			reverse.setStatus("sucess");
			ReverseDAOImpl daoImpl = new ReverseDAOImpl();
			logger.info("Inside createReverse...");
			resp = daoImpl.createReverse(reverse);
		} catch (JSONException e) {
			e.printStackTrace();			
			logger.error("MissingParameters: no existe parametros" + json);
			msg.setKey("MissingParameters");
			String[] dsc = {"Parametros requeridos no fueron recibidos"};
			msg.setDsc(dsc);
			msg.setLevel("error");			
			statusMessage.setStatus("success");
			Message[] mensajeFeliz = new Message[1];
			mensajeFeliz[0] = msg;
			statusMessage.setMessage(mensajeFeliz);
			return Response.status(403).entity(statusMessage).build();
		} 
		return resp;
	}



	


}
