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
	static String requisicao;

	public static void main(String[] args) throws IOException {
		System.out.println(
				"Bem vindo ao navegador: digite a url desejada e a porta logo em seguida.\nCASO VOCE QUEIRA ACESSAR UM DIREOT");
		System.out.println("navegador>");
		s = new Scanner(System.in);
		String re = s.nextLine();

		String[] parts = re.split(" ");
		if (parts.length == 2) {
			url = parts[0];
			door = "80";
		} else if (parts.length == 1) {
			url = parts[0];
			door = "80";
		} else {
			System.out.println("O ESPACO EM BRANCO");
		}
		String novourl = url.replace("http://", "").replace("https://", "");
		
		String[] vetor = novourl.split("/");
		String caminho = "";
		String extArquivo = null;
		for (int i = vetor.length - 1; i > 0; i--) {
			caminho = vetor[i].concat("/" + caminho);
		}

		// www.dcomp.ufsj.edu.br/~fls/redes/tp1.txt
		caminho = "/" + caminho;
		caminho = caminho.substring(0, caminho.length() - 1);
		extArquivo = (caminho.substring(caminho.lastIndexOf("/") + 1));
		if(extArquivo.length() == 0){
			extArquivo = "index";
		}
		int porta = Integer.parseInt(door);
		socket = new Socket(vetor[0], porta);

		if (socket.isConnected()) {

			System.out.println("Conectado a " + socket.getInetAddress());

			if (novourl.substring(novourl.length() - 1).equals("/")) {
				requisicao = "GET " + caminho + "/ HTTP/1.1\r\n" + "Host: " + vetor[0] + "\r\n" + "\r\n";
				extArquivo = extArquivo + ".html";
			} else {
				requisicao = "GET " + caminho + " HTTP/1.1\r\n" + "Host: " + vetor[0] + "\r\n" + "\r\n";
			}
			OutputStream envioServ = socket.getOutputStream();

			byte[] b = requisicao.getBytes();

			envioServ.write(b);

			envioServ.flush();

			sc = new Scanner(socket.getInputStream());
			arquivo = new FileWriter(new File(extArquivo));

			while (sc.hasNext()) {
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