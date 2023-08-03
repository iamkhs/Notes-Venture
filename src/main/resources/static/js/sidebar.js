// Custom JavaScript to handle the sidebar toggle and close
$(document).ready(function () {
    // Open sidebar
    $(".sidebar-toggler").on("click", function () {
        $("#sidebar").addClass("active");
        $("body").addClass("sidebar-active");
    });

    // Close sidebar
    $(".sidebar-close").on("click", function () {
        $("#sidebar").removeClass("active");
        $("body").removeClass("sidebar-active");
    });
});
