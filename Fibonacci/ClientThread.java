import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;



/** 
 * --------------------------- Documentation ------------------------------
 * @author benjaminafonso
 * Classe: ClientThread
 * Rôle: Gérer les connexions avec les clients.
 * Utilisation: ClientThread X = new ClientThread(Socket) pour le créer, X.start() pour le démarrer
 * - Fonction Publiques: 
 * --> getOutput()::Renvoie la PrintStream de sortie
 * --> run()::Demarre le Thread
 *
 * -------------------------------------------------------------------------
 */

public class ClientThread extends Thread
{   
	// Déclaration de type
	Socket socket;
	InputStream sInput;
	OutputStream sOutput;
	int n;
	int res;
	int nbServeurs;
	int PORT1;
	int PORT2;
	Serveur Serveur;
	InetAddress ADRESSE1;
	InetAddress ADRESSE2;
    /*********************************************/
    /******* Constructeur de thread client *******/
    /*********************************************/	

    public ClientThread(Serveur serv,Socket s) throws IOException {
    	// Constructeur
    	this.socket = s;
    	this.Serveur=serv;
        sOutput = this.socket.getOutputStream();
        sInput  = this.socket.getInputStream();
        ADRESSE1 = InetAddress.getLocalHost();
        ADRESSE2 = InetAddress.getLocalHost();
    }
    
    // Fonction d'accès discutable
    public PrintStream getOutput()
    {
    	return new PrintStream(this.sOutput);
    }
    
    /*********************************************/
    /*************** CONFIGURATION ***************/
    /*********************************************/    
    
    public void setNbServeurs(int nbserveurs)
    {
    	this.nbServeurs = nbserveurs;
    }
    
    public int getNbServeurs()
    {
    	return this.nbServeurs;
    }
    
    public void setAdresse1(InetAddress address)
    {
    	ADRESSE1 = address;
    }
    
    public void setAdresse2(InetAddress address)
    {
    	ADRESSE2 = address;
    }
    
    public void setPort1(int port)
    {
    	PORT1=port;
    }
    public void setPort2(int port)
    {
    	PORT2=port;
    }
    
    
    
    
    /*********************************************/
    /************ Fonction de calcul *************/
    /*********************************************/

    public int Calcul(int n) throws Exception {
    	int resultat;
    	// Condition d'arrêt
    	if (n <= 2)
    	{
    		resultat = 1;
    	}
    	// Teste si le résultat est dans le cache
    	else if (this.Serveur.estDansCache(this.n))
		{
			System.out.println("[+ ] LE RESULTAT EST DANS LE CACHE, On l'calcul pas !");
			resultat=this.Serveur.resultatCache(n);
		}
    	// Calcul...
    	else 
    	{
    		FakeClient fake1;
    		FakeClient fake2;
    		if (this.getNbServeurs() == 2)
    		{
    			fake1= new FakeClient(ADRESSE1,PORT1,n-1);
    			fake2= new FakeClient(ADRESSE2,PORT2,n-2);
    		}
    		else
    		{
    			fake1= new FakeClient(ADRESSE1,PORT1,n-1);
    			fake2= new FakeClient(ADRESSE1,PORT1,n-2);
        	
    		}
    		// Lancement des faux clients
    		fake1.start();
    		fake2.start();
    		// On attends qu'ils aient fini leur boulot.
    		fake1.join();
    		fake2.join();
    		
    		
    		
    		// On effectue la récursivité
        	resultat = fake1.getResultat() + fake2.getResultat();
    	}
    	return resultat;
    }
    

    /*********************************************/
    /************ Renvoie du résultat ************/
    /*********************************************/
    
    private void sendResultat()
    {
    	// Création du chemin de Graal vers le client
    	PrintStream output = new PrintStream(sOutput);
    	// Envoie d'une petite variable de type résultat
		output.println(this.res);
    }
    
    
    /*********************************************/
    /************ Lancement du thread ************/
    /*********************************************/
    
    
    public void run() {
    		// Récupération de l'entier.
	   		Scanner sc = new Scanner(sInput);
	    	
	    	if (sc.hasNext())
	    	{
	    		this.n = sc.nextInt();
	    		
	    		try {
	    			// Calcul de fibonacci au rang n
					this.res=Calcul(this.n);
					// Envoie du résultat vraiment bien sympathique
					sendResultat();
					
					// Stockage du résultat dans le cache bien entendu
					this.Serveur.stockerDansCache(this.n, res);
					
				} catch (Exception e) {
					System.out.println("Erreur lors du calcul de fibonacci.");
				}
	 
	    		// Fermeture du socket
	    		sc.close();
	    	}
    		
    	}//Run

    	
    }//ClientThread