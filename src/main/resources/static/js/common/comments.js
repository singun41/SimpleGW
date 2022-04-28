window.addEventListener('DOMContentLoaded', event => {
    getComments();
});

async function getComments() {
    let docsId = document.getElementById('docsId').innerText;
    let result = await fetchCustom('GET', 'default', '', 'comments/' + docsId, 'json');
    
    const commentList = document.getElementById('commentList');
    while(commentList.hasChildNodes())
        commentList.removeChild(commentList.firstChild);

    result.forEach(data => {
        let labelClass = 'col-form-label col-form-label-sm';

        let tr = document.createElement('tr');

        let tdId = document.createElement('td');
        tdId.setAttribute('class', 'd-none');
        tdId.append(data.id);
        
        let tdWriter = document.createElement('td');
        tdWriter.setAttribute('style', 'width: 15%');
        let labelWriter = document.createElement('label');
        labelWriter.setAttribute('class', labelClass);
        labelWriter.append(data.writerTeam, document.createElement('br'), (data.writerJobTitle + ' ' + data.writerName));
        tdWriter.append(labelWriter);

        let tdComment = document.createElement('td');
        let labelComment = document.createElement('label');
        labelComment.setAttribute('class', labelClass);
        labelComment.append(data.comment);
        tdComment.append(labelComment);

        let cdate = new Date(data.createdDatetime);
        let tdDatetime = document.createElement('td');
        tdDatetime.setAttribute('style', 'width: 10%')
        let labelDatetime = document.createElement('label');
        labelDatetime.setAttribute('class', labelClass);
        labelDatetime.append(
            cdate.toLocaleDateString(), document.createElement('br'),
            cdate.getHours() + ':' + cdate.getMinutes() + ':' + (
                cdate.getSeconds().toString().padStart(2, '0')
            )
        );
        tdDatetime.append(labelDatetime);

        let tdDel = document.createElement('td');
        tdDel.setAttribute('style', 'width: 5%');
        tdDel.setAttribute('class', 'align-middle');
        let buttonDel = document.createElement('button');
        buttonDel.setAttribute('class', 'btn btn-outline-secondary btn-sm');
        buttonDel.setAttribute('onclick', 'deleteComment("' + data.id + '")');
        let iconDel = document.createElement('i');
        iconDel.setAttribute('class', 'fas fa-trash-alt')
        buttonDel.append(iconDel);
        tdDel.append(buttonDel);

        tr.append(tdId, tdWriter, tdComment, tdDatetime, tdDel);
        commentList.append(tr);
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

async function deleteComment(id) {
    if(confirm('댓글을 삭제하시겠습니까?')) {
        await fetchCustom('DELETE', 'default', '', 'comments/' + id, 'msg');
        getComments();
    }
}