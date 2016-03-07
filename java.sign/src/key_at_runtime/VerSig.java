package key_at_runtime;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

public class VerSig {

	public static void main(String args[]){
		
		try{
			//file where public key is stored
			FileInputStream keyfin = new FileInputStream("/home/varun/Documents/pub_key");
			byte[] enckey = new byte[keyfin.available()];
			
			keyfin.read(enckey);
			keyfin.close();
			
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(enckey);
			KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
			
			PublicKey publicKey = keyFactory.generatePublic(keySpec);
			
			//file where received signed content is stored
			FileInputStream fin = new FileInputStream("/home/varun/Documents/TODO_3.sgn");
			byte[] sgnToVerfiy = new byte[fin.available()];
			
			fin.read(sgnToVerfiy);
			
			fin.close();
			
			Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
			sig.initVerify(publicKey);
			
			//file which is received whose signed content is also obtained above
			FileInputStream datafis = new FileInputStream("/home/varun/Documents/TODO");
			BufferedInputStream buffin = new BufferedInputStream(datafis);
			
			byte[] buffer = new byte[1024];
			int len;
			while(buffin.available()!=0){
				
				len = buffin.read(buffer);
				sig.update(buffer, 0, len);
			}
			
			buffin.close();
			
			boolean verifies = sig.verify(sgnToVerfiy);
			
			System.out.println("Signature Verified "+verifies);
			
		} catch(Exception ex){
			
			
		}
	}
}
