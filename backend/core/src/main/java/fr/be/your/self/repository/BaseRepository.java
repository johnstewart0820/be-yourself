package fr.be.your.self.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface BaseRepository<T> extends PagingAndSortingRepository<T, Integer> {

}
