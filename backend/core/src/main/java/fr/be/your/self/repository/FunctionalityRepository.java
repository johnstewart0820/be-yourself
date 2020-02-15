package fr.be.your.self.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import fr.be.your.self.model.Functionality;

@Repository
public interface FunctionalityRepository extends PagingAndSortingRepository<Functionality,Integer> {

}
