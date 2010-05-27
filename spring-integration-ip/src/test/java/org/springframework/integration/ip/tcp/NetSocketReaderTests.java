/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.integration.ip.tcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

import org.junit.Test;
import org.springframework.integration.ip.util.SocketUtils;

/**
 * @author Gary Russell
 *
 */
public class NetSocketReaderTests {

	/**
	 * Test method for {@link org.springframework.integration.ip.tcp.NioSocketReader#readFully()},
	 * using &lt;length&gt;&lt;message&gt;.
	 */
	@Test
	public void testReadLength() throws Exception {
		int port = SocketUtils.findAvailableServerSocket();
		ServerSocket server = ServerSocketFactory.getDefault().createServerSocket(port);
		SocketUtils.testSendLength(port, null);
		Socket socket = server.accept();
		socket.setSoTimeout(5000);
		NetSocketReader reader = new NetSocketReader(socket);
		if (reader.assembleData()) {
			assertEquals("Data", SocketUtils.TEST_STRING + SocketUtils.TEST_STRING, 
								 new String(reader.getAssembledData()));
		}
		else {
			fail("Failed to assemble first message");
		}
		if (reader.assembleData()) {
			assertEquals("Data", SocketUtils.TEST_STRING + SocketUtils.TEST_STRING, 
								 new String(reader.getAssembledData()));
		}
		else {
			fail("Failed to assemble second message");
		}
		server.close();
	}
	
	/**
	 * Test method for {@link org.springframework.integration.ip.tcp.NioSocketReader#readFully()},
	 * using STX&lt;message&gt;ETX
	 */
	@Test
	public void testReadStxEtx() throws Exception {
		int port = SocketUtils.findAvailableServerSocket();
		ServerSocket server = ServerSocketFactory.getDefault().createServerSocket(port);
		SocketUtils.testSendStxEtx(port, null);
		Socket socket = server.accept();
		socket.setSoTimeout(5000);
		NetSocketReader reader = new NetSocketReader(socket);
		reader.setMessageFormat(MessageFormats.FORMAT_STX_ETX);
		if (reader.assembleData()) {
			assertEquals("Data", SocketUtils.TEST_STRING + SocketUtils.TEST_STRING, 
								 new String(reader.getAssembledData()));
		}
		else {
			fail("Failed to assemble first message");
		}
		if (reader.assembleData()) {
			assertEquals("Data", SocketUtils.TEST_STRING + SocketUtils.TEST_STRING, 
								 new String(reader.getAssembledData()));
		}
		else {
			fail("Failed to assemble second message");
		}
		server.close();
	}

	/**
	 * Test method for {@link org.springframework.integration.ip.tcp.NioSocketReader#readFully()},
	 * using STX&lt;message&gt;ETX
	 */
	@Test
	public void testReadCrLf() throws Exception {
		int port = SocketUtils.findAvailableServerSocket();
		ServerSocket server = ServerSocketFactory.getDefault().createServerSocket(port);
		SocketUtils.testSendCrLf(port, null);
		Socket socket = server.accept();
		socket.setSoTimeout(5000);
		NetSocketReader reader = new NetSocketReader(socket);
		reader.setMessageFormat(MessageFormats.FORMAT_CRLF);
		if (reader.assembleData()) {
			assertEquals("Data", SocketUtils.TEST_STRING + SocketUtils.TEST_STRING, 
								 new String(reader.getAssembledData()));
		}
		else {
			fail("Failed to assemble first message");
		}
		if (reader.assembleData()) {
			assertEquals("Data", SocketUtils.TEST_STRING + SocketUtils.TEST_STRING, 
								 new String(reader.getAssembledData()));
		}
		else {
			fail("Failed to assemble second message");
		}
		server.close();
	}

