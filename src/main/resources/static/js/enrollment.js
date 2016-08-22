
var selectedIndex = -1;
var ul;
var liList;
var indexOptions;

function initEnrollment(){
    ul=document.getElementById('zw');
    liList=ul.getElementsByTagName('li');

    indexOptions = document.getElementById("registeredIndex").options
    initFinger();

    ul.onclick=function(event){
        var e=event||window.event,t=e.target||e.srcElement;
        if(t.nodeName.toLowerCase()=='li'){

            initFinger()
            t.className +=' now';

            for (var i=0,len=liList.length;i<len;i++){
                if (liList[i]==t){
                    selectedIndex = i;
                }
            }
        }
    }
}

function initFinger(){

    for(var i=0,len=liList.length;i<len;i++){
        liList[i].className ='';
        liList[4].className='thumb';
        liList[9].className='thumb1';

    }

    if (indexOptions.length>0){

        for (var i=0; i<indexOptions.length; i++){
            var fingerIndex = indexOptions[i].value;
            liList[fingerIndex].className += ' registered';
        }
    }

}

function capture(){

    if (selectedIndex<0){
        alert("请点击选中需要注册的手指")
        return;
    }
    window.location.href="/fp/capture?fingerIndex="+selectedIndex;
}
