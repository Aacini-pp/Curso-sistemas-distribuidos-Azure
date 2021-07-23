
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class Tarea4{

    static DataInputStream entrada;
    static DataOutputStream salida;
    public static Object lock = new Object();
    static boolean primera_vez = true;
    static String ip;
    static int nodo;
    static int token;
    static int contador=0;
    static class Worker extends Thread{
        int x;
        public void run(){
            try {
                ServerSocket server = new ServerSocket(50000);   
                Socket conexion = server.accept();

                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
 
            } catch (Exception e) {
                //TODO: handle exception
            }
            

        }
    }

    public static void main(String[] args) throws Exception{
        if(args.length != 2){
            System.err.println("Se debe pasar como parametros el numero del nodo y la IP del siguiente nodo");
            System.exit(1);
        }
        //primer parametro es numero de nodo, segundo la ip
        nodo = Integer.valueOf(args[0]);
        ip = args[1];

        //algoritmo 2
        Worker w = new Worker();
        w.start();
        Socket conexion = null;
        for(;;){
            try {
                conexion = new Socket(ip,50000);
                break;
            } catch (Exception e) {
                Thread.sleep(500);
            }
        }
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        w.join();
        int c=0;
        while(c==0){
            if(nodo == 0){
                if(primera_vez == true){
                    primera_vez = false;
                    token = 1;
                }else{
                    token = entrada.readInt();
                    contador++;
                    System.out.println("Nodo:"+nodo+", Contador: "+contador+", Token: "+token);
                }
            }
            if(nodo!=0){
                token = entrada.readInt();
                contador++;
                System.out.println("Nodo:"+nodo+", Contador: "+contador+", Token: "+token);
            }
            if(nodo==0 && contador==1000){
                c++;
            }
            salida.writeInt(token);
        }
        entrada.close();
        salida.close();
        conexion.close(); 
    }
}