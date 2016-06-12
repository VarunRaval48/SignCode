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
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

import signature.CertImport;
import stored_keys.GetSigExistingKeys;
import stored_keys.LoadKeystore;
import testing.CertToFromByte;

public class GenTestScript {
	
	public static void main(String args[]){
		
		String  f_name = "hello.js",
				language = "javascript",
				type = "Valid",
				dataFile = "/home/varun/Documents/projects/GSoC/T1_testData/files/"+f_name, 
				signFile = "TestScripts/"+type+"/"+language+"/"+f_name,
				pathToKeyStore = "/home/varun/keystore",
				keyStorePass = "",
				alias = "Varun Raval",
				privateKeyPass = "",
				begin = "/********BEGIN SIGNATURE********",
				end = "\n********END SIGNATURE********/\n";

		Certificate certificate = null;

		GetSigExistingKeys gsek = new GetSigExistingKeys();

		KeyStore keyStore = LoadKeystore.load(pathToKeyStore, keyStorePass);

		//perform signature and get Certificate in Certificate object from keystore
		byte[] realsig = null;
		try {

			realsig = gsek.sigUsingExistKeys(keyStore, alias, privateKeyPass, dataFile, signFile);

			CertImport certImport = new CertImport(keyStore);			
			certificate = certImport.importCert(alias);

		} catch (UnrecoverableKeyException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException
				| NoSuchProviderException | SignatureException e) {

			e.printStackTrace();
			return;
		}

		//convert signature to base64
		String strSig = CertToFromByte.convertBytesToBase64(realsig);

		//get certificate in bytes format
		byte[] certByte;
		try {
			certByte = certificate.getEncoded();
		} catch (CertificateEncodingException e1) {
			e1.printStackTrace();
			return;
		}

		//convert cert bytes to base64
		String strCert = CertToFromByte.convertBytesToBase64(certByte);

		//Appending Signature and Certificate
		try(BufferedWriter brW = new BufferedWriter(new FileWriter(signFile))){

			BufferedReader br = new BufferedReader(new FileReader(dataFile));

			int c;
			while((c=br.read())!=-1){
				brW.append((char)c);
				System.out.print(c+" ");
			}
			
			br.close();

			/* By default, last line in every file is ended by \n which is not visible directly.
			 * But if generated programmatically, it may not.
			 * To be sure, two \n characters are added.

			 * Fist to add \n if not already there and second to bring pointer to next line.
			 * This gives at most two empty lines and at least one empty line.
			 */
			brW.append("\n\n");

			String ext = (String)signFile.subSequence(signFile.lastIndexOf(".")+1, signFile.length());
			System.out.println(ext);

			//Each line is terminated by '\n'
			switch(ext){
		
			case "js":
				break;
		
			case "py":
				begin = "\"\"\"*****BEGIN SIGNSTURE********\n";
				end = "\n********END SIGNSTURE*****\"\"\"\n";
				break;
			}

			brW.append(begin);
			brW.append("\n");
			brW.append(strSig);
			brW.append("\n\n");

			int i = 0;
			for(String s: strCert.split("")){
				brW.append(s);
				i++;
				if(i%48 == 0)
					brW.append('\n');
			}

			brW.append(end);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
