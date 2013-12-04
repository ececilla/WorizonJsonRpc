WorizonJsonRpc [![Build Status](https://travis-ci.org/ececilla/WorizonJsonRpc.png?branch=master)](https://travis-ci.org/ececilla/WorizonJsonRpc)
==============

Easy to use Java implementation to perform JSON-RPC 2.0 requests over HTTP. This project uses [Gson](http://code.google.com/p/google-gson/)
library to convert java objects to and from json objects.   

## JSON-RPC 2.0 specification
Most of the specification that this library implements can be found at [jsonrpc.org](http://www.jsonrpc.org/specification).



## Usage:
The library is intented to be used through the facade class *Rpc*. This class exposes two ways to perform an rpc request: 

* Proxy api.
+ Regular api.

The **proxy api** creates a stub object that exposes a local interface ,when indeed it's remote, so calls are pretended to be local. To use the proxy api, the Java interface that mimics the remote service MUST be annotated as *@Remote*. The proxy api uses a set of annotations to tweak the representation of the json request to fit your marshalling needs.

Following you can find an example of the proxy api and it's related annotations:


```java
@Remote
public interface MyCalculator{

    public int sum(int x, int y);
}

Rpc rpc = new Rpc("http://myhost.mydomain.com:4444/rpc");
MyCalculator calculator = rpc.createProxy(Myservice.class);
int result = calculator.sum(4,5);//Remote blocking call
```
The previous code snippet generates a remote call which passes arguments as an ordered list :

>*{...,method:"sum", params:[4,5], id:62364}*. 

In order to pass the arguments to the remote procedure as a json object, interface's methods must be annotated with *@RemoteParams*.


```java
@Remote
public interface MyCalculator{

    @RemoteParams({"x","y"})
    public int substract(int x, int y);
}

Rpc rpc = new Rpc("http://myhost.mydomain.com:4444/rpc");
MyCalculator calculator = rpc.createProxy(Myservice.class);
int result = calculator.substract(4,5);//Remote blocking call
```
The remote call generated from the last example is as follows:

>*{...,method:"substract", params:{x:4,y:5}, id:62365}*. 

You can have a different remote procedure name than local one, for this purpose you should use the *@RemoteProcName* annotation as in the example below:

```java
@Remote
public interface MyCalculator{
    
    @RemoteProcName("my_multiplication")
    @RemoteParams({"x","y"})
    public int multiply(int x, int y);
}

Rpc rpc = new Rpc("http://myhost.mydomain.com:4444/rpc");
MyCalculator calculator = rpc.createProxy(Myservice.class);
int result = calculator.multiply(4,5);//Remote blocking call
```
The son object generated from this last example is as follows:

>*{...,method:"my_multiplication", params:{x:4,y:5}, id:62365}*. 




