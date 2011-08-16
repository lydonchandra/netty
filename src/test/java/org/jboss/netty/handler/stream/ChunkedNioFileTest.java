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
import java.nio.channels.Channels;

import junit.framework.Assert;

import org.junit.Test;


/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Don</a>
 *
 * @version $Rev$, $Date$
 *
 */
public class ChunkedNioFileTest {
	
	@Test
	public void testBasic() throws Exception {
		FileInputStream fileStream = new FileInputStream("k:\\soft\\java.zip");
		
		File file = new File("k:\\soft\\java.zip");
		ChunkedNioFile chunkedFile = new ChunkedNioFile(file);
		Assert.assertTrue(chunkedFile.getCurrentOffset() == 0);
		Assert.assertTrue(chunkedFile.getEndOffset() > 0);
		Assert.assertTrue(chunkedFile.getStartOffset() == 0);
		
		while( !chunkedFile.isEndOfInput() ) {
			Object obj = chunkedFile.nextChunk();
			Assert.assertNotNull(obj);
		}
		
		Assert.assertTrue(chunkedFile.hashCode() > 0);
		
		
	}
}
