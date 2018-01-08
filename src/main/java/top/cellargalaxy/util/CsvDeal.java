package top.cellargalaxy.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cellargalaxy on 18-1-2.
 */
@Component
public class CsvDeal {
	public static final String DATE_FORMAT_STRING = "yyyy/MM/dd";
	private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
	private static final String coding = "utf-8";
	private static final CSVFormat csvFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");
	
	public static final Iterable<CSVRecord> createCSVRecords(File file) {
		try {
			if (file == null || !file.exists()) {
				return null;
			}
			return CSVFormat.EXCEL.withHeader().parse(new BufferedReader(new InputStreamReader(new FileInputStream(file), coding)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final CSVPrinter createCSVPrinter(File file) {
		try {
			if (file == null) {
				return null;
			}
			file.getParentFile().mkdirs();
			return new CSVPrinter(new OutputStreamWriter(new FileOutputStream(file), coding), csvFormat);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final Date string2Date(String string) {
		try {
			return dateFormat.parse(string);
		} catch (Exception e) {
		}
		return null;
	}
	
	public static final String date2String(Date date) {
		try {
			return dateFormat.format(date);
		} catch (Exception e) {
		}
		return "";
	}
	
	public static final Integer string2Int(String string) {
		try {
			return new Integer(string);
		} catch (Exception e) {
		}
		return null;
	}
}
