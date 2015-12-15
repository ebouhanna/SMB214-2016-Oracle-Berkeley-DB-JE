import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;
import junit.framework.Assert;
import org.junit.Test;
import org.soatexpert.bdbje.model.Employee;

import java.io.File;

public class DPLSampleTest {

    @Test
    public void testDPL(){
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        // physiquement ou se trouve la base
        Environment environment =
                new Environment(new File(System.getProperty("java.io.tmpdir")), environmentConfig);
        StoreConfig storeConfig =
                new StoreConfig();
        storeConfig.setAllowCreate(true);

        EntityStore entityStore =
                new EntityStore(environment, "BDBJEDPLSample", storeConfig);

        PrimaryIndex<Integer, Employee> employeePrimaryIndex =
                entityStore.getPrimaryIndex(Integer.class, Employee.class);

        employeePrimaryIndex.putNoReturn(
                new Employee(12,"francois", "ostyn", null));

        Employee employee = employeePrimaryIndex.get(12);
        Assert.assertNotNull(employee);
        employeePrimaryIndex.delete(12);
        employee = employeePrimaryIndex.get(12);
        Assert.assertNull(employee);

        entityStore.close();
        environment.close();

    }
}
