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
    // file input에 있는 Filelist 객체 개수와 테이블의 tr 개수가 다르고 파일명이 같은 인덱스에 위치하지 않을 수 있기 때문에 2중 for loop를 써야 한다.
    for(let i=0; i<uploadTargetFileCount; i++) {
        for(let j=0; j<attachmentsInput.files.length; j++) {
            if(attachmentsTable.childNodes.item(i).childNodes.item(1).innerText === attachmentsInput.files[j].name) {
                // 테이블에 있는 파일명과 일치하는 것만 FormData에 담는다.
                formData.append('files', attachmentsInput.files[j]);
            }
        }
    }
    // formData.append('documentId', document.getElementById('documentId').innerText);
    formData.append('docsId', docsId);

    return await fetchCustomFormData('attachments/files', formData);
}

async function deleteOldAttachments(fileSeq, conversionName, originalName, elem) {
    if(confirm('기존에 업로드한 파일을 삭제하시겠습니까?' + '\n' + '선택한 파일: ' + originalName)) {
        let docsId = document.getElementById('docsId').innerText;
        await fetchCustom('DELETE', 'default', '', 'attachments/' + docsId + '/' + fileSeq + '/' + conversionName, 'msg');

        const oldAttachmentsList = document.getElementById('oldAttachmentsList');
        oldAttachmentsList.removeChild(elem.parentNode.parentNode);
    }
}