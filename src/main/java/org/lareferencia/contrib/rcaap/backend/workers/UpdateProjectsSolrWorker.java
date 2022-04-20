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

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lareferencia.contrib.rcaap.backend.domain.ProjectSolr;
import org.lareferencia.contrib.rcaap.backend.project.ProjectParserException;
import org.lareferencia.contrib.rcaap.backend.project.ProjectsParser;
import org.lareferencia.contrib.rcaap.backend.repositories.solr.ProjectSolrRepository;
import org.lareferencia.core.worker.BaseBatchWorker;
import org.lareferencia.core.worker.NetworkRunningContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import lombok.Getter;
import lombok.Setter;

public class UpdateProjectsSolrWorker extends BaseBatchWorker<ProjectSolr, NetworkRunningContext> {
    private static Logger logger = LogManager.getLogger(UpdateProjectsSolrWorker.class);

    @Value("${project.file.path}")
    String path;

    @Getter
    @Setter
    boolean executeDeletion;

    @Autowired
    ProjectSolrRepository projectSolrRepository;

    NumberFormat percentajeFormat = NumberFormat.getPercentInstance();
    private List<ProjectSolr> projects;

    @Override
    public void preRun() {

        try {

            ProjectsParser parser = new ProjectsParser(path);

            projects = parser.getProjectsSolr();
            this.setPaginator(new ProjectSolrPaginator(projects));
        } catch (IOException e) {
            logger.fatal("View Trace. " + e);
            e.printStackTrace();
        } catch (ProjectParserException e1) {
            logger.fatal("Process stopped: " + e1);
        }
    }

    @Override
    public void prePage() {
    }

    @Override
    public void processItem(ProjectSolr project) {
    }

    @Override
    public void postPage() {
    }

    @Override
    public void postRun() {
        if (projects != null && projects.size() > 0) {
            if (executeDeletion) {
                logger.info("Removing all projects in Solr...");
                projectSolrRepository.deleteAll();
            }
            logger.info("Indexing projects in Solr...");
            projectSolrRepository.saveAll(projects);
            logger.info(projects.size() + " records indexed from the xls file");
        } else {
            logger.error("Empty projects list to index, please check for errors within the xls file");
        }
    }

    @Override
    public String toString() {
        return "Update Projects Solr [" + percentajeFormat.format(this.getCompletionRate()) + "]";
    }

}
