document.addEventListener('DOMContentLoaded', () => {
    getSavedLines();
});

let approverIds = [];
let referrerIds = [];
let approverNames = [];
let referrerNames = [];

function reset() {
    document.getElementById('lines').selectedIndex = 0;
    resetLines();
}

function resetLines() {
    approverIds = [];
    referrerIds = [];
    approverNames = [];
    referrerNames = [];

    let approvers = document.getElementById('lineSetApprover');
    while(approvers.hasChildNodes())
        approvers.removeChild(approvers.firstChild);
    
    let referrers = document.getElementById('lineSetReferrer');
    while(referrers.hasChildNodes())
        referrers.removeChild(referrers.firstChild);
}

async function getSavedLines() {
    let lines = document.getElementById('lines');

    let response = await fetchGet('approver-line');
    let result = await response.json();
    
    let defaultOpt = document.createElement('option');
    defaultOpt.setAttribute('value', '');
    defaultOpt.selected = true;
    defaultOpt.disabled = true;

    if(response.ok) {
        while(lines.hasChildNodes())
            lines.removeChild(lines.firstChild);

        let savedLines = Array.from(result.obj);
        
        if(savedLines.length === 0) {
            defaultOpt.text = '저장된 결재라인 없음.';
            lines.append(defaultOpt);
        
        } else {
            defaultOpt.text = '선택...';
            lines.append(defaultOpt);

            savedLines.forEach(e => {
                let opt = document.createElement('option');
                opt.value = e.id;
                opt.text = e.title;
                lines.append(opt);
            });
        }
        
    } else {
        defaultOpt.text = '결재라인 로드 실패.';
        lines.append(opt);
    }
}

async function getTeamMembers(e) {
    let response = await fetchGet(`team/${e.value}/without-me`);
    let result = await response.json();
    
    if(response.ok) {
        let list = document.getElementById('list');
        while(list.hasChildNodes())
            list.removeChild(list.firstChild);

        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');
            tr.classList.add('text-center');

            let id = document.createElement('td');
            id.innerText = e.id;
            id.classList.add('d-none');

            let name = document.createElement('td');
            name.innerText = `${e.jobTitle} ${e.name}`;

            let approver = document.createElement('td');
            let referrer = document.createElement('td');

            let btnApprover = document.createElement('button');
            let btnReferrer = document.createElement('button');
            btnApprover.classList.add('btn', 'btn-outline-secondary', 'btn-sm');
            btnReferrer.classList.add('btn', 'btn-outline-secondary', 'btn-sm');
            btnApprover.setAttribute('onclick', 'addApprover(this)');
            btnReferrer.setAttribute('onclick', 'addReferrer(this)');

            let iApprover = document.createElement('i');
            let iReferrer = document.createElement('i');
            iApprover.classList.add('fa-solid', 'fa-file-signature');
            iReferrer.classList.add('fa-solid', 'fa-file-circle-check');

            btnApprover.append(iApprover);
            btnReferrer.append(iReferrer);
            approver.append(btnApprover);
            referrer.append(btnReferrer);

            tr.append(id, name, approver, referrer);
            list.append(tr);
        });
        document.getElementById('theadLine').classList.remove('d-none');
    }
}

async function getLineDetails() {
    let response = await fetchGet(`approver-line/${lines.value}`);
    let result = await response.json();

    if(response.ok) {
        resetLines();
        let approvers = result.obj.approvers;
        let referrers = result.obj.referrers;

        approvers.forEach(e => {
            pushApprover(e.id, `${e.jobTitle} ${e.name}`);
        });

        referrers.forEach(e => {
            pushReferrer(e.id, `${e.jobTitle} ${e.name}`);
        });
    }
}

function addApprover(e) {
    let approverId = e.parentNode.parentNode.childNodes[0].innerText;
    let approverName = e.parentNode.parentNode.childNodes[1].innerText;
    pushApprover(approverId, approverName);
}

function pushApprover(approverId, approverName) {
    let isDuplicated = false;
    approverIds.forEach(e => {
        if(e === approverId)
            isDuplicated = true;
    });

    if(isDuplicated) {
        alert('이미 등록한 멤버입니다.');
        return;
    }

    approverIds.push(approverId);
    approverNames.push(approverName);

    let list = document.getElementById('lineSetApprover');
    let no = list.childNodes.length + 1;
    let tr = document.createElement('tr');
    let td = document.createElement('td');
    tr.classList.add('text-center');
    td.innerText = `${no}. ${approverName}`;
    tr.append(td);
    list.append(tr);
}

function addReferrer(e) {
    let referrerId = e.parentNode.parentNode.childNodes[0].innerText;
    let referrerName = e.parentNode.parentNode.childNodes[1].innerText;
    pushReferrer(referrerId, referrerName);
}

function pushReferrer(referrerId, referrerName) {
    let isDuplicated = false;
    referrerIds.forEach(e => {
        if(e === referrerId)
            isDuplicated = true;
    });

    if(isDuplicated) {
        alert('이미 등록한 멤버입니다.');
        return;
    }

    referrerIds.push(referrerId);
    referrerNames.push(referrerName);

    let list = document.getElementById('lineSetReferrer');
    let tr = document.createElement('tr');
    let td = document.createElement('td');
    tr.classList.add('text-center');
    td.innerText = referrerName;
    tr.append(td);
    list.append(tr);
}

function apply() {
    if(approverIds.length === 0)
        alert('결재선을 지정하세요.');
}
