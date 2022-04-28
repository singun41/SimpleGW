window.addEventListener('DOMContentLoaded', event => {
    setTitleAndIcon('자료실', 'fas fa-file-archive');
    
    document.getElementById('search').setAttribute('onclick', 'searchBoardList("archive")');
    searchBoardList('archive');
});