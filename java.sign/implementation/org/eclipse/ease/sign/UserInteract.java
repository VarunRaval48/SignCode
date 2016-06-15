/*******************************************************************************
 * Copyright (c) 2016 Varun Raval and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Varun Raval - initial API and implementation
 *******************************************************************************/

package org.eclipse.ease.sign;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;

/**
 * Sample class to show implementation of user-interaction.
 * This user-interaction is using cmd and this will be replaced
 * by UI soon.
 */
public class UserInteract {

	BufferedReader br;
	public UserInteract() {
		br = new BufferedReader(new InputStreamReader(System.in));
	}

	String getDataFile() throws IOException{
		File f;
		do {
			System.out.print("Enter datafile Location: ");
			String file = br.readLine();
			f = new File(file);
			if (f.isDirectory())
				System.out.println("It's a directory");
			else if (f.exists())
				return file;
			else
				System.out.println("File does not exists");
		}
		while (true);
	}

	String getKeyStoreLocation() throws IOException {
		System.out.print("Enter KeyStore Location: ");
		return br.readLine();
	}

	String getKeyStoreType() throws IOException {
		System.out.println("Chose keystore type");
		System.out.println("1. Default\n2. Custom");

		System.out.print("Enter Choice(1/2): ");
		int inp = Integer.parseInt(br.readLine());

		if (inp == 1)
			return "Default";
		else {
			System.out.print("Enter Your type: ");
			return br.readLine();
		}
	}

	String getPrefferedProvider() throws IOException {
		int i=2, inp;

		System.out.println("Chose Provider");
		System.out.println();

		System.out.println("1. Preferred by System");

		Provider[] providers = Security.getProviders();
		for (Provider provider: providers) {
			System.out.println(i+". "+provider.getName());
			i++;
		}

		System.out.println();
		System.out.print("Enter Choice: ");
		inp = Integer.parseInt(br.readLine());

		if (inp == 1)
			return "Preferred";

		return providers[inp-2].getName();
	}

	String getKeyStorePass() throws IOException {
		System.out.print("Enter KeyStore Password: ");
		return br.readLine();
	}

	String getMessageDisgestAlgo() throws IOException {
		String list[] = {"Default", "MD2", "MD5", "SHA1", "SHA256","SHA384", "SHA512"}, algo;

		do {
			try {
				System.out.println("\nChose Message Digest Algo:");
				for(int i=0; i<list.length; i++){
					System.out.println((i+1)+". "+list[i]);
				}

				System.out.print("Enter Choice: ");
				algo = list[Integer.parseInt(br.readLine())-1];

				break;

			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Invalid Entry");
			} catch (NumberFormatException e) {
				System.out.println("Invalid Entry");
			}
		} while (true);

		return algo;
	}

	String[] getAlias(ArrayList<String> aliases) throws IOException {
		String vals[] = new String[2];
		int i=1;
		System.out.println("\n Aliases available:");
		for(String alias: aliases){

			System.out.println(i+". "+alias);
			i++;
		}

		do {
			try {
				System.out.print("Choose alias: ");
				vals[0] = aliases.get(Integer.parseInt(br.readLine())-1);

				break;

			} catch (IndexOutOfBoundsException e){
				System.out.println("Invalid Entry");

			} catch (NumberFormatException e){
				System.out.println("Invalid Entry");
			}
		} while (true);

		vals[1] = getPrefferedProvider();

		return vals;
	}

	String getPassword() throws IOException {
		System.out.print("Enter password: ");
		return br.readLine();
	}
}