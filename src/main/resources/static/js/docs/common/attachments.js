const listRow = document.getElementById('listRow');
const fileCnt = document.getElementById('fileCnt');

function selectFiles() {
    document.getElementById('inputAttachments').click();
}

let arrFile = [];
function listup(e) {
    Array.from(e.files).forEach(file => {
        let lbl = document.createElement('label');
        lbl.classList.add('col-form-label', 'col-form-label-sm', 'custom-fs-8');
        lbl.textContent = file.name;

        listRow.append(lbl);
        arrFile.push(file);
    });
    fileCnt.innerText = arrFile.length;
}

function listReset() {
    listRow.classList.remove('mt-3');
    while(listRow.hasChildNodes())
        listRow.removeChild(listRow.firstChild);
    arrFile = [];
    fileCnt.innerText = '';
}

async function uploadFiles(docsId) {
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
