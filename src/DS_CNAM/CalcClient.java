package DS_CNAM;

import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;


public class CalcClient {
      public static void main(String[] args) throws Exception {

    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();

    config.setServerURL(new URL("http://127.0.0.1:8080/xmlrpc"));
    XmlRpcClient client = new XmlRpcClient();
    client.setConfig(config);

    Object[] params = new Object[]{new Integer(33), new Integer(9)};
	System.out.println("About to get results...(params[0] = " + params[0] 
                           + ", params[1] = " + params[1] + ")." );

    Integer result = (Integer) client.execute("Calculator.add", params);
    System.out.println("Add Result = " + result );

    result = (Integer) client.execute("Calculator.sub", params);
    System.out.println("Sub Result = " + result );

    result = (Integer) client.execute("Calculator.mul", params);
    System.out.println("Mul Result = " + result );
	
    }
}

