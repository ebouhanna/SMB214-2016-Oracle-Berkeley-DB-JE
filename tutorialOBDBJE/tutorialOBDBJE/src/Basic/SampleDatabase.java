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

public class SampleDatabase
{
   
    // Environment
    private Environment env;
    
    // Defining The class catalog name
    private static final String CLASS_CATALOG = "java_class_catalog";
    private StoredClassCatalog javaCatalog;
    
    // The three Databases Names 
    private static final String SUPPLIER_STORE = "supplier_store";
    private static final String PART_STORE = "part_store";
    private static final String SHIPMENT_STORE = "shipment_store";
    
    // Databases 
    private Database supplierDb;
    private Database partDb;
    private Database shipmentDb;
    
    
    public SampleDatabase(String homeDirectory)
        throws DatabaseException, FileNotFoundException
    {
        //Defining the environment
        System.out.println("Opening environment in: " + homeDirectory);

        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setTransactional(true);
        envConfig.setAllowCreate(true);

        env = new Environment(new File(homeDirectory), envConfig);
        
        //Defining the configuration object which will be used for all DBs
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTransactional(true);
        dbConfig.setAllowCreate(true);
        
        //Creating & Defining Database Catalog
        Database catalogDb = env.openDatabase(null, CLASS_CATALOG,dbConfig);
        javaCatalog = new StoredClassCatalog(catalogDb);
        
        //Creating and opening three Databses
        partDb = env.openDatabase(null, PART_STORE, dbConfig);
        supplierDb = env.openDatabase(null, SUPPLIER_STORE, dbConfig);
        shipmentDb = env.openDatabase(null, SHIPMENT_STORE, dbConfig);        
           
    }

//Getter methods:    

public final Environment getEnvironment()
    {
        return env;
    }

public final StoredClassCatalog getClassCatalog() 
    {
        return javaCatalog;
    } 

public final Database getPartDatabase()
    {
        return partDb;
    }

public final Database getSupplierDatabase()
    {
        return supplierDb;
    }

public final Database getShipmentDatabase()
    {
        return shipmentDb;
    }
    
public void close() throws DatabaseException
    {
        partDb.close();
        supplierDb.close();
        shipmentDb.close();
        javaCatalog.close();
        env.close();
    }


} 
