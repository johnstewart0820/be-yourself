package fr.be.your.self.model;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class SubscriptionCsvMappingStrategy <T> extends ColumnPositionMappingStrategy<T> {
	 private static final String[] HEADER = new String[]{"Title",	"Last name",	"First name",
			 "Email",	"Subscription type",	"Canal",	"code",	"code type",	
			 "duration",	"start date",	"end date",	"termination asked",	"price",
			 "gift card",	"payment gateway"	};

	@Override
	public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
		super.generateHeader(bean);
		return HEADER;
	}
}
