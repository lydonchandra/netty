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
package org.jboss.netty.util.internal;

import static java.lang.System.out;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

/**
 * Unit test for {@link StringUtil}.
 * <p/>
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author don
 * @version $Rev$, $Date$
 */
public class ConcurrentWeakKeyHashMapTest {

    @Test
    public void basicConcurrentWeakKeyHashMap() {
    	ConcurrentMap<String,String> concurrentMap = new ConcurrentWeakKeyHashMap<String,String>();
    	boolean isEmpty = concurrentMap.isEmpty();
    	assertEquals(isEmpty, true);
    	
    	// basic put operation
    	concurrentMap.put("key1", "value1");
    	concurrentMap.put("key2", "value2");
    	String var1 = concurrentMap.get("key1");
    	assertEquals(var1, "value1");
    	
    	// basic get operation
    	String var2 = concurrentMap.get("key2");
    	assertEquals(var2, "value2");
    	
    	isEmpty = concurrentMap.isEmpty();
    	assertEquals(isEmpty, false);
    	
    	concurrentMap.remove("key1");
    	concurrentMap.remove("key2");
		isEmpty = concurrentMap.isEmpty();
		assertEquals(isEmpty, true);
    }
   
    /**
     * Trying to evaluate  if (segments[i].count != 0 || mc[i] != segments[i].modCount) 
     * 		in ConcurrentWeakKeyHashMap.isEmpty() to true     * 
     * http://stackoverflow.com/questions/7022428/concurrentweakkeyhashmap-isempty-method
     * 
     * @throws InterruptedException
     */
//    @Test
    public void testABA() throws InterruptedException {
    	
    	int nThreads = 2;
    	// a latch so all threads start at the same time
    	final CountDownLatch startGate = new CountDownLatch(1);
    	
    	// another latch to wait for all threads to finish
    	final CountDownLatch endGate = new CountDownLatch(nThreads);
    	
    	// our test subject to violate
    	final ConcurrentMap<String,String> concurrentMap = new ConcurrentWeakKeyHashMap<String,String>();
    	
    	for( int i=0; i<nThreads; i++) {
    		
    		// loopId is used to identify thread
    		// Thread 1 monitors the concurrentMap by calling isEmpty()
    		// Thread 2 repeatedly add and remove the same key/value paid "k1"/"v1"
    		final int loopId = i;
    		
    		Thread thread = new Thread() {
        		public void run() {

        			try {
						startGate.await();
						
						if( loopId == 0) {
							
							// Thread 1 monitors the concurrentMap by calling isEmpty()
							while(true) {
								boolean isEmpty = concurrentMap.isEmpty();
			        			out.println("Thread "+ this.getId() + ":" + "isEmpty(): " + isEmpty);
							}
							
		        			
						} else {
							
							// Thread 2 repeatedly add and remove the same key/value paid "k1"/"v1"
							while( true ) {
								concurrentMap.put("k1", "v1");
				    			out.println( "Thread "+ this.getId() + ":"  + concurrentMap.get("k1"));
				    			concurrentMap.remove("k1");
				    			out.println( "Thread "+ this.getId() + ":"  + concurrentMap.get("k1"));
							}
						}
        			
					} catch (InterruptedException e) {
						
					} finally {
						endGate.countDown();
					}	
        		}
        	};
        	thread.start();
    	}    	
    	
    	startGate.countDown();
    	
    	// wait for all thread to finish
    	endGate.await();
    	
    }
    
    
    @Test
    public void testSize() {
    	ConcurrentMap<String,String> concurrentMap = new ConcurrentWeakKeyHashMap<String,String>();
    	
    	// basic put operation
    	concurrentMap.put("key1", "value1");
    	concurrentMap.put("key2", "value2");
    	
    	int size = concurrentMap.size();
    	assertEquals(size, 2);
    	
    }
    
    
    @Test
    public void testConcurrentHashMap() {
    	int maxNum = 30000;
    	int initalCapacity = 2*maxNum;
    	ConcurrentMap<String,String> concurrentMap = new ConcurrentHashMap<String,String>(initalCapacity);
    	for( int i=0; i<maxNum; i++) {
    		String key = "k" + i;
    		String value = "v" + i;
    		concurrentMap.put(key, value);
    		//System.out.println(concurrentMap.size());
    	}
    	int size = concurrentMap.size();
    	assertEquals(size, maxNum);
    }
    
    @Test
    public void testWeakKeyGarbageCollection() throws InterruptedException {
    	int maxNum = 9000;
    	int initalCapacity = 2*maxNum;
    	ConcurrentWeakKeyHashMap<String,String> concurrentMap = new ConcurrentWeakKeyHashMap<String,String>(initalCapacity);    	
    	
    	List<String> strongList = new ArrayList();
    	
    	for( int i=0; i<maxNum; i++) {
    		String key = "k" + i;
    		String value = "v" + i;
    		concurrentMap.put(key, value);
    		strongList.add(key);
    	}
    	
    	int size = concurrentMap.size();
    	assertEquals(size, maxNum);
    	strongList.clear();
    	strongList = null;
    	System.gc();
    	
    	// garbage collection doesn't affect concurrentMap at this point.
    	size = concurrentMap.size();
    	assertEquals(size, maxNum);

    	// adding and removing just one entry doesn't affect the size.
    	concurrentMap.put("ka", "va");
    	concurrentMap.remove("ka");
//    	concurrentMap.keys();

    	size = concurrentMap.size();
    	assertEquals(size, maxNum);
    	
    	for( int i=maxNum; i<maxNum+1000; i++) {
    		String key = "k" + i;
    		String value = "v" + i;
    		concurrentMap.put(key, value);
    	}
     	
    	// adding many entries does affect the size, it's now variable (and always smaller than maxNum)
    	size = concurrentMap.size();
    	assertEquals(true, (size < maxNum));
    	out.println(size);
    }
    

}
