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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.activation.Activator;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

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
	 *             when signature format is improper
	 */
	public VerifySignature(final String location) throws ScriptSignatureException {

		SignatureInfo signatureInfo = SignatureHelper.getSignatureInfo(location);
		if (signatureInfo != null) {
			fContainSignature = true;
			fHashAlgorithm = signatureInfo.getMessageDigestAlgo();
			fProvider = signatureInfo.getProvider();
			fSignatureString = signatureInfo.getSignature();
			fCertificateChainString = signatureInfo.getCertificateChain();
			fContent = signatureInfo.getContentOnly();

			if (fSignatureString == null || fProvider == null || fHashAlgorithm == null
					|| fCertificateChainString == null || fContent == null)
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
	 * @return <code>true</code> if certificate is self-signed or
	 *         <code>false</code> if certificate is CA signed
	 * @throws ScriptSignatureException
	 *             when script does not contain signature or there is an error
	 *             while retrieving certificate
	 */
	public boolean isSelfSignedCertificate() throws ScriptSignatureException {
		if (fContainSignature) {
			try {
				ArrayList<Certificate> certificateList = (ArrayList<Certificate>) getCertificateChain();

				Certificate cert = certificateList.get(0);

				cert.verify(cert.getPublicKey());

				return true;
			} catch (CertificateException e) {
				Logger.error(Activator.PLUGIN_ID, "Error while parsing certificate.", e);
				throw new ScriptSignatureException("Error while parsing certificate.", e);
			} catch (InvalidKeyException e) {
				throw new ScriptSignatureException("Key of the certificate is invalid.", e);

			} catch (NoSuchAlgorithmException e) {
				throw new ScriptSignatureException("No aprovider support this type of algorithm.", e);

			} catch (NoSuchProviderException e) {
				throw new ScriptSignatureException("No provider for this certificate.", e);

			} catch (SignatureException e) {
				// private key with which certificate was signed does not
				// correspond to this public key. Hence it is not self-signed
				// certificate
				return false;
			}
		}
		throw new ScriptSignatureException("Script does not contain signature");
	}

	/**
	 * Checks the validity of certificate. If certificate is CA signed, then it
	 * checks the validity of CA with trust-store.
	 *
	 * @param location
	 * @param password
	 * @return <code>true</code> if certificate is valid and trusted or
	 *         <code>false</code> if certificate is invalid or not trusted
	 * @throws ScriptSignatureException
	 */
	public boolean isCertChainValid(String trustStoreLocation, char[] trustStorePassword)
			throws ScriptSignatureException {

		if ((trustStoreLocation == null && trustStorePassword != null)
				|| (trustStoreLocation != null && trustStorePassword == null)) {
			throw new ScriptSignatureException("Either both or none of the parameters should be null");
		}

		if (fContainSignature) {
			InputStream iStream = null;
			try {

				if (trustStoreLocation == null && trustStorePassword == null) {
					iStream = new FileInputStream(System.getProperty("java.home") + "/lib/security/" + "cacerts");
					trustStorePassword = "changeit".toCharArray();
				} else
					iStream = ResourceTools.getInputStream(trustStoreLocation);

				CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

				ArrayList<Certificate> certificateList = (ArrayList<Certificate>) getCertificateChain();

				CertPath certPath = certificateFactory.generateCertPath(certificateList);

				CertPathValidator validator = CertPathValidator.getInstance("PKIX");

				KeyStore keystore = KeyStore.getInstance("JKS");
				keystore.load(iStream, trustStorePassword);

				PKIXParameters params = new PKIXParameters(keystore);

				params.setRevocationEnabled(true);
				Security.setProperty("ocsp.enable", "true");
				System.setProperty("com.sun.net.ssl.checkRevocation", "true");
				System.setProperty("com.sun.security.enableCRLDP", "true");

				// Validate will throw an exception on invalid chains.
				validator.validate(certPath, params);

				return true;
			} catch (CertificateException e) {
				throw new ScriptSignatureException("One or more certificates can't be loaded.", e);

			} catch (NoSuchAlgorithmException e) {
				throw new ScriptSignatureException(
						"Algorithm used for securing keystore can't be found. Chose another Keystore", e);

			} catch (KeyStoreException e) {
				throw new ScriptSignatureException("Keystore can't be loaded.");

			} catch (IOException e) {
				if (e.getCause() instanceof UnrecoverableKeyException)
					throw new ScriptSignatureException("Invalid Keystore Password", e);
				else if (e.getCause() instanceof FileNotFoundException || e.getCause() instanceof SecurityException)
					throw new ScriptSignatureException("File can't be read. Chose another keystore or try again.", e);

				Logger.error(Activator.PLUGIN_ID, Arrays.toString(e.getStackTrace()), e);
				throw new ScriptSignatureException("Error loading keystore. Try again.", e);

			} catch (InvalidAlgorithmParameterException e) {
				Logger.error(Activator.PLUGIN_ID, Arrays.toString(e.getStackTrace()), e);
				throw new ScriptSignatureException("Can't perform validation.", e);

			} catch (CertPathValidatorException e) {
				// if any invalidation occurs, exception will be caught here
				throw new ScriptSignatureException(e.getMessage());

			} finally {
				try {
					if (iStream != null)
						iStream.close();
				} catch (IOException e) {
					Logger.error(Activator.PLUGIN_ID, Arrays.toString(e.getStackTrace()), e);
				}
			}
		}
		throw new ScriptSignatureException("Script does not contain signature");
	}

	/**
	 * Checks the validity of certificate. If certificate is CA signed, then it
	 * checks the validity of CA with trust-store.
	 *
	 * @return <code>true</code> if certificate is valid and trusted or
	 *         <code>false</code> if certificate is invalid or not trusted
	 * @throws ScriptSignatureException
	 */
	public boolean isCertChainValid() throws ScriptSignatureException {

		return isCertChainValid(null, null);
	}

	/**
	 * Verify given signature with provided public key of provided certificate.
	 *
	 * @return <code>true</code> if signature is valid or <code>false</code> if
	 *         signature is invalid
	 * @throws ScriptSignatureException
	 *             when script does not contain signature or there is an error
	 *             while retrieving certificate
	 */
	public boolean verify() throws ScriptSignatureException {

		if (fContainSignature) {
			byte[] signByte = SignatureHelper.convertBase64ToBytes(fSignatureString);
			byte[] certByte = SignatureHelper.convertBase64ToBytes(fCertificateChainString[0]);
			Certificate userCert = getCertificate(certByte);

			try {

				PublicKey publicKey = userCert.getPublicKey();
				String encryptionAlgo = publicKey.getAlgorithm();

				Signature signature = Signature.getInstance(fHashAlgorithm + "with" + encryptionAlgo, fProvider);

				// initialize signature instance with public key
				signature.initVerify(publicKey);

				// perform verification
				signature.update(fContent.getBytes());
				boolean verified = signature.verify(signByte);

				return verified;

			} catch (SignatureException e) {
				Logger.error(Activator.PLUGIN_ID,
						"Signature object not initialized properly or signature is not readable.", e);
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
