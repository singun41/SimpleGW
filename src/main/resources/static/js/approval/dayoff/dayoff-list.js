window.addEventListener('DOMContentLoaded', event => {
    setTitleAndIcon('휴가 신청서', 'fas fa-user-check');

    document.getElementById('search').setAttribute('onclick', 'searchApprovalList("dayoff")');
    searchApprovalList('dayoff');
});