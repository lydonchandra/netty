/*
 * Copyright 2009 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.netty.channel.socket.oio;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.junit.Test;

public class OioDatagramChannelTest {
	
	@Test
	public void testConstructor() {
//	public static void main(String [] args) {
		OioDatagramChannelFactory factory = new OioDatagramChannelFactory(Executors.newCachedThreadPool());
		ConnectionlessBootstrap bootstrap = new ConnectionlessBootstrap(factory);
		
		InetSocketAddress address = new InetSocketAddress("localhost", 9999);
		
	    bootstrap.getPipeline().addFirst("handler", new SimpleChannelHandler() {
	    	
	    	@Override
	    	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
	    		e.getMessage();
	    		System.out.println("message received");
	    	}
	    });

	    OioDatagramChannel datagramChannel = (OioDatagramChannel)factory.newChannel(bootstrap.getPipeline());
	    datagramChannel.bind(new InetSocketAddress("localhost", 9998));
	    assertEquals(9998, datagramChannel.getLocalAddress().getPort());
	    assertNull(datagramChannel.getRemoteAddress());
	    assertTrue(datagramChannel.isBound());
	    assertFalse(datagramChannel.isConnected());
	    datagramChannel.unbind();
	    assertFalse(datagramChannel.isBound());
	    
	    //Channel channel = bootstrap.bind(address);
	        
	}
    

}
