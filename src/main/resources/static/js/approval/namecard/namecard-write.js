window.addEventListener('DOMContentLoaded', event => {
    document.getElementById('documentKindTitle').innerText = '명함 신청서';

    document.getElementById('btnTempSave').setAttribute('onclick', 'insertApprovalDocs("namecard", 0)');
    document.getElementById('btnInsert').setAttribute('onclick', 'insertApprovalDocs("namecard", 1)');
});

async function insertApprovalDocs(docsKind, isRegistered) {
    let title = document.getElementById('title').value;
    let content = document.getElementById('content').value;

    if(isEmptyTitle(title)) return;

    let namecard = {
        team: document.getElementById('team').value,
        jobTitle: document.getElementById('jobTitle').value,
        name: document.getElementById('name').value,
        nameEng: document.getElementById('nameEng').value,
        mailAddress: document.getElementById('mail').value,
        tel: document.getElementById('tel').value,
        mobileNo: document.getElementById('mobile').value
    };
    let arrNamecard = [];
    arrNamecard.push(namecard);

    let param = {
        title: title,
        content: content,
        approvers: approver,
        referrers: referrer,
        registered: isRegistered,
        
        namecardDtoList: arrNamecard
    };

    const docsId = await saveSubListApprovalDocs(param, docsKind, isRegistered);
}
