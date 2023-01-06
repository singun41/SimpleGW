let arrFile;
async function uploadFiles(docsId) {
    arrFile = document.getElementById('attachments').files;
    if(arrFile.length === 0)
        return;
    
    let formData = new FormData;
    Array.from(arrFile).forEach(e => {
        formData.append('files', e);
    });

    return await fetchFormData(`attachments/${docsId}`, formData);
}

async function deleteFile(url, e) {
    if(!confirm('파일을 삭제하시겠습니까?'))
        return;
    
    let response = await fetchDelete(`attachments/${url}`);
    let result = await response.json();
    alert(result.msg);
    if(response.ok)
        e.parentNode.remove();
}
