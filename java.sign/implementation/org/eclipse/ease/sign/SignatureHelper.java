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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.rmi.activation.Activator;
import java.util.Base64;
import java.util.logging.Logger;

import signature.SignatureInfo;

/**
 * Class containing helper methods for conversion of format and appending
 * signature to file.
 *
 */
public class SignatureHelper {

	private static final String BEGIN_STRING = "-----BEGIN SIGNATURE-----", END_STRING = "-----END SIGNATURE-----";

	/**
	 * Converts given bytes in {@link Base64} form.
	 *
	 * @param bytes
	 *            bytes to be converted to Base64
	 * @return String representation of bytes in Base64 form or
	 *         <code>null</code> if input is <code>null</code>
	 */
	public static String convertBytesToBase64(final byte[] bytes) {

		if (bytes == null)
			return null;

		Base64.Encoder b = Base64.getEncoder();
		return b.encodeToString(bytes);
	}

	/**
	 * Converts given {@link Base64} string to bytes.
	 *
	 * @param str
	 *            provide {@link Base64} string to convert
	 * @return bytes is conversion is successful and <code>null</code> if input
	 *         is null
	 */
	public static byte[] convertBase64ToBytes(final String str) {

		if (str == null)
			return null;

		Base64.Decoder decoder = Base64.getDecoder();
		return decoder.decode(str);
	}

	/**
	 * Appends given signature, messageSigestAlgorithm ,provider, and
	 * certificate to given file.
	 * <p>
	 * Format for appending will be as follows:
	 * <p>
	 * <i>blockCommentStart</i><br>
	 * -----BEGIN SIGNATURE-----<br>
	 * Hash:SHA1 Provider:SUN <br>
	 * <br>
	 * signature in {@link Base64} format (48 bytes) <br>
	 * <br>
	 * certificate chain in {@link Base64} format (multiple lines)(each line
	 * containing 64 bytes)<br>
	 * <br>
	 * -----END SIGNSTURE-----<br>
	 * <i>blockCommentEnd</i>
	 *
	 * @param signStr
	 *            string representation of signature in Base64 format
	 * @param certStr
	 *            string representation of certificate chain in Base64 format
	 * @param messageDigestAlgo
	 *            name the message-digest algorithm using which signature is
	 *            created. Provide <code>null</code> or empty string or
	 *            'default' to set 'SHA1'
	 * @param provider
	 *            name the provider used to perform signature. Provide
	 *            <code>null</code> or empty string to set 'preferred'
	 * @param dataStream
	 *            stream to which signature and certificate are to be attached
	 * @param blockComStart
	 *            provide starting block comment string
	 * @param blockComEnd
	 *            provide ending block comment string
	 * @return <code>true</code> if signature is written to dataStream and
	 *         <code>false</code> if signature can't be written for e.g. due to
	 *         IOException
	 * @throws ScriptSignatureException
	 *             when one or more parameter are <code>null</code> or empty
	 */
	public static boolean appendSignature(final String signStr, final String certStr, String messageDigestAlgo,
			String provider, final OutputStream dataStream, final String blockComStart, final String blockComEnd)
					throws ScriptSignatureException {

		if (signStr == null || signStr.isEmpty() || certStr == null || certStr.isEmpty() || dataStream == null
				|| blockComStart == null || blockComEnd == null)
			throw new ScriptSignatureException("One or more parameters are null or empty");

		if (messageDigestAlgo == null || messageDigestAlgo.isEmpty() || "default".equalsIgnoreCase(messageDigestAlgo))
			messageDigestAlgo = "SHA1";

		if (provider == null || provider.isEmpty())
			provider = "preferred";

		final String begin = blockComStart + "\n" + BEGIN_STRING, end = END_STRING + "\n" + blockComEnd,
				signatureParam = "Hash:" + messageDigestAlgo + " Provider:" + provider;

		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(dataStream);
		try {
			/*
			 * By default, last line in every file is ended by \n which is not
			 * visible directly. But if generated programmatically, it may not.
			 * To be sure, two \n characters are added.
			 *
			 * Fist to add \n if not already there and second to bring pointer
			 * to next line. This gives at most two empty lines and at least one
			 * empty line.
			 *
			 * Now there is need of only one '\n'
			 */
			// TODO remember while appending to file as done below
			bufferedOutputStream.write("\n".getBytes());

			bufferedOutputStream.write(begin.getBytes());
			bufferedOutputStream.write("\n".getBytes());

			bufferedOutputStream.write(signatureParam.getBytes());
			bufferedOutputStream.write("\n\n".getBytes());

			bufferedOutputStream.write(signStr.getBytes());
			bufferedOutputStream.write("\n\n".getBytes());

			int i = 0;
			for (String s : certStr.split("")) {
				bufferedOutputStream.write(s.getBytes());
				i++;
				if (i % 48 == 0)
					bufferedOutputStream.write("\n".getBytes());
			}

			if (i % 48 != 0)
				bufferedOutputStream.write("\n".getBytes());

			bufferedOutputStream.write("\n".getBytes());
			bufferedOutputStream.write(end.getBytes());
			bufferedOutputStream.write("\n".getBytes());

			return true;

		} catch (IOException e) {
			// Logger.error(Activator.PLUGIN_ID,
			// Arrays.toString(e.getStackTrace()), e);

		} finally {
			try {
				if (bufferedOutputStream != null)
					bufferedOutputStream.close();
			} catch (IOException e) {
				// Logger.error(Activator.PLUGIN_ID,
				// Arrays.toString(e.getStackTrace()), e);
			}
		}
		return false;
	}

