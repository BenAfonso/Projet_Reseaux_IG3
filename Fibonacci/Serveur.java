import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * --------------------------- Documentation ------------------------------
 * Classe: Serveur (Fibonacci)
 * Rôle: Calculer les fonctions de Fibonacci des requêtes clients entrantes
 * Utilisation:
 * -> java Serveur -h  pour obtenir l'aide
 * Fonctionnalités:
 * -> Le serveur dispose d'un cache de calcul pour éviter de refaire les calculs
 * @author benjaminafonso
 * 
 * -------------------------------------------------------------------------
 */
public class Serveur extends Thread {
	// Port 
	private int port;
	// Numéro de client
	private int num;
	// Liste des clients
	private ArrayList <ClientThread> Clients = new ArrayList<ClientThread>();
	private Hashtable<Integer, Integer> Cache = new Hashtable<Integer, Integer>();
	private String nom;
	public static int NBSERVEURS;
	public static InetAddress ADRESSE1;
	public static InetAddress ADRESSE2;
	public static int PORT1;
	public static int PORT2;
 	
    /***********************************************/
	/********** Constructeur de serveur ************/
	/***********************************************/
	
	Serveur(int port, String nom)
	{
		this.num = 0;
		this.port = port;
		// Faut bien lui donner un nom à notre pauvre serveur
		this.nom = nom;
	}
	
	public Hashtable<Integer, Integer> getCache()
	{
		return this.Cache;
	}
	
	
    public void stockerDansCache(int N,int res)
    {
    	Cache.put(N, res);
    }
    
    public boolean estDansCache(int n)
    {
		return Cache.containsKey(n);
    	
    }
    
    public int resultatCache(int n)
    {
    	return Cache.get(n);
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
	public void run()
		{
			clearConsole();
			// Affichage du message vraiment sympathique
			System.out.println("========== Lancement du serveur de calcul de  "+this.nom+" ==========");
			System.out.println("~~ Port: "+this.port);
			System.out.println("============================================================\n");
			// Création du socket serveur
			ServerSocket sServer=null;
			try {
				sServer = new ServerSocket(this.port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while (true) {
			 // Acceptation de clients
				Socket s;
				try {
					s = sServer.accept();
					System.out.println("PORT: "+this.port+"\n[Client] Nouveau client connecté: "+num);
					
					
					ClientThread c = new ClientThread(this,s);
					c.setNbServeurs(NBSERVEURS);
					c.setAdresse1(ADRESSE1);
					c.setPort1(PORT1);
					if ((NBSERVEURS) == 2)
					{
						c.setAdresse2(ADRESSE2);
						c.setPort2(PORT2);
					}
					c.start();
					
					
					Clients.add(c);
					
					
					num++;
					
			
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	
    /**********************************************/
	/**** Programme principal et configuration ****/
	/**********************************************/
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		// Un serveur
		try
		{
		if (args[0].equals("-h") || args[0].equals("--help"))
		{
			clearConsole();
			System.out.println("============= AIDE: Serveur de calcul fibonacci ===========");
			System.out.println("---> java Serveur NBSERVEURS PORT1 (ADRESSESERV2 PORT2)");
			System.out.println("NBServeurs: 1/2");
			System.out.println("Le serveur 1 est en local");
			System.out.println("PORT1: Port numéro 1 (ex: 50000)");
			System.out.println("========================= FACULTATIF ========================");
			System.out.println("ADRESSESERV2: (ex: localhost, xxx.xxx.xx.xx)");
			System.out.println("PORT2: Si deux serveurs, un deuxième port");

		}
		else if (Integer.parseInt(args[0]) == 1)
		{
			Serveur serveur = new Serveur(Integer.parseInt(args[1]),"n-1");
			serveur.start();
			ADRESSE1 = InetAddress.getLocalHost();
			PORT1 = Integer.parseInt(args[1]);
			NBSERVEURS=1;
		}
		else if (Integer.parseInt(args[0]) == 2)
		{
			// Deux serveurs
			Serveur serveur = new Serveur(Integer.parseInt(args[1]),"n-1");
			Serveur serveur2 = new Serveur(Integer.parseInt(args[3]),"n-2");
			ADRESSE1 = InetAddress.getLocalHost();
			ADRESSE2 = InetAddress.getByName(args[2]);
			PORT1=Integer.parseInt(args[1]);
			PORT2=Integer.parseInt(args[3]);
			serveur.start();
			serveur2.start();
			NBSERVEURS=2;
		}
		else
		{
			System.out.println("Utilisation: java Serveur -h");
		}
		}catch(Exception e)
		{
			System.out.println("Utilisation: java Serveur -h");
		}
		
		

		
		
	}
	

}


