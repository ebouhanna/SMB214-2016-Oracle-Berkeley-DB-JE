import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;
import junit.framework.Assert;
import org.junit.Test;
import org.soatexpert.bdbje.model.Employee;

import java.io.File;

public class DPLSampleTransactionTest {

    @Test
    public void testDPL(){
        File file = new File(System.getProperty("java.io.tmpdir"));


        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        environmentConfig.setTransactional(true); //<< gestion des transactions

        TransactionConfig transactionConfig = new TransactionConfig(); //<< gestion des transactions
        transactionConfig.setReadCommitted(true); //<< gestion des transactions
        // physiquement ou se trouve la base
        Environment environment = new Environment(file, environmentConfig);
        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setAllowCreate(true);
        storeConfig.setTransactional(true); //<< gestion des transactions

        Transaction tx = environment.getThreadTransaction(); //<< gestion des transactions
        environment.beginTransaction(tx, transactionConfig); //<< gestion des transactions

        EntityStore entityStore =
                new EntityStore(environment, "BDBJEDPLSample", storeConfig);

        PrimaryIndex<Integer, Employee> employeePrimaryIndex =
                entityStore.getPrimaryIndex(Integer.class, Employee.class);

        employeePrimaryIndex.putNoReturn( new Employee(12,"francois", "ostyn", null));

        tx.commit();




        Employee employee = employeePrimaryIndex.get(12);




        Assert.assertNotNull(employee);
        employeePrimaryIndex.delete(12);
        employee = employeePrimaryIndex.get(12);
        Assert.assertNull(employee);

        entityStore.close();
        environment.close();

    }
}
