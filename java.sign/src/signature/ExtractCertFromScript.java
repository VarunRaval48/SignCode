package signature;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import stored_keys.LoadKeystore;
import testing.CertToFromByte;

public class ExtractCertFromScript {

	public static void main(String args[]){
		
		String file = "/home/varun/Documents/projects/GSoC/T1_testData/signed_files/hello.js";
		Certificate c = new ExtractCertFromScript().getCert(file);
		System.out.println(c.toString());
	}
	
	Certificate getCert(String file){

		String start = "/********BEGIN SIGNATURE********";
		String stop = "";
		try(BufferedReader br = new BufferedReader(new FileReader(file))){
			
			while(br.ready() && !br.readLine().equals(start));

			br.readLine();
			br.readLine();

			String str;
			StringBuffer strB = new StringBuffer();
			while(br.ready() && !(str=br.readLine()).equals(stop)){

				strB.append(str);
			}

			str = strB.toString();

			System.out.println(str);

			byte[] certB = CertToFromByte.convertBase64ToBytes(str);
			
			CertVal certVal = new CertVal(certB, "X.509");
			return certVal.getCert();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (CertificateException e) {
			e.printStackTrace();
			return null;
		}
	}
}