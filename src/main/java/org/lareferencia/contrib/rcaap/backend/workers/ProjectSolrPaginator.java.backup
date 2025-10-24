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

package org.lareferencia.contrib.rcaap.backend.workers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lareferencia.contrib.rcaap.backend.domain.ProjectSolr;
import org.lareferencia.core.worker.IPaginator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class ProjectSolrPaginator implements IPaginator<ProjectSolr> {

	// implements dummy starting page
	@Override
	public int getStartingPage() { return 1; }

	private static final int DEFAULT_PAGE_SIZE = 100;
	private static Logger logger = LogManager.getLogger(ProjectSolrPaginator.class);
	private List<ProjectSolr> projects;
	
	private int pageSize = DEFAULT_PAGE_SIZE;
	private int totalPages = 0;
	
	
	/**
	 * Este pedido paginado pide siempre la primera página
	 * restringida a que el id sea mayor al ultimo anterior
	 **/
	public ProjectSolrPaginator(List<ProjectSolr> projects) {
		
		this.projects = projects;
		
		Page<ProjectSolr> page = new PageImpl<ProjectSolr>(projects, PageRequest.of(0, pageSize), projects.size());
		this.totalPages = page.getTotalPages();
	}
	
	
	public int getTotalPages() {
		return totalPages;
	}
	
	public Page<ProjectSolr> nextPage() {
		Page<ProjectSolr> page;
		page = new PageImpl<>(projects, PageRequest.of(0, pageSize), projects.size());
		return page;
	}
	
	public void setPageSize(int size) {
		this.pageSize = size;
	}
	
}
