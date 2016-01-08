/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Basic;

/**
 *
 * @author ebouhanna
 */

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import java.io.File;
import java.io.FileNotFoundException;

public class Bd
{
   
    // Environment
    private Environment env;
    
    // Definir la class catalog 
    private static final String CLASS_CATALOG = "java_class_catalog";
    private StoredClassCatalog javaCatalog;
    
    // Definir les noms des trois BD
    private static final String DEPOT_FOURNISSEUR = "depot_fournisseur";
    private static final String DEPOT_PRODUIT = "depot_produit";
    private static final String DEPOT_VENTES = "depot_ventes";
    
    // Databases 
    private Database fournisseurBd;
    private Database produitBd;
    private Database ventesBd;
    
    
    public Bd(String homeDirectory)
        throws DatabaseException, FileNotFoundException
    {
        //Defining the environment
        System.out.println("Ouvrir l'environment dans: " + homeDirectory);

        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setTransactional(true);
        envConfig.setAllowCreate(true);

        env = new Environment(new File(homeDirectory), envConfig);
        
        //Defining tl'objet de configuration qui sera utilisé pout tous les BDs
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTransactional(true);
        dbConfig.setAllowCreate(true);
        
        //Créer et définir les catalogs des BDs 
        Database catalogDb = env.openDatabase(null, CLASS_CATALOG,dbConfig);
        javaCatalog = new StoredClassCatalog(catalogDb);
        
        //Créer et Ouvrir les Bds
        produitBd = env.openDatabase(null, DEPOT_PRODUIT, dbConfig);
        fournisseurBd = env.openDatabase(null, DEPOT_FOURNISSEUR, dbConfig);
        ventesBd = env.openDatabase(null, DEPOT_VENTES, dbConfig);        
           
    }

//Méthodes Getter:    

public final Environment getEnvironment()
    {
        return env;
    }

public final StoredClassCatalog getClassCatalog() 
    {
        return javaCatalog;
    } 

public final Database getBdProduit()
    {
        return produitBd;
    }

public final Database getBdFournisseur()
    {
        return fournisseurBd;
    }

public final Database getBdVentes()
    {
        return ventesBd;
    }
    
public void close() throws DatabaseException
    {
        produitBd.close();
        fournisseurBd.close();
        ventesBd.close();
        javaCatalog.close();
        env.close();
    }


} 
