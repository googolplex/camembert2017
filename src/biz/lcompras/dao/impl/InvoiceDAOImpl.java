package biz.lcompras.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

import java.util.List;

import javax.sql.DataSource;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import biz.lcompras.dao.InvoiceDAO;
import biz.lcompras.model.Invoice;
import biz.lcompras.model.InvoiceRequest;
import biz.lcompras.model.InvoiceResponse;
import biz.lcompras.model.Message;
import biz.lcompras.model.StatusMessage;
import biz.lcompras.util.Database;

public class InvoiceDAOImpl implements InvoiceDAO {
	private DataSource datasource = Database.getDataSource();
	private Logger logger = Logger.getLogger(InvoiceDAOImpl.class);

	@Override
	public Response getInvoice(InvoiceRequest request) {
		logger.info(String.format("getInvoice, parametros: sub_id: " + request.getSub_id()));
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Message msg = new Message();
		InvoiceResponse invoiceresp = null;
		String sub_id = request.getSub_id();
		StatusMessage statusMessage = new StatusMessage();
		String sql = "SELECT nombre, ruc, nrodeoperacion,cuota,vence,importe, auc_planilla, version, id "
				+ " FROM vista_apagar where LPAD(trim(ruc),15,'0') like LPAD(trim(?),15,'0') ORDER BY VENCE ASC LIMIT 8;";
		String tid ="";
		try {
			conn = datasource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, sub_id);
			rs = ps.executeQuery();
			tid = request.getTid();
			invoiceresp = new InvoiceResponse();			
			invoiceresp.setTid(tid);			
			List<Invoice> invoices = new ArrayList<Invoice>();
			while (rs.next()) {
				Invoice i = new Invoice();
				i.setAmt(rs.getInt("importe"));
				i.setCurr("PYG");
				i.setCm_curr("PYG");
				String due = rs.getDate("vence").toString();
				i.setDue(due);
				i.setMin_amt(rs.getInt("importe"));
				String[] str = {rs.getString("nrodeoperacion")};				
				i.setInv_id(str);
				/*String str2[] = {rs.getString("ruc")};
				i.setSub_id(str2);*/
				i.setDsc("Nro de Operacion: " + rs.getString("nrodeoperacion") + " - Cliente: "+ rs.getString("nombre"));
				invoices.add(i);
			}
			Invoice[] array = new Invoice[invoices.size()];
			for (int c = 0; c < invoices.size(); c++) {
				array[c] = invoices.get(c);
			}
			if(array.length > 0) {
				invoiceresp.setInvoices(array);
				Message[] mensajefeliz = new Message[1];				
				msg.setKey("QueryProcessed");
				String[] dsc = {"Consulta procesada con éxito"};
				msg.setDsc(dsc);
				msg.setLevel("success");				
				mensajefeliz[0] = msg;
				invoiceresp.setMessage(mensajefeliz);
				invoiceresp.setStatus("success");
				return Response.status(200).entity(invoiceresp).build();
			}
			else{
				
				logger.info("Cliente no tiene facturas");
				statusMessage = new StatusMessage();				
				msg.setKey("SubscriberWithoutDebt");
				String[] dsc = {"El abonado con código " + sub_id + " no tiene cuotas pendientes"};
				msg.setDsc(dsc);
				msg.setLevel("info");
				statusMessage.setTid(tid);
				statusMessage.setStatus("success");
				Message[] mensajeFeliz = new Message[1];
				mensajeFeliz[0] = msg;
				statusMessage.setMessage(mensajeFeliz);				
				return Response.status(404).entity(statusMessage).build();
			}
		} catch (SQLException e) {
			logger.error("Error: " + e.getMessage());			
			e.printStackTrace();
			msg.setKey("HostTransactionError");
			String[] dsc = { "Error: La base de datos no se encuentra disponible" };
			msg.setDsc(dsc);
			msg.setLevel("error");
			statusMessage.setStatus("error");
			Message[] mensajeFeliz = new Message[1];
			mensajeFeliz[0] = msg;
			statusMessage.setMessage(mensajeFeliz);
			return Response.status(403).entity(statusMessage).build();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error("Error closing resultset: " + e.getMessage());
					e.printStackTrace();
					msg.setKey("HostTransactionError");
					String[] dsc = { "Error: La base de datos no se encuentra disponible" };
					msg.setDsc(dsc);
					msg.setLevel("error");
					statusMessage.setStatus("error");
					Message[] mensajeFeliz = new Message[1];
					mensajeFeliz[0] = msg;
					statusMessage.setMessage(mensajeFeliz);
					return Response.status(403).entity(statusMessage).build();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					logger.error("Error closing PreparedStatement: " + e.getMessage());					
					e.printStackTrace();
					msg.setKey("HostTransactionError");
					String[] dsc = { "Error: La base de datos no se encuentra disponible" };
					msg.setDsc(dsc);
					msg.setLevel("error");
					statusMessage.setStatus("error");
					Message[] mensajeFeliz = new Message[1];
					mensajeFeliz[0] = msg;
					statusMessage.setMessage(mensajeFeliz);
					return Response.status(403).entity(statusMessage).build();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error("Error closing connection: " + e.getMessage());
					e.printStackTrace();
					msg.setKey("HostTransactionError");
					String[] dsc = { "Error: La base de datos no se encuentra disponible" };
					msg.setDsc(dsc);
					msg.setLevel("error");
					statusMessage.setStatus("error");
					Message[] mensajeFeliz = new Message[1];
					mensajeFeliz[0] = msg;
					statusMessage.setMessage(mensajeFeliz);
					return Response.status(403).entity(statusMessage).build();
				}
			}
		}
	}
}
