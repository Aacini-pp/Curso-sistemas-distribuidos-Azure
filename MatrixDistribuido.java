import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.lang.Thread;
import java.nio.ByteBuffer;

class MatrixDistribuido
{
    //Numero de renglones y columnas de las matrices principales
    static int N = 1000;
    //Numero de filas que tendran las 6 matrices(AN,BN) que utilizaran los clientes: nodo 1,2,3,4.
    static int FC = N/2;
    //Numero de elementos que tendran las 3 matrices(CN) que utilizaran los clientes: nodo 1,2,3,4.
    static int NC = FC*N;

    //Declaracion de las matrices a multiplicar, las cuales seran ocupadas por el servidor
    static int[][] A = new int[N][N];
    static int[][] B = new int[N][N];
    //Declaracion de la matriz que guardara el resultado de C=AXB
    static int[][] C = new int[N][N];
    //Declaracion del objeto lock mediante el cual se se lleva a cabo lo sincrinizacion de hilos
    static Object lock = new Object();
    //Declaracion de las matrices que utilizara el cliente 1
    static int[][] A1 = new int[FC][N];
    static int[][] B1 = new int[FC][N];
    static int[][] C1 = new int[FC][FC];
    //Declaracion de las matrices que utilizara el cliente 2
    static int[][] A2 = new int[FC][N];
    static int[][] B2 = new int[FC][N];
    static int[][] C2 = new int[FC][FC];
    //Declaracion de las matrices que utilizara el cliente 3
    static int[][] A3 = new int[FC][N];
    static int[][] B3 = new int[FC][N];
    static int[][] C3 = new int[FC][FC];
    //Declaracion de las matrices que utilizara el cliente 4
    static int[][] A4 = new int[FC][N];
    static int[][] B4 = new int[FC][N];
    static int[][] C4 = new int[FC][FC];
    
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

                //Recibimos un numero correspondiente al cliente(nodo) que atenderemos: 1,2,3,4
                int nodon=entrada.readInt();
                //Buffer para enviar al cliente FC enteros de 4 bytes
                byte[] a = new byte[NC*4];
                ByteBuffer b = ByteBuffer.allocate(NC*4);

                int aux,aux2;
                //Asignamos el valor a auxilar que se encargara de seleccionar la seccion de la matriz A que 
                //ocupara el cliente en cuestion.
                if(nodon==1 || nodon==2)
                    aux=0;
                else
                    aux=FC;
                
                //Llenamos el ByteBuffer con los datos de la matriz A que le corresponden  al cliente 
                for(int i=0+aux; i<FC+aux; i++)
                    for(int j=0; j<N; j++)
                        b.putInt(A[i][j]);
                //Enviamos los FC enteros de la matriz A al cliente
                a = b.array();
                salida.write(a);
                
                //Buffer para enviar al cliente FC enteros de 4 bytes
                //Limpiamos el buffer
                b.clear();
                
                //Asignamos el valor a auxilar que se encargara de seleccionar la seccion de la matriz B que 
                //ocupara el cliente en cuestion.
                if(nodon==1 || nodon==3)
                    aux=0;
                else
                    aux=FC;
                //Enviamos los datos de la matriz B que le corresponden  al cliente 
                for(int i=0+aux; i<FC+aux; i++)
                    for(int j=0; j<N; j++)
                        b.putInt(B[i][j]);
                //Enviamos los FC enteros de la matriz B al cliente
                a = b.array();
                salida.write(a);

                //Leemos el resultado de la multiplicacion
                read(entrada,a,0,FC*FC*4);
                b = ByteBuffer.wrap(a);
                
