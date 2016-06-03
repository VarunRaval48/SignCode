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

public class GenTestScript {

	
	public static void main(String args[]){
		
		String  f_name = "03 XML handling.js",
				language = "javascript",
				type = "Valid",
				dataFile = "/home/varun/Documents/projects/GSoC/T1_testData/files/"+f_name, 
				signFile = "TestScripts/"+type+"/"+language+"/"+f_name,
				pathToKeyStore = "/home/varun/keystore",
				keyStorePass = "",
				alias = "Varun Raval",
				privateKeyPass = "",
				begin = "/********BEGIN SIGNATURE********\n",
				end = "\n********END SIGNATURE********/";

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
		String strSig = gsek.convertBytesToBase64(realsig);

		//get certificate in bytes format
		byte[] certByte;
		try {
			certByte = certificate.getEncoded();
		} catch (CertificateEncodingException e1) {
			e1.printStackTrace();
			return;
		}

		//convert cert bytes to base64
		String strCert = gsek.convertBytesToBase64(certByte);

		//Appending Signature and Certificate
		try(BufferedWriter brW = new BufferedWriter(new FileWriter(signFile))){

			BufferedReader br = new BufferedReader(new FileReader(dataFile));

			int c;
			while((c=br.read())!=-1){
				brW.append((char)c);
			}
			
			br.close();

			//To enter new line after last line of script and bring pointer to next line
			brW.append("\n\n");

			String ext = (String)signFile.subSequence(signFile.lastIndexOf(".")+1, signFile.length());
			System.out.println(ext);

			//Each line is terminated by '\n'
			switch(ext){
		
			case "js":
				break;
		
			case "py":
				begin = "\"\"\"*****BEGIN SIGNSTURE********\n";
				end = "\n********END SIGNSTURE*****\"\"\"";
				break;
			}

			brW.append(begin);
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
