package key_at_runtime;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;

public class GenSig {

	public static void main(String args[]){

		String file;
		try{

			//User will input the file to be signed
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			file = br.readLine();
			
			br.close();
			
			KeyPairGenerator keyPairGenerator = 
					KeyPairGenerator.getInstance("DSA", "SUN");
			
			SecureRandom secureRandom = 
					SecureRandom.getInstance("SHA1PRNG", "SUN");
			
			keyPairGenerator.initialize(1024, secureRandom);

			KeyPair keypair = keyPairGenerator.generateKeyPair();
			PrivateKey privateKey = keypair.getPrivate();
			PublicKey publicKey = keypair.getPublic();
			
			System.out.println(privateKey.toString()+"\n");
			System.out.println(publicKey.toString());
			
			Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
			
			signature.initSign(privateKey);
			
			FileInputStream file_input = new FileInputStream(file);
			BufferedInputStream bf = new BufferedInputStream(file_input);
			
			byte buffer[] = new byte[1024];
			int len;
			
			while((len=bf.read(buffer)) >= 0){
				signature.update(buffer, 0, len);
			}
			
			bf.close();
			
			byte realsig[] = signature.sign();
			
			
			//file where signed content is stored
			FileOutputStream fout = new FileOutputStream("/home/varun/Documents/TODO_3.sgn");
			BufferedOutputStream bout = new BufferedOutputStream(fout);
			
			bout.write(realsig);
			
			bout.close();

			//file where public key is stored
			fout = new FileOutputStream("/home/varun/Documents/pub_key");
			fout.write(publicKey.getEncoded());
			fout.close();
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}