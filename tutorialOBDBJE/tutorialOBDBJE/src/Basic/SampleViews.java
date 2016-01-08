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
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.collections.StoredEntrySet;
import com.sleepycat.collections.StoredSortedMap;

public class SampleViews {

    private StoredSortedMap partMap;
    private StoredSortedMap supplierMap;
    private StoredSortedMap shipmentMap;

    
    
    
    public SampleViews(Bd bd)
    {
        ClassCatalog catalog = bd.getClassCatalog();
        EntryBinding partKeyBinding =  new SerialBinding(catalog, ProduitKey.class);
        EntryBinding partDataBinding = new SerialBinding(catalog, ProduitData.class);
        EntryBinding supplierKeyBinding = new SerialBinding(catalog, FournisseurKey.class);
        EntryBinding supplierDataBinding = new SerialBinding(catalog, FournisseurData.class);
        EntryBinding shipmentKeyBinding = new SerialBinding(catalog, VentesKey.class);
        EntryBinding shipmentDataBinding = new SerialBinding(catalog, VentesData.class);
    
        partMap =
            new StoredSortedMap(bd.getBdProduit(),
                          partKeyBinding, partDataBinding, true);
        supplierMap =
            new StoredSortedMap(bd.getBdFournisseur(),
                          supplierKeyBinding, partDataBinding, true);
        shipmentMap =
            new StoredSortedMap(bd.getBdVentes(),
                          shipmentKeyBinding, partDataBinding, true);
    
    }    

public final StoredSortedMap getPartMap()
    {
        return partMap;
    }

public final StoredSortedMap getSupplierMap()
    {
        return supplierMap;
    }

public final StoredSortedMap getShipmentMap()
    {
        return shipmentMap;
    }

public final StoredEntrySet getPartEntrySet()
    {
        return (StoredEntrySet) partMap.entrySet();
    }

public final StoredEntrySet getSupplierEntrySet()
    {
        return (StoredEntrySet) supplierMap.entrySet();
    }

public final StoredEntrySet getShipmentEntrySet()
    {
        return (StoredEntrySet) shipmentMap.entrySet();
    }
    
    
}
