document.addEventListener('DOMContentLoaded', () => {
    dayjs.locale('ko');
    getComments();
});
const inputComment = document.getElementById('inputComment');

async function getComments() {
    let comments = document.getElementById('comments');
    while(comments.hasChildNodes())
        comments.removeChild(comments.firstChild);

    let response = await fetchGet(`comment/${docsId}`);
    let result = await response.json();
    if(response.ok) {
        Array.from(result.obj).forEach(e => {
            let writer = document.createElement('span');
            writer.classList.add('custom-fs-8');
            writer.innerText = `${e.writerTeam} ${e.writerJobTitle} ${e.writerName}`;
            
            let btnDel = document.createElement('a');
            btnDel.classList.add('text-decoration-none', 'text-secondary', 'ms-3');
            btnDel.setAttribute('role', 'button');
            btnDel.setAttribute('onclick', `deleteComment('${e.id}')`);
            btnDel.innerHTML = 'X';

            let cdt = document.createElement('span');
            cdt.classList.add('float-end');
            cdt.innerText = dayjs(e.createdDatetime).format('YY. MM. DD. HH:mm:ss');
            cdt.append(btnDel);

            writer.append(cdt);

            let writerRow = document.createElement('div');
            writerRow.classList.add('row', 'text-secondary');
            writerRow.append(writer);

            let comment = document.createElement('span');
            comment.classList.add('custom-fs-8');
            comment.innerText = e.comment;

            let commentRow = document.createElement('div');
            commentRow.classList.add('row', 'mb-3');
            commentRow.append(comment);

            comments.append(writerRow, commentRow);
        });
    }
}

async function saveComment() {
    let params = {
        comment: inputComment.value
    };
    let response = await fetchPostParams(`comment/${docsType}/${docsId}`, params);
    let result = await response.json();
    alert(result.msg);
    inputComment.value = '';
    getComments();
}

async function deleteComment(id) {
    if(!confirm('댓글을 삭제하시겠습니까?'))
        return;

    let response = await fetchDelete(`comment/${id}`);
    let result = await response.json();
    alert(result.msg);

    if(response.ok)
        getComments();
}
