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

package org.lareferencia.contrib.rcaap.backend.project;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.lareferencia.contrib.rcaap.backend.domain.Project;
import org.lareferencia.contrib.rcaap.backend.domain.ProjectSolr;
import org.lareferencia.contrib.rcaap.backend.domain.CoreSolr.COAR_RIGHTS_ACCESS;
import org.lareferencia.contrib.rcaap.backend.workers.HistoricSolrWorker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Parser for projects's XLSX
 *
 * @author André Santos <asantos@keep.pt>
 */
public class ProjectsParser {
    private static Logger logger = LogManager.getLogger(ProjectsParser.class);

    public enum CELLS {
        CELL_ID(0, "idProjecto"), CELL_TITLE(1, "Title"), CELL_DESCRIPTION(2, "PublicDescription"), CELL_START_DATE(3,
                "StartDate"), CELL_END_DATE(4, "EndDate"), CELL_FUNDING_AMOUNT(5,
                        "FundingAmount"), CELL_FUNDING_CURRENCY(6, "FundingAmountCurrency"), CELL_REFERENCE(7,
                                "Reference"), CELL_FUNDING_PROGRAM(8, "FundingProgram"), CELL_DATE_AWARDED(9,
                                        "DateAwarded"), CELL_FUNDREF_URI(10, "URL"), CELL_FUNDER_NAME(11,
                                                "FunderName"), CELL_FUNDER_ACRONYM(12,
                                                        "FunderAcronym"), CELL_FUNDER_COUNTRY(13, "FunderCountry");

        public final int cell;
        public final String label;

        private CELLS(int cell, String label) {
            this.label = label;
            this.cell = cell;
        }

        public static CELLS valueOfLabel(String label) {
            for (CELLS e : values()) {
                if (e.label.equals(label)) {
                    return e;
                }
            }
            return null;
        }

        public static CELLS valueOfCell(int cell) {
            for (CELLS e : values()) {
                if (e.cell == cell) {
                    return e;
                }
            }
            return null;
        }
    }

    public static final int NUMBER_OF_CELLS = 14;

    private String xlsxFilePath;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    /**
     * Constructor that receives a path to XLSX
     *
     * @param xlsxFilePath
     *            Path to XLSX
     * @throws IOException
     */
    public ProjectsParser(String xlsxFilePath) throws IOException {
        this.xlsxFilePath = xlsxFilePath;

        FileInputStream xlsxFile = new FileInputStream(new File(this.xlsxFilePath));

        this.workbook = new XSSFWorkbook(xlsxFile);
        this.sheet = workbook.getSheetAt(0);

        xlsxFile.close();
    }

    /**
     * Get list of projects
     *
     * @return List<Project> List of projects
     */
    public List<Project> getProjects() throws ProjectParserException {
        List<Project> projects = new ArrayList<>();

        Iterator<Row> rowIterator = sheet.iterator();
        // Skip first row
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }
        //counter starts at 
        int rowCounter = 2;
        while (rowIterator.hasNext()) {
            try {
                Project project = new Project();
                Row row = rowIterator.next();
                Map<CELLS, Object> rowValues = populateRowValuesFromSheet(row);

                project.setProjectID((String) rowValues.get(CELLS.CELL_ID));
                project.setTitle((String) rowValues.get(CELLS.CELL_TITLE));
                project.setDescription((String) rowValues.get(CELLS.CELL_DESCRIPTION));
                project.setStartDate((Date) rowValues.get(CELLS.CELL_START_DATE));
                project.setEndDate((Date) rowValues.get(CELLS.CELL_END_DATE));
                project.setFundingAmount((Double) rowValues.get(CELLS.CELL_FUNDING_AMOUNT));
                project.setFundingCurrency((String) rowValues.get(CELLS.CELL_FUNDING_CURRENCY));
                project.setReference((String) rowValues.get(CELLS.CELL_REFERENCE));
                project.setFundingProgram((String) rowValues.get(CELLS.CELL_FUNDING_PROGRAM));
                project.setDateAwarded((Date) rowValues.get(CELLS.CELL_DATE_AWARDED));
                project.setFundRefURI((String) rowValues.get(CELLS.CELL_FUNDREF_URI));
                project.setFunderName((String) rowValues.get(CELLS.CELL_FUNDER_NAME));
                project.setFunderAcronym((String) rowValues.get(CELLS.CELL_FUNDER_ACRONYM));
                project.setFunderCountry((String) rowValues.get(CELLS.CELL_FUNDER_COUNTRY));

                if (project.isValid()) {
                    projects.add(project);
                } else {
                    throw new ProjectParserException("Validation fails");
                }
                rowCounter++;
            } catch (ProjectParserException e) {
                throw new ProjectParserException(e.getMessage() + " on row: " + rowCounter);
            } catch (Exception e1) {
                throw new ProjectParserException(e1.getMessage() + " on row: " + rowCounter);
            }
        }

