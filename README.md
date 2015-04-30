# SMTP Connection Pool

This library implements a SMTP connection pool using [Java Mail](https://java.net/projects/javamail/pages/Home) for the SMTP code and the 
[Apache Commons Pool](https://commons.apache.org/proper/commons-pool/) for the pool code.

The pool, thanks to the Apache library, supports common pool's features:

- Max total
- Min idle
- Eviction
- Test on borrow
- ...

 
 
# How to use it?

```java

//Declare the factory and the connection pool, usually at the application startup
SmtpConnectionPool smtpConnectionPool = new SmtpConnectionPool(SmtpConnectionFactoryBuilder.newSmtpBuilder().build());

//borrow an object in a try-with-resource statement or call `close` by yourself
try (ClosableSmtpConnection transport = smtpConnectionPool.borrowObject()) {
    MimeMessage mimeMessage = new MimeMessage(session);
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

Configure is holded by the Pool library, see the [Commons Pool Javadoc](https://commons.apache.org/proper/commons-pool/api-2.3/index.html). 

Example:
```java

//Create the configuration
GenericObjectPoolConfig config = new GenericObjectPoolConfig();
config.setMaxTotal(2);

//Declare the factory and the connection pool, usually at the application startup
SmtpConnectionPool smtpConnectionPool = new SmtpConnectionPool(SmtpConnectionFactoryBuilder.newSmtpBuilder().build(), config);

```

