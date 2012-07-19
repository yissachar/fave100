package com.gwtplatform.mvp.client.proxy;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class TokenFormatException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, com.gwtplatform.mvp.client.proxy.TokenFormatException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.RuntimeException_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static com.gwtplatform.mvp.client.proxy.TokenFormatException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.gwtplatform.mvp.client.proxy.TokenFormatException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.gwtplatform.mvp.client.proxy.TokenFormatException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.RuntimeException_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.gwtplatform.mvp.client.proxy.TokenFormatException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.gwtplatform.mvp.client.proxy.TokenFormatException_FieldSerializer.deserialize(reader, (com.gwtplatform.mvp.client.proxy.TokenFormatException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.gwtplatform.mvp.client.proxy.TokenFormatException_FieldSerializer.serialize(writer, (com.gwtplatform.mvp.client.proxy.TokenFormatException)object);
  }
  
}
