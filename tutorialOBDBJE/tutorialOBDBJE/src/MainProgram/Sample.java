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

import Basic.SampleDatabase;
import Basic.SampleViews;
import Basic.PartData;
import Basic.PartKey;
import Basic.ShipmentData;
import Basic.ShipmentKey;
import Basic.SupplierData;
import Basic.SupplierKey;
import Basic.Weight;
import com.sleepycat.je.DatabaseException;
import java.io.FileNotFoundException;
import com.sleepycat.collections.TransactionRunner;
import com.sleepycat.collections.TransactionWorker;
import java.util.Map;
import java.util.Iterator;


public class Sample {
    
    private SampleDatabase db;
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
        db = new SampleDatabase(homeDir);
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
            suppliers.put(new SupplierKey("S1"),
                          new SupplierData("Smith", 20, "London"));
            suppliers.put(new SupplierKey("S2"),
                          new SupplierData("Jones", 10, "Paris"));
            suppliers.put(new SupplierKey("S3"),
                          new SupplierData("Blake", 30, "Paris"));
            suppliers.put(new SupplierKey("S4"),
                          new SupplierData("Clark", 20, "London"));
            suppliers.put(new SupplierKey("S5"),
                          new SupplierData("Adams", 30, "Athens"));
        }
    }

    private void addParts()
    {
        Map parts = views.getPartMap();
        if (parts.isEmpty())
        {
            System.out.println("Adding Parts");
            parts.put(new PartKey("P1"),
                      new PartData("Nut", "Red",
                                    new Weight(12.0, Weight.GRAMS),
                                    "London"));
            parts.put(new PartKey("P2"),
                      new PartData("Bolt", "Green",
                                    new Weight(17.0, Weight.GRAMS),
                                    "Paris"));
            parts.put(new PartKey("P3"),
                      new PartData("Screw", "Blue",
                                    new Weight(17.0, Weight.GRAMS),
                                    "Rome"));
            parts.put(new PartKey("P4"),
                      new PartData("Screw", "Red",
                                    new Weight(14.0, Weight.GRAMS),
                                    "London"));
            parts.put(new PartKey("P5"),
                      new PartData("Cam", "Blue",
                                    new Weight(12.0, Weight.GRAMS),
                                    "Paris"));
            parts.put(new PartKey("P6"),
                      new PartData("Cog", "Red",
                                    new Weight(19.0, Weight.GRAMS),
                                    "London"));
        }
    }

    private void addShipments()
    {
        Map shipments = views.getShipmentMap();
        if (shipments.isEmpty())
        {
            System.out.println("Adding Shipments");
            shipments.put(new ShipmentKey("P1", "S1"),
                          new ShipmentData(300));
            shipments.put(new ShipmentKey("P2", "S1"),
                          new ShipmentData(200));
            shipments.put(new ShipmentKey("P3", "S1"),
                          new ShipmentData(400));
            shipments.put(new ShipmentKey("P4", "S1"),
                          new ShipmentData(200));
            shipments.put(new ShipmentKey("P5", "S1"),
                          new ShipmentData(100));
            shipments.put(new ShipmentKey("P6", "S1"),
                          new ShipmentData(100));
            shipments.put(new ShipmentKey("P1", "S2"),
                          new ShipmentData(300));
            shipments.put(new ShipmentKey("P2", "S2"),
                          new ShipmentData(400));
            shipments.put(new ShipmentKey("P2", "S3"),
                          new ShipmentData(200));
            shipments.put(new ShipmentKey("P2", "S4"),
                          new ShipmentData(200));
            shipments.put(new ShipmentKey("P4", "S4"),
                          new ShipmentData(300));
            shipments.put(new ShipmentKey("P5", "S4"),
                          new ShipmentData(400));
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
