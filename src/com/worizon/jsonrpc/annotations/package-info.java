/**
 * This package contains annotations for the facade {@link com.worizon.jsonrpc.RpcImpl}. An interface 
 * in order to be exported and used as a remote procedure must be annotated with {@literal @}Remote annotation.
 * 
 * <p>
 * By default the remote procedure name that will be called with the class method's name.
 * This behaviour can be overriden with the {@literal @}ProcName annotation. This annotation lets you define a 
 * different name on which the remote call will be targeted.
 * 
 * <p>
 * Ex: 
 * <pre>
 * {@literal @}ProcName("get_users")
 * public void  getUsers(); getusers() ==> {jsonrpc:"2.0", method:"get_users", id:88678}
 * </pre>
 * 
 * <p>
 * The annotation {@literal @}RemoteParams is used for remote calls with named parameters, if this annotation is not
 * supplied then the invocation is performed with numbered parameters.
 * 
 * <p>
 * Ex:
 * 
 * <pre>
 * public int substract(int x, int y); substract(76,23); ==> {jsonrpc:"2.0", method:"substract", params:[76,23], id:88678}
 * 
 * {@literal @}RemoteParams({"x","y"})
 * public int sum(int x, int y); sum(76,23); ==> {jsonrpc:"2.0", method:"sum", params:{x:76,y:23}, id:88678}
 * </pre>
 * 
 * @author Enric Cecilla
 * @since 1.0.0 
 */
package com.worizon.jsonrpc.annotations;