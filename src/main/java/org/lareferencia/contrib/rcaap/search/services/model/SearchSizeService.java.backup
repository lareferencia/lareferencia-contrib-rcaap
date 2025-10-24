
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

package org.lareferencia.contrib.rcaap.search.services.model;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.lareferencia.contrib.rcaap.search.model.Sizes;

public class SearchSizeService {
    private Sizes serviceSizes;
    private Map<BigInteger, BigInteger> sizesMap;

    private static SearchConfigurationObjectFactoryService objFactoryService = new SearchConfigurationObjectFactoryService();

    public SearchSizeService(Sizes sizes) {
        this.serviceSizes = sizes;

        setSizesHash(getServiceSizes());
    }

    private void setSizesHash(Sizes sizes) {
        this.sizesMap = new HashMap<BigInteger, BigInteger>();
        for (BigInteger size : sizes.getSize()) {
            sizesMap.put(size, size);
        }
    }

    public Sizes getServiceSizes() {
        if (this.serviceSizes == null) {
            this.serviceSizes = objFactoryService.getObjectFactory().createSizes();
        }
        return this.serviceSizes;
    }

    public static Sizes cloneSizes(Sizes sizes) {
        Sizes clonedSizes = objFactoryService.getObjectFactory().createSizes();
        clonedSizes.getSize().addAll(sizes.getSize());
        clonedSizes.setDefault(sizes.getDefault());
        return clonedSizes;
    }

    public boolean isValidSize(BigInteger size) {
        return this.sizesMap.containsKey(size);
    }

}
