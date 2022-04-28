window.addEventListener('DOMContentLoaded', event => {
    setTitleAndIcon('회의록', 'fas fa-paste');

    document.getElementById('search').setAttribute('onclick', 'searchBoardList("meeting")');
    searchBoardList('meeting');
});