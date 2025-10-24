
/*
 *   Copyright (c) 2013-2022. LA Referencia / Red CLARA and others
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   This file is part of LA Referencia software platform LRHarvester v4.x
 *   For any further information please contact Lautaro Matas <lmatas@gmail.com>
 */

package org.lareferencia.contrib.rcaap.rest.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class QueryStringParserHelper {

    private static final String QUERYENCODER = "UTF-8";

    /**
     * String for URL Encoding
     * 
     * @param toEncode
     *            String
     * @return
     */
    public static String encode(String toEncode) {
        if (toEncode == null) {
            return null;
        }
        try {
            return URLEncoder.encode(toEncode, QUERYENCODER);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return toEncode;
    }

    /**
     * String List for URL Encoding
     * 
     * @param toEncode
     *            String Array
     * @return
     */
    public static String[] encode(String[] toEncode) {
        if (toEncode == null) {
            return null;
        }
        String[] encoded = new String[toEncode.length];
        try {
            for (int i = 0; i < toEncode.length; i++) {
                encoded[i] = URLEncoder.encode(toEncode[i], QUERYENCODER);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encoded;
    }

}
