package com.dumbster.smtp;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Dummy SMTP server for testing purposes.
 */
public class SmtpServer implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(SmtpServer.class);

  public static final int DEFAULT_SMTP_PORT = 25;
  private static final int SERVER_SOCKET_TIMEOUT = 0;
  private static final int MAX_THREADS = 10;

  private volatile MailStore mailStore;
  private volatile boolean stopped = true;
  private volatile boolean ready = false;
  private volatile boolean threaded = false;

  private ServerSocket serverSocket;
  private int port;
  private ThreadPoolExecutor threadExecutor;

  private Deque<ClientSession> clientSessions = new ConcurrentLinkedDeque();


  public void run() {
    stopped = false;
    try {
      initializeServerSocket();
      serverLoop();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      ready = false;
      if (serverSocket != null) {
        try {
          serverSocket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void initializeServerSocket() throws Exception {
    serverSocket = new ServerSocket(port);
    serverSocket.setSoTimeout(SERVER_SOCKET_TIMEOUT);
  }

  private void serverLoop() throws IOException {
    int poolSize = threaded ? MAX_THREADS : 1;
    threadExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);
    while (!isStopped()) {
      Socket clientSocket;
      try {
        clientSocket = clientSocket();
      } catch (IOException ex) {
        if (isStopped()) {
          break;
        } else {
          throw ex;
        }
      }
      SocketWrapper source = new SocketWrapper(clientSocket);
      ClientSession session = new ClientSession(this, source, mailStore);
      clientSessions.add(session);
      threadExecutor.execute(session);
    }
    threadExecutor.shutdown();
    ready = false;
  }

  private Socket clientSocket() throws IOException {
    Socket socket = null;
    while (socket == null) {
      socket = accept();
    }
    return socket;
  }

  private Socket accept() throws IOException {
    ready = true;
    return serverSocket.accept();
  }

  public boolean isStopped() {
    return stopped;
  }

  public synchronized void stop() {
    stopped = true;
    try {
      serverSocket.close();

    } catch (IOException e) {
      throw new SmtpServerException(e);
    }

    closeClientSessions();
  }

  public void closeClientSessions() {

    for (ClientSession clientSession : clientSessions) {
      try {
        clientSession.forceClose();
      } catch (IOException e) {
        LOG.error(e.getMessage(), e);
      }
    }
  }


  public void closeClientSession(ClientSession clientSession) {
    clientSessions.remove(clientSession);
  }

  public static class SmtpServerException extends RuntimeException {
    public SmtpServerException(Throwable cause) {
      super(cause);
    }
  }

  public MailMessage[] getMessages() {
    return mailStore.getMessages();
  }

  public MailMessage getMessage(int i) {
    return mailStore.getMessage(i);
  }

  public int getEmailCount() {
    return mailStore.getEmailCount();
  }

  public void anticipateMessageCountFor(int messageCount, int ticks) {
    int tickdown = ticks;
    while (mailStore.getEmailCount() < messageCount && tickdown > 0) {
      tickdown--;
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        return;
      }
    }
  }

  public boolean isReady() {
    return ready;
  }

  /**
   * Toggles if the SMTP server is single or multi-threaded for response to
   * SMTP sessions.
   */
  public void setThreaded(boolean threaded) {
    this.threaded = threaded;
  }

  public void setMailStore(MailStore mailStore) {
    this.mailStore = mailStore;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void clearMessages() {
    this.mailStore.clearMessages();
  }

  public Deque<ClientSession> getClientSessions() {
    return clientSessions;
  }
}
