package model;

import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClienteAlive implements Runnable{
	
	private Servidor server;
	private InputStream cliente;
	private String userName;
	private Socket c;
	public ClienteAlive(InputStream cliente, Servidor servidor,String userName,Socket c){
		this.cliente = cliente;
		this.server = servidor;
		this.userName = userName;
		this.c = c;
	}

	@Override
	public void run() {
		
		while(true){
			
			System.out.println(c.isConnected());
			Scanner input = new Scanner(System.in);
			String pausa = input.next();
		}
		//this.server.getUserSet().get(userName).status = false;
		//System.out.println("Cliente"+userName+" saiu caaaaaaacete");
		
	}
	

}
