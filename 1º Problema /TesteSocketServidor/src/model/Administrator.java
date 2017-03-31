package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Administrator extends Usuario {

	private String name;
	private String position;
	private HashMap<String, Usuario> userSet;
	private Scanner isfromClient;
	private PrintStream toClient;
	private Diretorios dir;
	private FileInputStream fileIn = null;
	private OutputStream socketOut = null;
	private OutputStream out = null;
	private FileOutputStream fos = null;
	private InputStream is  = null;

	public Administrator(String name, String userName, String passWord,
			String position, HashMap<String, Usuario> userSet) {
		super(userName, passWord);
		this.name = name;
		this.position = position;
		this.userSet = userSet;
	}

	@Override
	public void run() {
		String op = "";
		dir = new Diretorios();
		try{
		do {
			op = isfromClient.nextLine();
			if (op.equals("1"))
				registerUser();
			else if (op.equals("2")) {
				listaAtual();
			} else if (op.equals("3")) {
				acessDir();
			} else if (op.equals("4")) {
				backDir();
			} else if (op.equals("5")) {
				sendFile();
			} else if (op.equals("6")) {
				receiveFile();
			} else if (op.equals("7")) {
				removeFile();

			} else if (op.equals("0")) {
				System.out.println("tchau");
			}

		} while (!op.equals("0"));
		this.status = false;
		}catch(NoSuchElementException e){
			System.out.println("cliente saiu");
			this.status = false;
		}

	}

	@Override
	public void loadConection(InputStream in, OutputStream out) {
		this.isfromClient = new Scanner(in);
		this.toClient = new PrintStream(out);
		this.socketOut = out;
		this.is = in;
	}

	private void registerUser() {
		String name, cargo, userName, passWord;

		name = isfromClient.nextLine();
		userName = isfromClient.nextLine();
		passWord = isfromClient.nextLine();
		cargo = isfromClient.nextLine();

		System.out
				.println(name + "," + userName + "," + passWord + "," + cargo);
		userSet.put(userName, new Cliente(name, userName, passWord, cargo));
	}

	private void listaAtual() {
		try {
			toClient.println(dir.listar());
		} catch (IOException e) {
			System.out.println("Erro");
		}

	}

	private void acessDir() {

		String dirName = isfromClient.nextLine();
		dir.navegar(dirName);
		try {
			
			if(dir.listar().equals("")){
				dir.back();
				toClient.println("/");
			}
			
			else toClient.println(dir.listar());
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void backDir() {
		try {
			dir.back();
			toClient.println(dir.listar());
		} catch (IOException e) {
			toClient.println("erro!");

		}
	}

	private void sendFile()  {
		String doc = isfromClient.nextLine();
		doc = dir.getPath(doc);
		File file = new File(doc);
		System.out.println(doc);
		if (file.isDirectory()){
			toClient.println("0");
			toClient.flush();
		}
		else {
			long fileSize = file.length();
			toClient.println("1");
			toClient.flush();
			toClient.println(fileSize);
			toClient.flush();
			int buffer = 4096;
			byte[] fileBuffer = new byte[buffer];
			int bytesRead = -1;
			long initialSize = 0;
			
				try {
					fileIn = new FileInputStream(file);
					System.out.println("enviando arquivo para "+this.name+" ...");
					while(initialSize < fileSize){
						bytesRead = fileIn.read(fileBuffer, 0, buffer);
						initialSize = initialSize + bytesRead;
						socketOut.write(fileBuffer, 0, bytesRead);
				}
					socketOut.flush();
					fileIn.close();
					System.out.println("arquivo enviado!");
						
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	private void removeFile() {
		
		String doc = isfromClient.nextLine();
		try {
			dir.remove(dir.getPath(doc));
			toClient.println(dir.listar()
					+ "/s/Arquivo Removido com sucesso :)");
		} catch (IOException e) {
			System.out.println();
		}
		
	}

	private void receiveFile() {
		long initialSize = 0;
		int bytesRead = -1;
		int buffer = 4096;
		byte[] bufferFile = new byte[buffer];
		
		String fileName = isfromClient.nextLine();
		long fileSize = isfromClient.nextLong();
		
		try {
			fos = new FileOutputStream(new File(dir.getPath(fileName).trim()));
			
			while(initialSize < fileSize){
				bytesRead = is.read(bufferFile, 0, buffer);
				initialSize = initialSize + bytesRead;
				fos.write(bufferFile, 0, bytesRead);
			}
			fos.flush();
			fos.close();
			System.out.println("arquivo recebido");
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}