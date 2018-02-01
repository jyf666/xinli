/**
 * www.net-future.com.cn
 * GenericDateFormat.java
 * @author fht
 * @since 2014-01-23 17:01:03
 */
package utils.json.support;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import utils.DateUtils;

/**
 * GenericDateFormat
 * @author lcd
 */
public class GenericDateFormat extends DateFormat {
	private static final long serialVersionUID = -535311322420097236L;

	private static final String dateTimeFormatPattern = DateUtils.dateTimeFormatPattern;
	private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateTimeFormatPattern);
	private static final String dateTimeFormatRegex = "\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}";

	private static final String dateTimeFormatPattern2 = DateUtils.dateTimeFormatPattern2;
	private static final SimpleDateFormat dateTimeFormat2 = new SimpleDateFormat(dateTimeFormatPattern2);
	private static final String dateTimeFormatRegex2 = "\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{1,3}";
	
	private static final String dateFormatPattern = DateUtils.dateFormatPattern;
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
	private static final String dateFormatRegex = "\\d{4}-\\d{1,2}-\\d{1,2}";
	
	public static final GenericDateFormat instance = new GenericDateFormat();
	
	public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
		if(date != null){
			return dateTimeFormat.format(date, toAppendTo, fieldPosition);
		}
		return null;
	}

	public Date parse(String source, ParsePosition pos) {
		if(source.matches(dateTimeFormatRegex)){
			return dateTimeFormat.parse(source, pos);
		}
		if(source.matches(dateTimeFormatRegex2)){
			return dateTimeFormat2.parse(source, pos);
		}
		if(source.matches(dateFormatRegex)){
			return dateFormat.parse(source, pos);
		}
		throw new RuntimeException(new ParseException(String.format("Can not parse date \"%s\": not compatible with any of standard forms (%s)",
			source, dateTimeFormatPattern + "," + dateTimeFormatPattern2 + "," + dateFormatPattern), pos.getErrorIndex()));
	}
	
	public GenericDateFormat clone() {
		return new GenericDateFormat();
	}

}
