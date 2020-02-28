package fr.be.your.self.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.model.Session;
import fr.be.your.self.repository.BaseRepository;
import fr.be.your.self.repository.SessionRepository;
import fr.be.your.self.service.SessionService;
import fr.be.your.self.util.StringUtils;

@Service
public class SessionServiceImpl extends BaseServiceImpl<Session, Integer> implements SessionService {
	
	@Autowired
	private SessionRepository repository;

	@Override
	protected BaseRepository<Session, Integer> getRepository() {
		return this.repository;
	}
	
	@Override
	public String getDefaultSort() {
		return "title|asc";
	}
	
	@Override
	@Transactional(readOnly = true)
	public long count(String text) {
		if (StringUtils.isNullOrSpace(text)) {
			return this.repository.count();
		}
		
		return this.repository.countByTitleContainsIgnoreCase(text);
	}

	@Override
	protected Iterable<Session> getList(String text, Sort sort) {
		return StringUtils.isNullOrSpace(text) 
				? this.repository.findAll(sort) 
				: this.repository.findAllByTitleContainsIgnoreCase(text, sort);
	}

	@Override
	protected Page<Session> getListByPage(String text, Pageable pageable) {
		return StringUtils.isNullOrSpace(text) 
				? this.repository.findAll(pageable)
				: this.repository.findAllByTitleContainsIgnoreCase(text, pageable);
	}

	@Override
	public long count(String text, List<Integer> categoryIds, Collection<Integer> voiceIds) {
		if (categoryIds == null || categoryIds.isEmpty()) {
			
			if (voiceIds == null || voiceIds.isEmpty()) {
				return this.count(text);
			}
			
			return this.repository.countByVoice(text, voiceIds);
		}
		
		if (voiceIds == null || voiceIds.isEmpty()) {
			return this.repository.countByCategory(text, categoryIds);
		}
		
		return this.repository.count(text, categoryIds, voiceIds);
	}

	@Override
	public PageableResponse<Session> pageableSearch(String text, 
			Collection<Integer> categoryIds, Collection<Integer> voiceIds, 
			Pageable pageable, Sort sort) {
		if (categoryIds == null || categoryIds.isEmpty()) {
			if (voiceIds == null || voiceIds.isEmpty()) {
				return this.pageableSearch(text, pageable, sort);
			}
		}
		
		if (pageable == null) {
			final List<Session> items = this.search(text, categoryIds, voiceIds, sort);
			
			final PageableResponse<Session> result = new PageableResponse<>();
			result.setItems(items);
			result.setTotalItems(items.size());
			result.setTotalPages(1);
			result.setPageIndex(1);
			result.setPageSize(-1);
			
			return result;
		}
		
		Page<Session> pageDomain;
		if (categoryIds == null || categoryIds.isEmpty()) {
			pageDomain = this.repository.findAllByVoice(text, voiceIds, pageable);
		} else if (voiceIds == null || voiceIds.isEmpty()) {
			pageDomain = this.repository.findAllByCategory(text, categoryIds, pageable);
		} else {
			pageDomain = this.repository.findAll(text, categoryIds, voiceIds, pageable);
		}
		
		if (pageDomain == null) {
			return null;
		}
		
		final int pageIndex = pageable.getPageNumber() + 1;
		final int pageSize = pageable.getPageSize();
		
		final PageableResponse<Session> result = new PageableResponse<>();
		result.setItems(pageDomain.getContent());
		result.setTotalItems(pageDomain.getTotalElements());
		result.setTotalPages(pageDomain.getTotalPages());
		result.setPageIndex(pageIndex);
		result.setPageSize(pageSize);
		
		return result;
	}

	@Override
	public List<Session> search(String text, Collection<Integer> categoryIds, Collection<Integer> voiceIds, Sort sort) {
		Iterable<Session> domains;
		
		final Sort domainSort = sort == null ? Sort.unsorted() : sort;
		if (categoryIds == null || categoryIds.isEmpty()) {
			if (voiceIds == null || voiceIds.isEmpty()) {
				return this.search(text, sort);
			}
			
			domains = this.repository.findAllByVoice(text, voiceIds, domainSort); 
		} else if (voiceIds == null || voiceIds.isEmpty()) {
			domains = this.repository.findAllByCategory(text, categoryIds, domainSort);
		} else {
			domains = this.repository.findAll(text, categoryIds, voiceIds, domainSort);	
		}
		
		if (domains == null) {
			return Collections.emptyList();
		}
		
		final List<Session> result = new ArrayList<>();
		domains.forEach(result::add);
		
		return result;
	}
}
