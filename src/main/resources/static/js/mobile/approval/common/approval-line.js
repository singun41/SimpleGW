window.addEventListener('DOMContentLoaded', event => {
    setDataTable();
    getLineMasterList();
});
const employeeList = document.getElementById('employeeList');
const approver = document.getElementById('approver');
const referrer = document.getElementById('referrer');
const savedLineSelect = document.getElementById('savedApprovalLine');

function destroyDataTable() {
    $('#employeeListTable').DataTable().destroy();
}

function setDataTable() {
    $('#employeeListTable').DataTable({
        order: [1, 'asc'],
        ordering: true,
        searching: false,
        columnDefs: [
            // 0번은 hide 된 idx
            { targets: 1, width: '40%' },
            { targets: 2, width: '25%' },
            { targets: 3, width: '20%' },
            { targets: 4, width: '15%' }
        ],
        scrollX: true,
        scrollXInner: '100%',
        fixedHeader: true,
        scrollY: 400
    });
}

async function getTeamMembers() {
    const teamSelector = document.getElementById('selectedTeam');
    let param = {
        team: teamSelector.value
    };

    let result = await fetchCustom('GET', 'urlEncoded', param, 'team-member', 'json');
    return result;
}

async function setEmployeeList() {
    destroyDataTable();

    while(employeeList.hasChildNodes()) {
        employeeList.removeChild(employeeList.firstChild);
    }

    let members = await getTeamMembers();
    if(members.length <= 0) {
        return;
    }

    members.forEach((member) => {
        let tr = document.createElement('tr');
        tr.setAttribute('class', 'text-center');

        let tdIdx = document.createElement('td');
        tdIdx.setAttribute('class', 'd-none');

        let tdTeam = document.createElement('td');
        tdTeam.setAttribute('class', 'd-none');

        let tdJobTitle = document.createElement('td');
        let tdName = document.createElement('td');
        let tdChk = document.createElement('td');
        
        let btnChk = document.createElement('button');
        btnChk.setAttribute('class', 'btn btn-outline-success btn-sm');
        btnChk.setAttribute('onclick', 'lineBinding(this)');

        let btnIcon = document.createElement('i');
        btnIcon.setAttribute('class', 'fas fa-check');
        btnChk.append(btnIcon);
        
        tdIdx.append(member.id);
        tdTeam.append(member.team);
        tdJobTitle.append(member.jobTitle);
        tdName.append(member.name);
        tdChk.append(btnChk);

        tr.append(tdIdx, tdTeam, tdJobTitle, tdName, tdChk);
        employeeList.append(tr);
    });

    setDataTable();
}

let templateLineMasterId = 0;
let arrApprover = [];
let arrApproverName = [];
let arrReferrer = [];
let arrReferrerName = [];
function lineBinding(elem) {
    let nodeTr = elem.parentNode.parentNode;
    let type = document.getElementById('approvalType').value;
    let idx = nodeTr.childNodes[0].innerText;

    if(isDuplicatedMember(type, idx)) {
        alert('이미 등록되어 있습니다.');
        return;
    }

    let jobTitle = nodeTr.childNodes[2].innerText;
    let name = nodeTr.childNodes[3].innerText;
    let seq = document.getElementById(type).childNodes.length + 1;

    detailsBinding(type, idx, seq, jobTitle, name);
}

function isDuplicatedMember(type, idx) {
    let dup = false;
    if(type === 'approver') {
        arrApprover.forEach(elem => {
            if(elem === idx) {
                dup = true;
            }
        });
    }
    if (type === 'referrer') {
        arrReferrer.forEach(elem => {
            if(elem === idx) {
                dup = true;
            }
        });
    }
    return dup;
}

function detailsBinding(type, idx, seq, jobTitle, name) {
    let tbody = document.getElementById(type);
    let tr = document.createElement('tr');
    tr.setAttribute('class', 'text-center');
    
    let tdIdx = document.createElement('td');
    tdIdx.setAttribute('class', 'd-none');
    tdIdx.append(idx);

    let tdSeq = document.createElement('td');
    tdSeq.append(seq);

    let tdName = document.createElement('td');
    tdName.append(jobTitle + ' ' + name);

    let delIcon = document.createElement('i');
    delIcon.setAttribute('class', 'fas fa-times');
    let delBtn = document.createElement('button');
    delBtn.setAttribute('onclick', 'removeMember("' + type + '", this, "' + idx + '")');
    delBtn.setAttribute('class', 'btn btn-outline-secondary btn-sm');
    delBtn.append(delIcon);
    let tdBtn = document.createElement('td');
    tdBtn.append(delBtn);

    if(type === 'approver') {
        tr.append(tdIdx, tdSeq, tdName, tdBtn);
        arrApprover.push(idx);
        arrApproverName.push(tdName.innerText);
    } else if(type === 'referrer') {
        tr.append(tdIdx, tdName, tdBtn);
        arrReferrer.push(idx);
        arrReferrerName.push(tdName.innerText);
    }
    tbody.append(tr);
}

