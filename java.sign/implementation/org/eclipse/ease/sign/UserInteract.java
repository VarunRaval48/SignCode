/*******************************************************************************
 * Copyright (c) 2016 varun and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     varun - initial API and implementation
 *******************************************************************************/

package org.eclipse.ease.sign;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;

/**
 * Following class is used to interact with user to perform signature.
 *
 */
public class UserInteract {

	BufferedReader br;
	public UserInteract() {
		br = new BufferedReader(new InputStreamReader(System.in));
	}

	String getDataFile() throws IOException {

		System.out.print("Enter datafile Location: ");
		return br.readLine();
	}

	String getKeyStoreLocation() throws IOException {

		System.out.println();
		System.out.print("Enter KeyStore Location: ");
		return br.readLine();
	}

	String getKeyStoreType() throws IOException {

		System.out.println();
		System.out.println("Chose keystore type");
		System.out.println("1. Default\n2. Custom");

		System.out.print("Enter Choice(1/2): ");
		int inp = Integer.parseInt(br.readLine());

		if(inp == 1)
			return "Default";
		else {

			System.out.print("Enter Your type: ");
			return br.readLine();
		}
	}

	String getPrefferedProvider() throws IOException {

		int i=2, inp;

		System.out.println();
		System.out.println("Chose Provider");
		System.out.println();

		System.out.println("1. Preferred by System");

		Provider[] providers = Security.getProviders();
		for(Provider provider: providers) {
			System.out.println(i+". "+provider.getName());
			i++;
		}

		System.out.println();
		System.out.print("Enter Choice: ");
		inp = Integer.parseInt(br.readLine());

		if(inp == 1)
			return "Preferred";

		return providers[inp-2].getName();
	}

	String getKeyStorePass() throws IOException {

		System.out.print("Enter KeyStore Password: ");
		return br.readLine();
	}

	String[] getAlias(ArrayList<String> aliases) throws IOException {

		String vals[] = new String[3];

		System.out.println();
		System.out.println("Available aliases");
		int i=1;
		for(String alias: aliases) {

			System.out.println(i+". "+alias);
			i++;
		}

		do {
			try {
				System.out.print("Choose alias: ");		
				vals[0] = aliases.get(Integer.parseInt(br.readLine())-1);

				break;
			} catch(IndexOutOfBoundsException e) {
				System.out.println("Invalid Entry");
			}
		} while(true);

		System.out.print("Enter password: ");
		vals[1] = br.readLine();

		vals[2] = getPrefferedProvider();

		return vals;
	}
}