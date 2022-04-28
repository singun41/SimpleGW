window.addEventListener('DOMContentLoaded', event => {
    document.getElementById('dateFrom').value = moment().add(-1, 'months').format('YYYY-MM-DD');
    setDatetimePicker('dateFrom', 'date', false);

    let docsKind = 'notice';
    document.getElementById('dateFrom').setAttribute('onchange', 'searchList("' + docsKind + '")');
    setTitle('fas fa-clipboard-list', '공지사항');
    searchList(docsKind);
});
