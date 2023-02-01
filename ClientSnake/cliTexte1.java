
import java.net.*;
import java.io.*;

public class cliTexte1 {
    public static void main(String[] argu) {
        Socket so;
        DataInputStream entree;
        PrintWriter sortie;
        String s; // le serveur
        int p; // le port de connexion
        String ch; // la chaîne envoyée
        int l; // et sa longueur reçue
        if (argu.length == 3) { // on récupère les paramètres
            s = argu[0];
            p = Integer.parseInt(argu[1]);
            ch = argu[2];
            try {// on connecte un socket
                so = new Socket(s, p);
                sortie = new PrintWriter(so.getOutputStream(), true);
                entree = new DataInputStream(so.getInputStream());
                sortie.println(ch); // on écrit la chaîne et le newline dans le canal de sortie
                l = entree.readInt(); // on lit l’entier qui arrive
                System.out.println("D’après le serveur la longueur de " + ch + " est " + l);
                so.close(); // on ferme la connexion
            } catch (UnknownHostException e) {
                System.out.println(e);
            } catch (IOException e) {
                System.out.println("Aucun serveur n’est rattaché au port ");
            }
        } else {
            System.out.println("syntaxe d’appel java cliTexte serveur port chaine_de_caractères\n");
        }
    }
}