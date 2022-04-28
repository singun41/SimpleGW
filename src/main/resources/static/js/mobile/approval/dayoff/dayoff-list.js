window.addEventListener('DOMContentLoaded', event => {
    document.getElementById('dateFrom').value = moment().add(-1, 'months').format('YYYY-MM-DD');
    setDatetimePicker('dateFrom', 'date', false);

    let docsKind = 'dayoff';
    document.getElementById('dateFrom').setAttribute('onchange', 'searchList("' + docsKind + '")');
    setTitle('fas fa-user-check', '휴가신청서');
    searchList(docsKind);
});
