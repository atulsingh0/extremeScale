## Catalog Server 
### Start -> 
startXsServer.bat csDINotify -listenerHost server.data.abc.com -listenerPort 9820

### Stop -> 
stopXsServer csDINotify -catalogServiceEndPoints server.data.abc.com:9820

## Container Server
### Start-> 
startXsServer con0DINotify -objectgridFile ..\csDINotify\Config\objectgrid.xml -deploymentPolicyFile ..\csDINotify\Config\deployment.xml -catalogServiceEndPoints server.data.abc.com:9820

### Stop -> 
stopXsServer con0DINotify -catalogServiceEndPoints server.data.abc.com:9820

### ADMIN Monitor Console (startConsoleServer.bat) Admin/Admin
https://<server>:7443
Register catalog servers: csDINotify
Register catalag servers into catalog service domain: server.data.abc.com


--ObjectGrid.xml--

<?xml version="1.0" encoding="UTF-8"?>
<!-- 
	This sample program is provided AS IS and may be used, executed, copied and modified
	without royalty payment by customer
	(a) for its own instruction and study,
	(b) in order to develop applications designed to run with an IBM WebSphere product,
	either for customer's own internal use or for redistribution by customer, as part of such an
	application, in customer's own products.
	Licensed Materials - Property of IBM
	5724-J34 (C) COPYRIGHT International Business Machines Corp. 2012
-->

<objectGridConfig xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xsi:schemaLocation="http://ibm.com/ws/objectgrid/config ../objectGrid.xsd"
 xmlns="http://ibm.com/ws/objectgrid/config">

  <objectGrids>
    <objectGrid name="DINotifyGrid" txTimeout="30">
    
      <backingMap name="Client" copyMode="COPY_TO_BYTES" 
      		lockStrategy="PESSIMISTIC" 
      		nullValuesSupported="false" />
      		
      <backingMap name="Account" copyMode="COPY_TO_BYTES" 
      		lockStrategy="PESSIMISTIC" 
      		nullValuesSupported="false" /> 
    </objectGrid>
  </objectGrids>
  
</objectGridConfig>


--Deployment.xml--

<?xml version="1.0" encoding="UTF-8"?>
<!-- 
	This sample program is provided AS IS and may be used, executed, copied and modified
	without royalty payment by customer
	(a) for its own instruction and study,
	(b) in order to develop applications designed to run with an IBM WebSphere product,
	either for customer's own internal use or for redistribution by customer, as part of such an
	application, in customer's own products.
	Licensed Materials - Property of IBM
	5724-J34 (C) COPYRIGHT International Business Machines Corp. 2012
-->

<deploymentPolicy xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xsi:schemaLocation="http://ibm.com/ws/objectgrid/deploymentPolicy ../deploymentPolicy.xsd"
 xmlns="http://ibm.com/ws/objectgrid/deploymentPolicy">

    <objectgridDeployment objectgridName="DINotifyGrid">
        <mapSet name="DINmapSet" numberOfPartitions="8" minSyncReplicas="0" maxSyncReplicas="1" >
            <map ref="Client"/>
            <map ref="Account"/>
        </mapSet>
    </objectgridDeployment>

</deploymentPolicy>

