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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import signature.SignatureInfo;

public class VerifySignature {

	private final boolean fContainSignature;
	private final String fHashAlgorithm, fProvider, fSignatureString, fCertificateChainString[], fContent;

	/**
	 * Use this constructor to use methods of this class.
	 *
	 * @param location
	 *            provide location of script to verify
	 * @throws ScriptSignatureException
	 *             when signature format is improper or
	 */
	public VerifySignature(final InputStream inputStream) throws ScriptSignatureException {

		SignatureInfo signatureInfo = SignatureHelper.getSignatureInfo(inputStream);
		if (signatureInfo != null) {
			fContainSignature = true;
			fHashAlgorithm = signatureInfo.getMessageDigestAlgo();
			fProvider = signatureInfo.getProvider();
			fSignatureString = signatureInfo.getSignature();
			fCertificateChainString = signatureInfo.getCertificateChain();
			fContent = signatureInfo.getContentOnly();

			if (fSignatureString == null || fProvider == null || fHashAlgorithm == null || fCertificateChainString == null || fContent == null)
				throw new ScriptSignatureException("Error while parsing script. Try again.");

		} else {

			fContainSignature = false;
			fContent = null;
			fHashAlgorithm = null;
			fProvider = null;
			fSignatureString = null;
			fCertificateChainString = null;
		}
	}

	/**
	 * Converts byte array to corresponding certificate.
	 *
	 * @param cert
	 *            provide byte array to convert
	 * @return certificate in form of {@link Certificate}
	 * @throws ScriptSignatureException
	 *             when there is an error while retrieving certificate
	 */
	private Certificate getCertificate(byte[] cert) throws ScriptSignatureException {

		CertificateFactory certificateFactory;
		try {
			certificateFactory = CertificateFactory.getInstance("X.509");
			return certificateFactory.generateCertificate(new ByteArrayInputStream(cert));

		} catch (CertificateException e) {
			throw new ScriptSignatureException("Error while retrieving certificate.", e);
		}
	}

	/**
	 * Converts certificate chain in form of string array to list.
	 *
	 * @return {@link List} of {@link Certificate}
	 * @throws ScriptSignatureException
	 *             when there is an error while retrieving certificate
	 */
	private List<Certificate> getCertificateChain() throws ScriptSignatureException {
		int noOfCert = fCertificateChainString.length;
		byte[][] certChainByte = new byte[noOfCert][];

		for (int i = 0; i < noOfCert; i++)
			certChainByte[i] = SignatureHelper.convertBase64ToBytes(fCertificateChainString[i]);

		ArrayList<Certificate> certificateList = new ArrayList<>();
		for (byte cert[] : certChainByte)
			certificateList.add(getCertificate(cert));

		return certificateList;
	}

