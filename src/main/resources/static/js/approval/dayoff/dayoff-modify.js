window.addEventListener('DOMContentLoaded', event => {
    document.getElementById('documentKindTitle').innerText = '휴가 신청서';
    document.getElementById('btnUpdate').setAttribute('onclick', 'updateApprovalDocs("dayoff", 1)');
    if(document.getElementById('btnTempSave')) {
        document.getElementById('btnTempSave').setAttribute('onclick', 'updateApprovalDocs("dayoff", 0)');
    }

    setDatetimePicker('dateStart0', 'date', true);
    setDatetimePicker('dateEnd0', 'date', true);

    getDetails(docsId);
    getApprovalLine(docsId);
});
const docsId = document.getElementById('docsId').innerText;

async function updateApprovalDocs(docsKind, isRegistered) {
    let title = document.getElementById('title').value;
    let content = document.getElementById('content').value;

    if(isEmptyTitle(title)) {
        return;
    }

    let arrCode = [];
    Array.from(document.getElementsByClassName('dayoff-code')).forEach(elem => {
        arrCode.push(elem.value);
    });

    let arrDateStart = [];
    Array.from(document.getElementsByClassName('input-date-start')).forEach(elem => {
        arrDateStart.push(elem.value);
    });

    let arrDateEnd = [];
    Array.from(document.getElementsByClassName('input-date-end')).forEach(elem => {
        arrDateEnd.push(elem.value);
    });

    let arrDayoff = [];
    for(let i=0; i<arrCode.length; i++) {
        let dayoff = {
            seq: i + 1,
            code: arrCode[i],
            dateStart: arrDateStart[i],
            dateEnd: arrDateEnd[i]
        };
        arrDayoff.push(dayoff);
    }

    let param = {
        id: docsId,
        title: title,
        content: content,
        dayoffDtoList: arrDayoff,
        approvers: approver,
        referrers: referrer,
        registered: isRegistered
    };

    let result = await saveSubListApprovalDocs(param, docsKind);

    if(result.message === 'ok') {
        let uploadFilesResult = await uploadFiles(docsId);
        if(uploadFilesResult === 'ok') {
            alert('등록하였습니다.');
            location.href = '/approval/' + docsKind + '/' + docsId;
        } else {
            alert(uploadFilesResult);
        }
    } else {
        alert(result);
    }
}