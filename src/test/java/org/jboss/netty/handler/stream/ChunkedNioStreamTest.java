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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import junit.framework.Assert;

import org.junit.Test;


/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Don</a>
 *
 * @version $Rev$, $Date$
 *
 */
public class ChunkedNioStreamTest {
	
	@Test
	public void testBasic() throws Exception {
		FileInputStream fileStream = new FileInputStream(
				new File(this.getClass().getResource("/data.zip").getFile()));
		BufferedInputStream bufferStream = new BufferedInputStream(fileStream);
		ChunkedStream chunkStream = new ChunkedStream(bufferStream);

		Assert.assertEquals(chunkStream.getTransferredBytes(), 0);
		
		while( !chunkStream.isEndOfInput() ) {
			Object chunk = chunkStream.nextChunk();
			Assert.assertNotNull(chunk);
		}
		
		chunkStream.close();
	}
}
