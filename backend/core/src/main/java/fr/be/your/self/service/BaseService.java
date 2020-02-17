package fr.be.your.self.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.be.your.self.dto.PageableResponse;

public interface BaseService<T> {

	public T getById(Integer id);

	public long count(String text);

	public PageableResponse<T> pageableSearch(String text, Pageable pageable);

	public List<T> search(String text);

	public T create(T domain);

	public T update(T domain);

	public boolean delete(Integer id);

	public Iterable<T> findAll();

	public Page<T> getPaginatedUsers(Pageable pageable);

	public <S extends T> Iterable<S> saveAll(Iterable<S> entities);
}
