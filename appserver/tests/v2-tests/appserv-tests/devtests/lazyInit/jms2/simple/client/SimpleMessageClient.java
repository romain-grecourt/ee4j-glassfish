/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.sun.s1peqe.ejb.mdb.simple.client;

import javax.jms.*;
import javax.naming.*;
import com.sun.ejte.ccl.reporter.SimpleReporterAdapter;

public class SimpleMessageClient {
    
        Context                 jndiContext = null;
        QueueConnectionFactory  queueConnectionFactory = null;
        QueueConnection  queueConnection = null;
        QueueSession            queueSession = null;
        Queue                   queue = null;
        QueueSender             queueSender = null;
        TextMessage             message = null;
        QueueReceiver           receiver = null;
        final String  MSG_TEXT = new String("Here is a client-acknowledge message");
        public static final String  QUEUECONFAC = "jms/QCFactory";
        public static final String  QUEUE = "jms/SampleQueue";
        final int               NUM_MSGS = 3;
        private static boolean  allDone=false;
        
        private static SimpleReporterAdapter stat = 
        new SimpleReporterAdapter("appserv-tests");
        
        public static void main(String[] args) {
            
            stat.addDescription("This is to test simple "+
            "message driven bean sample.");
            SimpleMessageClient client=new SimpleMessageClient();
            client.setup();
            client.sendMessage();
            client.recvMessage();
            client.printReport();           
                        
        }
        
        public Object jndiLookup(String name) 
        throws NamingException {
        Object    obj = null;
        if (jndiContext == null) {
            try {
                jndiContext = new InitialContext();            
            } catch (NamingException e) {
                System.err.println("Could not create JNDI API " +
                    "context: " + e.toString());                
                throw e;
            }
        }
        try {
           obj = jndiContext.lookup(name);
        } catch (NamingException e) {            
            System.err.println("JNDI API lookup failed: " + 
                e.toString());
            throw e;
        }
        return obj;
    }
        
        public void setup(){
            try{
            queueConnectionFactory=(QueueConnectionFactory)jndiLookup(QUEUECONFAC);                                
            queue = (Queue)jndiLookup(QUEUE);                                  
            stat.addStatus("simple mdb jndiLookup", stat.PASS);
            }catch(Throwable e)
            {
                stat.addStatus("simple mdb jndiLookup", stat.FAIL);
                System.out.println("Problem in looking up connection factories");
                e.printStackTrace();
            }
            
        }
        
        

        
        public SimpleMessageClient(){}
        
        
        public void sendMessage(){
            try {
            queueConnectionFactory=(QueueConnectionFactory)jndiLookup(QUEUECONFAC);                    
            
            queue = (Queue)jndiLookup(QUEUE);                                  
       

            queueConnection =
                queueConnectionFactory.createQueueConnection();
            queueSession =
                queueConnection.createQueueSession(false,
                    Session.CLIENT_ACKNOWLEDGE);
            queueSender = queueSession.createSender(queue);
            message = queueSession.createTextMessage();
            message.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
            //System.out.println("Created durable queue subscriber,persistent delivery mode");

            for (int i = 0; i < NUM_MSGS; i++) {                
                message.setText(MSG_TEXT + (i + 1));
                if(i==(NUM_MSGS-1))
                    message.setStringProperty("MESSAGE_NUM","LAST");
                System.out.println("Sending message: " + message.getText());
                queueSender.send(message);                              
                Thread.sleep(1000);
            }
            System.out.println("Sent 3 messages,now sleeping");          
        } catch (Throwable e) {
            System.out.println("Exception occurred: " + e.toString());
            stat.addStatus("simple mdb main", stat.FAIL);
        } finally {
            System.out.println("In finally block of send message");
	    if (queueConnection !=null){
	    try{
	    queueConnection.close();
	    }catch(JMSException ex){
	    ex.printStackTrace();
	    }
	    }
           
            stat.addStatus("simple mdb sendmessage", stat.PASS);                     
        } // finally
    }          
        
        public static void printReport(){
            if(allDone)
            stat.printSummary("simpleMdbID");            
           else
                System.out.println("MessageStream from server not finished");
        }    
        
    
    
    
    public void recvMessage(){
        QueueConnection connect=null;
        /*
         * Create connection.
         * Create session from connection; false means session is
         * not transacted.
         * Create consumer, then start message delivery.
         * Receive all text messages from destination until
         * a 3 messages are received indicating end of
         * message stream.
         * Close connection.
         */
        System.out.println("********************************");
	System.out.println("inside recvMessage of jms2 appclient");
        try {
            connect = queueConnectionFactory.createQueueConnection();
            QueueSession session = connect.createQueueSession(false,0);
            receiver=session.createReceiver(queue);  
            System.out.println("Started Receiver");
            connect.start();
            int msgcount=1;
            while (true) {                
                Message m = receiver.receive(10000);
                System.out.println("Bingo!. got a ack msg back from server");
                msgcount++;
                System.out.println("COUNT :"+msgcount);
                if (m != null) {
                    if (m instanceof TextMessage) {
                        message = (TextMessage) m;
                        System.out.println("Reading message: " +
                            message.getText());
                        String props=message.getStringProperty("MESSAGE_NUM");
                    } else {
                        break;
                        
                    }
                }
                if(msgcount>=3){
                System.out.println("All messages from server recieved******************");
                stat.addStatus("simple jms2 recvmessage", stat.PASS);
                break;
                }
            }
            System.out.println("******************");
            System.out.println("Messages from Queue finished**");
            System.out.println("******************");
        } catch (JMSException e) {
            System.out.println("Exception occurred: " + 
                e.toString());
            stat.addStatus("simple jms2 recvmessage", stat.FAIL);
        } catch(Throwable e){
            e.printStackTrace();
        }
        finally {
            if (connect != null) {
                try {
                    connect.close();
                } catch (JMSException e) {}
            }
        }
        allDone=true;
    }
    }




