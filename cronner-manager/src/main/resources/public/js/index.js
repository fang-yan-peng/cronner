$(function() {
    $("#content").load("/html/config/job_config_overwrite.html");
    $("#job-settings").click(function() {
        $("#content").load("/html/config/job_config_overwrite.html");
    });
    $("#job-trace-history").click(function() {
        $("#content").load("/html/history/job_status_history.html");
    });
    /*$("#help").click(function() {
        $("#content").load("/html/help/help.html", null, function(){
            doLocale();
        });
    });*/
    switchLanguage();
});
