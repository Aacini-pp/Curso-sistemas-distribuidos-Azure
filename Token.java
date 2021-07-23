import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.lang.Thread;
import java.nio.ByteBuffer;

class Token
{
  static DataInputStream entrada;
  static DataOutputStream salida;
  static boolean primera_vez = true;
  static String ip;
  static long token = 0;
  static int nodo;
  static int contador = 0;

  static class Worker extends Thread
  {
    
    public void run()
    {
      //Algoritmo 1
      try
      {
        //Declaramos el socket con el puerto mediante el cual va a estar atendiendo el servidor a los clientes
        ServerSocket servidor = new ServerSocket(50000);
        
        //Esperamos a que se conecte el cliente
        Socket conexion = servidor.accept();
        
        //Declaramos el stream de entrada
        entrada = new DataInputStream(conexion.getInputStream());
      }
      catch (Exception e)
      {
        System.err.println(e.getMessage());
      }

    }
  }

  public static void main(String[] args) throws Exception
  {
    if (args.length != 2)
    {
      System.err.println("Se debe pasar como parametros el numero de nodo y la IP del siguiente nodo");
      System.exit(1);
    }

    nodo = Integer.valueOf(args[0]);  // el primer parametro es el numero de nodo
    ip = args[1];  // el segundo parametro es la IP del siguiente nodo en el anillo

    //Algoritmo 2
    Worker w = new Worker();
    w.start();

    //Socket para establecer la conexion con el servidor
    Socket conexion = null;

    //Establecemos la conexion con el servidor, en la cual 
    for(;;)
    try
    {
      conexion = new Socket(ip,50000);
      break;
    }
    catch (Exception e)
    {
      Thread.sleep(100);
    }
    //Creamos los streams de entrada y salida.
    salida = new DataOutputStream(conexion.getOutputStream());

    w.join();
    for(;;)
    {
      if(nodo == 0)
      {
        if(primera_vez == true)
          primera_vez = false;
        else{
          token =  entrada.readLong();
          contador++;
        }
          
      }
      else{
        token =  entrada.readLong();
        contador++;
      }
       
      token++;
      System.out.println("Nodo:"+nodo+", Contador: "+contador+", Token: "+token);   

      if(nodo==0 && contador ==1000 ){
        break; //8.3.1 Salir del ciclo.
       }
           


      salida.writeLong(token);
    }
  }
}