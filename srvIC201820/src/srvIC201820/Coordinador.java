package srvIC201820;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;


public class Coordinador {

	private static ServerSocket ss;	
	private static final String MAESTRO = "MAESTRO: ";

	public static double getSystemCpuLoad() throws Exception 
	{
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
		AttributeList list = mbs.getAttributes(name, new String[]{ "SystemCpuLoad" });
		if (list.isEmpty()) return Double.NaN;
		Attribute att = (Attribute)list.get(0);
		Double value = (Double)att.getValue();
		// usually takes a couple of seconds before we get real values
		if (value == -1.0) return Double.NaN;
		// returns a percentage value with 1 decimal point precision
		return ((int)(value * 1000) / 10.0);
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub

		System.out.println(MAESTRO + "Establezca puerto de conexion:");
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		int ip = Integer.parseInt(br.readLine());
		System.out.println(MAESTRO + "Empezando servidor maestro en puerto " + ip);

		BufferedWriter writerCPU;
		writerCPU = new BufferedWriter(new FileWriter("CPULoad.txt", true));
		// Adiciona la libreria como un proveedor de seguridad.
		// Necesario para crear llaves.

		int numT = 2;

		ExecutorService exec = Executors.newFixedThreadPool(numT);

		System.out.println(MAESTRO + "Creado pool de tamanio "+ numT); 

		int idThread = 0;
		// Crea el socket que escucha en el puerto seleccionado.
		ss = new ServerSocket(ip);
		System.out.println(MAESTRO + "Socket creado.");		
		while (true) {
			try { 
				Socket sc = ss.accept();
				System.out.println(MAESTRO + "Cliente " + idThread + " aceptado.");
				Delegado4 d4 = new Delegado4(sc,idThread);
				idThread++;
				d4.start();
			} catch (IOException e) {
				System.out.println(MAESTRO + "Error creando el socket cliente.");
				e.printStackTrace();
			}
			writerCPU.newLine();
			writerCPU.write(getSystemCpuLoad()+"");
			writerCPU.close();

		}
	}


}