	/**
	 * Checks whether certificate is self-signed or not.
	 *
	 * @return <code>true</code> if certificate is self-signed or <code>false</code> if certificate is CA signed
	 * @throws ScriptSignatureException
	 */
	public boolean isSelfSignedCertificate() throws ScriptSignatureException {
		if (fContainSignature) {
			try {
				ArrayList<Certificate> certificateList = (ArrayList<Certificate>) getCertificateChain();

				System.out.println(certificateList.get(0).toString());
				X509Certificate X509Cert = (X509Certificate)certificateList.get(0);
				System.out.println(X509Cert == null);

				X509Cert.verify(X509Cert.getPublicKey());
				return true;

			} catch (CertificateException e) {
				//	Logger.error(Activator.PLUGIN_ID, "Error while parsing certificate.", e);
				throw new ScriptSignatureException("Error while parsing certificate.", e);
			} catch (InvalidKeyException e) {
				e.printStackTrace();
				throw new ScriptSignatureException("Incorrect key.");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ScriptSignatureException("Error while parsing certificate.", e);
			} catch (NoSuchProviderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ScriptSignatureException("Error while parsing certificate.", e);
			} catch (SignatureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		throw new ScriptSignatureException("Script does not contain signature");
	}

	/**
	 * Checks the validity of certificate. If certificate is CA signed, then it checks the validity of CA with trust-store.
	 *
	 * @return <code>true</code> if certificate is valid and trusted or <code>false</code> if certificate is invalid or not trusted
	 * @throws ScriptSignatureException
	 */
	public boolean isCertChainValid() throws ScriptSignatureException {

		// TODO check for validity of certificate and if it is not self-signed check validity of CA

		if (fContainSignature) {
			try {
				CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

				ArrayList<Certificate> certificateList = (ArrayList<Certificate>) getCertificateChain();

				CertPath certPath = certificateFactory.generateCertPath(certificateList);
				CertPathValidator validator = CertPathValidator.getInstance("PKIX");

				KeyStore keystore = KeyStore.getInstance("JKS");
				// TODO allow keystore from other locations also
				//				InputStream is = new FileInputStream(System.getProperty("java.home")+"/lib/security/"+"cacerts");
				InputStream is = Files.newInputStream(Paths.get(System.getProperty("java.home")+"/lib/security/"+"cacerts"));
				keystore.load(is, "changeit".toCharArray());

				//				Collection<? extends CRL> crls;
				//				is = Files.newInputStream(Paths.get("crls.p7c"));
				//				crls = certificateFactory.generateCRLs(is);

				PKIXParameters params = new PKIXParameters(keystore);
				//				CertStore store = CertStore.getInstance("Collection", new CollectionCertStoreParameters(crls));
				/*
				 * If necessary, specify the certificate policy or other requirements with the appropriate params.setXXX() method.
				 */
				//				params.addCertStore(store);
				/* Validate will throw an exception on invalid chains. */
				PKIXCertPathValidatorResult r = (PKIXCertPathValidatorResult) validator.validate(certPath, params);
				return true;
			} catch (CertificateException e) {
				throw new ScriptSignatureException("No provider support certifiacte of this type");

			} catch (NoSuchAlgorithmException e) {

			} catch (KeyStoreException e) {
				// TODO handle this exception (but for now, at least know it happened)
				throw new RuntimeException(e);
			} catch (IOException e) {
				// TODO handle this exception (but for now, at least know it happened)
				throw new RuntimeException(e);

			}
			//			catch (CRLException e) {
			//				// TODO handle this exception (but for now, at least know it happened)
			//				throw new RuntimeException(e);
			//
			//			}
			catch (InvalidAlgorithmParameterException e) {
				// TODO handle this exception (but for now, at least know it happened)
				throw new RuntimeException(e);

			} catch (CertPathValidatorException e) {
				// TODO handle this exception (but for now, at least know it happened)
				throw new RuntimeException(e);

			}
			return false;
		}
		throw new ScriptSignatureException("Script does not contain signature");
	}

	/**
	 * Verify given signature with provided public key of provided certificate.
	 *
	 * @return <code>true</code> if signature is valid or <code>false</code> if signature is invalid
	 * @throws ScriptSignatureException
	 *             when script does not contain signature or there is an error while retrieving certificate
	 */
	public boolean verifySignature() throws ScriptSignatureException {

		if (fContainSignature) {
			byte[] signByte = SignatureHelper.convertBase64ToBytes(fSignatureString);
			byte[] certByte = SignatureHelper.convertBase64ToBytes(fCertificateChainString[0]);
			Certificate userCert = getCertificate(certByte);

			try {

				PublicKey publicKey = userCert.getPublicKey();
				String encryptionAlgo = publicKey.getAlgorithm();

				Signature signature = Signature.getInstance(fHashAlgorithm + "with" + encryptionAlgo, fProvider);

				signature.initVerify(publicKey);

				signature.update(fContent.getBytes());

				boolean verified = signature.verify(signByte);

				return verified;

			} catch (SignatureException e) {
				//				Logger.error(Activator.PLUGIN_ID, "Signature object not initialized properly or signature is not readable.", e);
				throw new ScriptSignatureException("Signature is not readable.", e);

			} catch (NoSuchAlgorithmException e) {
				throw new ScriptSignatureException("Algorithm used by signature is not recognized by provider.", e);

			} catch (InvalidKeyException e) {
				throw new ScriptSignatureException("Public key is invalid.", e);

			} catch (NoSuchProviderException e) {
				throw new ScriptSignatureException("No such provider is registered in Security Providers' list.", e);

			}
		}
		throw new ScriptSignatureException("Script does not contain signature.");
	}
}
