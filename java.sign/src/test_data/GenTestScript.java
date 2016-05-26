package test_data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;

import stored_keys.GetSigExistingKeys;
import stored_keys.LoadKeystore;

public class GenTestScript {

	public static void main(String args[]){
		
		String dataFile = "/home/varun/Documents/projects/GSoC/T1_testData/files/hello.js", 
				signFile = "/home/varun/Documents/projects/GSoC/T1_testData/signed_files/hello.js",
				pathToKeyStore = "/home/varun/keystore",
				keyStorePass = "",
				alias = "Varun Raval",
				privateKeyPass = "";
		
		GetSigExistingKeys gsek = new GetSigExistingKeys();

		KeyStore keyStore = LoadKeystore.load(pathToKeyStore, keyStorePass);
		
		byte[] realsig = null;
		try {
		
			realsig = gsek.sigUsingExistKeys(keyStore, alias, privateKeyPass, dataFile, signFile);
			
		} catch (UnrecoverableKeyException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException
				| NoSuchProviderException | SignatureException e) {

			e.printStackTrace();
		}

		String strSig = gsek.convertBytesToBase64(realsig);

		try(BufferedWriter brW = new BufferedWriter(new FileWriter(signFile))){

			BufferedReader br = new BufferedReader(new FileReader(dataFile));

			int c;
			while((c=br.read())!=-1){
				brW.append((char)c);
			}
			
			br.close();

			brW.append("\n\n");
			
			brW.append("/********BEGIN SIGNATURE********\n");
			brW.append(strSig);
			brW.append("\n********END SIGNATURE********/");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}