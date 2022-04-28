window.addEventListener('DOMContentLoaded', event => {
    setTitleAndIcon('명함 신청서', 'far fa-id-card');

    document.getElementById('search').setAttribute('onclick', 'searchApprovalList("namecard")');
    searchApprovalList('namecard');
});