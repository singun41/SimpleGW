window.addEventListener('DOMContentLoaded', event => {
    setCkEditor();
});

function setCkEditor() {
    // 에디터 설정 및 생성
    CKEDITOR.on('dialogDefinition', (ev) => {
        let dialogName = ev.data.name;
        let dialogDefinition = ev.data.definition;

        if (dialogName == 'table') {
            let info = dialogDefinition.getContents( 'info' );
            info.get( 'txtWidth' )[ 'default' ] = '750px';       // Set default width to 100%
            info.get( 'txtHeight' )[ 'default' ] = '100px';
            info.get( 'txtBorder' )[ 'default' ] = '1';         // Set default border to 1
            info.get( 'txtCellPad' )[ 'default' ] = '1';
            info.get( 'txtCellSpace' )[ 'default' ] = '0';
        }
    });
    
    // 테이블 리사이즈 플러그인은 여기에 등록해야 동작한다. config.js에서 하면 동작 안 함.
    CKEDITOR.replace('ckeditorTextarea', {
        customConfig: '/js/common/ckeditor-config.js',
        extraPlugins: 'tableresize',
        contentsCss: '/css/ckeditor.css'
    });
}