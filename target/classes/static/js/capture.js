
var readers;
var reader;
var result;
var reset = false;
var readerDisposed = true;
var fmd1Verify, fmd2Verify;
var OKAY = 0;
var CONTINUE = 1;
var ERROR = 2;
var currentDiv = null;

var fid1;
var captureCount=0;

var fmdArray = new Array();

function Capture() {
    reset = false;

    // Open.
    result = reader.Open("DP_PRIORITY_COOPERATIVE");

    if (result != "DP_SUCCESS") {
        alert("Non-success result:  " + result + ".");
        DestroyReader();
        Initialize();
        return;
    }

    readerDisposed = false;

    CaptureThread();
}

function CaptureThread() {

    while (!reset) {
        // Check status.
        result = reader.GetStatus();
        //alert("result:" + result)

        if (result == "DP_STATUS_BUSY") {
            Pause(50);
            alert("busy")
            continue;
        }
        else if (result == "DP_STATUS_NEED_CALIBRATION") {
            reader.Calibrate();
        }
        else if (result != "DP_STATUS_READY" && result != "DP_SUCCESS") {
            alert("Non-success result:  " + result + ".");
            reset = true;
            DestroyReader();
            //Initialize();
            break;
        }

        reader.Capture("ANSI", 0, -1, 500);
        break;
    }


}

function objReader::On_Captured(reader, captureResult){

    //alert("on capture!")
    try {

        var result = ValidateCapture(reader, captureResult);
        if (result == OKAY)
        {
            if (captureResult.Quality == "DP_QUALITY_GOOD"){
                Verify(reader, captureResult);
            }
            var t = setTimeout("CaptureThread(currentDiv)", 25);
        }
        else if (result == ERROR)
        {
            DestroyReader();
            Initialize();
        }
    } catch (e) {
    } finally {
    }
}

function DestroyReader(){
    reader.Dispose();
    readerDisposed = true;
}



function ValidateCapture(reader, captureResult){

    /*采集满4次,不再响应指纹获取事件*/
    if (captureCount>=4) return;

    if (captureResult == null)
    {
        alert("Error occurred in capture.  Reselect a reader and try again.");
        return ERROR;
    }

    if (captureResult.ResultCode != "DP_SUCCESS") {
        alert("Non-success result:  " + captureResult.ResultCode + ".");
        return ERROR;
    }

    if (captureResult.Quality == "DP_QUALITY_CANCELED") {
        return CONTINUE;
    }
    else if (captureResult.Quality == "DP_QUALITY_GOOD") {

    }
    else if (captureResult.Quality == "DP_QUALITY_TIMED_OUT") {
        alert("Warning: A timeout occurred while capturing. Please start the operation over.");
        return CONTINUE;
    }
    else if (captureResult.Quality == "DP_QUALITY_NO_FINGER") {
        alert("Warning: No finger detected.");
        return OKAY;
    }
    else if (captureResult.Quality == "DP_QUALITY_FAKE_FINGER") {
        alert("Warning: Fake finger detected.");
        return OKAY;
    }

    fid1 = captureResult.Fid;

    if (fid1 == null)
    {
        alert("Error occurred in capture.  Please try again.");
        return ERROR;
    }

    return OKAY;
}


function Initialize() {

    GetReader()
    Capture();

}

function GetReader() {

    readers = new Array();

    readers = objReaderCollection.GetReaders();

    if (readers == null) {
        return;
    }

    if (readers.count == 0) {
        return;
    }

    reader = readers(0);

    // Replace the reader with registered events with the reader returned by ReaderCollection.
    objReader.ReaderX = reader.ReaderX;

    // In case user did not wait for reader to clean up.  Must create a new IE session (File->NewSession) or create IE using -nomerge argument.
    objReader.Dispose();

}

function Verify(reader, captureResult)
{
    fmd1Verify = objFeatureExtraction.CreateFmdFromFid(fid1, "ANSI").Fmd;

    fmdArray[captureCount] = fmd1Verify;

    captureCount++;
    showStatus();

}

function selectFmd(){

    var maxMatchCount=0;
    var selectedFmdXml="";
    for (var i=0; i<fmdArray.length; i++){
        var currentFmd = fmdArray[i];
        var matchCount = 0;
        for (var j=0;j<fmdArray.length; j++){
            if (j!=i){
                var compareResult = objComparison.Compare(currentFmd, 0, fmdArray[j], 0);
                if (compareResult.Score < (2147483647/100000)){
                    matchCount++;
                }
            }
        }

        if (matchCount==fmdArray.length-1){
            maxMatchCount = matchCount;
            selectedFmdXml = currentFmd.SerializeXml();
            break;
        }
        if (matchCount>maxMatchCount){
            maxMatchCount = matchCount;
            selectedFmdXml = currentFmd.SerializeXml();
        }
    }

    if (maxMatchCount==fmdArray.length-1) {
        if (selectedFmdXml == "") {
            selectedFmdXml = fmdArray[0].SerializeXml();
        }
        form_fp.fmdXml.value = selectedFmdXml;
        return true;
    }

    return false;

}

function register(){

    if ( form_fp.userId.value.trim()=='' ){
        alert('用户信息丢失，请返回重新操作')
        return
    }

    //selectFmd();
    form_fp.submit();

}


function showStatus(){

    var showHtml = '扫描成功'+captureCount+'次！'

    if (captureCount==1){
        img_step1.src="/images/step_01.png"
    }
    else if(captureCount==2) {
        img_step2.src="/images/step_02.png"
    }
    else if(captureCount==3) {
        img_step3.src="/images/step_03.png"
    }
    else if(captureCount==4) {
        img_step4.src="/images/step_04.png"
    }

    if (captureCount<4){
        showHtml += '<br/>再次将您的手指放到指纹阅读器上!'
    }
    else{
        if ( selectFmd() ){
            btnRegister.style.display=""
        }
        else{
            captureCount = 0;
            fmdArray = new Array()
            img_step1.src="/images/step_n_01.png"
            img_step2.src="/images/step_n_02.png"
            img_step3.src="/images/step_n_03.png"
            img_step4.src="/images/step_n_04.png"
            showHtml = '指纹不一致，扫描失败！请重试'
        }

    }

    captureStatus.innerHTML=showHtml;

}

