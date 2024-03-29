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

package org.lareferencia.contrib.rcaap.backend.repositories.jpa;

import org.lareferencia.contrib.rcaap.backend.domain.Historic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "historic", collectionResourceRel = "historic")
public interface HistoricRepository extends JpaRepository<Historic, Long> {

	@Query(value="select h from Historic h where h.networkId = ?1 and h.month = ?2 and h.year = ?3")
	Historic findNetworkMonthHistoric(long networkId, int month, int year);
	
	@Query(value="select h from Historic h where h.networkId = ?1")
	Page<Historic> findLastNByNetworkID(long networkId, Pageable pageable);

}
