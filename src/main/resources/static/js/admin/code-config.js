window.addEventListener('DOMContentLoaded', event => {
    setDataTable();
});
const type = document.getElementById('codeType');

const id = document.getElementById('id');
const code = document.getElementById('code');
const value = document.getElementById('value');
const seq = document.getElementById('seq');
const rmks = document.getElementById('remarks');
const enabled = document.getElementById('enabled');

function setDataTable() {
    $('#tableCodeList').DataTable({
        order: [2, 'asc'],
        ordering: true,
        columnDefs: [
            { targets: 2, width: '7%' },
            { targets: 3, width: '12%' },
            { targets: 4, width: '23%' },
            { targets: 5, width: '35%' },
            { targets: 6, width: '13%' },
            { targets: 7, width: '10%' }
        ],
        scrollX: true,
        scrollXInner: '100%',
        fixedHeader: true,
        scrollY: 480,
        pageLength: 100
    });
}

function destoryDataTable() {
    $('#tableCodeList').DataTable().destroy();
}

async function getCodeList() {
    clearForm();
    setCodeReadonly();
    if(type.value === '') return;
    
    destoryDataTable();

    const codeList = document.getElementById('codeList');
    while(codeList.hasChildNodes())
        codeList.removeChild(codeList.firstChild);

    let param = {
        type: type.value
    };

    let result = await fetchCustom('GET', 'urlEncoded', param, 'admin/code-list', 'json');
    result.forEach(e => {
        let tr = document.createElement('tr');
        tr.setAttribute('class', 'text-center');
        tr.setAttribute('onclick', 'selectCode(this)');

        let tdId = document.createElement('td');
        tdId.textContent = e.id;
        tdId.setAttribute('class', 'd-none');

        let tdSeq = document.createElement('td');
        tdSeq.textContent = e.seq;

        let tdCode = document.createElement('td');
        tdCode.textContent = e.code;

        let tdValue = document.createElement('td');
        tdValue.textContent = e.value;

        let tdRmks = document.createElement('td');
        tdRmks.textContent = e.remarks;
        tdRmks.setAttribute('class', 'text-left');

        let isEnabled = '';
        let tdEnabled = document.createElement('td');
        let icon = document.createElement('i');
        let tdEnabledText = document.createElement('td');
        if(e.enabled) {
            icon.setAttribute('class', 'fas fa-check text-success mr-2');
            tdEnabledText.textContent = '1';
            isEnabled = 'true';
        } else {
            icon.setAttribute('class', 'fas fa-times text-danger mr-2');
            tdEnabledText.textContent = '0';
            isEnabled = 'false';
        }
        tdEnabled.append(icon, isEnabled);
        tdEnabledText.setAttribute('class', 'd-none');

        let tdRadio = document.createElement('td');
        let radioInput = document.createElement('input');
        radioInput.setAttribute('type', 'radio');
        radioInput.setAttribute('name', 'selectRadio');
        tdRadio.append(radioInput);

        tr.append(tdId, tdEnabledText, tdSeq, tdCode, tdValue, tdRmks, tdEnabled, tdRadio);
        codeList.append(tr);
    });

    setDataTable();
}

function selectCode(e) {
    let child = e.childNodes;
    let radioTd = child[child.length - 1];
    radioTd.firstChild.checked = true;
    setDataToForm(child);
}

function setDataToForm(elems) {
    id.value = elems[0].innerText;
    code.value = elems[3].innerText;
    value.value = elems[4].innerText;
    seq.value = elems[2].innerText;
    rmks.value = elems[5].innerText;
    enabled.value = elems[1].innerText;
    setCodeReadonly();
}

function clearForm() {
    id.value = '';
    code.value = '';
    code.readOnly = false;
    value.value = '';
    seq.value = '';
    rmks.value = '';
    enabled.selectedIndex = 0;
}
function setCodeReadonly() {
    code.readOnly = true;
}

async function saveCode() {
    if(id.value) {
        if(confirm('수정 하시겠습니까?') === false) { return; }
    } else {
        if(confirm('저장 하시겠습니까?') === false) { return; }
    }

    let param = {
        id: id.value,
        type: type.value,
        code: code.value,
        value: value.value,
        enabled: (enabled.value === '1' ? true : false),
        remarks: rmks.value,
        seq: seq.value
    };

    let result = await fetchCustom('POST', 'default', param, 'admin/code', 'msg');
    if(result.status === 'SUCCESS') {
        clearForm();
        getCodeList();
        setCodeReadonly();
    }
}
