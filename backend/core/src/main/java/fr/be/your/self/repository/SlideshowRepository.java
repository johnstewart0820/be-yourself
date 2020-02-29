package fr.be.your.self.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import fr.be.your.self.model.Slideshow;

@Repository
public interface SlideshowRepository extends BaseRepository<Slideshow, Integer> {
	
	long countByStartDateGreaterThanEqual(Date date);
    Iterable<Slideshow> findAllByStartDateGreaterThanEqual(Date date, Sort sort);
    Page<Slideshow> findAllByStartDateGreaterThanEqual(Date date, Pageable pageable);
    
    long countByStartDateIsNotNull();
    Iterable<Slideshow> findAllByStartDateIsNotNull(Sort sort);
    Page<Slideshow> findAllByStartDateIsNotNull(Pageable pageable);
    
    Iterable<Slideshow> findAllByStartDateIsNull();
}
