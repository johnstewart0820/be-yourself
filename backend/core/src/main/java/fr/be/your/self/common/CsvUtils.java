package fr.be.your.self.common;

import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.MappingStrategy;

import fr.be.your.self.model.Subscription;
import fr.be.your.self.model.SubscriptionCsv;


public enum CsvUtils {
	SINGLETON;
	
	public <T> List<T> readCsvFile(MultipartFile file, Class clazz) throws Exception {
		Reader reader = new InputStreamReader(file.getInputStream());
		CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(0).build();

		MappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
		strategy.setType(clazz);

		CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(csvReader).withType(clazz)
				.withMappingStrategy(strategy).build();
		List<T> results = csvToBean.parse();
		return results;
	}

	public Subscription createSubscriptionFromCsv(SubscriptionCsv subCsv) throws ParseException {
		Subscription sub = new Subscription();
		sub.setCanal(subCsv.getCanal());
		sub.setCode(subCsv.getCode());
		sub.setDuration(subCsv.getDuration());
		
		sub.setSubscriptionEndDate(getSqlDate(subCsv.getEndDate()));
		sub.setPaymentGateway(subCsv.getPaymenGateway());
		sub.setPrice(subCsv.getPrice());
		sub.setSubscriptionStartDate(getSqlDate(subCsv.getStartDate()));
		sub.setTerminationAsked(subCsv.isTerminationAsked());
		return sub;
	}

	private java.sql.Date getSqlDate(String dateStr) throws ParseException {
		String pattern = SubscriptionCsv.DATE_FORMAT;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		java.sql.Date date = new java.sql.Date(simpleDateFormat.parse(dateStr).getTime());
		return date;
	}
}
