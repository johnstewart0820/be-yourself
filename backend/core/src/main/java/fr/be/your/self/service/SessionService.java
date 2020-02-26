package fr.be.your.self.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.model.Session;

public interface SessionService extends BaseService<Session, Integer> {
	
	public long count(String text, List<Integer> categoryIds);

	public PageableResponse<Session> pageableSearch(String text, List<Integer> categoryIds, Pageable pageable, Sort sort);

	public List<Session> search(String text, List<Integer> categoryIds, Sort sort);
}
