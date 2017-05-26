package edu.thu.rlab.datamanager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader
{
  private Workbook workBook;
  private Sheet sheet;
  private Row row;
  private Cell cell;
  private String string;

  public ExcelReader(String filename)
  {
    this.workBook = null;
    if (filename == null) {
      return;
    }

    String extension = filename.substring(filename.lastIndexOf("."));
    try {
      InputStream inputStream = new FileInputStream(filename);
      if (extension.equals(".xls"))
      {
        this.workBook = new HSSFWorkbook(inputStream);
      } else if (extension.equals(".xlsx"))
      {
        this.workBook = new XSSFWorkbook(inputStream);
      }
    } catch (FileNotFoundException e) {
      System.out.print("file not found");
    } catch (IOException e) {
      System.out.println("io Exception");
    }
  }

  public List<List<String>> readExcelSheet(int num)
  {
	  List<List<String>> content = new ArrayList<List<String>>();

    if (this.workBook == null) {
      return content;
    }
    this.sheet = this.workBook.getSheetAt(num);
    int rowNum = this.sheet.getLastRowNum();
    this.row = this.sheet.getRow(0);

    int colNum = 7;

    for (int i = 1; i < rowNum; i++) {
      this.row = this.sheet.getRow(i);
      List<String> line = new ArrayList<String>();
      for (int j = 0; j < colNum; j++) {
        this.cell = this.row.getCell(j);
        if (this.cell.getCellType() == cell.CELL_TYPE_STRING) {
          this.string = this.cell.getStringCellValue();

          if (this.string.equals("")) {
            this.string = "-";
          }
        }
        else
        {
          this.string = "-";
        }
        line.add(this.string);
      }
      content.add(line);
    }
    return content;
  }
}