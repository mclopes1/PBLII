package model;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class Diretorios {
	private LinkedList<String> path;
	private String nPath;

	public Diretorios() {
		this.path = new LinkedList<String>();
		this.path.add("/home/murilo");
		nPath = "/home/murilo";
	}
	

	public String home() throws IOException {
		String s = "";
		String concatena = "";
		Process p = Runtime.getRuntime().exec("ls " + "/home/murilo");
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		s = stdInput.readLine();
		while (s != null) {
			concatena = concatena + s + "/s/";
			s = stdInput.readLine();

		}
		
		return concatena;

	}
	/*
	 * recebe o nome do diretório e adiciona a uma lista encadeada que guarda as pastas que o usuário
	 * navegou o momento
	 */
	public void navegar(String diretorio) {
		path.addLast("/" + diretorio);
		concatena();
	}
	
	/*
	 * Como cada diretório que usuário navega fica salvo em uma lista encadeada o método concatena 
	 * é responsável por concatenar todos esses diretório para poder formar o caminho.
	 */
	private void concatena() {
		nPath = "";
		for (int i = 0; i < path.size(); i++)
			nPath += path.get(i);
	}
	
	/*
	 * Lista o conteúdo do diretório que o usuário está no momento
	 */
	public String listar() throws IOException {
		String s = "";
		String concatena = "";
		Process p = Runtime.getRuntime().exec("ls " + nPath);
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		s = stdInput.readLine();
		while (s != null) {
			concatena = concatena + s + "/s/";
			s = stdInput.readLine();

		}
		
		return concatena;
	}
	
	/*
	 * volta um diretório antes. Para voltar o metódo remove o ultimo item da lista encadeada que 
	 * indica o diretório atual
	 */
	public void back() throws IOException {
		if (!nPath.equals("/home/murilo")) {
			path.removeLast();
			concatena();
		}
		else{
			System.out.println("Vc não tem mais para onde navegar");
		}
	}
	
	/*
	 * remove um arquivo
	 */
	public void remove(String doc) throws IOException{
		
		System.out.println(doc);
		Runtime.getRuntime().exec("rm " + doc);
	
	}
	
	public String getPath(String doc){
		concatena();
		return this.nPath + "/"+doc;
		}
	
	public String getPathDir(){
		return this.nPath;
	}
}
