package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Cliente extends Usuario {

	private String name;
	private String cargo;
	private InputStream in;
	private OutputStream out;
	private Scanner isfromClient;
	private PrintStream toClient;
	private OutputStream socketOut;
	private Diretorios dir;
	private FileInputStream fileIn = null;

	public Cliente(String nome, String userName, String passWord, String cargo) {
		super(userName, passWord);
		this.name = nome;
		this.cargo = cargo;

	}

	@Override
	public void run() {
		String op = "";
		dir = new Diretorios();

		listaAtual();
		try {
			do {
				op = isfromClient.nextLine();
				if (op.equals("1")) {
					acessDir();
				} else if (op.equals("2")) {
					backDir();
				} else if (op.equals("3")) {
					sendFile();
				} else if (op.equals("0")) {
					System.out.println("tchau");
				}

			} while (!op.equals("0"));
		} catch (NoSuchElementException e) {
			System.out.println("Cliente " + this.userName + " saiu");
		}

	}

	@Override
	public void loadConection(InputStream in, OutputStream out) {
		this.out = out;
		this.in = in;
		isfromClient = new Scanner(this.in);
		toClient = new PrintStream(this.out);
		// socketOut = out;

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
			if (dir.listar().equals("")) {
				dir.back();
				toClient.println("/");
			} else
				toClient.println(dir.listar());

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

	public void sendFile() {
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

	public String getName() {
		return this.name;
	}

	public String getCargo() {
		return this.cargo;
	}

}
