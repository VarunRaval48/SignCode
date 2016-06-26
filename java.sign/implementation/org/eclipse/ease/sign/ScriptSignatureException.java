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

/**
 * Exception Class for handling errors with custom messages.
 */
public class ScriptSignatureException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * @see Exception#Exception()
	 */
	public ScriptSignatureException() {
		super();
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public ScriptSignatureException(String arg0) {
		super(arg0);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public ScriptSignatureException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public ScriptSignatureException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @see Exception#Exception(String, Throwable, boolean, boolean)
	 */
	public ScriptSignatureException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}