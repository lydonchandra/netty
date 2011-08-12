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
package org.jboss.netty.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import static org.junit.Assert.*;

public class HashedWheelTimerTest {

	@Test
	public void testVirtualExecutorService() throws InterruptedException, ExecutionException {
		Executor executor = Executors.newFixedThreadPool(2);

		VirtualExecutorService virtualExecutor = new VirtualExecutorService(executor);
		Future future = virtualExecutor.submit(new Runnable() {

			@Override
			public void run() {
				System.out.println("running inside VirtualExecutor");				
			}			
		});
		
		Object futureReturn = future.get();
		
		// completed future returns null
		assertNull(futureReturn);
		
		virtualExecutor.shutdown();
	}
}
