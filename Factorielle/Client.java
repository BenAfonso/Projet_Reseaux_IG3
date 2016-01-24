import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Classe: Client (Factorielle)
 * Rôle: Permet la connexion paramétrable à un serveur, l'envoie d'un entier N et la réception du résultat à Factorielle(N)
 * Utilisation: 
 * -> java Client ADDRESSE PORT N
 * -> java Client -h / java Client --help pour obtenir de l'aide
 * @author benjaminafonso
 *
 */
public class Client {
	
	InetAddress address;
	int port;
	int n;
	
	/**
	 * Classe: Listen
	 * Rôle: Permet l'écoute du thread client sur le socket présent
	 * @author benjaminafonso
	 *
	 */
    class Listen extends Thread {

        Socket socket;
        InputStream sInput;
    
        Listen(Socket socket) {
	    try {
	        this.socket = socket;
	        sInput  = socket.getInputStream();
	    }//try
	    catch (Exception e) {}
        }//Listen

        public void run() {
            Scanner sc = new Scanner(sInput);

                if (sc.hasNext()) {
                    String msg = sc.nextLine();
                    System.out.println(msg);
                }//if
                sc.close();
            }//run
        }//Listen

    /************************************************/
    /************ Constructeur de client ************/
    /************************************************/
    
	Client(InetAddress address, int port,int n){
		this.address = address;
		this.port = port;
		this.n = n;
	}
	
	/************************************************/
    /*********** Lancement de la requête ************/
    /************************************************/
	
    void run() throws SocketException, IOException, UnknownHostException {
    	Socket socket = new Socket(address, port);
        Listen l = new Listen(socket);
        l.start();
        PrintStream output = new PrintStream(socket.getOutputStream());
        Scanner sc = new Scanner(System.in);
        output.println(n);
        sc.close();
    
    }
	
    /************************************************/
    /***** Programme principal et configuration *****/
    /************************************************/
    
	public static void main(String[] args) throws SocketException, UnknownHostException, IOException {
		// TODO Auto-generated method stub
		try
		{
		if (args[0].equals("-h") || args[0].equals("--help")){
			System.out.println("~~~ java Client Help");
			System.out.println("Calcul de Factorielle(N)");
			System.out.println("---> java Client IP PORT N");
			System.out.println("# IP (ex: localhost, xxx.xxx.xxx.xxx, ...");
			System.out.println("# PORT (ex: 50000)");
			System.out.println("# N (ex: 1,2,3,4,5,...)");
		}
		else{
			InetAddress address=null;
			int port=0;
			int n=0;
			try{
				address = InetAddress.getByName(args[0]);
				port = Integer.parseInt(args[1]);
				n = Integer.parseInt(args[2]);
			}catch(Exception e)
			{
				System.out.println("[Erreur] -> 'java Client -h' pour obtenir de l'aide.");
			}
			
			try
			{
				Client c=new Client(address,port,n); 
				c.run();
			}catch(Exception e)
			{
				System.out.println("[Erreur] Serveur non joignable ou erroné.");
			}
			

		}
		}catch(Exception e)
		{
			System.out.println("[Erreur] -> 'java Client -h' pour obtenir de l'aide.");
		}

	}

}
