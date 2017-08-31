package biz.lcompras.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import biz.lcompras.dao.PaymentDAO;
import biz.lcompras.model.Message;
import biz.lcompras.model.Payment;
import biz.lcompras.model.StatusMessage;
import biz.lcompras.util.Database;

public class PaymentDAOImpl implements PaymentDAO {
	private DataSource datasource = Database.getDataSource();
	private Logger logger = Logger.getLogger(PaymentDAOImpl.class);

	@Override
	public Response createPayment(Payment payment) {
		StatusMessage statusMessage = new StatusMessage();
		Message msg = new Message();
		Connection conn = null;
		PreparedStatement psInsertPayment = null;
		PreparedStatement psFindInvoiceId = null;
		PreparedStatement psTid = null;
		PreparedStatement psVistaBancard = null;
		ResultSet rs = null;
		ResultSet rsTid = null;
		
		String queryNroOpeVistaBancard = "select * from vista_apagar where ruc like ?";
		String queryInsertPayment = "insert into payment (tid, prd_id, sub_id, inv_id, amt, curr, trn_dat, trn_hou, cm_amt, cm_curr, addl, type)"
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?)";
		String queryCuotaPendiente = "Select * from vista_apagar where nrodeoperacion like ?";
		try {
			conn = datasource.getConnection();
			ResultSet rs_vista = null;
			psTid = conn.prepareStatement("Select * from payment where tid like ?");
			psTid.setString(1, payment.getTid());
			rsTid = psTid.executeQuery();
			int c2 = 0;
			while(rsTid.next()) {
				c2++;
			}
			if(c2 > 0) {
				logger.error("PaymentNotAuthorized... Tid duplicado");				
				msg.setKey("PaymentNotAuthorized");
				String[] dsc = { "El pago no se autoriza, tid duplicado" };
				msg.setDsc(dsc);
				msg.setLevel("error");
				statusMessage.setStatus("error");
				Message[] mensajeFeliz = new Message[1];
				mensajeFeliz[0] = msg;
				statusMessage.setMessage(mensajeFeliz);
				statusMessage.setTid(payment.getTid());
				return Response.status(403).entity(statusMessage).build();
			}	
			psVistaBancard = conn.prepareStatement(queryNroOpeVistaBancard);
			psVistaBancard.setString(1, payment.getSub_id()[0]);
			rs_vista = psVistaBancard.executeQuery();
			int cantidadCoutasPendientes = 0;
			while (rs_vista.next()) {
				cantidadCoutasPendientes++;
			}
			// si no existe el numero de operacion SUB_ID
			if (cantidadCoutasPendientes <= 0) {
				logger.error("SubscriberWithoutDebt...");				
				msg.setKey("SubscriberWithoutDebt");
				String[] dsc = { "El pago no fue aprobado. El abonado no tiene deuda pendiente" };
				msg.setDsc(dsc);
				msg.setLevel("info");
				statusMessage.setStatus("error");
				statusMessage.setTid(payment.getTid());
				Message[] mensajeFeliz = new Message[1];
				mensajeFeliz[0] = msg;
				statusMessage.setMessage(mensajeFeliz);		
				return Response.status(403).entity(statusMessage).build();

			}
			//si no existe el pago se inserta  
			String queryFindInvoiceId = "select inv_id from payment where inv_id like ?";
			psFindInvoiceId = conn.prepareStatement(queryFindInvoiceId);
			psFindInvoiceId.setString(1, payment.getInv_id()[0]);
			ResultSet rs2 = psFindInvoiceId.executeQuery();
			int cantidadCuotasPagadas = 0;
			while (rs2.next()) {
				++cantidadCuotasPagadas;
			}

			if (cantidadCuotasPagadas <= 0) {
				// 1 2 3 4 5 6 7 8 9 10 11 12
				// String sql = "insert into payment (tid, prd_id, sub_id, inv_id, amt, curr,
				// trn_dat, trn_hou, cm_amt, cm_curr, addl, type)"
				psInsertPayment = conn.prepareStatement(queryInsertPayment);
				psInsertPayment.setString(1, payment.getTid());
				psInsertPayment.setInt(2, payment.getPrd_id());
				psInsertPayment.setString(3, payment.getSub_id()[0].trim());
				psInsertPayment.setString(4, payment.getInv_id()[0].trim());
				psInsertPayment.setDouble(5, payment.getAmt());
				psInsertPayment.setString(6, payment.getCurr());
				psInsertPayment.setString(7, payment.getTrn_dat());
				psInsertPayment.setString(8, payment.getTrn_hou());
				psInsertPayment.setDouble(9, payment.getCm_amt());
				psInsertPayment.setString(10, payment.getCm_curr());
				psInsertPayment.setString(11, payment.getAddl().toString());
				psInsertPayment.setString(12, payment.getType());
				int rows = psInsertPayment.executeUpdate();
				if (rows == 0) {
					logger.error("HostTransactionError");
					msg.setKey("HostTransactionError");
					String[] dsc = { "Error al insertar el Pago" };
					msg.setDsc(dsc);
					msg.setLevel("error");
					statusMessage.setStatus("error");
					Message[] mensajeFeliz = new Message[1];
					mensajeFeliz[0] = msg;
					statusMessage.setMessage(mensajeFeliz);
					statusMessage.setTid(payment.getTid());
					return Response.status(403).entity(statusMessage).build();
				}
			} else {
				logger.error("PaymentNotAuthorized... pago duplicado");				
				msg.setKey("PaymentNotAuthorized");
				String[] dsc = { "El pago no se autoriza, pago duplicado" };
				msg.setDsc(dsc);
				msg.setLevel("error");
				statusMessage.setStatus("error");
				Message[] mensajeFeliz = new Message[1];
				mensajeFeliz[0] = msg;
				statusMessage.setMessage(mensajeFeliz);
				statusMessage.setTid(payment.getTid());
				return Response.status(403).entity(statusMessage).build();
			}

		} catch (SQLException e) {
			logger.error("Error database resultset: " + e.getMessage());
			e.printStackTrace();
			msg.setKey("HostTransactionError");
			String[] dsc = { "Error al insertar el Pago" };
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
			if (psInsertPayment != null) {
				try {
					psInsertPayment.close();
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
			if (psFindInvoiceId != null) {
				try {
					psFindInvoiceId.close();
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
			if (psVistaBancard != null) {
				try {
					psVistaBancard.close();
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
					String[] dsc = { "Error al insertar el Pago" };
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

		statusMessage.setStatus("success");
		statusMessage.setTid(payment.getTid());
		msg.setKey("PaymentProcessed");
		String[] dsc = { "El pago fue autorizado" };
		msg.setDsc(dsc);
		msg.setLevel("info");
		Message[] mensajeFeliz = new Message[1];
		mensajeFeliz[0] = msg;
		statusMessage.setMessage(mensajeFeliz);
		return Response.status(200).entity(statusMessage).build();
	}
}
