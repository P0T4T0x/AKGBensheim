/*
 * Copyright (C) 2013 ePapyrus, Inc. All rights reserved.
 *
 * This file is part of PlugPDF for Android project.
 */

/*
 * SimpleReaderFactory.java
 * 
 * Version:
 *       id
 *       
 * Revision:
 *      logs
 */

package com.epapyrus.plugpdf;

import android.app.Activity;

/**
 * You can create a custom PDF viewer class by using PDFDocument class directly.
 * 
 * @brief Generates and returns the SimpleDocumnetViewer.
 * 
 * @author ePapyrus
 * 
 * @see <a href="http://www.plugpdf.com">http://www.plugpdf.com</a>
 */
public class SimpleReaderFactory {

	/**
	 * Creates and returns a PDF viewer used for viewing, reading, and
	 * navigating PDF document
	 * 
	 * @param act
	 *            The activity of the current application.
	 * @param listener
	 *            The SimpleDocumentViewerListener instance.
	 * 
	 * @return SimpleDocumentViewer The default PDF document viewer instance
	 */
	public static SimpleDocumentReader createSimpleViewer(Activity act,
			SimpleDocumentReaderListener listener) {

		SimpleDocumentReader viewer = new SimpleDocumentReader(act);
		viewer.setListener(listener);

		return viewer;
	}
}
