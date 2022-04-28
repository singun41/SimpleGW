window.addEventListener('DOMContentLoaded', event => {
    setTitleAndIcon('공지사항', 'fas fa-clipboard-list');

    document.getElementById('search').setAttribute('onclick', 'searchBoardList("notice")');
    searchBoardList('notice');
});