window.addEventListener('DOMContentLoaded', event => {
    document.getElementById('documentKindTitle').innerText = '[ 휴가 신청서 ]';

    // document.getElementById('btnTempSave').setAttribute('onclick', 'insertApprovalDocs("dayoff", 0)');
    // document.getElementById('btnInsert').setAttribute('onclick', 'insertApprovalDocs("dayoff", 1)');

    setDatetimePicker('dateStart0', 'date', true);
    setDatetimePicker('dateEnd0', 'date', true);
    // document.getElementById('duration0').value = 1;
    // document.getElementById('count0').value = 1;
});