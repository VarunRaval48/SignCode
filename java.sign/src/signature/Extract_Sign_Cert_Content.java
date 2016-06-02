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

public class Extract_Sign_Cert_Content {

	private String begin, stop, content;
	private BufferedReader br;

	public Extract_Sign_Cert_Content(String file, String f_type) throws IOException{

		br = new BufferedReader(new FileReader(file));
//		br.mark(0);
		
		content = null;

		begin = "/********BEGIN SIGNATURE********";
		
		String ext = (String)file.subSequence(file.lastIndexOf(".")+1, file.length());
		System.out.println(ext);

		switch(ext){

		case "js":
			break;

		case "py":
			begin = "\"\"\"*****BEGIN SIGNSTURE********";
			break;
		}

		stop = "";
	}

	public static void main(String args[]) throws IOException{

		String file = "/home/varun/Documents/projects/GSoC/T1_testData/signed_files/hello.js";

		Extract_Sign_Cert_Content extr = new Extract_Sign_Cert_Content(file, "js");

		String sign = extr.getSign();
		Certificate c = extr.getCert();
		extr.getContent();

		System.out.println();
		System.out.println(sign);
		System.out.println();
		System.out.println(c.toString());
	}

	public Certificate getCert(){

		try{
			br.readLine();

			String str;
			StringBuffer strB = new StringBuffer();
			while(br.ready() && !(str=br.readLine()).equals(stop)){

				strB.append(str);
			}

			str = strB.toString();

//			System.out.println(str);

			byte[] certB = CertToFromByte.convertBase64ToBytes(str);

			CertVal certVal = new CertVal(certB, "X.509");
			return certVal.getCert();
		} catch(IOException e){

			e.printStackTrace();
			return null;
		} catch (CertificateException e) {

			e.printStackTrace();
			return null;
		}
	}

	public String getSign(){

		StringBuffer sb = new StringBuffer();
		String temp, prev="";
		try{
			if(br.ready() && !(temp=br.readLine()).equals(begin))
				sb.append(temp);
//				prev = temp;
			
			if(br.ready() && !(temp=br.readLine()).equals(begin))
				prev = temp;
			while(br.ready() && !(temp=br.readLine()).equals(begin)){
//				sb.append("\n"+temp);
				sb.append("\n"+prev);
				prev = temp;
			}
			content = sb.toString();

			//Checks if empty line before signature is modified or not
			if(!prev.equals(""))
				throw new IOException();

			return br.readLine();
		} catch(IOException e){
			e.printStackTrace();
			System.out.println("Signature format Improper");
			return null;
		}
	}

	public String getContent(){

		if(content!=null){
			System.out.println(content);
			return content;
		}

		StringBuffer sb = new StringBuffer();
		String temp, prev="";

		try {
//			br.reset();

			if(br.ready() && !(temp=br.readLine()).equals(begin))
				sb.append(temp);			

			if(br.ready() && !(temp=br.readLine()).equals(begin))
				prev = temp;			
			while(br.ready() && !(temp=br.readLine()).equals(begin)){
//				sb.append("\n"+temp);
				sb.append("\n"+prev);
				prev = temp;
			}
			content = sb.toString();

			System.out.println(content);
			return content;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}		
	}
}