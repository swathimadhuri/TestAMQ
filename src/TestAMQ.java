/**
 * Created by Ramababu.Bendalam on 28/09/2016.
 */
import java.util.Properties;
import javax.jms.*;
import javax.naming.*;

class Consumer {


    private String queueName = "adstream.yadn.nodes.LVO1V-GDNMARK0";
    private String user = "system";
    private String password = "manager";
    //private String url = "ssl://hostname:61616";
    private String url = "tcp://10.44.127.90:61616";

    private boolean transacted;
    private boolean isRunning = false;

    public static void main(String[] args) throws NamingException, JMSException {
        Consumer consumer = new Consumer();
        consumer.run();
    }

    public Consumer() {
        /** For SSL connections only, add the following: **/
//		System.setProperty("javax.net.ssl.keyStore", "path/to/client.ks");
//		System.setProperty("javax.net.ssl.keyStorePassword", "password");
//		System.setProperty("javax.net.ssl.trustStore", "path/to/client.ts");

    }

    public void run() throws NamingException, JMSException {
        isRunning = true;

        //JNDI properties
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, url);

        //specify queue propertyname as queue.jndiname
        props.setProperty("queue.slQueue", queueName);

        javax.naming.Context ctx = new InitialContext(props);
        ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
        Connection connection = connectionFactory.createConnection(user, password);
        connection.start();

        Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);

        Destination destination = (Destination) ctx.lookup("slQueue");
        //Using Message selector ObjectClass = ‘AlarmImpl’
        MessageConsumer consumer = session.createConsumer(destination);

        while (isRunning) {
            System.out.println("Waiting for message...");
            Message message = consumer.receive(1000);
            if (message != null && message instanceof TextMessage) {
                TextMessage txtMsg = (TextMessage) message;
                System.out.println("Received: " + txtMsg.getText());
            }
        }
        System.out.println("Closing connection");
        consumer.close();
        session.close();
        connection.close();
    }
}