package com.worizon;

public abstract class Doc {
	
	protected String wid;
	protected String uid;
	protected String catalog;
	protected String[] rcpts;
	protected Long ctime;
	protected Long etime;
	
	protected Doc( String uid, String catalog ){
		
		this.uid = uid;
		this.catalog = catalog;
	}

	public String getWid() {
		return wid;
	}

	public void setWid(String wid) {
		this.wid = wid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String[] getRcpts() {
		return rcpts;
	}

	public void setRcpts(String[] rcpts) {
		this.rcpts = rcpts;
	}

	public Long getCtime() {
		return ctime;
	}

	public void setCtime(Long ctime) {
		this.ctime = ctime;
	}

	public Long getEtime() {
		return etime;
	}

	public void setEtime(Long etime) {
		this.etime = etime;
	}
	

}
