window.addEventListener('DOMContentLoaded', event => {
    document.getElementById('documentKindTitle').innerText = '연장 근무 신청서';
    document.getElementById('btnUpdate').setAttribute('onclick', 'updateApprovalDocs("overtime", 1)');
    if(document.getElementById('btnTempSave')) {
        document.getElementById('btnTempSave').setAttribute('onclick', 'updateApprovalDocs("overtime", 0)');
    }

    setDatetimePicker('workDate0', 'date', true);
    setDatetimePicker('timeStart0', 'time', true);
    setDatetimePicker('timeEnd0', 'time', true);
    
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

    let arrMember = [];
    Array.from(document.getElementsByClassName('overtime-member')).forEach(elem => {
        arrMember.push(elem.value);
    });

    let arrCode = [];
    Array.from(document.getElementsByClassName('overtime-code')).forEach(elem => {
        arrCode.push(elem.value);
    });

    let arrWorkDate = [];
    Array.from(document.getElementsByClassName('input-work-date')).forEach(elem => {
        arrWorkDate.push(elem.value);
    });

    let arrTimeStart = [];
    Array.from(document.getElementsByClassName('input-time-start')).forEach(elem => {
        arrTimeStart.push(elem.value);
    });

    let arrTimeEnd = [];
    Array.from(document.getElementsByClassName('input-time-end')).forEach(elem => {
        arrTimeEnd.push(elem.value);
    });

    let arrRemarks = [];
    Array.from(document.getElementsByClassName('input-remarks')).forEach(elem => {
        arrRemarks.push(elem.value);
    });

    let arrOvertime = [];
    for(let i=0; i<arrMember.length; i++) {
        let overtime = {
            seq: i + 1,
            memberId: arrMember[i],
            code: arrCode[i],
            workDate: arrWorkDate[i],
            timeStart: arrTimeStart[i],
            timeEnd: arrTimeEnd[i],
            remarks: arrRemarks[i]
        };
        arrOvertime.push(overtime);
    }

    let param = {
        id: docsId,
        title: title,
        content: content,
        approvers: approver,
        referrers: referrer,
        registered: isRegistered,
        
        overtimeDtoList: arrOvertime
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