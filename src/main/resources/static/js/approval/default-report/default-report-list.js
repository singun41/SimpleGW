window.addEventListener('DOMContentLoaded', event => {
    setTitleAndIcon('기안서', 'far fa-copy');

    document.getElementById('search').setAttribute('onclick', 'searchApprovalList("default-report")');
    searchApprovalList('default-report');
});