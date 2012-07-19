package com.google.gwt.logging.shared;

import com.google.gwt.user.client.rpc.impl.RemoteServiceProxy;
import com.google.gwt.user.client.rpc.impl.ClientSerializationStreamWriter;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.impl.RequestCallbackAdapter.ResponseReader;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.RpcToken;
import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.core.client.impl.Impl;
import com.google.gwt.user.client.rpc.impl.RpcStatsContext;

public class RemoteLoggingService_Proxy extends RemoteServiceProxy implements com.google.gwt.logging.shared.RemoteLoggingServiceAsync {
  private static final String REMOTE_SERVICE_INTERFACE_NAME = "com.google.gwt.logging.shared.RemoteLoggingService";
  private static final String SERIALIZATION_POLICY ="5018A242D7F4A0AE3903FED1FB55D655";
  private static final com.google.gwt.logging.shared.RemoteLoggingService_TypeSerializer SERIALIZER = new com.google.gwt.logging.shared.RemoteLoggingService_TypeSerializer();
  
  public RemoteLoggingService_Proxy() {
    super(GWT.getModuleBaseURL(),
      "remote_logging", 
      SERIALIZATION_POLICY, 
      SERIALIZER);
  }
  
  public void logOnServer(java.util.logging.LogRecord record, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("RemoteLoggingService_Proxy", "logOnServer");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("java.util.logging.LogRecord/2492345967");
      streamWriter.writeObject(record);
      helper.finish(callback, ResponseReader.STRING);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  @Override
  public SerializationStreamWriter createStreamWriter() {
    ClientSerializationStreamWriter toReturn =
      (ClientSerializationStreamWriter) super.createStreamWriter();
    if (getRpcToken() != null) {
      toReturn.addFlags(ClientSerializationStreamWriter.FLAG_RPC_TOKEN_INCLUDED);
    }
    return toReturn;
  }
  @Override
  protected void checkRpcTokenType(RpcToken token) {
    if (!(token instanceof com.google.gwt.user.client.rpc.XsrfToken)) {
      throw new RpcTokenException("Invalid RpcToken type: expected 'com.google.gwt.user.client.rpc.XsrfToken' but got '" + token.getClass() + "'");
    }
  }
}
