package view;
import java.io.IOException;

import model.Servidor;

public class application {
	public static void main(String[] args){
		

			try {
				new Servidor(12345).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		
	}

}
