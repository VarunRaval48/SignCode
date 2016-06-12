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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class shows how to use core methods with user interaction.
 * For now, as user interaction, console is used whose implementation is in UserInteract.java class.
 * In future, this command line will be replaced by series of dialog boxes.
 * 
 * In below implementation, consider calls to System.out.println to be replaced by call to warning dialog box.
 *
 */
public class SignTest {

	public static void main(String args[]) throws IOException{

		String pathToKeyStore, type, provider, keyStorePass, dataFile;
		int res;

		PerformSignature pSign = new PerformSignature();		
		UserInteract userInteract = new UserInteract();

		do{
			File f;

			/* get file path from user */
			dataFile = userInteract.getDataFile();

			f = new File(dataFile);
			if(f.exists()){
				if(f.isDirectory())
					System.out.println("It's a directory");
				else
					break;
			}
			else
				System.out.println("File does not exists");
		}while(true);

		do{
			/* get keystore info from user */
			pathToKeyStore = userInteract.getKeyStoreLocation();
			type = userInteract.getKeyStoreType();
			provider = userInteract.getPrefferedProvider();

			/* check keystore exists */
			res = pSign.keystoreExists(pathToKeyStore, type, provider);
			if(res == HelperClass.DONE)
				break;
			System.out.println(HelperClass.getErrorInfo(res));
		}while(true);

		do{
			/* get ksystore password from user */
			keyStorePass = userInteract.getKeyStorePass();

			/* load keystore using keystore password */
			res = pSign.keyStoreLoad(keyStorePass);

			/* checks result */
			if(res == HelperClass.DONE)
				break;
			System.out.println(HelperClass.getErrorInfo(res));
			if(res == HelperClass.CERT_NOT_LOADED)
				return;
			else if(res == HelperClass.EXCEPTION)
				return;
		}while(true);

		do{
			/* get list of all aliases available in keystore*/
			ArrayList<String> aliases = pSign.getAliases();
			if(aliases==null){
				System.out.println("Error retrieving aliases. Try Again");
				return;
			}
			String vals[] = userInteract.getAlias(aliases);

			/* perform signature */
			res = pSign.sign(dataFile, vals[0], vals[1], vals[2]);

			/* analyze result */
			if(res == HelperClass.DONE)
				break;
			System.out.println(HelperClass.getErrorInfo(res));
			if(res == HelperClass.TRY_AGAIN_ERROR || res == HelperClass.KEYSTORE_NOT_LOADED)
				return;
		}while(true);		
	}
}