        return projects;
    }

    public List<ProjectSolr> getProjectsSolr() throws ProjectParserException {
        List<ProjectSolr> projects = new ArrayList<>();

        Iterator<Row> rowIterator = sheet.iterator();
        // Skip first row
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }
        int rowCounter = 2;
        while (rowIterator.hasNext()) {
            try {
                ProjectSolr project = new ProjectSolr();
                Row row = rowIterator.next();

                Map<CELLS, Object> rowValues = populateRowValuesFromSheet(row);

                project.setProjectID((String) rowValues.get(CELLS.CELL_ID));
                project.setTitle((String) rowValues.get(CELLS.CELL_TITLE));
                project.setDescription((String) rowValues.get(CELLS.CELL_DESCRIPTION));
                project.setStartDate((Date) rowValues.get(CELLS.CELL_START_DATE));
                project.setEndDate((Date) rowValues.get(CELLS.CELL_END_DATE));
                project.setFundingAmount((Double) rowValues.get(CELLS.CELL_FUNDING_AMOUNT));
                project.setFundingCurrency((String) rowValues.get(CELLS.CELL_FUNDING_CURRENCY));
                project.setReference((String) rowValues.get(CELLS.CELL_REFERENCE));
                project.setFundingProgram((String) rowValues.get(CELLS.CELL_FUNDING_PROGRAM));
                project.setDateAwarded((Date) rowValues.get(CELLS.CELL_DATE_AWARDED));
                project.setFundRefURI((String) rowValues.get(CELLS.CELL_FUNDREF_URI));
                project.setFunderName((String) rowValues.get(CELLS.CELL_FUNDER_NAME));
                project.setFunderAcronym((String) rowValues.get(CELLS.CELL_FUNDER_ACRONYM));
                project.setFunderCountry((String) rowValues.get(CELLS.CELL_FUNDER_COUNTRY));

                if (project.isValid()) {
                    projects.add(project);
                } else {
                    throw new ProjectParserException("Validation fails");
                }
                rowCounter++;
            } catch (ProjectParserException e) {
                throw new ProjectParserException(e.getMessage() + " on row: " + rowCounter);
            } catch (Exception e1) {
                throw new ProjectParserException(e1.getMessage() + " on row: " + rowCounter);
            }
        }

        return projects;
    }

    private Map<CELLS, Object> populateRowValuesFromSheet(Row row) throws ProjectParserException {
        Map<CELLS, Object> rowValues = new HashMap<>();

        for (int cellNumber = 0; cellNumber <= NUMBER_OF_CELLS; cellNumber++) {
            try {
                Cell cell = row.getCell(cellNumber);

                Object value = null;
                switch (CELLS.valueOfCell(cellNumber)) {
                case CELL_DATE_AWARDED:
                    value = parseDateCell(cell);
                    break;
                case CELL_DESCRIPTION:
                    value = parseStringCell(cell);
                    break;
                case CELL_END_DATE:
                    value = parseDateCell(cell);
                    break;
                case CELL_FUNDER_ACRONYM:
                    value = parseStringCell(cell);
                    break;
                case CELL_FUNDER_COUNTRY:
                    value = parseStringCell(cell);
                    break;
                case CELL_FUNDER_NAME:
                    value = parseStringCell(cell);
                    break;
                case CELL_FUNDING_AMOUNT:
                    value = parseNumericCell(cell);
                    break;
                case CELL_FUNDING_CURRENCY:
                    value = parseStringCell(cell);
                    break;
                case CELL_FUNDING_PROGRAM:
                    value = parseStringCell(cell);
                    break;
                case CELL_FUNDREF_URI:
                    value = parseStringCell(cell);
                    break;
                case CELL_ID:
                    value = parseStringCell(cell);
                    break;
                case CELL_REFERENCE:
                    value = parseStringCell(cell);
                    break;
                case CELL_START_DATE:
                    value = parseDateCell(cell);
                    break;
                case CELL_TITLE:
                    value = parseStringCell(cell);
                    break;
                default:
                    value = new String();
                    break;

                }
                rowValues.put(CELLS.valueOfCell(cellNumber), value);
            } catch (ProjectParserException e) {
                rowValues.put(CELLS.valueOfCell(cellNumber), null);
                throw new ProjectParserException(
                        e.getMessage() + " with field: " + CELLS.valueOfCell(cellNumber).label);
            } catch (Exception e1) {
                // for instance a case of a null value don't do nothing for that field
                rowValues.put(CELLS.valueOfCell(cellNumber), null);
            }

        }
        return rowValues;
    }

    private Double parseNumericCell(Cell cell) throws ProjectParserException {
        Double value = 0d;

        if (cell == null) {
            return value;
        }

        switch (cell.getCellTypeEnum()) {
        case NUMERIC:
            value = cell.getNumericCellValue();
            break;
        case STRING:
            // if value is a numeric string and has a ,
            String cellStringValue = cell.getStringCellValue().replaceAll(",", ".");
            try {
                value = Double.parseDouble(cellStringValue);
            } catch (NumberFormatException e) {
                // don't do nothing since value will be 0
                logger.error("error parsing numeric cell");
            }
            break;
        default:
            break;
        }

        return value;
    }

    private String parseStringCell(Cell cell) throws ProjectParserException {
        String value = "";

        if (cell == null) {
            return value;
        }

        switch (cell.getCellTypeEnum()) {
        case STRING:
            try {
                value = cell.getStringCellValue();
            } catch (Exception e) {
                // don't do nothing since value will be empty string
                throw new ProjectParserException("error parsing string cell");
            }
            break;
        default:
            break;
        }

        return value;
    }

    private Date parseDateCell(Cell cell) throws ProjectParserException {
        Date value = new Date();

        if (cell == null) {
            return value;
        }

        switch (cell.getCellTypeEnum()) {
        default:
            try {
                value = cell.getDateCellValue();
            } catch (Exception e) {
                // don't do nothing since value will be empty string
                throw new ProjectParserException("error parsing date cell");
            }
            break;
        }

        return value;
    }

}
