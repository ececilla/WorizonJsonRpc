WorizonJsonRpc [![Build Status](https://travis-ci.org/ececilla/WorizonJsonRpc.png?branch=master)](https://travis-ci.org/ececilla/WorizonJsonRpc)
==============

Easy to use Java implementation to perform synchronous JSON-RPC 2.0 requests over HTTP. This project uses [Gson](http://code.google.com/p/google-gson/)
library to convert java objects to and from json objects.   

### JSON-RPC 2.0 specification
Most of the specification that this library implements can be found at [jsonrpc.org](http://www.jsonrpc.org/specification). There is a group that discusses the specification and related issues [here](https://groups.google.com/forum/#!forum/json-rpc).



### Usage
The library is intented to be used through the facade class *Rpc*. This class exposes two ways to perform an rpc request: 

* Proxy api.
+ Regular api.

---

The **proxy api** creates a stub object that exposes a local interface ,when indeed it's remote, so calls are pretended to be local. To use the proxy api, the Java interface that mimics the remote service MUST be annotated as *@Remote*. The proxy api uses a set of annotations to tweak the representation of the json request to fit your marshalling needs.

Following you can find several examples of the proxy api and it's related annotations:


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

You can invoke a different remote procedure name than local one, for this purpose you should use the *@RemoteProcName* annotation as in the example below:

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
The json object generated from this last example is as follows:

>*{...,method:"my_multiplication", params:{x:4,y:5}, id:62365}*. 

Finally, the proxy api let's you map error codes to local exception classes, this mapping is provided by the annotations *@LocalExceptions* and *@LocalException*: 

```java
@Remote
@LocalExceptions({@LocalException(code=-10,exception=DivideByZeroException.class)})
public interface MyCalculator{
    
    public double divide(double x, double y);
}

Rpc rpc = new Rpc("http://myhost.mydomain.com:4444/rpc");
MyCalculator calculator = rpc.createProxy(Myservice.class);
double result = calculator.divide(4,0);//Remote blocking call
```

---

The **regular api** is intented to be used as a delated object to which delegate the responsability to make remote calls. The regular api conforms a set of methods that differ each other on the expected return type:

+ callInteger: Call remote procedure with Integer as expected return type.
+ callIntegerArray: Call remote procedure with array of ints as expected return value.
+ callDouble: Call remote procedure with double as expected return type.
+ callDoubleArray: Call remote procedure with array of doubles as expected return value.
+ ...

The arguments to these methods are passed as varargs arguments. Following you can find a code snippet demonstrating this api usage:

```java
public class MyService{
    
    private Rpc delegate = new Rpc("http://myhost.mydomain.com:4444/rpc");    

    public MyService();

    public int task1(int x, int y){
        
        return delegate.callInteger("task1",x,y);
    }
    
    public void task2(){
        
        delegate.callVoid("task2");
    }
}
MyService service = new MyService();
service.task1(4,5)
service.task2();
```

The requests sent to the server would be as follows:

>*{...,method:"task1", params:[4,5], id:62369}*.

>*{...,method:"task2", id:62370}*.

As you can notice in the example above the parameters are encoded as an ordered list. To switch to a named-style, varargs arguments must be wrapped with a call to *Rpc.RemoteParam* in a all-or-none policy, otherwise and IllegalArgumentException is thrown.

```java
public class MyService{
    
    private Rpc delegate = new Rpc("http://myhost.mydomain.com:4444/rpc");    

    public MyService();

    public int[] task3(boolean x, double y){
        
        return delegate.callIntegerArray("task3",Rpc.RemoteParam("x",x),Rpc.RemoteParam("y",y));
    }    
}
MyService service = new MyService();
int result[] = service.task3(true,56.92);
```
>*{...,method:"task3", params:{x:true,y:56.92}, id:62369}*.


To use this library add the maven repository location and dependency to your pom.xml.

```xml
<dependencies>
	<dependency>
		<groupId>com.worizon</groupId>
		<artifactId>WorizonJsonRpc</artifactId>
		<version>1.0.0-SNAPSHOT </version>		
	</dependency>  
  </dependencies>
<repositories>
    <repository>
        <id>worizonjsonrpc-maven-s3-repo</id>
        <url>s3://ececilla-maven-com-worizon-jsonrpc-snapshot/snapshot</url>
    </repository>
</repositories>
```





