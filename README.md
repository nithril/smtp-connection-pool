# SMTP Connection Pool

This library implements a SMTP connection pool using [Java Mail](https://java.net/projects/javamail/pages/Home) for the SMTP code and the 
[Apache Commons Pool](https://commons.apache.org/proper/commons-pool/) for the pool code.

The pool, thanks to the Apache library, supports most common pool features:

- Max total
- Min idle
- Eviction
- Test on borrow
- ...



# Maven dependency

[Maven configuration](http://search.maven.org/#search|ga|1|g%3A%22com.github.nithril%22%20a%3A%22smtp-connection-pool%22):
```xml
<dependency>
    <groupId>com.github.nithril</groupId>
    <artifactId>smtp-connection-pool</artifactId>
    <version>1.0.1</version>
</dependency>
```
 
 
# How to use the connection pool?

The `SmtpConnectionPool` creates a `Transport` using a `SmtpConnectionFactory`.

## Smtp Connection Factory

The factory can be created in some ways.
 
**If you already have a configured `Session`**
```java
SmtpConnectionFactory factory = SmtpConnectionFactories.newSmtpFactory(aSession);
```
JavaMail will retrieve the protocol, host, username... from the session


**You can build the factory using a builder**
```java
SmtpConnectionFactory factory = SmtpConnectionFactoryBuilder.newSmtpBuilder()
                .session(aSession)
                .protocol("smtp") 
                .host("mailer")
                .port(2525)
                .username("foo")
                .password("bar").build();
```

All builder parameters are optionals. JavaMail will fallback to the default configuration (smtp, port 25...)



**You can instanciate directly the factory**
```java
new SmtpConnectionFactory(aSession, aTransportStrategy, aConnectionStrategy);
```

Where:

- `TransportStrategy` allows to configure how the transport is got (default, protocol, url, provider)
- `ConnectionStrategy` allows to configure how the connection is made (default, username/password...)
 

## Smtp Connection Pool


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

