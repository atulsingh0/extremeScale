import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import com.devwebsphere.wxsutils.WXSUtils;
import com.ibm.websphere.objectgrid.ObjectGrid;
import com.ibm.websphere.objectgrid.ObjectGridException;
import com.ibm.websphere.objectgrid.ObjectMap;
import com.ibm.websphere.objectgrid.Session;
import com.ibm.websphere.objectgrid.plugins.TransactionCallbackException;

public class WXSTestServer {

	/**
	 * @param args
	 * @throws ObjectGridException
	 * @throws TransactionCallbackException
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws TransactionCallbackException, ObjectGridException, FileNotFoundException, URISyntaxException, IOException {
		
		// This looks for the wxsutils.properties file in the classpath.
		// Typically, we put wxsutils.properties AND the grid configuration xml
		// files referenced from the wxsutils.properties into a "config" folder under the root, and
		// add that folder to our build path as a class folder.
		WXSUtils utils = WXSUtils.getDefaultUtils();
		
		// The wxsutils.properties file contains something like:
		//     #cep=localhost:2809
		//     grid=DataCache
		//     og_xml_path=/objectGrid.xml
		//     dp_xml_path=/objectGridDeployment.xml
		//     threads=32
		// This call uses that configuration to connect to the specified
		// grid.  If the cep is specified, then the grid is already available
		// elsewhere (and the dp_xml_path is not used).  If the cep is NOT
		// specified, then this call creates a test catalog and container
		// server using the configuration specified (and the dpl_xml_path is
		// required).
		ObjectGrid og = utils.getObjectGrid();
		

		/*****
		
	    // We have our grid connection.  Now we can do our usual
		// stuff, getting a session...
		Session s = og.getSession();
		
		// ... and a map to use.  The following example uses a 
		// template map defined in our objectGrid.xml.
		ObjectMap map = s.getMap("WXSTestServer.LAT.P");
		
		// The following tests that we're able to use the retrieved map, by
		// populating it with a bunch of stuff.
		final int KEY_COUNT  = 10000;
		final int VALUE_SIZE = 1 * 1024;

		String[] keys = new String[KEY_COUNT];
		byte[] value = new byte[VALUE_SIZE];
		for (int i = 0; i < KEY_COUNT; ++i) {
			keys[i] = String.valueOf(i);
			map.upsert(keys[i], value);
		}
		
		*****/
		
		System.out.println("ObjectGrid is ready for use");
	}
}
