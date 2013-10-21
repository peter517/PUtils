package com.pengjun.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class POIUtils {
	public static void main(String[] args) {
		try {

			List<String> strList = readWordDocx("d://test.docx");
			for (String str : strList) {
				System.out.println(str);
			}

			writeWordDocx("d://test1.docx", strList);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> readWordDocx(String sourceFileFullPath)
			throws Exception {

		ArrayList<String> strList = new ArrayList<String>();

		File docFile = new File(sourceFileFullPath);
		FileInputStream fis = null;
		if (!docFile.exists()) {
			throw new FileNotFoundException("The Word dcoument "
					+ sourceFileFullPath + " does not exist.");
		}
		try {
			fis = new FileInputStream(docFile);
			XWPFDocument doc = new XWPFDocument(fis);
			List<XWPFParagraph> paragraphs = doc.getParagraphs();
			Iterator<XWPFParagraph> it = paragraphs.iterator();
			while (it.hasNext()) {
				XWPFParagraph p = it.next();
				strList.add(p.getText());
			}
		} finally {
			if (fis != null) {
				try {
					fis.close();
					fis = null;
				} catch (IOException ioEx) {
					ioEx.printStackTrace();
				}
			}
		}

		return strList;
	}

	public static void writeWordDocx(String fileFullPath, List<String> strList) {
		writeWordDocx(fileFullPath, strList, false, -1);
	}

	public static void writeWordDocx(String fileFullPath, List<String> strList,
			boolean indent, int indentUnit) {

		XWPFDocument document = new XWPFDocument();
		FileOutputStream outStream = null;
		try {
			outStream = new FileOutputStream(fileFullPath);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		// XWPFTable table = document.createTable(2, 3);
		// // 设置上下左右四个方向的距离，可以将表格撑大
		// table.setCellMargins(100, 100, 50, 250);
		// // table.set
		// List<XWPFTableCell> tableCells = table.getRow(0).getTableCells();
		// tableCells.get(0).setText("( )1. A. room");
		// tableCells.get(1).setText("B. ruler");
		// tableCells.get(2).setText("C. school");
		//
		// List<XWPFTableCell> tableCellsq = table.getRow(1).getTableCells();
		// tableCellsq.get(0).setText("( )2. A. do");
		// tableCellsq.get(1).setText("B. too");
		// tableCellsq.get(2).setText("C. book");

		// for (int i = 0; i < 10; i++) {
		// XWPFParagraph p = document.createParagraph();
		// XWPFRun r1 = p.createRun();
		// r1.setFontFamily("Times New Roman");
		// r1.setFontSize(10);
		// r1.setText("A.a   B.b   C.c   D.d");
		// }

		for (String str : strList) {

			XWPFParagraph paragraph = document.createParagraph();
			if (indent) {
				paragraph.setIndentationFirstLine(getStartingBlankCharsNum(str)
						* indentUnit);// indent如果太小，看不出来！因此乘一个因子
			}
			//
			XWPFRun r = paragraph.createRun();
			// r.setColor(...);
			r.setFontFamily("Times New Roman");
			r.setFontSize(10);
			if (str.startsWith("一、") || str.startsWith("二、")
					|| str.startsWith("三、") || str.startsWith("四、")
					|| str.startsWith("五、") || str.startsWith("六、")
					|| str.startsWith("七、") || str.startsWith("八、")
					|| str.startsWith("九、") || str.startsWith("十、")) {
				r.setBold(true);
			}
			// r.setUnderline(u);
			if (str == null) {
				r.setText("");
			} else {
				r.setText(str.trim());
			}
		}

		try {
			document.write(outStream);
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static int getStartingBlankCharsNum(String s) {
		if (s != null && s.length() > 0) {
			int count = 0;
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) == ' ') {
					count++;
				} else if (s.charAt(i) == '\t') {
					count = count + 4;
				} else {
					break;
				}
			}
			return count;
		}
		return 0;
	}
}
