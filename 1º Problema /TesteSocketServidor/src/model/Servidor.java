package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Servidor {

	private int port;
	private HashMap<String, Usuario> userSet;
	private LinkedList<String> isDownload;
	private ServerSocket server;
	private Scanner fromClient;
	private PrintStream toClient;

	public Servidor(int port) {
		this.port = port;
		userSet = new HashMap<String, Usuario>();
		isDownload = new LinkedList<String>();
	}

	/*
	 * Executa os principais ações do servidor, como aceitar uma conexão e criar
	 * uma nova thread para o usuário conectado.
	 */
	public void execute() throws IOException {
		loadAdmin(); //Carrega os dados do administrador do arquivo.
		
		try{
		Socket cliente;
		server = new ServerSocket(this.port);
		System.out.println("Servidor iniciado!");
		while (true) {

			cliente = server.accept();
			System.out.println("Nova conexão com o cliente: "+ cliente.getInetAddress().getHostName());

			fromClient = new Scanner(cliente.getInputStream());//Scanner para a entrada de dados do Cliente conectado.
			toClient = new PrintStream(cliente.getOutputStream());//Print para a saida dos dados do cliente conectado.

			String userName = fromClient.nextLine();

			String[] login = acceptClient(userName, fromClient.nextLine()).split(" ");// O valor da posição 0 é o tipo de cliente o daposição 1 é o erro.
			
			int status = Integer.parseInt(login[1]);
			if (status < 0)
				toClient.println("notOk " + status);
			else {
				
				toClient.println("ok " + login[0]);

				Usuario user = userSet.get(userName);
				user.SetStatus(true);

				user.loadConection(cliente.getInputStream(),cliente.getOutputStream());
				Thread t1 = new Thread(user);
				t1.start();	

			}

		}
		}catch(BindException e){
			System.out.println("Servidor já está ativo!");
		}catch(NoSuchElementException e){
			System.out.println("Cliente desconectou-se na tela de Login");
		}
		
	}

	/*
	 * Verifica aceita ou não o login do usuário.
	 */
	public String acceptClient(String userName, String passWord) {
		Usuario user = userSet.get(userName);
		String acp = "";

		if (user != null) {
			if (user instanceof Administrator)// Verifica se o usuário é simples ou administrador. O "A" indica que é administrador
				acp = "A";
			else if (user instanceof Cliente)
				acp = "S"; // "S" de usuário simples
			
			if (user.getPassWord().equals(passWord)
					&& user.getStatus() == false)
				return acp + " " + 1;
			else if (!user.getPassWord().equals(passWord))
				return acp + " " + -2;
			else if (user.getPassWord().equals(passWord)
					&& user.getStatus() == true)
				return acp + " " + -3;

		}
		return "N" + " -1";
	}

	/*
	 * Carrega os dados do Admnistador do arquivo para o HashMap para garantir
	 * que o admistrador sempre seja criado ao rodar o servidor.
	 */
	public void loadAdmin() throws FileNotFoundException {
		int i = 0;
		String[] dataAdmin = new String[4];
		InputStream file = new FileInputStream("admin.txt");
		Scanner fileIn = new Scanner(file);
		while (fileIn.hasNextLine()) {
			dataAdmin[i] = fileIn.nextLine();
			i++;
		}
		userSet.put(dataAdmin[1], new Administrator(dataAdmin[0], dataAdmin[1],
				dataAdmin[2], dataAdmin[3], userSet));
		fileIn.close();
	}
	
	public HashMap<String,Usuario> getUserSet(){
		return userSet;
	}

}
