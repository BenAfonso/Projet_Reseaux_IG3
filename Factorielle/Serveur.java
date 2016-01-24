import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * --------------------------- Documentation ------------------------------
 * @author benjaminafonso
 * Classe: Serveur (Factorielle)
 * Rôle: Crée un serveur de calcul pour la fonction factorielle sur un port donné
 * Utilisation: 
 * -> java Serveur PORT
 * -> java Serveur -h pour obtenir de l'aide
 * Fonctionnalité:
 * -> Nombre maximum de connexions avant la création d'un nouveau serveur
 * -> Serveur disposant d'un cache de calcul pour ne pas tout recalculer
 * -------------------------------------------------------------------------
 */
public class Serveur {
	private static final int maxconnexions = 10;
	int port; // Port number for server
	int num=0; //Number of clients
	int res=1;
	// Liste des clients 
	ArrayList<ClientThread> socks = new ArrayList<ClientThread>();
	// Cache de calcul
	Hashtable<Integer, Integer> cache = new Hashtable<Integer, Integer>();
	ArrayList<Integer> N = new ArrayList<Integer>();
	
	
	/** 
	 * Classe ClientThread
	 * Rôle: Permet au serveur de gérer les connexions entrantes
	 * @author benjaminafonso
	 *
	 */
	// ET LA ON THREAD VNR
    class ClientThread extends Thread {
        
        Socket socket;
        InputStream sInput;
        OutputStream sOutput;
    
        ClientThread(Socket socket) {
	    try {
	        this.socket = socket;
	        sOutput = socket.getOutputStream();
	        sInput  = socket.getInputStream();
	    }//try
	    catch (Exception e) {}
        }//ClientThread

        public void run() {
            Scanner sc = new Scanner(sInput);
 
                if (sc.hasNext()) {
                    int n = sc.nextInt();           
                    if(n > 1){
                    	N.add(n);
                        System.out.println("[Calcul] Le client "+(num)+" demande le calcul de factorielle "+n+"\n");
                        
                        // On vérifie sur n n'est pas dans le cache 
                    	if (cache.get(n) != null){
                        	System.out.println("[InfoCool] Coool ! Il semblerait que "+n+" soit dans l'cache ! On n'calcule pas ! Yaaaay ! REPOS.");
                        	res=res*cache.get(n);
                        	n=1;
                        }
        				FauxClient fake1 = new FauxClient(n-1);
        				// OOOOULAA
        				res=res*n;
        				
        				//n=n-1;
        				try {
							fake1.recurse();
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        				
        			}
                    
        			else{
        				// Fermeture du scanner.
        				sc.close();
        				System.out.println("\n[Terminé] Le résultat est: " + res);
        				// Envoie du résultat au client initial
        				PrintStream output = new PrintStream(socks.get(0).sOutput);
        	            output.println("Le résultat est: " +res);
        	            
        	            //Stockage du résultat dans le Cache
        	            cache.put(N.get(0), res);
        	            
        				// Ré-initialisation du res
        				res = 1;
        				// Close de toutes les connexions !
        				closeConnexions();
        				
        			}
                    
                }
                
        }// FaimDuRunlol

    }//Fin du thread
    
    
    /**
     * Classe: Faux Client
     * Rôle: Permet la création d'un faux client pour faire des requêtes de calcul
     * @author benjaminafonso
     *
     */
    
	// ET LA ON THREAD VNR
    class FauxClient extends Thread {
        
        int n;
    
        FauxClient(int n) {
	        this.n = n;
        }//ClientThread

        void recurse() throws SocketException, IOException, UnknownHostException {
        	
        	//Création d'une socket
            InetAddress address = InetAddress.getLocalHost();
            Socket socket = new Socket(address, port);
            
            // Ecriture de la valeur de n sur le PrintStream de sortie
            PrintStream output = new PrintStream(socket.getOutputStream());
            output.println(n);
            
            // Fermeture de la socket du faux Client
            socket.close();
        }
    }//Fin du thread
    
    
	
	void closeConnexions(){
		System.out.println("\n[Ménage] Fermeture des connexions clients.\n");
		for (int j = 0; j < socks.size(); j++) {
			try {
				
				System.out.println("	[Adieu] Le client "+j+" va maintenant nous quitter.");
				// On décrémente le compteur de clients à chaque fermeture
				num=num-1;
				// On close une à une les sockets
				socks.get(j).socket.close();
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		// On vide les n et les clients !
		System.out.println("\n[Ménage] Nettoyage de l'historique des N.");
		N.clear();
		System.out.println("\n[Ménage] Nettoyage du tableau de clients.\n");
		socks.clear();
	}
    
    /**********************************************/
	/********** Constructeur de serveur ***********/
	/**********************************************/
	
	Serveur(int port){
		this.port=port;
	}
	
	// Juste pour la beauté de la chose
    public final static void clearConsole()
    {
        try
        {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows"))
            {
                Runtime.getRuntime().exec("cls");
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e)
        {
        }
    }

    /***********************************************/
	/************ Lanceur de serveur ***************/
	/***********************************************/
    
	@SuppressWarnings("resource")
	void run() {
		try {
			clearConsole();
			// Affichage du message vraiment sympathique
			System.out.println("======= Lancement du serveur factorielle de l'espace =======");
			System.out.println("~~ Port: "+port);
			System.out.println("============================================================\n");
			// Création du socket serveur
		    ServerSocket sServer = new ServerSocket(port);
		    
		    while (true) {
		    // Acceptation de clients
			Socket s = sServer.accept();
			System.out.println("[Client] Nouveau client connecté: "+num);
			ClientThread c = new ClientThread(s);
	        num++;
	                
	        if (num > maxconnexions){
	        	// Fork le serveur
	        	System.out.println("\n[Wooooooow] Ca va exploser,création d'un nouveau serveur.");
	        	Serveur serveur2 = new Serveur(this.port);
	        	num=0; //...
	        	serveur2.res=res;
	    		serveur2.run();
	        }
	        socks.add(c);
			c.start();
			
		    }
		}
		catch (Exception e) {}
	    }

	
    /**********************************************/
	/**** Programme principal et configuration ****/
	/**********************************************/
	
	public static void main(String[] args) {
		try{
		if (args[0].equals("-h")){
			clearConsole();
			System.out.println("========== Serveur de calcul de factorielle ========");
			System.out.println("~~~ java Serveur Help");
			System.out.println("Nombre de connexions maximale: 8 (Nouveau serveur après)");
			System.out.println("---> java PORT");
			System.out.println("# PORT (default: 50000)");
		}
		else{
			int port = Integer.parseInt(args[0]);
			Serveur serveur = new Serveur(50000);
			if (port != 0){
				serveur.port = port;
			}
			serveur.run();
		}
		}catch(Exception e)
		{
			System.out.println("Utilisation: java Serveur -h pour obtenir de l'aide.");
		}
	}

}
