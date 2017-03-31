package view;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import model.ClienteLocal;

public class ClientApplication {

	public static void main(String[] args) {
		Scanner keyBoard = new Scanner(System.in);
		System.out.println("IP do servidor: ");
		String ip = keyBoard.nextLine();
		ClienteLocal cliente = new ClienteLocal(ip,12345);
		
		//int op;
		try {
			cliente.conectClient();
			//do{
			String value = cliente.logIn();
			if(value.equals("A")){
				cliente.menuAdmin();
				cliente.closeConection();
			}
			else if (value.equals("S")){
				cliente.menuClient();
				cliente.closeConection();	
			}
			else if (value.equals("-1"))
				System.out.println("Usuário não existe");
			else if (value.equals("-2"))
				System.out.println("Senha inválida");
			else if(value.equals("-3"))
				System.out.println("Usuário está online");
			else{
				System.out.println("Tentar Fazer cadastro novamente? \n 1-SIM \n 0-NÂO");
				}
			
				//op = Integer.parseInt(teclado.nextLine());
			//}
			//}while(op != 0);
		} catch (IOException e) {
			
			System.out.println("Servidor Desligado!");
		} catch (ClassNotFoundException e) {
			System.out.println("Sei lá o que é esse erro!");
		}catch(NoSuchElementException e){
			System.out.println("Usuario saiu");
		}
		
		keyBoard.close();
			
		

	}

}
