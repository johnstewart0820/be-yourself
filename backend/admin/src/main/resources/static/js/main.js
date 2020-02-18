function previewImage(inputElement, previewElementId) {
	if (!inputElement.files || !inputElement.files[0]) {
		return;
	}
		
	var reader = new FileReader();
	
	reader.onload = function(e) {
		$('#' + previewElementId).attr('src', e.target.result);
	}
	
	reader.readAsDataURL(inputElement.files[0]);
}

function validateAndPreviewImage(inputElement, previewElementId, labelElementId, defaultLabel, 
		supportFileType, maxFileSize, fileTypeModalId, fileSizeModalId) {
	
	if (!inputElement.files || !inputElement.files[0]) {
		return null;
	}
	
	var file = inputElement.files[0];
	var fileType = file.type;
    var fileSize = file.size;
    var fileName = file.name;
    
    if (supportFileType && supportFileType.indexOf(fileType) < 0) {
    	resetImageElement(inputElement, previewElementId, labelElementId, defaultLabel);
    	
    	showModal(fileTypeModalId);
    	
    	return null;
    }
    
    if (maxFileSize && maxFileSize > 0 && fileSize > maxFileSize) {
    	resetImageElement(inputElement, previewElementId, labelElementId, defaultLabel)
    	
    	showModal(fileSizeModalId);
    	
    	return null;
    }
    
    previewImage(inputElement, previewElementId);
    
    if (labelElementId) {
		$('#' + labelElementId).text(fileName);
	}
    
    return fileName;
}

function resetImageElement(inputElement, previewElementId, labelElementId, defaultLabel) {
	inputElement.value = null;
	
	if (labelElementId && defaultLabel) {
		$('#' + labelElementId).text(defaultLabel);
	}
	
	if (previewElementId) {
		var defaultSrc = $('#' + previewElementId).attr('default-src');
		
		if (defaultSrc) {
			$('#' + previewElementId).attr('src', defaultSrc);
		}
	}
}

function showModal(modalId) {
	if (modalId && modalId != '') {
		$('#' + modalId).modal({ show: true });
	}
}

function formatBytes(bytes, decimals) {
    if (bytes === 0) return '0 Bytes';

    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];

    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}
