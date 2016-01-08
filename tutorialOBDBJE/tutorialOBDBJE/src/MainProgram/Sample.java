/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainProgram;

/**
 *
 * @author ebouhanna
 */

import Basic.Bd;
import Basic.SampleViews;
import Basic.ProduitData;
import Basic.ProduitKey;
import Basic.VentesData;
import Basic.VentesKey;
import Basic.FournisseurData;
import Basic.FournisseurKey;
import Basic.Poids;
import com.sleepycat.je.DatabaseException;
import java.io.FileNotFoundException;
import com.sleepycat.collections.TransactionRunner;
import com.sleepycat.collections.TransactionWorker;
import java.util.Map;
import java.util.Iterator;


public class Sample {
    
    private Bd db;
    private SampleViews views;

    public static void main(String[] args)
    {
        System.out.println("\nRunning sample: " + Sample.class);
        String homeDir = "./tmp";
        for (int i = 0; i < args.length; i += 1)
        {
            String arg = args[i];
            if (args[i].equals("-h") && i < args.length - 1)
            {
                i += 1;
                homeDir = args[i];
            }
            else
            {
                System.err.println("Usage:\n java " + 
                                   Sample.class.getName() +
                                  "\n  [-h <home-directory>]");
                System.exit(2);
            }
        }
    
    Sample sample = null;
        try
        {
            sample = new Sample(homeDir);
            sample.run();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (sample != null)
            {
                try
                {
                    sample.close();
                }
                catch (Exception e)
                {
                    System.err.println("Exception during database close:");
                    e.printStackTrace();
                }
            }
        }
    
    }

    private Sample(String homeDir) throws DatabaseException, FileNotFoundException
    {
        db = new Bd(homeDir);
        views = new SampleViews(db);
    }

    private void close()
        throws DatabaseException
    {
         db.close();
    }

    private void run() throws Exception
    {
        TransactionRunner runner = new TransactionRunner(db.getEnvironment());
        runner.run(new PopulateDatabase());
        runner.run(new PrintDatabase());
    }

    private class PopulateDatabase implements TransactionWorker
    {
        public void doWork()
            throws Exception
        {
            addSuppliers();
            addParts();
            addShipments();
        }
    }

    private class PrintDatabase implements TransactionWorker
    {
        public void doWork()
            throws Exception
        {
            printEntries("Parts",
                          views.getPartEntrySet().iterator());
            printEntries("Suppliers",
                          views.getSupplierEntrySet().iterator());
            printEntries("Shipments",
                          views.getShipmentEntrySet().iterator());
        }
    }

    private void addSuppliers()
    {
        Map suppliers = views.getSupplierMap();
        if (suppliers.isEmpty())
        {
            System.out.println("Adding Suppliers");
            suppliers.put(new FournisseurKey("S1"),
                          new FournisseurData("Smith", 20, "London"));
            suppliers.put(new FournisseurKey("S2"),
                          new FournisseurData("Jones", 10, "Paris"));
            suppliers.put(new FournisseurKey("S3"),
                          new FournisseurData("Blake", 30, "Paris"));
            suppliers.put(new FournisseurKey("S4"),
                          new FournisseurData("Clark", 20, "London"));
            suppliers.put(new FournisseurKey("S5"),
                          new FournisseurData("Adams", 30, "Athens"));
        }
    }

    private void addParts()
    {
        Map parts = views.getPartMap();
        if (parts.isEmpty())
        {
            System.out.println("Adding Parts");
            parts.put(new ProduitKey("P1"),
                      new ProduitData("Nut", "Red",
                                    new Poids(12.0, Poids.GRAMMES),
                                    "London"));
            parts.put(new ProduitKey("P2"),
                      new ProduitData("Bolt", "Green",
                                    new Poids(17.0, Poids.GRAMMES),
                                    "Paris"));
            parts.put(new ProduitKey("P3"),
                      new ProduitData("Screw", "Blue",
                                    new Poids(17.0, Poids.GRAMMES),
                                    "Rome"));
            parts.put(new ProduitKey("P4"),
                      new ProduitData("Screw", "Red",
                                    new Poids(14.0, Poids.GRAMMES),
                                    "London"));
            parts.put(new ProduitKey("P5"),
                      new ProduitData("Cam", "Blue",
                                    new Poids(12.0, Poids.GRAMMES),
                                    "Paris"));
            parts.put(new ProduitKey("P6"),
                      new ProduitData("Cog", "Red",
                                    new Poids(19.0, Poids.GRAMMES),
                                    "London"));
        }
    }

    private void addShipments()
    {
        Map shipments = views.getShipmentMap();
        if (shipments.isEmpty())
        {
            System.out.println("Adding Shipments");
            shipments.put(new VentesKey("P1", "S1"),
                          new VentesData(300));
            shipments.put(new VentesKey("P2", "S1"),
                          new VentesData(200));
            shipments.put(new VentesKey("P3", "S1"),
                          new VentesData(400));
            shipments.put(new VentesKey("P4", "S1"),
                          new VentesData(200));
            shipments.put(new VentesKey("P5", "S1"),
                          new VentesData(100));
            shipments.put(new VentesKey("P6", "S1"),
                          new VentesData(100));
            shipments.put(new VentesKey("P1", "S2"),
                          new VentesData(300));
            shipments.put(new VentesKey("P2", "S2"),
                          new VentesData(400));
            shipments.put(new VentesKey("P2", "S3"),
                          new VentesData(200));
            shipments.put(new VentesKey("P2", "S4"),
                          new VentesData(200));
            shipments.put(new VentesKey("P4", "S4"),
                          new VentesData(300));
            shipments.put(new VentesKey("P5", "S4"),
                          new VentesData(400));
        }
    }

    private void printEntries(String label, Iterator iterator)
    {
        System.out.println("\n--- " + label + " ---");
        while (iterator.hasNext())
        {
            Map.Entry entry = (Map.Entry) iterator.next();
            System.out.println(entry.getKey().toString());
            System.out.println(entry.getValue().toString());
        }
    }
}
