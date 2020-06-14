/*-
 * #%L
 * This file is part of libtiled-java.
 * %%
 * Copyright (C) 2020 Adam Hornacek <adam.hornacek@icloud.com>
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.mapeditor.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;

/**
 * Helper class containing util methods for jar protocol URLs.
 */
public class URLHelper {

    private static final String JAR_PROTOCOL = "jar";
    private static final char URL_SEPARATOR_CHAR = '/';
    private static final String URL_SEPARATOR = "" + URL_SEPARATOR_CHAR;
    private static final String PARENT_DIR = "..";
    private static final String CURRENT_DIR = ".";

    private URLHelper() {
    }

    /**
     * Returns parent directory of the URL's path.
     */
    public static URL getParent(final URL url) throws MalformedURLException, URISyntaxException {
        if (url == null) {
            throw new IllegalArgumentException("Url cannot be null");
        }
        if (JAR_PROTOCOL.equals(url.getProtocol())) {
            if (isDirectory(url)) {
                String urlStr = url.toString();
                return new URL(urlStr.substring(0, urlStr.lastIndexOf(URL_SEPARATOR_CHAR, urlStr.length() - 2) + 1));
            } else {
                return new URL(url.toString().substring(0, url.toString().lastIndexOf(URL_SEPARATOR_CHAR) + 1));
            }
        } else {
            if (isDirectory(url)) {
                return url.toURI().resolve(PARENT_DIR).toURL();
            } else {
                return url.toURI().resolve(CURRENT_DIR).toURL();
            }
        }
    }

    private static boolean isDirectory(final URL url) {
        return url.getPath().endsWith(URL_SEPARATOR);
    }

    /**
     * Reimplementation of {@link java.net.URI#resolve(String)} with support for jar URLs.
     */
    public static URL resolve(final URL url, final String path) throws URISyntaxException, MalformedURLException {
        if (url == null) {
            throw new IllegalArgumentException("Url cannot be null");
        }
        if (path == null || path.isEmpty()) {
            return url;
        }

        String urlPath = path.replace(File.separatorChar, URL_SEPARATOR_CHAR);
        if (JAR_PROTOCOL.equals(url.getProtocol())) {
            String resultUrl;
            if (isDirectory(url)) {
                resultUrl = url.toString() + urlPath;
            } else {
                resultUrl = getParent(url) + urlPath;
            }
            String[] segments = resultUrl.split(URL_SEPARATOR);
            LinkedList<String> result = new LinkedList<>();
            for (String segment : segments) {
                if (PARENT_DIR.equals(segment)) {
                    if (result.isEmpty()) {
                        throw new IllegalArgumentException("Cannot resolve URL " + url + " for path " + urlPath);
                    }
                    result.removeLast();
                } else if (!CURRENT_DIR.equals(segment)) {
                    result.addLast(segment);
                }
            }
            return new URL(String.join(URL_SEPARATOR, result));
        } else {
            return url.toURI().resolve(urlPath).toURL();
        }
    }

}
