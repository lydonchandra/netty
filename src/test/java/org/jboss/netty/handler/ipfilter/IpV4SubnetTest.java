package org.jboss.netty.handler.ipfilter;

import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.TestCase;

import org.junit.Test;

public class IpV4SubnetTest extends TestCase
{
    @Test
    public void testBasic() throws UnknownHostException{
    	IpV4Subnet subnet1 = new IpV4Subnet("10.1.1.0/24");
    	IpV4Subnet subnet2 = new IpV4Subnet("10.1.1.0/255.255.255.0");
    	assertTrue(subnet1.compareTo(subnet2) == 0);
    	
    	InetAddress address1 = InetAddress.getByName("10.1.1.1");
    	assertTrue(subnet1.contains(address1));
    	
    	
    }
}
