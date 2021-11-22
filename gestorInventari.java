package Projecte1;

import java.sql.*;
import java.util.Scanner;

public class gestorInventari {

    static Connection connexioBD = null;

    String[] proveedors = new String[60];
    int[] stock = new int[60];

    public static boolean exit = false;
    public static boolean exit2 = false;
    public static Scanner keyboard = new Scanner(System.in);

    public static void main(String[] args) {

        try {
            connexioBD();
            System.out.println("Connexió efectuada amb exit\n");
            menuBD();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void menuBD() throws SQLException {

        do {
            System.out.println("GESTOR D'INVENTARI");
            System.out.println("1. Gestió de productes");
            System.out.println("2. Actualitzar stock");
            System.out.println("3. Preparar comanda");
            System.out.println("4. Analitzar les comandes");
            System.out.println("5. Sortir");
            System.out.println("\nTria una opcio");

            int opcio = keyboard.nextInt();

            switch (opcio) {
            case 1:
                do {
                    System.out.println("GESTIO DE PRODUCTES");
                    System.out.println("1. LLISTA TOTS ELS PRODUCTES");
                    System.out.println("2. ALTA PRODUCTE");
                    System.out.println("3. MODIFICA PRODUCTE");
                    System.out.println("4. ESBORRA PRODUCTE");
                    System.out.println("5. Sortir");
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
                    break;
                } while (!exit2);
            case 2:
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

    public static void connexioBD() throws SQLException {
        String servidor = "jdbc:mysql://192.168.16.150:3306/";
        String bbdd = "stock";
        String user = "dam";
        String password = "123123123d";
        connexioBD = DriverManager.getConnection(servidor + bbdd, user, password);
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

            System.out.println("Quin nom vols posar?");
            String nom_prod = keyboard.nextLine();
            String mod = "UPDATE PRODUCTES SET NOM = ? WHERE ID_PRODCUCTE = ?";
            PreparedStatement p1 = connexioBD.prepareStatement(mod);

            p1.setString(1, nom_prod);
            p1.setInt(2, id_prod);
            p1.executeUpdate();

        case 2:

            System.out.println("Quin preu vols posar?");
            int preu_prod = keyboard.nextInt();
            String mod2 = "UPDATE PRODUCTES SET PREU = ? WHERE ID_PRODCUCTE = ?";
            PreparedStatement p2 = connexioBD.prepareStatement(mod2);

            p2.setInt(1, preu_prod);
            p2.setInt(2, id_prod);
            p2.executeUpdate();
            break;

        case 3:

            System.out.println("Quin material vols posar?");
            String mat_prod = keyboard.nextLine();
            String mod3 = "UPDATE PRODUCTES SET MATERIAL = ? WHERE ID_PRODCUCTE = ?";
            PreparedStatement p3 = connexioBD.prepareStatement(mod3);

            p3.setString(1, mat_prod);
            p3.setInt(2, id_prod);
            p3.executeUpdate();
            break;

        case 4:

            System.out.println("Quin preu vols posar?");
            int stock_prod = keyboard.nextInt();
            String mod4 = "UPDATE PRODUCTES SET STOCK = ? WHERE ID_PRODCUCTE = ?";
            PreparedStatement p4 = connexioBD.prepareStatement(mod4);

            p4.setInt(1, stock_prod);
            p4.setInt(2, id_prod);
            p4.executeUpdate();
            break;

        case 5:
            exit = true;
        
        default:
            System.out.println("No és una opció vàl·lida");
        } while (!exit);
    }

    static void baixaProducte() {

    }
}
