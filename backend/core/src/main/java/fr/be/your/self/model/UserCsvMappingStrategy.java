package fr.be.your.self.model;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class UserCsvMappingStrategy <T> extends ColumnPositionMappingStrategy<T> {
    private static final String[] HEADER = new String[]{"Title",	"Last name", "First name",	"Email",
    										"Login type",	"Status",	"Referral code",	
    										"Account type",	"Subscription type"	};

    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
    	super.generateHeader(bean);
        return HEADER;
    }
}