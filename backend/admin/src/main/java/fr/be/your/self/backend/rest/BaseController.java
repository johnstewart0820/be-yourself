package fr.be.your.self.backend.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.be.your.self.backend.setting.DataSetting;
import fr.be.your.self.common.StatusCode;
import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.exception.BusinessException;
import fr.be.your.self.exception.InvalidException;
import fr.be.your.self.service.BaseService;

public abstract class BaseController<T> {
	
	@Autowired
	protected DataSetting dataSetting;
	
	protected abstract BaseService<T> getService();
	
	@GetMapping("")
	public ResponseEntity<?> search(
			HttpServletRequest request,
    		@RequestParam(value = "q", required = false) String search,
    		@RequestParam(required = false) String sort) throws RuntimeException {
		
		final List<T> result = this.getService().search(search);
		if (result == null) {
			throw new BusinessException(StatusCode.PROCESSING_ERROR);
		}
		
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/paging")
	public ResponseEntity<?> pageableSearch(
			HttpServletRequest request,
    		@RequestParam(value = "q", required = false) String search,
    		@RequestParam(required = false) String sort,
    		@RequestParam(required = false) Integer page,
    		@RequestParam(required = false) Integer size) throws RuntimeException {
		
		final PageRequest pageable = this.getPageRequest(page, size);
		if (pageable == null) {
			throw new InvalidException(StatusCode.INVALID_PARAMETER, "page");
		}
		
		final PageableResponse<T> result = this.getService().pageableSearch(search, pageable);
		if (result == null) {
			throw new BusinessException(StatusCode.PROCESSING_ERROR);
		}
		
		return ResponseEntity.ok(result);
	}
	
	protected final PageRequest getPageRequest(Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }

        if (size == null || size < 1) {
            size = this.dataSetting.getDefaultPageSize();
        }
        
        return PageRequest.of(page - 1, size);
    }
	
}