                if(nodon == 1 || nodon == 2)
                {
                    aux=FC*nodon;
                    aux2=FC*(nodon-1);
                    for(int i=0; i<FC; i++)
                        for(int j=aux2; j<aux; j++)
                        {
                            //Escribimos en la matriz c, mediante un bloque synchronized para el manejo de la zona critica
                            synchronized(lock)
                            {        
                                C[i][j]=b.getInt();
                            }
                        }
                }
                else
                {
                    aux=FC*(nodon-2);
                    aux2=FC*(nodon-3);
                    for(int i=FC; i<N; i++)
                        for(int j=aux2; j<aux; j++)
                        {
                            //Escribimos en la matriz c, mediante un bloque synchronized para el manejo de la zona critica
                            synchronized(lock)
                            {
                                C[i][j]=b.getInt();
                            }
                        }
                }
                //Cerramos los streams de entrada y salida y cerramos la conexion
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
      System.err.println("java MulMatricesD <nodo>");
      System.exit(0);
    }
    int nodo = Integer.valueOf(args[0]);
    //Codigo que ejecutara el nodo 0 (nodo servidor) 
    if (nodo == 0)
    {
        //Inicializamos las matrices
        for(int i=0; i<N; i++)
            for(int j=0; j<N; j++)
            {
                A[i][j]=2*i+3*j;
                B[i][j]=2*i-3*j;
                C[i][j]=0;
            }

        //mostramos la matriz A y B  
        if(N==4){
            System.out.println("Mostrando matriz A: ");
            for(int i=0; i<N; i++){
                for(int j=0; j<N; j++)
                {
                    System.out.print( A[i][j]+"\t" );
                }
                System.out.println( " \n" );
            }
             
            System.out.println("Mostrando matriz B: ");
            
            for(int i=0; i<N; i++){
                for(int j=0; j<N; j++)
                {
                    System.out.print( B[i][j]+"  " );
                }
                System.out.println( " \n" );
            }

        }
        
        
        //Transpone la matriz B, la matriz traspuesta queda en B
        for (int i = 0; i < N; i++)
            for (int j = 0; j < i; j++)
            {
                int x = B[i][j];
                B[i][j] = B[j][i];
                B[j][i] = x;
            }
        //Declaramos el socket con el puerto mediante el cual va a estar atendiendo el servidor a los clientes
        ServerSocket servidor = new ServerSocket(50003);
        //Arreglo donde guardaremos la coneccion de cada cliente
        Worker[] w = new Worker[4];

        //Espera la conexion de los nodos 1,2,3,4
        for(int i=0; i<4; i++)
        {
            //Esperamos a que se conecte el cliente
            Socket conexion = servidor.accept();
            //Guardamos el socket de la coneccion del cliente
            w[i] = new Worker(conexion);
            //Atendemos al cliente enviandole las matrices y recibiendo el calculo de la multiplicacion
            w[i].start();
        }
        //Esperamos a que cada cliente termine su ejecucion antes de que el servidor termine su ejecucion
        for(int i=0; i<4; i++)
        {
            w[i].join();
        } 
        //Calcular el checksum de la matriz C.
        long checksum=0;
        for(int i=0; i<N; i++)
        {
            for(int j=0; j<N; j++)
            {
                checksum+=C[i][j];
            }
        }
        //Desplegar el checksum de la matriz C.
        System.out.println("El checksum de la matriz C es:" +checksum);

        //Si N=4 entonces desplegar la matriz C
        if(N==4)
        {
            System.out.println("Matriz C:");
            for(int i=0; i<N; i++)
            {
                for(int j=0; j<N; j++)
                {
                    System.out.print(C[i][j]+" ");
                }
                System.out.println("");
            }
        }
    }
    else
    {
        //Socket para establecer la conexion con el servidor
        Socket conexion = null;

        //Establecemos la conexion con el servidor, en la cual 
        for(;;)
        try
        {
            conexion = new Socket("localhost",50003);
            break;
        }
        catch (Exception e)
        {
            Thread.sleep(100);
        }
        //Creamos los streams de entrada y salida.
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream()); 

        //Enviamos el numero de nodo que somos
        salida.writeInt(nodo);

        //Parte para recibir las matrices a multiplicar

        //Areglo onde guardaremos los N/2*N elementos de la matriz AN 
        byte[] a = new byte[NC*4];
        ByteBuffer b = ByteBuffer.allocate(NC*4);
        //Areglo onde guardaremos los N/2*N elementos de la matriz BN 
        byte[] aa = new byte[NC*4];
        ByteBuffer bb = ByteBuffer.allocate(NC*4);
        
        //Recibimos el los N/2*N elementos de la matriz AN y BN
        read(entrada,a,0,NC*4);
        b = ByteBuffer.wrap(a);

        read(entrada,aa,0,NC*4);
        bb = ByteBuffer.wrap(aa);
        if(nodo == 1)
        {
            for(int i=0; i<FC; i++)
            {
                for(int j=0; j<N; j++)
                {
                    A1[i][j]=b.getInt();
                }   
            }
            
            for(int i=0; i<FC; i++)
            {
                for(int j=0; j<N; j++)
                {
                    B1[i][j]=bb.getInt();
                }   
            }
        }    
        else if(nodo == 2)
        {
            for(int i=0; i<FC; i++)
            {
                for(int j=0; j<N; j++)
                {
                    A2[i][j]=b.getInt();
                }   
            }
            
            for(int i=0; i<FC; i++)
            {
                for(int j=0; j<N; j++)
                {
                    B2[i][j]=bb.getInt();
                }   
            }        
        }    
        else if(nodo == 3)
        {
            for(int i=0; i<FC; i++)
            {
                for(int j=0; j<N; j++)
                {
                    A3[i][j]=b.getInt();
                }   
            }
            for(int i=0; i<FC; i++)
            {
                for(int j=0; j<N; j++)
                {
                    B3[i][j]=bb.getInt();
                }   
            }        
        }
        else
        {
            for(int i=0; i<FC; i++)
            {
                for(int j=0; j<N; j++)
                {
                    A4[i][j]=b.getInt();
                }   
            }
            for(int i=0; i<FC; i++)
            {
                for(int j=0; j<N; j++)
                {
                    B4[i][j]=bb.getInt();
                }   
            }        
        }
        b.clear();
        //Enviar el resultado de multiplicar las matrices
        if(nodo == 1)
        {
            //Multiplicamos 
            MultMatTrans(A1,B1,C1);
            for(int i=0; i<FC; i++)
            {
                for(int j=0; j<FC; j++)
                {
                    b.putInt(C1[i][j]);
                    
                }       
            }
            a = b.array();
            salida.write(a);
        }
        else if(nodo == 2)
        {
            //Multiplicamos 
            MultMatTrans(A2,B2,C2);
            for(int i=0; i<FC; i++)
                for(int j=0; j<FC; j++)
                    b.putInt(C2[i][j]);
            a = b.array();
            salida.write(a);
        }
        else if( nodo == 3)
        {
            //Multiplicamos 
            MultMatTrans(A3,B3,C3);
            for(int i=0; i<FC; i++)
                for(int j=0; j<FC; j++)
                    b.putInt(C3[i][j]);
            a = b.array();
            salida.write(a);
        }
        else
        {
            //Multiplicamos 
            MultMatTrans(A4,B4,C4);
            for(int i=0; i<FC; i++)
                for(int j=0; j<FC; j++)
                    b.putInt(C4[i][j]);
            a = b.array();
            salida.write(a);
        }

        //Cerramos los streams de entrada y salida y cerramos la conexion
        salida.close();
        entrada.close();
        conexion.close();
    }
  }
    //Multiplica 2 matrices, pero la segunda matriz ha sido cambiada por su transpuesta.
    public static void MultMatTrans(int[][] AN,int[][] BN,int[][] CN)
    {
        for (int i = 0; i < FC; i++)
            for (int j = 0; j < FC; j++)
                CN[i][j]=0;

        for (int i = 0; i < FC; i++)
            for (int j = 0; j < FC; j++)
                for (int k = 0; k < N; k++)
                    CN[i][j] += AN[i][k] * BN[j][k];
    }

    // lee del DataInputStream todos los bytes requeridos
    static void read(DataInputStream f,byte[] b,int posicion,int longitud) throws Exception
    {
        while (longitud > 0)
        {
        int n = f.read(b,posicion,longitud);
        posicion += n;
        longitud -= n;
        }
    }
}