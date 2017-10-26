import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
	static Scanner s;
	static String navegador;
	static String url;
	static String door;
	private static FileWriter arquivo;
	private static Scanner sc;
	private static Socket socket;

	public static void main(String[] args) throws IOException {
		System.out.println("navegador http://urldesejada Porta");
		s = new Scanner(System.in);
		String re = s.nextLine();
		System.out.println(re);
		String[] parts = re.split(" ");
		if (parts.length == 3) {
			navegador = parts[0];
			url = parts[1];
			door = parts[2];
		} else if (parts.length == 2) {
			navegador = parts[0];
			url = parts[1];
			door = "80";
		} else {
			System.out.println("O ESPACO EM BRANCO");
		}
		int porta = Integer.parseInt(door);
		socket = new Socket(url, porta);
		// verifica se esta conectado
		if (socket.isConnected()) {
			// imprime o endereço de IP do servidor
			System.out.println("Conectado a " + socket.getInetAddress());
			/*
			 * veja que a requisição termina com \r\n que equivale a <CR><LF>
			 * para encerar a requisição tem uma linha em branco
			 */
			String requisicao = "GET /~fls/redes/tp1.txt HTTP/1.1\r\n" + "Host: www.dcomp.ufsj.edu.br\r\n" + "\r\n";
			// OutputStream para enviar a requisição
			//PrintStream envioServ = new PrintStream(socket.getOutputStream());

			OutputStream envioServ = socket.getOutputStream();
			// temos que mandar a requisição no formato de vetor de bytes
			byte[] b = requisicao.getBytes();
			
			// escreve o vetor de bytes no "recurso" de envio
			envioServ.write(b);
			//System.out.println(envioServ);
			// marca a finalização da escrita
			envioServ.flush();
			
			sc = new Scanner(socket.getInputStream());
			arquivo = new FileWriter(new File("tp1.txt"));
		

			while (sc.hasNext()) {
				// imprime uma linha da resposta
				String line = sc.nextLine();
				System.out.println(line);
				arquivo.write(line);
				arquivo.write("\n");
				
			}
			arquivo.flush();
			arquivo.close();
		}

	}

}