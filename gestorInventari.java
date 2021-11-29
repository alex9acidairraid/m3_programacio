package Projecte1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;
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

    public static void connexioBD() throws SQLException {
        String servidor = "jdbc:mysql://192.168.16.150:3306/";
        String bbdd = "stock";
        String user = "dam";
        String password = "123123123d";
        connexioBD = DriverManager.getConnection(servidor + bbdd, user, password);
    }

    public static void menuBD() throws SQLException, FileNotFoundException, IOException {

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
                    
                    break;
                case 4:

                    break;
                case 5:
                    exit = true;
                    break;
                default:
                    System.out.println("La opció sel·leccionada no és vàl·lida");
            }

            System.out.println("\nOpció: " + opcio);

        } while (!exit);
        // desconexioBD();
    }

    static void llistarProductes() throws SQLException {

        String consulta = "SELECT * FROM PRODUCTES ORDER BY ID_PRODUCTE";

        PreparedStatement ps = connexioBD.prepareStatement(consulta);
        ResultSet rs = ps.executeQuery();

        while (rs.next())
            System.out.println("\n\nID_PRODUCTE: " + rs.getString(1) + " \nNOM: " + rs.getString(2) + " \nPREU: "
                    + rs.getString(3) + " \nMATERIAL: " + rs.getString(4) + " \nSTOCK: " + rs.getString(5));

    }

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
                p1.executeUpdate();

                if (p1.executeUpdate() != 1) {
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
                p2.executeUpdate();

                if (p2.executeUpdate() != 1) {
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
                p3.executeUpdate();

                if (p3.executeUpdate() != 1) {
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
                p4.executeUpdate();

                if (p4.executeUpdate() != 1) {
                    System.out.println("Hi ha hagut un error inesperat");
                } else {
                    System.out.println("El canvi s'ha realitzat correctament");
                }

                exit = true;
                break;

            case 5:
                exit = true;

            default:
                System.out.println("No és una opció vàl·lida");
        }
        while (!exit)
            ;
    }

    static void baixaProducte() throws SQLException {
        System.out.println("Indica la ID del producte que vols eliminar");
        int del_prod = keyboard.nextInt();

        String del = "DELETE FROM PRODUCTES WHERE ID_PRODUCTE = ?";
        PreparedStatement delete = connexioBD.prepareStatement(del);

        delete.setInt(1, del_prod);
        delete.executeUpdate();
    }

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

            //Una manera de tancar-los sense fer servir el try catch resource.
            //buffer.close();
            //reader.close();
        
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    static void moureFitxersBD(File files) throws SQLException, IOException {

        FileSystem sistemaFicheros = FileSystems.getDefault();
        Path origen = sistemaFicheros.getPath(PATHPENDENTS + files.getName());
        Path desti = sistemaFicheros.getPath(PATHPROCESSADES + files.getName());

        Files.move(origen, desti, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("S'ha mogut a processats el fitxer: " + files.getName());
    }
}
