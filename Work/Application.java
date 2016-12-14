import java.util.Iterator;

import com.ibm.websphere.objectgrid.ClientClusterContext;
import com.ibm.websphere.objectgrid.ObjectGrid;
import com.ibm.websphere.objectgrid.ObjectGridManager;
import com.ibm.websphere.objectgrid.ObjectGridManagerFactory;
import com.ibm.websphere.objectgrid.ObjectMap;
import com.ibm.websphere.objectgrid.Session;
import com.ibm.websphere.objectgrid.config.QueryConfig;
import com.ibm.websphere.objectgrid.config.QueryMapping;
import com.ibm.websphere.objectgrid.query.ObjectQuery;

public class Application
{

    static public void main(String [] args) throws Exception
    {
        ObjectGridManager oMgr = ObjectGridManagerFactory.getObjectGridManager();
        ClientClusterContext ccc = oMgr.connect("localhost:2809", null, null);
        ObjectGrid og = oMgr.getObjectGrid(ccc, "AtulGrid");
        
        Session s = og.getSession();
        ObjectMap orderMap = s.getMap("Order");
        ObjectMap custMap = s.getMap("Customer");


        s.begin();
        OrderBean o = new OrderBean();
        OrderBeanKeys ok = new OrderBeanKeys();
        // posting data into Order map
        ok.orderNumber = "15";
        o.customerName = "El_15";
        o.customerId = o.customerName;
        o.date = new java.util.Date(System.currentTimeMillis());
        o.itemName = "Data";
        o.orderNumber = ok.orderNumber;
        o.price = 14.87;
        o.quantity = 20;
        orderMap.insert(ok, o);
        s.commit();

        s.begin();
        CustomerBean c = new CustomerBean();
        CustomerBeanKeys ck = new CustomerBeanKeys();
        // posting data into Customer map
        ck.id = "15";;
	ck.firstName =  "El_15";   
        c.id = ck.id;
        c.firstName = ck.firstName;
        c.surname = c.firstName;
        c.address = c.firstName+" Address";
        c.phoneNumber = ck.id+ck.id;
        custMap.insert(ck, c);
        s.commit();


        int i = 0;
        for (i=0; i<13; i++){		
        		s.begin();

        		OrderBean res = new OrderBean();
//       		ObjectQuery query = s.createObjectQuery("SELECT p FROM Order p WHERE p.keys.orderNumber='96'");
//        		ObjectQuery query = s.createObjectQuery("SELECT p FROM Order p WHERE p.orderNumber='96'");
//        		ObjectQuery query = s.createObjectQuery("SELECT p FROM Order p WHERE p.orderNumber='15'");
        		ObjectQuery query = s.createObjectQuery("SELECT p FROM Order p, Customer c WHERE p.orderNumber = c.id");

//        		query.setParameter(1, "13");
//        		query.setParameter(new OrderBeanKeys().setOrderNumber("96"), res);
//        		ObjectQuery query = s.createObjectQuery("SELECT p FROM Order p WHERE p.quantity=12");
//        		ObjectQuery query = s.createObjectQuery("SELECT p FROM Order p WHERE p.itemName='Data'");        		
        		query.setPartition(i);
        		Iterator result2 = query.getResultIterator();
        		while (result2.hasNext()){
        				res = (OrderBean) result2.next();
        				System.out.println("Found order for customer: " + res.customerName + " Order: "+ res.price +" quantity: "+res.quantity);
        				}
                s.commit();

        }
	s.close();
}
}
