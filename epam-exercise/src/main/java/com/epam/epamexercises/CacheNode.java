package com.epam.epamexercises;

import java.util.Date;

public class CacheNode {
	
	private MyObject key;
	private Date creation;
	
	public CacheNode(MyObject value, Date creation) {
		this.key = value;
		this.creation = creation;
	}
	
	public MyObject getKey() {
		return key;
	}
	public void setKey(MyObject value) {
		this.key = value;
	}
	public Date getCreation() {
		return creation;
	}
	public void setCreation(Date creation) {
		this.creation = creation;
	}

}
