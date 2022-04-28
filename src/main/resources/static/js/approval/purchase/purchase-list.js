window.addEventListener('DOMContentLoaded', event => {
    setTitleAndIcon('물품 구매 신청서', 'fas fa-boxes');

    document.getElementById('search').setAttribute('onclick', 'searchApprovalList("purchase")');
    searchApprovalList('purchase');
});