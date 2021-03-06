package fr.be.your.self.service;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import fr.be.your.self.dto.PageableResponse;

public interface BaseService<T> {
	
	/**
	 * @return default data sort, format column|{asc|desc}
	 **/
	public String getDefaultSort();
	
	public T getById(Integer id);

	public List<T> getByIds(Collection<Integer> ids);
	
	public List<T> getByIds(int...ids);
	
	public List<T> getAll(Sort sort);
	
	public long count(String text);

	public PageableResponse<T> pageableSearch(String text, Pageable pageable, Sort sort);

	public List<T> search(String text, Sort sort);
	
	public T create(T domain);

	public T update(T domain);

	public boolean delete(Integer id);

	public Iterable<T> findAll();
	
	// TODO: TVA - Change method name
	public Page<T> getPaginatedUsers(Pageable pageable);

	public <S extends T> Iterable<S> saveAll(Iterable<S> entities);
}
