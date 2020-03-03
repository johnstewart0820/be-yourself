package fr.be.your.self.service;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.model.BusinessCode;

public interface BusinessCodeService extends BaseService<BusinessCode, Integer> {

	public boolean existsName(String name);

	public long count(String text, Collection<Integer> types);

	public PageableResponse<BusinessCode> pageableSearch(String text, Collection<Integer> types, 
			Pageable pageable, Sort sort);

	public List<BusinessCode> search(String text, Collection<Integer> types, Sort sort);
}
