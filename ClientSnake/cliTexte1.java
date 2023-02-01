
import java.net.*;
import java.io.*;

public class cliTexte1 {
    public static void main(String[] argu) {
        Socket so;
        BufferedReader entree;
        PrintWriter sortie;
        String s; // le serveur
        int p; // le port de connexion
        String ch; // la chaîne envoyée
        String l; // et sa longueur reçue
        if (argu.length == 3) { // on récupère les paramètres
            s = argu[0];
            p = Integer.parseInt(argu[1]);
            ch = argu[2];
            try {// on connecte un socket
                so = new Socket(s, p);
                sortie = new PrintWriter(so.getOutputStream(), true);
                sortie.println(ch); // on écrit la chaîne et le newline dans le canal de sortie
                entree = new BufferedReader(new InputStreamReader(so.getInputStream()));

                l = entree.readLine(); 

                System.out.println(l);
                                   
            } catch (UnknownHostException e) {
                System.out.println(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("syntaxe d’appel java cliTexte serveur port chaine_de_caractères\n");
        }
    }
}