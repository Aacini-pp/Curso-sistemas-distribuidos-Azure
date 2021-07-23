import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class ClaseRMI extends UnicastRemoteObject implements InterfaceRMI
{
  // es necesario que el contructor default de la clase ClaseRMI invoque el constructor de la super-clase
  public ClaseRMI() throws RemoteException
  {
    super( );
  }
  


public float[][] multiplica_matrices(float[][] A,float[][] B) throws RemoteException
 {
  int N = A[0].length;
  float[][] C = new float[N/2][N/2];
  for (int i = 0; i < N/2; i++)
    for (int j = 0; j < N/2; j++)
      for (int k = 0; k < N; k++)
        C[i][j] += A[i][k] * B[j][k];
    return C;
 }







  public long checksum(int[][] m) throws RemoteException
  {
    long s = 0;
    for (int i = 0; i < m.length; i++)
      for (int j = 0; j < m[0].length; j++)
        s += m[i][j];
    return s;
  }
}