package com.doyatama.university.service;

import com.doyatama.university.model.Lecture;
import com.doyatama.university.model.RPS;
import com.doyatama.university.model.RPSDetail;
import com.doyatama.university.model.Subject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ExcelUploadService {
    public static boolean isValidExcelFile(MultipartFile file) {
        return Objects.equals(file.getContentType(),
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public static List<RPS> getRPSDataFromExcel(InputStream inputStream) {
        List<RPS> rpsList = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            // Try to get sheet by name "RPS" or default to first sheet
            XSSFSheet sheet = workbook.getSheet("RPS");
            if (sheet == null && workbook.getNumberOfSheets() > 0) {
                sheet = workbook.getSheetAt(0);
            }

            if (sheet == null) {
                workbook.close();
                throw new IllegalArgumentException("Tidak ada sheet yang ditemukan dalam file Excel");
            }

            int rowIndex = 0;
            for (Row row : sheet) {
                // Skip header row
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }

                // Skip empty rows
                if (row.getPhysicalNumberOfCells() == 0) {
                    continue;
                }

                try {
                    Iterator<Cell> cellIterator = row.iterator();
                    int cellIndex = 0;
                    RPS rps = new RPS();

                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();

                        try {
                            switch (cellIndex) {
                                case 0: // ID
                                    if (cell.getCellType() == CellType.STRING) {
                                        rps.setId(cell.getStringCellValue().trim());
                                    }
                                    break;
                                case 1: // Name
                                    if (cell.getCellType() == CellType.STRING) {
                                        rps.setName(cell.getStringCellValue().trim());
                                    }
                                    break;
                                case 2: // SKS
                                    if (cell.getCellType() == CellType.NUMERIC) {
                                        rps.setSks((int) cell.getNumericCellValue());
                                    }
                                    break;
                                case 3: // Semester
                                    if (cell.getCellType() == CellType.NUMERIC) {
                                        rps.setSemester((int) cell.getNumericCellValue());
                                    }
                                    break;
                                case 4: // CPL Prodi
                                    if (cell.getCellType() == CellType.STRING) {
                                        rps.setCpl_prodi(cell.getStringCellValue().trim());
                                    }
                                    break;
                                case 5: // CPL MK
                                    if (cell.getCellType() == CellType.STRING) {
                                        rps.setCpl_mk(cell.getStringCellValue().trim());
                                    }
                                    break;
                                case 6: // Subject
                                    if (cell.getCellType() == CellType.STRING) {
                                        String subjectName = cell.getStringCellValue().trim();
                                        Subject subject = new Subject();
                                        subject.setName(subjectName);
                                        rps.setSubject(subject);
                                    }
                                    break;
                                case 7: // Dev Lecturers
                                    if (cell.getCellType() == CellType.STRING) {
                                        String[] devLecturersArray = cell.getStringCellValue().split("[;,]");
                                        List<Lecture> devLecturers = new ArrayList<>();
                                        for (String lecturerName : devLecturersArray) {
                                            String trimmedName = lecturerName.trim();
                                            if (!trimmedName.isEmpty()) {
                                                Lecture lecture = new Lecture();
                                                lecture.setName(trimmedName);
                                                devLecturers.add(lecture);
                                            }
                                        }
                                        rps.setDev_lecturers(devLecturers);
                                    }
                                    break;

                                default:
                                    break;
                            }
                        } catch (Exception cellEx) {
                            // Log cell parsing error but continue
                            System.err.println("Error parsing cell " + cellIndex + " in row " + rowIndex + ": "
                                    + cellEx.getMessage());
                        }

                        cellIndex++;
                    }

                    // Only add if ID and Name are present
                    if (rps.getId() != null && !rps.getId().isEmpty() &&
                            rps.getName() != null && !rps.getName().isEmpty()) {
                        rpsList.add(rps);
                    }

                } catch (Exception rowEx) {
                    // Log row parsing error but continue
                    System.err.println("Error parsing row " + rowIndex + ": " + rowEx.getMessage());
                }

                rowIndex++;
            }
            workbook.close();
        } catch (Exception e) {
            System.err.println("Error reading Excel file: " + e.getMessage());
            e.printStackTrace();
            // Return empty list if parsing fails completely
        }
        return rpsList;
    }

    public static List<RPSDetail> getRPSDetailDataFromExcel(InputStream inputStream) {
        List<RPSDetail> rpsDetailList = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("RPSDetail");

            if (sheet == null) {
                workbook.close();
                throw new IllegalArgumentException("Sheet 'RPSDetail' does not exist in the Excel file.");
            }

            int rowIndex = 0;
            for (Row row : sheet) {
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                Iterator<Cell> cellIterator = row.iterator();
                int cellIndex = 0;
                RPSDetail rpsDetail = new RPSDetail();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cellIndex) {
                        case 0:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                rpsDetail.setId(String.valueOf((int) cell.getNumericCellValue()));
                            } else {
                                rpsDetail.setId(cell.getStringCellValue());
                            }
                            break;
                        case 1:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                rpsDetail.setWeek((int) cell.getNumericCellValue()); // Set week as Integer
                            } else if (cell.getCellType() == CellType.STRING) {
                                try {
                                    rpsDetail.setWeek(Integer.parseInt(cell.getStringCellValue())); // Parse string to
                                                                                                    // Integer
                                } catch (NumberFormatException e) {
                                    // Handle the case where the string cannot be parsed to an integer
                                }
                            }
                            break;
                        case 2:
                            String rpsId = cell.getStringCellValue().trim();
                            cell = cellIterator.next(); // Move to the next cell for subject name
                            String rpsName = cell.getStringCellValue().trim();
                            RPS rps = new RPS();
                            rps.setId(rpsId);
                            rps.setName(rpsName);
                            rpsDetail.setRps(rps);
                            break;
                        case 3:
                            rpsDetail.setSub_cp_mk(cell.getStringCellValue());
                            break;
                        case 4:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                rpsDetail.setWeight((float) cell.getNumericCellValue());
                            } else {
                                rpsDetail.setWeight(Float.parseFloat(cell.getStringCellValue()));
                            }
                            break;
                        case 5: // Assuming learning materials are in the 7th column
                            List<String> learningMaterials = rpsDetail.getLearning_materials();
                            if (learningMaterials == null) {
                                learningMaterials = new ArrayList<>();
                            }
                            String[] materials = cell.getStringCellValue().split(";"); // Assuming materials are
                                                                                       // semicolon-separated
                            for (String material : materials) {
                                learningMaterials.add(material.trim());
                            }
                            rpsDetail.setLearning_materials(learningMaterials);
                            break;
                        default:
                            break;
                    }
                    cellIndex++;
                }
                rpsDetailList.add(rpsDetail);
                rowIndex++;
            }
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rpsDetailList;
    }

}