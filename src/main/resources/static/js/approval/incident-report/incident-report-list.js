window.addEventListener('DOMContentLoaded', event => {
    setTitleAndIcon('사고 보고서', 'far fa-copy');

    document.getElementById('search').setAttribute('onclick', 'searchApprovalList("incident-report")');
    searchApprovalList('incident-report');
});