function removeMember(type, elem, idx) {
    let tbody = elem.parentNode.parentNode.parentNode;
    let tr = elem.parentNode.parentNode

    if(type === 'approver') {
        tbody.removeChild(tr);
        approverAlign();

        let targetIdx = arrApprover.indexOf(parseInt(idx), 0);
        arrApprover = arrApprover.filter((elem) => parseInt(elem) !== parseInt(idx));
        arrApproverName.splice(targetIdx, 1);
    }
    
    if(type === 'referrer') {
        tbody.removeChild(tr);

        let targetIdx = arrReferrer.indexOf(parseInt(idx), 0);
        arrReferrer = arrReferrer.filter((elem) => parseInt(elem) !== parseInt(idx));
        arrReferrerName.splice(targetIdx, 1);
    }
}

function approverAlign() {
    let count = approver.children.length;
    for(let i=0; i<count; i++) {
        approver.childNodes[i].childNodes[1].innerText = i+1;
    }
}

function clearLine() {
    while(approver.hasChildNodes()) {
        approver.removeChild(approver.firstChild);
    }
    arrApprover = [];
    arrApproverName = [];

    while(referrer.hasChildNodes()) {
        referrer.removeChild(referrer.firstChild);
    }
    arrReferrer = [];
    arrReferrerName = [];
}

function resetLine() {
    clearLine();
    savedLineSelect.selectedIndex = 0;
}

async function getLineMasterList() {
    let result = await fetchCustom('GET', 'default', '', 'approval/line-template/master-list', 'json');

    while(savedLineSelect.hasChildNodes()) {
        savedLineSelect.removeChild(savedLineSelect.firstChild);
    }
    
    let optionDefault = document.createElement('option');
    optionDefault.value = '';
    optionDefault.selected = true;
    optionDefault.disabled = true;
    optionDefault.text = '저장한 결재라인';
    savedLineSelect.append(optionDefault);

    result.forEach(elem => {
        let option = document.createElement('option');
        option.value = elem.id;
        option.text = elem.title;
        savedLineSelect.append(option);
    });
}

async function getLineDetailsList() {
    let param = {
        masterId: savedLineSelect.value
    };
    return await fetchCustom('GET', 'urlEncoded', param, 'approval/line-template/details-list', 'json');
}

async function saveLine() {
    if(arrApprover.length === 0) {
        alert('저장할 결재라인을 설정하세요.');
        return;
    }
    let save = false;
    let title;
    if(savedLineSelect.selectedIndex === 0) {
        title = prompt('결재라인 제목을 입력하세요.');
        if(title !== null && title !== '') {   // 제목을 제대로 입력한 경우만 실행하도록.
            save = true;
        }

    } else {
        title = savedLineSelect.options[savedLineSelect.selectedIndex].innerText;
        save = true;
    }

    if(save){
        let params = {
            masterId: savedLineSelect.value,
            title: title,
            arrApprover: arrApprover,
            arrReferrer: arrReferrer
        };

        await fetchCustom('POST', 'default', params, 'approval/line-template', 'msg');
        await getLineMasterList();
    }
}

async function deleteLine() {
    if(savedLineSelect.selectedIndex === 0) { alert('삭제할 결재라인을 선택하세요.'); return; }
    let masterId = savedLineSelect.value;
    let result = await fetchCustom('DELETE', 'urlEncoded', '', 'approval/line-template/' + masterId, 'msg');
    if(result.status === 'SUCCESS') {
        await getLineMasterList();
        clearLine();
    }
}

async function selectSavedLine() {
    let result = await getLineDetailsList();
    clearLine();

    result.approverList.forEach(elem => {
        let idx = elem.memberId;
        let seq = elem.seq;
        let jobTitle = elem.jobTitle;
        let name = elem.name;
        detailsBinding('approver', idx, seq, jobTitle, name);
    });

    result.referrerList.forEach(elem => {
        let idx = elem.memberId;
        let seq = 0;
        let jobTitle = elem.jobTitle;
        let name = elem.name;
        detailsBinding('referrer', idx, seq, jobTitle, name);
    });
}

function applyLine() {
    if(arrApprover.length === 0) {
        alert('적용할 결재라인을 설정하세요.');
        return;
    }

    let lineMembers = {
        approver: arrApprover,
        approverName: arrApproverName,
        referrer: arrReferrer,
        referrerName: arrReferrerName
    };

    window.opener.applyToLineTables(lineMembers);   // approval-line-table.js에 있는 function으로 전달
    self.close();
}
