import java.net.*;
import java.io.*;
public class servTexte1 {
public static void main(String[] argu) {
int p; // le port d’écoute
ServerSocket ecoute;
Socket so;
BufferedReader entree;
DataOutputStream sortie;
String ch; // la chaîne reçue
if (argu.length == 1) {
try {
    p=Integer.parseInt(argu[0]); // on récupère le port
    ecoute = new ServerSocket(p); // on crée le serveur
    System.out.println("serveur mis en place ");
    while (true) {// le serveur va attendre qu’une connexion arrive
        so = ecoute.accept();
        entree = new BufferedReader(new InputStreamReader(so.getInputStream()));
        sortie = new DataOutputStream (so.getOutputStream());
        ch = entree.readLine(); // on lit ce qui arrive
        System.out.println("on a reçu : |"+ch+"|");
        sortie.writeInt(ch.length()); // on renvoie la longueur de la chaîne
        so.close();
        System.out.println("on a envoyé : "+ch.length()+" et on a fermé la connexion");
    }
} catch (IOException e) { System.out.println("problème\n"+e); }
} else { System.out.println("syntaxe d’appel java servTexte port\n"); } } }