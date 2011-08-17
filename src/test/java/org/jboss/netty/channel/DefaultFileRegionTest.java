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
package org.jboss.netty.channel;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import org.jboss.netty.util.internal.ExecutorUtil;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author Don
 *
 * @version $Rev$, $Date$
 *
 */
public class DefaultFileRegionTest {

	static final String message = "hellooo";
	static final int testPort = 45678;
	private static ExecutorService bossExecutor, slaveExecutor, clientExecutor;

    @BeforeClass
    public static void init() {
    	bossExecutor = Executors.newCachedThreadPool();
    	slaveExecutor = Executors.newCachedThreadPool();
    	clientExecutor = Executors.newCachedThreadPool();
    }
	
	@AfterClass
    public static void destroy() {
        ExecutorUtil.terminate(bossExecutor);
        ExecutorUtil.terminate(slaveExecutor);
        ExecutorUtil.terminate(clientExecutor);
    }

    
	@Test
	public void testBasic() throws IOException, InterruptedException {

		/**************************************
		 * Create Server and Client process
		 */
    	ServerBootstrap server = new ServerBootstrap(new NioServerSocketChannelFactory(bossExecutor, slaveExecutor));
    	server.getPipeline().addFirst("chunked", new ChunkedWriteHandler());
    	server.bind(new InetSocketAddress(testPort));
    	
    	NioClientSocketChannelFactory channelFactory = new NioClientSocketChannelFactory(clientExecutor, Executors.newCachedThreadPool());
    	ClientBootstrap clientBootstrap = new ClientBootstrap(channelFactory);
    	clientBootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return org.jboss.netty.channel.Channels.pipeline(
						new SimpleChannelUpstreamHandler() {
							
							@Override
						      public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
						          	// Send the first message.  Server will not send anything here
						          	// because the firstMessage's capacity is 0.
									System.out.println("client - channel connected");
						      }
							
							@Override
							public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
								System.out.println("message received");
							}
							
							@Override
							      public void exceptionCaught(
							              ChannelHandlerContext ctx, ExceptionEvent e) {
						          		// Close the connection when an exception is raised.
						                System.out.println(e.getCause());
						                e.getChannel().close();
							      }
						},
						
						new ChunkedWriteHandler()
				);
			}
    		
    	});

    	InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", testPort);
    	final ChannelFuture channelFuture =  clientBootstrap.connect(inetSocketAddress);
    	Thread.sleep(2000);
    	
    	
    	////////////////////////////////
    	// FileRegion related tests
    	////////////////////////////////
		File file = new File(this.getClass().getResource("/data.zip").getFile());
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		long fileLength = raf.length();
		DefaultFileRegion fileRegion = new DefaultFileRegion(raf.getChannel(),0, fileLength );
		assertEquals( fileRegion.getCount(), fileLength);
		assertEquals( fileRegion.getPosition(), 0);

		final ChannelFuture writeFuture = channelFuture.getChannel().write(fileRegion);
		writeFuture.addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				System.out.println("FileRegion is transferred");
				assertTrue(writeFuture.isDone());
				assertTrue(writeFuture.isSuccess());
			}
		});
		writeFuture.await(2000);
		
		WritableByteChannel writableChannel = Channels.newChannel( new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				// discarding the data going into this channel
				// behaves like a /dev/null
			}
		});
		
		long transferred = fileRegion.transferTo(writableChannel, 0);
		assertEquals(transferred, fileLength);
		
		fileRegion.releaseExternalResources();
	}
}
