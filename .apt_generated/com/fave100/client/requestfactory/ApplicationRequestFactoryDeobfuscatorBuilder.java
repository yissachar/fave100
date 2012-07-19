// Automatically Generated -- DO NOT EDIT
// com.fave100.client.requestfactory.ApplicationRequestFactory
package com.fave100.client.requestfactory;
import java.util.Arrays;
import com.google.web.bindery.requestfactory.vm.impl.OperationData;
import com.google.web.bindery.requestfactory.vm.impl.OperationKey;
public final class ApplicationRequestFactoryDeobfuscatorBuilder extends com.google.web.bindery.requestfactory.vm.impl.Deobfuscator.Builder {
{
withOperation(new OperationKey("ivJOvWAq6$29ItJ4N4qK878smig="),
  new OperationData.Builder()
  .withClientMethodDescriptor("()Lcom/google/web/bindery/requestfactory/shared/InstanceRequest;")
  .withDomainMethodDescriptor("()V")
  .withMethodName("remove")
  .withRequestContext("com.fave100.client.requestfactory.FaveItemRequest")
  .build());
withOperation(new OperationKey("6utjAQR3HGGVtfCaNTL$dvhbxQ8="),
  new OperationData.Builder()
  .withClientMethodDescriptor("()Lcom/google/web/bindery/requestfactory/shared/InstanceRequest;")
  .withDomainMethodDescriptor("()Lcom/fave100/server/domain/FaveItem;")
  .withMethodName("persist")
  .withRequestContext("com.fave100.client.requestfactory.FaveItemRequest")
  .build());
withOperation(new OperationKey("Fpm1058HQvb767D1qxlMOHtfzR4="),
  new OperationData.Builder()
  .withClientMethodDescriptor("(Ljava/lang/Long;)Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("(Ljava/lang/Long;)V")
  .withMethodName("removeFaveItem")
  .withRequestContext("com.fave100.client.requestfactory.FaveItemRequest")
  .build());
withOperation(new OperationKey("wwaxoVholihY4p9_IzA_W2_3szA="),
  new OperationData.Builder()
  .withClientMethodDescriptor("(Ljava/lang/Long;)Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("(Ljava/lang/Long;)Ljava/util/List;")
  .withMethodName("getAllFaveItemsForUser")
  .withRequestContext("com.fave100.client.requestfactory.FaveItemRequest")
  .build());
withOperation(new OperationKey("7p63a6sAZB$VlRSF$vbfhBf6eLk="),
  new OperationData.Builder()
  .withClientMethodDescriptor("(Ljava/lang/Long;)Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("(Ljava/lang/Long;)Lcom/fave100/server/domain/FaveItem;")
  .withMethodName("findFaveItem")
  .withRequestContext("com.fave100.client.requestfactory.FaveItemRequest")
  .build());
withOperation(new OperationKey("7fWTXndLKL3kxJKodxlpw7xPDjM="),
  new OperationData.Builder()
  .withClientMethodDescriptor("(Ljava/lang/String;)Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("(Ljava/lang/String;)Ljava/lang/String;")
  .withMethodName("getLoginLogoutURL")
  .withRequestContext("com.fave100.client.requestfactory.AppUserRequest")
  .build());
withOperation(new OperationKey("ygOtjJemSv4w0yubjG9wT19fdGc="),
  new OperationData.Builder()
  .withClientMethodDescriptor("()Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("()Lcom/fave100/server/domain/AppUser;")
  .withMethodName("findLoggedInAppUser")
  .withRequestContext("com.fave100.client.requestfactory.AppUserRequest")
  .build());
withOperation(new OperationKey("nD21tQVIDyzncjOoVyeX9oZ6PV0="),
  new OperationData.Builder()
  .withClientMethodDescriptor("()Lcom/google/web/bindery/requestfactory/shared/InstanceRequest;")
  .withDomainMethodDescriptor("()Lcom/fave100/server/domain/AppUser;")
  .withMethodName("persist")
  .withRequestContext("com.fave100.client.requestfactory.AppUserRequest")
  .build());
withOperation(new OperationKey("Jw6NYPjfNLifEbdY3OF3rv$oKXk="),
  new OperationData.Builder()
  .withClientMethodDescriptor("(Ljava/lang/Long;)Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("(Ljava/lang/Long;)Lcom/fave100/server/domain/AppUser;")
  .withMethodName("findAppUser")
  .withRequestContext("com.fave100.client.requestfactory.AppUserRequest")
  .build());
withRawTypeToken("fgtdGHxB5OEBQEtPsPS5V7TkHWs=", "com.fave100.client.requestfactory.AppUserProxy");
withRawTypeToken("82QpBIZ2Thda3j4h9ELHeAmdfBE=", "com.fave100.client.requestfactory.FaveItemProxy");
withRawTypeToken("w1Qg$YHpDaNcHrR5HZ$23y518nA=", "com.google.web.bindery.requestfactory.shared.EntityProxy");
withRawTypeToken("FXHD5YU0TiUl3uBaepdkYaowx9k=", "com.google.web.bindery.requestfactory.shared.BaseProxy");
withClientToDomainMappings("com.fave100.server.domain.AppUser", Arrays.asList("com.fave100.client.requestfactory.AppUserProxy"));
withClientToDomainMappings("com.fave100.server.domain.FaveItem", Arrays.asList("com.fave100.client.requestfactory.FaveItemProxy"));
}}
