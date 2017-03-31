package model;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class Usuario implements Runnable{
	
	protected String userName;
	protected String passWord;
	protected boolean status;
	
	public Usuario(String userName, String passWord){
		this.userName = userName;
		this.passWord = passWord;
		this.status = false;
	}
	
	
	public abstract void run();
	
	public abstract void loadConection(InputStream in, OutputStream out);
	
	public String getUserName(){
		return this.userName;
	}
	
	public String getPassWord(){
		return this.passWord;
	}
	
	public boolean getStatus(){
		return this.status;
	}
	
	public void SetStatus(boolean status){
		this.status = status;
	}
	
	
	
	
	

}
