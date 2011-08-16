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
package org.jboss.netty.handler.stream;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.internal.ExecutorUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 *
 * @version $Rev$, $Date$
 *
 */
public class ChunkedWriteHandlerTest {

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
    public void testConstructor() throws IOException, InterruptedException {
    	
    	ServerBootstrap server = new ServerBootstrap(new NioServerSocketChannelFactory(bossExecutor, slaveExecutor));
    	server.getPipeline().addFirst("chunked", new ChunkedWriteHandler());
    	
    	server.bind(new InetSocketAddress(testPort));
    	
    	
    	
    	NioClientSocketChannelFactory channelFactory = new NioClientSocketChannelFactory(clientExecutor, Executors.newCachedThreadPool());
    	ClientBootstrap clientBootstrap = new ClientBootstrap(channelFactory);
    	clientBootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(
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
    	ChannelFuture channelFuture =  clientBootstrap.connect(inetSocketAddress);
    	
//    	channelFuture.addListener(
//    			new ChannelFutureListener() {
//					@Override
//					public void operationComplete(ChannelFuture future) throws Exception {
//						System.out.println("connected!");
//					}
//			}
//    	);

    	// why do I need this sleep ??? for some reason, I can't just use channelFutureListener.operationComplete.
    	// something else is killing the channel if i wrap things up inside channelFutureListener.operationComplete
    	Thread.sleep(1000);
//    	channelFuture.addListener(
//    			new ChannelFutureListener() {
//					@Override
//					public void operationComplete(ChannelFuture future) throws Exception {
//						System.out.println("operationComplete!");
//					}
//				}
//    	);
    	
    	Channel clientChannel = channelFuture.getChannel();
    	ChannelFuture channelFuture2 = clientChannel.write( new ChunkedFile( new File("i:\\test\\mipro-v11-eval.zip"))  );
    	
    	channelFuture2.addListener(
    			new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						System.out.println("finished uploading!");
					}
				}
    	);
    	
    	clientChannel.getCloseFuture().await(100000);
    	
    	
    	
    	
    	
    	
    	
    	
    	
    }
}
