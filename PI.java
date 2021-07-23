
import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.lang.Thread;
import java.nio.ByteBuffer;

class PI
{
  //Declaración del objeto lock mediante el cual se se lleva a cabo lo sincrinización de hilos
  static Object lock = new Object();
  //Variable sobra la cual van a escribir los diferentes clientes
  static double pi = 0; 
    
    //Clase worker para recibir la conexion de multiples clientes,
    static class Worker extends Thread
    {
        Socket conexion;

        Worker(Socket conexion)
        {
        this.conexion = conexion;
        }

        public void run()
        {
            try
            {
                //Creamos los streams de entrada y salida.
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                
                //Recibimos el valor de PI calculado por el cliente
                double x = entrada.readDouble();;
                
                //Actualizamos el valor de pi, mediante un bloque synchronized para el manejo de la zono crítica
                synchronized(lock)
                {
                    pi+=x;
                }
                //Cerramos los streams de entrada y salida y cerramos la conexión
                salida.close();
                entrada.close();
                conexion.close();
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
            }
        }
  
    
  }
  public static void main(String[] args) throws Exception
  {
    if (args.length != 1)
    {
      System.err.println("Uso:");
      System.err.println("java PI <nodo>");
      System.exit(0);
    }
    int nodo = Integer.valueOf(args[0]);
    //Código que ejecutara el nodo 0 (nodo servidor) 
    if (nodo == 0)
    {
        //declaramos el socket con el puerto mediante el cual va a estar atendiendo el servidor a los clientes
        ServerSocket servidor = new ServerSocket(50001);
        //Arreglo donde guardaremos la coneccion de cada cliente
        Worker[] w = new Worker[3];


        //Espera la conexion de los nodos 1,2,3
        for(int i=0; i<3; i++)
        {
            //Esperamos a que se conecte el cliente
            Socket conexion = servidor.accept();
            //Guardamos el socket de la coneccion del cliente
            w[i] = new Worker(conexion);
            //Atendemos al cliente
            w[i].start();
        }
        double suma=0;
        //Calcula la parte del valor de PI  que le corresponde al servidor
        for(int i=0; i<10000000; i++)
        {
            suma+=4.0/(8*i+1);
        }
        //Actualizamos el valor de pi, mediante un bloque synchronized para el manejo de la zona crítica
        synchronized(lock)
        {
            pi+=suma;
        }
        //Esṕeramos a que cada cliente termine su ejecución antes de que el servidor termine su ejecución
        for(int i=0; i<3; i++)
        {
            w[i].join();
        }
        //Imprimimos el valor de PI con base a los calculos de los 4 nodos.
        System.out.println("El valor de la variable pi es; " +pi);

    }
    else
    {
        //Socket para establecer la conexion con el servidor
        Socket conexion = null;

        //Establecemos la conexión con el servidor, en la cual 
        for(;;)
        try
        {
            conexion = new Socket("localhost",50001);
            break;
        }
        catch (Exception e)
        {
            Thread.sleep(100);
        }
        //Creamos los streams de entrada y salida.
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream()); 
        double suma=0;
        //Calculamos la parte del valor de PI con base al numero de nodo
        for(int i=0; i<10000000; i++)
        {
            suma+=4.0/(8*i+2*(nodo-1)+3);
        }
        suma = nodo%2==0?suma:-suma;

        //Enviamos el valor de suma al servidor
        salida.writeDouble(suma);
        //Cerramos los streams de entrada y salida y cerramos la conexión
        salida.close();
        entrada.close();
        conexion.close();
    }
  }
}