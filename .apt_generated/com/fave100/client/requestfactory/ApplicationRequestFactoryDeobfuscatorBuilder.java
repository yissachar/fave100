// Automatically Generated -- DO NOT EDIT
// com.fave100.client.requestfactory.ApplicationRequestFactory
package com.fave100.client.requestfactory;
import java.util.Arrays;
import com.google.web.bindery.requestfactory.vm.impl.OperationData;
import com.google.web.bindery.requestfactory.vm.impl.OperationKey;
public final class ApplicationRequestFactoryDeobfuscatorBuilder extends com.google.web.bindery.requestfactory.vm.impl.Deobfuscator.Builder {
{
withOperation(new OperationKey("nqq21bQ$UCxWvNA3$LNnnk4LXPs="),
  new OperationData.Builder()
  .withClientMethodDescriptor("(Ljava/lang/Long;Lcom/fave100/client/requestfactory/SongProxy;)Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("(Ljava/lang/Long;Lcom/fave100/server/domain/Song;)V")
  .withMethodName("addFaveItemForCurrentUser")
  .withRequestContext("com.fave100.client.requestfactory.FaveItemRequest")
  .build());
withOperation(new OperationKey("lbCkliOjChfWyneuhn8yIUF0vIQ="),
  new OperationData.Builder()
  .withClientMethodDescriptor("()Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("()Ljava/util/List;")
  .withMethodName("getAllFaveItemsForCurrentUser")
  .withRequestContext("com.fave100.client.requestfactory.FaveItemRequest")
  .build());
withOperation(new OperationKey("rtLWIFQpdNzkh7MSEB1LlkrK_eA="),
  new OperationData.Builder()
  .withClientMethodDescriptor("(Ljava/lang/Long;)Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("(Ljava/lang/Long;)V")
  .withMethodName("removeFaveItemForCurrentUser")
  .withRequestContext("com.fave100.client.requestfactory.FaveItemRequest")
  .build());
withOperation(new OperationKey("KL3nZTK9Al0Mi9eCnN98nRw_YOA="),
  new OperationData.Builder()
  .withClientMethodDescriptor("(Ljava/lang/Long;)Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("(Ljava/lang/Long;)Lcom/fave100/server/domain/Song;")
  .withMethodName("findSong")
  .withRequestContext("com.fave100.client.requestfactory.SongRequest")
  .build());
withOperation(new OperationKey("DXmsaOWLsvKP_TYnC6XoIzo8CdU="),
  new OperationData.Builder()
  .withClientMethodDescriptor("()Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("()Lcom/fave100/server/domain/AppUser;")
  .withMethodName("getLoggedInAppUser")
  .withRequestContext("com.fave100.client.requestfactory.AppUserRequest")
  .build());
withOperation(new OperationKey("2FX0wJxxb8Ig88825qTzZ78tNgs="),
  new OperationData.Builder()
  .withClientMethodDescriptor("()Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("()Z")
  .withMethodName("isGoogleUserLoggedIn")
  .withRequestContext("com.fave100.client.requestfactory.AppUserRequest")
  .build());
withRawTypeToken("fgtdGHxB5OEBQEtPsPS5V7TkHWs=", "com.fave100.client.requestfactory.AppUserProxy");
withRawTypeToken("82QpBIZ2Thda3j4h9ELHeAmdfBE=", "com.fave100.client.requestfactory.FaveItemProxy");
withRawTypeToken("7ZIsY9ni4G4q5bH4OkFUe1ZsROs=", "com.fave100.client.requestfactory.SongProxy");
withRawTypeToken("w1Qg$YHpDaNcHrR5HZ$23y518nA=", "com.google.web.bindery.requestfactory.shared.EntityProxy");
withRawTypeToken("FXHD5YU0TiUl3uBaepdkYaowx9k=", "com.google.web.bindery.requestfactory.shared.BaseProxy");
withClientToDomainMappings("com.fave100.server.domain.AppUser", Arrays.asList("com.fave100.client.requestfactory.AppUserProxy"));
withClientToDomainMappings("com.fave100.server.domain.FaveItem", Arrays.asList("com.fave100.client.requestfactory.FaveItemProxy"));
withClientToDomainMappings("com.fave100.server.domain.Song", Arrays.asList("com.fave100.client.requestfactory.SongProxy"));
}}
