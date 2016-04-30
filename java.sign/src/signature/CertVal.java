package signature;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

//reads certificate from file or string
public class CertVal {

	Certificate cert;
	
	public CertVal(String cert, String type) throws IOException, CertificateException{
		
		BufferedInputStream buf_in = new BufferedInputStream(new ByteArrayInputStream(cert.getBytes()));
		getCert(buf_in, type);
	}
	
	public CertVal(File cert, String type) throws FileNotFoundException, IOException, CertificateException{
		BufferedInputStream buf_in = new BufferedInputStream(new FileInputStream(cert));
		getCert(buf_in, type);
	}
	
	private void getCert(BufferedInputStream buf_in, String type) throws IOException, CertificateException{
		
		CertificateFactory certificateFactory = CertificateFactory.getInstance(type);		//X.509
		
		while(buf_in.available()>0){
			cert = certificateFactory.generateCertificate(buf_in);
		}
	}
	
	//verify, isrootsigned, import to keystore, export from keystore, check validity

}
