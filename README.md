# SMTP Connection Pool

This library implements a SMTP connection pool using [Java Mail](https://java.net/projects/javamail/pages/Home) for the SMTP code and the 
[Apache Commons Pool](https://commons.apache.org/proper/commons-pool/) for the pool code.

The pool, thanks to the Apache library, supports most common pool features:

- Max total
- Min idle
- Eviction
- Test on borrow
- ...

 
 
# How to use the connection pool?

[Maven configuration](http://search.maven.org/#artifactdetails|com.github.nithril|smtp-connection-pool|1.0.0|jar):
```xml
<dependency>
    <groupId>com.github.nithril</groupId>
    <artifactId>smtp-connection-pool</artifactId>
    <version>1.0.0</version>
</dependency>
```


Java code:
```java

//Declare the factory and the connection pool, usually at the application startup
SmtpConnectionPool smtpConnectionPool = new SmtpConnectionPool(SmtpConnectionFactoryBuilder.newSmtpBuilder().build());

//borrow an object in a try-with-resource statement or call `close` by yourself
try (ClosableSmtpConnection transport = smtpConnectionPool.borrowObject()) {
    MimeMessage mimeMessage = new MimeMessage(transport.getSession());
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false);
    mimeMessageHelper.addTo("to@example.com");
    mimeMessageHelper.setFrom("from@example.com");
    mimeMessageHelper.setSubject("Hi!");
    mimeMessageHelper.setText("Hello World!", false);
    transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
}

//Close the pool, usually when the application shutdown
smtpConnectionPool.close();

```

# How to configure the pool?

Configuration is held by the Pool code, see the [Commons Pool Javadoc](https://commons.apache.org/proper/commons-pool/api-2.3/index.html). 

Example:
```java

//Create the configuration
GenericObjectPoolConfig config = new GenericObjectPoolConfig();
config.setMaxTotal(2);

//Declare the factory and the connection pool, usually at application startup
SmtpConnectionPool smtpConnectionPool = new SmtpConnectionPool(SmtpConnectionFactoryBuilder.newSmtpBuilder().build(), config);

```

