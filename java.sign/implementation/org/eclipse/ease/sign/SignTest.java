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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SignTest {

	public static void main(String args[]) throws IOException{
		String pathToKeyStore, type, provider, keyStorePass, dataFile;
		boolean result;

		PerformSignature pSign = new PerformSignature();

		UserInteract userInteract = new UserInteract();

		// get file path from user
		dataFile = userInteract.getDataFile();
		do {
			result = false;
			// get keystore info from user
			pathToKeyStore = userInteract.getKeyStoreLocation();
			type = userInteract.getKeyStoreType();
			provider = userInteract.getPrefferedProvider();

			// check keystore exists
			try {
				pSign.existkeystore(pathToKeyStore, type, provider);

			} catch (ScriptSignatureException e) {
				System.out.println(e.getMessage());
				if(e.exitApplication())
					return;
			}

			// get ksystore password from user
			keyStorePass = userInteract.getKeyStorePass();

			// load keystore using keystore password
			try {
				result = pSign.loadKeyStore(keyStorePass);

			} catch (ScriptSignatureException e) {
				System.out.println(e.getMessage());
				if(e.exitApplication())
					return;
			}

			if (result)
				break;

		} while (true);

		do {
			try {
				result = false;
				// get list of all aliases available in keystore
				String vals[] = userInteract.getAlias((ArrayList<String>)pSign.getAliases());

				String certStr = pSign.getCertificate(vals[0], true);	//true to allow self-signed certificate

				String pass = userInteract.getPassword();
				String messageDigestAlgo = userInteract.getMessageDisgestAlgo();

				// perform signature
				String signStr = pSign.getSignature(new FileInputStream(dataFile), vals[0], pass, vals[1], messageDigestAlgo);

				HelperClass fHelperClass = new HelperClass();
				result = fHelperClass.appendSignature(signStr, certStr, new FileOutputStream(dataFile, true), "/*", "*/");

				if (result) {
					System.out.println("operation successful");
					return;
				}

			} catch (ScriptSignatureException e) {
				System.out.println(e.getMessage());
				if (e.exitApplication())
					return;
			}
		} while (true);
	}
}