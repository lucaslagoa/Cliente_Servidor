import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Scanner;

public class Servidor implements Runnable {

	static ObjectOutputStream saida;
	static OutputStream os;
	static int porta = 8087;
	static String navegador;
	static String url;
	static String door;
	static PrintStream mensagem;
	static Socket servidor;
	static ServerSocket sservidor;
	static BufferedWriter responseWriter;
	
	
	private static final String OUTPUT_NOT_FOUND = "<html><head><title>Not Found</title></head><body><p>Resource Not Found!!</p></body></html>";
	private static final String OUTPUT_HEADERS_NOT_FOUND = "HTTP/1.1 404 Not Found\r\n" +
	    "Content-Type: text/html\r\n" + 
	    "Content-Length: ";
	private static final String OUTPUT_END_OF_HEADERS = "\r\n\r\n";	
	Servidor(Socket servidor) {
	      this.servidor = servidor;
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {

		sservidor = new ServerSocket(porta);
		System.out.println("Servidor está rodando na porta - " + porta + "\n");
		
		 while (true) {
	         Socket servidor = sservidor.accept();
	         new Thread(new Servidor(servidor)).start();
	      }
		 
	}
	
	public void run() {
		System.out.println("Nova conexão com o servidor " + servidor.getInetAddress().getHostAddress() + "\n");

		try {
			responseWriter = new BufferedWriter(new OutputStreamWriter(
				        new BufferedOutputStream(servidor.getOutputStream()), "UTF-8"));
			//responseWriter.write(OUTPUT_HEADERS + OUTPUT.length() + OUTPUT_END_OF_HEADERS + OUTPUT);

			Scanner s = new Scanner(servidor.getInputStream());
//
			while (s.hasNextLine()) {
				String requisicao = s.nextLine();
				System.out.println(requisicao);
				String[] parts = requisicao.split(" ");
				if (parts.length == 3) {
					navegador = parts[0];
					url = parts[1];
					door = parts[2];
				} else if (parts.length == 2) {
					navegador = parts[0];
					url = parts[1];
					door = "8080";
				} else {
					System.out.println("O ESPACO EM BRANCO");
				}
				break;
			}
			GET();
			responseWriter.flush();
			responseWriter.close();
			servidor.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	static public void GET() throws IOException, ClassNotFoundException {

		String nomeArquivo = "/home/lucas/workspace/Servidor/src/" + url;
		
		File file = new File(nomeArquivo);
		
		
		String mime = Files.probeContentType(file.toPath());
		System.out.println("Content Type: " + mime);

//		saida.write("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\nContent-Length: 0\r\n\r\n".getBytes("ASCII"));
//		saida.flush();
//		os.write(new String("HTTP/1.1 200 OK\r\nContent-Length: 0\r\n\r\n").getBytes());
//		os.flush();

		
		if (file.exists()) {
			OutputStream stream = servidor.getOutputStream();
			
			String send = "HTTP/1.1 200 OK\r\nContent-Type: "+mime+"\r\n";
			stream.write(send.getBytes(Charset.forName("UTF-8")));
			//stream.write("HTTP/1.1 200 OK\r\nContent-Type: "+mime+"\r\n".getBytes());
//			responseWriter.write(msg);
//			saida.flush();

			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
 
//			String contentLength = "Content-Length: " + String.valueOf(file.length()) + "\r\n\r\n";
			stream.write(("Content-Length: " + String.valueOf(file.length()) + "\r\n\r\n").getBytes());
//			responseWriter.write(contentLength);
//			responseWriter.write("\r\n\r\n");
//			saida.writeLong((long) file.length());
//			saida.flush();

			// long i = 0;
			
			long tam = file.length();
			int valor = 0;
			byte[] contents;
			int i = 0;
			while (tam > 0) {

				if (tam >= 1) {
					tam = tam - 1;
					valor = 1;
				} else if (tam < 1) {
					valor = (int) tam;
					tam = 0;
				}

				contents = new byte[valor];
				bis.read(contents, 0, valor);
				stream.write(contents);
				
//				os.write(contents);
//				os.flush();C

				//System.out.println("Enviando arquivo ... " + (i * 100) /
				//file.length() + "% completo!");
				//i += 1;

			}
			stream.flush();		
			fis.close();
			bis.close();

		} else {
			responseWriter.write(OUTPUT_HEADERS_NOT_FOUND + OUTPUT_NOT_FOUND.length() + OUTPUT_END_OF_HEADERS + OUTPUT_NOT_FOUND);
		}

	}

	
}