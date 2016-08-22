
var readers;
var reader;
var result;
var reset = false;
var readerDisposed = true;
var verifyState = 0;
var fmd1Verify, fmd2Verify;
var OKAY = 0;
var CONTINUE = 1;
var ERROR = 2;
var currentDiv = null;

var fpEnrolled = false;
var fid1;

function Capture() {
    reset = false;

    // Open.
    result = reader.Open("DP_PRIORITY_COOPERATIVE");

    //alert("result:" + result)

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

        if (result == "DP_STATUS_BUSY") {
            Pause(50);
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

function objReader::On_Captured(reader, captureResult) {

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

    Readers = new Array();

    Readers = objReaderCollection.GetReaders();

    if (Readers == null) {
        return;
    }

    if (Readers.count == 0) {
        return;
    }

    reader = Readers(0);

    // Replace the reader with registered events with the reader returned by ReaderCollection.
    objReader.ReaderX = reader.ReaderX;

    // In case user did not wait for reader to clean up.  Must create a new IE session (File->NewSession) or create IE using -nomerge argument.
    objReader.Dispose();

}

function Verify(reader, captureResult)
{

            fmd1Verify = objFeatureExtraction.CreateFmdFromFid(fid1, "ANSI").Fmd;

            form_fp.fmdXml.value = fmd1Verify.SerializeXml();

            alert(form_fp.fmdXml.value);

            fpEnrolled = true;

            submitCompare();

}

function submitCompare(){

    var user_id = form_fp.userId.value.trim();

    if ( user_id==''|| user_id=='请输入工号' ){
        alert('请指定用户ID')
        form_fp.userId.focus();
        return
    }

    form_fp.submit();

    fpEnrolled = false;


    /*
     fmd1Verify = objFeatureExtraction.CreateFmdFromFid(fid1, "ANSI").Fmd;

     fmd2Verify = objFeatureExtraction.CreateFmdFromFid(fid1, "ANSI").Fmd;

     // Match.
     var compareResult = objComparison.Compare(fmd1Verify, 0, fmd2Verify, 0);
     if (compareResult.Score < (2147483647/100000))
     matchResult = " (match)";
     else
     matchResult = " (no match)";
     SendText(txtVerify, "Fingers captured and extracted to FMDs. Comparing...");
     SendText(txtVerify, "Dissimilarity score = " + compareResult.Score + matchResult);
     SendText(txtVerify, "Place a finger on the reader then remove it.  ");
     */

}

function fpRegister(){

    if (!fpEnrolled){
        alert("没有采集到指纹")
        return;
    }

    if ( form_fp.userId.value.trim()=='' ){
        alert('请指定用户ID')
        form_fp.userId.focus();
        return
    }

    form_fp.action = "/fp/register";
    form_fp.submit();

    fpEnrolled = false;

}

function checkInput(){
    if (form_fp.userId.value.trim()==''){
        form_fp.userId.focus();
        return false;
    }

    if (form_fp.fmdXml.value.trim()==''){
        return false;
    }

}

