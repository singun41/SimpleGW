window.addEventListener('DOMContentLoaded', event => {
    setTitleAndIcon('연장 근무 신청서', 'fas fa-user-clock');

    document.getElementById('search').setAttribute('onclick', 'searchApprovalList("overtime")');
    searchApprovalList('overtime');
});