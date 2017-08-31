package biz.lcompras.dao;

import javax.ws.rs.core.Response;

import biz.lcompras.model.InvoiceRequest;

public interface InvoiceDAO {
	public Response getInvoice(InvoiceRequest request);
}
