window.addEventListener('DOMContentLoaded', event => {
    document.getElementById('documentKindTitle').innerText = '명함 신청서';
    document.getElementById('btnUpdate').setAttribute('onclick', 'updateApprovalDocs("namecard", 1)');
    if(document.getElementById('btnTempSave')) {
        document.getElementById('btnTempSave').setAttribute('onclick', 'updateApprovalDocs("namecard", 0)');
    }

    getDetails();
    getApprovalLine(docsId);
});
const docsId = document.getElementById('docsId').innerText;

async function getDetails() {
    let details = await fetchCustom('GET', 'default', '', 'approval/namecard/details/' + docsId, 'json');
    document.getElementById('team').value = details.team;
    document.getElementById('jobTitle').value = details.jobTitle;
    document.getElementById('name').value = details.name;
    document.getElementById('nameEng').value = details.nameEng;
    document.getElementById('mail').value = details.mailAddress;
    document.getElementById('tel').value = details.tel;
    document.getElementById('mobile').value = details.mobileNo;
}

async function updateApprovalDocs(docsKind, isRegistered) {
    let title = document.getElementById('title').value;
    let content = document.getElementById('content').value;

    if(isEmptyTitle(title)) {
        return;
    }

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
        id: docsId,
        title: title,
        content: content,
        approvers: approver,
        referrers: referrer,
        registered: isRegistered,
        
        namecardDtoList: arrNamecard
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
