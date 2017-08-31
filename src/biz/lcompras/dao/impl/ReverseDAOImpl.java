package biz.lcompras.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import biz.lcompras.dao.ReverseDAO;
import biz.lcompras.model.Message;
import biz.lcompras.model.Reverse;
import biz.lcompras.model.StatusMessage;
import biz.lcompras.util.Database;

public class ReverseDAOImpl implements ReverseDAO {
	private DataSource datasource = Database.getDataSource();
	private Logger logger = Logger.getLogger(ReverseDAOImpl.class);

	@Override
	public Response createReverse(Reverse reverse) {
		StatusMessage statusMessage = new StatusMessage();
		Message msg = new Message();
		// verificar si es nulo
		if (reverse == null) {
			logger.error("MissingParameters: no existe parametros");
			msg.setKey("MissingParameters");
			String[] dsc = { "Parametros requeridos no fueron recibidos" };
			msg.setDsc(dsc);
			msg.setLevel("info");
			statusMessage.setStatus("success");
			Message[] mensajeFeliz = new Message[1];
			mensajeFeliz[0] = msg;
			statusMessage.setMessage(mensajeFeliz);
			return Response.status(403).entity(statusMessage).build();
		}
		// verificar solo el tid
		if (reverse.getTid().equals("")) {
			logger.error("InvalidParameters: tid");
			msg.setKey("MissingParameters");
			String[] dsc = { "Parametros requeridos no fueron recibidos" };
			msg.setDsc(dsc);
			msg.setLevel("info");
			statusMessage.setStatus("success");
			Message[] mensajeFeliz = new Message[1];
			mensajeFeliz[0] = msg;
			statusMessage.setMessage(mensajeFeliz);
			return Response.status(403).entity(statusMessage).build();
		}
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement psFindInvoice = null;
		ResultSet rsFindInvoice = null;
		PreparedStatement psFindReverse = null;
		ResultSet rsFindReverse = null;
		PreparedStatement psPayment = null;
		ResultSet rsPayment = null;
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// dd/MM/yyyy
		Date now = new Date();
		String fechahora = sdfDate.format(now);
		String queryInsertReverse = "insert into reverse (tid,prd_id,sub_id,fechahora,inv_id,amt,curr,trn_dat,trn_hou,cm_amt,cmt_curr,addl,type)"
				+
				/*
				 * SUB_ID = RUC/CI INV_ID = INVOICE ID/Numero de Factura/Numero de Operacion
				 * tid,prd_id,sub_id,fechahora,inv_id,amt,curr,trn_dat,trn_hou,cm_amt,cmt_curr,
				 * addl,type 1 , 2, 3 , 4 , 5 , 6 , 7 , 8 , 9 , 10 , 11 , 12 , 13
				 * 
				 */
				"values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		// consulta buscar si existe el pago para reversarlo
		String queryFindInvoiceId = "Select * from payment where tid like ?";

		// Consulta buscar reverso duplicado
		String queryFindTransaccionId = "Select tid from reverse where tid like ?";
		try {
			conn = datasource.getConnection();
			int c = 0;
			/*
			 * Antes de reversar verificar que exista el pago!!
			 */
			
				psFindInvoice = conn.prepareStatement(queryFindInvoiceId);
				psFindInvoice.setString(1, reverse.getTid());
				rsFindInvoice = psFindInvoice.executeQuery();

				while (rsFindInvoice.next()) {
					c = c + 1;
				}

				if (c <= 0) {
					logger.error("No Existe el Pago: " + reverse.getInv_id()[0]);
					msg = new Message();
					msg.setKey("TransactionNotReversed");
					String[] dsc = { "Transacción con ese tid no existe" };
					msg.setDsc(dsc);
					msg.setLevel("error");
					statusMessage = new StatusMessage();
					statusMessage.setStatus("error");
					Message[] mensajeFeliz = new Message[1];
					mensajeFeliz[0] = msg;
					statusMessage.setMessage(mensajeFeliz);
					return Response.status(403).entity(statusMessage).build();
			
			}
			c = 0;
			psFindReverse = conn.prepareStatement(queryFindTransaccionId);
			psFindReverse.setString(1, reverse.getTid());
			rsFindReverse = psFindReverse.executeQuery();
			while (rsFindReverse.next()) {
				c = c + 1;
			}
			if (c > 0) {
				logger.error("Transaccion Reverse duplicado: " + reverse.getTid());
				msg.setKey("AlreadyReversed");
				String[] dsc = { "La transacción ya fue reversada." };
				msg.setDsc(dsc);
				msg.setLevel("error");
				statusMessage.setStatus("success");
				Message[] mensajeFeliz = new Message[1];
				mensajeFeliz[0] = msg;
				statusMessage.setMessage(mensajeFeliz);
				return Response.status(403).entity(statusMessage).build();
			}

			ps = conn.prepareStatement(queryInsertReverse);
			ps.setString(1, reverse.getTid());
			ps.setInt(2, reverse.getPrd_id());
			ps.setString(3, reverse.getSub_id());
			ps.setString(4, fechahora);
			ps.setString(5, reverse.getInv_id()[0]);
			ps.setDouble(6, reverse.getAmt());
			ps.setString(7, reverse.getCurr());
			ps.setString(8, reverse.getTrn_dat());
			ps.setString(9, reverse.getTrn_hou());
			ps.setDouble(10, reverse.getCm_amt());
			ps.setString(11, reverse.getCm_curr());
			ps.setString(12, reverse.getAddl().toString());
			ps.setString(13, reverse.getType());
			int rows = ps.executeUpdate();

			if (rows == 0) {
				logger.error("HostTransactionError: error al momento de hacer insert");
				msg.setKey("HostTransactionError");
				String[] dsc = { "Parametros requeridos no fueron recibidos" };
				msg.setDsc(dsc);
				msg.setLevel("error");
				statusMessage.setStatus("error");
				Message[] mensajeFeliz = new Message[1];
				mensajeFeliz[0] = msg;
				statusMessage.setMessage(mensajeFeliz);
				return Response.status(403).entity(statusMessage).build();

			}
			//actualizar pago luego de insertar el reverse
			String updatePaymentInvId = "update payment set inv_id = 'reversado' where tid like ?";
			psPayment = conn.prepareStatement(updatePaymentInvId);			
			psPayment.setString(1, reverse.getTid());
			rows = psPayment.executeUpdate();
			if (rows == 0) {
				logger.error("HostTransactionError: error al momento de hacer update payment");
				msg.setKey("HostTransactionError");
				String[] dsc = { "HostTransactionError: error al momento de hacer update payment" };
				msg.setDsc(dsc);
				msg.setLevel("error");
				statusMessage.setStatus("error");
				Message[] mensajeFeliz = new Message[1];
				mensajeFeliz[0] = msg;
				statusMessage.setMessage(mensajeFeliz);
				return Response.status(403).entity(statusMessage).build();

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error("Error closing resultset: " + e.getMessage());
					e.printStackTrace();
					msg.setKey("HostTransactionError");
					String[] dsc = { "Error en la base de datos" };
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
					String[] dsc = { "Error de conexion con la base de datos" };
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
		msg.setKey("TransactionReversed");
		String[] dsc = { "Transacción reversada satisfactoriamente" };
		msg.setDsc(dsc);
		msg.setLevel("info");
		statusMessage.setTid(reverse.getTid());
		statusMessage.setStatus("success");
		Message[] mensajeFeliz = new Message[1];
		mensajeFeliz[0] = msg;
		statusMessage.setMessage(mensajeFeliz);
		return Response.status(200).entity(statusMessage).build();

	}
}