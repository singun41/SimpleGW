window.addEventListener('DOMContentLoaded', event => {
    document.getElementById('documentKindTitle').innerText = '연장 근무 신청서';

    document.getElementById('btnTempSave').setAttribute('onclick', 'insertApprovalDocs("overtime", 0)');
    document.getElementById('btnInsert').setAttribute('onclick', 'insertApprovalDocs("overtime", 1)');

    setDatetimePicker('workDate0', 'date', true);
    setDatetimePicker('timeStart0', 'time', true);
    setDatetimePicker('timeEnd0', 'time', true);

    copyApprovalDocsCheck();
});

async function insertApprovalDocs(docsKind, isRegistered) {
    let title = document.getElementById('title').value;
    let content = document.getElementById('content').value;

    if(isEmptyTitle(title)) return;

    let arrMember = [];
    Array.from(document.getElementsByClassName('overtime-member')).forEach(elem => { arrMember.push(elem.value); });

    let arrCode = [];
    Array.from(document.getElementsByClassName('overtime-code')).forEach(elem => { arrCode.push(elem.value); });

    let arrWorkDate = [];
    Array.from(document.getElementsByClassName('input-work-date')).forEach(elem => { arrWorkDate.push(elem.value); });

    let arrTimeStart = [];
    Array.from(document.getElementsByClassName('input-time-start')).forEach(elem => { arrTimeStart.push(elem.value); });

    let arrTimeEnd = [];
    Array.from(document.getElementsByClassName('input-time-end')).forEach(elem => { arrTimeEnd.push(elem.value); });

    let arrRemarks = [];
    Array.from(document.getElementsByClassName('input-remarks')).forEach(elem => { arrRemarks.push(elem.value); });

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
        title: title,
        content: content,
        approvers: approver,
        referrers: referrer,
        registered: isRegistered,
        
        overtimeDtoList: arrOvertime
    };

    const docsId = await saveSubListApprovalDocs(param, docsKind, isRegistered);
}
