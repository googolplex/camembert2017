package biz.lcompras.dao;

import javax.ws.rs.core.Response;

import biz.lcompras.model.Payment;

public interface PaymentDAO {	
	public Response createPayment(Payment payment);	
}
