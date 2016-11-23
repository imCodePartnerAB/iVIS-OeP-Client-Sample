/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.webutils.http;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class StatusCapturingResponse extends HttpServletResponseWrapper {
    
	private int captureStatus;
	private int status = 200;

    public StatusCapturingResponse(HttpServletResponse response, int captureStatus) {
        super(response);
        this.captureStatus = captureStatus;
    }

    @Override public void sendError(int status, String message) throws IOException {
        this.status = status;
    }

    @Override public void sendError(int status) throws IOException {
        this.status = status;
    }

    @Override public void sendRedirect(String path) throws IOException {
        this.status = 302;
        super.sendRedirect(path);
    }

    @Override public void setStatus(int status) {
        this.status = status;
        if (!isCapturedStatus()) {
            super.setStatus(status);
        }
    }

    @Override public void setStatus(int status, String message) {
        this.status = status;
        if (!isCapturedStatus()) {
            super.setStatus(status, message);
        }
    }

    @Override public void flushBuffer() throws IOException {
        if (!isCapturedStatus()) {
            super.flushBuffer();
        }
    }

    public boolean isCapturedStatus() {
        return status == captureStatus;
    }

	public int getStatus() {
		return this.status;
	}
}
