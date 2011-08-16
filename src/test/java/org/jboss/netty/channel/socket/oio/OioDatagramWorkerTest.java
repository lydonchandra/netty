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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.UdpClient;
import org.junit.Before;
import org.junit.Test;

public class OioDatagramWorkerTest {
	
	OioDatagramChannel datagramChannel;
	InetSocketAddress address;
	ConnectionlessBootstrap bootstrap;
	
	final String testMessage = "hellooo";
	
	@Before
	public void init() {
		OioDatagramChannelFactory factory = new OioDatagramChannelFactory(Executors.newFixedThreadPool(1));
		bootstrap = new ConnectionlessBootstrap(factory);
		
		address = new InetSocketAddress("localhost", 9999);
		
		// put a dummy handler so it doesn't complain when we do "bind"
	    bootstrap.getPipeline().addFirst("handler", new SimpleChannelHandler() {
	    });

	    datagramChannel = (OioDatagramChannel)factory.newChannel(bootstrap.getPipeline());
	    datagramChannel.bind(address);
	}
	
	/**
	 * Send a testMessage using UdpClient into our test program.
	 * OioWorker processes the test message.
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testRun() throws InterruptedException, IOException {
		
		// add our specific handler which has assert* in it
		bootstrap.getPipeline().addFirst("handler2", new SimpleChannelHandler() {
	    	
	    	@Override
	    	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
	    		ChannelBuffer incomingBuffer = (ChannelBuffer)e.getMessage();
	    		int capacity = incomingBuffer.capacity();
	    		ChannelBuffer tempBuffer = new BigEndianHeapChannelBuffer(capacity);
	    		
	    		// copy into our temp buffer
	    		incomingBuffer.getBytes(0, tempBuffer, capacity);
	    		
	    		String incomingMessage = new String(tempBuffer.array());
	    		
	    		assertEquals(incomingMessage, testMessage );
	    		
	    		System.out.println("message received:" + incomingMessage);
	    	}
	    });

		// oioworker processes the message sent by udpClient
	    OioDatagramWorker oioworker = new OioDatagramWorker(datagramChannel);
	    Thread workerThread = new Thread( oioworker );

	    // start a new thread separated from this main running thread
	    workerThread.start();
	    
	    UdpClient udpClient = new UdpClient(address.getAddress(), 9999);
	    udpClient.send("hellooo".getBytes());
	    	    
	    // only gives it 1 second to execute.
	    workerThread.join(1000);
	}
    

}
