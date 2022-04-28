window.addEventListener('DOMContentLoaded', event => {
    setDataTable();
    getUserList();
});

const idx = document.getElementById('idx');
const userId = document.getElementById('userId');
const userPw = document.getElementById('userPw');
const role = document.getElementById('role');
const enabled = document.getElementById('enabled');
const team = document.getElementById('team');
const jobTitle = document.getElementById('jobTitle');
const userName = document.getElementById('name');
const retired = document.getElementById('retired');
const selectRetired = document.getElementById('isRetired');

function setDataTable() {
    $('#tableUserList').DataTable({
        order: [1, 'asc'],
        ordering: true,
        // columnDefs: [
        //     { targets: 0, width: '7%' },
        //     { targets: 1, width: '9%' },
        //     { targets: 2, width: '20%' },
        //     { targets: 3, width: '15%' },
        //     { targets: 4, width: '13%' },
        //     { targets: 5, width: '12%' },
        //     { targets: 6, width: '12%' }
        // ],
        scrollX: true,
        scrollXInner: '100%',
        fixedHeader: true,
        scrollY: 480,
        pageLength: 100
    });
}

function destoryDataTable() {
    $('#tableUserList').DataTable().destroy();
}

async function getUserList() {
    destoryDataTable();
    
    retired.value = selectRetired.value;
    setInputReadOnly(true);

    let param = {
        isRetired: selectRetired.value
    };
    
    let result = await fetchCustom('GET', 'urlEncoded', param, 'admin/member-list', 'json');

    const userListTbody = document.getElementById('userList');
    while(userListTbody.hasChildNodes()) {
        userListTbody.removeChild(userListTbody.firstChild);
    }

    if(result.length === 0) { setDataTable(); return; }

    result.forEach(elem => {
        let tr = document.createElement('tr');
        tr.setAttribute('class', 'text-center');
        tr.setAttribute('onclick', 'selectUser(this)');

        let tdIdx = document.createElement('td');
        let tdUserId = document.createElement('td');
        let tdTeam = document.createElement('td');
        let tdJobTitle = document.createElement('td');
        let tdName = document.createElement('td');
        let tdRole = document.createElement('td');
        let tdEnabled = document.createElement('td');
        let tdEnabledText = document.createElement('td');
        let tdRadio = document.createElement('td');

        tdIdx.append(elem.id);
        tdUserId.append(elem.userId);
        tdTeam.append(elem.team);
        tdJobTitle.append(elem.jobTitle);
        tdName.append(elem.name);
        
        if(elem.role === 'ADMIN') {
            tdRole.setAttribute('class', 'text-danger');
        }
        tdRole.append(elem.role);

        let isEnabled = '';
        let iconEnabled = document.createElement('i');
        if(elem.enabled) {
            iconEnabled.setAttribute('class', 'fas fa-check text-success mr-2');
            isEnabled = 'True';
            tdEnabledText.innerText = '1';
        } else {
            iconEnabled.setAttribute('class', 'fas fa-times text-danger mr-2');
            isEnabled = 'False';
            tdEnabledText.innerText = '0';
        }
        tdEnabled.append(iconEnabled, isEnabled);
        tdEnabledText.setAttribute('class', 'd-none');

        let radioInput = document.createElement('input');
        radioInput.setAttribute('type', 'radio');
        radioInput.setAttribute('name', 'selectRadio');
        tdRadio.append(radioInput);

        tr.append(tdIdx, tdUserId, tdTeam, tdJobTitle, tdName, tdRole, tdEnabled, tdEnabledText, tdRadio);
        userListTbody.append(tr);
    });

    setDataTable();
}

function selectUser(elem) {
    let child = elem.childNodes;
    let radioTd = child[child.length - 1];
    radioTd.firstChild.checked = true;
    setDataToForm(child);
}

function setDataToForm(elems) {
    idx.value = elems[0].innerText;
    userId.value = elems[1].innerText;
    userPw.value = '';
    role.value = elems[5].innerText;
    enabled.value = elems[7].innerText;
    team.value = elems[2].innerText;
    
    let jotTitleMatchIndex;
    jobTitle.childNodes.forEach(child => {
        if(child.nodeName === 'OPTION') {   // select box의 childNodes를 콘솔로 출력해보면 text 노드도 같이 나와서 필터링해야 한다.
            if(child.label === elems[3].innerText) {
                jotTitleMatchIndex = child.index;
            }
        }
    });
    jobTitle.value = jobTitle.options[jotTitleMatchIndex].value;

    userName.value = elems[4].innerText;
    retired.value = selectRetired.value;

    setInputReadOnly(true);
}

function clearForm() {
    idx.value = '';
    userId.value = '';
    userPw.value = '';
    role.value = role.options[0].value;
    enabled.value = '1';
    team.value = '';
    jobTitle.value = jobTitle.options[0].value;
    userName.value = '';
    retired.value = selectRetired.value;

    setInputReadOnly(false);
}

function setInputReadOnly(isReadonly) {
    userId.readOnly = isReadonly;
    // userName.readOnly = isReadonly;
}

async function saveMember() {
    let param = {
        id: idx.value,
        userId: userId.value,
        userPw: userPw.value,
        role: role.value,
        enabled: (enabled.value === '1' ? true : false),
        team: team.value,
        jobTitle: jobTitle.value,
        name: userName.value,
        retired: (retired.value === '1' ? true : false)
    };

    let result = await fetchCustom('POST', 'default', param, 'admin/member', 'msg');
    if(result.status === 'SUCCESS') {
        clearForm();
        getUserList();
    }
}