function previewImage(inputElement, imageElementId) {
	if (!inputElement.files || !inputElement.files[0]) {
		return;
	}
		
	var reader = new FileReader();
	
	reader.onload = function(e) {
		$('#' + imageElementId).attr('src', e.target.result);
	}
	
	reader.readAsDataURL(inputElement.files[0]);
}

function validateAndPreviewImage(inputElement, imageElementId, supportFileType, maxFileSize, fileTypeModalId, fileSizeModalId) {
	if (!inputElement.files || !inputElement.files[0]) {
		return null;
	}
	
	var fileType = inputElement.files[0].type;
    var fileSize = inputElement.files[0].size;
    
    if (supportFileType && supportFileType.indexOf(fileType) < 0) {
    	inputElement.value = null;
    	
    	showModal(fileTypeModalId);
    	
    	return null;
    }
    
    if (maxFileSize && maxFileSize > 0 && fileSize > maxFileSize) {
    	inputElement.value = null;
    	
    	showModal(fileSizeModalId);
    	
    	return null;
    }
    
    previewImage(inputElement, imageElementId);
    
    return inputElement.files[0].name;
}

function showModal(modalId) {
	$('#' + modalId).modal({ show: true });
}

function formatBytes(bytes, decimals) {
    if (bytes === 0) return '0 Bytes';

    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];

    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}
