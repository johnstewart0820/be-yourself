package fr.be.your.self.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.repository.BaseRepository;
import fr.be.your.self.service.BaseService;

public abstract class BaseServiceImpl<T, K extends Serializable> implements BaseService<T, K> {
	
	protected final static String TEXT_SEARCH_KEY = "q";
	
	protected abstract BaseRepository<T, K> getRepository();
	
	protected abstract Iterable<T> getList(String text, Sort sort);
	
	protected abstract Page<T> getListByPage(String text, Pageable pageable);
	
	@Override
	@Transactional(readOnly = true)
	public T getById(K id) {
		final Optional<T> domain = this.getRepository().findById(id);
		return domain.isPresent() ? domain.get() : null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<T> getByIds(Collection<K> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}
		
		final Iterable<T> domains = this.getRepository().findAllById(ids);
		if (domains == null) {
			return Collections.emptyList();
		}
		
		final List<T> result = new ArrayList<>();
		domains.forEach(result::add);
		
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<T> getByIds(@SuppressWarnings("unchecked") K...ids) {
		if (ids == null || ids.length == 0) {
			return Collections.emptyList();
		}
		
		final List<K> domainIds = Arrays.stream(ids).collect(Collectors.toList());
		return this.getByIds(domainIds);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<T> getAll(Sort sort) {
		final Iterable<T> domains = this.getRepository().findAll(sort == null ? Sort.unsorted() : sort);
		
		if (domains == null) {
			return Collections.emptyList();
		}
		
		final List<T> result = new ArrayList<>();
		domains.forEach(result::add);
		
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<T> search(String text, Sort sort) {
		final Iterable<T> domains = this.getList(text, sort == null ? Sort.unsorted() : sort);
		
		if (domains == null) {
			return Collections.emptyList();
		}
		
		final List<T> result = new ArrayList<>();
		domains.forEach(result::add);
		
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public PageableResponse<T> pageableSearch(String text, Pageable pageable, Sort sort) throws RuntimeException {
		if (pageable == null) {
			final List<T> items = this.search(text, sort);
			
			final PageableResponse<T> result = new PageableResponse<>();
			result.setItems(items);
			result.setTotalItems(items.size());
			result.setTotalPages(1);
			result.setPageIndex(1);
			result.setPageSize(-1);
			
			return result;
		}
		
		final Page<T> pageDomain = this.getListByPage(text, pageable);
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
	public boolean delete(K id) {
		this.getRepository().deleteById(id);
		return true;
	}

	@Override
	public Iterable<T> findAll() {
		return this.getRepository().findAll();
	}

	@Override
	public Page<T> getPaginatedUsers(Pageable pageable) {
		return this.getRepository().findAll(pageable);
	}

	@Override
	public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
		return this.getRepository().saveAll(entities);
	}
}
