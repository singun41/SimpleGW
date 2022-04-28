window.addEventListener('DOMContentLoaded', event => {
    setDataTable();
    document.getElementById('targetId').textContent = opener.document.getElementById('docsId').textContent;
});
const employeeList = document.getElementById('employeeList');
const referrer = document.getElementById('referrer');

function destroyDataTable() {
    $('#employeeListTable').DataTable().destroy();
}

function setDataTable() {
    $('#employeeListTable').DataTable({
        order: [1, 'asc'],
        ordering: true,
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

const teamSelector = document.getElementById('selectedTeam');
async function getTeamMembers() {
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

let arrReferrer = [];
function lineBinding(elem) {
    let nodeTr = elem.parentNode.parentNode;
    let type = 'referrer';
    let idx = nodeTr.childNodes[0].innerText;

    if(isDuplicatedMember(type, idx)) {
        alert('이미 등록되어 있습니다.');
        return;
    }

    let team = nodeTr.childNodes[1].innerText;
    let jobTitle = nodeTr.childNodes[2].innerText;
    let name = nodeTr.childNodes[3].innerText;
    let seq = document.getElementById(type).childNodes.length + 1;

    detailsBinding(type, idx, seq, team, jobTitle, name);
}

function isDuplicatedMember(type, idx) {
    let dup = false;

    if (type === 'referrer') {
        arrReferrer.forEach(elem => {
            if(elem === idx) {
                dup = true;
            }
        });
    }
    return dup;
}

function detailsBinding(type, idx, seq, team, jobTitle, name) {
    let tbody = document.getElementById(type);
    let tr = document.createElement('tr');
    tr.setAttribute('class', 'text-center');
    
    let tdIdx = document.createElement('td');
    tdIdx.setAttribute('class', 'd-none');
    tdIdx.append(idx);

    let tdSeq = document.createElement('td');
    tdSeq.append(seq);

    let tdTeam = document.createElement('td');
    tdTeam.append(team);

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

    if(type === 'referrer') {
        tr.append(tdIdx, tdTeam, tdName, tdBtn);
        arrReferrer.push(idx);
    }
    tbody.append(tr);
}

function removeMember(type, elem, idx) {
    let tbody = elem.parentNode.parentNode.parentNode;
    let tr = elem.parentNode.parentNode

    if(type === 'referrer') {
        tbody.removeChild(tr);

        arrReferrer = arrReferrer.filter((elem) => parseInt(elem) !== parseInt(idx));
    }
}

function addAll() {
    if(teamSelector.value === '')
        return;
    
    employeeList.childNodes.forEach(elem => {
        lineBinding(elem.childNodes[4].childNodes[0]);
    });
}

function clear() {
    while(referrer.hasChildNodes()) {
        referrer.removeChild(referrer.firstChild);
    }
    arrReferrer = [];
}

function reset() {
    clear();
}

async function apply() {
    if(arrReferrer.length === 0) {
        alert('공유할 멤버를 선택하세요.');
        return;
    }

    let param = {
        docsId: document.getElementById('targetId').textContent,
        referrers: arrReferrer
    };
    await fetchCustom('POST', 'default', param, 'docs/share', '');
    alert('적용하였습니다.');
    window.close();
}
