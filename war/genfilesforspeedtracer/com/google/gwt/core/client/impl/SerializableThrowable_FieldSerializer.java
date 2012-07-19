package com.google.gwt.core.client.impl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SerializableThrowable_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native com.google.gwt.core.client.impl.SerializableThrowable getCause(com.google.gwt.core.client.impl.SerializableThrowable instance) /*-{
    return instance.@com.google.gwt.core.client.impl.SerializableThrowable::cause;
  }-*/;
  
  private static native void setCause(com.google.gwt.core.client.impl.SerializableThrowable instance, com.google.gwt.core.client.impl.SerializableThrowable value) 
  /*-{
    instance.@com.google.gwt.core.client.impl.SerializableThrowable::cause = value;
  }-*/;
  
  private static native java.lang.String getMessage(com.google.gwt.core.client.impl.SerializableThrowable instance) /*-{
    return instance.@com.google.gwt.core.client.impl.SerializableThrowable::message;
  }-*/;
  
  private static native void setMessage(com.google.gwt.core.client.impl.SerializableThrowable instance, java.lang.String value) 
  /*-{
    instance.@com.google.gwt.core.client.impl.SerializableThrowable::message = value;
  }-*/;
  
  private static native java.lang.StackTraceElement[] getStackTrace(com.google.gwt.core.client.impl.SerializableThrowable instance) /*-{
    return instance.@com.google.gwt.core.client.impl.SerializableThrowable::stackTrace;
  }-*/;
  
  private static native void setStackTrace(com.google.gwt.core.client.impl.SerializableThrowable instance, java.lang.StackTraceElement[] value) 
  /*-{
    instance.@com.google.gwt.core.client.impl.SerializableThrowable::stackTrace = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.google.gwt.core.client.impl.SerializableThrowable instance) throws SerializationException {
    setCause(instance, (com.google.gwt.core.client.impl.SerializableThrowable) streamReader.readObject());
    setMessage(instance, streamReader.readString());
    setStackTrace(instance, (java.lang.StackTraceElement[]) streamReader.readObject());
    
  }
  
  public static com.google.gwt.core.client.impl.SerializableThrowable instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.google.gwt.core.client.impl.SerializableThrowable();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.google.gwt.core.client.impl.SerializableThrowable instance) throws SerializationException {
    streamWriter.writeObject(getCause(instance));
    streamWriter.writeString(getMessage(instance));
    streamWriter.writeObject(getStackTrace(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.google.gwt.core.client.impl.SerializableThrowable_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.google.gwt.core.client.impl.SerializableThrowable_FieldSerializer.deserialize(reader, (com.google.gwt.core.client.impl.SerializableThrowable)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.google.gwt.core.client.impl.SerializableThrowable_FieldSerializer.serialize(writer, (com.google.gwt.core.client.impl.SerializableThrowable)object);
  }
  
}
