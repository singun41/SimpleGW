const attachmentsInput = document.getElementById('attachmentsInput');
const attachmentsTable = document.getElementById('attachmentsList');

function appendSelectedFiles() {
    const filesCount = attachmentsInput.files.length;

    while(attachmentsTable.hasChildNodes()) {
        attachmentsTable.removeChild(attachmentsTable.firstChild);
    }

    for(let i=0; i<filesCount; i++) {
        let no = i + 1;
        let tdNo = document.createElement('td');
        tdNo.setAttribute('class', 'align-middle text-center');
        tdNo.textContent = no;

        let name = attachmentsInput.files[i].name;
        let tdName = document.createElement('td');
        tdName.setAttribute('class', 'pl-4 pr-2 align-middle text-center');
        tdName.textContent = name;

        let size = (attachmentsInput.files[i].size / 1024 / 1024).toFixed(2);
        let tdSize = document.createElement('td');
        if(size > 50.00) {
            tdSize.setAttribute('class', 'align-middle text-center text-danger font-weight-bold');
        } else {
            tdSize.setAttribute('class', 'align-middle text-center');
        }

        tdSize.textContent = size;
        
        let icon = document.createElement('i');
        icon.setAttribute('class', 'fas fa-trash-alt');

        let btn = document.createElement('button');
        btn.setAttribute('type', 'button');
        btn.setAttribute('class', 'btn btn-outline-secondary btn-sm');
        btn.setAttribute('onclick', 'deleteFile(this)');
        btn.append(icon);

        let tdDelBtn = document.createElement('td');
        tdDelBtn.setAttribute('class', 'text-right');
        tdDelBtn.append(btn);

        let row = document.createElement('tr');
        row.append(tdNo, tdName, tdSize, tdDelBtn);
        
        attachmentsTable.append(row);
    }
}

function selectedFilesRestore() {
    appendSelectedFiles();
}

function deleteFile(elem) {
    attachmentsTable.removeChild(elem.parentNode.parentNode);

    for(let i=0; i<attachmentsTable.children.length; i++) {
        attachmentsTable.childNodes.item(i).childNodes.item(0).innerText = i + 1;
    }
}

async function uploadFiles(docsId) {
    if(attachmentsInput.files.length === 0) {
        return 'ok';
    }

    let uploadTargetFileCount = attachmentsTable.children.length;
    if(uploadTargetFileCount === 0) {
        return 'ok';
    }

    let formData = new FormData;
    // file input??? ?????? Filelist ?????? ????????? ???????????? tr ????????? ????????? ???????????? ?????? ???????????? ???????????? ?????? ??? ?????? ????????? 2??? for loop??? ?????? ??????.
    for(let i=0; i<uploadTargetFileCount; i++) {
        for(let j=0; j<attachmentsInput.files.length; j++) {
            if(attachmentsTable.childNodes.item(i).childNodes.item(1).innerText === attachmentsInput.files[j].name) {
                // ???????????? ?????? ???????????? ???????????? ?????? FormData??? ?????????.
                formData.append('files', attachmentsInput.files[j]);
            }
        }
    }
    // formData.append('documentId', document.getElementById('documentId').innerText);
    formData.append('docsId', docsId);

    return await fetchCustomFormData('attachments/files', formData);
}

async function deleteOldAttachments(fileSeq, conversionName, originalName, elem) {
    if(confirm('????????? ???????????? ????????? ?????????????????????????' + '\n' + '????????? ??????: ' + originalName)) {
        let docsId = document.getElementById('docsId').innerText;
        await fetchCustom('DELETE', 'default', '', 'attachments/' + docsId + '/' + fileSeq + '/' + conversionName, 'msg');

        const oldAttachmentsList = document.getElementById('oldAttachmentsList');
        oldAttachmentsList.removeChild(elem.parentNode.parentNode);
    }
}