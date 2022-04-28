window.addEventListener('DOMContentLoaded', event => {
    setTitleAndIcon('자유게시판', 'fas fa-comment-alt');
    
    document.getElementById('search').setAttribute('onclick', 'searchBoardList("freeboard")');
    searchBoardList('freeboard');
});