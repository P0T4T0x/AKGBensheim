/*
 * Copyright (C) 2013 ePapyrus, Inc. All rights reserved.
 *
 * This file is part of PlugPDF for Android project.
 */

/*
 * SimpleDocumentReaderListener.java
 * 
 * Version:
 *       id
 *       
 * Revision:
 *      logs
 */

package com.epapyrus.plugpdf;

import com.epapyrus.plugpdf.core.viewer.DocumentState;

/**
 * The listener to be implemented to receive event notifications on completion
 * of PDF document loading on a {@link SimpleDocumentReader}.
 * 
 * @author ePapyrus
 * 
 * @see <a href="http://www.plugpdf.com">http://www.plugpdf.com</a>
 */
public interface SimpleDocumentReaderListener {
	/**
	 * Notifies that a PDF document has finished loading.
	 * 
	 * @param state
	 *            {@link DocumentState.OPEN}
	 */
	void onLoadFinish(DocumentState.OPEN state);
}