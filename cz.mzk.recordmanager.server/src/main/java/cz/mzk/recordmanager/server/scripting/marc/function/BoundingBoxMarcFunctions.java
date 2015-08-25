package cz.mzk.recordmanager.server.scripting.marc.function;

import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marc4j.marc.DataField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cz.mzk.recordmanager.server.marc.MarcRecord;
import cz.mzk.recordmanager.server.scripting.marc.MarcFunctionContext;

@Component
public class BoundingBoxMarcFunctions implements MarcRecordFunctions {

	private static Logger logger = LoggerFactory
			.getLogger(BoundingBoxMarcFunctions.class);

	private static final String WEST = "w";

	private static final String SOUTH = "s";

	private static final Pattern PATTERN = Pattern
			.compile("([eEwWnNsS]{1})(\\d{3})(\\d{2})(\\d{2})");

	public String getBoundingBoxAsPolygon(MarcRecord rec) {
		double points[] =  parseBoundingBox(rec);
		if (points == null) {
			return null;
		}
		return MessageFormat.format("POLYGON(({0} {1}, {2} {1}, {2} {3}, {0} {3}, {0} {1}))",
				points[0], points[1], points[2], points[3]);
	}

	public String getBoundingBoxAsPolygon(MarcFunctionContext ctx) {
		MarcRecord record = ctx.record();
		return getBoundingBoxAsPolygon(record);
	}

	public String getBoundingBox(MarcFunctionContext ctx) {
		return getBoundingBox(ctx.record());
	}

	public String getBoundingBox(MarcRecord record) {
		double points[] =  parseBoundingBox(record);
		if (points == null) {
			return null;
		}
		return String.format("%s %s %s %s",
				points[0], points[1], points[2], points[3]);
	}

	protected double[] parseBoundingBox(MarcRecord record) {
		List<DataField> fields = record.getAllFields().get("034");
		if (fields == null || fields.isEmpty()) {
			return null;
		}
		DataField field = fields.get(0);
		String origWest = (field.getSubfield('d') == null) ? null : field.getSubfield('d').getData();
		String origEast = (field.getSubfield('e') == null) ? null : field.getSubfield('e').getData();
		String origNorth = (field.getSubfield('f') == null) ? null : field.getSubfield('f').getData();
		String origSouth = (field.getSubfield('g') == null) ? null : field.getSubfield('g').getData();
		double west = coordinateToDecimal(origWest);
		double east = coordinateToDecimal(origEast);
		double north = coordinateToDecimal(origNorth);
		double south = coordinateToDecimal(origSouth);
		double longitude = Double.NaN;
		double latitude = Double.NaN;
		if (!Double.isNaN(west) && !Double.isNaN(north)) {
			if (!Double.isNaN(east)) {
				longitude = (west + east) / 2.0;
			} else {
				longitude = west;
			}

			if (!Double.isNaN(south)) {
				latitude = (north + south) / 2;
			} else {
				latitude = north;
			}
			if ((longitude < -180 || longitude > 180)
					|| (latitude < -90 || latitude > 90)) {
				logger.warn(
						"Discarding invalid coordinates {}, {}  decoded from w={}, e={}, n={}, s={}",
						longitude, latitude, origWest, origEast, origNorth,
						origSouth);
			} else {
				if (Double.isNaN(north) || Double.isNaN(south)
						|| Double.isNaN(east) || Double.isNaN(north)) {
					logger.warn(
							"INVALID RECORD missig coordinate w={} e={} n={} s={}",
							origWest, origEast, origNorth, origSouth);
				} else {
					if (west < east && south < north) {
						return new double[]{ west, south, east,
								north};
					} else {
						logger.warn(
								"INVALID RECORD missig coordinate w={} e={} n={} s={}",
								origWest, origEast, origNorth, origSouth);
					}
				}
			}
		}
		return null;
	}

	public static double coordinateToDecimal(String value) {
		if (value == null || value.isEmpty()) {
			return (float) Double.NaN;
		}
		value = value.trim();
		Matcher matcher = PATTERN.matcher(value);
		if (!matcher.matches()) {
			try {
				return Double.parseDouble(value);
			} catch (NumberFormatException nfe) {
				return Double.NaN;
			}
		}
		int hours = Integer.valueOf(matcher.group(2));
		int minutes = Integer.valueOf(matcher.group(3));
		int seconds = Integer.valueOf(matcher.group(4));
		double loc = hours + (minutes / 60.0) + (seconds / 3600.0);
		String hemisphere = matcher.group(1).toLowerCase();
		if (WEST.equals(hemisphere) || SOUTH.equals(hemisphere)) {
			loc *= -1.0;
		}
		return loc;
	}

}
