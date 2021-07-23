import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

class Chat{


    static void envia_mensaje_multicast(byte buffer[], String ip, int puerto) throws IOException{
		DatagramSocket socket = new DatagramSocket();
		socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), puerto));
		socket.close();
	}

	static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException{
		byte[] buffer = new byte[longitud_mensaje];
		DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
		socket.receive(paquete);
		return paquete.getData();
	}


	static class Worker extends Thread{
		@Override
		public void run(){
			try{
				while(true){
					InetAddress group = InetAddress.getByName("230.0.0.0");
					MulticastSocket socket = new MulticastSocket(50000);
					socket.joinGroup(group);
					byte msj[] = recibe_mensaje_multicast(socket,1024);
					System.out.print("\n");
					System.out.println(new String(msj,"Windows-1252"));
					System.out.println("");
					socket.leaveGroup(group);
					socket.close();
				}
			}catch(Exception except){
				System.err.println(except.toString());
			}
		}
	}

	public static void main(String args[])throws Exception{
		Worker w = new Worker();
		w.start();
		String nombre = args[0];
		BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Ingrese el mensaje a enviar: ");
		while(true){
			String message = nombre + ":" + buffer.readLine();
			envia_mensaje_multicast(message.getBytes(), "230.0.0.0", 50000);
		}
	}

	
}