window.addEventListener('DOMContentLoaded', () => {
    buildCkEditor();
});

function buildCkEditor() {
    // ckeditor table dialog 커스터마이징
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

            // 다른 css에 의해서 테이블 렌더링이 핸들링 되지 않도록 커스텀 클래스를 기본으로 지정한다. common.css에 해당 클래스에 대한 렌더링을 추가로 설정함.
            let customClass = dialogDefinition.getContents('advanced').get('advCSSClasses');
            customClass['default'] = 'ckeditor-tables';
        }
    });
    
    // 테이블 리사이즈 플러그인은 여기에 등록해야 동작한다. config.js에서 하면 동작 안 함.
    CKEDITOR.replace('ckeditorTextarea', {
        customConfig: '/js/ckeditor/config.js',
        extraPlugins: 'tableresize,image2,uploadimage',
        contentsCss: '/css/common/ckeditor-custom.css'
    });
}
