/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//package distribuidos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class PI
{
  static Object lock = new Object();
  static double pi = 0;
  static class Worker extends Thread
  {
    Socket conexion;
    Worker(Socket conexion)
    {
      this.conexion = conexion;
    }
    public void run()
    {
        
        try{
             // Algoritmo 1
        
            //1. Crear los streams de entrada y salida.
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            //2. Declarar la variable "x" de tipo double.
            //3. Recibir en la variable "x" la suma calculada por el cliente.
            double x =entrada.readDouble(); 
            System.err.println("recibido: "+x);
            
     
            //4. En un bloque synchronized mediante el objeto "lock":
            synchronized(lock){
             //4.1 Asignar a la variable "pi" la expresión: x+pi
                 pi=x+pi;
            }
             //5. Cerrar los streams de entrada y salida.
             entrada.close();
             salida.close();
            //6. Cerrar la conexión "conexion"
            
            
            
           
            
        }catch(Exception e){
            
            
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
    if (nodo == 0)
    {
      // Algoritmo 2 // servidor
//     1. Declarar una variable "servidor" de tipo ServerSocket.
//    2. Crear un socket servidor utilizando el puerto 50000 y asignarlo a la variable "servidor".
        ServerSocket servidor = new ServerSocket(50000);

//    3. Declarar un vector "w" de tipo Worker con 4 elementos.
        Worker[] w= new Worker[4];

//    4. Declarar una variable entera "i" y asignarle cero.
//    5. En un ciclo:
        for(int i=0; i<4;i++){  // esperamos a que 4 clientes se conecten.
            //    5.1 Si la variable "i" es igual a 4, entonces salir del ciclo.
//    5.2 Declarar una variable "conexion" de tipo Socket.
//    5.3 Invocar el método servidor.accept() y asignar el resultado a la variable "conexion".
             Socket conexion = servidor.accept();
//    5.4 Crear una instancia de la clase Worker, pasando como parámetro la variable "conexion". Asignar la instancia al elemento w[i].
            w[i]=new Worker(conexion);  //guardamos la conexion  de cada cliente
//    5.5 Invocar el método w[i].start()
            w[i].start(); //iniciamos el hilo
//    5.6 Incrementar la variable "i".
//    5.7 Ir al paso 5.1
        }


//    6. Declarar una variable "i" entera y asignarle cero.
//    7. En un ciclo:
//    7.1 Si la variable "i" es igual a 4, entonces salir del ciclo.
//    7.2 Invocar el método w[i].join()
//    7.3 Incrementar la variable "i".
//    7.4 Ir al paso 7.1

         for(int i=0; i<4;i++){
            w[i].join();
        
        }
         
         
//    8. Desplegar el valor de la variable "pi".
        System.out.println("Pi es: "+pi);
       
        
        
        
    }
    else
    {
      // Algoritmo 3 --cliente
        
//1. Declarar la variable "conexion" de tipo Socket y asignarle null.
        Socket conexion= null;
//2. Realizar la conexión con el servidor implementando re-intento. Asignar el socket a la variable "conexion".
        for(;;) //intentar la conexion de forma indefinida
            try{
                
                conexion = new Socket("localhost",50000);
                break;

            
            }catch(Exception e ){
            
                Thread.sleep(100);
            
            }


//3. Crear los streams de entrada y salida.
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
//4. Declarar la variable "suma" de tipo double y asignarle cero.

            double suma =0;
//5. Declarar una variable "i" de tipo entero y asignarle cero.
            
//6. En un ciclo:
//6.1 Si la variable "i" es igual a 10000000, entonces salir del ciclo.
//6.2 Asignar a la variable "suma" la expresión:  4.0/(8*i+2*(nodo-2)+3)+suma
//6.3 Incrementar la variable "i".
//6.4 Ir al paso 6.1
           for(int i=0; i<10000000;i++){
                suma=4.0/(8*i+2*(nodo-2)+3)+suma;
               
           }



//7. Asignar a la variable "suma" la expresión:  nodo%2==0?-suma:suma
        suma = nodo%2==0?-suma:suma;
//8. Enviar al servidor el valor de la variable "suma".
       salida.writeDouble(suma);
//9. Cerrar los streams de entrada y salida.
//10. Cerrar la conexión "conexion".
        entrada.close();
        salida.close();
        conexion.close();
    
//        
        
        
        
    }
  }
}