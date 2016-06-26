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

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.activation.Activator;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * Class containing methods to perform signature. Methods include loading of
 * keystore, private key and performing signature to provided file.
 *
 */
public class PerformSignature {

	/**
	 * Checks whether file provided for keystore exists. Checks keystore of
	 * given type and provider can be instantiated and if so, instantiates
	 * keystore. Then, loads the keyStore from file using provided password.
	 *
	 * @param locationOfKeyStore
	 *            provide location of user keystore
	 * @param type
	 *            name type of the keystore like JKS, JCEKS, PKCS12, PKCS12S2.
	 *            Provide <code>null</code> or empty string or 'default' to set
	 *            default type
	 * @param provider
	 *            name provider of the keystore like SUN, SUNRsaSign, SUNJCE,
	 *            etc. Provide <code>null</code> or empty string or 'preferred'
	 *            to let system take decision
	 * @param keyStorePass
	 *            provide password to unlock keystore
	 * @return instance of keystore if loaded properly
	 * @throws ScriptSignatureException
	 *             when exception can be recovered without closing the
	 *             application. For e.g., arguments provided for keystore or
	 *             password are invalid, keystore file can't be read, etc.
	 */
	public static KeyStore loadKeyStore(final Object locationOfKeyStore, String type, String provider,
			final String keyStorePass) throws ScriptSignatureException {
		KeyStore keyStore;

		InputStream inputStream = ResourceTools.getInputStream(locationOfKeyStore);
		if (inputStream == null)
			throw new ScriptSignatureException("Given location of keystore can't be accessed.");

		if (type == null || type.isEmpty() || "default".equalsIgnoreCase(type))
			type = KeyStore.getDefaultType();

		if ("".equals(provider) || "preferred".equalsIgnoreCase(provider))
			provider = null;

		try {
			// instantiate keystore using type and provider
			if (provider == null)
				keyStore = KeyStore.getInstance(type);
			else
				keyStore = KeyStore.getInstance(type, provider);

			// load keystore from file
			keyStore.load(inputStream, keyStorePass.toCharArray());

			Logger.info(Activator.PLUGIN_ID, "Keystore loaded");

			return keyStore;
		} catch (KeyStoreException e) {
			Logger.error(Activator.PLUGIN_ID, Arrays.toString(e.getStackTrace()), e);
			throw new ScriptSignatureException("No provider support '" + type + "' type of keystore.", e);

		} catch (NoSuchProviderException e) {
			throw new ScriptSignatureException("No such provider available.", e);

		} catch (IOException e) {
			if (e.getCause() instanceof UnrecoverableKeyException)
				throw new ScriptSignatureException("Invalid Keystore Password", e);
			else if (e.getCause() instanceof FileNotFoundException || e.getCause() instanceof SecurityException)
				throw new ScriptSignatureException("File can't be read. Chose another keystore or try again.", e);

			Logger.error(Activator.PLUGIN_ID, Arrays.toString(e.getStackTrace()), e);
			throw new ScriptSignatureException("Error loading keystore. Try again.", e);

		} catch (NoSuchAlgorithmException e) {
			throw new ScriptSignatureException(
					"Algorithm used for securing keystore can't be found. Chose another Keystore", e);

		} catch (CertificateException e) {
			throw new ScriptSignatureException("Some certificate/s in keystore can't be loaded", e);

		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				Logger.error(Activator.PLUGIN_ID, Arrays.toString(e.getStackTrace()), e);
			}
		}
	}

	/**
	 * Gets all aliases from keystore.
	 *
	 * @param keyStore
	 *            provide keystore instance to read aliases
	 * @return collection of string of aliases in keystore or <code>null</code>
	 *         if keystore is not loaded properly
	 */
	public static Collection<String> getAliases(final KeyStore keyStore) {
		try {
			if (keyStore != null)
				return Collections.list(keyStore.aliases());

		} catch (KeyStoreException e) {
			// keystore is not initialized properly
			return null;
		}
		// keystore is not initialized properly
		return null;
	}

	/**
	 * Obtains certificate for corresponding alias.
	 *
	 * @param keyStore
	 *            provide instance of loaded keystore
	 * @param alias
	 *            provide alias of which certificate is required
	 * @param canAttachSelfSign
	 *            tell whether to allow to attach self-signed certificate
	 * @return certificate chain in Base64 String format, each certificate
	 *         separated by colon(:) or <code>null</code> if keystore is not
	 *         initialized properly
	 * @throws ScriptSignatureException
	 *             if alias can't be found or certificate can't be loaded
	 */
	public static String getCertificate(final KeyStore keyStore, final String alias, final boolean canAttachSelfSign)
			throws ScriptSignatureException {

		if (alias == null)
			throw new ScriptSignatureException("Alias is null. Try again.");

		if (keyStore != null) {
			try {
				if (!keyStore.containsAlias(alias))
					throw new ScriptSignatureException("Alias can't be found");

				if (!keyStore.isCertificateEntry(alias) && !canAttachSelfSign)
					throw new ScriptSignatureException(
							"This certificate is self-signed certificate. Chose another trusted certificate.");

				// get certificate chain from keyStore, convert it to bytes and
				// then to base64
				Certificate certificateChain[] = keyStore.getCertificateChain(alias);

				if (certificateChain != null) {
					StringBuffer certStrBuf = new StringBuffer();
					for (Certificate cert : certificateChain) {
						certStrBuf.append(SignatureHelper.convertBytesToBase64(cert.getEncoded()) + ":");
					}

					return certStrBuf.toString();
				} else {
					Certificate cert = keyStore.getCertificate(alias);
					return SignatureHelper.convertBytesToBase64(cert.getEncoded());
				}
			} catch (KeyStoreException e) {
				Logger.error(Activator.PLUGIN_ID, "Keystore not initialized properly. Try Again.", e);
				return null;

			} catch (CertificateEncodingException e) {
				throw new ScriptSignatureException(
						"Encoding of certificate is improper. Please try again or chose another alias.", e);
			}
		}
		Logger.error(Activator.PLUGIN_ID, "Keystore not initialized properly. Try Again.");
		return null;
	}

	/**
	 * Calculates signature of provided file.
	 *
	 * @param signature
	 *            provide initialized instance of {@link Signature}
	 * @param dataStream
	 *            provide input stream from which script is to be read
	 * @return calculated signature in byte[] format
	 * @throws ScriptSignatureException
	 *             when signature is not successful and error occurs while
	 *             reading file
	 */
	private static byte[] getSignature(final Signature signature, final InputStream dataStream)
			throws ScriptSignatureException {
		byte[] buffer = new byte[1024];

		BufferedInputStream bf = new BufferedInputStream(dataStream);
		try {
			int len;
			while ((len = bf.read(buffer)) >= 0)
				signature.update(buffer, 0, len);

			return signature.sign();

		} catch (SignatureException e) {
			// signature can't be performed, try again
			throw new ScriptSignatureException("Signature is not successful. Try again.", e);

		} catch (IOException e) {
			throw new ScriptSignatureException("An error occured while reading file.", e);

		} finally {
			try {
				if (bf != null)
					bf.close();
			} catch (IOException e) {
				Logger.error(Activator.PLUGIN_ID, Arrays.toString(e.getStackTrace()), e);
			}
		}
	}

	/**
	 * Initializes private key and gets signature of provided file.
	 *
	 * @param keyStore
	 *            provide instance of loaded keystore
	 * @param dataStream
	 *            give input stream of the script which is to be signed
	 * @param alias
	 *            give alias corresponding to private key used to sign file
	 * @param privateKeyPass
	 *            provide password protecting the private key
	 * @param provider
	 *            give provider used to perform signature. Provide
	 *            <code>null</code> or empty string or 'preferred' to let system
	 *            take decision
	 * @param messageDigestAlgo
	 *            name the message-digest algorithm to perform signature.
	 *            Provide <code>null</code> or empty string or 'default' to
	 *            chose default algorithm
	 * @return provider of signature and signature in string Base64 format
	 *         separated by colon(:) or <code>null</code> if keystore not
	 *         initialized properly
	 * @throws ScriptSignatureException
	 *             if alias or privateKeyPass is <code>null</code>, signature
	 *             can't be performed, password to alias is wrong, parameters
	 *             for private key are wrong
	 */
	public static String createSignature(final KeyStore keyStore, final InputStream dataStream, final String alias,
			final String privateKeyPass, String provider, String messageDigestAlgo) throws ScriptSignatureException {

		Signature signature;

		if (alias != null && privateKeyPass != null)
			throw new ScriptSignatureException("Alias or private key password is null. Try again.");

		if ("".equals(provider) || "preferred".equalsIgnoreCase(provider))
			provider = null;

		if (messageDigestAlgo == null || messageDigestAlgo.isEmpty() || "default".equalsIgnoreCase(messageDigestAlgo))
			messageDigestAlgo = "SHA1";

		if (keyStore != null) {
			try {
				if (!keyStore.containsAlias(alias))
					throw new ScriptSignatureException("Alias can't be found. Try again.");

				if (!keyStore.isKeyEntry(alias))
					throw new ScriptSignatureException("Keystore does not contain alias. Please chose another alias.");

				// get private key corresponding to selected alias
				PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, privateKeyPass.toCharArray());

				// Encryption algorithm is decided based on algorithm using
				// which key is made.
				String encryptionAlgo = privateKey.getAlgorithm();

				// check algorithm of private key. It must belong to signature
				// algorithms
				if (!encryptionAlgo.equals("DSA") && !encryptionAlgo.equals("RSA"))
					throw new ScriptSignatureException("Key contain invalid algorithm. It must contain DSA or RSA.");

				// initialize signature object
				if (provider == null)
					// let system select appropriate provider
					signature = Signature.getInstance(messageDigestAlgo + "with" + encryptionAlgo);
				else
					signature = Signature.getInstance(messageDigestAlgo + "with" + encryptionAlgo, provider);

				provider = signature.getProvider().getName();

				signature.initSign(privateKey);

				// perform signature of data in dataStream using private key
				byte[] signByte = getSignature(signature, dataStream);

				Logger.info(Activator.PLUGIN_ID, "Signature performed.");

				String signStr = SignatureHelper.convertBytesToBase64(signByte);
				return provider + ":" + signStr;

			} catch (KeyStoreException e) {
				// keystore is not initialized properly
				Logger.error(Activator.PLUGIN_ID, "Keystore not initialized properly. Try Again.", e);
				return null;

			} catch (NoSuchAlgorithmException e) {
				throw new ScriptSignatureException(
						"Algorithm for key is not recognized. Please try again or chose another alias.", e);

			} catch (UnrecoverableKeyException e) {
				throw new ScriptSignatureException("Invalid Alias-Password combination. Please try Again.", e);

			} catch (NoSuchProviderException e) {
				throw new ScriptSignatureException("No such provider available. Chose another provider.", e);

			} catch (InvalidKeyException e) {
				throw new ScriptSignatureException("Key is invalid. Please try again or chose another alias.", e);

			}
		}
		Logger.error(Activator.PLUGIN_ID, "Keystore not initialized properly. Try Again.");
		return null;
	}
}