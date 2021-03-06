/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.enterprise.tools.verifier.tests.ejb.entity.ejbcreatemethod;

import com.sun.enterprise.tools.verifier.Result;
import com.sun.enterprise.tools.verifier.Verifier;
import com.sun.enterprise.tools.verifier.VerifierTestContext;
import com.sun.enterprise.tools.verifier.tests.ComponentNameConstructor;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbCheck;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbTest;
import org.glassfish.ejb.deployment.descriptor.EjbDescriptor;
import org.glassfish.ejb.deployment.descriptor.EjbEntityDescriptor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**  
 * Entity Bean's ejbCreate(...) methods public test.
 * Each entity Bean class may define zero or more ejbCreate(...) methods. 
 * The number and signatures of a entity Bean's create methods are specific 
 * to each EJB class. The method signatures must follow these rules: 
 * 
 * The method name must be ejbCreate. 
 *
 * The method must be declared as public. 
 *
 */
public class EjbCreateMethodPublic extends EjbTest implements EjbCheck { 


    /** 
     * Entity Bean's ejbCreate(...) methods public test.
     * Each entity Bean class may define zero or more ejbCreate(...) methods. 
     * The number and signatures of a entity Bean's create methods are specific 
     * to each EJB class. The method signatures must follow these rules: 
     * 
     * The method name must be ejbCreate. 
     *
     * The method must be declared as public. 
     *
     * @param descriptor the Enterprise Java Bean deployment descriptor
     *   
     * @return <code>Result</code> the results for this assertion
     */
    public Result check(EjbDescriptor descriptor) {

	Result result = getInitializedResult();
	ComponentNameConstructor compName = getVerifierContext().getComponentNameConstructor();

	if (descriptor instanceof EjbEntityDescriptor) {
	    boolean oneFailed = false;
	    int foundAtLeastOne = 0;
	    try {
		VerifierTestContext context = getVerifierContext();
		ClassLoader jcl = context.getClassLoader();
		Class c = Class.forName(descriptor.getEjbClassName(), false, getVerifierContext().getClassLoader());

		boolean ejbCreateFound = false;
		boolean isPublic = false;
                // start do while loop here....
                do {
		    Method [] methods = c.getDeclaredMethods();
		    for (int i = 0; i < methods.length; i++) {
			// reset flags from last time thru loop
			ejbCreateFound = false;
			isPublic = false;

			// The method name must be ejbCreate. 
			if (methods[i].getName().startsWith("ejbCreate")) {
			    foundAtLeastOne++;
			    ejbCreateFound = true;

			    // The method must be declared as public. 
			    int modifiers = methods[i].getModifiers();
			    if (Modifier.isPublic(modifiers)){
				isPublic = true;
			    }

			    // now display the appropriate results for this particular ejbCreate
			    // method
			     if (ejbCreateFound && !isPublic) {
				oneFailed = true;
				result.addErrorDetails(smh.getLocalString
						  ("tests.componentNameConstructor",
						   "For [ {0} ]",
						   new Object[] {compName.toString()}));
				result.addErrorDetails(smh.getLocalString
						       (getClass().getName() + ".debug1",
							"For EJB Class [ {0} ] method [ {1} ]",
							new Object[] {descriptor.getEjbClassName(),methods[i].getName()}));
				result.addErrorDetails(smh.getLocalString
						       (getClass().getName() + ".failed",
							"Error: An [ {0} ] method was found, but was not public.",
							new Object[] {methods[i].getName()}));
				break;
			    } 
			}
		    }
		    if (oneFailed == true)
			break;
                } while (((c = c.getSuperclass()) != null) && (foundAtLeastOne == 0));
        
		if (foundAtLeastOne == 0){
		    result.addNaDetails(smh.getLocalString
						  ("tests.componentNameConstructor",
						   "For [ {0} ]",
						   new Object[] {compName.toString()}));
		    result.notApplicable(smh.getLocalString
					 (getClass().getName() + ".notApplicable1",
					  "[ {0} ] does not declare any ejbCreate(...) methods.",
					  new Object[] {descriptor.getEjbClassName()}));
		}
		if (oneFailed == false && foundAtLeastOne > 0) {
		    result.addGoodDetails(smh.getLocalString
						  ("tests.componentNameConstructor",
						   "For [ {0} ]",
						   new Object[] {compName.toString()}));
		    result.addGoodDetails(smh.getLocalString
					  (getClass().getName() + ".debug1",
					   "For EJB Class [ {0} ]",
					   new Object[] {descriptor.getEjbClassName()}));
		    result.addGoodDetails(smh.getLocalString
					  (getClass().getName() + ".passed",
					   "[ {0} ] properly declares public ejbCreate<method> method.",
					   new Object[] {descriptor.getEjbClassName()}));
		}
	    } catch (ClassNotFoundException e) {
		Verifier.debug(e);
		result.addErrorDetails(smh.getLocalString
				       ("tests.componentNameConstructor",
					"For [ {0} ]",
					new Object[] {compName.toString()}));
		result.failed(smh.getLocalString
			      (getClass().getName() + ".failedException",
			       "Error: [ {0} ] class not found.",
			       new Object[] {descriptor.getEjbClassName()}));
		oneFailed = true;
	    }  

            if (oneFailed) {
                result.setStatus(result.FAILED);
            } else if (foundAtLeastOne == 0) {
                result.setStatus(result.NOT_APPLICABLE);
            } else {
	
                result.setStatus(result.PASSED);
            }

	    return result;
 
	} else {
	    result.addNaDetails(smh.getLocalString
						  ("tests.componentNameConstructor",
						   "For [ {0} ]",
						   new Object[] {compName.toString()}));
	    result.notApplicable(smh.getLocalString
				 (getClass().getName() + ".notApplicable",
				  "[ {0} ] expected {1} bean, but called with {2} bean.",
				  new Object[] {getClass(),"Entity","Session"}));
	    return result;
	}
    }
}
