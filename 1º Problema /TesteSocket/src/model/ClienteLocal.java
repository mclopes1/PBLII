package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClienteLocal {

	private String ip;
	private int port;
	private Socket client;
	private OutputStream socketOut = null;
	private FileInputStream fileIn = null;
	private PrintStream saida;
	private Scanner entrada;
	private Scanner teclado = new Scanner(System.in);
	private InputStream is = null;
	private FileOutputStream fos = null;
	private String path = "";

	public ClienteLocal(String ip, int port) {
		this.ip = ip;
		this.port = port;

	}

	/*
	 * inicia as conexões do cliente com o servidor
	 */
	public void conectClient() throws UnknownHostException, IOException {
		this.client = new Socket(this.ip, this.port);
		System.out.println("O cliente se conectou ao servidor. :)");
		saida = new PrintStream(client.getOutputStream());
		entrada = new Scanner(client.getInputStream());
		is = client.getInputStream();
		socketOut = client.getOutputStream();
		creatDownloadDuto(); //chama o método que cria a pasta downloadDuto 
	}

	/*
	 * Responsável por pegar o login e senha do usuário e enviar ao servidor a
	 * fim de conecta-lo ao mesmo
	 */
	public String logIn() throws IOException {

		System.out.println("Login: ");
		String userName = teclado.nextLine();

		System.out.println("Senha: ");
		String passWord = teclado.nextLine();

		saida.println(userName);
		saida.println(passWord);
		saida.flush();
		String[] value = entrada.nextLine().split(" ");

		if (value[0].equals("ok"))
			return value[1];

		return value[1];
	}

	/*
	 * Exibe o menu do cliente simples. Neste método o usuário escolhe as opções
	 * e a mesma é enviado ao servidor que retorna com o serviço
	 */
	public void menuClient() throws IOException, ClassNotFoundException {
		String opcao = "";

		printData(entrada.nextLine().split("/s/"));

		do {

			System.out.println("1-Navegar\n2-voltar\n3-Fazer download\n0-sair");
			opcao = teclado.nextLine();
			saida.println(opcao);

			// condição responsável por enviar o nome do diretório para o
			// servidor. O servidor retorna as listagens do diretório.
			if (opcao.equals("1")) {

				System.out.println("navegar: ");
				String navegar = teclado.nextLine();
				saida.println(navegar);
				saida.flush();
				String saida = entrada.nextLine();

				if (saida.equals("/"))//O Servidor retorna um barra caso o usuário tente entrar em um diretório inexistente ou errado
					System.out.println("Diretório inválido ou inexistente");
				else
					printData(saida.split("/s/"));
			//Condição em que o usuário retorna ao diretório anterior
			} else if (opcao.equals("2")) {

				printData(entrada.nextLine().split("/s/"));
			//Condição 3: O usuário entra com o documento que deseja baixar
			} else if (opcao.equals("3")) {

				System.out.println("Entre com o nome do documento: ");
				String docName = teclado.nextLine();
				saida.println(docName);
				if (entrada.nextLine().equals("0"))
					System.out.println("é um diretório");
				else
					receiveFile(docName);

			} else if (opcao.equals("0"))
				System.out.println("fim");

		} while (!opcao.equals("0"));

	}
	
	/*
	 * Exibe o menu do usuário adminitrador. O administrador possui mais opções
	 *como por exemplo: Cadastro de Usuário, remoção e upload de arquivos.
	 */
	public void menuAdmin() throws ClassNotFoundException, IOException {
		String opcao = "";

		System.out
				.println("1-Cadastrar Usuário\n2-Entrar no modo navegação de pasta\n0-sair");
		do {
			System.out.println("opcao: ");
			opcao = teclado.nextLine().trim();

			saida.println(opcao);
			saida.flush();
			if (opcao.equals("1")) {
				registerClient();
			} else if (opcao.equals("2")) {
				printData(entrada.nextLine().split("/s/"));// imprime string de
															// diretórios.
				do {

					System.out
							.println("\n3-Navegar\n4-Voltar\n5-Download\n6-Adicionar arquivo\n7-Remover arquivo\n8-sair");
					opcao = teclado.nextLine();

					if (opcao.equals("1") || opcao.equals("2"))
						System.out.println("opcão Inválida para estem modo.");
					else
						saida.println(opcao);

					if (opcao.equals("3")) {

						System.out.println("Diretório: ");
						saida.println(teclado.nextLine());
						String saida = entrada.nextLine();
						if (saida.equals("/")) // utilizo o "/" pq os diretórios
												// nao podem ser nomeados assim
							System.out
									.println("Diretório inválido ou inexistente");
						else
							printData(saida.split("/s/"));

					} else if (opcao.equals("4"))
						printData(entrada.nextLine().split("/s/"));
					else if (opcao.equals("5")) {

						System.out.println("Entre com o nome do documento: ");
						String docName = teclado.nextLine();
						saida.println(docName);
						if (entrada.nextLine().equals("0"))
							System.out.println("é um diretório");
						else
							receiveFile(docName);

					} else if (opcao.equals("6"))
						sendFile();

					else if (opcao.equals("7")) {
						System.out
								.println("Entre com o nome do documento que quer excluir: ");
						saida.println(teclado.nextLine());
					} else if (opcao.equals("8"))
						System.out.println("tchau");

				} while (!opcao.equals("8"));

			} else if (opcao.equals("0"))
				System.out.println("tchau");
			else {
				System.out.println("Opção Inválido");
			}

		} while (!opcao.equals("0"));
	}
	
	/*
	 * receiveFile recebe um arquivo do servidor.
	 */
	private void receiveFile(String nameFile) {
		long fileSize = entrada.nextLong();
		try {
			fos = new FileOutputStream(new File(this.path + nameFile));
			int buffer = 4096;
			byte[] bufferFile = new byte[buffer];
			int bytesRead = -1;
			long initialSize = 0;
			System.out.println("recebendo...");
			while (initialSize < fileSize) {
				bytesRead = is.read(bufferFile, 0, buffer);
				initialSize = initialSize + bytesRead;
				fos.write(bufferFile, 0, bytesRead);
			}
			System.out.println("arquivo recebido");
			fos.flush();
			fos.close();

		} catch (IOException e) {
			System.out.println("erro");
		}

	}
	
	/*
	 * registerClient cadastra um novo usuário, apenas o administrador acessa esse método
	 */
	private void registerClient() {

		System.out.println("Nome: ");
		saida.println(teclado.nextLine().trim());
		saida.flush();
		System.out.println("User Nome: ");
		saida.println(teclado.nextLine().trim());
		saida.flush();
		System.out.println("Senha: ");
		saida.println(teclado.nextLine().trim());
		saida.flush();
		System.out.println("Cargo: ");
		saida.println(teclado.nextLine().trim());
		saida.flush();

	}
	
	/*
	 * sendFile envia um arquivo ao servidor
	 */
	private void sendFile() {
		int buffer = 4096;
		byte[] bufferFile = new byte[buffer];
		int bytesRead = -1;
		long initialSize = 0;

		System.out.println("Caminho que o arquivo está: ");
		String doc = teclado.nextLine();
		File file = new File(doc);
		if (file.isDirectory())
			System.out
					.println("Não foi possível, Isto é um diretório não um arquivo");
		else {

			System.out
					.println("O arquivo vai para o diretório que você está no momento");
			long fileSize = file.length();
			String[] f = doc.split("/");
			saida.println(f[f.length - 1]);
			saida.flush();
			saida.println(fileSize);
			saida.flush();

			try {
				fileIn = new FileInputStream(file);
				while (initialSize < fileSize) {
					bytesRead = fileIn.read(bufferFile, 0, buffer);
					initialSize = initialSize + bytesRead;
					socketOut.write(bufferFile, 0, bytesRead);
				}
				socketOut.flush();
				fileIn.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	// Este metódo imprime as strings dos diretórios.
	private void printData(String[] dir) {

		for (int i = 0; i < dir.length; i++)
			System.out.println(dir[i]);
	}
	
	/*
	 * cria a pasta DownloadDuto para onde todo o arquivo baixado irão
	 */
	private void creatDownloadDuto(){
		File f = new File("DownloadDuto");
		if(f.exists())
			System.out.println("Os arquivos baixados será enviado para a pasta DownloadDuto");
		else{
			f.mkdir();
			System.out.println("Pasta criada no seguinte caminho: "+f.getAbsolutePath());
		}
		this.path = f.getAbsolutePath()+"/";
		
	}
	// Fecha as conexões
	public void closeConection() throws IOException {
		saida.close();
		entrada.close();
		is.close();
		teclado.close();
	}

}
