package biz.lcompras.model;

import org.json.JSONObject;

public class Payment {
	private String[] sub_id;
	private String tid;
	private String tkt;
	private int prd_id;
	private String[] inv_id;
	private double amt;
	private String curr;
	private String trn_dat;
	private String trn_hou;
	private double cm_amt;
	private String cm_curr;
	private JSONObject addl;
	private String type;
	public String getTid() {
		return tid;
	}
	
	public void setTid(String tid) {
		this.tid = tid.toUpperCase();
	}

	public int getPrd_id() {
		return prd_id;
	}
	
	public void setPrd_id(int prd_id) {
		this.prd_id = prd_id;
	}

	public String[] getSub_id() {
		return sub_id;
	}
	
	public void setSub_id(String[] sub_id) {
		this.sub_id = sub_id;
	}

	public String[] getInv_id() {
		return inv_id;
	}
	
	public void setInv_id(String[] inv_id) {
		this.inv_id = inv_id;
	}

	public double getAmt() {
		return amt;
	}
	
	public void setAmt(double amt) {
		this.amt = amt;
	}

	public String getCurr() {
		return curr;
	}
	
	public void setCurr(String curr) {
		this.curr = curr.toUpperCase();
	}

	public String getTrn_dat() {
		return trn_dat;
	}
	
	public void setTrn_dat(String trn_dat) {
		this.trn_dat = trn_dat.toUpperCase();
	}

	public String getTrn_hou() {
		return trn_hou;
	}
	
	public void setTrn_hou(String trn_hou) {
		this.trn_hou = trn_hou.toUpperCase();
	}

	public double getCm_amt() {
		return cm_amt;
	}
	
	public void setCm_amt(double cm_amt) {
		this.cm_amt = cm_amt;
	}

	public String getCm_curr() {
		return cm_curr.toUpperCase();
	}
	
	public void setCm_curr(String cm_curr) {
		this.cm_curr = cm_curr.toUpperCase();
	}

	public JSONObject getAddl() {
		return addl;
	}
	
	public void setAddl(JSONObject addl) {
		this.addl = addl;
	}

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTkt() {
		return tkt;
	}

	public void setTkt(String tkt) {
		this.tkt = tkt;
	}
}
