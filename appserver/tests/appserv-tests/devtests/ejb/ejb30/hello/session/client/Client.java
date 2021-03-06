/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.s1asdev.ejb.ejb30.hello.session.client;

import java.io.*;
import java.util.*;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import com.sun.s1asdev.ejb.ejb30.hello.session.*;
import com.sun.ejte.ccl.reporter.SimpleReporterAdapter;

public class Client {

    private static SimpleReporterAdapter stat = 
        new SimpleReporterAdapter("appserv-tests");

    public static void main (String[] args) {

        stat.addDescription("ejb-ejb30-hello-session");
        Client client = new Client(args);
        client.doTest();
        stat.printSummary("ejb-ejb30-hello-sessionID");
    }  
    
    public Client (String[] args) {
    }

    private static @EJB Sful sful;
    
    private static @EJB(mappedName="com.sun.s1asdev.ejb.ejb30.hello.session.Sless") Sless sless;

    //
    // NOTE: Token 3700 will be replaced in @EJB annotations below 
    // with the value of the port from config.properties during the build
    //
    private static @EJB(mappedName="corbaname:iiop:localhost:3700#com.sun.s1asdev.ejb.ejb30.hello.session.Sless") Sless sless2;

    private static @EJB(mappedName="corbaname:iiop:localhost:3700#java:global/ejb-ejb30-hello-sessionApp/ejb-ejb30-hello-session-ejb/SlessEJB!com.sun.s1asdev.ejb.ejb30.hello.session.Sless") Sless sless3;

    private static @EJB(mappedName="corbaname:iiop:localhost:3700#java:global/ejb-ejb30-hello-sessionApp/ejb-ejb30-hello-session-ejb/SlessEJB") Sless sless4;

    public void doTest() {

        try {

//            System.out.println("Creating InitialContext()");
//	    InitialContext ic = new InitialContext();

//	    org.omg.CORBA.ORB orb = (org.omg.CORBA.ORB) ic.lookup("java:comp/ORB"); 

//	    Sful sful = (Sful) ic.lookup("com.sun.s1asdev.ejb.ejb30.hello.session.Sful");
//	    Sless sless = (Sless) ic.lookup("com.sun.s1asdev.ejb.ejb30.hello.session.Sless");

            System.out.println("invoking stateful");
            sful.hello();

            System.out.println("invoking stateless");
            sless.hello();
            System.out.println("invoking stateless2");
	    sless2.hello();
            System.out.println("invoking stateless3");
	    sless3.hello();
            System.out.println("invoking stateless4");
	    sless4.hello();

            System.out.println("test complete");

            stat.addStatus("local main", stat.PASS);

        } catch(Exception e) {
            e.printStackTrace();
            stat.addStatus("local main" , stat.FAIL);
        }
        
    	return;
    }

}

