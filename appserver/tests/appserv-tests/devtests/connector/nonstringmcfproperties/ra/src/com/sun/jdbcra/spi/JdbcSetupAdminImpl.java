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

package com.sun.jdbcra.spi;

import javax.naming.*;
import javax.sql.*;
import java.sql.*;
// import javax.sql.DataSource;
public class JdbcSetupAdminImpl implements JdbcSetupAdmin {

    private String tableName;

    private String jndiName;

    private String schemaName;

    private Integer noOfRows;

    public void setTableName(String db) {
        tableName = db;
    }

    public String getTableName(){
        return tableName;
    }

    public void setJndiName(String name){
        jndiName = name;
    }

    public String getJndiName() {
        return jndiName;
    }

    public void setSchemaName(String name){
        schemaName = name;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setNoOfRows(Integer i) {
        System.out.println("Setting no of rows :" + i);
        noOfRows = i;
    }

    public Integer getNoOfRows() {
        return noOfRows;
    }

private void printHierarchy(ClassLoader cl, int cnt){
while(cl != null) {
	for(int i =0; i < cnt; i++) 
		System.out.print(" " );
	System.out.println("PARENT :" + cl);
	cl = cl.getParent();
	cnt += 3;
}
}

private void compareHierarchy(ClassLoader cl1, ClassLoader cl2 , int cnt){
while(cl1 != null || cl2 != null) {
        for(int i =0; i < cnt; i++)
                System.out.print(" " );
        System.out.println("PARENT of ClassLoader 1 :" + cl1);
	System.out.println("PARENT of ClassLoader 2 :" + cl2);
	System.out.println("EQUALS : " + (cl1 == cl2));
        cl1 = cl1.getParent();
	cl2 = cl2.getParent();
        cnt += 3;
}
}


    public boolean checkSetup(){

        if (jndiName== null || jndiName.trim().equals("")) {
	   return false;
	}

        if (tableName== null || tableName.trim().equals("")) {
	   return false;
	}

        Connection con = null;
	Statement s = null;
	ResultSet rs = null;
	boolean b = false;
        try {
	    InitialContext ic = new InitialContext();
	//debug
	Class clz = DataSource.class;
/*
	if(clz.getClassLoader() != null) {
		System.out.println("DataSource's clasxs : " +  clz.getName() +  " classloader " + clz.getClassLoader());
		printHierarchy(clz.getClassLoader().getParent(), 8);
	}
	Class cls = ic.lookup(jndiName).getClass();
	System.out.println("Looked up class's : " + cls.getPackage() + ":" + cls.getName()  + " classloader "  + cls.getClassLoader());
	printHierarchy(cls.getClassLoader().getParent(), 8);

	System.out.println("Classloaders equal ? " +  (clz.getClassLoader() == cls.getClassLoader()));
	System.out.println("&*&*&*&* Comparing Hierachy DataSource vs lookedup");
	if(clz.getClassLoader() != null) {
		compareHierarchy(clz.getClassLoader(), cls.getClassLoader(), 8);
	}

	System.out.println("Before lookup");
*/
	Object o = ic.lookup(jndiName);
//	System.out.println("after lookup lookup");

	    DataSource ds = (DataSource)o ;
/*
	System.out.println("after cast");
	System.out.println("---------- Trying our Stuff !!!");
	try {
		Class o1 = (Class.forName("com.sun.jdbcra.spi.DataSource"));
		ClassLoader cl1 = o1.getClassLoader();
		ClassLoader cl2 = DataSource.class.getClassLoader();
		System.out.println("Cl1 == Cl2" + (cl1 == cl2));
		System.out.println("Classes equal" + (DataSource.class == o1));
	} catch (Exception ex) {
		ex.printStackTrace();
	}
*/
            con = ds.getConnection();
	    String fullTableName = tableName;
	    if (schemaName != null && (!(schemaName.trim().equals("")))) {
	        fullTableName = schemaName.trim() + "." + fullTableName;
	    }
	    String qry = "select * from " + fullTableName; 

	    System.out.println("Executing query :" + qry);

	    s = con.createStatement();
	    rs = s.executeQuery(qry); 

            int i = 0;
	    if (rs.next()) {
	        i++;
	    }

            System.out.println("No of rows found:" + i);
            System.out.println("No of rows expected:" + noOfRows);

	    if (i == noOfRows.intValue()) {
	       b = true;
	    } else {
	       b = false;
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	    b = false;
	} finally {
	    try {
	        if (rs != null) rs.close();
	        if (s != null) s.close();
	        if (con != null) con.close();
            } catch (Exception e) {
	    }
	}
	System.out.println("Returning setup :" +b);
	return b;
    }

}
