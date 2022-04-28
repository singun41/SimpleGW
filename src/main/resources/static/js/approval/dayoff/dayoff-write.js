window.addEventListener('DOMContentLoaded', event => {
    document.getElementById('documentKindTitle').innerText = '휴가 신청서';

    document.getElementById('btnTempSave').setAttribute('onclick', 'insertApprovalDocs("dayoff", 0)');
    document.getElementById('btnInsert').setAttribute('onclick', 'insertApprovalDocs("dayoff", 1)');

    setDatetimePicker('dateStart0', 'date', true);
    setDatetimePicker('dateEnd0', 'date', true);
    document.getElementById('duration0').value = 1;
    document.getElementById('count0').value = 1;

    copyApprovalDocsCheck();
});

async function insertApprovalDocs(docsKind, isRegistered) {
    let title = document.getElementById('title').value;
    let content = document.getElementById('content').value;

    if(isEmptyTitle(title)) return;

    let arrCode = [];
    Array.from(document.getElementsByClassName('dayoff-code')).forEach(elem => { arrCode.push(elem.value); });

    let arrDateStart = [];
    Array.from(document.getElementsByClassName('input-date-start')).forEach(elem => { arrDateStart.push(elem.value); });

    let arrDateEnd = [];
    Array.from(document.getElementsByClassName('input-date-end')).forEach(elem => { arrDateEnd.push(elem.value); });

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
        title: title,
        content: content,
        approvers: approver,
        referrers: referrer,
        registered: isRegistered,
        
        dayoffDtoList: arrDayoff
    };

    const docsId = await saveSubListApprovalDocs(param, docsKind, isRegistered);
}
