package org.jboss.netty.handler.ipfilter;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.UpstreamMessageEvent;
import org.junit.Test;

public class CIDR6Test extends TestCase
{
    @Test
    public void testBasic() throws UnknownHostException {
    	CIDR cidr = CIDR.newCIDR(InetAddress.getByName("10.1.1.1"), 24);
    	Inet4Address address1 = (Inet4Address) cidr.getBaseAddress();
    	assertEquals(address1.getHostAddress(), "10.1.1.0");
    	
    	byte[] address2 = CIDR.getIpV6FromIpV4(address1);
    	byte [] address2check = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10, 1, 1, 0 };
    	for(int i=0; i<address2.length; i++) {
    		assertEquals(address2[i], address2check[i]);
    	}
    	
    	
    }
}