	@Test
	public void testReadLengthOverflow() throws Exception {
		int port = SocketUtils.findAvailableServerSocket();
		ServerSocket server = ServerSocketFactory.getDefault().createServerSocket(port);
		SocketUtils.testSendLengthOverflow(port);
		Socket socket = server.accept();
		socket.setSoTimeout(5000);
		NetSocketReader reader = new NetSocketReader(socket);
		try {
		    if (reader.assembleData()) {
		    	fail("Expected message length exceeded exception");
		    }
		} catch (IOException e) {
			if (!e.getMessage().startsWith("Message length")) {
				e.printStackTrace();
				fail("Unexpected IO Error:" + e.getMessage());
			}
		}
		server.close();
	}

	@Test
	public void testReadStxEtxTimeout() throws Exception {
		int port = SocketUtils.findAvailableServerSocket();
		ServerSocket server = ServerSocketFactory.getDefault().createServerSocket(port);
		SocketUtils.testSendStxEtxOverflow(port);
		Socket socket = server.accept();
		socket.setSoTimeout(500);
		NetSocketReader reader = new NetSocketReader(socket);
		reader.setMessageFormat(MessageFormats.FORMAT_STX_ETX);
		try {
		    if (reader.assembleData()) {
		    	fail("Expected message length exceeded exception");
		    }
		} catch (IOException e) {
			if (!e.getMessage().startsWith("Read timed out")) {
				e.printStackTrace();
				fail("Unexpected IO Error:" + e.getMessage());
			}
		}
		server.close();
	}

	@Test
	public void testReadStxEtxOverflow() throws Exception {
		int port = SocketUtils.findAvailableServerSocket();
		ServerSocket server = ServerSocketFactory.getDefault().createServerSocket(port);
		SocketUtils.testSendStxEtxOverflow(port);
		Socket socket = server.accept();
		socket.setSoTimeout(5000);
		NetSocketReader reader = new NetSocketReader(socket);
		reader.setMessageFormat(MessageFormats.FORMAT_STX_ETX);
		reader.setMaxMessageSize(1024);
		try {
		    if (reader.assembleData()) {
		    	fail("Expected message length exceeded exception");
		    }
		} catch (IOException e) {
			if (!e.getMessage().startsWith("ETX not found")) {
				e.printStackTrace();
				fail("Unexpected IO Error:" + e.getMessage());
			}
		}
		server.close();
	}

	@Test
	public void testReadCrLfTimeout() throws Exception {
		int port = SocketUtils.findAvailableServerSocket();
		ServerSocket server = ServerSocketFactory.getDefault().createServerSocket(port);
		SocketUtils.testSendCrLfOverflow(port);
		Socket socket = server.accept();
		socket.setSoTimeout(500);
		NetSocketReader reader = new NetSocketReader(socket);
		reader.setMessageFormat(MessageFormats.FORMAT_CRLF);
		try {
		    if (reader.assembleData()) {
		    	fail("Expected message length exceeded exception");
		    }
		} catch (IOException e) {
			if (!e.getMessage().startsWith("Read timed out")) {
				e.printStackTrace();
				fail("Unexpected IO Error:" + e.getMessage());
			}
		}
		server.close();
	}

	@Test
	public void testReadCrLfOverflow() throws Exception {
		int port = SocketUtils.findAvailableServerSocket();
		ServerSocket server = ServerSocketFactory.getDefault().createServerSocket(port);
		SocketUtils.testSendCrLfOverflow(port);
		Socket socket = server.accept();
		socket.setSoTimeout(5000);
		NetSocketReader reader = new NetSocketReader(socket);
		reader.setMessageFormat(MessageFormats.FORMAT_CRLF);
		reader.setMaxMessageSize(1024);
		try {
		    if (reader.assembleData()) {
		    	fail("Expected message length exceeded exception");
		    }
		} catch (IOException e) {
			if (!e.getMessage().startsWith("CRLF not found")) {
				e.printStackTrace();
				fail("Unexpected IO Error:" + e.getMessage());
			}
		}
		server.close();
	}

}