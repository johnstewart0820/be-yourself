package fr.be.your.self.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import fr.be.your.self.model.Functionality;

@Repository
public interface FunctionalityRepository extends PagingAndSortingRepository<Functionality,Integer> {
	
	public Optional<Functionality> findByPath(String path);
	
}
