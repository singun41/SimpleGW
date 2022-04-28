window.addEventListener('DOMContentLoaded', event => {
    getComments();
});

async function getComments() {
    let docsId = document.getElementById('docsId').innerText;
    let result = await fetchCustom('GET', 'default', '', 'comments/' + docsId, 'json');
    
    const commentList = document.getElementById('commentList');
    while(commentList.hasChildNodes())
        commentList.removeChild(commentList.firstChild);

    result.forEach(e => {
        let trWriter = document.createElement('tr');
        let tdWriter = document.createElement('td');
        tdWriter.textContent = e.writerTeam + ' ' + e.writerJobTitle + ' ' + e.writerName;
        trWriter.append(tdWriter);

        let tdTime = document.createElement('td');
        let cdate = new Date(e.createdDatetime);
        tdTime.append(
            cdate.toLocaleDateString().substring(2, 11) + '. ',
            cdate.getHours().toString().padStart(2, '0') + ':' + cdate.getMinutes().toString().padStart(2, '0') + ':' + cdate.getSeconds().toString().padStart(2, '0')
        );
        trWriter.append(tdTime);

        let trComment = document.createElement('tr');
        let tdComment = document.createElement('td');
        tdComment.setAttribute('colspan', 2);
        tdComment.textContent = e.comment;
        trComment.append(tdComment);

        let trLine = document.createElement('tr');
        let tdLine = document.createElement('td');
        tdLine.setAttribute('colspan', 2);
        let line = document.createElement('hr');
        tdLine.append(line);
        trLine.append(tdLine);

        commentList.append(trWriter, trComment, trLine);
    });
}

async function insertComment() {
    let commentText = document.getElementById('comment').value;
    if(commentText === null || commentText === '') { return; }

    let comment = {
        docsId: document.getElementById('docsId').innerText,
        comment: commentText
    }
    await fetchCustom('POST', 'default', comment, 'comments', 'msg');
    document.getElementById('comment').value = '';
    getComments();
}