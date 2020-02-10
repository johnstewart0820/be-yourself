package fr.be.your.self.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.service.BaseService;

public abstract class BaseServiceImpl<T> implements BaseService<T> {
	
	protected abstract PagingAndSortingRepository<T, Integer> getRepository();
	
	protected abstract Iterable<T> findAll(String text);
	
	protected abstract Page<T> findPage(String text, Pageable pageable);
	
	@Override
	@Transactional(readOnly = true)
	public T getById(Integer id) {
		final Optional<T> domain = this.getRepository().findById(id);
		return domain.isPresent() ? domain.get() : null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<T> search(String text) {
		final Iterable<T> domains = this.findAll(text);
		
		final List<T> result = new ArrayList<>();
		domains.forEach(result::add);
		
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public PageableResponse<T> pageableSearch(String text, Pageable pageable) throws RuntimeException {
		if (pageable == null) {
			return null;
		}
		
		final Page<T> pageDomain = this.findPage(text, pageable);
		if (pageDomain == null) {
			return null;
		}
		
		final int pageIndex = pageable.getPageNumber() + 1;
		final int pageSize = pageable.getPageSize();
		
		final PageableResponse<T> result = new PageableResponse<>();
		result.setItems(pageDomain.getContent());
		result.setTotalItems(pageDomain.getTotalElements());
		result.setTotalPages(pageDomain.getTotalPages());
		result.setPageIndex(pageIndex);
		result.setPageSize(pageSize);
		
		return result;
	}
	
	@Override
	@Transactional
	public T create(T domain) {
		return this.getRepository().save(domain);
	}
	
	@Override
	@Transactional
	public T update(T domain) {
		return this.getRepository().save(domain);
	}
	
	@Override
	@Transactional
	public boolean delete(Integer id) {
		this.getRepository().deleteById(id);
		return true;
	}
}
