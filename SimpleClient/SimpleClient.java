package us.gregreid.samples;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.ibm.websphere.objectgrid.BackingMap;
import com.ibm.websphere.objectgrid.ClientClusterContext;
import com.ibm.websphere.objectgrid.ObjectGrid;
import com.ibm.websphere.objectgrid.ObjectGridException;
import com.ibm.websphere.objectgrid.ObjectGridManager;
import com.ibm.websphere.objectgrid.ObjectGridManagerFactory;
import com.ibm.websphere.objectgrid.ObjectMap;
import com.ibm.websphere.objectgrid.PartitionManager;
import com.ibm.websphere.objectgrid.Session;
import com.ibm.websphere.objectgrid.plugins.TransactionCallbackException;

public class SimpleClient {

	private static final int ITERATIONS = 100;          // How many times to iterate each test
	private static final int NUM_KEYS   = 1000;       // How many key/value sets to create
	private static final int VALUE_SIZE = 2 * 1024;   // How big should the value string be?
	
	private static final String PROPERTIES_FILE = "C:/temp/SimpleClient.properties";

	private static String cep      = "";
	private static String gridName = "";
	private static String mapName  = "";
	
	static long startTime;
	static long endTime;
	static long totalTime;

	/**
	 * @param args
	 * @throws ObjectGridException
	 * @throws TransactionCallbackException
	 * @throws IOException 
	 */
	public static void main(String[] args) throws TransactionCallbackException, ObjectGridException, IOException {
		
		// Get required data from our Properties file
		Properties props = new Properties();
        InputStream inputStream = new FileInputStream(PROPERTIES_FILE);
		props.load(inputStream);
		cep      = getRequiredProperty(props, "cep");		
		gridName = getRequiredProperty(props, "gridName");
		mapName  = getRequiredProperty(props, "mapName");		
		
		System.out.println("Note: All times reported are in MILLISECONDS");

		// Get connected to the unsecured grid
		startTime = System.nanoTime();
		ObjectGridManager ogMgr = ObjectGridManagerFactory.getObjectGridManager();
		endTime = System.nanoTime();
		System.out.println("Time for ObjectGridManagerFactory.getObjectGridManager(): " + TimeUnit.NANOSECONDS.toMillis(endTime-startTime));

		startTime = System.nanoTime();
		ClientClusterContext ccc = ogMgr.connect(cep, null, null);
		endTime = System.nanoTime();
		System.out.println("Time for ogMgr.connect(): " + TimeUnit.NANOSECONDS.toMillis(endTime-startTime));

		startTime = System.nanoTime();
		ObjectGrid grid = ogMgr.getObjectGrid(ccc, gridName);
		endTime = System.nanoTime();
		System.out.println("Time for ogMgr.getObjectGrid(): " + TimeUnit.NANOSECONDS.toMillis(endTime-startTime));

		// Get a Session and the ObjectMap
		startTime = System.nanoTime();
		Session sess = grid.getSession();
		endTime = System.nanoTime();
		System.out.println("Time for grid.getSession(): " + TimeUnit.NANOSECONDS.toMillis(endTime-startTime));
		
		startTime = System.nanoTime();
		ObjectMap om = sess.getMap(mapName);
		endTime = System.nanoTime();
		System.out.println("Time for sess.getMap(mapName): " + TimeUnit.NANOSECONDS.toMillis(endTime-startTime));

		// Get the number of partitions in this map
		startTime = System.nanoTime();
		BackingMap bm = grid.getMap(mapName);
		PartitionManager pm = bm.getPartitionManager();
		int numOfPartitions = pm.getNumOfPartitions();
		endTime = System.nanoTime();
		System.out.println("Time for getNumOfPartitions: " + TimeUnit.NANOSECONDS.toMillis(endTime-startTime));		
		
		// Spit out "stuff of possible interest" from the ObjectMap
		System.out.println("ObjectMap"
                   + " Name="              + om.getName()
                   + " MapType="           + om.getMapType()
                   + " KeyOutputFormat="   + om.getKeyOutputFormat()
                   + " ValueOutputFormat=" + om.getValueOutputFormat()
                   );
		
		// Spit out "stuff of possible interest" from the BackingMap
		System.out.println("BackingMap"
		                   + " Name="              + bm.getName()
				           + " TimeToLive="        + bm.getTimeToLive()
				           + " TtlEvictorType="    + bm.getTtlEvictorType()
				           + " CopyMode="          + bm.getCopyMode()
				           + " LockStrategy="      + bm.getLockStrategy()
				           + " State="             + bm.getState()
				           + " NumOfPartitions="   + numOfPartitions
				           );
				
		
		// Populate the map with keys and values for testing
		String[] keys = new String[NUM_KEYS];
		byte[]  value = new byte[VALUE_SIZE];
		startTime = System.nanoTime();
		for (int i = 0; i < NUM_KEYS; ++i) {
			keys[i]  = new String("Key"+i);
			value[0] = (byte) i;  // Make each value have a unique first byte, wrapping 00..FF
			om.upsert(keys[i], value);
		}
		endTime = System.nanoTime();
		System.out.println("Time to om.upsert() " + NUM_KEYS + " keys into the grid: "+ TimeUnit.NANOSECONDS.toMillis(endTime-startTime));

		// Retrieve all keys one at a time to warm up.  We won't bother
		// timing this.
		for (int i = 0; i < NUM_KEYS; ++i) {
			om.get(keys[i]);
		}
		
		// Test the time to retrieve (ITERATIONS times) all keys, one at a time
		totalTime = 0;
		for (int j = 0; j < ITERATIONS; ++j) {
			startTime = System.nanoTime();
			for (int i = 0; i < NUM_KEYS; ++i) {
				om.get(keys[i]);
			}
			endTime = System.nanoTime();
			totalTime += (endTime - startTime);
		}
		System.out.println("Avg om.get() of " + NUM_KEYS + " keys: " + TimeUnit.NANOSECONDS.toMillis(totalTime) / ITERATIONS);
		
		// Test the time to retrieve (ITERATIONS times) all keys, 
		// using the standard WXS ObjectMap.getAll() API.
		List<String> keyList = Arrays.asList(keys); 
		totalTime = 0;
		for (int j = 0; j < ITERATIONS; ++j) {
			startTime = System.nanoTime();
			om.getAll(keyList);
			endTime = System.nanoTime();
			totalTime += (endTime - startTime);
		}
		System.out.println("Avg om.getAll() of " + NUM_KEYS + " keys: " + TimeUnit.NANOSECONDS.toMillis(totalTime) / ITERATIONS);

		System.out.println("Sleeping for 30 seconds to let you kill some containers...");
		try {
		    Thread.sleep(30000);                 
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		
		System.out.println("Woke up; trying stuff again");
		
		startTime = System.nanoTime();
		ObjectGrid grid2 = ogMgr.getObjectGrid(ccc, gridName);
		endTime = System.nanoTime();
		System.out.println("Time for ogMgr.getObjectGrid(): " + TimeUnit.NANOSECONDS.toMillis(endTime-startTime));

		// Get a Session and the ObjectMap
		startTime = System.nanoTime();
		Session sess2 = grid2.getSession();
		endTime = System.nanoTime();
		System.out.println("Time for grid2.getSession(): " + TimeUnit.NANOSECONDS.toMillis(endTime-startTime));
		
		startTime = System.nanoTime();
		ObjectMap om2 = sess2.getMap(mapName);
		endTime = System.nanoTime();
		System.out.println("Time for sess2.getMap(mapName): " + TimeUnit.NANOSECONDS.toMillis(endTime-startTime));
		
		// Test the time to retrieve (ITERATIONS times) all keys, one at a time
		totalTime = 0;
		for (int j = 0; j < ITERATIONS; ++j) {
			startTime = System.nanoTime();
			for (int i = 0; i < NUM_KEYS; ++i) {
				om2.get(keys[i]);
			}
			endTime = System.nanoTime();
			totalTime += (endTime - startTime);
		}
		System.out.println("Avg om2.get() of " + NUM_KEYS + " keys: " + TimeUnit.NANOSECONDS.toMillis(totalTime) / ITERATIONS);

		
		
		System.exit(0);
	}
	
	
	// Gets a property from the properties file; throws exception if not found
	static String getRequiredProperty(Properties props, String propName) {
		String propVal = props.getProperty(propName);
		if (propVal == null)
			throw new RuntimeException("Missing required property: " + propName);
		return propVal;
	}
}





