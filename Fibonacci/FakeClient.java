import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


/**
 * --------------------------- Documentation ------------------------------
 * @author benjaminafonso
 * Classe: FakeClient
 * Rôle: Création d'un faux client pour envoyer des requêtes récursives à un serveur.
 * Utilisation: FakeClient X = new FakeClient(PORT, N)
 * ATTENTION: Les Faux clients ne se connectent qu'en local au port spécifié !
 * -------------------------------------------------------------------------
 */
public class FakeClient extends Thread
		{
			private int n;
	        private Socket socket;
			private int port;
			private InetAddress address;
			private int resultat;
			
	        FakeClient(InetAddress address,int port,int n) throws IOException {
		        //Constructeur
	        	this.port=port;
	        	this.address=address;
	        	this.socket = new Socket(this.address,this.port);
	        	this.n= n;   	
	        }//Constructeur

	        public Socket getSocket()
	        {
	        	return this.socket;
	        }
	        
	        public int getResultat()
	        {
	        	return resultat;
	        }
	        public void Listen() {
	            //Fonction gérant la reception du résultat
	                try {
	                    //Ecriture du n à calculer
	                    PrintStream out = new PrintStream(this.socket.getOutputStream());
	                    out.println(this.n);
	                    out.flush(); // Cleaaaan
	                    Scanner sc = new Scanner(socket.getInputStream());
	                    if (sc.hasNext())
	                        //Reception -> Affichage du résultat
	                        resultat = sc.nextInt();
	                    sc.close();
	                    this.socket.close();
	                    //On ferme le scanner et le socket
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	                
	            }
	        
	        public void run() {
				// Fonction
	        	try {
					PrintStream output = new PrintStream(this.socket.getOutputStream());
					output.println(this.n);
				} catch (IOException e) {
					System.out.println("[ERREUR] Erreur lors de l'envoie ");
					e.printStackTrace();
				}
	        	
	        	Listen();
				
	        	
	        }//Run
		}