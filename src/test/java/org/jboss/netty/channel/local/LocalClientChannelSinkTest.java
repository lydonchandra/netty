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
package org.jboss.netty.channel.local;

import static org.jboss.netty.buffer.ChannelBuffers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ScatteringByteChannel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.easymock.classextension.EasyMock;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.util.CharsetUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LocalClientChannelSinkTest {

	@Test
	public void testHeapChannelBuffer() throws IOException {
		
		
		DefaultLocalServerChannelFactory factory = new DefaultLocalServerChannelFactory();
		LocalServerChannel channel = factory.newChannel(Channels.pipeline());
		channel.close();
		assertEquals(channel.getFactory(), factory);
		
		
	}
}