	/**
	 * Checks the given input stream to see whether it contains signature or
	 * not.
	 *
	 * @param location
	 *            provide location to check for signature
	 * @return <code>true</code> if signature is found or <code>false</code> if
	 *         signature is not found
	 * @throws ScriptSignatureException
	 *             when signature format is improper
	 */
	public boolean containSignature(final String location) throws ScriptSignatureException {

		if (getSignatureInfo(location) == null)
			return false;

		return true;
	}

	/**
	 * Gets signature, certificates, provider and message-digest algorithm of
	 * signature, and content excluding signature block.
	 *
	 * @param location
	 *            provide location of the script to get signature from
	 * @return {@link SignatureInfo} instance containing signature,
	 *         certificates, provider and message-digest algorithm, and content
	 *         excluding signature block or <code>null</code> if signature is
	 *         not found or is not in proper format
	 * @throws ScriptSignatureException
	 *             when there is text after signature block
	 */
	public static SignatureInfo getSignatureInfo(final String location) throws ScriptSignatureException {

		InputStream inputStream = ResourceTools.getInputStream(location);

		// TODO For getting language specific comment block
		// IScriptService scriptService = ScriptService.getInstance();
		// ScriptType scriptType = scriptService.getScriptType(location);
		// AbstractCodeParser codeParser = (AbstractCodeParser)
		// scriptType.getCodeParser();

		BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));

		try {

			String prev, cur;
			StringBuffer contentBuffer = new StringBuffer();
			StringBuffer certBuf;

			cur = bReader.readLine();
			if (cur == null)
				return null;

			// A line before BEGIN_STRING will be comment and to not include
			// that in contentOnly, prev is used. Using prev, contentOnly will
			// be appended with
			// previous line only if next line is not BEGIN_STRING.
			prev = cur;
			while ((cur = bReader.readLine()) != null) {

				while (!cur.equals(BEGIN_STRING)) {
					contentBuffer.append(prev + "\n");
					prev = cur;
					cur = bReader.readLine();
					if (cur == null)
						return null;
				}

				StringBuffer contentOnlyBuffer = new StringBuffer(contentBuffer);
				// remove an extra \n character at end. Since this content is
				// used for verification, same script is required in contentOnly
				// as it was before
				// signing
				contentOnlyBuffer.deleteCharAt(contentOnlyBuffer.length() - 1);

				// TODO check prev with start block comment string here
				contentBuffer.append(prev + "\n");
				contentBuffer.append(cur + "\n");

				// else{ continue; } denote that signature in proper format is
				// not yet found and can be found later. So, continue with
				// finding BEGIN_STRING.
				// else{ break; } denote that end of script is reached and so,
				// return null.

				String provider, messageDigestAlgo;
				cur = bReader.readLine();
				if (cur != null) {
					String params[] = cur.split(" ");
					if (params.length == 2) {
						String temp[] = params[0].split(":");
						if (temp.length == 2)
							messageDigestAlgo = temp[1];
						else {
							prev = cur;
							continue;
						}
						temp = params[1].split(":");
						if (temp.length == 2)
							provider = temp[1];
						else {
							prev = cur;
							continue;
						}

					} else {
						prev = cur;
						continue;
					}
					contentBuffer.append(cur + "\n");
				} else
					break;

				// following block checks for empty line. If one is not found,
				// then restart checking BEGIN_STRING
				cur = bReader.readLine();
				if (cur != null) {
					if (!cur.isEmpty()) {
						prev = cur;
						continue;
					}
					contentBuffer.append(cur + "\n");
				} else
					break;

				// following block fetches signature
				String signature;
				cur = bReader.readLine();
				if (cur != null) {
					if (cur.length() == 64)
						signature = cur;
					else {
						prev = cur;
						continue;
					}
					contentBuffer.append(cur + "\n");
				} else
					break;

				// following block checks for empty line.
				cur = bReader.readLine();
				if (cur != null) {
					if (!cur.isEmpty()) {
						prev = cur;
						continue;
					}
					contentBuffer.append(cur + "\n");
				} else
					break;

				// following block fetches certificates separated by colon(:)
				certBuf = new StringBuffer();
				while ((cur = bReader.readLine()) != null && !cur.isEmpty()) {
					certBuf.append(cur);
					contentBuffer.append(cur + "\n");
				}

				if (cur == null)
					break;

				contentBuffer.append(cur + "\n");

				cur = bReader.readLine();
				if (cur != null) {
					if (!cur.equals(END_STRING)) {
						prev = cur;
						continue;
					}
					contentBuffer.append(cur + "\n");
				} else
					break;

				cur = bReader.readLine();
				if (cur != null) {
					// TODO check cur with for end block comment string here
					contentBuffer.append(cur + "\n");
				} else
					break;

				// checks end of script
				cur = bReader.readLine();
				if (cur != null)
					throw new ScriptSignatureException("Text after signature is not allowed");
				else {
					String certificates[] = certBuf.toString().split(":");
					return new SignatureInfo(signature, provider, messageDigestAlgo, certificates,
							contentOnlyBuffer.toString());
				}
			}
			return null;

		} catch (IOException e) {
			Logger.error(Activator.PLUGIN_ID, "An IO error occurred while reading file.", e);
			throw new ScriptSignatureException("An IO error occurred while reading file.", e);
		}
	}
}