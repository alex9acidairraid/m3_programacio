package Projecte1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class gestorInventari {

    // Aqui declarem la connexió.
    static Connection connexioBD;

    // Fem booleans per a poder sortir del switch.
    public static boolean exit = false;
    public static boolean exit2 = false;

    public static String PATHPENDENTS = "files/ENTRADES PENDENTS/";
    public static String PATHPROCESSADES = "files/ENTRADES PROCESSADES/";
    public static String COMANDES = "files/COMANDES/";

    public static String[] proveedors = new String[100];
    public static int[] productes = new int[100];

    // Aqui declarem el Scanner per poder introduïr les dades.
    public static Scanner keyboard = new Scanner(System.in);

    public static void main(String[] args) {

        // Dintre del main hem ficat el try/catch per poder redirigir les excepcions,
        // fer la connexió amb la nostra base de dades i cridar directament el menú que
        // hem fet.
        try {
            connexioBD();
            System.out.println("Connexió efectuada amb exit\n");
            menuBD();
        } catch (SQLException ex) {
            // El print stack trace ens ajuda a trobar un error en el nostre codi.
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Connexió a la nostra base de dades via msql
    public static void connexioBD() throws SQLException {
        String servidor = "jdbc:mysql://192.168.16.150:3306/";
        String bbdd = "stock";
        String user = "dam";
        String password = "123123123d";
        connexioBD = DriverManager.getConnection(servidor + bbdd, user, password);
    }

    public static void menuBD() throws SQLException, FileNotFoundException, IOException {

        // Menu principal que contè tots els nostres metodes separats per un switch,
        // tenim un nextLine que ens permet fer servir la informació que introdueix el
        // usuari.
        do {
            System.out.println("GESTOR D'INVENTARI");
            System.out.println("1. Gestió de productes");
            System.out.println("2. Actualitzar stock");
            System.out.println("3. Preparar comanda");
            System.out.println("4. Analitzar les comandes");
            System.out.println("5. Sortir");
            System.out.println("\nTria una opcio");

            // Declarem que el int opció es pugui introduir per consola mitjançant la funció
            // nextInt.
            int opcio = keyboard.nextInt();

            switch (opcio) {
                case 1:
                    do {
                        System.out.println("GESTIO DE PRODUCTES");
                        System.out.println("1. LLISTA TOTS ELS PRODUCTES");
                        System.out.println("2. ALTA PRODUCTE");
                        System.out.println("3. MODIFICA PRODUCTE");
                        System.out.println("4. ESBORRA PRODUCTE");
                        System.out.println("5. Enrere");
                        System.out.println("\nTria una opcio");

                        int opcio2 = keyboard.nextInt();

                        switch (opcio2) {
                            case 1:
                                // consultar tots els productes
                                llistarProductes();
                                break;
                            case 2:
                                altaProductes();
                                break;
                            case 3:
                                modificacioProductes();
                                break;
                            case 4:
                                baixaProducte();
                                break;
                            case 5:
                                exit2 = true;
                                break;
                        }
                    } while (!exit2);
                    break;
                case 2:
                    actualitzacioStock();
                    break;
                case 3:
                    prepararComanda();
                    break;
                case 4:
                    analitzarComanda();
                    productesMinim(productes, proveedors);
                    productesMaxim(productes, proveedors);
                    productesMitjana(productes);
                    break;
                case 5:
                    exit = true;
                    break;
                default:
                    System.out.println("La opció sel·leccionada no és vàl·lida");
            }

            System.out.println("\nOpció: " + opcio + "\n");

        } while (!exit);
        // desconexioBD();
    }

    // Un mètode soimple per llistar tots els nostres productes.
    static void llistarProductes() throws SQLException {

        String consulta = "SELECT * FROM PRODUCTES ORDER BY ID_PRODUCTE";

        PreparedStatement ps = connexioBD.prepareStatement(consulta);
        ResultSet rs = ps.executeQuery();

        while (rs.next())
            System.out.println("\n\nID_PRODUCTE: " + rs.getString(1) + " \nNOM: " + rs.getString(2) + " \nPREU: "
                    + rs.getString(3) + " \nMATERIAL: " + rs.getString(4) + " \nSTOCK: " + rs.getString(5));

    }

    // Un mètode per introduïr i donar d'alta un producte.
    static void altaProductes() throws SQLException {

        System.out.println("\nALTA PRODUCTE");

        keyboard.nextLine();

        System.out.println("Introdueix el nom del producte:");
        String nom = keyboard.nextLine();

        System.out.println("Introdueix el preu:");
        int preu = keyboard.nextInt();
        keyboard.nextLine();

        System.out.println("Introdueix el material");
        String material = keyboard.nextLine();

        System.out.println("Introdueix el stock");
        int stock = keyboard.nextInt();

        String insert = "INSERT INTO PRODUCTES (NOM, PREU, MATERIAL, STOCK) VALUES (?,?,?,?)";
        PreparedStatement sentence = connexioBD.prepareStatement(insert);

        sentence.setString(1, nom);
        sentence.setInt(2, preu);
        sentence.setString(3, material);
        sentence.setInt(4, stock);

        if (sentence.executeUpdate() != 0) {
            System.out.println("Producte donat d'alta: " + nom + " " + preu + " " + material + " " + stock);
        } else {
            System.out.println("No s'ha donat d'alta cap producte");
        }
    }

    // Modifiquem la informació de un producte, elegirem què volem modificar.
    static void modificacioProductes() throws SQLException {

        String consulta = "SELECT * FROM PRODUCTES ORDER BY ID_PRODUCTE";

        PreparedStatement ps = connexioBD.prepareStatement(consulta);
        ResultSet rs = ps.executeQuery();

        while (rs.next())
            System.out.println("\n\nID_PRODUCTE: " + rs.getString(1) + " \nNOM: " + rs.getString(2) + " \nPREU: "
                    + rs.getString(3) + " \nMATERIAL: " + rs.getString(4) + " \nSTOCK: " + rs.getString(5));

        System.out.println();

        System.out.println("Fica el ID del producte QUE VOLS MODIFICAR");
        int id_prod = keyboard.nextInt();

        System.out.println("1. Modifica \"NOM\" del producte");
        System.out.println("2. Modifica \"PREU\" del producte");
        System.out.println("3. Modifica \"MATERIAL\" del producte");
        System.out.println("4. Modifica \"STOCK\" del producte");
        System.out.println("5. Sortir");
        System.out.println("\nTria una opcio");

        int eleccio = keyboard.nextInt();

        switch (eleccio) {

            case 1:
                keyboard.nextLine();
                System.out.println("Quin nom vols posar?");
                String nom_prod = keyboard.nextLine();
                String mod = "UPDATE PRODUCTES SET NOM = ? WHERE ID_PRODUCTE = ?";
                PreparedStatement p1 = connexioBD.prepareStatement(mod);

                p1.setString(1, nom_prod);
                p1.setInt(2, id_prod);

                if (p1.executeUpdate() == 0) {
                    System.out.println("Hi ha hagut un error inesperat");
                } else {
                    System.out.println("El canvi s'ha realitzat correctament");
                }

                exit = true;
                break;

            case 2:

                System.out.println("Quin preu vols posar?");
                int preu_prod = keyboard.nextInt();
                String mod2 = "UPDATE PRODUCTES SET PREU = ? WHERE ID_PRODUCTE = ?";
                PreparedStatement p2 = connexioBD.prepareStatement(mod2);

                p2.setInt(1, preu_prod);
                p2.setInt(2, id_prod);

                if (p2.executeUpdate() == 0) {
                    System.out.println("Hi ha hagut un error inesperat");
                } else {
                    System.out.println("El canvi s'ha realitzat correctament");
                }

                exit = true;
                break;

            case 3:

                keyboard.nextLine();
                System.out.println("Quin material vols posar?");
                String mat_prod = keyboard.nextLine();
                String mod3 = "UPDATE PRODUCTES SET MATERIAL = ? WHERE ID_PRODUCTE = ?";
                PreparedStatement p3 = connexioBD.prepareStatement(mod3);

                p3.setString(1, mat_prod);
                p3.setInt(2, id_prod);

                if (p3.executeUpdate() == 0) {
                    System.out.println("Hi ha hagut un error inesperat");
                } else {
                    System.out.println("El canvi s'ha realitzat correctament");
                }

                exit = true;
                break;

            case 4:

                System.out.println("Quin stock vols posar?");
                int stock_prod = keyboard.nextInt();
                String mod4 = "UPDATE PRODUCTES SET STOCK = ? WHERE ID_PRODUCTE = ?";
                PreparedStatement p4 = connexioBD.prepareStatement(mod4);

                p4.setInt(1, stock_prod);
                p4.setInt(2, id_prod);

                if (p4.executeUpdate() == 0) {
                    System.out.println("Hi ha hagut un error inesperat");
                } else {
                    System.out.println("El canvi s'ha realitzat correctament");
                }

                exit = true;
                break;

            case 5:

                exit = true;
                break;

            default:
                System.out.println("No és una opció vàl·lida");
        }
        while (!exit)
            ;
    }

    // Eliminar un producte, fem servir un stmt.execute("SET FOREIGN_KEY_CHECKS=0");
    // per poder borrar totes les relacions de les taules en la nostra base de
    // dades.
    static void baixaProducte() throws SQLException {

        System.out.println("Indica la ID del producte que vols eliminar");
        int del_prod = keyboard.nextInt();

        Statement stmt = connexioBD.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS=0");

        String del = "DELETE FROM PRODUCTES WHERE ID_PRODUCTE = ?";
        PreparedStatement delete = connexioBD.prepareStatement(del);

        delete.setInt(1, del_prod);
        delete.executeUpdate();

        if (delete.executeUpdate() == 0) {
            System.out.println("Hi ha hagut un error inesperat...");
        } else {
            System.out.println("S'ha esborrat el producte correctament.");
        }
    }

    // Crea 2 carpetes i truca al mètode de actualització i el de moure fitxers i
    // recorre cada fitxer i llegeix els fitxers.
    static void actualitzacioStock() throws SQLException, IOException {

        System.out.println("ACTUALITZACIÓ D'ESTOC");

        File file = new File(PATHPENDENTS);
        file.mkdirs();

        if (file.isDirectory()) {
            System.out.println();

            File[] fitxers = file.listFiles();

            for (int i = 0; i < fitxers.length; i++) {
                System.out.println(fitxers[i].getName());
                actualitzarFitxerBD(fitxers[i]);
                moureFitxersBD(fitxers[i]);
            }

        } else {
            System.out.println("No es un directori");
        }

        File file2 = new File(PATHPROCESSADES);
        file2.mkdirs();

    }

    // Separa la informació del .txt i actualitza el stock.
    static void actualitzarFitxerBD(File file) throws IOException, SQLException {

        // llegeix caracter a caracter
        FileReader reader = new FileReader(file);

        try (
                // llegeix linea a linea molt més eficientment
                BufferedReader buffer = new BufferedReader(reader)) {
            String linea;

            while ((linea = buffer.readLine()) != null) {

                System.out.println(linea);
                System.out.println();

                int posSeparador = linea.indexOf(":");
                System.out.println("Index of : " + posSeparador);

                int id_prod = Integer.parseInt(linea.substring(0, posSeparador));
                System.out.println("La ID del producte és: " + id_prod);
                System.out.println();

                int stock_producte = Integer.parseInt(linea.substring(posSeparador + 1));
                System.out.println("El numero de productes aportat és: " + stock_producte);
                System.out.println();

                // String select_num_prod = "SELECT STOCK FROM PRODUCTES WHERE ID_PRODUCTE = ?";
                // PreparedStatement agafa_stock = connexioBD.prepareStatement(select_num_prod);
                // agafa_stock.setInt(1, numprod);
                // ResultSet rs = agafa_stock.executeQuery();

                // if (rs.next()){

                // int stk = rs.getInt("STOCK");
                // stk += stock_producte;
                // } else {
                // System.out.println("No s'ha trobat el producte");
                // }

                String update_prod = "UPDATE PRODUCTES SET STOCK = STOCK + ? WHERE ID_PRODUCTE = ?";
                PreparedStatement ps_update = connexioBD.prepareStatement(update_prod);

                ps_update.setInt(1, stock_producte);
                ps_update.setInt(2, id_prod);
                ps_update.executeUpdate();
            }

            // Una manera de tancar-los sense fer servir el try catch resource.
            // buffer.close();
            // reader.close();

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    // Mou directament el fitxer, de la carpeta PATHPENDENTS i el mou a la carpeta
    // PATHPROCESSADES.
    static void moureFitxersBD(File files) throws SQLException, IOException {

        FileSystem sistemaFicheros = FileSystems.getDefault();
        Path origen = sistemaFicheros.getPath(PATHPENDENTS + files.getName());
        Path desti = sistemaFicheros.getPath(PATHPROCESSADES + files.getName());
        Files.move(origen, desti, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("S'ha mogut a processats el fitxer: " + files.getName());
    }

    // Crea fitxers a partir de la consulta, en la qual, el stock dels productes que
    // crea es de menys de 20. Tambè separa els proveedors i crea un fitxer per cada
    // proveedor i a dintre de cada comanda de proveedor llista els productes als
    // quals els falte més stock.
    static void prepararComanda() throws SQLException, IOException {

        String cons = "SELECT P.ID_PRODUCTE, P.NOM, P.STOCK, PROV.NOM FROM PRODUCTES P,SUBMINISTRA S, PROVEEDORS PROV WHERE PROV.CODI_PROVEEDOR = S.CODI_PROVEEDOR AND S.ID_PRODUCTE = P.ID_PRODUCTE AND STOCK < 20 ORDER BY PROV.CODI_PROVEEDOR;";

        int countprov = 0;
        int countprod = 0;

        PreparedStatement comanda = connexioBD.prepareStatement(cons);
        ResultSet rs = comanda.executeQuery();

        String prov = null;
        PrintWriter wrtr = null;

        if (rs.next()) {
            // primera fila del result set
            prov = rs.getString(4);
            proveedors[countprov] = rs.getString(4);
            wrtr = capcaleraComandes(prov);

            do {
                if (!prov.equals(rs.getString(4))) {

                    // Fem que el array agaifi el proveedor de la base de dades i li afegim un
                    // counteig al contador de proveedors, el qual ha iniciat hem iniciat a 0.
                    productes[countprov] = countprod;
                    countprod = 0;
                    countprov++;
                    proveedors[countprov] = rs.getString(4);

                    prov = rs.getString(4);
                    wrtr.close();

                    wrtr = capcaleraComandes(prov);

                    System.out.println("\nID PRODUCTE: " + rs.getString(1) + " " + "\nNOM PRODUCTE: " + rs.getString(2) + " " + "\nSTOCK PRODUCTE: " + rs.getString(3) + " " + "\nNOM PROVEEDOR: " + rs.getString(4));
                }

                countprod++;

                wrtr.println("Proveedor: " + rs.getString("PROV.NOM"));
                wrtr.println("Id del producte: " + rs.getString("ID_PRODUCTE"));
                wrtr.println("Stock del producte actual: " + rs.getString("STOCK"));
                wrtr.println(LocalDate.now());
                wrtr.println("***********************************************");

            } while (rs.next());
            productes[countprov] = countprod;
            wrtr.close();

        }
    }

    // Definim un file writer amb la adreça, un bufered writer i print writer per
    // fer servir aquest mètode amb el mètode anterior i aprofitem per escriure una
    // capcelera per cada arxiu creat amb la informació de la nostra empresa.
    static PrintWriter capcaleraComandes(String prov) throws SQLException, IOException {

        FileWriter fw = new FileWriter("files\\COMANDES\\" + prov + "_" + LocalDate.now() + "_" + ".txt");
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter wrtr = new PrintWriter(bw);

        wrtr.println("*********************" + LocalDate.now() + "*********************");
        wrtr.println("Empresa: More STOCK");
        wrtr.println("Versió: 2.65.47.3");
        wrtr.println("_______________________________________________");
        wrtr.println("********************COMANDA********************");
        return wrtr;
    }

    // Analitzem els arrays amb un bucle per poder imprimir en cas de que un
    // proveedor no sigui null.
    static void analitzarComanda() throws SQLException, IOException {

        for (int x = 0; proveedors[x] != null; x++) {
            System.out.println("Proveedor: " + proveedors[x] + " Te encomanats: " + productes[x] + " productes.");
        }
    }
    
    public static void productesMinim(int [] productes, String [] proveedor){

        int minim = productes[0];
        int indxmin = 0;

        for (int i=0; i<productes.length;i++){
            if (productes[i] < minim){
                minim=productes[i];
                indxmin=i;
            }
        }
        
        System.out.println();
        System.out.println("El proveedor amb menys productes sol·licitats és: " + proveedors[indxmin]+ ": "+minim);
    }

    public static void productesMaxim(int [] productes, String [] proveedor){

        int maxim = productes[0];
        int indxmax = 0;
        
        for (int i=0; i<productes.length;i++){
            if (productes[i] > maxim){
                maxim=productes[i];  
                indxmax=i;     
            }
        }

        System.out.println("El proveedor amb més productes sol·licitats és: " + proveedor[indxmax] + ": "+maxim);

    }
        
    public static void productesMitjana(int [] productes){

        int mitja= 0;    
        
        for (int i=0; i<productes.length;i++){
            mitja += productes[i];
        }
        
        System.out.printf("La mitjana de productes demanats és: ", mitja/productes.length);

    } 
}
