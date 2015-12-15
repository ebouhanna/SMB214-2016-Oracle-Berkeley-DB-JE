import com.sleepycat.je.*;
import org.junit.Test;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class BaseAPISampleTest {


    @Test
    public void baseAPITest() throws UnsupportedEncodingException {
        // ouverture de la base de données et creation si nécessaire
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        // physiquement ou se trouve la base
        Environment environment = new Environment(new File(System.getProperty("java.io.tmpdir")), environmentConfig);
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setAllowCreate(true);
        databaseConfig.setSortedDuplicates(false);
        Database database = environment.openDatabase(null, "BDBJESample", databaseConfig);
        // enregistrement en base
        DatabaseEntry key = new DatabaseEntry("keyValue".getBytes("UTF-8"));
        DatabaseEntry data = new DatabaseEntry("dataValue".getBytes("UTF-8"));
        database.put(null, key, data);
        // récupération des enregistrements
        DatabaseEntry searchEntry = new DatabaseEntry();
        database.get(null, key, searchEntry, LockMode.DEFAULT);
        String found = new String(searchEntry.getData(),"UTF-8");
        // mise à jour de l'enregistrement
        database.put(null, key, data);
        // suppression en base
        database.delete(null, key);
        // fermeture de la connexion à la base
        database.close();
        environment.close();
    }

}
