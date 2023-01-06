CKEDITOR.editorConfig = (config) => {
    config.language = 'ko';
    config.enterMode = '2';

    config.height = 600;
    config.resize_minHeight = 250;
    config.resize_maxHeight = 1200;
    
    // config.extraPlugins = 'tableresize';   // 플러그인은 build.js 에서 등록해야 제대로 동작함.
    config.clipboard_handleImages = false;   // 기본 이미지 플러그인(image) 대신 image2 플러그인을 사용. ckeditor 공식문서에서 권고한대로 이 옵션을 false로 적용한다.
    config.image2_captionedClass = 'ckeditor-image-captioned';   // 이미지에 캡션을 추가할 때 클래스명 추가, css/common/common.css 에서 활용.
    config.removePlugins = 'image,exportpdf,wsc,smiley,iframe,flash,scayt';   // 제외할 플러그인

    config.imageUploadUrl = '/images?type=Images';
    config.filebrowserImageUploadUrl = '/images?type=Images';

    // config.font_names = '맑은 고딕/Malgun Gothic;굴림/Gulim;돋움/Dotum;바탕/Batang;궁서/Gungsuh;Consolas;' + config.font_names;
    
    // mc7 webaccess light에서 사용하고 있는 ckeditor 설정과 동일하게 맞춤.
    config.font_names = '맑은 고딕/Malgun Gothic;굴림/굴림;궁서/궁서;돋움/돋움;바탕/바탕;Times New Roman/times new roman;MS Sans Serif/ms sans serif;Tahoma/tahoma;Verdana/verdana;Arial/arial;Courier New/courier new;Consolas/consolas';
    config.fontSize_sizes = '8pt/8pt;9pt/9pt;10pt/10pt;11pt/11pt;12pt/12pt;14pt/14pt;16pt/16pt;18pt/18pt;20pt/20pt;24pt/24pt;28pt/28pt;36pt/36pt;48pt/48pt;72pt/72pt';

    config.toolbar = [
        { name: 'document', groups: [ 'mode', 'document', 'doctools' ], items: [ 'NewPage', 'Print', 'Source' ] },
        { name: 'clipboard', groups: [ 'clipboard', 'undo' ], items: [ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo' ] },
        { name: 'editing', groups: [ 'find', 'selection' ], items: [ 'Find', 'Replace', '-', 'SelectAll' ] },
        // { name: 'forms', items: [ 'Form', 'Checkbox', 'Radio', 'TextField', 'Textarea', 'Select', 'Button', 'ImageButton', 'HiddenField' ] },
        { name: 'links', items: [ 'Link', 'Unlink' ] },
        { name: 'insert', items: [ 'Image', 'Table', 'HorizontalRule', 'SpecialChar', 'PageBreak'] },
        '/',
        { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ], items: [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'CopyFormatting', 'RemoveFormat' ] },
        { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ], items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock' ] },
        { name: 'colors', items: [ 'TextColor', 'BGColor' ] },
        // { name: 'tools', items: [ 'Maximize' ] },
        { name: 'others', items: [ '-' ] },
        // { name: 'about', items: [ 'About' ] },
        // '/',
        // { name: 'styles', items: [ 'Styles', 'Format', 'Font', 'FontSize' ] }
        { name: 'styles', items: [ 'Font', 'FontSize' ] }
    ];
};
